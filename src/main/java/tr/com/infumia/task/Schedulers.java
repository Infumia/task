package tr.com.infumia.task;

import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;

@UtilityClass
public class Schedulers {

  @NotNull
  public Scheduler async() {
    return Internal.asyncScheduler();
  }

  @NotNull
  public Scheduler get(@NotNull final ThreadContext context) {
    return switch (context) {
      case SYNC -> Schedulers.sync();
      case ASYNC -> Schedulers.async();
    };
  }

  @NotNull
  public TaskBuilder newBuilder() {
    return TaskBuilder.newBuilder();
  }

  @NotNull
  public Scheduler sync() {
    return Internal.syncScheduler();
  }
}
