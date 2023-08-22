package tr.com.infumia.task;

import java.util.function.BiFunction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

final class PromiseHandle<V, U> implements Runnable {

  @NotNull
  private final BiFunction<V, Throwable, ? extends U> function;

  @NotNull
  private final Promise<U> promise;

  @Nullable
  private final Throwable throwable;

  @Nullable
  private final V value;

  public PromiseHandle(
    @NotNull final Promise<U> promise,
    @NotNull final BiFunction<V, Throwable, ? extends U> function,
    @Nullable final V value,
    @Nullable final Throwable throwable
  ) {
    this.promise = promise;
    this.function = function;
    this.value = value;
    this.throwable = throwable;
  }

  @Override
  public void run() {
    if (this.promise.cancelled()) {
      return;
    }
    try {
      this.promise.complete(this.function.apply(this.value, this.throwable));
    } catch (final Throwable throwable) {
      Internal.logger().severe(throwable.getMessage(), throwable);
      this.promise.completeExceptionally(throwable);
    }
  }
}
