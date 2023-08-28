package tr.com.infumia.task;

import java.util.function.BiConsumer;
import java.util.function.Function;
import org.jetbrains.annotations.NotNull;

final class PromiseCompose<V, U> implements Runnable {

  @NotNull
  private final Function<V, ? extends Promise<U>> function;

  @NotNull
  private final Promise<U> promise;

  private final boolean sync;

  @NotNull
  private final V value;

  PromiseCompose(
    @NotNull final Promise<U> promise,
    @NotNull final Function<V, ? extends Promise<U>> function,
    @NotNull final V value,
    final boolean sync
  ) {
    this.promise = promise;
    this.function = function;
    this.value = value;
    this.sync = sync;
  }

  @Override
  public void run() {
    if (this.promise.cancelled()) {
      return;
    }
    try {
      final Promise<U> promise = this.function.apply(this.value);
      if (promise == null) {
        this.promise.complete(null);
      } else {
        final BiConsumer<U, Throwable> action = (u, throwable) -> {
          if (throwable == null) {
            this.promise.complete(u);
          } else {
            this.promise.completeExceptionally(throwable);
          }
        };
        if (this.sync) {
          promise.whenCompleteSync(action);
        } else {
          promise.whenCompleteAsync(action);
        }
      }
    } catch (final PromiseFilterException filter) {
      this.promise.completeExceptionally(filter);
    } catch (final Throwable throwable) {
      Internal.logger().severe(throwable.getMessage(), throwable);
      this.promise.completeExceptionally(throwable);
    }
  }
}
