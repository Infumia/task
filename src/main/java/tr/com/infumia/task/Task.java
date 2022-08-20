package tr.com.infumia.task;

import tr.com.infumia.terminable.Terminable;

/**
 * an interface to determine tasks.
 */
public interface Task extends Terminable {
  @Override
  default void close() {
    this.stop();
  }

  /**
   * obtains the task id.
   *
   * @return task id.
   */
  int id();

  /**
   * stops the task.
   *
   * @return {@code true} if the task wasn't already cancelled.
   */
  boolean stop();

  /**
   * obtains the times ran.
   *
   * @return times ran.
   */
  int timesRan();
}
