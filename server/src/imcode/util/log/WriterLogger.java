package imcode.util.log ;

import java.io.IOException ; 
import java.io.Writer ;
import java.util.Timer ;
import java.text.DateFormat ;
import java.text.SimpleDateFormat ;

public class WriterLogger implements LogListener, LogLevels, Runnable  {

    /** The flushing timer. */
    protected Thread timer ;

    /** The interval between logs. */
    protected long interval ;

    /** The writer to log to */
    protected Writer out ;

    /** The logcache */
    protected StringBuffer cache ;

    /** The minimum loglevel to  keep in memory. */
    protected int flushLevel ;

    /** The maximum loglevel to log. */
    protected int logLevel ;

    /** The maximum size of cache to keep in memory. */
    protected int logsize ;

    protected DateFormat dateFormat = new SimpleDateFormat("[yyyy-MM-dd HH:mm:ss.SSS]") ;

    final static String EOL = System.getProperty("line.separator") ;

    /** Contains human readable names for the loglevels, each with a nice prefix character so it is easily spottable in a long list. */  
    protected final static String[] levelNames = {
	"[*EMERGENCY]",
	"[*CRITICAL ]",
	"[+ERROR    ]",
	"[+WARNING  ]",
	"[-NOTICE   ]",
	"[.INFO     ]",
	"[ DEBUG    ]",
	"[ WILD     ]",
    } ;

    /**
       Creates a LogWriter that keeps a cache of events, and only writes them to a file when required.
       @param log        The file to log to.
       @param logLevel   The maximum loglevel logged.
       @param flushLevel The maximum loglevel that causes immediate flushing of the cache.
       @param interval   The maximum time (in milliseconds) to keep the cache in memory.
       @param size       The maximum size of the cache, before it is flushed.
     */
    public WriterLogger (Writer out, int logLevel, int flushLevel, int interval, int size) {
	cache = new StringBuffer(size+128) ;

	this.out = out ;
	this.logsize = size ;
	this.interval = interval ;
	this.logLevel = logLevel ;
	this.flushLevel = flushLevel ;

	timer = new Thread(this) ;
	timer.setDaemon(true) ;
	timer.start() ;
    }

    /**
       Invoked by Log when something needs to be logged.
       If the object in the event is a Throwable, uses the message of the Throwable if available.
       @param event The event to log.
    */
    public void logged (LogEvent event) {
	if (event.getLevel()>logLevel) { // Ignore greater events with loglevels greater than logLevel
	    return ;
	}
	// If the object in the event is a Throwable, uses the message of the Throwable if available.
	Object obj = event.getObject() ;
	String trace = event.getTrace() ;
	if (obj instanceof Throwable) {
	    obj = ((Throwable)obj).getMessage() ;
	    if (obj == null) {
		obj = event.getObject() ;
	    }
	} else {
	    trace = null ;
	}
	String log = ""+dateFormat.format(event.getTime())+levelNames[event.getLevel()]+"("+event.getLog().getName()+") "+event.getMessage()+(obj!=null?" Obj: "+obj:"")+(trace!=null?" Trace: "+trace:"")+EOL ;
	//synchronized (cache) { // Make sure nothing is written to the cache while flushing it.
	    cache.append(log) ;
	    //}
	// Log immediately if necessary.
	if (event.getLevel()<=flushLevel || cache.length()>logsize ) {
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
	    flush() ;
	}
    }

    /**
       Flushes the log-cache.
     */
    public void flush() {
	if (cache.length() > 0) { // Is there anything to log in the cache?
	    //synchronized (cache) { // Make sure nothing is written to the cache while flushing it.
	    String cache_str = cache.toString() ;
		try {
		    out.write(cache_str) ;
		    out.flush() ;
		} catch (IOException ex) {
		    System.err.println(ex) ;               // If we failed to log, let's make it known through standard error instead.
		    System.err.println("Failed to log:") ;
		    System.err.println(cache_str) ;
		}
		cache.setLength(0) ; // Clear out the cache.
		//}
	}
    }

    /**
       Makes sure everything is logged before reaping this LogListener.
    */
    protected void finalize () throws Throwable {
	super.finalize() ;
	flush() ;
    }

    /**
       Set the flushlevel.
       @param flushLevel The maximum loglevel that causes immediate flushing.
     */
    public void setFlushLevel (int flushLevel) {
	// Is this a valid loglevel?
	if ( flushLevel < EMERGENCY || flushLevel > WILD ) {
	    throw new IllegalArgumentException("Illegal flushlevel.") ;
	}
	this.flushLevel = flushLevel ;
    }

    /**
       Set the flushlevel.
       @param flushLevel The maximum loglevel that is logged. Every log of a level over this is ignored.
     */
    public void setLogLevel (int logLevel) {
	// Is this a valid loglevel?
	if ( logLevel < EMERGENCY || logLevel > WILD ) {
	    throw new IllegalArgumentException("Illegal loglevel.") ;
  	}
	this.logLevel = logLevel ;
    }
}
