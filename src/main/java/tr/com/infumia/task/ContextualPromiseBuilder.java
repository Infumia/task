package tr.com.infumia.task;

import java.util.concurrent.Callable;
import java.util.function.Supplier;
import org.jetbrains.annotations.NotNull;
import tr.com.infumia.task.Promise;

/**
 * an interface to determine contextual promise builders.
 */
public interface ContextualPromiseBuilder {
  /**
   * calls it.
   *
   * @param callable the callable to call.
   * @param <T> type of the promise object.
   *
   * @return called promise.
   */
  @NotNull
  <T> Promise<T> call(@NotNull Callable<T> callable);

  /**
   * runs it.
   *
   * @param runnable the runnable to run.
   *
   * @return ran promise.
   */
  @NotNull
  Promise<Void> run(@NotNull Runnable runnable);

  /**
   * supplies it.
   *
   * @param supplier the supplier to supply.
   * @param <T> type of the supplied object.
   *
   * @return supplied promise.
   */
  @NotNull
  <T> Promise<T> supply(@NotNull Supplier<T> supplier);
}
