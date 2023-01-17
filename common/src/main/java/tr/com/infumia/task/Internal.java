package tr.com.infumia.task;

import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;
import tr.com.infumia.terminable.Terminable;

@UtilityClass
public class Internal {

  private final AtomicReference<Logger> LOGGER = new AtomicReference<>();

  private final AtomicReference<Thread> MAIN_THREAD = new AtomicReference<>();

  private final long MILLISECONDS_PER_SECOND = 1000L;

  private final AtomicReference<SchedulerProvider> SCHEDULER_PROVIDER = new AtomicReference<>();

  private final long TICKS_PER_SECOND = 20L;

  private final long MILLISECONDS_PER_TICK =
    Internal.MILLISECONDS_PER_SECOND / Internal.TICKS_PER_SECOND;

  @NotNull
  public Terminable init(
    @NotNull final SchedulerProvider schedulerProvider,
    @NotNull final Logger logger
  ) {
    Internal.MAIN_THREAD.set(Thread.currentThread());
    Internal.SCHEDULER_PROVIDER.set(schedulerProvider);
    Internal.LOGGER.set(logger);
    return AsyncExecutor.INSTANCE::cancelRepeatingTasks;
  }

  public long ticksFrom(@NotNull final Duration duration) {
    return duration.toMillis() / Internal.MILLISECONDS_PER_TICK;
  }

  @NotNull
  Scheduler async() {
    return Internal.schedulerProvider().async();
  }

  @NotNull
  Duration durationFrom(final long ticks) {
    return Internal.durationFrom(ticks * Internal.MILLISECONDS_PER_TICK, TimeUnit.MILLISECONDS);
  }

  @NotNull
  Duration durationFrom(final long duration, @NotNull final TimeUnit unit) {
    return Duration.of(duration, unit.toChronoUnit());
  }

  @NotNull
  Scheduler get(@NotNull final ThreadContext context) {
    return switch (context) {
      case SYNC -> Internal.schedulerProvider().sync();
      case ASYNC -> Internal.schedulerProvider().async();
    };
  }

  @NotNull
  Logger logger() {
    return Objects.requireNonNull(Internal.LOGGER.get(), "initiate the task first!");
  }

  @NotNull
  Thread mainThread() {
    return Objects.requireNonNull(Internal.MAIN_THREAD.get(), "initiate task first!");
  }

  @NotNull
  Scheduler sync() {
    return Internal.schedulerProvider().sync();
  }

  @NotNull
  private SchedulerProvider schedulerProvider() {
    return Objects.requireNonNull(Internal.SCHEDULER_PROVIDER.get(), "initiate the task first!");
  }
}
