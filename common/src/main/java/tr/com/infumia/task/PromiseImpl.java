package tr.com.infumia.task;

import java.time.Duration;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class PromiseImpl<V> implements Promise<V> {

  private final AtomicBoolean cancelled = new AtomicBoolean(false);

  @Getter
  @NotNull
  private final CompletableFuture<V> future;

  private final AtomicBoolean supplied = new AtomicBoolean(false);

  @Nullable
  private Promise<?> parent;

  PromiseImpl() {
    this.future = new CompletableFuture<>();
  }

  PromiseImpl(@Nullable final V value) {
    this.future = CompletableFuture.completedFuture(value);
    this.supplied.set(true);
  }

  PromiseImpl(@NotNull final CompletableFuture<V> future) {
    this.future = future;
    this.supplied.set(true);
    this.cancelled.set(future.isCancelled());
  }

  @Override
  public boolean cancel(final boolean mayInterruptIfRunning) {
    if (this.parent != null) {
      this.parent.cancel(mayInterruptIfRunning);
    }
    this.cancelled.set(true);
    return this.future().cancel(mayInterruptIfRunning);
  }

  @Override
  public boolean cancelled() {
    return this.cancelled.get();
  }

  @NotNull
  @Override
  public Promise<V> setParent(@NotNull final Promise<?> parent) {
    if (this.parent == null) {
      this.parent = parent;
    } else {
      this.parent.setParent(parent);
    }
    return this;
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
    Executors.async(new PromiseSupply<>(this, supplier));
    return this;
  }

  @NotNull
  @Override
  public PromiseImpl<V> supplyDelayedAsync(
    @NotNull final Supplier<V> supplier,
    @NotNull final Duration delay
  ) {
    this.markAsSupplied();
    Executors.asyncDelayed(new PromiseSupply<>(this, supplier), delay);
    return this;
  }

  @NotNull
  @Override
  public PromiseImpl<V> supplyDelayedSync(
    @NotNull final Supplier<V> supplier,
    @NotNull final Duration delay
  ) {
    this.markAsSupplied();
    Executors.syncDelayed(new PromiseSupply<>(this, supplier), delay);
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
  public PromiseImpl<V> supplyExceptionallyAsync(@NotNull final Callable<V> callable) {
    this.markAsSupplied();
    Executors.async(new PromiseThrowingSupply<>(this, callable));
    return this;
  }

  @NotNull
  @Override
  public PromiseImpl<V> supplyExceptionallyDelayedAsync(
    @NotNull final Callable<V> callable,
    @NotNull final Duration delay
  ) {
    this.markAsSupplied();
    Executors.asyncDelayed(new PromiseThrowingSupply<>(this, callable), delay);
    return this;
  }

  @NotNull
  @Override
  public PromiseImpl<V> supplyExceptionallyDelayedSync(
    @NotNull final Callable<V> callable,
    @NotNull final Duration delay
  ) {
    this.markAsSupplied();
    Executors.syncDelayed(new PromiseThrowingSupply<>(this, callable), delay);
    return this;
  }

  @NotNull
  @Override
  public PromiseImpl<V> supplyExceptionallySync(@NotNull final Callable<V> callable) {
    this.markAsSupplied();
    Executors.sync(new PromiseThrowingSupply<>(this, callable));
    return this;
  }

  @NotNull
  @Override
  public PromiseImpl<V> supplySync(@NotNull final Supplier<V> supplier) {
    this.markAsSupplied();
    Executors.sync(new PromiseSupply<>(this, supplier));
    return this;
  }

  private void markAsSupplied() {
    if (this.supplied.compareAndSet(true, true)) {
      throw new IllegalStateException("Promise is already being supplied.");
    }
  }
}
