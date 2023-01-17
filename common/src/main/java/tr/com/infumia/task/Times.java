package tr.com.infumia.task;

import java.time.Duration;
import java.util.concurrent.TimeUnit;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;

@UtilityClass
class Times {

  private final long MILLISECONDS_PER_SECOND = 1000L;

  private final long TICKS_PER_SECOND = 20L;

  private final long MILLISECONDS_PER_TICK = Times.MILLISECONDS_PER_SECOND / Times.TICKS_PER_SECOND;

  public long ticksFrom(@NotNull final Duration duration) {
    return duration.toMillis() / Times.MILLISECONDS_PER_TICK;
  }

  @NotNull
  Duration durationFrom(final long ticks) {
    return Times.durationFrom(ticks * Times.MILLISECONDS_PER_TICK, TimeUnit.MILLISECONDS);
  }

  @NotNull
  Duration durationFrom(final long duration, @NotNull final TimeUnit unit) {
    return Duration.of(duration, unit.toChronoUnit());
  }
}
