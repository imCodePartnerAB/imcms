package imcode.util.log;

import org.apache.log4j.*;
import org.apache.log4j.spi.*;
import org.apache.log4j.helpers.*;

import java.io.*;
import java.util.Enumeration ;

import imcode.server.WebAppGlobalConstants;

public class WebAppFileAppender extends AppenderSkeleton implements AppenderAttachable {

    private final static String CVS_REV = "$Revision$" ;
    private final static String CVS_DATE = "$Date$" ;

    private static File absoluteWebAppPath = WebAppGlobalConstants.getInstance().getAbsoluteWebAppPath() ;

    private AppenderAttachableImpl appenders = new AppenderAttachableImpl() ;

    public WebAppFileAppender() {
	super();
    }

    private String fileName ;

    public void setFile(String fileName) {
	this.fileName = fileName ;
    }

    public void close() {
	closed = true ;
    }

    public boolean requiresLayout() {
	return false ;
    }

    public void append(LoggingEvent event) {
	appenders.appendLoopOnAppenders(event) ;
    }

    public void addAppender(Appender newAppender) {
	LogLog.debug("Adding appender "+newAppender.getName()+" to WebAppFileAppender") ;
	if (newAppender instanceof FileAppender) {
	    FileAppender fileAppender = (FileAppender)newAppender ;
	    String newFile = new File(absoluteWebAppPath, fileName).toString() ;
	    LogLog.debug("Setting file of appender "+fileAppender.getName()+" to "+newFile) ;
	    fileAppender.setFile(newFile) ;
	    fileAppender.activateOptions() ;
	}
	appenders.addAppender(newAppender) ;
    }

    public Enumeration getAllAppenders() {
	return appenders.getAllAppenders() ;
    }

    public Appender getAppender(String name) {
	return appenders.getAppender(name) ;
    }

    public boolean isAttached(Appender appender) {
	return appenders.isAttached(appender) ;
    }

    public void removeAllAppenders() {
	appenders.removeAllAppenders() ;
    }

    public void removeAppender(Appender appender) {
	appenders.removeAppender(appender) ;
    }

    public void removeAppender(String name) {
	appenders.removeAppender(name) ;
    }
}
