package tr.com.infumia.task;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
final class InternalBukkitTask extends BukkitRunnable implements InternalTask {

  @NotNull
  Predicate<Task> backingTask;

  @NotNull
  AtomicBoolean cancelled = new AtomicBoolean(false);

  @NotNull
  AtomicInteger counter = new AtomicInteger(0);

  @Override
  public int id() {
    return this.getTaskId();
  }
}
