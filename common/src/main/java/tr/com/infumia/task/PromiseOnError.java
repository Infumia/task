package tr.com.infumia.task;

import java.util.function.Consumer;
import org.jetbrains.annotations.NotNull;

final class PromiseOnError<U> implements Runnable {

  @NotNull
  private final Consumer<Throwable> consumer;

  @NotNull
  private final Promise<U> promise;

  @NotNull
  private final Throwable throwable;

  public PromiseOnError(
    @NotNull final Promise<U> promise,
    @NotNull final Consumer<Throwable> consumer,
    @NotNull final Throwable throwable
  ) {
    this.promise = promise;
    this.consumer = consumer;
    this.throwable = throwable;
  }

  @Override
  public void run() {
    if (this.promise.cancelled()) {
      return;
    }
    try {
      this.consumer.accept(this.throwable);
      this.promise.completeExceptionally(this.throwable);
    } catch (final PromiseFilterException filter) {
      this.promise.completeExceptionally(filter);
    } catch (final Throwable throwable) {
      Internal.logger().severe(throwable.getMessage(), throwable);
      this.promise.completeExceptionally(throwable);
    }
  }
}
