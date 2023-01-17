package tr.com.infumia.task;

import com.google.common.base.Preconditions;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import tr.com.infumia.terminable.Terminable;

@UtilityClass
public class BukkitTasks {

  private final Scheduler ASYNC_SCHEDULER = new BukkitAsyncScheduler();

  private final AtomicReference<Plugin> PLUGIN = new AtomicReference<>();

  private final Scheduler SYNC_SCHEDULER = new BukkitSyncScheduler();

  @NotNull
  public Terminable init(@NotNull final Plugin plugin) {
    Preconditions.checkState(
      Bukkit.getServer().isPrimaryThread(),
      "Please use #init(Plugin) method in a main thread!"
    );
    BukkitTasks.PLUGIN.set(plugin);
    return Internal.init(
      SchedulerProvider.of(BukkitTasks.ASYNC_SCHEDULER, BukkitTasks.SYNC_SCHEDULER),
      new BukkitLogger()
    );
  }

  @NotNull
  Plugin plugin() {
    return Objects.requireNonNull(BukkitTasks.PLUGIN.get(), "init task first!");
  }
}
