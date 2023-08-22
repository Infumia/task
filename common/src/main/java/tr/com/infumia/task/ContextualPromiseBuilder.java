package tr.com.infumia.task;

import java.util.concurrent.Callable;
import java.util.function.Supplier;
import org.jetbrains.annotations.NotNull;

public interface ContextualPromiseBuilder {
  @NotNull
  <T> Promise<T> call(@NotNull Callable<T> callable);

  @NotNull
  Promise<?> run(@NotNull Runnable runnable);

  @NotNull
  <T> Promise<T> supply(@NotNull Supplier<T> supplier);
}
