package imcode.util.log ;

import java.util.Date ;
import java.io.StringWriter ;
import java.io.PrintWriter ;

public class LogEvent extends java.util.EventObject implements LogLevels {

    /** The time of this event. */
    protected Date time ;

    /** The message for this event. */
    protected String message ;

    /** The object that was sent with this event. Might be an Exception, or the Object that was the cause of the event */
    protected Object obj ;

    /** The severity level of this log. */
    protected int level ;

    /** The stacktrace for this event */
    protected String trace ;

    /** Contains human readable names for the loglevels, each with a nice prefix character so it is easily spottable in a long list. */  
    protected static final String[] levelNames = {
	" [*EMERGENCY ] ",
	" [*CRITICAL  ] ",
	" [+ERROR     ] ",
	" [+WARNING   ] ",
	" [-NOTICE    ] ",
	" [.INFO      ] ",
	" [ DEBUG     ] ",
	" [ WILD      ] ",
	} ;

    final String EOL = System.getProperty("line.separator") ;
    final int EOLlength = EOL.length() ;

    /**
       Create a LogEvent.
       @param log The log which this event was sent from.
       @param level The severitylevel of this event. One of the LogLevels-constants.
       @param message A logmessage. May be null. If it is null, and obj is a Throwable, obj.getMessage() will be used.
       @param obj An object. Preferably an exception, or something to blame as the cause of this event. May be null.
    */
    public LogEvent(Log log, int level, String message, Object obj) {
	super(log) ;

	// Set time to now.
	time = new Date() ;

	// Set the level to the level given.
	this.level = level ;

	// Set the object to the object given.
	this.obj = obj ;

	if (obj instanceof Throwable) {
	    trace = createTrace((Throwable)obj) ;
	    if (message == null) { // Use the message from obj if message is null and obj is Throwable.
		this.message = ((Throwable)obj).getMessage() ;
	    }
	}
	// Set the message to the message given.
	if (message != null) {
	    this.message = message ;
	}
    }

    final static int LINES_TO_CREATETRACE = 1 ;
    final static int TRACELINES = 5 ;

    /**
       Get a stacktrace from a Throwable
       @param tw The Throwable
       @return A stacktrace
     */
    protected String createTrace(Throwable tw) {
	// Get a stacktrace. This should be easier.
	StringWriter sw = new StringWriter(256) ;
	int trimLines ; // The number of uninteresting lines of trace. (Lines referring to calls within the Log-classes)

	tw.printStackTrace(new PrintWriter(sw)) ;

	// Put it in a string.
	String tr = sw.toString() ;
	// Get rid of the lines that refer to calls within this class, since they are irrelevant.
	int index = 0;
	for (int i=0; (i<LINES_TO_CREATETRACE) && (-1!=(index=tr.indexOf(EOL,index))) ; ++i) {
	    index+=EOLlength ;
	}
	// Get the number of lines wanted.
	int endindex = index ;
	for (int i=0; (i<TRACELINES) && (-1!=(endindex=tr.indexOf(EOL,endindex))) ; ++i) {
	    endindex+=EOLlength ;
	}
	// Return our trace.
	if (index!=-1) {
	    return EOL+tr.substring(index,(endindex!=-1)?endindex:tr.length()) ; // Check if we got the number of lines wanted.
	}
	return "[Stacktrace not available!]" ;
    }

    /**
       Get the Log the event was sent from (to).
       Same as getLog(), but returns an Object instead of a Log.
       @return The Log.
    */
    public Object getSource() {
	return super.getSource() ;
    }

    /**
       Get the Log the event was sent from (to).
       Same as getSource(), but returns a Log instead of an Object.
       @return The Log.
    */
    public Log getLog() {
	return (Log)super.getSource() ;
    }

    /**
       @return The severity-level of this Event.
    */
    public int getLevel() {
	return level ;
    }

    /**
       @return The object that was sent with this event.
    */
    public Object getObject() {
	return obj ;
    }

    /**
       @return The stacktrace for this event.
    */
    public String getTrace() {
	return trace ;
    }

    /**
       @return The message for this event.
    */
    public String getMessage() {
	return message ;
    }

    public Date getTime() {
	return time ;
    }

    /**
       @return A simple string describing this event.
    */
    public String toString() {
	return ""+time+" Lvl: "+levelNames[level]+" Log: "+getLog().getName()+" Msg: "+message+" Obj: "+obj+" Trc: "+trace ;
    }
}



