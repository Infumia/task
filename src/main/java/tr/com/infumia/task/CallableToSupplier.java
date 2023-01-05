package tr.com.infumia.task;

import java.util.concurrent.Callable;
import java.util.function.Supplier;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;

record CallableToSupplier<T>(@NotNull Callable<T> delegate) implements Supplier<T> {
  @Override
  @SneakyThrows
  public T get() {
    return this.delegate.call();
  }
}
