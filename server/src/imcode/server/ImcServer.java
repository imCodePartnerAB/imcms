package imcode.server ;

import imcode.util.log.Log ;
import imcode.util.log.WriterLogger ;

public class ImcServer extends Thread {

    final static int LOGINTERVAL = 30000 ;
    final static int LOGSIZE = 16384 ;

    static imcode.server.ApplicationServer server ;

    public ImcServer () throws java.io.IOException {

	server = new imcode.server.ApplicationServer() ;
    }


    public static void main(String[] args) {
	try {
	    Log log = Log.getLog("server") ;
	    int logLevel = Log.INFO ;
	    int flushLevel = Log.WARNING ;
	    if (args.length == 1) {
		if ("-w".equals(args[0])) {
		    logLevel = Log.WILD ;
		} else if ("-d".equals(args[0])) {
		    logLevel = Log.DEBUG ;
		} else if ("-q".equals(args[0])) {
		    logLevel = Log.CRITICAL ;
		}
	    }
	    WriterLogger stderr  = new WriterLogger(new java.io.OutputStreamWriter(System.err),logLevel,Log.INFO,LOGINTERVAL,LOGSIZE) ;
	    log.addLogListener(stderr) ;

	    ImcServer is = new ImcServer() ;
	    stderr.setFlushLevel(flushLevel) ;

	} catch (Exception ex) {
	    System.err.println("Failed to start server.") ;
	    System.err.println(ex.getMessage()) ;
	    System.exit(1) ;
	}
    }

}







