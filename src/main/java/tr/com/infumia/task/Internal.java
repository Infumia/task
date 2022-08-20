package tr.com.infumia.task;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;
import java.util.function.Supplier;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@UtilityClass
class Internal {

  final Scheduler ASYNC_SCHEDULER = new AsyncScheduler();

  final Scheduler SYNC_SCHEDULER = new SyncScheduler();

  private final Executor ASYNC_BUKKIT = new BukkitAsyncExecutor();

  private final AsyncExecutor ASYNC_TASK = new AsyncExecutor();

  private final AtomicReference<Thread> MAIN_THREAD = new AtomicReference<>();

  private final int MILLISECONDS_PER_SECOND = 1000;

  private final AtomicReference<Plugin> PLUGIN = new AtomicReference<>();

  private final Executor SYNC_BUKKIT = new BukkitSyncExecutor();

  private final int TICKS_PER_SECOND = 20;

  private final int MILLISECONDS_PER_TICK =
    Internal.MILLISECONDS_PER_SECOND / Internal.TICKS_PER_SECOND;

  @NotNull
  public static Executor asyncBukkit() {
    return Internal.ASYNC_BUKKIT;
  }

  public void shutdown() {
    Internal.ASYNC_TASK.cancelRepeatingTasks();
  }

  @NotNull
  ScheduledExecutorService async() {
    return Internal.ASYNC_TASK;
  }

  @NotNull
  static <T> Supplier<T> callableToSupplier(
    @NotNull final Callable<T> callable
  ) {
    return new CallableToSupplier<>(callable);
  }

  void mainThread(@NotNull final Thread thread) {
    Internal.MAIN_THREAD.set(thread);
  }

  @NotNull
  Thread mainThread() {
    return Objects.requireNonNull(
      Internal.MAIN_THREAD.get(),
      "initiate task first!"
    );
  }

  void plugin(@NotNull final Plugin plugin) {
    Internal.PLUGIN.set(plugin);
  }

  @NotNull
  Plugin plugin() {
    return Objects.requireNonNull(
      Internal.PLUGIN.get(),
      "init paper task first!"
    );
  }

  @NotNull
  static <T> Consumer<T> runnableToConsumer(@NotNull final Runnable runnable) {
    return new RunnableToConsumer<>(runnable);
  }

  @NotNull
  static Supplier<Void> runnableToSupplier(@NotNull final Runnable runnable) {
    return new RunnableToSupplier<>(runnable);
  }

  @NotNull
  static Executor syncBukkit() {
    return Internal.SYNC_BUKKIT;
  }

  long ticksFrom(final long duration, @NotNull final TimeUnit unit) {
    return unit.toMillis(duration) / Internal.MILLISECONDS_PER_TICK;
  }

  @NotNull
  Runnable wrapSchedulerTask(@NotNull final Runnable runnable) {
    return new SchedulerWrappedRunnable(runnable);
  }

  @NotNull
  private ThreadFactory thread(@NotNull final String format) {
    return new ThreadFactory() {
      private final AtomicLong count = new AtomicLong();

      @Override
      public Thread newThread(@NotNull final Runnable r) {
        final var thread = Executors.defaultThreadFactory().newThread(r);
        thread.setName(format.formatted(this.count.getAndIncrement()));
        thread.setDaemon(true);
        return thread;
      }
    };
  }

  @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
  static final class AsyncExecutor
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
              new FixedRateWorker(Internal.wrapSchedulerTask(command), this),
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
    private ScheduledFuture<?> consumeTask(
      @NotNull final ScheduledFuture<?> future
    ) {
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

  static final class AsyncScheduler implements Scheduler {

    @NotNull
    @Override
    public ThreadContext context() {
      return ThreadContext.ASYNC;
    }

    @NotNull
    @Override
    public Task runRepeating(
      @NotNull final Consumer<Task> consumer,
      final long delayTicks,
      final long intervalTicks
    ) {
      final var task = new InternalTask(consumer);
      task.runTaskTimerAsynchronously(
        Internal.plugin(),
        delayTicks,
        intervalTicks
      );
      return task;
    }

    @Override
    public void execute(@NotNull final Runnable command) {
      Internal.async().execute(command);
    }
  }

  private static final class BukkitAsyncExecutor implements Executor {

    @Override
    public void execute(@NotNull final Runnable command) {
      Bukkit
        .getScheduler()
        .runTaskAsynchronously(
          Internal.plugin(),
          Internal.wrapSchedulerTask(command)
        );
    }
  }

  private static final class BukkitSyncExecutor implements Executor {

    @Override
    public void execute(@NotNull final Runnable command) {
      Bukkit
        .getScheduler()
        .scheduleSyncDelayedTask(
          Internal.plugin(),
          Internal.wrapSchedulerTask(command)
        );
    }
  }

  private record CallableToSupplier<T>(@NotNull Callable<T> delegate)
    implements Supplier<T> {
    @Override
    public T get() {
      try {
        return this.delegate.call();
      } catch (final RuntimeException | Error e) {
        throw e;
      } catch (final Exception e) {
        throw new RuntimeException(e);
      }
    }
  }

  @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
  @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
  static final class InternalTask extends BukkitRunnable implements Task {

    @NotNull
    Consumer<Task> backingTask;

    @NotNull
    AtomicBoolean cancelled = new AtomicBoolean(false);

    @NotNull
    AtomicInteger counter = new AtomicInteger(0);

    @Override
    public boolean closed() {
      return this.cancelled.get();
    }

    @Override
    public int id() {
      return this.getTaskId();
    }

    @Override
    public boolean stop() {
      return !this.cancelled.getAndSet(true);
    }

    @Override
    public int timesRan() {
      return this.counter.get();
    }

    @Override
    public void run() {
      if (this.cancelled.get()) {
        this.cancel();
        return;
      }
      try {
        this.backingTask.accept(this);
        this.counter.incrementAndGet();
      } catch (final Throwable e) {
        e.printStackTrace();
      }
      if (this.cancelled.get()) {
        this.cancel();
      }
    }
  }

  private record RunnableToConsumer<T>(@NotNull Runnable delegate)
    implements Consumer<T> {
    @Override
    public void accept(final T t) {
      this.delegate.run();
    }
  }

  private record RunnableToSupplier<T>(@NotNull Runnable delegate)
    implements Supplier<@Nullable T> {
    @Nullable
    @Override
    public T get() {
      this.delegate.run();
      return null;
    }
  }

  private record SchedulerWrappedRunnable(@NotNull Runnable delegate)
    implements Runnable {
    @Override
    public void run() {
      try {
        this.delegate.run();
      } catch (final Throwable t) {
        t.printStackTrace();
      }
    }
  }

  static final class SyncScheduler implements Scheduler {

    @NotNull
    @Override
    public ThreadContext context() {
      return ThreadContext.SYNC;
    }

    @NotNull
    @Override
    public Task runRepeating(
      @NotNull final Consumer<Task> consumer,
      final long delayTicks,
      final long intervalTicks
    ) {
      final var task = new InternalTask(consumer);
      task.runTaskTimer(Internal.plugin(), delayTicks, intervalTicks);
      return task;
    }

    @Override
    public void execute(@NotNull final Runnable command) {
      Internal.syncBukkit().execute(command);
    }
  }
}
