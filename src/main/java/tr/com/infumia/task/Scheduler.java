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
    return Promise.supplying(this.context(), new CallableToSupplier<>(callable));
  }

  @NotNull
  default <T> Promise<T> callLater(@NotNull final Callable<T> callable, final long delayTicks) {
    return Promise.supplyingDelayed(this.context(), new CallableToSupplier<>(callable), delayTicks);
  }

  @NotNull
  default <T> Promise<T> callLater(
    @NotNull final Callable<T> callable,
    final long delay,
    @NotNull final TimeUnit unit
  ) {
    return this.callLater(callable, Internal.ticksFrom(delay, unit));
  }

  @NotNull
  default <T> Promise<T> callLater(
    @NotNull final Callable<T> callable,
    @NotNull final Duration delay
  ) {
    return this.callLater(callable, Internal.ticksFrom(delay));
  }

  @NotNull
  ThreadContext context();

  @NotNull
  Promise<Void> run(@NotNull Runnable runnable);

  @NotNull
  default Promise<Void> runLater(
    @NotNull final Runnable runnable,
    final long delay,
    @NotNull final TimeUnit unit
  ) {
    return this.runLater(runnable, Internal.ticksFrom(delay, unit));
  }

  @NotNull
  default Promise<Void> runLater(@NotNull final Runnable runnable, @NotNull final Duration delay) {
    return this.runLater(runnable, delay.toMillis(), TimeUnit.MILLISECONDS);
  }

  @NotNull
  default Promise<Void> runLater(@NotNull final Runnable runnable, final long delayTicks) {
    return this.runLater(runnable, Duration.ofMillis(Internal.ticksToMs(delayTicks)));
  }

  @NotNull
  default Task runRepeating(
    @NotNull final Consumer<Task> taskConsumer,
    final long delayTicks,
    final long intervalTicks
  ) {
    return this.runRepeatingCloseIf(
        new ConsumerToPredicate<>(taskConsumer, false),
        delayTicks,
        intervalTicks
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
        Internal.ticksFrom(delay, delayUnit),
        Internal.ticksFrom(interval, intervalUnit)
      );
  }

  @NotNull
  default Task runRepeating(
    @NotNull final Consumer<Task> taskConsumer,
    @NotNull final Duration delay,
    @NotNull final Duration interval
  ) {
    return this.runRepeating(taskConsumer, Internal.ticksFrom(delay), Internal.ticksFrom(interval));
  }

  @NotNull
  default Task runRepeating(
    @NotNull final Runnable runnable,
    final long delayTicks,
    final long intervalTicks
  ) {
    return this.runRepeating(new RunnableToConsumer<>(runnable), delayTicks, intervalTicks);
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
        Internal.ticksFrom(delay, delayUnit),
        Internal.ticksFrom(interval, intervalUnit)
      );
  }

  @NotNull
  default Task runRepeating(
    @NotNull final Runnable runnable,
    @NotNull final Duration delay,
    @NotNull final Duration interval
  ) {
    return this.runRepeating(runnable, Internal.ticksFrom(delay), Internal.ticksFrom(interval));
  }

  @NotNull
  Task runRepeatingCloseIf(
    @NotNull Predicate<Task> taskPredicate,
    long delayTicks,
    long intervalTicks
  );

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
        Internal.ticksFrom(delay, delayUnit),
        Internal.ticksFrom(interval, intervalUnit)
      );
  }

  @NotNull
  default Task runRepeatingCloseIf(
    @NotNull final Predicate<Task> taskPredicate,
    @NotNull final Duration delay,
    @NotNull final Duration interval
  ) {
    return this.runRepeatingCloseIf(
        taskPredicate,
        Internal.ticksFrom(delay),
        Internal.ticksFrom(interval)
      );
  }

  @NotNull
  Task scheduleRepeating(
    @NotNull Predicate<Task> taskPredicate,
    long delay,
    long interval,
    @NotNull TimeUnit unit
  );

  @NotNull
  default Task scheduleRepeating(
    @NotNull final Consumer<Task> taskPredicate,
    final long delay,
    final long interval,
    @NotNull final TimeUnit unit
  ) {
    return this.scheduleRepeating(
        new ConsumerToPredicate<>(taskPredicate, false),
        delay,
        interval,
        unit
      );
  }

  @NotNull
  default Task scheduleRepeating(
    @NotNull final Runnable taskPredicate,
    final long delay,
    final long interval,
    @NotNull final TimeUnit unit
  ) {
    return this.scheduleRepeating(new RunnableToConsumer<>(taskPredicate), delay, interval, unit);
  }

  @NotNull
  default <T> Promise<T> supply(@NotNull final Supplier<T> supplier) {
    return Promise.supplying(this.context(), supplier);
  }

  @NotNull
  default <T> Promise<T> supplyLater(@NotNull final Supplier<T> supplier, final long delayTicks) {
    return Promise.supplyingDelayed(this.context(), supplier, delayTicks);
  }

  @NotNull
  default <T> Promise<T> supplyLater(
    @NotNull final Supplier<T> supplier,
    final long delay,
    @NotNull final TimeUnit unit
  ) {
    return this.supplyLater(supplier, Internal.ticksFrom(delay, unit));
  }

  @NotNull
  default <T> Promise<T> supplyLater(
    @NotNull final Supplier<T> supplier,
    @NotNull final Duration delay
  ) {
    return this.supplyLater(supplier, Internal.ticksFrom(delay));
  }
}
