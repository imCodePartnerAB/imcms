package imcode.external.diverse ;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import imcode.server.* ;
import java.io.* ;
import java.net.* ;
import imcode.util.* ;
import imcode.server.* ;

import org.apache.log4j.Category;

public class RmiLayer {
	private final static String CVS_REV = "$Revision$" ;
	private final static String CVS_DATE = "$Date$" ;
    imcode.server.User user ;
    static Hashtable interfaces ;	// Keeps track of servers. "ip:port"=interface
	
	private static Category log = Category.getInstance("server");

    static {
        interfaces = new Hashtable() ;
    }

    public RmiLayer( imcode.server.User user) {
        this.user = user ;
    }

/**
 * GetInterface.
 */
    private static imcode.server.IMCServiceInterface getInterface(String server) {
        return IMCServiceRMI.getInterface(server) ;
    }



    public void activateChild(String server, String meta_id )  {
         imcode.server.IMCServiceInterface imc = getInterface( server ) ;
         
            imc.activateChild(Integer.parseInt(meta_id), user) ;
            return ;
    } // activateChild


/***
 * Inactivates a child in the janu system. default value in Janus is inactivated.
 * This func makes the child invisible in the Janus System
 */

    public void inActivateChild(String server, String meta_id)  {
        imcode.server.IMCServiceInterface imc = getInterface( server ) ;
        
	imc.inActiveChild(Integer.parseInt(meta_id), user) ;
	return ;
    }

    // ************* DELETE METAID *************

/**
 * Deletes a meta id in the db
 */

    public boolean deleteDocAll(String server, int metaId) {
        imcode.server.IMCServiceInterface imc = getInterface( server ) ;
        
            imc.deleteDocAll(metaId, this.user) ;
            return true ;
    }

    // ************* COUNTER FUNCTIONS *************

/**
 * Sets the counter on janus. returns the newValue the counter obj.
 **/

    public boolean setCounter(String server, int value)  {
         imcode.server.IMCServiceInterface imc = getInterface( server ) ;
         
            int newValue = imc.setCounter(value) ;
            return true ;
    }

/**
 * Increases the counter on janus with 1. returns the newValue the counter obj.
 */

    public int incCounter(String server)  {
        imcode.server.IMCServiceInterface imc = getInterface( server ) ;
        
            return imc.incCounter() ;
    }


/**
 * Returns the current counter value
 */

    public int getCounter(String server)  {
        imcode.server.IMCServiceInterface imc = getInterface( server ) ;
        
           return imc.getCounter() ;

    }


/**
 * Sets the counterdate on janus
 */

    public boolean setCounterDate(String server, String date)  {
        imcode.server.IMCServiceInterface imc = getInterface( server ) ;
        
            imc.setCounterDate(date) ;

        return true;
    }

/**
 * Sets the counterdate on janus
 */

    public String getCounterDate(String server)  {
        imcode.server.IMCServiceInterface imc = getInterface( server ) ;
        
          return imc.getCounterDate() ;


    }


/***************************	END COUNTER FUNCTIONS *********
 *
 *
 * // ******************* TEMPLATE FOLDER FUNCTIONS ****************
 * /**
 * Returns the physical path to the folder where the templates are located.
 * observer that if janus cant recognize the metaId were sending, it returns the
 * path to its own template catalogue
 */

    public static File getExternalTemplateFolder(String server, int meta_id)  {
        imcode.server.IMCServiceInterface imc = getInterface( server ) ;
        
            return imc.getExternalTemplateFolder(meta_id) ;

    }

/**
 * Returns the physical path to the folder where the internal templates are located.
 */

    public static File getInternalTemplateFolder(String server)  {
        imcode.server.IMCServiceInterface imc = getInterface( server ) ;
        
	// Lets get the templatefolder.
	return imc.getInternalTemplateFolder(-1) ;
    }


/**
 * Returns the physical path to the folder where the internal templates are located.
 * The function takes the meta id as argument, or use -1 if no metaid is known
 */

    public static File getInternalTemplateFolder(String server, int meta_id)  {
        imcode.server.IMCServiceInterface imc = getInterface( server ) ;
        
	// Lets get the templatefolder.
	return imc.getInternalTemplateFolder(meta_id) ;
    }


/**
 * Returns the physical path to the folder where the internal templates are located.
 */

    public static File getInternalTemplateFolder(HttpServletRequest req) throws IOException {

        String theFolder = "" ;
        // Lets get the server information from request object
        String host 				= req.getHeader("Host") ;
        String server 			= imcode.util.Utility.getDomainPref("userserver",host) ;

        // Lets get the metaid and convert it to an int
        String meta_id = req.getParameter("meta_id") ;
        int aMeta_id ;
        try {
            aMeta_id = Integer.parseInt("meta_id") ;
        } catch(NumberFormatException ex) {
	        log.debug("Exception occured" + ex );	   
		throw new IllegalArgumentException() ;
        }

        imcode.server.IMCServiceInterface imc = getInterface( server ) ;

	// Lets get the templatefolder from Janus,
	return imc.getInternalTemplateFolder(aMeta_id) ;
    }



/**
 * Checks whether an user is administrator or not
 */

    public static boolean checkAdminRights(String server, String metaId, imcode.server.User user)  {
        imcode.server.IMCServiceInterface imc = getInterface( server ) ;
        int newMetaId = Integer.parseInt(metaId) ;
        
            return imc.checkDocAdminRights(newMetaId, user, 65536) ;

    } // checkAdminRights



/**
 * Parses a doc on the server.
 **/

    public String parseDoc(String server, String htmlStr, java.util.Vector variables, java.util.Vector data )  {
	imcode.server.IMCServiceInterface imc = getInterface( server ) ;
     
	// Lets parse
	String anHtmlStr = imc.parseDoc(htmlStr, variables, data) ;
	return anHtmlStr ;
	
    } // parseDoc



/**
 * Parses a doc on the server.
 */

    public String parseDoc(String server, String htmlStr, java.util.Vector variables ) {
	imcode.server.IMCServiceInterface imc = getInterface( server ) ;
	// Lets parse
	String anHtmlStr = imc.parseDoc(htmlStr, variables) ;
	return anHtmlStr ;
    } // parseDoc



    // ****** CALL AN STORED PROCEDURE IN DB FUNCTIONS *********************

/***
 * Executes a READ SQL stored procedure and returns a string
 */

    public String execSQlProcedureStr(String server, String sqlStr )  {
        return this.execSqlProcedureStr(server, sqlStr) ;

    } // execSql

  /***
   * Executes a READ SQL stored procedure and returns a string
   */

    public String execSqlProcedureStr(String server, String sqlStr )  {
        imcode.server.IMCServiceInterface imc = getInterface( server ) ;
        
            return imc.sqlProcedureStr(sqlStr) ;

    } // execSql

/**
 * Executes a READ SQL stored procedure and returns a String array
 */

    public String[] execSqlProcedure(String server, String sqlStr )  {
        imcode.server.IMCServiceInterface imc = getInterface( server ) ;
        
            return imc.sqlProcedure(sqlStr) ;

    } // execSql


/**
 * Executes an WRITE statement on the database without any returnvalue
 */

    public void execSqlUpdateProcedure(String server, String sqlStr )
     {
        imcode.server.IMCServiceInterface imc = getInterface( server ) ;
        
           imc.sqlUpdateProcedure(sqlStr) ;

    } // execSql




    // ****** HERE IS THE SQL QUERIES FUNCTIONS *********************

/**
 * Executes a SQL statement and returns a string
 */

    public String execSqlQueryStr(String server, String sqlStr )  {
        imcode.server.IMCServiceInterface imc = getInterface( server ) ;
        
            return imc.sqlQueryStr(sqlStr) ;

    } // execSql


/**
 * Executes a SQL statement and returns a string array
 */

    public String[] execSqlQuery(String server, String sqlStr )  {
        imcode.server.IMCServiceInterface imc = getInterface( server ) ;
        
            return imc.sqlQuery(sqlStr) ;

    } // execSql


/**
 * Executes a SQL statement without any returnvalue
 */

    public void execSqlUpdateQuery(String server, String sqlStr )  {
        imcode.server.IMCServiceInterface imc = getInterface( server ) ;
        
            imc.sqlUpdateQuery(sqlStr) ;
            return ;

    } // execSql


/**
 * Executes a SQL stored procedure and returns an multidimensional string array.
 */

    public static String[][] execProcedureMulti(String server, String sqlStr )  {
       imcode.server.IMCServiceInterface imc = getInterface( server ) ;
        
          return imc.sqlProcedureMulti(sqlStr) ;

    }


    public static void log(String str) {
        System.out.println("RmiLayer: " + str ) ;
    }



} // End class
