package imcode.server.document.index;

import org.apache.log4j.Logger;

import java.util.Date;
import java.util.TimerTask;

class IndexRebuildTimerTask extends TimerTask {

    private final static Logger log = Logger.getLogger(IndexRebuildTimerTask.class);

    private final long indexRebuildSchedulePeriodInMilliseconds;
    private final BackgroundIndexBuilder backgroundIndexBuilder;

    IndexRebuildTimerTask(long indexRebuildSchedulePeriodInMilliseconds,
                          BackgroundIndexBuilder backgroundIndexBuilder) {
        this.indexRebuildSchedulePeriodInMilliseconds = indexRebuildSchedulePeriodInMilliseconds;
        this.backgroundIndexBuilder = backgroundIndexBuilder;
    }

    public void run() {
        try {
            log.info("Starting scheduled index rebuild.");
            if (backgroundIndexBuilder.otherProcessModifiedIndexDirectory()) {
                log.debug("Another process modified index directory. Aborting.") ;
                cancel() ;
                return ;
            }
            backgroundIndexBuilder.start();
        } catch ( Exception e ) {
            log.warn("Caught exception during scheduled index rebuild.", e);
        }
        Date nextTime = new Date(scheduledExecutionTime() + indexRebuildSchedulePeriodInMilliseconds);
        log.info("Next index rebuild scheduled at " + RebuildingDirectoryIndex.formatDatetime(nextTime));
    }

}
