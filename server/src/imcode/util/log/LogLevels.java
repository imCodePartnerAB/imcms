package imcode.util.log ;

public interface LogLevels {

    final static String CVS_REV = "$Revision$" ;
    final static String CVS_DATE = "$Date$" ;

    /**
       EMERGENCY: Something that brings the system down completely.
     */
    static final int EMERGENCY = 0 ;

    /**
       CRITICAL:  Something that brings down part of the system.
    */
    static final int CRITICAL  = 1 ;

    /**
       ERROR:     Something that malfunctions in an unrecoverable way.
    */
    static final int ERROR     = 2 ;

    /**
       WARNING:   Something that malfunctions in a recoverable way, or something that will malfunction.
    */
    static final int WARNING   = 3 ;

    /**
       NOTICE:    Something unusual or out of the ordinary that might need some attention.
    */
    static final int NOTICE    = 4 ;

    /**
       INFO:      Regular information. What we are doing.
    */
    static final int INFO      = 5 ;

    /**
       DEBUG:     For debugging info. Use to print values of variables, and so on.
    */
    static final int DEBUG     = 6 ;

    /**
       WILD:      For wild logging with the purpose of getting a glimpse of that annoying braindead bug that is impossible to find. Useful for logging decimals of pi, the users mothers maiden name, dirty jokes, and so on.
    */
    static final int WILD      = 7 ;

}


















