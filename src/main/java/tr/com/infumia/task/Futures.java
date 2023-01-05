package tr.com.infumia.task;

import java.util.function.BiConsumer;
import org.jetbrains.annotations.NotNull;

interface Futures {
  @NotNull
  static <V> BiConsumer<? super V, ? super Throwable> throwIfNotNull() {
    return (value, t) -> {
      if (t != null) {
        t.printStackTrace();
      }
    };
  }
}
