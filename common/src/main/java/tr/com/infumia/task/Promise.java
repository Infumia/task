package tr.com.infumia.task;

import java.time.Duration;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tr.com.infumia.terminable.Terminable;

@SuppressWarnings({ "unchecked", "resource", "unused" })
public interface Promise<V> extends Future<V>, Terminable {
  @NotNull
  static <U> Promise<U> completed(@Nullable final U value) {
    return new PromiseImpl<>(value);
  }

  @NotNull
  static <U> Promise<U> empty() {
    return new PromiseImpl<>();
  }

  @NotNull
  static <U> Promise<U> exceptionally(@NotNull final Throwable exception) {
    return new PromiseImpl<>(exception);
  }

  @NotNull
  static Promise<Void> start() {
    return Promise.completed(null);
  }

  @NotNull
  static <U> Promise<U> supplying(
    @NotNull final ThreadContext context,
    @NotNull final Supplier<U> supplier
  ) {
    return Promise.<U>empty().supply(context, supplier);
  }

  @NotNull
  static <U> Promise<U> supplyingAsync(@NotNull final Supplier<U> supplier) {
    return Promise.<U>empty().supplyAsync(supplier);
  }

  @NotNull
  static <U> Promise<U> supplyingDelayed(
    @NotNull final ThreadContext context,
    @NotNull final Supplier<U> supplier,
    final long delayTicks
  ) {
    return Promise.supplyingDelayed(context, supplier, Internal.durationFrom(delayTicks));
  }

  @NotNull
  static <U> Promise<U> supplyingDelayed(
    @NotNull final ThreadContext context,
    @NotNull final Supplier<U> supplier,
    final long delay,
    @NotNull final TimeUnit unit
  ) {
    return Promise.supplyingDelayed(context, supplier, Internal.durationFrom(delay, unit));
  }

  @NotNull
  static <U> Promise<U> supplyingDelayed(
    @NotNull final ThreadContext context,
    @NotNull final Supplier<U> supplier,
    @NotNull final Duration delay
  ) {
    return Promise.<U>empty().supplyDelayed(context, supplier, delay);
  }

  @NotNull
  static <U> Promise<U> supplyingDelayedAsync(
    @NotNull final Supplier<U> supplier,
    final long delayTicks
  ) {
    return Promise.supplyingDelayedAsync(supplier, Internal.durationFrom(delayTicks));
  }

  @NotNull
  static <U> Promise<U> supplyingDelayedAsync(
    @NotNull final Supplier<U> supplier,
    final long delay,
    @NotNull final TimeUnit unit
  ) {
    return Promise.supplyingDelayedAsync(supplier, Internal.durationFrom(delay, unit));
  }

  @NotNull
  static <U> Promise<U> supplyingDelayedAsync(
    @NotNull final Supplier<U> supplier,
    @NotNull final Duration delay
  ) {
    return Promise.<U>empty().supplyDelayedAsync(supplier, delay);
  }

  @NotNull
  static <U> Promise<U> supplyingDelayedSync(
    @NotNull final Supplier<U> supplier,
    final long delayTicks
  ) {
    return Promise.supplyingDelayedSync(supplier, Internal.durationFrom(delayTicks));
  }

  @NotNull
  static <U> Promise<U> supplyingDelayedSync(
    @NotNull final Supplier<U> supplier,
    final long delay,
    @NotNull final TimeUnit unit
  ) {
    return Promise.supplyingDelayedSync(supplier, Internal.durationFrom(delay, unit));
  }

  @NotNull
  static <U> Promise<U> supplyingDelayedSync(
    @NotNull final Supplier<U> supplier,
    @NotNull final Duration delay
  ) {
    return Promise.<U>empty().supplyDelayedSync(supplier, delay);
  }

  @NotNull
  static <U> Promise<U> supplyingExceptionally(
    @NotNull final ThreadContext context,
    @NotNull final Callable<U> callable
  ) {
    return Promise.<U>empty().supplyExceptionally(context, callable);
  }

  @NotNull
  static <U> Promise<U> supplyingExceptionallyAsync(@NotNull final Callable<U> callable) {
    return Promise.<U>empty().supplyExceptionallyAsync(callable);
  }

  @NotNull
  static <U> Promise<U> supplyingExceptionallyDelayed(
    @NotNull final ThreadContext context,
    @NotNull final Callable<U> callable,
    final long delayTicks
  ) {
    return Promise.supplyingExceptionallyDelayed(
      context,
      callable,
      Internal.durationFrom(delayTicks)
    );
  }

  @NotNull
  static <U> Promise<U> supplyingExceptionallyDelayed(
    @NotNull final ThreadContext context,
    @NotNull final Callable<U> callable,
    final long delay,
    @NotNull final TimeUnit unit
  ) {
    return Promise.supplyingExceptionallyDelayed(
      context,
      callable,
      Internal.durationFrom(delay, unit)
    );
  }

  @NotNull
  static <U> Promise<U> supplyingExceptionallyDelayed(
    @NotNull final ThreadContext context,
    @NotNull final Callable<U> callable,
    @NotNull final Duration delay
  ) {
    return Promise.<U>empty().supplyExceptionallyDelayed(context, callable, delay);
  }

  @NotNull
  static <U> Promise<U> supplyingExceptionallyDelayedAsync(
    @NotNull final Callable<U> callable,
    final long delayTicks
  ) {
    return Promise.supplyingExceptionallyDelayedAsync(callable, Internal.durationFrom(delayTicks));
  }

  @NotNull
  static <U> Promise<U> supplyingExceptionallyDelayedAsync(
    @NotNull final Callable<U> callable,
    final long delay,
    @NotNull final TimeUnit unit
  ) {
    return Promise.supplyingExceptionallyDelayedAsync(callable, Internal.durationFrom(delay, unit));
  }

  @NotNull
  static <U> Promise<U> supplyingExceptionallyDelayedAsync(
    @NotNull final Callable<U> callable,
    @NotNull final Duration delay
  ) {
    return Promise.<U>empty().supplyExceptionallyDelayedAsync(callable, delay);
  }

  @NotNull
  static <U> Promise<U> supplyingExceptionallyDelayedSync(
    @NotNull final Callable<U> callable,
    final long delayTicks
  ) {
    return Promise.supplyingExceptionallyDelayedSync(callable, Internal.durationFrom(delayTicks));
  }

  @NotNull
  static <U> Promise<U> supplyingExceptionallyDelayedSync(
    @NotNull final Callable<U> callable,
    final long delay,
    @NotNull final TimeUnit unit
  ) {
    return Promise.supplyingExceptionallyDelayedSync(callable, Internal.durationFrom(delay, unit));
  }

  @NotNull
  static <U> Promise<U> supplyingExceptionallyDelayedSync(
    @NotNull final Callable<U> callable,
    @NotNull final Duration delay
  ) {
    return Promise.<U>empty().supplyExceptionallyDelayedSync(callable, delay);
  }

  @NotNull
  static <U> Promise<U> supplyingExceptionallySync(@NotNull final Callable<U> callable) {
    return Promise.<U>empty().supplyExceptionallySync(callable);
  }

  @NotNull
  static <U> Promise<U> supplyingSync(@NotNull final Supplier<U> supplier) {
    return Promise.<U>empty().supplySync(supplier);
  }

  @NotNull
  static <U> Promise<U> wrapFuture(@NotNull final Future<U> future) {
    if (future instanceof CompletableFuture<?>) {
      return new PromiseImpl<>(((CompletableFuture<U>) future).thenApply(Function.identity()));
    }
    if (future instanceof CompletionStage<?>) {
      return new PromiseImpl<>(
        ((CompletionStage<U>) future).toCompletableFuture().thenApply(Function.identity())
      );
    }
    if (future.isDone()) {
      try {
        return Promise.completed(future.get());
      } catch (final ExecutionException e) {
        return Promise.exceptionally(e);
      } catch (final InterruptedException e) {
        throw new RuntimeException(e);
      }
    }
    return Promise.supplyingExceptionallyAsync(future::get);
  }

  default boolean cancel() {
    return this.cancel(true);
  }

  @Override
  default void close() {
    this.cancel();
  }

  @Override
  default boolean closed() {
    return this.isCancelled();
  }

  @NotNull
  default Promise<V> exceptionally(
    @NotNull final ThreadContext context,
    @NotNull final Function<Throwable, ? extends V> function
  ) {
    return switch (context) {
      case SYNC -> this.exceptionallySync(function);
      case ASYNC -> this.exceptionallyAsync(function);
    };
  }

  @NotNull
  default Promise<V> exceptionallyAsync(@NotNull final Function<Throwable, ? extends V> function) {
    final var promise = new PromiseImpl<V>();
    this.future()
      .whenComplete((value, t) -> {
        if (t == null) {
          promise.complete(value);
        } else {
          Executors.async(new PromiseExceptionally<>(promise, function, t));
        }
      });
    return promise;
  }

  @NotNull
  default Promise<V> exceptionallyDelayed(
    @NotNull final ThreadContext context,
    @NotNull final Function<Throwable, ? extends V> function,
    final long delayTicks
  ) {
    return this.exceptionallyDelayed(context, function, Internal.durationFrom(delayTicks));
  }

  @NotNull
  default Promise<V> exceptionallyDelayed(
    @NotNull final ThreadContext context,
    @NotNull final Function<Throwable, ? extends V> function,
    final long delay,
    @NotNull final TimeUnit unit
  ) {
    return this.exceptionallyDelayed(context, function, Internal.durationFrom(delay, unit));
  }

  @NotNull
  default Promise<V> exceptionallyDelayed(
    @NotNull final ThreadContext context,
    @NotNull final Function<Throwable, ? extends V> function,
    @NotNull final Duration delay
  ) {
    return switch (context) {
      case SYNC -> this.exceptionallyDelayedSync(function, delay);
      case ASYNC -> this.exceptionallyDelayedAsync(function, delay);
    };
  }

  @NotNull
  default Promise<V> exceptionallyDelayedAsync(
    @NotNull final Function<Throwable, ? extends V> function,
    final long delayTicks
  ) {
    return this.exceptionallyDelayedAsync(function, Internal.durationFrom(delayTicks));
  }

  @NotNull
  default Promise<V> exceptionallyDelayedAsync(
    @NotNull final Function<Throwable, ? extends V> function,
    final long delay,
    @NotNull final TimeUnit unit
  ) {
    return this.exceptionallyDelayedAsync(function, Internal.durationFrom(delay, unit));
  }

  @NotNull
  default Promise<V> exceptionallyDelayedAsync(
    @NotNull final Function<Throwable, ? extends V> function,
    @NotNull final Duration delay
  ) {
    final var promise = new PromiseImpl<V>();
    this.future()
      .whenComplete((value, t) -> {
        if (t == null) {
          promise.complete(value);
        } else {
          Executors.asyncDelayed(new PromiseExceptionally<>(promise, function, t), delay);
        }
      });
    return promise;
  }

  @NotNull
  default Promise<V> exceptionallyDelayedSync(
    @NotNull final Function<Throwable, ? extends V> function,
    final long delayTicks
  ) {
    return this.exceptionallyDelayedSync(function, Internal.durationFrom(delayTicks));
  }

  @NotNull
  default Promise<V> exceptionallyDelayedSync(
    @NotNull final Function<Throwable, ? extends V> function,
    final long delay,
    @NotNull final TimeUnit unit
  ) {
    return this.exceptionallyDelayedSync(function, Internal.durationFrom(delay, unit));
  }

  @NotNull
  default Promise<V> exceptionallyDelayedSync(
    @NotNull final Function<Throwable, ? extends V> function,
    @NotNull final Duration delay
  ) {
    final var promise = new PromiseImpl<V>();
    this.future()
      .whenComplete((value, t) -> {
        if (t == null) {
          promise.complete(value);
        } else {
          Executors.syncDelayed(new PromiseExceptionally<>(promise, function, t), delay);
        }
      });
    return promise;
  }

  @NotNull
  default Promise<V> exceptionallySync(@NotNull final Function<Throwable, ? extends V> function) {
    final var promise = new PromiseImpl<V>();
    this.future()
      .whenComplete((value, t) -> {
        if (t == null) {
          promise.complete(value);
        } else {
          Executors.sync(new PromiseExceptionally<>(promise, function, t));
        }
      });
    return promise;
  }

  @NotNull
  CompletableFuture<V> future();

  @Nullable
  @Contract("!null -> !null")
  default V getNow(@Nullable final V valueIfAbsent) {
    return this.future().getNow(valueIfAbsent);
  }

  @Override
  default boolean isCancelled() {
    return this.future().isCancelled();
  }

  @Override
  default boolean isDone() {
    return this.future().isDone();
  }

  @Override
  default V get() throws InterruptedException, ExecutionException {
    return this.future().get();
  }

  @Override
  default V get(final long timeout, @NotNull final TimeUnit unit)
    throws InterruptedException, ExecutionException, TimeoutException {
    return this.future().get(timeout, unit);
  }

  @Nullable
  default V join() {
    return this.future().join();
  }

  @NotNull
  Promise<V> supply(@Nullable V value);

  @NotNull
  default Promise<V> supply(
    @NotNull final ThreadContext context,
    @NotNull final Supplier<V> supplier
  ) {
    return switch (context) {
      case SYNC -> this.supplySync(supplier);
      case ASYNC -> this.supplyAsync(supplier);
    };
  }

  @NotNull
  Promise<V> supplyAsync(@NotNull Supplier<V> supplier);

  @NotNull
  default Promise<V> supplyDelayed(
    @NotNull final ThreadContext context,
    @NotNull final Supplier<V> supplier,
    final long delayTicks
  ) {
    return this.supplyDelayed(context, supplier, Internal.durationFrom(delayTicks));
  }

  @NotNull
  default Promise<V> supplyDelayed(
    @NotNull final ThreadContext context,
    @NotNull final Supplier<V> supplier,
    final long delay,
    @NotNull final TimeUnit unit
  ) {
    return this.supplyDelayed(context, supplier, Internal.durationFrom(delay, unit));
  }

  @NotNull
  default Promise<V> supplyDelayed(
    @NotNull final ThreadContext context,
    @NotNull final Supplier<V> supplier,
    @NotNull final Duration delay
  ) {
    return switch (context) {
      case SYNC -> this.supplyDelayedSync(supplier, delay);
      case ASYNC -> this.supplyDelayedAsync(supplier, delay);
    };
  }

  @NotNull
  default Promise<V> supplyDelayedAsync(
    @NotNull final Supplier<V> supplier,
    final long delayTicks
  ) {
    return this.supplyDelayedAsync(supplier, Internal.durationFrom(delayTicks));
  }

  @NotNull
  default Promise<V> supplyDelayedAsync(
    @NotNull final Supplier<V> supplier,
    final long delay,
    @NotNull final TimeUnit unit
  ) {
    return this.supplyDelayedAsync(supplier, Duration.of(delay, unit.toChronoUnit()));
  }

  @NotNull
  Promise<V> supplyDelayedAsync(@NotNull Supplier<V> supplier, @NotNull Duration delay);

  @NotNull
  default Promise<V> supplyDelayedSync(@NotNull final Supplier<V> supplier, final long delayTicks) {
    return this.supplyDelayedSync(supplier, Internal.durationFrom(delayTicks));
  }

  @NotNull
  default Promise<V> supplyDelayedSync(
    @NotNull final Supplier<V> supplier,
    final long delay,
    @NotNull final TimeUnit unit
  ) {
    return this.supplyDelayedSync(supplier, Internal.durationFrom(delay, unit));
  }

  @NotNull
  Promise<V> supplyDelayedSync(@NotNull Supplier<V> supplier, @NotNull Duration delay);

  @NotNull
  Promise<V> supplyException(@NotNull Throwable exception);

  @NotNull
  default Promise<V> supplyExceptionally(
    @NotNull final ThreadContext context,
    @NotNull final Callable<V> callable
  ) {
    return switch (context) {
      case SYNC -> this.supplyExceptionallySync(callable);
      case ASYNC -> this.supplyExceptionallyAsync(callable);
    };
  }

  @NotNull
  Promise<V> supplyExceptionallyAsync(@NotNull Callable<V> callable);

  @NotNull
  default Promise<V> supplyExceptionallyDelayed(
    @NotNull final ThreadContext context,
    @NotNull final Callable<V> callable,
    final long delayTicks
  ) {
    return this.supplyExceptionallyDelayed(context, callable, Internal.durationFrom(delayTicks));
  }

  @NotNull
  default Promise<V> supplyExceptionallyDelayed(
    @NotNull final ThreadContext context,
    @NotNull final Callable<V> callable,
    final long delay,
    @NotNull final TimeUnit unit
  ) {
    return this.supplyExceptionallyDelayed(context, callable, Internal.durationFrom(delay, unit));
  }

  @NotNull
  default Promise<V> supplyExceptionallyDelayed(
    @NotNull final ThreadContext context,
    @NotNull final Callable<V> callable,
    @NotNull final Duration delay
  ) {
    return switch (context) {
      case SYNC -> this.supplyExceptionallyDelayedSync(callable, delay);
      case ASYNC -> this.supplyExceptionallyDelayedAsync(callable, delay);
    };
  }

  @NotNull
  default Promise<V> supplyExceptionallyDelayedAsync(
    @NotNull final Callable<V> callable,
    final long delayTicks
  ) {
    return this.supplyExceptionallyDelayedAsync(callable, Internal.durationFrom(delayTicks));
  }

  @NotNull
  default Promise<V> supplyExceptionallyDelayedAsync(
    @NotNull final Callable<V> callable,
    final long delay,
    @NotNull final TimeUnit unit
  ) {
    return this.supplyExceptionallyDelayedAsync(callable, Internal.durationFrom(delay, unit));
  }

  @NotNull
  Promise<V> supplyExceptionallyDelayedAsync(
    @NotNull final Callable<V> callable,
    @NotNull final Duration delay
  );

  @NotNull
  default Promise<V> supplyExceptionallyDelayedSync(
    @NotNull final Callable<V> callable,
    final long delayTicks
  ) {
    return this.supplyExceptionallyDelayedSync(callable, Internal.durationFrom(delayTicks));
  }

  @NotNull
  default Promise<V> supplyExceptionallyDelayedSync(
    @NotNull final Callable<V> callable,
    final long delay,
    @NotNull final TimeUnit unit
  ) {
    return this.supplyExceptionallyDelayedSync(callable, Internal.durationFrom(delay, unit));
  }

  @NotNull
  Promise<V> supplyExceptionallyDelayedSync(
    @NotNull final Callable<V> callable,
    @NotNull final Duration delay
  );

  @NotNull
  Promise<V> supplyExceptionallySync(@NotNull Callable<V> callable);

  @NotNull
  Promise<V> supplySync(@NotNull Supplier<V> supplier);

  @NotNull
  default Promise<Void> thenAccept(
    @NotNull final ThreadContext context,
    @NotNull final Consumer<? super V> action
  ) {
    return switch (context) {
      case SYNC -> this.thenAcceptSync(action);
      case ASYNC -> this.thenAcceptAsync(action);
    };
  }

  @NotNull
  default Promise<Void> thenAcceptAsync(@NotNull final Consumer<? super V> action) {
    return this.thenApplyAsync(v -> {
        action.accept(v);
        return null;
      });
  }

  @NotNull
  default Promise<Void> thenAcceptDelayed(
    @NotNull final ThreadContext context,
    @NotNull final Consumer<? super V> action,
    final long delayTicks
  ) {
    return this.thenAcceptDelayed(context, action, Internal.durationFrom(delayTicks));
  }

  @NotNull
  default Promise<Void> thenAcceptDelayed(
    @NotNull final ThreadContext context,
    @NotNull final Consumer<? super V> action,
    final long delay,
    @NotNull final TimeUnit unit
  ) {
    return this.thenAcceptDelayed(context, action, Internal.durationFrom(delay, unit));
  }

  @NotNull
  default Promise<Void> thenAcceptDelayed(
    @NotNull final ThreadContext context,
    @NotNull final Consumer<? super V> action,
    @NotNull final Duration delay
  ) {
    return switch (context) {
      case SYNC -> this.thenAcceptDelayedSync(action, delay);
      case ASYNC -> this.thenAcceptDelayedAsync(action, delay);
    };
  }

  @NotNull
  default Promise<Void> thenAcceptDelayedAsync(
    @NotNull final Consumer<? super V> action,
    final long delayTicks
  ) {
    return this.thenAcceptDelayedAsync(action, Internal.durationFrom(delayTicks));
  }

  @NotNull
  default Promise<Void> thenAcceptDelayedAsync(
    @NotNull final Consumer<? super V> action,
    final long delay,
    @NotNull final TimeUnit unit
  ) {
    return this.thenAcceptDelayedAsync(action, Internal.durationFrom(delay, unit));
  }

  @NotNull
  default Promise<Void> thenAcceptDelayedAsync(
    @NotNull final Consumer<? super V> action,
    @NotNull final Duration delay
  ) {
    return this.thenApplyDelayedAsync(new ConsumerToFunction<>(action), delay);
  }

  @NotNull
  default Promise<Void> thenAcceptDelayedSync(
    @NotNull final Consumer<? super V> action,
    final long delayTicks
  ) {
    return this.thenAcceptDelayedSync(action, Internal.durationFrom(delayTicks));
  }

  @NotNull
  default Promise<Void> thenAcceptDelayedSync(
    @NotNull final Consumer<? super V> action,
    final long delay,
    @NotNull final TimeUnit unit
  ) {
    return this.thenAcceptDelayedSync(action, Internal.durationFrom(delay, unit));
  }

  @NotNull
  default Promise<Void> thenAcceptDelayedSync(
    @NotNull final Consumer<? super V> action,
    @NotNull final Duration delay
  ) {
    return this.thenApplyDelayedSync(new ConsumerToFunction<>(action), delay);
  }

  @NotNull
  default Promise<Void> thenAcceptSync(@NotNull final Consumer<? super V> action) {
    return this.thenApplySync(new ConsumerToFunction<>(action));
  }

  @NotNull
  default <U> Promise<U> thenApply(
    @NotNull final ThreadContext context,
    @NotNull final Function<? super V, ? extends U> function
  ) {
    return switch (context) {
      case SYNC -> this.thenApplySync(function);
      case ASYNC -> this.thenApplyAsync(function);
    };
  }

  @NotNull
  default <U> Promise<U> thenApplyAsync(@NotNull final Function<? super V, ? extends U> function) {
    final var promise = new PromiseImpl<U>();
    this.future()
      .whenComplete((value, t) -> {
        if (t != null) {
          promise.completeExceptionally(t);
        } else {
          Executors.async(new PromiseApply<>(promise, function, value));
        }
      });
    return promise;
  }

  @NotNull
  default <U> Promise<U> thenApplyDelayed(
    @NotNull final ThreadContext context,
    @NotNull final Function<? super V, ? extends U> function,
    final long delayTicks
  ) {
    return this.thenApplyDelayed(context, function, Internal.durationFrom(delayTicks));
  }

  @NotNull
  default <U> Promise<U> thenApplyDelayed(
    @NotNull final ThreadContext context,
    @NotNull final Function<? super V, ? extends U> function,
    final long delay,
    @NotNull final TimeUnit unit
  ) {
    return this.thenApplyDelayed(context, function, Internal.durationFrom(delay, unit));
  }

  @NotNull
  default <U> Promise<U> thenApplyDelayed(
    @NotNull final ThreadContext context,
    @NotNull final Function<? super V, ? extends U> function,
    @NotNull final Duration delay
  ) {
    return switch (context) {
      case SYNC -> this.thenApplyDelayedSync(function, delay);
      case ASYNC -> this.thenApplyDelayedAsync(function, delay);
    };
  }

  @NotNull
  default <U> Promise<U> thenApplyDelayedAsync(
    @NotNull final Function<? super V, ? extends U> function,
    final long delayTicks
  ) {
    return this.thenApplyDelayedAsync(function, Internal.durationFrom(delayTicks));
  }

  @NotNull
  default <U> Promise<U> thenApplyDelayedAsync(
    @NotNull final Function<? super V, ? extends U> function,
    final long delay,
    @NotNull final TimeUnit unit
  ) {
    return this.thenApplyDelayedAsync(function, Internal.durationFrom(delay, unit));
  }

  @NotNull
  default <U> Promise<U> thenApplyDelayedAsync(
    @NotNull final Function<? super V, ? extends U> function,
    @NotNull final Duration delay
  ) {
    final var promise = new PromiseImpl<U>();
    this.future()
      .whenComplete((value, t) -> {
        if (t != null) {
          promise.completeExceptionally(t);
        } else {
          Executors.asyncDelayed(new PromiseApply<>(promise, function, value), delay);
        }
      });
    return promise;
  }

  @NotNull
  default <U> Promise<U> thenApplyDelayedSync(
    @NotNull final Function<? super V, ? extends U> function,
    final long delayTicks
  ) {
    return this.thenApplyDelayedSync(function, Internal.durationFrom(delayTicks));
  }

  @NotNull
  default <U> Promise<U> thenApplyDelayedSync(
    @NotNull final Function<? super V, ? extends U> function,
    final long delay,
    @NotNull final TimeUnit unit
  ) {
    return this.thenApplyDelayedSync(function, Internal.durationFrom(delay, unit));
  }

  @NotNull
  default <U> Promise<U> thenApplyDelayedSync(
    @NotNull final Function<? super V, ? extends U> function,
    @NotNull final Duration delay
  ) {
    final var promise = new PromiseImpl<U>();
    this.future()
      .whenComplete((value, t) -> {
        if (t != null) {
          promise.completeExceptionally(t);
        } else {
          Executors.syncDelayed(new PromiseApply<>(promise, function, value), delay);
        }
      });
    return promise;
  }

  @NotNull
  default <U> Promise<U> thenApplySync(@NotNull final Function<? super V, ? extends U> function) {
    final var promise = new PromiseImpl<U>();
    this.future()
      .whenComplete((value, t) -> {
        if (t != null) {
          promise.completeExceptionally(t);
        } else {
          Executors.sync(new PromiseApply<>(promise, function, value));
        }
      });
    return promise;
  }

  @NotNull
  default <U> Promise<U> thenCompose(
    @NotNull final ThreadContext context,
    @NotNull final Function<? super V, ? extends Promise<U>> function
  ) {
    return switch (context) {
      case SYNC -> this.thenComposeSync(function);
      case ASYNC -> this.thenComposeAsync(function);
    };
  }

  @NotNull
  default <U> Promise<U> thenComposeAsync(
    @NotNull final Function<? super V, ? extends Promise<U>> function
  ) {
    final var promise = new PromiseImpl<U>();
    this.future()
      .whenComplete((value, t) -> {
        if (t != null) {
          promise.completeExceptionally(t);
        } else {
          Executors.async(new PromiseCompose<>(promise, function, value, false));
        }
      });
    return promise;
  }

  @NotNull
  default <U> Promise<U> thenComposeDelayedAsync(
    @NotNull final Function<? super V, ? extends Promise<U>> function,
    final long delayTicks
  ) {
    return this.thenComposeDelayedAsync(function, Internal.durationFrom(delayTicks));
  }

  @NotNull
  default <U> Promise<U> thenComposeDelayedAsync(
    @NotNull final Function<? super V, ? extends Promise<U>> function,
    final long delay,
    @NotNull final TimeUnit unit
  ) {
    return this.thenComposeDelayedAsync(function, Internal.durationFrom(delay, unit));
  }

  @NotNull
  default <U> Promise<U> thenComposeDelayedAsync(
    @NotNull final Function<? super V, ? extends Promise<U>> function,
    @NotNull final Duration delay
  ) {
    final var promise = new PromiseImpl<U>();
    this.future()
      .whenComplete((value, t) -> {
        if (t != null) {
          promise.completeExceptionally(t);
        } else {
          Executors.asyncDelayed(new PromiseCompose<>(promise, function, value, false), delay);
        }
      });
    return promise;
  }

  @NotNull
  default <U> Promise<U> thenComposeDelayedSync(
    @NotNull final ThreadContext context,
    @NotNull final Function<? super V, ? extends Promise<U>> function,
    final long delayTicks
  ) {
    return this.thenComposeDelayedSync(context, function, Internal.durationFrom(delayTicks));
  }

  @NotNull
  default <U> Promise<U> thenComposeDelayedSync(
    @NotNull final ThreadContext context,
    @NotNull final Function<? super V, ? extends Promise<U>> function,
    final long delay,
    @NotNull final TimeUnit unit
  ) {
    return this.thenComposeDelayedSync(context, function, Internal.durationFrom(delay, unit));
  }

  @NotNull
  default <U> Promise<U> thenComposeDelayedSync(
    @NotNull final ThreadContext context,
    @NotNull final Function<? super V, ? extends Promise<U>> function,
    @NotNull final Duration delay
  ) {
    return switch (context) {
      case SYNC -> this.thenComposeDelayedSync(function, delay);
      case ASYNC -> this.thenComposeDelayedAsync(function, delay);
    };
  }

  @NotNull
  default <U> Promise<U> thenComposeDelayedSync(
    @NotNull final Function<? super V, ? extends Promise<U>> function,
    final long delayTicks
  ) {
    return this.thenComposeDelayedSync(function, Internal.durationFrom(delayTicks));
  }

  @NotNull
  default <U> Promise<U> thenComposeDelayedSync(
    @NotNull final Function<? super V, ? extends Promise<U>> function,
    final long delay,
    @NotNull final TimeUnit unit
  ) {
    return this.thenComposeDelayedSync(function, Internal.durationFrom(delay, unit));
  }

  @NotNull
  default <U> Promise<U> thenComposeDelayedSync(
    @NotNull final Function<? super V, ? extends Promise<U>> function,
    @NotNull final Duration delay
  ) {
    final var promise = new PromiseImpl<U>();
    this.future()
      .whenComplete((value, t) -> {
        if (t != null) {
          promise.completeExceptionally(t);
        } else {
          Executors.syncDelayed(new PromiseCompose<>(promise, function, value, true), delay);
        }
      });
    return promise;
  }

  @NotNull
  default <U> Promise<U> thenComposeSync(
    @NotNull final Function<? super V, ? extends Promise<U>> function
  ) {
    final var promise = new PromiseImpl<U>();
    this.future()
      .whenComplete((value, t) -> {
        if (t != null) {
          promise.completeExceptionally(t);
        } else {
          Executors.sync(new PromiseCompose<>(promise, function, value, true));
        }
      });
    return promise;
  }

  @NotNull
  default Promise<Void> thenRun(
    @NotNull final ThreadContext context,
    @NotNull final Runnable action
  ) {
    return switch (context) {
      case SYNC -> this.thenRunSync(action);
      case ASYNC -> this.thenRunAsync(action);
    };
  }

  @NotNull
  default Promise<Void> thenRunAsync(@NotNull final Runnable action) {
    return this.thenApplyAsync(new RunnableToFunction<>(action));
  }

  @NotNull
  default Promise<Void> thenRunDelayed(
    @NotNull final ThreadContext context,
    @NotNull final Runnable action,
    final long delayTicks
  ) {
    return this.thenRunDelayed(context, action, Internal.durationFrom(delayTicks));
  }

  @NotNull
  default Promise<Void> thenRunDelayed(
    @NotNull final ThreadContext context,
    @NotNull final Runnable action,
    final long delay,
    @NotNull final TimeUnit unit
  ) {
    return this.thenRunDelayed(context, action, Internal.durationFrom(delay, unit));
  }

  @NotNull
  default Promise<Void> thenRunDelayed(
    @NotNull final ThreadContext context,
    @NotNull final Runnable action,
    @NotNull final Duration delay
  ) {
    return switch (context) {
      case SYNC -> this.thenRunDelayedSync(action, delay);
      case ASYNC -> this.thenRunDelayedAsync(action, delay);
    };
  }

  @NotNull
  default Promise<Void> thenRunDelayedAsync(@NotNull final Runnable action, final long delayTicks) {
    return this.thenRunDelayedAsync(action, Internal.durationFrom(delayTicks));
  }

  @NotNull
  default Promise<Void> thenRunDelayedAsync(
    @NotNull final Runnable action,
    final long delay,
    @NotNull final TimeUnit unit
  ) {
    return this.thenRunDelayedAsync(action, Internal.durationFrom(delay, unit));
  }

  @NotNull
  default Promise<Void> thenRunDelayedAsync(
    @NotNull final Runnable action,
    @NotNull final Duration delay
  ) {
    return this.thenApplyDelayedAsync(new RunnableToFunction<>(action), delay);
  }

  @NotNull
  default Promise<Void> thenRunDelayedSync(@NotNull final Runnable action, final long delayTicks) {
    return this.thenRunDelayedSync(action, Internal.durationFrom(delayTicks));
  }

  @NotNull
  default Promise<Void> thenRunDelayedSync(
    @NotNull final Runnable action,
    final long delay,
    @NotNull final TimeUnit unit
  ) {
    return this.thenRunDelayedSync(action, Internal.durationFrom(delay, unit));
  }

  @NotNull
  default Promise<Void> thenRunDelayedSync(
    @NotNull final Runnable action,
    @NotNull final Duration delay
  ) {
    return this.thenApplyDelayedSync(new RunnableToFunction<>(action), delay);
  }

  @NotNull
  default Promise<Void> thenRunSync(@NotNull final Runnable action) {
    return this.thenApplySync(new RunnableToFunction<>(action));
  }
}
