package tr.com.infumia.task;

import tr.com.infumia.terminable.Terminable;

public interface Task extends Terminable {
  @Override
  default void close() {
    this.stop();
  }

  int id();

  boolean stop();

  int timesRan();
}
