package tr.com.infumia.task;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public final class AsyncExecutor
  extends AbstractExecutorService
  implements ScheduledExecutorService {

  public static final AsyncExecutor INSTANCE = new AsyncExecutor();

  @NotNull
  ExecutorService executorService;

  @NotNull
  Set<ScheduledFuture<?>> tasks = Collections.newSetFromMap(new WeakHashMap<>());

  @NotNull
  ScheduledExecutorService timerExecutionService;

  private AsyncExecutor() {
    this.executorService = Executors.newCachedThreadPool(AsyncExecutor.thread("task-scheduler-%d"));
    this.timerExecutionService =
    Executors.newSingleThreadScheduledExecutor(AsyncExecutor.thread("task-scheduler-timer"));
  }

  @NotNull
  private static ThreadFactory thread(@NotNull final String format) {
    return new ThreadFactory() {
      private final AtomicLong count = new AtomicLong();

      @Override
      public Thread newThread(@NotNull final Runnable r) {
        final Thread thread = Executors.defaultThreadFactory().newThread(r);
        thread.setName(String.format(format, this.count.getAndIncrement()));
        thread.setDaemon(true);
        return thread;
      }
    };
  }

  @Override
  public void execute(@NotNull final Runnable command) {
    this.executorService.execute(new UncheckedRunnable(command));
  }

  @NotNull
  @Override
  public ScheduledFuture<?> schedule(
    @NotNull final Runnable command,
    final long delay,
    @NotNull final TimeUnit unit
  ) {
    return this.consumeTask(
        this.timerExecutionService.schedule(
            () -> this.executorService.execute(new UncheckedRunnable(command)),
            delay,
            unit
          )
      );
  }

  @Override
  public <V> ScheduledFuture<V> schedule(
    @NotNull final Callable<V> callable,
    final long delay,
    @NotNull final TimeUnit unit
  ) {
    throw new UnsupportedOperationException();
  }

  @NotNull
  @Override
  public ScheduledFuture<?> scheduleAtFixedRate(
    @NotNull final Runnable command,
    final long initialDelay,
    final long period,
    @NotNull final TimeUnit unit
  ) {
    return this.consumeTask(
        this.timerExecutionService.scheduleAtFixedRate(
            new FixedRateWorker(new UncheckedRunnable(command), this),
            initialDelay,
            period,
            unit
          )
      );
  }

  @NotNull
  @Override
  public ScheduledFuture<?> scheduleWithFixedDelay(
    @NotNull final Runnable command,
    final long initialDelay,
    final long delay,
    @NotNull final TimeUnit unit
  ) {
    return this.scheduleAtFixedRate(command, initialDelay, delay, unit);
  }

  @Override
  public void shutdown() {}

  @NotNull
  @Override
  public List<Runnable> shutdownNow() {
    return Collections.emptyList();
  }

  @Override
  public boolean isShutdown() {
    return false;
  }

  @Override
  public boolean isTerminated() {
    return false;
  }

  @Override
  public boolean awaitTermination(final long timeout, @NotNull final TimeUnit unit) {
    throw new IllegalStateException("Not shutdown");
  }

  void cancelRepeatingTasks() {
    synchronized (this.tasks) {
      for (final ScheduledFuture<?> task : this.tasks) {
        task.cancel(false);
      }
    }
  }

  @NotNull
  private ScheduledFuture<?> consumeTask(@NotNull final ScheduledFuture<?> future) {
    synchronized (this.tasks) {
      this.tasks.add(future);
    }
    return future;
  }

  @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
  private static final class FixedRateWorker implements Runnable {

    @NotNull
    Runnable delegate;

    @NotNull
    AsyncExecutor executor;

    @NotNull
    ReentrantLock lock = new ReentrantLock();

    @NotNull
    AtomicInteger running = new AtomicInteger(0);

    FixedRateWorker(@NotNull final Runnable delegate, @NotNull final AsyncExecutor executor) {
      this.delegate = delegate;
      this.executor = executor;
    }

    @Override
    public void run() {
      if (this.running.incrementAndGet() > 2) {
        this.running.decrementAndGet();
        return;
      }
      this.executor.executorService.execute(() -> {
          this.lock.lock();
          try {
            this.delegate.run();
          } finally {
            this.lock.unlock();
            this.running.decrementAndGet();
          }
        });
    }
  }
}
