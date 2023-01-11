package tr.com.infumia.task;

import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;
import tr.com.infumia.terminable.Terminable;

@UtilityClass
class Internal {

  private final AtomicReference<Thread> MAIN_THREAD = new AtomicReference<>();

  private final long MILLISECONDS_PER_SECOND = 1000L;

  private final AtomicReference<SchedulerProvider> SCHEDULER_PROVIDER = new AtomicReference<>();

  private final long TICKS_PER_SECOND = 20L;

  private final long MILLISECONDS_PER_TICK =
    Internal.MILLISECONDS_PER_SECOND / Internal.TICKS_PER_SECOND;

  @NotNull
  Scheduler async() {
    return Internal.schedulerProvider().async();
  }

  @NotNull
  Scheduler get(@NotNull final ThreadContext context) {
    return switch (context) {
      case SYNC -> Internal.schedulerProvider().sync();
      case ASYNC -> Internal.schedulerProvider().async();
    };
  }

  @NotNull
  Terminable init(@NotNull final SchedulerProvider schedulerProvider) {
    Internal.MAIN_THREAD.set(Thread.currentThread());
    Internal.SCHEDULER_PROVIDER.set(schedulerProvider);
    return AsyncExecutor.INSTANCE::cancelRepeatingTasks;
  }

  @NotNull
  Thread mainThread() {
    return Objects.requireNonNull(Internal.MAIN_THREAD.get(), "initiate task first!");
  }

  @NotNull
  Scheduler sync() {
    return Internal.schedulerProvider().sync();
  }

  long ticksFrom(final long duration, @NotNull final TimeUnit unit) {
    return Internal.ticksFrom(Duration.of(duration, unit.toChronoUnit()));
  }

  long ticksFrom(@NotNull final Duration duration) {
    return duration.toMillis() / Internal.MILLISECONDS_PER_TICK;
  }

  static long ticksToMs(final long delayTicks) {
    return delayTicks * Internal.MILLISECONDS_PER_TICK;
  }

  @NotNull
  private SchedulerProvider schedulerProvider() {
    return Objects.requireNonNull(Internal.SCHEDULER_PROVIDER.get(), "initiate the task first!");
  }
}
