package tr.com.infumia.task;

import java.time.Duration;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Supplier;
import org.jetbrains.annotations.NotNull;

/**
 * an interface to determine schedulers.
 */
public interface Scheduler extends Executor {
  @NotNull
  default <T> Promise<T> call(@NotNull final Callable<T> callable) {
    return Promise.supplying(
      this.context(),
      Internal.callableToSupplier(callable)
    );
  }

  @NotNull
  default <T> Promise<T> callLater(
    @NotNull final Callable<T> callable,
    final long delayTicks
  ) {
    return Promise.supplyingDelayed(
      this.context(),
      Internal.callableToSupplier(callable),
      delayTicks
    );
  }

  @NotNull
  default <T> Promise<T> callLater(
    @NotNull final Callable<T> callable,
    final long delay,
    @NotNull final TimeUnit unit
  ) {
    return Promise.supplyingDelayed(
      this.context(),
      Internal.callableToSupplier(callable),
      delay,
      unit
    );
  }

  /**
   * obtains the context.
   *
   * @return context.
   */
  @NotNull
  ThreadContext context();

  @NotNull
  default Promise<Void> run(@NotNull final Runnable runnable) {
    return Promise.supplying(
      this.context(),
      Internal.runnableToSupplier(runnable)
    );
  }

  @NotNull
  default Promise<Void> runLater(
    @NotNull final Runnable runnable,
    final long delayTicks
  ) {
    return Promise.supplyingDelayed(
      this.context(),
      Internal.runnableToSupplier(runnable),
      delayTicks
    );
  }

  @NotNull
  default Promise<Void> runLater(
    @NotNull final Runnable runnable,
    final long delay,
    @NotNull final TimeUnit unit
  ) {
    return Promise.supplyingDelayed(
      this.context(),
      Internal.runnableToSupplier(runnable),
      delay,
      unit
    );
  }

  @NotNull
  Task runRepeating(
    @NotNull Consumer<Task> consumer,
    long delayTicks,
    long intervalTicks
  );

  @NotNull
  default Task runRepeating(
    @NotNull final Consumer<Task> consumer,
    final long delay,
    @NotNull final TimeUnit delayUnit,
    final long interval,
    @NotNull final TimeUnit intervalUnit
  ) {
    return this.runRepeating(
        consumer,
        Internal.ticksFrom(delay, delayUnit),
        Internal.ticksFrom(interval, intervalUnit)
      );
  }

  @NotNull
  default Task runRepeating(
    @NotNull final Consumer<Task> consumer,
    @NotNull final Duration delay,
    @NotNull final Duration interval
  ) {
    return this.runRepeating(
        consumer,
        delay.toMillis(),
        TimeUnit.MICROSECONDS,
        interval.toMillis(),
        TimeUnit.MICROSECONDS
      );
  }

  @NotNull
  default Task runRepeating(
    @NotNull final Runnable runnable,
    final long delayTicks,
    final long intervalTicks
  ) {
    return this.runRepeating(
        Internal.runnableToConsumer(runnable),
        delayTicks,
        intervalTicks
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
        Internal.runnableToConsumer(runnable),
        delay,
        delayUnit,
        interval,
        intervalUnit
      );
  }

  @NotNull
  default Task runRepeating(
    @NotNull final Runnable runnable,
    @NotNull final Duration delay,
    @NotNull final Duration interval
  ) {
    return this.runRepeating(
        runnable,
        delay.toMillis(),
        TimeUnit.MICROSECONDS,
        interval.toMillis(),
        TimeUnit.MICROSECONDS
      );
  }

  @NotNull
  default <T> Promise<T> supply(@NotNull final Supplier<T> supplier) {
    return Promise.supplying(this.context(), supplier);
  }

  @NotNull
  default <T> Promise<T> supplyLater(
    @NotNull final Supplier<T> supplier,
    final long delayTicks
  ) {
    return Promise.supplyingDelayed(this.context(), supplier, delayTicks);
  }

  @NotNull
  default <T> Promise<T> supplyLater(
    @NotNull final Supplier<T> supplier,
    final long delay,
    @NotNull final TimeUnit unit
  ) {
    return Promise.supplyingDelayed(this.context(), supplier, delay, unit);
  }
}
