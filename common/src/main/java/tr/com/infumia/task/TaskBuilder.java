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
    default ContextualTaskBuilder every(final long ticks) {
      return this.every(Times.durationFrom(ticks));
    }

    @NotNull
    default ContextualTaskBuilder every(final long duration, @NotNull final TimeUnit unit) {
      return this.every(Times.durationFrom(duration, unit));
    }

    @NotNull
    ContextualTaskBuilder every(@NotNull Duration duration);
  }

  interface ThreadContextual {
    @NotNull
    default Delayed after(final long ticks) {
      return this.after(Times.durationFrom(ticks));
    }

    @NotNull
    default Delayed after(final long duration, @NotNull final TimeUnit unit) {
      return this.after(Times.durationFrom(duration, unit));
    }

    @NotNull
    Delayed after(@NotNull Duration duration);

    @NotNull
    default ContextualTaskBuilder afterAndEvery(final long ticks) {
      return this.afterAndEvery(Times.durationFrom(ticks));
    }

    @NotNull
    default ContextualTaskBuilder afterAndEvery(final long duration, @NotNull final TimeUnit unit) {
      return this.afterAndEvery(Times.durationFrom(duration, unit));
    }

    @NotNull
    ContextualTaskBuilder afterAndEvery(@NotNull Duration duration);

    @NotNull
    default ContextualTaskBuilder every(final long ticks) {
      return this.every(Times.durationFrom(ticks));
    }

    @NotNull
    default ContextualTaskBuilder every(final long duration, @NotNull final TimeUnit unit) {
      return this.every(Times.durationFrom(duration, unit));
    }

    @NotNull
    ContextualTaskBuilder every(@NotNull Duration duration);

    @NotNull
    ContextualPromiseBuilder now();
  }
}
