package tr.com.infumia.task;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;
import tr.com.infumia.terminable.Terminable;

@UtilityClass
public class Internal {

  private final AtomicReference<Logger> LOGGER = new AtomicReference<>();

  private final AtomicReference<Thread> MAIN_THREAD = new AtomicReference<>();

  private final AtomicReference<SchedulerProvider> SCHEDULER_PROVIDER = new AtomicReference<>();

  @NotNull
  public Terminable init(
    @NotNull final SchedulerProvider schedulerProvider,
    @NotNull final Logger logger
  ) {
    Internal.MAIN_THREAD.set(Thread.currentThread());
    Internal.SCHEDULER_PROVIDER.set(schedulerProvider);
    Internal.LOGGER.set(logger);
    return AsyncExecutor.INSTANCE::cancelRepeatingTasks;
  }

  @NotNull
  Scheduler async() {
    return Internal.schedulerProvider().async();
  }

  @NotNull
  Scheduler get(@NotNull final ThreadContext context) {
    return switch (context) {
      case SYNC -> Internal.sync();
      case ASYNC -> Internal.sync();
    };
  }

  @NotNull
  Logger logger() {
    return Objects.requireNonNull(Internal.LOGGER.get(), "initiate the task first!");
  }

  @NotNull
  Thread mainThread() {
    return Objects.requireNonNull(Internal.MAIN_THREAD.get(), "initiate task first!");
  }

  @NotNull
  Scheduler sync() {
    return Internal.schedulerProvider().sync();
  }

  @NotNull
  private SchedulerProvider schedulerProvider() {
    return Objects.requireNonNull(Internal.SCHEDULER_PROVIDER.get(), "initiate the task first!");
  }
}
