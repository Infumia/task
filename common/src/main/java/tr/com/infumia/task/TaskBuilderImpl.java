package tr.com.infumia.task;

import java.time.Duration;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Supplier;
import org.jetbrains.annotations.NotNull;

record TaskBuilderImpl(@NotNull ThreadContextual async, @NotNull ThreadContextual sync)
  implements TaskBuilder {
  static final TaskBuilder INSTANCE = new TaskBuilderImpl();

  private TaskBuilderImpl() {
    this(
      new ThreadContextualBuilder(ThreadContext.ASYNC),
      new ThreadContextualBuilder(ThreadContext.SYNC)
    );
  }

  private record ContextualPromiseBuilderImpl(@NotNull ThreadContext context)
    implements ContextualPromiseBuilder {
    @NotNull
    @Override
    public <T> Promise<T> call(@NotNull final Callable<T> callable) {
      return Internal.get(this.context).call(callable);
    }

    @NotNull
    @Override
    public Promise<Void> run(@NotNull final Runnable runnable) {
      return Internal.get(this.context).run(runnable);
    }

    @NotNull
    @Override
    public <T> Promise<T> supply(@NotNull final Supplier<T> supplier) {
      return Internal.get(this.context).supply(supplier);
    }
  }

  private record ContextualTaskBuilderImpl(
    @NotNull ThreadContext context,
    @NotNull Duration delay,
    @NotNull Duration interval
  )
    implements ContextualTaskBuilder {
    @NotNull
    @Override
    public Task consume(@NotNull final Consumer<Task> consumer) {
      return Internal.get(this.context).runRepeating(consumer, this.delay, this.interval);
    }

    @NotNull
    @Override
    public Task run(@NotNull final Runnable runnable) {
      return Internal.get(this.context).runRepeating(runnable, this.delay, this.interval);
    }
  }

  private record DelayedBuilder(@NotNull ThreadContext context, @NotNull Duration delay)
    implements TaskBuilder.Delayed {
    @NotNull
    @Override
    public <T> Promise<T> call(@NotNull final Callable<T> callable) {
      return Internal.get(this.context).callLater(callable, this.delay);
    }

    @NotNull
    @Override
    public Promise<Void> run(@NotNull final Runnable runnable) {
      return Internal.get(this.context).runLater(runnable, this.delay);
    }

    @NotNull
    @Override
    public <T> Promise<T> supply(@NotNull final Supplier<T> supplier) {
      return Internal.get(this.context).supplyLater(supplier, this.delay);
    }

    @NotNull
    @Override
    public ContextualTaskBuilder every(@NotNull final Duration duration) {
      return new ContextualTaskBuilderImpl(this.context, this.delay, duration);
    }
  }

  private record ThreadContextualBuilder(
    @NotNull ThreadContext context,
    @NotNull ContextualPromiseBuilder instant
  )
    implements TaskBuilder.ThreadContextual {
    private ThreadContextualBuilder(@NotNull final ThreadContext context) {
      this(context, new ContextualPromiseBuilderImpl(context));
    }

    @NotNull
    @Override
    public TaskBuilder.Delayed after(@NotNull final Duration duration) {
      return new DelayedBuilder(this.context, duration);
    }

    @NotNull
    @Override
    public ContextualTaskBuilder afterAndEvery(@NotNull final Duration duration) {
      return new ContextualTaskBuilderImpl(this.context, duration, duration);
    }

    @NotNull
    @Override
    public ContextualTaskBuilder every(@NotNull final Duration duration) {
      return new ContextualTaskBuilderImpl(this.context, Duration.ZERO, duration);
    }

    @NotNull
    @Override
    public ContextualPromiseBuilder now() {
      return this.instant;
    }
  }
}
