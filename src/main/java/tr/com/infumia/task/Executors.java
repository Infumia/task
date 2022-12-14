package tr.com.infumia.task;

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

  void asyncDelayed(@NotNull final Runnable runnable, final long delayInTicks) {
    if (delayInTicks <= 0L) {
      Executors.async(runnable);
    } else {
      Schedulers.async().runLater(new UncheckedRunnable(runnable), delayInTicks);
    }
  }

  void sync(@NotNull final Runnable runnable) {
    if (ThreadContext.forCurrentThread() == ThreadContext.SYNC) {
      new UncheckedRunnable(runnable).run();
    } else {
      Schedulers.sync().run(runnable);
    }
  }

  void syncDelayed(@NotNull final Runnable runnable, final long delayInTicks) {
    if (delayInTicks <= 0L) {
      Executors.sync(runnable);
    } else {
      Schedulers.sync().runLater(new UncheckedRunnable(runnable), delayInTicks);
    }
  }
}
