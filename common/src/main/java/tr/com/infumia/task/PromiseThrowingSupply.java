package tr.com.infumia.task;

import java.util.concurrent.Callable;
import org.jetbrains.annotations.NotNull;

final class PromiseThrowingSupply<V> implements Runnable {

  @NotNull
  private final Promise<V> promise;

  @NotNull
  private final Callable<V> supplier;

  PromiseThrowingSupply(@NotNull final Promise<V> promise, @NotNull final Callable<V> supplier) {
    this.promise = promise;
    this.supplier = supplier;
  }

  @Override
  public void run() {
    if (this.promise.cancelled()) {
      return;
    }
    try {
      this.promise.complete(this.supplier.call());
    } catch (final Throwable throwable) {
      Internal.logger().severe(throwable.getMessage(), throwable);
      this.promise.completeExceptionally(throwable);
    }
  }
}
