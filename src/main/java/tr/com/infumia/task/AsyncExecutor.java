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
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
final class AsyncExecutor
  extends AbstractExecutorService
  implements ScheduledExecutorService {

  @NotNull
  ExecutorService executorService;

  @NotNull
  Set<ScheduledFuture<?>> tasks = Collections.newSetFromMap(
    new WeakHashMap<>()
  );

  @NotNull
  ScheduledExecutorService timerExecutionService;

  AsyncExecutor() {
    this.executorService =
      Executors.newCachedThreadPool(Internal.thread("task-scheduler-%d"));
    this.timerExecutionService =
      Executors.newSingleThreadScheduledExecutor(
        Internal.thread("task-scheduler-timer")
      );
  }

  @Override
  public void execute(@NotNull final Runnable command) {
    this.executorService.execute(Internal.wrapSchedulerTask(command));
  }

  @NotNull
  @Override
  public ScheduledFuture<?> schedule(
    @NotNull final Runnable command,
    final long delay,
    @NotNull final TimeUnit unit
  ) {
    final Runnable delegate = () -> {
      try {
        command.run();
      } catch (final Throwable t) {
        t.printStackTrace();
      }
    };
    return this.consumeTask(
      this.timerExecutionService.schedule(
        () -> this.executorService.execute(delegate),
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
        new FixedRateWorker(
          Internal.wrapSchedulerTask(command),
          this
        ),
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
  public void shutdown() {
  }

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
  public boolean awaitTermination(
    final long timeout,
    @NotNull final TimeUnit unit
  ) {
    throw new IllegalStateException("Not shutdown");
  }

  void cancelRepeatingTasks() {
    synchronized (this.tasks) {
      for (final var task : this.tasks) {
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

  @RequiredArgsConstructor
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
