package tr.com.infumia.task;

import java.time.Duration;
import java.util.concurrent.TimeUnit;
import org.jetbrains.annotations.NotNull;

/**
 * an interface to determine task builders.
 */
public interface TaskBuilder {

  /**
   * creates a new task builder.
   *
   * @return a newly created task builder.
   */
  @NotNull
  static TaskBuilder newBuilder() {
    return TaskBuilderImpl.INSTANCE;
  }

  /**
   * obtains the async.
   *
   * @return async.
   */
  @NotNull
  ThreadContextual async();

  /**
   * gets the task depends on the context.
   *
   * @param context the context to get.
   *
   * @return task.
   */
  @NotNull
  default ThreadContextual on(@NotNull final ThreadContext context) {
    return switch (context) {
      case SYNC -> this.sync();
      case ASYNC -> this.async();
    };
  }

  /**
   * obtains the sync.
   *
   * @return sync.
   */
  @NotNull
  ThreadContextual sync();

  /**
   * an interface to determine delayed task builders.
   */
  interface Delayed extends ContextualPromiseBuilder {

    /**
     * marks that the new task should repeat on the specified interval.
     *
     * @param ticks the number of ticks to wait between executions.
     *
     * @return a delayed builder.
     */
    @NotNull
    ContextualTaskBuilder every(long ticks);

    /**
     * marks that the new task should repeat on the specified interval.
     *
     * @param duration the duration to wait between executions.
     * @param unit the units of the duration.
     *
     * @return a delayed builder.
     */
    @NotNull
    default ContextualTaskBuilder every(final long duration, @NotNull final TimeUnit unit) {
      return this.every(Internal.ticksFrom(duration, unit));
    }

    /**
     * marks that the new task should repeat on the specified interval.
     *
     * @param duration the duration to wait between executions.
     *
     * @return a delayed builder.
     */
    @NotNull
    default ContextualTaskBuilder every(@NotNull final Duration duration) {
      return this.every(duration.toMillis(), TimeUnit.MILLISECONDS);
    }
  }

  /**
   * an interface to determine thread contextual task builders.
   */
  interface ThreadContextual {

    /**
     * marks that the new task should run after the specified delay.
     *
     * @param ticks the number of ticks to delay execution by.
     *
     * @return a delayed builder.
     */
    @NotNull
    Delayed after(long ticks);

    /**
     * marks that the new task should run after the specified delay.
     *
     * @param duration the duration to delay execution by.
     * @param unit the units of the duration.
     *
     * @return a delayed builder.
     */
    @NotNull
    default Delayed after(final long duration, @NotNull final TimeUnit unit) {
      return this.after(Internal.ticksFrom(duration, unit));
    }

    /**
     * marks that the new task should run after the specified delay.
     *
     * @param duration the duration to delay execution by.
     *
     * @return a delayed builder.
     */
    @NotNull
    default Delayed after(@NotNull final Duration duration) {
      return this.after(duration.toMillis(), TimeUnit.MILLISECONDS);
    }

    /**
     * marks that the new task should run after the specified delay.
     *
     * @param ticks the number of ticks to delay execution by.
     *
     * @return a delayed builder.
     */
    @NotNull
    ContextualTaskBuilder afterAndEvery(long ticks);

    /**
     * marks that the new task should run after the specified delay.
     *
     * @param duration the duration to delay execution by.
     * @param unit the units of the duration.
     *
     * @return a delayed builder.
     */
    @NotNull
    default ContextualTaskBuilder afterAndEvery(final long duration, @NotNull final TimeUnit unit) {
      return this.afterAndEvery(Internal.ticksFrom(duration, unit));
    }

    /**
     * marks that the new task should run after the specified delay.
     *
     * @param duration the duration to delay execution by.
     *
     * @return a delayed builder.
     */
    @NotNull
    default ContextualTaskBuilder afterAndEvery(@NotNull final Duration duration) {
      return this.afterAndEvery(duration.toMillis(), TimeUnit.MILLISECONDS);
    }

    /**
     * marks that the new task should start running instantly, but repeat on the specified interval.
     *
     * @param ticks the number of ticks to wait between executions.
     *
     * @return a delayed builder.
     */
    @NotNull
    ContextualTaskBuilder every(long ticks);

    /**
     * marks that the new task should start running instantly, but repeat on the specified interval.
     *
     * @param duration the duration to wait between executions.
     * @param unit the units of the duration.
     *
     * @return a delayed builder.
     */
    @NotNull
    default ContextualTaskBuilder every(final long duration, @NotNull final TimeUnit unit) {
      return this.every(Internal.ticksFrom(duration, unit));
    }

    /**
     * marks that the new task should start running instantly, but repeat on the specified interval.
     *
     * @param duration the duration to wait between executions.
     *
     * @return a delayed builder.
     */
    @NotNull
    default ContextualTaskBuilder every(@NotNull final Duration duration) {
      return this.every(duration.toMillis(), TimeUnit.MILLISECONDS);
    }

    /**
     * marks that the new task should execute immediately,
     *
     * @return an instant promise builder.
     */
    @NotNull
    ContextualPromiseBuilder now();
  }
}
