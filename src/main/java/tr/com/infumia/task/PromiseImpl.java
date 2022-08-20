package tr.com.infumia.task;

import com.google.common.base.Preconditions;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.function.Supplier;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
final class PromiseImpl<V> implements Promise<V> {

  AtomicBoolean cancelled = new AtomicBoolean(false);

  @Getter
  @NotNull
  CompletableFuture<V> future;

  AtomicBoolean supplied = new AtomicBoolean(false);

  PromiseImpl() {
    this.future = new CompletableFuture<>();
  }

  PromiseImpl(@Nullable final V value) {
    this.future = CompletableFuture.completedFuture(value);
    this.supplied.set(true);
  }

  PromiseImpl(@NotNull final Throwable throwable) {
    this();
    this.future.completeExceptionally(throwable);
    this.supplied.set(true);
  }

  PromiseImpl(@NotNull final CompletableFuture<V> future) {
    this.future = future;
    this.supplied.set(true);
    this.cancelled.set(future.isCancelled());
  }

  @Override
  public boolean cancel(final boolean mayInterruptIfRunning) {
    this.cancelled.set(true);
    return this.future().cancel(mayInterruptIfRunning);
  }

  @NotNull
  @Override
  public PromiseImpl<V> exceptionallyAsync(
    @NotNull final Function<Throwable, ? extends V> function
  ) {
    final var promise = new PromiseImpl<V>();
    this.future()
      .whenComplete((value, t) -> {
        if (t == null) {
          promise.complete(value);
        } else {
          this.executeAsync(new ExceptionallyRunnable<>(promise, function, t));
        }
      });
    return promise;
  }

  @NotNull
  @Override
  public PromiseImpl<V> exceptionallyDelayedAsync(
    @NotNull final Function<Throwable, ? extends V> function,
    final long delayTicks
  ) {
    final var promise = new PromiseImpl<V>();
    this.future()
      .whenComplete((value, t) -> {
        if (t == null) {
          promise.complete(value);
        } else {
          this.executeDelayedAsync(
              new ExceptionallyRunnable<>(promise, function, t),
              delayTicks
            );
        }
      });
    return promise;
  }

  @NotNull
  @Override
  public PromiseImpl<V> exceptionallyDelayedAsync(
    @NotNull final Function<Throwable, ? extends V> function,
    final long delay,
    @NotNull final TimeUnit unit
  ) {
    final var promise = new PromiseImpl<V>();
    this.future()
      .whenComplete((value, t) -> {
        if (t == null) {
          promise.complete(value);
        } else {
          this.executeDelayedAsync(
              new ExceptionallyRunnable<>(promise, function, t),
              delay,
              unit
            );
        }
      });
    return promise;
  }

  @NotNull
  @Override
  public PromiseImpl<V> exceptionallyDelayedSync(
    @NotNull final Function<Throwable, ? extends V> function,
    final long delayTicks
  ) {
    final var promise = new PromiseImpl<V>();
    this.future()
      .whenComplete((value, t) -> {
        if (t == null) {
          promise.complete(value);
        } else {
          this.executeDelayedSync(
              new ExceptionallyRunnable<>(promise, function, t),
              delayTicks
            );
        }
      });
    return promise;
  }

  @NotNull
  @Override
  public PromiseImpl<V> exceptionallyDelayedSync(
    @NotNull final Function<Throwable, ? extends V> function,
    final long delay,
    @NotNull final TimeUnit unit
  ) {
    final var promise = new PromiseImpl<V>();
    this.future()
      .whenComplete((value, t) -> {
        if (t == null) {
          promise.complete(value);
        } else {
          this.executeDelayedSync(
              new ExceptionallyRunnable<>(promise, function, t),
              delay,
              unit
            );
        }
      });
    return promise;
  }

  @NotNull
  @Override
  public PromiseImpl<V> exceptionallySync(
    @NotNull final Function<Throwable, ? extends V> function
  ) {
    final var promise = new PromiseImpl<V>();
    this.future()
      .whenComplete((value, t) -> {
        if (t == null) {
          promise.complete(value);
        } else {
          this.executeSync(new ExceptionallyRunnable<>(promise, function, t));
        }
      });
    return promise;
  }

  @NotNull
  @Override
  public PromiseImpl<V> supply(@Nullable final V value) {
    this.markAsSupplied();
    this.complete(value);
    return this;
  }

  @NotNull
  @Override
  public PromiseImpl<V> supplyAsync(@NotNull final Supplier<V> supplier) {
    this.markAsSupplied();
    this.executeAsync(new SupplyRunnable<>(this, supplier));
    return this;
  }

  @NotNull
  @Override
  public PromiseImpl<V> supplyDelayedAsync(
    @NotNull final Supplier<V> supplier,
    final long delayTicks
  ) {
    this.markAsSupplied();
    this.executeDelayedAsync(new SupplyRunnable<>(this, supplier), delayTicks);
    return this;
  }

  @NotNull
  @Override
  public PromiseImpl<V> supplyDelayedAsync(
    @NotNull final Supplier<V> supplier,
    final long delay,
    @NotNull final TimeUnit unit
  ) {
    this.markAsSupplied();
    this.executeDelayedAsync(new SupplyRunnable<>(this, supplier), delay, unit);
    return this;
  }

  @NotNull
  @Override
  public PromiseImpl<V> supplyDelayedSync(
    @NotNull final Supplier<V> supplier,
    final long delayTicks
  ) {
    this.markAsSupplied();
    this.executeDelayedSync(new SupplyRunnable<>(this, supplier), delayTicks);
    return this;
  }

  @NotNull
  @Override
  public PromiseImpl<V> supplyDelayedSync(
    @NotNull final Supplier<V> supplier,
    final long delay,
    @NotNull final TimeUnit unit
  ) {
    this.markAsSupplied();
    this.executeDelayedSync(new SupplyRunnable<>(this, supplier), delay, unit);
    return this;
  }

  @NotNull
  @Override
  public PromiseImpl<V> supplyException(@NotNull final Throwable exception) {
    this.markAsSupplied();
    this.completeExceptionally(exception);
    return this;
  }

  @NotNull
  @Override
  public PromiseImpl<V> supplyExceptionallyAsync(
    @NotNull final Callable<V> callable
  ) {
    this.markAsSupplied();
    this.executeAsync(new ThrowingSupplyRunnable<>(this, callable));
    return this;
  }

  @NotNull
  @Override
  public PromiseImpl<V> supplyExceptionallyDelayedAsync(
    @NotNull final Callable<V> callable,
    final long delayTicks
  ) {
    this.markAsSupplied();
    this.executeDelayedAsync(
        new ThrowingSupplyRunnable<>(this, callable),
        delayTicks
      );
    return this;
  }

  @NotNull
  @Override
  public PromiseImpl<V> supplyExceptionallyDelayedAsync(
    @NotNull final Callable<V> callable,
    final long delay,
    @NotNull final TimeUnit unit
  ) {
    this.markAsSupplied();
    this.executeDelayedAsync(
        new ThrowingSupplyRunnable<>(this, callable),
        delay,
        unit
      );
    return this;
  }

  @NotNull
  @Override
  public PromiseImpl<V> supplyExceptionallyDelayedSync(
    @NotNull final Callable<V> callable,
    final long delayTicks
  ) {
    this.markAsSupplied();
    this.executeDelayedSync(
        new ThrowingSupplyRunnable<>(this, callable),
        delayTicks
      );
    return this;
  }

  @NotNull
  @Override
  public PromiseImpl<V> supplyExceptionallyDelayedSync(
    @NotNull final Callable<V> callable,
    final long delay,
    @NotNull final TimeUnit unit
  ) {
    this.markAsSupplied();
    this.executeDelayedSync(
        new ThrowingSupplyRunnable<>(this, callable),
        delay,
        unit
      );
    return this;
  }

  @NotNull
  @Override
  public PromiseImpl<V> supplyExceptionallySync(
    @NotNull final Callable<V> callable
  ) {
    this.markAsSupplied();
    this.executeSync(new ThrowingSupplyRunnable<>(this, callable));
    return this;
  }

  @NotNull
  @Override
  public PromiseImpl<V> supplySync(@NotNull final Supplier<V> supplier) {
    this.markAsSupplied();
    this.executeSync(new SupplyRunnable<>(this, supplier));
    return this;
  }

  @NotNull
  @Override
  public <U> Promise<U> thenApplyAsync(
    @NotNull final Function<? super V, ? extends U> function
  ) {
    final var promise = new PromiseImpl<U>();
    this.future()
      .whenComplete((value, t) -> {
        if (t != null) {
          promise.completeExceptionally(t);
        } else {
          this.executeAsync(new ApplyRunnable<>(promise, function, value));
        }
      });
    return promise;
  }

  @NotNull
  @Override
  public <U> Promise<U> thenApplyDelayedAsync(
    @NotNull final Function<? super V, ? extends U> function,
    final long delayTicks
  ) {
    final var promise = new PromiseImpl<U>();
    this.future()
      .whenComplete((value, t) -> {
        if (t != null) {
          promise.completeExceptionally(t);
        } else {
          this.executeDelayedAsync(
              new ApplyRunnable<>(promise, function, value),
              delayTicks
            );
        }
      });
    return promise;
  }

  @NotNull
  @Override
  public <U> Promise<U> thenApplyDelayedAsync(
    @NotNull final Function<? super V, ? extends U> function,
    final long delay,
    @NotNull final TimeUnit unit
  ) {
    final var promise = new PromiseImpl<U>();
    this.future()
      .whenComplete((value, t) -> {
        if (t != null) {
          promise.completeExceptionally(t);
        } else {
          this.executeDelayedAsync(
              new ApplyRunnable<>(promise, function, value),
              delay,
              unit
            );
        }
      });
    return promise;
  }

  @NotNull
  @Override
  public <U> Promise<U> thenApplyDelayedSync(
    @NotNull final Function<? super V, ? extends U> function,
    final long delayTicks
  ) {
    final var promise = new PromiseImpl<U>();
    this.future()
      .whenComplete((value, t) -> {
        if (t != null) {
          promise.completeExceptionally(t);
        } else {
          this.executeDelayedSync(
              new ApplyRunnable<>(promise, function, value),
              delayTicks
            );
        }
      });
    return promise;
  }

  @NotNull
  @Override
  public <U> Promise<U> thenApplyDelayedSync(
    @NotNull final Function<? super V, ? extends U> function,
    final long delay,
    @NotNull final TimeUnit unit
  ) {
    final var promise = new PromiseImpl<U>();
    this.future()
      .whenComplete((value, t) -> {
        if (t != null) {
          promise.completeExceptionally(t);
        } else {
          this.executeDelayedSync(
              new ApplyRunnable<>(promise, function, value),
              delay,
              unit
            );
        }
      });
    return promise;
  }

  @NotNull
  @Override
  public <U> Promise<U> thenApplySync(
    @NotNull final Function<? super V, ? extends U> function
  ) {
    final var promise = new PromiseImpl<U>();
    this.future()
      .whenComplete((value, t) -> {
        if (t != null) {
          promise.completeExceptionally(t);
        } else {
          this.executeSync(new ApplyRunnable<>(promise, function, value));
        }
      });
    return promise;
  }

  @NotNull
  @Override
  public <U> Promise<U> thenComposeAsync(
    @NotNull final Function<? super V, ? extends Promise<U>> function
  ) {
    final var promise = new PromiseImpl<U>();
    this.future()
      .whenComplete((value, t) -> {
        if (t != null) {
          promise.completeExceptionally(t);
        } else {
          this.executeAsync(
              new ComposeRunnable<>(promise, function, value, false)
            );
        }
      });
    return promise;
  }

  @NotNull
  @Override
  public <U> Promise<U> thenComposeDelayedAsync(
    @NotNull final Function<? super V, ? extends Promise<U>> function,
    final long delayTicks
  ) {
    final var promise = new PromiseImpl<U>();
    this.future()
      .whenComplete((value, t) -> {
        if (t != null) {
          promise.completeExceptionally(t);
        } else {
          this.executeDelayedAsync(
              new ComposeRunnable<>(promise, function, value, false),
              delayTicks
            );
        }
      });
    return promise;
  }

  @NotNull
  @Override
  public <U> Promise<U> thenComposeDelayedAsync(
    @NotNull final Function<? super V, ? extends Promise<U>> function,
    final long delay,
    @NotNull final TimeUnit unit
  ) {
    final var promise = new PromiseImpl<U>();
    this.future()
      .whenComplete((value, t) -> {
        if (t != null) {
          promise.completeExceptionally(t);
        } else {
          this.executeDelayedAsync(
              new ComposeRunnable<>(promise, function, value, false),
              delay,
              unit
            );
        }
      });
    return promise;
  }

  @NotNull
  @Override
  public <U> Promise<U> thenComposeDelayedSync(
    @NotNull final Function<? super V, ? extends Promise<U>> function,
    final long delayTicks
  ) {
    final var promise = new PromiseImpl<U>();
    this.future()
      .whenComplete((value, t) -> {
        if (t != null) {
          promise.completeExceptionally(t);
        } else {
          this.executeDelayedSync(
              new ComposeRunnable<>(promise, function, value, true),
              delayTicks
            );
        }
      });
    return promise;
  }

  @NotNull
  @Override
  public <U> Promise<U> thenComposeDelayedSync(
    @NotNull final Function<? super V, ? extends Promise<U>> function,
    final long delay,
    @NotNull final TimeUnit unit
  ) {
    final var promise = new PromiseImpl<U>();
    this.future()
      .whenComplete((value, t) -> {
        if (t != null) {
          promise.completeExceptionally(t);
        } else {
          this.executeDelayedSync(
              new ComposeRunnable<>(promise, function, value, true),
              delay,
              unit
            );
        }
      });
    return promise;
  }

  @NotNull
  @Override
  public <U> Promise<U> thenComposeSync(
    @NotNull final Function<? super V, ? extends Promise<U>> function
  ) {
    final var promise = new PromiseImpl<U>();
    this.future()
      .whenComplete((value, t) -> {
        if (t != null) {
          promise.completeExceptionally(t);
        } else {
          this.executeSync(
              new ComposeRunnable<>(promise, function, value, true)
            );
        }
      });
    return promise;
  }

  private void complete(@Nullable final V value) {
    if (!this.cancelled.get()) {
      this.future().complete(value);
    }
  }

  private void completeExceptionally(@NotNull final Throwable ex) {
    if (!this.cancelled.get()) {
      this.future().completeExceptionally(ex);
    }
  }

  private void executeAsync(@NotNull final Runnable runnable) {
    Internal.async().execute(runnable);
  }

  private void executeDelayedAsync(
    @NotNull final Runnable runnable,
    final long delay,
    @NotNull final TimeUnit unit
  ) {
    if (delay <= 0) {
      this.executeAsync(runnable);
    } else {
      Internal
        .async()
        .schedule(Internal.wrapSchedulerTask(runnable), delay, unit);
    }
  }

  private void executeDelayedAsync(
    @NotNull final Runnable runnable,
    final long delayTicks
  ) {
    if (delayTicks <= 0) {
      this.executeAsync(runnable);
    } else {
      Bukkit
        .getScheduler()
        .runTaskLaterAsynchronously(
          Internal.plugin(),
          Internal.wrapSchedulerTask(runnable),
          delayTicks
        );
    }
  }

  private void executeDelayedSync(
    @NotNull final Runnable runnable,
    final long delay,
    @NotNull final TimeUnit unit
  ) {
    if (delay <= 0) {
      this.executeSync(runnable);
    } else {
      Bukkit
        .getScheduler()
        .runTaskLater(
          Internal.plugin(),
          Internal.wrapSchedulerTask(runnable),
          Internal.ticksFrom(delay, unit)
        );
    }
  }

  private void executeDelayedSync(
    @NotNull final Runnable runnable,
    final long delayTicks
  ) {
    if (delayTicks <= 0) {
      this.executeSync(runnable);
    } else {
      Bukkit
        .getScheduler()
        .runTaskLater(
          Internal.plugin(),
          Internal.wrapSchedulerTask(runnable),
          delayTicks
        );
    }
  }

  private void executeSync(@NotNull final Runnable runnable) {
    if (ThreadContext.forCurrentThread() == ThreadContext.SYNC) {
      Internal.wrapSchedulerTask(runnable).run();
    } else {
      Internal.syncBukkit().execute(runnable);
    }
  }

  private void markAsSupplied() {
    Preconditions.checkState(
      this.supplied.compareAndSet(false, true),
      "Promise is already being supplied."
    );
  }

  private record ApplyRunnable<V, U>(
    @NotNull PromiseImpl<U> promise,
    @NotNull Function<? super V, ? extends U> function,
    @NotNull V value
  )
    implements Runnable {
    @Override
    public void run() {
      if (this.promise.cancelled.get()) {
        return;
      }
      try {
        this.promise.complete(this.function.apply(this.value));
      } catch (final Throwable t) {
        this.promise.completeExceptionally(t);
      }
    }
  }

  private record ComposeRunnable<V, U>(
    @NotNull PromiseImpl<U> promise,
    @NotNull Function<? super V, ? extends Promise<U>> function,
    @NotNull V value,
    boolean sync
  )
    implements Runnable {
    @Override
    public void run() {
      if (this.promise.cancelled.get()) {
        return;
      }
      try {
        final var p = this.function.apply(this.value);
        if (p == null) {
          this.promise.complete(null);
        } else {
          if (this.sync) {
            p.thenAcceptSync(this.promise::complete);
          } else {
            p.thenAcceptAsync(this.promise::complete);
          }
        }
      } catch (final Throwable throwable) {
        this.promise.completeExceptionally(throwable);
      }
    }
  }

  private record ExceptionallyRunnable<U>(
    @NotNull PromiseImpl<U> promise,
    @NotNull Function<Throwable, ? extends U> function,
    @NotNull Throwable throwable
  )
    implements Runnable {
    @Override
    public void run() {
      if (this.promise.cancelled.get()) {
        return;
      }
      try {
        this.promise.complete(this.function.apply(this.throwable));
      } catch (final Throwable throwable) {
        this.promise.completeExceptionally(throwable);
      }
    }
  }

  private record SupplyRunnable<V>(
    @NotNull PromiseImpl<V> promise,
    @NotNull Supplier<V> supplier
  )
    implements Runnable {
    @Override
    public void run() {
      if (this.promise.cancelled.get()) {
        return;
      }
      try {
        this.promise.future.complete(this.supplier.get());
      } catch (final Throwable throwable) {
        this.promise.future.completeExceptionally(throwable);
      }
    }
  }

  private record ThrowingSupplyRunnable<V>(
    @NotNull PromiseImpl<V> promise,
    @NotNull Callable<V> supplier
  )
    implements Runnable {
    @Override
    public void run() {
      if (this.promise.cancelled.get()) {
        return;
      }
      try {
        this.promise.complete(this.supplier.call());
      } catch (final Throwable throwable) {
        this.promise.completeExceptionally(throwable);
      }
    }
  }
}
