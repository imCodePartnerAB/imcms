package imcode.external.diverse ;
import java.util.*;
import java.rmi.*;
import java.rmi.registry.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.io.* ;
import java.net.* ;
import imcode.server.* ;
import imcode.util.* ;

public class RmiConf {
    static Hashtable interfaces ;	// Keeps track of servers. "ip:port"=interface
    static Hashtable paths ;	    // Keeps track of paths. "server"=interface
    imcode.server.User user ;


        /** This is a static initializer, which runs when the class is loaded!
         * Notice the lack of any method declaration
         */

    static {
        interfaces = new Hashtable() ;
        paths = new Hashtable() ;
    }

        /**
         * Constructor
         **/
    public RmiConf( imcode.server.User aUser) {
        this.user = aUser ;
    }

        /**
         * Constructor
         **/
    public RmiConf() {

    }

/**
 * GetInterface. Returns an interface to the host db. The JanusDB
 */
    static imcode.server.IMCServiceInterface getInterface(String server) throws IOException {
        if ( server == null ) {
            log("Invalid server argument") ;
            throw new IllegalArgumentException("Server == null") ;
        }
        imcode.server.IMCServiceInterface imc = (imcode.server.IMCServiceInterface)interfaces.get(server) ;
        if (imc == null) {
            imc = renewInterface(server) ;
        }
        return imc;
    }


/**
 * RenewInterface. Returns a renewed interface towards the host DB
 */

    static imcode.server.IMCServiceInterface renewInterface(String server) throws IOException {
        if ( server == null ) {
            throw new IllegalArgumentException("Server == null") ;
        }

        // Lets detect the rmi server
        String ip = "", port = "1099", object;
        try {
            StringTokenizer st = new StringTokenizer(server,":") ;
            String protocol = st.nextToken() ;
            if ( protocol.indexOf("/")!=-1 ) {
                throw new MalformedURLException ("Bad RMI-URL: "+server) ;
            }
            if ( !protocol.toLowerCase().equals("rmi") ) {
                throw new MalformedURLException ("Unknown protocol: "+protocol+" in RMI-URL "+server) ;
            }
            st.nextToken("/") ;
            String host = st.nextToken() ;
            if ( host.indexOf(":")!=-1 ) {
                StringTokenizer st2 = new StringTokenizer(host,":") ;
                host = st2.nextToken() ;
                port = st2.nextToken() ;
            }
            object = st.nextToken() ;
        } catch ( NoSuchElementException ex ) {
            throw new MalformedURLException ("Bad RMI-URL: "+server) ;
        }

        // OK, Lets try our interface with,

        imcode.server.IMCServiceInterface imc ;
        try {
            Registry reg = LocateRegistry.getRegistry(ip, Integer.parseInt(port)) ;
            imc = (imcode.server.IMCServiceInterface)reg.lookup(object) ;
        } catch ( Exception ex ) {
            Registry reg = LocateRegistry.getRegistry(ip, Integer.parseInt(port)) ;
            try {
                imc = (imcode.server.IMCServiceInterface)reg.lookup(object) ;
            } catch ( NotBoundException exc ) {
                log("No IMCService object found") ;
                throw new RemoteException (exc.getMessage() + " No IMCService object found") ;
            }
        }
        interfaces.put(server, imc) ;
        return imc ;
    }

/********************* POOL FUNCTIONS *****************************'
 *
 * /**
 * GetInterface. Returns an interface to the host db. The JanusDB
 */
    static imcode.server.IMCPoolInterface getPoolInterface(String server) throws IOException {
        if ( server == null ) {
            log("Invalid server argument") ;
            throw new IllegalArgumentException("Server == null") ;
        }
        imcode.server.IMCPoolInterface imc = (imcode.server.IMCPoolInterface)interfaces.get(server) ;
        if (imc == null) {
            imc = renewPoolInterface(server) ;
        }
        return imc;
    }


/**
 * RenewInterface. Returns a renewed interface towards the host DB
 */

    static imcode.server.IMCPoolInterface renewPoolInterface(String server) throws IOException {
        if ( server == null ) {
            throw new IllegalArgumentException("Server == null") ;
        }

        // Lets detect the rmi server
        String ip = "", port = "1099", object;
        try {
            StringTokenizer st = new StringTokenizer(server,":") ;
            String protocol = st.nextToken() ;
            if ( protocol.indexOf("/")!=-1 ) {
                throw new MalformedURLException ("Bad RMI-URL: "+server) ;
            }
            if ( !protocol.toLowerCase().equals("rmi") ) {
                throw new MalformedURLException ("Unknown protocol: "+protocol+" in RMI-URL "+server) ;
            }
            st.nextToken("/") ;
            String host = st.nextToken() ;
            if ( host.indexOf(":")!=-1 ) {
                StringTokenizer st2 = new StringTokenizer(host,":") ;
                host = st2.nextToken() ;
                port = st2.nextToken() ;
            }
            object = st.nextToken() ;
        } catch ( NoSuchElementException ex ) {
            throw new MalformedURLException ("Bad RMI-URL: "+server) ;
        }

        // OK, Lets try our interface with,

        imcode.server.IMCPoolInterface imc ;
        try {
            Registry reg = LocateRegistry.getRegistry(ip, Integer.parseInt(port)) ;
            imc = (imcode.server.IMCPoolInterface)reg.lookup(object) ;
        } catch ( Exception ex ) {
            Registry reg = LocateRegistry.getRegistry(ip, Integer.parseInt(port)) ;
            try {
                imc = (imcode.server.IMCPoolInterface)reg.lookup(object) ;
            } catch ( NotBoundException exc ) {
                log("No IMCService object found") ;
                throw new RemoteException (exc.getMessage() + " No IMCPool object found") ;
            }
        }
        interfaces.put(server, imc) ;
        return imc ;
    }

    // ************** END OF POOL INTERFACE FUNCTIONS ************************



    // ************** CALLS TO THE HOST DB - JANUS *****************************

/***
 * Executes a READ SQL stored procedure and returns a string
 **/

    public static String execJanusSqlProcedureStr(String server, String sqlStr ) throws IOException {
        imcode.server.IMCServiceInterface imc = getInterface( server ) ;
        String retVal = null ;
        try {
            retVal = imc.sqlProcedureStr(sqlStr) ;
        } catch (IOException ex) {
            imc = renewInterface(server) ;
            retVal = imc.sqlProcedureStr(sqlStr) ;
        }
        return retVal ;
    } // execSql

/**
 * Executes a READ SQL stored procedure and returns a String array
 */

    public static String[] execJanusSqlProcedure(String server, String sqlStr ) throws IOException {
        imcode.server.IMCServiceInterface imc = getInterface( server ) ;
        String[] retVal = null ;
        try {
           retVal = imc.sqlProcedure(sqlStr) ;
        } catch (IOException ex) {
            imc = renewInterface(server) ;
            retVal = imc.sqlProcedure(sqlStr) ;
        }
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

    public static String[] execJanusSqlProcedureExt(String server, String sqlStr ) throws IOException {
        imcode.server.IMCServiceInterface imc = getInterface( server ) ;
        String[] retVal = null ;
        try {
            retVal = imc.sqlProcedureExt(sqlStr) ;
        } catch (IOException ex) {
            imc = renewInterface(server) ;
            retVal = imc.sqlProcedure(sqlStr) ;
        }
        return retVal ;
    }

/**
 * Executes an stored procedure on the database.
 * Updates or WRITE statement on the database without any returnvalue
 */

    public static void execJanusSqlUpdateProcedure(String server, String sqlStr ) throws IOException {
        imcode.server.IMCServiceInterface imc = getInterface( server ) ;
        try {
            imc.sqlUpdateProcedure(sqlStr) ;
            return ;
        } catch (IOException ex) {
            imc = renewInterface(server) ;
            imc.sqlUpdateProcedure(sqlStr) ;
        }
    }

/**
 * Executes a SQL statement and returns a hashtable
 * Ex. "select * from myTable"
 */

    public static Hashtable execJanusQueryHash(String server, String sqlStr ) throws IOException {
        imcode.server.IMCServiceInterface imc = getInterface( server ) ;
        try {
            return imc.sqlQueryHash(sqlStr) ;
        } catch (IOException ex) {
            imc = renewInterface(server) ;
            return imc.sqlQueryHash(sqlStr) ;
        }
    }

/**
 * Executes a SQL stored procedure and returns a hashtable. The keys in the table
 * are the column names. The value is a string with all fields in that column
 */

    public static Hashtable execJanusProcedureHash(String server, String sqlStr ) throws IOException {
        imcode.server.IMCServiceInterface imc = getInterface( server ) ;
        try {
            return imc.sqlProcedureHash(sqlStr) ;
        } catch (IOException ex) {
            imc = renewInterface(server) ;
            return imc.sqlProcedureHash(sqlStr) ;
        }
    }

/**
 * Executes a SQL stored procedure and returns an multidimensional string array.
 *
 */
    public static String[][] execJanusProcedureMulti(String server, String sqlStr ) throws IOException {
        imcode.server.IMCServiceInterface imc = getInterface( server ) ;
        try {
            return imc.sqlProcedureMulti(sqlStr) ;
        } catch (IOException ex) {
            imc = renewInterface(server) ;
            return imc.sqlProcedureMulti(sqlStr) ;
        }
    }


/***
 * Activates a child in the janus system. default value in Janus is inactivated
 */

    public void activateChild(String server, String meta_id ) throws IOException {
        imcode.server.IMCServiceInterface imc = getInterface( server ) ;
        try {
            imc.activateChild(Integer.parseInt(meta_id), user) ;
        } catch (IOException ex) {
            imc = renewInterface(server) ;
            imc.activateChild(Integer.parseInt(meta_id), user) ;
        }
    } // activateChild


    // ****** CALLS TO THE CONFERENCE DB *****************

/***
 * Executes a READ SQL stored procedure and returns a string
 **/

    public static String execSqlProcedureStr(String server, String sqlStr ) throws IOException {
        imcode.server.IMCPoolInterface imc = getPoolInterface( server ) ;
        try {
            return imc.sqlProcedureStr(sqlStr) ;
        } catch (IOException ex) {
            imc = renewPoolInterface(server) ;
            return imc.sqlProcedureStr(sqlStr) ;
        }
    } // execSql

/**
* Executes a READ SQL stored procedure and returns a String array
**/

    public static String[] execSqlProcedure(String server, String sqlStr ) throws IOException {
        imcode.server.IMCPoolInterface imc = getPoolInterface( server ) ;
        try {
           return imc.sqlProcedure(sqlStr) ;
        } catch (IOException ex) {
            imc = renewPoolInterface(server) ;
            return imc.sqlProcedure(sqlStr) ;
        }
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

    public static String[] execSqlProcedureExt(String server, String sqlStr ) throws IOException {
        imcode.server.IMCPoolInterface imc = getPoolInterface( server ) ;
        try {
            return imc.sqlProcedureExt(sqlStr) ;
        } catch (IOException ex) {
            imc = renewPoolInterface(server) ;
            return imc.sqlProcedureExt(sqlStr) ;
        } /*catch (java.rmi.RemoteException ex) {
            log(ex.getMessage()) ;
            ex.printStackTrace();
            throw new ServletException(ex.getMessage());
        } catch (java.io.IOException ex) {
            log(ex.getMessage()) ;
            ex.printStackTrace();
            throw new ServletException(ex.getMessage());
        }
        */
    } // execSql


/**
 * Executes an stored procedure on the database.
 * Updates or WRITE statement on the database without any returnvalue
 */

    public static void execSqlUpdateProcedure(String server, String sqlStr ) throws IOException {
         imcode.server.IMCPoolInterface imc = getPoolInterface( server ) ;
        try {
            imc.sqlUpdateProcedure(sqlStr) ;
        } catch (IOException ex) {
            imc = renewPoolInterface(server) ;
            imc.sqlUpdateProcedure(sqlStr) ;
       }
    }

/**
 * Executes a SQL statement and returns a string
 */

    public static String execSqlQueryStr(String server, String sqlStr ) throws IOException {
        imcode.server.IMCPoolInterface imc = getPoolInterface( server ) ;
        try {
            return imc.sqlQueryStr(sqlStr) ;
        } catch (IOException ex) {
            imc = renewPoolInterface(server) ;
            return imc.sqlQueryStr(sqlStr) ;
        }
    } // execSql


/**
 * Executes a SQL statement and returns a string array
 * Ex. "select * from myTable"
 */

    public static String[] execSqlQuery(String server, String sqlStr ) throws IOException {
        imcode.server.IMCPoolInterface imc = getPoolInterface( server ) ;
        try {
            return imc.sqlQuery(sqlStr) ;
        } catch (IOException ex) {
            imc = renewPoolInterface(server) ;
            return imc.sqlQuery(sqlStr) ;
       }
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

    public static String[] execSqlQueryExt(String server, String sqlStr ) throws IOException {
        imcode.server.IMCPoolInterface imc = getPoolInterface( server ) ;
        try {
            return imc.sqlQueryExt(sqlStr) ;
        } catch (IOException ex) {
            imc = renewPoolInterface(server) ;
            return imc.sqlQueryExt(sqlStr) ;
        }
    }


/**
 * Executes a SQL statement without any returnvalue
 */

    public static void execSqlUpdateQuery(String server, String sqlStr ) throws IOException {
        imcode.server.IMCPoolInterface imc = getPoolInterface( server ) ;
        try {
            imc.sqlUpdateQuery(sqlStr) ;
        } catch (IOException ex) {
            imc = renewPoolInterface(server) ;
            imc.sqlUpdateQuery(sqlStr) ;
        }
    }

/**
 * Executes a SQL statement and returns a hashtable
 * Ex. "select * from myTable"
 */

    public static Hashtable execSqlQueryHash(String server, String sqlStr ) throws IOException {
        imcode.server.IMCPoolInterface imc = getPoolInterface( server ) ;
        try {
            return imc.sqlQueryHash(sqlStr) ;
        } catch (IOException ex) {
            imc = renewPoolInterface(server) ;
            return imc.sqlQueryHash(sqlStr) ;
        }
    }

/**
 * Executes a SQL stored procedure and returns a hashtable. The keys in the table
 * are the column names. The value is a string with all fields in that column
 */

    public static Hashtable execSqlProcedureHash(String server, String sqlStr ) throws IOException {
        imcode.server.IMCPoolInterface imc = getPoolInterface( server ) ;
        try {
            return imc.sqlProcedureHash(sqlStr) ;
        } catch (IOException ex) {
            imc = renewPoolInterface(server) ;
            return imc.sqlProcedureHash(sqlStr) ;
        }
    }


/**
 * Executes a SQL stored procedure and returns an multidimensional string array.
 */

    public static String[][] execProcedureMulti(String server, String sqlStr ) throws IOException {
        imcode.server.IMCPoolInterface imc = getPoolInterface( server ) ;
        try {
            return imc.sqlProcedureMulti(sqlStr) ;
        } catch (IOException ex) {
            imc = renewPoolInterface(server) ;
            return imc.sqlProcedureMulti(sqlStr) ;
        }
    }


    // ******************* TEMPLATE FOLDER FUNCTIONS ****************



/**
 * Returns the physical path to the folder where the templates are located.
 * observer that if janus cant recognize the metaId were sending, it returns the
 * path to its own template catalogue
 */

    public static String getExternalTemplateFolder(String imcServer, int meta_id) throws IOException {
        imcode.server.IMCServiceInterface imc = getInterface( imcServer ) ;
        try {
            return imc.getExternalTemplateFolder(meta_id) ;
        } catch (IOException ex) {
            imc = renewInterface(imcServer) ;
            return imc.getExternalTemplateFolder(meta_id) ;
        }
    }


/**
 * Returns the physical path to the folder where the internal templates are located.
 * The function takes the meta id as argument, or use -1 if no metaid is known
 */

    public static String getInternalTemplateFolder(String imcServer, int meta_id) throws IOException {
        imcode.server.IMCServiceInterface imc = getInterface( imcServer ) ;
        try {
            return imc.getInternalTemplateFolder(meta_id) ;
        } catch (IOException ex) {
            imc = renewInterface(imcServer) ;
            return imc.getInternalTemplateFolder(meta_id) ;
        }
    }


/**
 * Returns the physical path to the folder where the internal templates are located.
 */

    public static String getInternalTemplateFolder(HttpServletRequest req) throws IOException, NumberFormatException {
        String host = req.getHeader("Host") ;
        String imcServer = imcode.util.Utility.getDomainPref("userserver",host) ;
        imcode.server.IMCServiceInterface imc = getInterface( imcServer ) ;
        int aMeta_id  = -1;

        // Lets get the metaid
        String meta_id = req.getParameter("meta_id") ;
        if(meta_id == null )	return "" ;

        try {
            aMeta_id = Integer.parseInt("meta_id") ;
        } catch(NumberFormatException ex) {
            log("NumberFormatException: " + meta_id + " " + ex.getMessage()) ;
            throw new NumberFormatException() ;
        }

        try {
            // Lets get the templatefolder from Janus,
            return imc.getInternalTemplateFolder(aMeta_id) ;
        } catch (IOException ex) {
              imc = renewInterface(imcServer) ;
              return imc.getInternalTemplateFolder(aMeta_id) ;
        }
    }

/**
 * Returns the physical path to the imagefolder of the system.
 * Ex http://dev.imcode.com/images
 */

    public static String getInternalImageFolder(String imcServer) throws IOException {
        imcode.server.IMCServiceInterface imc = getInterface( imcServer ) ;
        try {
            // Lets get the imageTemplateFolder from Janus,
            return imc.getImageHome() ;
        } catch (IOException ex) {
            imc = renewInterface(imcServer) ;
            return imc.getImageHome() ;
        }
    }

/**
 * Returns the logical path to the imagefolder from the servlets folder.
 * Ex: ../images/se/
 */

    public static String getExternalImageFolder(String imcServer, String metaId) throws IOException {
        String theFolder = "" ;
        try {
           // Lets get the ExternalimageTemplateFolder for a certain metaId,
            theFolder = getInternalImageFolder(imcServer) ;
            theFolder += execJanusSqlProcedureStr(imcServer, "GetMetaPathInfo " + metaId) ;

        } catch (IOException ex) {
            ex.printStackTrace();
            throw new IOException(ex.getMessage());
        }
        return theFolder ;
    }


/**
 * Checks whether an user is administrator for a meta id or not, Returns true if the user has
 * rights to administrate the document, and and false if he is not
 */

    public static boolean checkAdminRights(String imcServer, String metaId, imcode.server.User user) throws IOException {
        boolean admin = false ;
        imcode.server.IMCServiceInterface imc = getInterface( imcServer ) ;
        int newMetaId = Integer.parseInt(metaId) ;
        try {
             // Lets check if the user is an admin
            return imc.checkDocAdminRights(newMetaId, user, 65536) ;
        } catch (IOException ex) {
            imc = renewInterface(imcServer) ;
            return imc.checkDocAdminRights(newMetaId, user, 65536) ;
        }
    } // checkAdminRights

/**
 * Checks whether an user has the right to administrate
 **/

    public static boolean checkDocRights(String imcServer, String metaId, imcode.server.User user) throws IOException {

        imcode.server.IMCServiceInterface imc = getInterface( imcServer ) ;
        int newMetaId = Integer.parseInt(metaId) ;
        try {
            // Lets check if the user is an admin
            return imc.checkDocRights(newMetaId, user) ;
        } catch (IOException ex) {
            imc = renewInterface(imcServer) ;
            return imc.checkDocRights(newMetaId, user) ;
        }
    } // checkDocRights

/**
 * Parses a doc on the server.
 */

    public static String parseDoc(String imcServer, String htmlStr, java.util.Vector variables,
    java.util.Vector data ) throws IOException {
        imcode.server.IMCServiceInterface imc = getInterface( imcServer ) ;
        try {
            return imc.parseDoc(htmlStr, variables, data) ;
        } catch (IOException ex) {
            imc = renewInterface(imcServer) ;
            return imc.parseDoc(htmlStr, variables, data) ;
        }
    } // parseDoc


/**
 * Parses a doc on the server.
 */

    public static String parseDoc(String imcServer, String htmlStr, java.util.Vector variables )
    throws IOException {
        imcode.server.IMCServiceInterface imc = getInterface( imcServer ) ;
        try {
            // Lets parse
            return imc.parseDoc(htmlStr, variables) ;
        } catch (IOException ex) {
            renewInterface(imcServer) ;
            return imc.parseDoc(htmlStr, variables) ;
        }
    } // parseDoc

    // *********************** CONFERENCE URL'S *****************************
/**
 * Returns the conferences from the host.cfg file.
 */

    public static String getLoginUrl( String host) {
        try {
            return imcode.util.Utility.getDomainPref("start_url",host) ;
        } catch(IOException e) {
            log("Cant retrieve start_url preference: " + e.getMessage()) ;
            return "Error: " + e.getMessage() ;
        }
    } // end getLoginUrl

/**
 * Returns the name on the file used to login to the conference from.
 * Takes the property from the host config file.
 * Example. magnumsoft.properties
 */

    public static String getLoginPage(String host) {
        // Lets get the path to the diagramfiles
        // String host = req.getHeader("Host") ;
        try {
            return imcode.util.Utility.getDomainPref("login_page",host) ;
        } catch(IOException e) {
             log("Cant retrieve login_page preference: " + e.getMessage()) ;
            return "Error: " + e.getMessage() ;
        }
    } // end getLoginPage

/**
 * Returns the imageHomefolder. Takes the property image_path
 * from the host config file.
 * Example: D:\apache\
 **/

    public static String getImageHomeFolder(String host ) {
        try {
            return imcode.util.Utility.getDomainPref("image_path",host) ;
        } catch(IOException e) {
            log("Cant retrieve image_path preference: " + e.getMessage()) ;
             return "Error: " + e.getMessage() ;
        }
    } // end getImageFolder

/**
 * Returns the imageHomefolder from the imcserver.cfg file.
 * Example :D:\apache\
 */

    public static String getExternalImageHomeFolder(String host, String imcServer, String metaId ) {

        String imageFolder = getImageHomeFolder(host) ;
        imageFolder += getLanguage(imcServer, metaId) + "/";
        imageFolder += getDocType(imcServer, metaId) + "/" ;
        return imageFolder ;
    } // end getExternalImageHomeFolder


/**
 * Returns the langprefix for a meta id, for example "SE" for swedish
 */

    public static String getLanguage(String imcServer, String metaId) {
        String str = "" ;
        try {
            str = execJanusSqlProcedureStr(imcServer, "GetLangPrefix " + metaId ) ;
        } catch( IOException e) {
            log("Error: GetLanguage. " + e.getMessage()) ;
            return str ;
        }
        return str ;
    }

/**
 * Returns the doctype for a meta id, for example 102 for Conference
 */

    public static String getDocType(String imcServer, String metaId) {
        String str = "" ;
        try {
            str = execJanusSqlProcedureStr(imcServer, "GetDocType " + metaId ) ;
        } catch( Exception e) {
            return str ;
        }
        return str ;
    }


    // ******************* HELP METHODS  **********************
/**
 * Log. logs to the err file
 */
    private static void log( String str) {
        System.err.println("RmiConf: " + str ) ;
    }

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
            catch (Exception ex) {
            }
        }
        return n;
    }



} // End class




