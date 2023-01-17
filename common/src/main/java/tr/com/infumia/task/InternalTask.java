package tr.com.infumia.task;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import org.jetbrains.annotations.NotNull;

public interface InternalTask extends Task, Runnable {
  @NotNull
  Predicate<Task> backingTask();

  void cancel();

  @NotNull
  AtomicBoolean cancelled();

  @Override
  default boolean closed() {
    return this.cancelled().get();
  }

  @NotNull
  AtomicInteger counter();

  @Override
  default void run() {
    if (this.cancelled().get()) {
      this.cancel();
      return;
    }
    try {
      if (this.backingTask().test(this)) {
        this.cancel();
        return;
      }
      this.counter().incrementAndGet();
    } catch (final Throwable e) {
      e.printStackTrace();
    }
    if (this.cancelled().get()) {
      this.cancel();
    }
  }

  @Override
  default boolean stop() {
    return !this.cancelled().getAndSet(true);
  }

  @Override
  default int timesRan() {
    return this.counter().get();
  }
}
