package tr.com.infumia.task;

import java.util.function.Function;
import org.jetbrains.annotations.NotNull;

record PromiseExceptionally<U>(
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
