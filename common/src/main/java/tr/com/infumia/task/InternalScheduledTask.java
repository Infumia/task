package tr.com.infumia.task;

import java.time.Duration;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Getter
public final class InternalScheduledTask implements InternalTask {

  @NotNull
  private final Predicate<Task> backingTask;

  @Getter
  @NotNull
  private final AtomicBoolean cancelled = new AtomicBoolean(false);

  @Getter
  @NotNull
  private final AtomicInteger counter = new AtomicInteger(0);

  @Nullable
  private ScheduledFuture<?> task;

  public InternalScheduledTask(@NotNull final Predicate<Task> backingTask) {
    this.backingTask = backingTask;
  }

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

  public void scheduleAtFixedRate(
    @NotNull final Duration initialDelay,
    @NotNull final Duration period
  ) {
    if (this.task != null) {
      throw new IllegalStateException("You cannot schedule the same task twice!");
    }
    this.task =
    AsyncExecutor.INSTANCE.scheduleAtFixedRate(
      this,
      initialDelay.toMillis(),
      period.toMillis(),
      TimeUnit.MILLISECONDS
    );
  }
}
