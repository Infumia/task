package tr.com.infumia.task;

import java.time.Duration;
import java.util.concurrent.TimeUnit;
import lombok.experimental.UtilityClass;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;

@Log4j2
@UtilityClass
class Internal {

  private final Scheduler ASYNC_SCHEDULER = new AsyncScheduler();

  private final long MILLISECONDS_PER_SECOND = 1000L;

  private final Scheduler SYNC_SCHEDULER = new SyncScheduler();

  private final long TICKS_PER_SECOND = 20L;

  private final long MILLISECONDS_PER_TICK =
    Internal.MILLISECONDS_PER_SECOND / Internal.TICKS_PER_SECOND;

  static long ticksToMs(final long delayTicks) {
    return 0;
  }

  @NotNull
  Scheduler asyncScheduler() {
    return Internal.ASYNC_SCHEDULER;
  }

  @NotNull
  Scheduler syncScheduler() {
    return Internal.SYNC_SCHEDULER;
  }

  long ticksFrom(final long duration, @NotNull final TimeUnit unit) {
    return Internal.ticksFrom(Duration.of(duration, unit.toChronoUnit()));
  }

  long ticksFrom(@NotNull final Duration duration) {
    return duration.toMillis() / Internal.MILLISECONDS_PER_TICK;
  }
}
