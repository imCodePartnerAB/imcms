package imcode.util.log ;

import java.text.DateFormat ;
import java.text.SimpleDateFormat ;

public class SimpleLogFormat implements LogFormat {

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

    public String format(LogEvent event) {
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

	return ""+dateFormat.format(event.getTime())+levelNames[event.getLevel()]+"("+event.getLog().getName()+") "+event.getMessage()+(obj!=null?" Obj: "+obj:"")+(trace!=null?" Trace: "+trace:"")+EOL ;
    }
}
