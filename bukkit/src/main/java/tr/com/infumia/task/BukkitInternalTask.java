package tr.com.infumia.task;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import lombok.Getter;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

@Getter
final class BukkitInternalTask extends BukkitRunnable implements InternalTask {

  @NotNull
  private final Predicate<Task> backingTask;

  @NotNull
  private final AtomicBoolean cancelled = new AtomicBoolean(false);

  @NotNull
  private final AtomicInteger counter = new AtomicInteger(0);

  BukkitInternalTask(@NotNull final Predicate<Task> backingTask) {
    this.backingTask = backingTask;
  }

  @Override
  public int id() {
    return this.getTaskId();
  }
}
