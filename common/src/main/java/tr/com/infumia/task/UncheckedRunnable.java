package tr.com.infumia.task;

import org.jetbrains.annotations.NotNull;

final class UncheckedRunnable implements Runnable {

  @NotNull
  private final Runnable delegate;

  UncheckedRunnable(@NotNull final Runnable delegate) {
    this.delegate = delegate;
  }

  @Override
  public void run() {
    try {
      this.delegate.run();
    } catch (final PromiseFilterException ignored) {} catch (final Throwable throwable) {
      Internal.logger().severe(throwable.getMessage(), throwable);
    }
  }
}
