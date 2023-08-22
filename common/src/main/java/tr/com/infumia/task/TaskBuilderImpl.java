package tr.com.infumia.task;

import java.time.Duration;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Supplier;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

@Getter
final class TaskBuilderImpl implements TaskBuilder {

  static final TaskBuilder INSTANCE = new TaskBuilderImpl();

  @NotNull
  private final ThreadContextual async;

  @NotNull
  private final ThreadContextual sync;

  private TaskBuilderImpl(
    @NotNull final ThreadContextual async,
    @NotNull final ThreadContextual sync
  ) {
    this.async = async;
    this.sync = sync;
  }

  private TaskBuilderImpl() {
    this(
      new ThreadContextualBuilder(ThreadContext.ASYNC),
      new ThreadContextualBuilder(ThreadContext.SYNC)
    );
  }

  private static final class ContextualPromiseBuilderImpl implements ContextualPromiseBuilder {

    @NotNull
    private final ThreadContext context;

    private ContextualPromiseBuilderImpl(@NotNull final ThreadContext context) {
      this.context = context;
    }

    @NotNull
    @Override
    public <T> Promise<T> call(@NotNull final Callable<T> callable) {
      return Internal.get(this.context).call(callable);
    }

    @NotNull
    @Override
    public Promise<?> run(@NotNull final Runnable runnable) {
      return Internal.get(this.context).run(runnable);
    }

    @NotNull
    @Override
    public <T> Promise<T> supply(@NotNull final Supplier<T> supplier) {
      return Internal.get(this.context).supply(supplier);
    }
  }

  private static final class ContextualTaskBuilderImpl implements ContextualTaskBuilder {

    @NotNull
    private final ThreadContext context;

    @NotNull
    private final Duration delay;

    @NotNull
    private final Duration interval;

    private ContextualTaskBuilderImpl(
      @NotNull final ThreadContext context,
      @NotNull final Duration delay,
      @NotNull final Duration interval
    ) {
      this.context = context;
      this.delay = delay;
      this.interval = interval;
    }

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

  private static final class DelayedBuilder implements TaskBuilder.Delayed {

    @NotNull
    private final ThreadContext context;

    @NotNull
    private final Duration delay;

    private DelayedBuilder(@NotNull final ThreadContext context, @NotNull final Duration delay) {
      this.context = context;
      this.delay = delay;
    }

    @NotNull
    @Override
    public <T> Promise<T> call(@NotNull final Callable<T> callable) {
      return Internal.get(this.context).callLater(callable, this.delay);
    }

    @NotNull
    @Override
    public Promise<?> run(@NotNull final Runnable runnable) {
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

  private static final class ThreadContextualBuilder implements TaskBuilder.ThreadContextual {

    @NotNull
    private final ThreadContext context;

    @NotNull
    private final ContextualPromiseBuilder instant;

    private ThreadContextualBuilder(
      @NotNull final ThreadContext context,
      @NotNull final ContextualPromiseBuilder instant
    ) {
      this.context = context;
      this.instant = instant;
    }

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
