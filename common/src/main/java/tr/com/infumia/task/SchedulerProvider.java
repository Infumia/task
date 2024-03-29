package tr.com.infumia.task;

import org.jetbrains.annotations.NotNull;

public interface SchedulerProvider {
  @NotNull
  static SchedulerProvider of(@NotNull final Scheduler async, @NotNull final Scheduler sync) {
    return new SchedulerProviderImpl(async, sync);
  }

  @NotNull
  Scheduler async();

  @NotNull
  Scheduler sync();
}
