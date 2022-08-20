package tr.com.infumia.task;

import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;

/**
 * a class that contains utility methods for bukkit schedulers.
 */
@UtilityClass
public class Schedulers {

  /**
   * obtains the async scheduler.
   *
   * @return async scheduler.
   */
  @NotNull
  public Scheduler async() {
    return Internal.ASYNC_SCHEDULER;
  }

  /**
   * gets the scheduler from the context.
   *
   * @param context the context to get.
   *
   * @return scheduler.
   */
  @NotNull
  public Scheduler get(@NotNull final ThreadContext context) {
    return switch (context) {
      case SYNC -> Schedulers.sync();
      case ASYNC -> Schedulers.async();
    };
  }

  /**
   * creates a new task builder.
   *
   * @return a newly created task builder.
   */
  @NotNull
  public TaskBuilder newBuilder() {
    return TaskBuilder.newBuilder();
  }

  /**
   * obtains the sync scheduler.
   *
   * @return sync scheduler.
   */
  @NotNull
  public Scheduler sync() {
    return Internal.SYNC_SCHEDULER;
  }
}
