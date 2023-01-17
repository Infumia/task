package tr.com.infumia.task;

import java.time.Duration;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;

@UtilityClass
class Executors {

  void async(@NotNull final Runnable runnable) {
    if (ThreadContext.forCurrentThread() == ThreadContext.ASYNC) {
      new UncheckedRunnable(runnable).run();
    } else {
      Schedulers.async().run(runnable);
    }
  }

  void asyncDelayed(@NotNull final Runnable runnable, @NotNull final Duration delay) {
    if (delay.isNegative() || delay.isZero()) {
      Executors.async(runnable);
    } else {
      Schedulers.async().runLater(new UncheckedRunnable(runnable), delay);
    }
  }

  void sync(@NotNull final Runnable runnable) {
    if (ThreadContext.forCurrentThread() == ThreadContext.SYNC) {
      new UncheckedRunnable(runnable).run();
    } else {
      Schedulers.sync().run(runnable);
    }
  }

  void syncDelayed(@NotNull final Runnable runnable, @NotNull final Duration delay) {
    if (delay.isNegative() || delay.isZero()) {
      Executors.sync(runnable);
    } else {
      Schedulers.sync().runLater(new UncheckedRunnable(runnable), delay);
    }
  }
}
