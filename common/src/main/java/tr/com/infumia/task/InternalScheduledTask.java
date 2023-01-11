package tr.com.infumia.task;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
final class InternalScheduledTask implements InternalTask {

  @Getter
  @NotNull
  Predicate<Task> backingTask;

  @Getter
  @NotNull
  AtomicBoolean cancelled = new AtomicBoolean(false);

  @Getter
  @NotNull
  AtomicInteger counter = new AtomicInteger(0);

  @Nullable
  @NonFinal
  ScheduledFuture<?> task;

  @Override
  public void cancel() {
    if (this.task == null) {
      throw new IllegalStateException(
        "Initiate the task using #scheduleAtFixedRate(long, long, TimeUnit)"
      );
    }
    this.task.cancel(false);
  }

  @Override
  public int id() {
    throw new UnsupportedOperationException();
  }

  void scheduleAtFixedRate(
    final long initialDelay,
    final long period,
    @NotNull final TimeUnit unit
  ) {
    if (this.task != null) {
      throw new IllegalStateException("You cannot schedule the same task twice!");
    }
    this.task = AsyncExecutor.INSTANCE.scheduleAtFixedRate(this, initialDelay, period, unit);
  }
}
