package tr.com.infumia.task;

import java.util.concurrent.CountDownLatch;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;
import tr.com.infumia.terminable.Terminable;

public interface ServerThreadLock extends Terminable {
  @NotNull
  static ServerThreadLock obtain() {
    return new Impl();
  }

  @Override
  void close();

  @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
  final class Impl implements ServerThreadLock {

    CountDownLatch done = new CountDownLatch(1);

    CountDownLatch obtained = new CountDownLatch(1);

    private Impl() {
      if (ThreadContext.forCurrentThread() == ThreadContext.SYNC) {
        this.obtained.countDown();
        return;
      }
      Internal.sync().run(this::signal);
      this.await();
    }

    @Override
    public void close() {
      this.done.countDown();
    }

    private void await() {
      try {
        this.obtained.await();
      } catch (final InterruptedException e) {
        Thread.currentThread().interrupt();
      }
    }

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
