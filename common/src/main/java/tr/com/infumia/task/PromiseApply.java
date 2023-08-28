package tr.com.infumia.task;

import java.util.function.Function;
import org.jetbrains.annotations.NotNull;

final class PromiseApply<V, U> implements Runnable {

  @NotNull
  private final Function<V, ? extends U> function;

  @NotNull
  private final Promise<U> promise;

  @NotNull
  private final V value;

  PromiseApply(
    @NotNull final Promise<U> promise,
    @NotNull final Function<V, ? extends U> function,
    @NotNull final V value
  ) {
    this.promise = promise;
    this.function = function;
    this.value = value;
  }

  @Override
  public void run() {
    if (this.promise.cancelled()) {
      return;
    }
    try {
      this.promise.complete(this.function.apply(this.value));
    } catch (final PromiseFilterException filter) {
      this.promise.completeExceptionally(filter);
    } catch (final Throwable throwable) {
      Internal.logger().severe(throwable.getMessage(), throwable);
      this.promise.completeExceptionally(throwable);
    }
  }
}
