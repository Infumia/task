package tr.com.infumia.task;

import java.time.Duration;
import java.util.concurrent.TimeUnit;
import org.jetbrains.annotations.NotNull;

public interface TaskBuilder {
  @NotNull
  static TaskBuilder newBuilder() {
    return TaskBuilderImpl.INSTANCE;
  }

  @NotNull
  ThreadContextual async();

  @NotNull
  default ThreadContextual on(@NotNull final ThreadContext context) {
    return switch (context) {
      case SYNC -> this.sync();
      case ASYNC -> this.async();
    };
  }

  @NotNull
  ThreadContextual sync();

  interface Delayed extends ContextualPromiseBuilder {
    @NotNull
    ContextualTaskBuilder every(long ticks);

    @NotNull
    default ContextualTaskBuilder every(final long duration, @NotNull final TimeUnit unit) {
      return this.every(Internal.ticksFrom(duration, unit));
    }

    @NotNull
    default ContextualTaskBuilder every(@NotNull final Duration duration) {
      return this.every(duration.toMillis(), TimeUnit.MILLISECONDS);
    }
  }

  interface ThreadContextual {
    @NotNull
    Delayed after(long ticks);

    @NotNull
    default Delayed after(final long duration, @NotNull final TimeUnit unit) {
      return this.after(Internal.ticksFrom(duration, unit));
    }

    @NotNull
    default Delayed after(@NotNull final Duration duration) {
      return this.after(duration.toMillis(), TimeUnit.MILLISECONDS);
    }

    @NotNull
    ContextualTaskBuilder afterAndEvery(long ticks);

    @NotNull
    default ContextualTaskBuilder afterAndEvery(final long duration, @NotNull final TimeUnit unit) {
      return this.afterAndEvery(Internal.ticksFrom(duration, unit));
    }

    @NotNull
    default ContextualTaskBuilder afterAndEvery(@NotNull final Duration duration) {
      return this.afterAndEvery(duration.toMillis(), TimeUnit.MILLISECONDS);
    }

    @NotNull
    ContextualTaskBuilder every(long ticks);

    @NotNull
    default ContextualTaskBuilder every(final long duration, @NotNull final TimeUnit unit) {
      return this.every(Internal.ticksFrom(duration, unit));
    }

    @NotNull
    default ContextualTaskBuilder every(@NotNull final Duration duration) {
      return this.every(duration.toMillis(), TimeUnit.MILLISECONDS);
    }

    @NotNull
    ContextualPromiseBuilder now();
  }
}
