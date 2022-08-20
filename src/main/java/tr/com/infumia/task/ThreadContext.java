package tr.com.infumia.task;

import org.jetbrains.annotations.NotNull;

/**
 * an enum class that contains thread contexts.
 */
public enum ThreadContext {
  /**
   * the sync context.
   */
  SYNC,
  /**
   * the async context.
   */
  ASYNC;

  /**
   * gets the context for current thread.
   *
   * @return current thread's context.
   */
  @NotNull
  public static ThreadContext forCurrentThread() {
    return ThreadContext.forThread(Thread.currentThread());
  }

  /**
   * gets the context for thread.
   *
   * @param thread the thread to get.
   *
   * @return thread's context.
   */
  @NotNull
  public static ThreadContext forThread(@NotNull final Thread thread) {
    return Internal.mainThread().equals(thread)
      ? ThreadContext.SYNC
      : ThreadContext.ASYNC;
  }
}
