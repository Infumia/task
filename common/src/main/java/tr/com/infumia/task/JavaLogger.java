package tr.com.infumia.task;

import java.util.logging.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

final class JavaLogger implements Logger {

  @NotNull
  private final java.util.logging.Logger logger;

  JavaLogger(@NotNull final java.util.logging.Logger logger) {
    this.logger = logger;
  }

  @Override
  public void severe(@NotNull final String message, @NotNull final Throwable cause) {
    this.logger.log(Level.SEVERE, message, cause);
  }

  @Override
  public void severe(@NotNull final String message) {
    this.logger.severe(message);
  }

  @Override
  public void warning(@NotNull final String message, @NotNull final Object @Nullable... args) {
    this.logger.warning(String.format(message, args));
  }
}
