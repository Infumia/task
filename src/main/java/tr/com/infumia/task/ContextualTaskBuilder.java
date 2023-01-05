package tr.com.infumia.task;

import java.util.function.Consumer;
import org.jetbrains.annotations.NotNull;

public interface ContextualTaskBuilder {
  @NotNull
  Task consume(@NotNull Consumer<Task> consumer);

  @NotNull
  Task run(@NotNull Runnable runnable);
}
