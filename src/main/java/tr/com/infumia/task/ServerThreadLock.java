package tr.com.infumia.task;

import java.util.concurrent.CountDownLatch;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import tr.com.infumia.terminable.Terminable;

/**
 * an interface to determine server thread lock.
 */
public interface ServerThreadLock extends Terminable {
  /**
   * creates a new server thread locker.
   *
   * @return a newly created server thread lock.
   */
  @NotNull
  static ServerThreadLock obtain() {
    return new Impl();
  }

  @Override
  void close();

  /**
   * a simple implementation for {@link ServerThreadLock}.
   */
  @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
  final class Impl implements ServerThreadLock {

    /**
     * the done.
     */
    CountDownLatch done = new CountDownLatch(1);

    /**
     * the obtained.
     */
    CountDownLatch obtained = new CountDownLatch(1);

    /**
     * ctor.
     */
    private Impl() {
      if (ThreadContext.forCurrentThread() == ThreadContext.SYNC) {
        this.obtained.countDown();
        return;
      }
      Bukkit
        .getScheduler()
        .scheduleSyncDelayedTask(Internal.plugin(), this::signal);
      this.await();
    }

    @Override
    public void close() {
      this.done.countDown();
    }

    /**
     * awaits.
     */
    private void await() {
      try {
        this.obtained.await();
      } catch (final InterruptedException e) {
        Thread.currentThread().interrupt();
      }
    }

    /**
     * signals.
     */
    private void signal() {
      this.obtained.countDown();
      try {
        this.done.await();
      } catch (final InterruptedException e) {
        e.printStackTrace();
      }
    }
  }
}
