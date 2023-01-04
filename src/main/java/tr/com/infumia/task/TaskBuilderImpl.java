package tr.com.infumia.task;

import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Supplier;
import org.jetbrains.annotations.NotNull;

record TaskBuilderImpl(
  @NotNull ThreadContextual async,
  @NotNull ThreadContextual sync
)
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
      return Schedulers.get(this.context).call(callable);
    }

    @NotNull
    @Override
    public Promise<Void> run(@NotNull final Runnable runnable) {
      return Schedulers.get(this.context).run(runnable);
    }

    @NotNull
    @Override
    public <T> Promise<T> supply(@NotNull final Supplier<T> supplier) {
      return Schedulers.get(this.context).supply(supplier);
    }
  }

  private record ContextualTaskBuilderTickImpl(
    @NotNull ThreadContext context,
    long delay,
    long interval
  )
    implements ContextualTaskBuilder {
    @NotNull
    @Override
    public Task consume(@NotNull final Consumer<Task> consumer) {
      return Schedulers
        .get(this.context)
        .runRepeating(consumer, this.delay, this.interval);
    }

    @NotNull
    @Override
    public Task run(@NotNull final Runnable runnable) {
      return Schedulers
        .get(this.context)
        .runRepeating(runnable, this.delay, this.interval);
    }
  }

  private record DelayedBuilder(@NotNull ThreadContext context, long delay)
    implements TaskBuilder.Delayed {
    @NotNull
    @Override
    public <T> Promise<T> call(@NotNull final Callable<T> callable) {
      return Schedulers.get(this.context).callLater(callable, this.delay);
    }

    @NotNull
    @Override
    public Promise<Void> run(@NotNull final Runnable runnable) {
      return Schedulers.get(this.context).runLater(runnable, this.delay);
    }

    @NotNull
    @Override
    public <T> Promise<T> supply(@NotNull final Supplier<T> supplier) {
      return Schedulers.get(this.context).supplyLater(supplier, this.delay);
    }

    @NotNull
    @Override
    public ContextualTaskBuilder every(final long ticks) {
      return new ContextualTaskBuilderTickImpl(this.context, this.delay, ticks);
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
    public TaskBuilder.Delayed after(final long ticks) {
      return new DelayedBuilder(this.context, ticks);
    }

    @NotNull
    @Override
    public ContextualTaskBuilder afterAndEvery(final long ticks) {
      return new ContextualTaskBuilderTickImpl(this.context, ticks, ticks);
    }

    @NotNull
    @Override
    public ContextualTaskBuilder every(final long ticks) {
      return new ContextualTaskBuilderTickImpl(this.context, 0, ticks);
    }

    @NotNull
    @Override
    public ContextualPromiseBuilder now() {
      return this.instant;
    }
  }
}
