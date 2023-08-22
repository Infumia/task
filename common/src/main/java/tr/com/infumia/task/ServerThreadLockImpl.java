package tr.com.infumia.task;

import java.util.concurrent.CountDownLatch;

final class ServerThreadLockImpl implements ServerThreadLock {

  private final CountDownLatch done = new CountDownLatch(1);

  private final CountDownLatch obtained = new CountDownLatch(1);

  ServerThreadLockImpl() {
    if (ThreadContext.forCurrentThread() == ThreadContext.SYNC) {
      this.obtained.countDown();
      return;
    }
    Schedulers.sync().run(this::signal);
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
