package imcode.util.log ;

import java.io.IOException ; 
import java.io.Writer ;
import java.util.Timer ;

public class LogWriter implements LogListener, LogLevels  {

    /** The flushing timer. */
    protected Thread timer ;

    /** The interval between logs. */
    protected long interval ;

    /** The writer to log to */
    protected Writer out ;

    /** The logcache */
    protected StringBuffer cache ;

    /** The maximum time to keep the cache in memory. */
    protected int level ;

    /** The maximum size of cache to keep in memory. */
    protected int logsize ;

    /** Contains human readable names for the loglevels, each with a nice prefix character so it is easily spottable in a long list. */  
    protected final static String[] levelNames = {
	" [*EMERGENCY] ",
	" [*CRITICAL ] ",
	" [+ERROR    ] ",
	" [+WARNING  ] ",
	" [-NOTICE   ] ",
	" [.INFO     ] ",
	" [ DEBUG    ] ",
	" [ WILD     ] ",
    } ;

    /**
       Creates a LogWriter that keeps a cache of events, and only writes them to a file when required.
       @param log      The file to log to.
       @param level    The maximum loglevel that causes immediate flushing of the cache.
       @param interval The maximum time (in milliseconds) to keep the cache in memory.
       @param size     The maximum size of the cache, before it is flushed.
     */
    public LogWriter (Writer out, int level, int interval, int size) {

	cache = new StringBuffer(size+128) ;

	this.out = out ;
	this.logsize = size ;
	this.interval = interval ;
	this.level = level ;

	timer = new Thread() ;
	timer.setDaemon(true) ;
	timer.start() ;
    }

    /**
       Invoked by Log when something needs to be logged.
       @param event The event to log.
    */
    public void logged (LogEvent event) {
	String log = ""+event.getTime()+" "+levelNames[event.getLevel()]+"("+event.getLog()+") "+event.getMessage()+" Obj: "+event.getObject()+" Trace: "+event.getTrace() ;
	synchronized (cache) { // Make sure nothing is written to the cache between writing it and emptying it.
	    cache.append(log) ;
	}
	// Log immediately if necessary.
	if (cache.length()>logsize || event.getLevel()<=level) {
	    timer.interrupt() ;
       }
    }

    /**
       The flushing method.
       This keeps on flushing the cache with the given interval.
    */
    public void run () {
	Thread t = Thread.currentThread() ;
	while (timer == t) {
	    try {
		t.sleep(interval) ; // Sleep until it is time to log.
	    } catch (InterruptedException ex) {
		// OK. We were interrupted. Let's get to work.
	    }
	    if (cache.length() > 0) { // Is there anything to log in the cache?
		synchronized (cache) { // Make sure nothing is written to the cache between writing it and emptying it.
		    try {
			out.write(cache.toString()) ;
		    } catch (IOException ex) {
			System.err.println(ex) ;               // If we failed to log, let's make it known through standard error instead.
			System.err.println("Failed to log:") ;
			System.err.println(cache.toString()) ;
		    }
		    cache.setLength(0) ; // Clear out the cache.
		}
	    }
	}
    }
}
