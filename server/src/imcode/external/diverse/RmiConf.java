 package imcode.external.diverse ;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.io.* ;
import java.net.* ;
import imcode.server.* ;
import imcode.util.* ;

public class RmiConf {
	private final static String CVS_REV = "$Revision$" ;
	private final static String CVS_DATE = "$Date$" ;
    static Hashtable interfaces = new Hashtable() ;	// Keeps track of servers. "ip:port"=interface
    static Hashtable paths = new Hashtable() ;	    // Keeps track of paths. "server"=interface
    private final static String LOGIN_PAGE = "conf_login.htm" ;

    imcode.server.User user ;

	/**
	 * Constructor
	 **/
    public RmiConf( imcode.server.User aUser) {
	this.user = aUser ;
    }

/**
 * GetInterface. Returns an interface to the host db. The JanusDB
 */
    static imcode.server.IMCServiceInterface getInterface(String server)  {
	return IMCServiceRMI.getInterface(server) ;
    }

    static imcode.server.IMCPoolInterface getPoolInterface(String server)  {
	return IMCServiceRMI.getPoolInterface(server) ;
    }



/********************* POOL FUNCTIONS *****************************'
 */



    // ************** END OF POOL INTERFACE FUNCTIONS ************************



    // ************** CALLS TO THE HOST DB - JANUS *****************************

/***
 * Executes a READ SQL stored procedure and returns a string
 **/

    public static String execJanusSqlProcedureStr(String server, String sqlStr )  {
	imcode.server.IMCServiceInterface imc = getInterface( server ) ;
	String retVal = null ;

	    retVal = imc.sqlProcedureStr(sqlStr) ;

	return retVal ;
    } // execSql

/**
 * Executes a READ SQL stored procedure and returns a String array
 */

    public static String[] execJanusSqlProcedure(String server, String sqlStr )  {
	imcode.server.IMCServiceInterface imc = getInterface( server ) ;
	String[] retVal = null ;

	   retVal = imc.sqlProcedure(sqlStr) ;

	return retVal ;
    }

/**
 * Executes a READ SQL stored procedure and returns a String array with the
 * following syntax.
 * In the first position, a string with the (n) nbr of fields each records has.
 * In the n following positions, the name of the columns in the sql query will be.
 * Position [0] = n nbr of columns
 * Position [1- n] = Metadata, the names of the columns
 * Position [n + 1] = Data
 *
 * Example: The stored procedure returns 3 rows with for each row, 3 columns named
 * name, age, length
 * [3, name, age, length, Kalle, 20, 1.80, Pelle, 30, 1.90, Nisse, 40, 1.60]
 */

    public static String[] execJanusSqlProcedureExt(String server, String sqlStr )  {
	imcode.server.IMCServiceInterface imc = getInterface( server ) ;
	String[] retVal = null ;

	    retVal = imc.sqlProcedureExt(sqlStr) ;

	return retVal ;
    }

/**
 * Executes an stored procedure on the database.
 * Updates or WRITE statement on the database without any returnvalue
 */

    public static void execJanusSqlUpdateProcedure(String server, String sqlStr )  {
	imcode.server.IMCServiceInterface imc = getInterface( server ) ;

	    imc.sqlUpdateProcedure(sqlStr) ;
	    return ;

    }

/**
 * Executes a SQL statement and returns a hashtable
 * Ex. "select * from myTable"
 */

    public static Hashtable execJanusQueryHash(String server, String sqlStr )  {
	imcode.server.IMCServiceInterface imc = getInterface( server ) ;

	    return imc.sqlQueryHash(sqlStr) ;

    }

/**
 * Executes a SQL stored procedure and returns a hashtable. The keys in the table
 * are the column names. The value is a string with all fields in that column
 */

    public static Hashtable execJanusProcedureHash(String server, String sqlStr )  {
	imcode.server.IMCServiceInterface imc = getInterface( server ) ;

	    return imc.sqlProcedureHash(sqlStr) ;

    }

/**
 * Executes a SQL stored procedure and returns an multidimensional string array.
 *
 */
    public static String[][] execJanusProcedureMulti(String server, String sqlStr )  {
	imcode.server.IMCServiceInterface imc = getInterface( server ) ;

	    return imc.sqlProcedureMulti(sqlStr) ;

    }


/***
 * Activates a child in the janus system. default value in Janus is inactivated
 */

    public void activateChild(String server, String meta_id )  {
	imcode.server.IMCServiceInterface imc = getInterface( server ) ;

	    imc.activateChild(Integer.parseInt(meta_id), user) ;

    } // activateChild


    // ****** CALLS TO THE CONFERENCE DB *****************

/***
 * Executes a READ SQL stored procedure and returns a string
 **/

    public static String execSqlProcedureStr(String server, String sqlStr )  {
	imcode.server.IMCPoolInterface imc = getPoolInterface( server ) ;

	return imc.sqlProcedureStr(sqlStr) ;

    } // execSql

/**
* Executes a READ SQL stored procedure and returns a String array
**/

    public static String[] execSqlProcedure(String server, String sqlStr )  {
	imcode.server.IMCPoolInterface imc = getPoolInterface( server ) ;

	   return imc.sqlProcedure(sqlStr) ;

    }


/**
 * Executes a READ SQL stored procedure and returns a String array with the
 * following syntax.
 * In the first position, a string with the (n) nbr of fields each records has.
 * In the n following positions, the name of the columns in the sql query will be.
 * Position [0] = n nbr of columns
 * Position [1- n] = Metadata, the names of the columns
 * Position [n + 1] = Data
 *
 * Example: The stored procedure returns 3 rows with for each row, 3 columns named
 * name, age, length
 * [3, name, age, length, Kalle, 20, 1.80, Pelle, 30, 1.90, Nisse, 40, 1.60]
 */

    public static String[] execSqlProcedureExt(String server, String sqlStr )  {
	imcode.server.IMCPoolInterface imc = getPoolInterface( server ) ;

	return imc.sqlProcedureExt(sqlStr) ;
    } // execSql


/**
 * Executes an stored procedure on the database.
 * Updates or WRITE statement on the database without any returnvalue
 */

    public static void execSqlUpdateProcedure(String server, String sqlStr )  {
	 imcode.server.IMCPoolInterface imc = getPoolInterface( server ) ;

	    imc.sqlUpdateProcedure(sqlStr) ;

    }

/**
 * Executes a SQL statement and returns a string
 */

    public static String execSqlQueryStr(String server, String sqlStr )  {
	imcode.server.IMCPoolInterface imc = getPoolInterface( server ) ;

	    return imc.sqlQueryStr(sqlStr) ;

    } // execSql


/**
 * Executes a SQL statement and returns a string array
 * Ex. "select * from myTable"
 */

    public static String[] execSqlQuery(String server, String sqlStr )  {
	imcode.server.IMCPoolInterface imc = getPoolInterface( server ) ;

	    return imc.sqlQuery(sqlStr) ;

    }


/**
 * Executes a SQL statement and returns a string array
 * Ex. "select * from myTable"	following syntax.
 * In the first position, a string with the (n) nbr of fields each records has.
 * In the n following positions, the name of the columns in the sql query will be.
 * Example: The stored procedure returns 3 rows with for each row, 3 columns named
 * name, age, length
 * Executes a READ SQL stored procedure and returns a String array with the
 * following syntax.
 * In the first position, a string with the (n) nbr of fields each records has.
 * In the n following positions, the name of the columns in the sql query will be.
 * Position [0] = n nbr of columns
 * Position [1- n] = Metadata, the names of the columns
 * Position [n + 1] = Data
 *
 * 3, name, age, length, Kalle, 20, 1.80, Pelle, 30, 1.90, Nisse, 40, 1.60
 */

    public static String[] execSqlQueryExt(String server, String sqlStr )  {
	imcode.server.IMCPoolInterface imc = getPoolInterface( server ) ;

	    return imc.sqlQueryExt(sqlStr) ;

    }


/**
 * Executes a SQL statement without any returnvalue
 */

    public static void execSqlUpdateQuery(String server, String sqlStr )  {
	imcode.server.IMCPoolInterface imc = getPoolInterface( server ) ;

	    imc.sqlUpdateQuery(sqlStr) ;

    }

/**
 * Executes a SQL statement and returns a hashtable
 * Ex. "select * from myTable"
 */

    public static Hashtable execSqlQueryHash(String server, String sqlStr )  {
	imcode.server.IMCPoolInterface imc = getPoolInterface( server ) ;

	    return imc.sqlQueryHash(sqlStr) ;

    }

/**
 * Executes a SQL stored procedure and returns a hashtable. The keys in the table
 * are the column names. The value is a string with all fields in that column
 */

    public static Hashtable execSqlProcedureHash(String server, String sqlStr )  {
	imcode.server.IMCPoolInterface imc = getPoolInterface( server ) ;

	    return imc.sqlProcedureHash(sqlStr) ;

    }


/**
 * Executes a SQL stored procedure and returns an multidimensional string array.
 */

    public static String[][] execProcedureMulti(String server, String sqlStr )  {
	imcode.server.IMCPoolInterface imc = getPoolInterface( server ) ;

	    return imc.sqlProcedureMulti(sqlStr) ;

    }


    // ******************* TEMPLATE FOLDER FUNCTIONS ****************



/**
 * Returns the physical path to the folder where the templates are located.
 * observer that if janus cant recognize the metaId were sending, it returns the
 * path to its own template catalogue
 */

    public static File getExternalTemplateFolder(String imcServer, int meta_id)  {
	imcode.server.IMCServiceInterface imc = getInterface( imcServer ) ;

	    return imc.getExternalTemplateFolder(meta_id) ;

    }


/**
 * Returns the physical path to the folder where the internal templates are located.
 * The function takes the meta id as argument, or use -1 if no metaid is known
 */

    public static File getInternalTemplateFolder(String imcServer, int meta_id)  {
	imcode.server.IMCServiceInterface imc = getInterface( imcServer ) ;

	    return imc.getInternalTemplateFolder(meta_id) ;

    }


/**
 * Returns the physical path to the folder where the internal templates are located.
 */

    public static File getInternalTemplateFolder(HttpServletRequest req) throws IOException, NumberFormatException {
	String host = req.getHeader("Host") ;
	String imcServer = imcode.util.Utility.getDomainPref("userserver",host) ;
	imcode.server.IMCServiceInterface imc = getInterface( imcServer ) ;
	int aMeta_id  = -1;

	// Lets get the metaid
	String meta_id = req.getParameter("meta_id") ;
	try {
	    aMeta_id = Integer.parseInt("meta_id") ;
	} catch (NumberFormatException ex) {
	    throw new IllegalArgumentException() ;
	}
	
	// Lets get the templatefolder from Janus,
	return imc.getInternalTemplateFolder(aMeta_id) ;

    }

/**
 * Returns the physical path to the imagefolder of the system.
 * Ex http://dev.imcode.com/images
 */

    public static String getInternalImageFolder(String imcServer)  {
	imcode.server.IMCServiceInterface imc = getInterface( imcServer ) ;

	    // Lets get the imageTemplateFolder from Janus,
	    return imc.getImageHome() ;

    }

/**
 * Returns the logical path to the imagefolder from the servlets folder.
 * Ex: ../images/se/
 */

    public static String getExternalImageFolder(String imcServer, String metaId)  {
	String theFolder = "" ;

	   // Lets get the ExternalimageTemplateFolder for a certain metaId,
	    theFolder = getInternalImageFolder(imcServer) ;
	    theFolder += execJanusSqlProcedureStr(imcServer, "GetMetaPathInfo " + metaId) ;


	return theFolder ;
    }


/**
 * Checks whether an user is administrator for a meta id or not, Returns true if the user has
 * rights to administrate the document, and and false if he is not
 */

    public static boolean checkAdminRights(String imcServer, String metaId, imcode.server.User user)  {
	boolean admin = false ;
	imcode.server.IMCServiceInterface imc = getInterface( imcServer ) ;
	int newMetaId = Integer.parseInt(metaId) ;

	     // Lets check if the user is an admin
	    return imc.checkDocAdminRights(newMetaId, user, 65536) ;

    } // checkAdminRights

/**
 * Checks whether an user has the right to administrate
 **/

    public static boolean checkDocRights(String imcServer, String metaId, imcode.server.User user)  {

	imcode.server.IMCServiceInterface imc = getInterface( imcServer ) ;
	int newMetaId = Integer.parseInt(metaId) ;

	    // Lets check if the user is an admin
	    return imc.checkDocRights(newMetaId, user) ;

    } // checkDocRights

/**
 * Parses a doc on the server.
 */

    public static String parseDoc(String imcServer, String htmlStr, java.util.Vector variables,
    java.util.Vector data )  {
	imcode.server.IMCServiceInterface imc = getInterface( imcServer ) ;

	    return imc.parseDoc(htmlStr, variables, data) ;

    } // parseDoc


/**
 * Parses a doc on the server.
 */

    public static String parseDoc(String imcServer, String htmlStr, java.util.Vector variables )
     {
	imcode.server.IMCServiceInterface imc = getInterface( imcServer ) ;

	    // Lets parse
	    return imc.parseDoc(htmlStr, variables) ;

    } // parseDoc

    // *********************** CONFERENCE URL'S *****************************
/**
 * Returns the conferences from the host.cfg file.
 */

    public static String getLoginUrl( String host) throws IOException {

	    return imcode.util.Utility.getDomainPref("start_url",host) ;

    } // end getLoginUrl

/**
 * Returns the name on the file used to login to the conference from.
 * Takes the property from the host config file.
 * Example. magnumsoft.properties
 */

    public static String getLoginPage(String host) throws IOException {
	// Lets get the path to the diagramfiles
	// String host = req.getHeader("Host") ;

	    return LOGIN_PAGE ;

    } // end getLoginPage

/**
 * Returns the imageHomefolder. Takes the property image_path
 * from the host config file.
 * Example: D:\apache\
 **/

    public static String getImageHomeFolder(String host ) throws IOException {

	    return imcode.util.Utility.getDomainPrefPath("image_path",host).toString() ;
    } // end getImageFolder

/**
 * Returns the imageHomefolder from the imcserver.cfg file.
 * Example :D:\apache\
 */

    public static String getExternalImageHomeFolder(String host, String imcServer, String metaId ) throws IOException {

	File imageFolder = new File(getImageHomeFolder(host)) ;
	imageFolder = new File(imageFolder, getLanguage(imcServer, metaId)) ;
	imageFolder = new File(imageFolder, getDocType(imcServer, metaId)) ;
	return imageFolder.toString() ;
    } // end getExternalImageHomeFolder


/**
 * Returns the langprefix for a meta id, for example "SE" for swedish
 */

    public static String getLanguage(String imcServer, String metaId) {
	String str = "" ;

	    str = execJanusSqlProcedureStr(imcServer, "GetLangPrefix " + metaId ) ;
	return str ;
    }

/**
 * Returns the doctype for a meta id, for example 102 for Conference
 */

    public static String getDocType(String imcServer, String metaId) {
	String str = "" ;

	    str = execJanusSqlProcedureStr(imcServer, "GetDocType " + metaId ) ;
	return str ;
    }


    // ******************* HELP METHODS  **********************
/**
 * Log. logs to the err file
 */
/*
    private static void log( String str) {
	System.err.println("RmiConf: " + str ) ;
    }
*/
/**
 * <p>Consumes the given property and returns the integer
 * value.
 *
 * @param properties Properties table
 * @param key Key of the property to retrieve and remove from
 * the properties table
 * @return Value of the property, or -1 if not found
 */
    private static int consumeInt(String value)
    {
	int n = -1;

	// Get the String value
	//value = consume(p, key);

	// Got a value; convert to an integer
	if (value != null) {
    try {
    n = Integer.parseInt(value);
}
catch (NumberFormatException ex) {
    }
	}
	return n;
    }



} // End class
