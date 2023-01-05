package tr.com.infumia.task;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import tr.com.infumia.terminable.Terminable;

@UtilityClass
public class Tasks {

  private final AtomicReference<Thread> MAIN_THREAD = new AtomicReference<>();

  private final AtomicReference<Plugin> PLUGIN = new AtomicReference<>();

  @NotNull
  public Terminable init(@NotNull final Plugin plugin) {
    if (!Bukkit.getServer().isPrimaryThread()) {
      throw new IllegalStateException("Please use #init(Plugin) method in a main thread!");
    }
    Tasks.PLUGIN.set(plugin);
    Tasks.MAIN_THREAD.set(Thread.currentThread());
    return AsyncExecutor.INSTANCE::cancelRepeatingTasks;
  }

  @NotNull
  Thread mainThread() {
    return Objects.requireNonNull(Tasks.MAIN_THREAD.get(), "initiate task first!");
  }

  @NotNull
  Plugin plugin() {
    return Objects.requireNonNull(Tasks.PLUGIN.get(), "init task first!");
  }
}
