package tr.com.infumia.task;

import java.util.function.BiConsumer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

final class PromiseWhenComplete<U> implements Runnable {

  @NotNull
  private final BiConsumer<U, Throwable> consumer;

  @NotNull
  private final Promise<U> promise;

  @Nullable
  private final Throwable throwable;

  @Nullable
  private final U value;

  PromiseWhenComplete(
    @NotNull final Promise<U> promise,
    @NotNull final BiConsumer<U, Throwable> consumer,
    @Nullable final U value,
    @Nullable final Throwable throwable
  ) {
    this.promise = promise;
    this.consumer = consumer;
    this.value = value;
    this.throwable = throwable;
  }

  @Override
  public void run() {
    if (this.promise.cancelled()) {
      return;
    }
    try {
      this.consumer.accept(this.value, this.throwable);
      if (this.throwable == null) {
        this.promise.complete(this.value);
      } else {
        this.promise.completeExceptionally(this.throwable);
      }
    } catch (final Throwable throwable) {
      Internal.logger().severe(throwable.getMessage(), throwable);
      this.promise.completeExceptionally(throwable);
    }
  }
}
