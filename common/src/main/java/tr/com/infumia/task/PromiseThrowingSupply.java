package tr.com.infumia.task;

import java.util.concurrent.Callable;
import org.jetbrains.annotations.NotNull;

record PromiseThrowingSupply<V>(@NotNull PromiseImpl<V> promise, @NotNull Callable<V> supplier)
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
