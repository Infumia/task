package tr.com.infumia.task;

import java.util.function.Function;
import org.jetbrains.annotations.NotNull;

record PromiseApply<V, U>(
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
