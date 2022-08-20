package tr.com.infumia.task;

import java.util.function.Consumer;
import org.jetbrains.annotations.NotNull;

/**
 * an interface to determine contextual task builders.
 */
public interface ContextualTaskBuilder {
  /**
   * consumes the task.
   *
   * @param consumer the consumer to consume.
   *
   * @return consumed task.
   */
  @NotNull
  Task consume(@NotNull Consumer<Task> consumer);

  /**
   * runs the task.
   *
   * @param runnable the runnable to run.
   *
   * @return ran task.
   */
  @NotNull
  Task run(@NotNull Runnable runnable);
}
