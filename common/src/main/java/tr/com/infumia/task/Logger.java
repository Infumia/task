package tr.com.infumia.task;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface Logger {
  void severe(@NotNull String message, @NotNull Throwable cause);

  void severe(@NotNull String message);

  void warning(@NotNull String message, @NotNull Object @Nullable... args);
}
