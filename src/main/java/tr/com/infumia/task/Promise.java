package tr.com.infumia.task;

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

@SuppressWarnings("unchecked")
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
    return Promise.<U>empty().supplyDelayed(context, supplier, delayTicks);
  }

  @NotNull
  static <U> Promise<U> supplyingDelayed(
    @NotNull final ThreadContext context,
    @NotNull final Supplier<U> supplier,
    final long delay,
    @NotNull final TimeUnit unit
  ) {
    return Promise.<U>empty().supplyDelayed(context, supplier, delay, unit);
  }

  @NotNull
  static <U> Promise<U> supplyingDelayedAsync(
    @NotNull final Supplier<U> supplier,
    final long delayTicks
  ) {
    return Promise.<U>empty().supplyDelayedAsync(supplier, delayTicks);
  }

  @NotNull
  static <U> Promise<U> supplyingDelayedAsync(
    @NotNull final Supplier<U> supplier,
    final long delay,
    @NotNull final TimeUnit unit
  ) {
    return Promise.<U>empty().supplyDelayedAsync(supplier, delay, unit);
  }

  @NotNull
  static <U> Promise<U> supplyingDelayedSync(
    @NotNull final Supplier<U> supplier,
    final long delayTicks
  ) {
    return Promise.<U>empty().supplyDelayedSync(supplier, delayTicks);
  }

  @NotNull
  static <U> Promise<U> supplyingDelayedSync(
    @NotNull final Supplier<U> supplier,
    final long delay,
    @NotNull final TimeUnit unit
  ) {
    return Promise.<U>empty().supplyDelayedSync(supplier, delay, unit);
  }

  @NotNull
  static <U> Promise<U> supplyingExceptionally(
    @NotNull final ThreadContext context,
    @NotNull final Callable<U> callable
  ) {
    return Promise.<U>empty().supplyExceptionally(context, callable);
  }

  @NotNull
  static <U> Promise<U> supplyingExceptionallyAsync(
    @NotNull final Callable<U> callable
  ) {
    return Promise.<U>empty().supplyExceptionallyAsync(callable);
  }

  @NotNull
  static <U> Promise<U> supplyingExceptionallyDelayed(
    @NotNull final ThreadContext context,
    @NotNull final Callable<U> callable,
    final long delayTicks
  ) {
    return Promise
      .<U>empty()
      .supplyExceptionallyDelayed(context, callable, delayTicks);
  }

  @NotNull
  static <U> Promise<U> supplyingExceptionallyDelayed(
    @NotNull final ThreadContext context,
    @NotNull final Callable<U> callable,
    final long delay,
    @NotNull final TimeUnit unit
  ) {
    return Promise
      .<U>empty()
      .supplyExceptionallyDelayed(context, callable, delay, unit);
  }

  @NotNull
  static <U> Promise<U> supplyingExceptionallyDelayedAsync(
    @NotNull final Callable<U> callable,
    final long delayTicks
  ) {
    return Promise
      .<U>empty()
      .supplyExceptionallyDelayedAsync(callable, delayTicks);
  }

  @NotNull
  static <U> Promise<U> supplyingExceptionallyDelayedAsync(
    @NotNull final Callable<U> callable,
    final long delay,
    @NotNull final TimeUnit unit
  ) {
    return Promise
      .<U>empty()
      .supplyExceptionallyDelayedAsync(callable, delay, unit);
  }

  @NotNull
  static <U> Promise<U> supplyingExceptionallyDelayedSync(
    @NotNull final Callable<U> callable,
    final long delayTicks
  ) {
    return Promise
      .<U>empty()
      .supplyExceptionallyDelayedSync(callable, delayTicks);
  }

  @NotNull
  static <U> Promise<U> supplyingExceptionallyDelayedSync(
    @NotNull final Callable<U> callable,
    final long delay,
    @NotNull final TimeUnit unit
  ) {
    return Promise
      .<U>empty()
      .supplyExceptionallyDelayedSync(callable, delay, unit);
  }

  @NotNull
  static <U> Promise<U> supplyingExceptionallySync(
    @NotNull final Callable<U> callable
  ) {
    return Promise.<U>empty().supplyExceptionallySync(callable);
  }

  @NotNull
  static <U> Promise<U> supplyingSync(@NotNull final Supplier<U> supplier) {
    return Promise.<U>empty().supplySync(supplier);
  }

  @NotNull
  static <U> Promise<U> wrapFuture(@NotNull final Future<U> future) {
    if (future instanceof CompletableFuture<?>) {
      return new PromiseImpl<>(
        ((CompletableFuture<U>) future).thenApply(Function.identity())
      );
    }
    if (future instanceof CompletionStage<?>) {
      return new PromiseImpl<>(
        ((CompletionStage<U>) future).toCompletableFuture()
          .thenApply(Function.identity())
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
  Promise<V> exceptionallyAsync(
    @NotNull Function<Throwable, ? extends V> function
  );

  @NotNull
  default Promise<V> exceptionallyDelayed(
    @NotNull final ThreadContext context,
    @NotNull final Function<Throwable, ? extends V> function,
    final long delayTicks
  ) {
    return switch (context) {
      case SYNC -> this.exceptionallyDelayedSync(function, delayTicks);
      case ASYNC -> this.exceptionallyDelayedAsync(function, delayTicks);
    };
  }

  @NotNull
  default Promise<V> exceptionallyDelayed(
    @NotNull final ThreadContext context,
    @NotNull final Function<Throwable, ? extends V> function,
    final long delay,
    @NotNull final TimeUnit unit
  ) {
    return switch (context) {
      case SYNC -> this.exceptionallyDelayedSync(function, delay, unit);
      case ASYNC -> this.exceptionallyDelayedAsync(function, delay, unit);
    };
  }

  @NotNull
  Promise<V> exceptionallyDelayedAsync(
    @NotNull Function<Throwable, ? extends V> function,
    long delayTicks
  );

  @NotNull
  Promise<V> exceptionallyDelayedAsync(
    @NotNull Function<Throwable, ? extends V> function,
    long delay,
    @NotNull TimeUnit unit
  );

  @NotNull
  Promise<V> exceptionallyDelayedSync(
    @NotNull Function<Throwable, ? extends V> function,
    long delayTicks
  );

  @NotNull
  Promise<V> exceptionallyDelayedSync(
    @NotNull Function<Throwable, ? extends V> function,
    long delay,
    @NotNull TimeUnit unit
  );

  @NotNull
  Promise<V> exceptionallySync(
    @NotNull Function<Throwable, ? extends V> function
  );

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
    return switch (context) {
      case SYNC -> this.supplyDelayedSync(supplier, delayTicks);
      case ASYNC -> this.supplyDelayedAsync(supplier, delayTicks);
    };
  }

  @NotNull
  default Promise<V> supplyDelayed(
    @NotNull final ThreadContext context,
    @NotNull final Supplier<V> supplier,
    final long delay,
    @NotNull final TimeUnit unit
  ) {
    return switch (context) {
      case SYNC -> this.supplyDelayedSync(supplier, delay, unit);
      case ASYNC -> this.supplyDelayedAsync(supplier, delay, unit);
    };
  }

  @NotNull
  Promise<V> supplyDelayedAsync(@NotNull Supplier<V> supplier, long delayTicks);

  @NotNull
  Promise<V> supplyDelayedAsync(
    @NotNull Supplier<V> supplier,
    long delay,
    @NotNull TimeUnit unit
  );

  @NotNull
  Promise<V> supplyDelayedSync(@NotNull Supplier<V> supplier, long delayTicks);

  @NotNull
  Promise<V> supplyDelayedSync(
    @NotNull Supplier<V> supplier,
    long delay,
    @NotNull TimeUnit unit
  );

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
    return switch (context) {
      case SYNC -> this.supplyExceptionallyDelayedSync(callable, delayTicks);
      case ASYNC -> this.supplyExceptionallyDelayedAsync(callable, delayTicks);
    };
  }

  @NotNull
  default Promise<V> supplyExceptionallyDelayed(
    @NotNull final ThreadContext context,
    @NotNull final Callable<V> callable,
    final long delay,
    @NotNull final TimeUnit unit
  ) {
    return switch (context) {
      case SYNC -> this.supplyExceptionallyDelayedSync(callable, delay, unit);
      case ASYNC -> this.supplyExceptionallyDelayedAsync(callable, delay, unit);
    };
  }

  @NotNull
  Promise<V> supplyExceptionallyDelayedAsync(
    @NotNull Callable<V> callable,
    long delayTicks
  );

  @NotNull
  Promise<V> supplyExceptionallyDelayedAsync(
    @NotNull Callable<V> callable,
    long delay,
    @NotNull TimeUnit unit
  );

  @NotNull
  Promise<V> supplyExceptionallyDelayedSync(
    @NotNull Callable<V> callable,
    long delayTicks
  );

  @NotNull
  Promise<V> supplyExceptionallyDelayedSync(
    @NotNull Callable<V> callable,
    long delay,
    @NotNull TimeUnit unit
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
  default Promise<Void> thenAcceptAsync(
    @NotNull final Consumer<? super V> action
  ) {
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
    return switch (context) {
      case SYNC -> this.thenAcceptDelayedSync(action, delayTicks);
      case ASYNC -> this.thenAcceptDelayedAsync(action, delayTicks);
    };
  }

  @NotNull
  default Promise<Void> thenAcceptDelayed(
    @NotNull final ThreadContext context,
    @NotNull final Consumer<? super V> action,
    final long delay,
    @NotNull final TimeUnit unit
  ) {
    return switch (context) {
      case SYNC -> this.thenAcceptDelayedSync(action, delay, unit);
      case ASYNC -> this.thenAcceptDelayedAsync(action, delay, unit);
    };
  }

  @NotNull
  default Promise<Void> thenAcceptDelayedAsync(
    @NotNull final Consumer<? super V> action,
    final long delayTicks
  ) {
    return this.thenApplyDelayedAsync(
      v -> {
        action.accept(v);
        return null;
      },
      delayTicks
    );
  }

  @NotNull
  default Promise<Void> thenAcceptDelayedAsync(
    @NotNull final Consumer<? super V> action,
    final long delay,
    @NotNull final TimeUnit unit
  ) {
    return this.thenApplyDelayedAsync(
      v -> {
        action.accept(v);
        return null;
      },
      delay,
      unit
    );
  }

  @NotNull
  default Promise<Void> thenAcceptDelayedSync(
    @NotNull final Consumer<? super V> action,
    final long delayTicks
  ) {
    return this.thenApplyDelayedSync(
      v -> {
        action.accept(v);
        return null;
      },
      delayTicks
    );
  }

  @NotNull
  default Promise<Void> thenAcceptDelayedSync(
    @NotNull final Consumer<? super V> action,
    final long delay,
    @NotNull final TimeUnit unit
  ) {
    return this.thenApplyDelayedSync(
      v -> {
        action.accept(v);
        return null;
      },
      delay,
      unit
    );
  }

  @NotNull
  default Promise<Void> thenAcceptSync(
    @NotNull final Consumer<? super V> action
  ) {
    return this.thenApplySync(v -> {
      action.accept(v);
      return null;
    });
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

  @NotNull <U> Promise<U> thenApplyAsync(
    @NotNull Function<? super V, ? extends U> function
  );

  @NotNull
  default <U> Promise<U> thenApplyDelayed(
    @NotNull final ThreadContext context,
    @NotNull final Function<? super V, ? extends U> function,
    final long delayTicks
  ) {
    return switch (context) {
      case SYNC -> this.thenApplyDelayedSync(function, delayTicks);
      case ASYNC -> this.thenApplyDelayedAsync(function, delayTicks);
    };
  }

  @NotNull
  default <U> Promise<U> thenApplyDelayed(
    @NotNull final ThreadContext context,
    @NotNull final Function<? super V, ? extends U> function,
    final long delay,
    @NotNull final TimeUnit unit
  ) {
    return switch (context) {
      case SYNC -> this.thenApplyDelayedSync(function, delay, unit);
      case ASYNC -> this.thenApplyDelayedAsync(function, delay, unit);
    };
  }

  @NotNull <U> Promise<U> thenApplyDelayedAsync(
    @NotNull Function<? super V, ? extends U> function,
    long delayTicks
  );

  @NotNull <U> Promise<U> thenApplyDelayedAsync(
    @NotNull Function<? super V, ? extends U> function,
    long delay,
    @NotNull TimeUnit unit
  );

  @NotNull <U> Promise<U> thenApplyDelayedSync(
    @NotNull Function<? super V, ? extends U> function,
    long delayTicks
  );

  @NotNull <U> Promise<U> thenApplyDelayedSync(
    @NotNull Function<? super V, ? extends U> function,
    long delay,
    @NotNull TimeUnit unit
  );

  @NotNull <U> Promise<U> thenApplySync(
    @NotNull Function<? super V, ? extends U> function
  );

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

  @NotNull <U> Promise<U> thenComposeAsync(
    @NotNull Function<? super V, ? extends Promise<U>> function
  );

  @NotNull <U> Promise<U> thenComposeDelayedAsync(
    @NotNull Function<? super V, ? extends Promise<U>> function,
    long delayTicks
  );

  @NotNull <U> Promise<U> thenComposeDelayedAsync(
    @NotNull Function<? super V, ? extends Promise<U>> function,
    long delay,
    @NotNull TimeUnit unit
  );

  @NotNull
  default <U> Promise<U> thenComposeDelayedSync(
    @NotNull final ThreadContext context,
    @NotNull final Function<? super V, ? extends Promise<U>> function,
    final long delayTicks
  ) {
    return switch (context) {
      case SYNC -> this.thenComposeDelayedSync(function, delayTicks);
      case ASYNC -> this.thenComposeDelayedAsync(function, delayTicks);
    };
  }

  @NotNull
  default <U> Promise<U> thenComposeDelayedSync(
    @NotNull final ThreadContext context,
    @NotNull final Function<? super V, ? extends Promise<U>> function,
    final long delay,
    @NotNull final TimeUnit unit
  ) {
    return switch (context) {
      case SYNC -> this.thenComposeDelayedSync(function, delay, unit);
      case ASYNC -> this.thenComposeDelayedAsync(function, delay, unit);
    };
  }

  @NotNull <U> Promise<U> thenComposeDelayedSync(
    @NotNull Function<? super V, ? extends Promise<U>> function,
    long delayTicks
  );

  @NotNull <U> Promise<U> thenComposeDelayedSync(
    @NotNull Function<? super V, ? extends Promise<U>> function,
    long delay,
    @NotNull TimeUnit unit
  );

  @NotNull <U> Promise<U> thenComposeSync(
    @NotNull Function<? super V, ? extends Promise<U>> function
  );

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
    return this.thenApplyAsync(v -> {
      action.run();
      return null;
    });
  }

  @NotNull
  default Promise<Void> thenRunDelayed(
    @NotNull final ThreadContext context,
    @NotNull final Runnable action,
    final long delayTicks
  ) {
    return switch (context) {
      case SYNC -> this.thenRunDelayedSync(action, delayTicks);
      case ASYNC -> this.thenRunDelayedAsync(action, delayTicks);
    };
  }

  @NotNull
  default Promise<Void> thenRunDelayed(
    @NotNull final ThreadContext context,
    @NotNull final Runnable action,
    final long delay,
    @NotNull final TimeUnit unit
  ) {
    return switch (context) {
      case SYNC -> this.thenRunDelayedSync(action, delay, unit);
      case ASYNC -> this.thenRunDelayedAsync(action, delay, unit);
    };
  }

  @NotNull
  default Promise<Void> thenRunDelayedAsync(
    @NotNull final Runnable action,
    final long delayTicks
  ) {
    return this.thenApplyDelayedAsync(
      v -> {
        action.run();
        return null;
      },
      delayTicks
    );
  }

  @NotNull
  default Promise<Void> thenRunDelayedAsync(
    @NotNull final Runnable action,
    final long delay,
    @NotNull final TimeUnit unit
  ) {
    return this.thenApplyDelayedAsync(
      v -> {
        action.run();
        return null;
      },
      delay,
      unit
    );
  }

  @NotNull
  default Promise<Void> thenRunDelayedSync(
    @NotNull final Runnable action,
    final long delayTicks
  ) {
    return this.thenApplyDelayedSync(
      v -> {
        action.run();
        return null;
      },
      delayTicks
    );
  }

  @NotNull
  default Promise<Void> thenRunDelayedSync(
    @NotNull final Runnable action,
    final long delay,
    @NotNull final TimeUnit unit
  ) {
    return this.thenApplyDelayedSync(
      v -> {
        action.run();
        return null;
      },
      delay,
      unit
    );
  }

  @NotNull
  default Promise<Void> thenRunSync(@NotNull final Runnable action) {
    return this.thenApplySync(v -> {
      action.run();
      return null;
    });
  }
}
