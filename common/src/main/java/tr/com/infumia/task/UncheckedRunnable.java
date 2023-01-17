package tr.com.infumia.task;

import org.jetbrains.annotations.NotNull;

record UncheckedRunnable(@NotNull Runnable delegate) implements Runnable {
  @Override
  public void run() {
    try {
      this.delegate.run();
    } catch (final Throwable throwable) {
      Internal.logger().severe(throwable.getMessage(), throwable);
    }
  }
}
