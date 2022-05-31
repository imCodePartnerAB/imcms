package imcode.server.document.index.service.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;

// translated from scala...
public interface IndexRebuildScheduler {

    Logger logger = LogManager.getLogger(IndexRebuildScheduler.class);

    ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1, runnable -> {
        final Thread thread = new Thread(runnable);
        thread.setName("index-rebuild-scheduler");
        thread.setDaemon(true);

        return null;
    });

    AtomicReference<ScheduledFuture> scheduledFutureRef = new AtomicReference<>();

    /**
     * Schedules periodic index rebuild with fixed interval.
     * Cancels any previously scheduled rebuild.
     * If interval is not given or its value is less or equals to zero then rebuild is not scheduled.
     *
     * @param interval interval in minutes.
     */
    default void setRebuildIntervalInMinutes(long interval) {
        final ScheduledFuture future = scheduledFutureRef.getAndSet(null);

        if (future != null) {
            future.cancel(true);
        }

        if (interval > 0) {
            logger.info("Index rebuild schedule interval is set to $interval minutes.");

            final Runnable scheduledCommand = () -> {
                logger.info("Running scheduled index rebuild.");

                try {
                    rebuild().get();
                    logger.info("Scheduled index rebuild task has finished. Next scheduled run in " + interval + " minutes.");

                } catch (InterruptedException e) {
                    logger.info("Scheduled index rebuild task has been interrupted.");
                } catch (CancellationException e) {
                    logger.info("Scheduled index rebuild task has been cancelled.");
                } catch (ExecutionException e) {
                    logger.info("Scheduled index rebuild task has failed.", e);
                }
            };

            final ScheduledFuture<?> scheduledFuture = executor.scheduleWithFixedDelay(
                    scheduledCommand, interval, interval, TimeUnit.MINUTES
            );

            scheduledFutureRef.set(scheduledFuture);
        }
    }

    Future rebuild();
}
