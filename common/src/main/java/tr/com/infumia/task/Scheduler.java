package tr.com.infumia.task;

import java.time.Duration;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public interface Scheduler {
  @NotNull
  default <T> Promise<T> call(@NotNull final Callable<T> callable) {
    return this.supply(new CallableToSupplier<>(callable));
  }

  @NotNull
  default <T> Promise<T> callLater(@NotNull final Callable<T> callable, final long delayTicks) {
    return this.callLater(callable, Times.durationFrom(delayTicks));
  }

  @NotNull
  default <T> Promise<T> callLater(
    @NotNull final Callable<T> callable,
    final long delay,
    @NotNull final TimeUnit unit
  ) {
    return this.callLater(callable, Times.durationFrom(delay, unit));
  }

  @NotNull
  default <T> Promise<T> callLater(
    @NotNull final Callable<T> callable,
    @NotNull final Duration delay
  ) {
    return this.supplyLater(new CallableToSupplier<>(callable), delay);
  }

  @NotNull
  ThreadContext context();

  @NotNull
  Promise<Void> run(@NotNull Runnable runnable);

  @NotNull
  default Promise<Void> runLater(@NotNull final Runnable runnable, final long delayTicks) {
    return this.runLater(runnable, Times.durationFrom(delayTicks));
  }

  @NotNull
  default Promise<Void> runLater(
    @NotNull final Runnable runnable,
    final long delay,
    @NotNull final TimeUnit unit
  ) {
    return this.runLater(runnable, Times.durationFrom(delay, unit));
  }

  @NotNull
  Promise<Void> runLater(@NotNull Runnable runnable, @NotNull Duration delay);

  @NotNull
  default Task runRepeating(
    @NotNull final Consumer<Task> taskConsumer,
    final long delayTicks,
    final long intervalTicks
  ) {
    return this.runRepeating(
        taskConsumer,
        Times.durationFrom(delayTicks),
        Times.durationFrom(intervalTicks)
      );
  }

  @NotNull
  default Task runRepeating(
    @NotNull final Consumer<Task> taskConsumer,
    final long delay,
    @NotNull final TimeUnit delayUnit,
    final long interval,
    @NotNull final TimeUnit intervalUnit
  ) {
    return this.runRepeating(
        taskConsumer,
        Times.durationFrom(delay, delayUnit),
        Times.durationFrom(interval, intervalUnit)
      );
  }

  @NotNull
  default Task runRepeating(
    @NotNull final Consumer<Task> taskConsumer,
    @NotNull final Duration delay,
    @NotNull final Duration interval
  ) {
    return this.runRepeatingCloseIf(
        new ConsumerToPredicate<>(taskConsumer, false),
        delay,
        interval
      );
  }

  @NotNull
  default Task runRepeating(
    @NotNull final Runnable runnable,
    final long delayTicks,
    final long intervalTicks
  ) {
    return this.runRepeating(
        runnable,
        Times.durationFrom(delayTicks),
        Times.durationFrom(intervalTicks)
      );
  }

  @NotNull
  default Task runRepeating(
    @NotNull final Runnable runnable,
    final long delay,
    @NotNull final TimeUnit delayUnit,
    final long interval,
    @NotNull final TimeUnit intervalUnit
  ) {
    return this.runRepeating(
        runnable,
        Times.durationFrom(delay, delayUnit),
        Times.durationFrom(interval, intervalUnit)
      );
  }

  @NotNull
  default Task runRepeating(
    @NotNull final Runnable runnable,
    @NotNull final Duration delay,
    @NotNull final Duration interval
  ) {
    return this.runRepeating(new RunnableToConsumer<>(runnable), delay, interval);
  }

  @NotNull
  default Task runRepeatingCloseIf(
    @NotNull final Predicate<Task> taskPredicate,
    final long delayTicks,
    final long intervalTicks
  ) {
    return this.runRepeatingCloseIf(
        taskPredicate,
        Times.durationFrom(delayTicks),
        Times.durationFrom(intervalTicks)
      );
  }

  @NotNull
  default Task runRepeatingCloseIf(
    @NotNull final Predicate<Task> taskPredicate,
    final long delay,
    @NotNull final TimeUnit delayUnit,
    final long interval,
    @NotNull final TimeUnit intervalUnit
  ) {
    return this.runRepeatingCloseIf(
        taskPredicate,
        Times.durationFrom(delay, delayUnit),
        Times.durationFrom(interval, intervalUnit)
      );
  }

  @NotNull
  Task runRepeatingCloseIf(
    @NotNull final Predicate<Task> taskPredicate,
    @NotNull final Duration delay,
    @NotNull final Duration interval
  );

  @NotNull
  default Task scheduleRepeating(
    @NotNull final Runnable task,
    final long delayTicks,
    final long intervalTicks
  ) {
    return this.scheduleRepeating(
        task,
        Times.durationFrom(delayTicks),
        Times.durationFrom(intervalTicks)
      );
  }

  @NotNull
  default Task scheduleRepeating(
    @NotNull final Runnable task,
    final long delay,
    final long interval,
    @NotNull final TimeUnit unit
  ) {
    return this.scheduleRepeating(
        task,
        Times.durationFrom(delay, unit),
        Times.durationFrom(interval, unit)
      );
  }

  @NotNull
  default Task scheduleRepeating(
    @NotNull final Runnable task,
    @NotNull final Duration delay,
    @NotNull final Duration interval
  ) {
    return this.scheduleRepeating(new RunnableToConsumer<>(task), delay, interval);
  }

  @NotNull
  default Task scheduleRepeating(
    @NotNull final Consumer<Task> taskConsumer,
    final long delayTicks,
    final long intervalTicks
  ) {
    return this.scheduleRepeating(
        taskConsumer,
        Times.durationFrom(delayTicks),
        Times.durationFrom(intervalTicks)
      );
  }

  @NotNull
  default Task scheduleRepeating(
    @NotNull final Consumer<Task> taskConsumer,
    final long delay,
    final long interval,
    @NotNull final TimeUnit unit
  ) {
    return this.scheduleRepeating(
        taskConsumer,
        Times.durationFrom(delay, unit),
        Times.durationFrom(delay, unit)
      );
  }

  @NotNull
  default Task scheduleRepeating(
    @NotNull final Consumer<Task> taskConsumer,
    @NotNull final Duration delay,
    @NotNull final Duration interval
  ) {
    return this.scheduleRepeatingCloseIf(
        new ConsumerToPredicate<>(taskConsumer, false),
        delay,
        interval
      );
  }

  @NotNull
  default Task scheduleRepeatingCloseIf(
    @NotNull final Predicate<Task> taskPredicate,
    final long delayTicks,
    final long intervalTicks
  ) {
    return this.scheduleRepeatingCloseIf(
        taskPredicate,
        Times.durationFrom(delayTicks),
        Times.durationFrom(intervalTicks)
      );
  }

  @NotNull
  default Task scheduleRepeatingCloseIf(
    @NotNull final Predicate<Task> taskPredicate,
    final long delay,
    final long interval,
    @NotNull final TimeUnit unit
  ) {
    return this.scheduleRepeatingCloseIf(
        taskPredicate,
        Times.durationFrom(delay, unit),
        Times.durationFrom(delay, unit)
      );
  }

  @NotNull
  Task scheduleRepeatingCloseIf(
    @NotNull Predicate<Task> taskPredicate,
    @NotNull Duration delay,
    @NotNull Duration interval
  );

  @NotNull
  default <T> Promise<T> supply(@NotNull final Supplier<T> supplier) {
    return Promise.supplying(this.context(), supplier);
  }

  @NotNull
  default <T> Promise<T> supplyLater(@NotNull final Supplier<T> supplier, final long delayTicks) {
    return this.supplyLater(supplier, Times.durationFrom(delayTicks));
  }

  @NotNull
  default <T> Promise<T> supplyLater(
    @NotNull final Supplier<T> supplier,
    final long delay,
    @NotNull final TimeUnit unit
  ) {
    return this.supplyLater(supplier, Times.durationFrom(delay, unit));
  }

  @NotNull
  default <T> Promise<T> supplyLater(
    @NotNull final Supplier<T> supplier,
    @NotNull final Duration delay
  ) {
    return Promise.supplyingDelayed(this.context(), supplier, delay);
  }
}
