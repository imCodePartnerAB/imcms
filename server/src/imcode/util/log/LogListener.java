package imcode.util.log ;

public interface LogListener extends java.util.EventListener {

    /**
       Invoked by a Log when something is logged.
     */
    public void logged (LogEvent log) ;

    /**
       Flush this listener. Make sure all LogEvents are logged.
    */
    public void flush () ;

}
