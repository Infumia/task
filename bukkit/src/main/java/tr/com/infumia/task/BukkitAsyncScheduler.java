package tr.com.infumia.task;

import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;

@Log4j2
@NoArgsConstructor(access = AccessLevel.PACKAGE)
final class BukkitAsyncScheduler implements Scheduler {

  @NotNull
  @Override
  public ThreadContext context() {
    return ThreadContext.ASYNC;
  }

  @NotNull
  @Override
  public Promise<Void> run(@NotNull final Runnable runnable) {
    final var promise = new PromiseImpl<Void>();
    AsyncExecutor.INSTANCE.execute(
      new PromiseSupply<>(promise, new RunnableToSupplier<>(runnable))
    );
    return promise;
  }

  @NotNull
  @Override
  public Promise<Void> runLater(
    @NotNull final Runnable runnable,
    final long delay,
    @NotNull final TimeUnit unit
  ) {
    final var promise = new PromiseImpl<Void>();
    AsyncExecutor.INSTANCE.schedule(
      new PromiseSupply<>(promise, new RunnableToSupplier<>(runnable)),
      delay,
      unit
    );
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
    final var task = new InternalBukkitTask(taskPredicate);
    if (plugin.isEnabled()) {
      task.runTaskTimerAsynchronously(plugin, delayTicks, intervalTicks);
    } else {
      BukkitAsyncScheduler.log.error("Plugin attempted to register task while disabled!");
      BukkitAsyncScheduler.log.error("The task won't be run because this is a repeating task!");
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
    final var task = new InternalScheduledTask(taskPredicate);
    task.scheduleAtFixedRate(delay, interval, unit);
    return task;
  }
}
