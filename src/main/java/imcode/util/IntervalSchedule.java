package imcode.util;

import org.apache.commons.lang.time.StopWatch;

public class IntervalSchedule {

    private StopWatch stopWatch;
    private long scheduleInterval;
    private long scheduledTime;

    public IntervalSchedule( long scheduleInterval ) {
        this.scheduleInterval = scheduleInterval;
        this.scheduledTime = scheduleInterval;
        this.stopWatch = new StopWatch();
        stopWatch.start();
    }

    public boolean isTime() {
        if ( stopWatch.getTime() < scheduledTime ) {
            return false;
        }
        scheduledTime += scheduleInterval;
        return true;
    }

    public StopWatch getStopWatch() {
        return stopWatch;
    }

}
