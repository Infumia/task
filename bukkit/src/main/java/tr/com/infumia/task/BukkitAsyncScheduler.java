package tr.com.infumia.task;

import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

final class BukkitAsyncScheduler implements Scheduler {

  @NotNull
  private final Logger logger;

  BukkitAsyncScheduler(@NotNull final Logger logger) {
    this.logger = logger;
  }

  @NotNull
  @Override
  public ThreadContext context() {
    return ThreadContext.ASYNC;
  }

  @NotNull
  @Override
  public Promise<?> run(@NotNull final Runnable runnable) {
    final Promise<?> promise = Promise.empty();
    AsyncExecutor.INSTANCE.execute(
      new PromiseSupply<>(
        promise,
        () -> {
          runnable.run();
          return null;
        }
      )
    );
    return promise;
  }

  @NotNull
  @Override
  public Promise<?> runLater(@NotNull final Runnable runnable, @NotNull final Duration delay) {
    final Promise<?> promise = Promise.empty();
    AsyncExecutor.INSTANCE.schedule(
      new PromiseSupply<>(
        promise,
        () -> {
          runnable.run();
          return null;
        }
      ),
      delay.toMillis(),
      TimeUnit.MILLISECONDS
    );
    return promise;
  }

  @NotNull
  @Override
  public Task runRepeatingCloseIf(
    @NotNull final Predicate<Task> taskPredicate,
    @NotNull final Duration delay,
    @NotNull final Duration interval
  ) {
    final Plugin plugin = BukkitTasks.plugin();
    final BukkitInternalTask task = new BukkitInternalTask(taskPredicate);
    if (plugin.isEnabled()) {
      task.runTaskTimerAsynchronously(
        plugin,
        Internal.ticksFrom(delay),
        Internal.ticksFrom(interval)
      );
    } else {
      this.logger.severe("Plugin attempted to register task while disabled!");
      this.logger.severe("The task won't be run because this is a repeating task!");
    }
    return task;
  }

  @NotNull
  @Override
  public Task scheduleRepeatingCloseIf(
    @NotNull final Predicate<Task> taskPredicate,
    @NotNull final Duration delay,
    @NotNull final Duration interval
  ) {
    final InternalScheduledTask task = new InternalScheduledTask(taskPredicate);
    task.scheduleAtFixedRate(delay, interval);
    return task;
  }
}
