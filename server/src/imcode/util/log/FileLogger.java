package imcode.util.log ;

import java.io.IOException ; 
import java.io.Writer ;
import java.util.Timer ;

public class LogWriter implements LogListener, LogLevels, Runnable {

    /** The flushing thread. */
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
    protected static String[] levelNames = {
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
	this.level = level ;
	timer = new Timer(true) ;
	timer.schedule(this, interval, interval) ;
    }

    public void logged (LogEvent event) {
	String log = ""+event.getTime()+" Lvl: "+levelNames[event.getLevel()]+" Log: "+event.getLog()+" Msg: "+event.getMessage()+" Obj: "+event.getObject()+" Trc: "+event.getTrace() ;

	synchronized (cache) {
	    cache.append(log) ;
	    // Log immediately if cache length exceeds maxsize, or loglevel is severe enough.
	    if (cache.length()>logsize || event.getLevel()<=level) {
		notify() ;
	    }
	}
    }

    public void run () {
	while (true) {
	    wait(
	    synchronized (cache) {
		if (cache.length() > 0) {
		    try {
			out.write(cache.toString()) ;
		    } catch (IOException ex) {
			System.err.print(ex+cache.toString()) ;
		    }
		    cache.setLength(0) ;

		}
	    }
	}
    }
    
}
