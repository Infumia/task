package tr.com.infumia.task;

import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

/**
 * a class that contains utility methods for paper module.
 */
@UtilityClass
public class Tasks {

  /**
   * initiates the task.
   *
   * @param plugin the plugin to init.
   */
  public void init(@NotNull final Plugin plugin) {
    if (!Bukkit.getServer().isPrimaryThread()) {
      throw new IllegalStateException(
        "Please use #init(Plugin) method in a main thread!"
      );
    }
    Internal.plugin(plugin);
    Internal.mainThread(Thread.currentThread());
  }
}
