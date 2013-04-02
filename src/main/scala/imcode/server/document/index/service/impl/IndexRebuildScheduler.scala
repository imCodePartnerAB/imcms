package imcode.server.document.index.service.impl

import com.imcode._
import com.imcode.util.Threads
import java.util.concurrent._
import java.util.concurrent.atomic.AtomicReference
import imcode.server.document.index.service.DocumentIndexService


trait IndexRebuildScheduler { this: DocumentIndexService =>

  private val executor = new ScheduledThreadPoolExecutor(1, new ThreadFactory {
    def newThread(runnable: Runnable): Thread = new Thread(runnable) |>> { thread =>
      thread.setName("index-rebuild-scheduler")
      thread.setDaemon(true)
    }
  })

  private val scheduledFutureRef = new AtomicReference[ScheduledFuture[_]]

  /**
   * Schedules periodic index rebuild with fixed interval.
   * Cancels any previously scheduled rebuild.
   * If interval is not given or its value is less or equals to zero then rebuild is not scheduled.
   *
   * @param intervalOpt interval in minutes.
   */
  def setRebuildIntervalInMinutes(intervalOpt: Option[Int]): Unit = synchronized {
    scheduledFutureRef.getAndSet(null).asOption.foreach(_.cancel(true))

    for (interval <- intervalOpt if interval > 0) {
      logger.info(s"Index rebuild schedule interval is set to $interval minutes.")

      executor.scheduleWithFixedDelay(
        Threads.mkRunnable {
          logger.info("Running scheduled index rebuild.")
          rebuild().foreach { indexRebuildTask =>
            try {
              indexRebuildTask.future.get
              logger.info(s"Scheduled index rebuild task has finished. Next scheduled run in $interval minutes.")
            } catch {
              case _: InterruptedException =>
                logger.info("Scheduled index rebuild task has been interrupted.")

              case _: CancellationException =>
                logger.info("Scheduled index rebuild task has been cancelled.")

              case e: ExecutionException =>
                logger.info("Scheduled index rebuild task has failed.", e)
            }
          }
        },
        interval,
        interval,
        TimeUnit.MINUTES
      ) |> scheduledFutureRef.set
    }
  }
}
