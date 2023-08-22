package tr.com.infumia.task;

import java.util.function.Predicate;
import org.jetbrains.annotations.NotNull;

final class PromiseFilter<V> implements Runnable {

  @NotNull
  private final Predicate<V> filter;

  @NotNull
  private final Promise<V> promise;

  @NotNull
  private final V value;

  PromiseFilter(
    @NotNull final Promise<V> promise,
    @NotNull final Predicate<V> filter,
    @NotNull final V value
  ) {
    this.promise = promise;
    this.filter = filter;
    this.value = value;
  }

  @Override
  public void run() {
    if (this.promise.cancelled()) {
      return;
    }
    try {
      if (this.filter.test(this.value)) {
        this.promise.complete(this.value);
      } else {
        this.promise.completeExceptionally(new PromiseFilterException());
      }
    } catch (final Throwable throwable) {
      Internal.logger().severe(throwable.getMessage(), throwable);
      this.promise.completeExceptionally(throwable);
    }
  }
}
