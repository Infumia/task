package tr.com.infumia.task;

import java.util.function.Function;
import org.jetbrains.annotations.NotNull;

record PromiseCompose<V, U>(
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
