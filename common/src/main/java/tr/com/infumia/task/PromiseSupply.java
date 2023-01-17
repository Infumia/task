package tr.com.infumia.task;

import java.util.function.Supplier;
import org.jetbrains.annotations.NotNull;

record PromiseSupply<V>(@NotNull PromiseImpl<V> promise, @NotNull Supplier<V> supplier)
  implements Runnable {
  @Override
  public void run() {
    if (this.promise.cancelled.get()) {
      return;
    }
    try {
      this.promise.complete(this.supplier.get());
    } catch (final Throwable throwable) {
      Internal.logger().severe(throwable.getMessage(), throwable);
      this.promise.completeExceptionally(throwable);
    }
  }
}
