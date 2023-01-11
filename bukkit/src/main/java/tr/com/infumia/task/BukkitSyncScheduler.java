package tr.com.infumia.task;

import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;

@Log4j2
@NoArgsConstructor(access = AccessLevel.PACKAGE)
final class BukkitSyncScheduler implements Scheduler {

  @NotNull
  @Override
  public ThreadContext context() {
    return ThreadContext.SYNC;
  }

  @NotNull
  @Override
  public Promise<Void> run(@NotNull final Runnable runnable) {
    final var promise = new PromiseImpl<Void>();
    final var plugin = BukkitTasks.plugin();
    final var task = new BukkitInternalTask(t -> {
      new PromiseSupply<>(promise, new RunnableToSupplier<>(runnable)).run();
      return false;
    });
    if (plugin.isEnabled()) {
      task.runTask(plugin);
    } else {
      BukkitSyncScheduler.log.error("Plugin attempted to register task while disabled!");
      BukkitSyncScheduler.log.error(
        "We are going to run the task in the current thread which is {}!",
        Thread.currentThread()
      );
      task.run();
    }
    return promise;
  }

  @NotNull
  @Override
  public Promise<Void> runLater(@NotNull final Runnable runnable, final long delayTicks) {
    final var promise = new PromiseImpl<Void>();
    final var plugin = BukkitTasks.plugin();
    final var task = new BukkitInternalTask(t -> {
      new PromiseSupply<>(promise, new RunnableToSupplier<>(runnable)).run();
      return false;
    });
    if (plugin.isEnabled()) {
      task.runTaskLater(plugin, delayTicks);
    } else {
      BukkitSyncScheduler.log.error("Plugin attempted to register task while disabled!");
      BukkitSyncScheduler.log.error("The task won't be run because this is a repeating task!");
    }
    return promise;
  }

  @NotNull
  @Override
  public Task runRepeatingCloseIf(
    @NotNull final Predicate<Task> taskPredicate,
    final long delayTicks,
    final long intervalTicks
  ) {
    final var plugin = BukkitTasks.plugin();
    final var task = new BukkitInternalTask(taskPredicate);
    if (plugin.isEnabled()) {
      task.runTaskTimer(plugin, delayTicks, intervalTicks);
    } else {
      BukkitSyncScheduler.log.error("Plugin attempted to register task while disabled!");
      BukkitSyncScheduler.log.error("The task won't be run because this is a repeating task!");
    }
    return task;
  }

  @NotNull
  @Override
  public Task scheduleRepeating(
    @NotNull final Predicate<Task> taskPredicate,
    final long delay,
    final long interval,
    @NotNull final TimeUnit unit
  ) {
    BukkitSyncScheduler.log.error(
      "Sync scheduler does not support #scheduleRepeating(Consumer<Task>, long, long, TimeUnit), using async scheduler to schedule repeating instead!"
    );
    return Schedulers.async().scheduleRepeating(taskPredicate, delay, interval, unit);
  }
}