package imcode.util.log ;

import org.apache.log4j.Category;
import org.apache.log4j.PropertyConfigurator;

/**
   A logging class.
   Distributes log-events to log-listeners.
   
   @author kreiger@imcode.com
   Modifierad av hasse@erudio.se 2001 09 12
**/
public class Log implements LogLevels {


	/* 
	* We uses Log4J logging library from http://jakarta.apache.org/log4j/
	* You find a short introductory manual at
	* http://jakarta.apache.org/log4j/docs/manual.html
	* The settings for logging, witch messages to log, how and where,
	* you find in a file called log4j.lcf.
	* This file is placed in the the catalogue pointed out by the 
	* parameter "log4j-init-file" in the web.xml file in the /WEB-INF directory
	*/
	public static void initLog( String logInitFile )
	{
		PropertyConfigurator.configure( logInitFile );

		Category logger = Category.getInstance( Log.class.getName() );
		logger.info("Logging started" );
	}

	private Category _logger = null;
	private String _name;

	public Log (String name)
	{
		_name = name;
		_logger = Category.getInstance( _name );
	}
	
	/**
	Returns the name of this log.
	@return The name of this log.
	*/
	public String getName ()
	{
		return _name ;
	}

	/**
	Put an entry into this log.
	@param level The level of severity of this logentry. One of the LogLevels-constants.
	@param msg The message to log
	*/
	public void log (int level, String msg)
	{
		log(level,msg,null) ;
	}

	/**
	Put an entry into this log.
	@param level The level of severity of this logentry. One of the LogLevels-constants.
	@param obj An Object to log
	*/
	public void log (int level, Object obj)
	{
		log(level,null,obj) ;
	}


	/**
	Put an entry into this log.
	@param level The level of severity of this logentry. One of the LogLevels-constants.
	@param msg The message to log. May be null.
	@param obj   An object to blame as the cause of this log. An exception, for example. May be null.
	*/
	public void log (int level, String msg, Object obj)
	{
		if ( level < EMERGENCY || level > WILD )
		{
			throw new IllegalArgumentException("Illegal loglevel.") ;
		}
		
		String message =  createMessage( msg, obj );
		log( _logger, level, message );	
	}
	
	private static String createMessage( String msg, Object obj ) 
	{
		String message = msg;
		if ( obj != null ) 
		{
			StringBuffer messageBuff = new StringBuffer( msg );
			messageBuff.append( ", " );
			messageBuff.append( obj.toString() );
			message = messageBuff.toString();
		}
		return message;
	}
	
	private static void log( Category logger, int oldLevel, String message ) 
	{

		switch ( oldLevel ) 
		{
		case LogLevels.CRITICAL:
			logger.fatal( "(CRITICAL) " + message );	
			break;
		case LogLevels.EMERGENCY:
			logger.fatal( "(EMERGENCY) " + message );
			break;
		case LogLevels.ERROR:
			logger.error( message );
			break;
		case LogLevels.NOTICE:
			logger.info( "(NOTICE) " + message );
			break;
		case LogLevels.WARNING:
			logger.warn( message );
			break;
		case LogLevels.INFO:
			logger.info( message );
			break;
		case LogLevels.DEBUG:
			logger.debug( message );
			break;
		case LogLevels.WILD:
			logger.debug( "(WILD)" + message );	
			break;
		}
	}

	/**
	Flush this log.
	*/
	public void flush ()
	{
		//ToDo: Is there anything to do here with log4j?
	}

	/**
	Flush all logs.
	*/
	public static void flushAll ()
	{
		//ToDo: Is there anything to do here with log4j?
	}

	/**
	Add a listener to this log.
	@param listener The listener.
	*/
//	public void addLogListener(LogListener listener)
//	{
//		//ToDo: Is there anything to do here with log4j?
//	}

	/**
	Remove a listener from this log.
	@param listener The listener.
	*/
//	public void removeLogListener(LogListener listener)
//	{
//		//ToDo: Is there anything to do here with log4j?
//	}

	/**
	Get a log with the given name. Creates a log if there is none.
	@param logname The name of the log to get or create.
	@return The log.
	*/
	public static Log getLog (String logName )
	{
		return new Log( logName );
	}

	/**
	Puts an entry into a log.
	@param log   The log.
	@param level The level of severity of this logentry. One of the LogLevels-constants.
	@param msg   The message to log.
	@param obj   An optional object to blame as the cause of this log.
	*/
	public static void log (String log, int level, String msg, Object obj)
	{
		log( Category.getInstance( log ), level, createMessage( msg, obj ) );
	}

}





