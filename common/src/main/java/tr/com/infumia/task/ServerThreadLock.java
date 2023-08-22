package tr.com.infumia.task;

import org.jetbrains.annotations.NotNull;
import tr.com.infumia.terminable.Terminable;

public interface ServerThreadLock extends Terminable {
  @NotNull
  static ServerThreadLock obtain() {
    return new ServerThreadLockImpl();
  }

  @Override
  void close();
}
