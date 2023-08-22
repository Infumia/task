package tr.com.infumia.task;

import java.time.Duration;
import java.util.function.Predicate;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

final class BukkitSyncScheduler implements Scheduler {

  @NotNull
  private final Logger logger;

  BukkitSyncScheduler(@NotNull final Logger logger) {
    this.logger = logger;
  }

  @NotNull
  @Override
  public ThreadContext context() {
    return ThreadContext.SYNC;
  }

  @NotNull
  @Override
  public Promise<?> run(@NotNull final Runnable runnable) {
    final Promise<?> promise = Promise.empty();
    final Plugin plugin = BukkitTasks.plugin();
    final BukkitInternalTask task = new BukkitInternalTask(t -> {
      new PromiseSupply<>(
        promise,
        () -> {
          runnable.run();
          return null;
        }
      )
        .run();
      return false;
    });
    if (plugin.isEnabled()) {
      task.runTask(plugin);
    } else {
      this.logger.warning("Plugin attempted to register task while disabled!");
      this.logger.warning(
          "We are going to run the task in the current thread which is %s!",
          Thread.currentThread()
        );
      task.run();
    }
    return promise;
  }

  @NotNull
  @Override
  public Promise<?> runLater(@NotNull final Runnable runnable, @NotNull final Duration delay) {
    final Promise<?> promise = Promise.empty();
    final Plugin plugin = BukkitTasks.plugin();
    final BukkitInternalTask task = new BukkitInternalTask(t -> {
      new PromiseSupply<>(
        promise,
        () -> {
          runnable.run();
          return null;
        }
      )
        .run();
      return false;
    });
    if (plugin.isEnabled()) {
      task.runTaskLater(plugin, Internal.ticksFrom(delay));
    } else {
      this.logger.warning("Plugin attempted to register task while disabled!");
      this.logger.warning(
          "We are going to run the task in the current thread which is %s!",
          Thread.currentThread()
        );
      task.run();
    }
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
      task.runTaskTimer(plugin, Internal.ticksFrom(delay), Internal.ticksFrom(interval));
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
    this.logger.severe(
        "Sync scheduler does not support #scheduleRepeating(Consumer<Task>, long, long, TimeUnit), using async scheduler to schedule repeating instead!"
      );
    return Schedulers.async().scheduleRepeatingCloseIf(taskPredicate, delay, interval);
  }
}
