package tr.com.infumia.task;

import org.jetbrains.annotations.NotNull;

interface Logger {
  void severe(@NotNull String message, @NotNull Throwable cause);
}
