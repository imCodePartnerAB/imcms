package imcode.util.log ;

import java.text.DateFormat ;
import java.text.SimpleDateFormat ;
import java.util.HashMap ;
import java.util.HashSet ;
import java.util.GregorianCalendar ;
import java.util.Iterator ;
import java.io.Writer ;
import java.io.IOException ;


/**
   A logging class.
 */
public class Log implements LogLevels {

    /**
       Sets up a shutdownhook to make sure all logs are flushed before exiting the VM.
     */
    static {
	Runtime.getRuntime().addShutdownHook(
					     new Thread() {
		/** Flushes all logs. */
		public void run () {
		    Log.flushAll() ;
		}
		
	    }
					     ) ;
    }

    /**
       Contains the pool of logs for this VM.
    */
    protected static HashMap logs = new HashMap() ;


    /**
       The listeners on this log.
    */
    protected HashSet logListeners = new HashSet() ;

    protected String name ;

    /**
       Constructs a log.
    */
    protected Log (String name) {
	this.name = name ;
    }

    /**
       Returns the name of this log.
       @return The name of this log.
    */
    public String getName () {
	return name ;
    }

    /**
       Put an entry into this log.
       @param level The level of severity of this logentry. One of the LogLevels-constants.
       @param msg The message to log
    */
    public void log (int level, String msg) {
	log(level,msg,null) ;
    }

    /**
       Put an entry into this log.
       @param level The level of severity of this logentry. One of the LogLevels-constants.
       @param obj An Object to log
    */
    public void log (int level, Object obj) {
	log(level,null,obj) ;
    }


    /**
       Put an entry into this log.
       @param level The level of severity of this logentry. One of the LogLevels-constants.
       @param msg The message to log. May be null.
       @param obj   An object to blame as the cause of this log. An exception, for example. May be null.
    */
    public void log (int level, String msg, Object obj) {

	// Is this a valid loglevel?
	if ( level < EMERGENCY || level > WILD ) {
	    throw new IllegalArgumentException("Illegal loglevel.") ;
	}

	LogEvent event = new LogEvent(this,level,msg,obj) ;

	// Notify our listeners.
	synchronized (logListeners) {
	    Iterator it = logListeners.iterator() ;
	    while (it.hasNext()) {
		((LogListener)it.next()).logged(event) ;
	    }
	}
    }

    /**
       Flush this log.
    */
    public void flush () {
	// Flush our listeners.
	synchronized (logListeners) {
	    Iterator it = logListeners.iterator() ;
	    while (it.hasNext()) {
		((LogListener)it.next()).flush() ;
	    }
	}
    }

    /**
       Flush all logs.
    */
    public static void flushAll () {
	synchronized (logs) {
	    java.util.Collection logColl = logs.values() ;
	    Iterator it = logColl.iterator() ;
	    while (it.hasNext()) {
		Log l = (Log)it.next() ;
		l.flush() ;
	    }
	}
    }

    /**
       Add a listener to this log.
       @param listener The listener.
    */
    public void addLogListener(LogListener listener) {
	synchronized (logListeners) {
	    logListeners.add(listener) ;
	}
    }

    /**
       Remove a listener from this log.
       @param listener The listener.
    */
    public void removeLogListener(LogListener listener) {
	synchronized (logListeners) {
	    logListeners.remove(listener) ;
	}
    }

    /**
       Get a log with the given name. Creates a log if there is none.
       @param logname The name of the log to get or create.
       @return The log.
    */
    public static Log getLog (String logname) {
	Log log ;
	synchronized (logs) {
	    log = (Log)logs.get(logname) ;
	}
	if (log == null) {
	    log = new Log(logname) ;
	    synchronized (logs) {
		logs.put(logname, log) ;
	    }
	}	
	return log ;
    }

    /**
       Puts an entry into a log.
       @param log   The log.
       @param level The level of severity of this logentry. One of the LogLevels-constants.
       @param msg   The message to log.
       @param obj   An optional object to blame as the cause of this log.
    */
    public static void log (String log, int level, String msg, Object obj) {
	getLog(log).log(level, msg, obj) ;
    }

    /**
       A shutdownhook, used for flushing all logs before exiting the VM.
     */
}









