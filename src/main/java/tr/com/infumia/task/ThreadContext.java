package tr.com.infumia.task;

import org.jetbrains.annotations.NotNull;

public enum ThreadContext {
  SYNC,

  ASYNC;

  @NotNull
  public static ThreadContext forCurrentThread() {
    return ThreadContext.forThread(Thread.currentThread());
  }

  @NotNull
  public static ThreadContext forThread(@NotNull final Thread thread) {
    return Tasks.mainThread().equals(thread) ? ThreadContext.SYNC : ThreadContext.ASYNC;
  }
}
