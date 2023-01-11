package tr.com.infumia.task;

import java.util.function.Supplier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

record RunnableToSupplier<T>(@NotNull Runnable delegate) implements Supplier<@Nullable T> {
  @Nullable
  @Override
  public T get() {
    this.delegate.run();
    return null;
  }
}
