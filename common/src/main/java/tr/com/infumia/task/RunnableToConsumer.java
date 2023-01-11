package tr.com.infumia.task;

import java.util.function.Consumer;
import org.jetbrains.annotations.NotNull;

record RunnableToConsumer<T>(@NotNull Runnable delegate) implements Consumer<T> {
  @Override
  public void accept(final T t) {
    this.delegate.run();
  }
}
