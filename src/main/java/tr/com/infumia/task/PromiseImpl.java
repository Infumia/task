package tr.com.infumia.task;

import com.google.common.base.Preconditions;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
    final long delayTicks
  ) {
    this.markAsSupplied();
    Executors.asyncDelayed(new PromiseSupply<>(this, supplier), delayTicks);
    return this;
  }

  @NotNull
  @Override
  public PromiseImpl<V> supplyDelayedSync(
    @NotNull final Supplier<V> supplier,
    final long delayTicks
  ) {
    this.markAsSupplied();
    Executors.syncDelayed(new PromiseSupply<>(this, supplier), delayTicks);
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
    final long delayTicks
  ) {
    this.markAsSupplied();
    Executors.asyncDelayed(new PromiseThrowingSupply<>(this, callable), delayTicks);
    return this;
  }

  @NotNull
  @Override
  public PromiseImpl<V> supplyExceptionallyDelayedSync(
    @NotNull final Callable<V> callable,
    final long delayTicks
  ) {
    this.markAsSupplied();
    Executors.syncDelayed(new PromiseThrowingSupply<>(this, callable), delayTicks);
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

  void complete(@Nullable final V value) {
    if (!this.cancelled.get()) {
      this.future().complete(value);
    }
  }

  void completeExceptionally(@NotNull final Throwable ex) {
    if (!this.cancelled.get()) {
      this.future().completeExceptionally(ex);
    }
  }

  private void markAsSupplied() {
    Preconditions.checkState(
      this.supplied.compareAndSet(false, true),
      "Promise is already being supplied."
    );
  }
}
