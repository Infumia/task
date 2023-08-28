package tr.com.infumia.task;

import java.util.function.Supplier;
import org.jetbrains.annotations.NotNull;

final class PromiseSupply<V> implements Runnable {

  @NotNull
  private final Promise<V> promise;

  @NotNull
  private final Supplier<V> supplier;

  public PromiseSupply(@NotNull final Promise<V> promise, @NotNull final Supplier<V> supplier) {
    this.promise = promise;
    this.supplier = supplier;
  }

  @Override
  public void run() {
    if (this.promise.cancelled()) {
      return;
    }
    try {
      this.promise.complete(this.supplier.get());
    } catch (final PromiseFilterException filter) {
      this.promise.completeExceptionally(filter);
    } catch (final Throwable throwable) {
      Internal.logger().severe(throwable.getMessage(), throwable);
      this.promise.completeExceptionally(throwable);
    }
  }
}
