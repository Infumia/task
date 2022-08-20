package tr.com.infumia.task;

import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

@UtilityClass
class Internal {

  private final Executor ASYNC_BUKKIT = new BukkitAsyncExecutor();

  private final AsyncExecutor ASYNC_TASK = new AsyncExecutor();

  private final AtomicReference<Thread> MAIN_THREAD = new AtomicReference<>();

  private final int MILLISECONDS_PER_SECOND = 1000;

  private final AtomicReference<Plugin> PLUGIN = new AtomicReference<>();

  private final Executor SYNC_BUKKIT = new BukkitSyncExecutor();

  private final int TICKS_PER_SECOND = 20;

  private final int MILLISECONDS_PER_TICK = Internal.MILLISECONDS_PER_SECOND / Internal.TICKS_PER_SECOND;

  @NotNull
  public ScheduledExecutorService async() {
    return Internal.ASYNC_TASK;
  }

  @NotNull
  public static Executor asyncBukkit() {
    return Internal.ASYNC_BUKKIT;
  }

  public void shutdown() {
    Internal.ASYNC_TASK.cancelRepeatingTasks();
  }

  @NotNull
  public static Executor syncBukkit() {
    return Internal.SYNC_BUKKIT;
  }

  public long ticksFrom(final long duration, @NotNull final TimeUnit unit) {
    return unit.toMillis(duration) / Internal.MILLISECONDS_PER_TICK;
  }

  public long ticksTo(final long ticks, @NotNull final TimeUnit unit) {
    return unit.convert(ticks * Internal.MILLISECONDS_PER_TICK, TimeUnit.MILLISECONDS);
  }

  void mainThread(@NotNull final Thread thread) {
    Internal.MAIN_THREAD.set(thread);
  }

  @NotNull
  Thread mainThread() {
    return Objects.requireNonNull(Internal.MAIN_THREAD.get(), "initiate task first!");
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
  ThreadFactory thread(@NotNull final String format) {
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

  @NotNull
  Runnable wrapSchedulerTask(@NotNull final Runnable runnable) {
    return new SchedulerWrappedRunnable(runnable);
  }

  private static final class BukkitAsyncExecutor implements Executor {

    @Override
    public void execute(@NotNull final Runnable command) {
      Bukkit.getScheduler().runTaskAsynchronously(Internal.plugin(), Internal.wrapSchedulerTask(command));
    }
  }

  private static final class BukkitSyncExecutor implements Executor {

    @Override
    public void execute(@NotNull final Runnable command) {
      Bukkit.getScheduler().scheduleSyncDelayedTask(Internal.plugin(), Internal.wrapSchedulerTask(command));
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
}
