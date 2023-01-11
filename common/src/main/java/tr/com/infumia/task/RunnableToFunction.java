package tr.com.infumia.task;

import java.util.function.Function;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

record RunnableToFunction<T, R>(@NotNull Runnable delegate) implements Function<T, @Nullable R> {
  @Nullable
  @Override
  public R apply(final T t) {
    this.delegate.run();
    return null;
  }
}
