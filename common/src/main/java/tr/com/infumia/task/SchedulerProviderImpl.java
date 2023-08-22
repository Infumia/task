package tr.com.infumia.task;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;

@Getter
final class SchedulerProviderImpl implements SchedulerProvider {

  @NotNull
  private final Scheduler async;

  @NotNull
  private final Scheduler sync;

  SchedulerProviderImpl(@NotNull final Scheduler async, @NotNull final Scheduler sync) {
    this.async = async;
    this.sync = sync;
  }
}
