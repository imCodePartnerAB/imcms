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
		  Notice the lack of any method declaration
	*/

 	static {
    	interfaces = new Hashtable() ;
      paths = new Hashtable() ;
	}

	/**
		Constructor
	**/
	public RmiConf( imcode.server.User aUser) {
		this.user = aUser ;
	}

	/**
		Constructor
	**/
	public RmiConf() {

	}

/**
	GetInterface. Returns an interface to the host db. The JanusDB
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
	RenewInterface. Returns a renewed interface towards the host DB
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

/**
	GetInterface. Returns an interface to the host db. The JanusDB
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
	RenewInterface. Returns a renewed interface towards the host DB
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
	Executes a READ SQL stored procedure and returns a string
**/

public static String execJanusSqlProcedureStr(String server, String sqlStr ) throws ServletException {
  try {
 		imcode.server.IMCServiceInterface imc = getInterface( server ) ;
	  return imc.sqlProcedureStr(sqlStr) ;
  } catch (Exception ex) {
    ex.printStackTrace();
    throw new ServletException(ex.getMessage());
 	}
} // execSql


/**
	Executes a READ SQL stored procedure and returns a String array
*/

public static String[] execJanusSqlProcedure(String server, String sqlStr ) throws ServletException {
 	try {
		imcode.server.IMCServiceInterface imcJanusDB = getInterface( server ) ;
	 	return imcJanusDB.sqlProcedure(sqlStr) ;
  } catch (Exception ex) {
      ex.printStackTrace();
      throw new ServletException(ex.getMessage());
  }
}

/**
	Executes a READ SQL stored procedure and returns a String array with the
	following syntax.
	In the first position, a string with the (n) nbr of fields each records has.
	In the n following positions, the name of the columns in the sql query will be.
	Position [0] = n nbr of columns
	Position [1- n] = Metadata, the names of the columns
	Position [n + 1] = Data

	Example: The stored procedure returns 3 rows with for each row, 3 columns named
	name, age, length
	[3, name, age, length, Kalle, 20, 1.80, Pelle, 30, 1.90, Nisse, 40, 1.60]
*/

public static String[] execJanusSqlProcedureExt(String server, String sqlStr ) throws ServletException {
	try {
		imcode.server.IMCServiceInterface imcJanusDB = getInterface( server ) ;
   	return imcJanusDB.sqlProcedureExt(sqlStr) ;
  } catch (Exception ex) {
      ex.printStackTrace();
			throw new ServletException(ex.getMessage());
	}
} // execSql


/**
	Executes an stored procedure on the database.
	Updates or WRITE statement on the database without any returnvalue
*/

public static void execJanusSqlUpdateProcedure(String server, String sqlStr ) throws ServletException {
 	try {
 		imcode.server.IMCServiceInterface imcJanusDB = getInterface( server ) ;
   	imcJanusDB.sqlUpdateProcedure(sqlStr) ;
   	return ;
	} catch (Exception ex) {
        ex.printStackTrace();
       throw new ServletException(ex.getMessage());
	}
}

/**
	Executes a SQL statement and returns a hashtable
	Ex. "select * from myTable"
*/

public static Hashtable execJanusQueryHash(String server, String sqlStr ) throws ServletException {
 	try {
		imcode.server.IMCServiceInterface imcJanusDB = getInterface( server ) ;
		 	return imcJanusDB.sqlQueryHash(sqlStr) ;
	} catch (Exception ex) {
        ex.printStackTrace();
        throw new ServletException(ex.getMessage());
	}
}

/**
	Executes a SQL stored procedure and returns a hashtable. The keys in the table
	are the column names. The value is a string with all fields in that column
*/

public static Hashtable execJanusProcedureHash(String server, String sqlStr ) throws ServletException {
 	try {
		imcode.server.IMCServiceInterface imcJanusDB = getInterface( server ) ;
		 	return imcJanusDB.sqlProcedureHash(sqlStr) ;
 		} catch (Exception ex) {
        ex.printStackTrace();
        throw new ServletException(ex.getMessage());
		}
}

/**
	Executes a SQL stored procedure and returns an multidimensional string array.

*/
public static String[][] execJanusProcedureMulti(String server, String sqlStr ) throws ServletException {
 	try {
		imcode.server.IMCServiceInterface imcJanusDB = getInterface( server ) ;
	 	return imcJanusDB.sqlProcedureMulti(sqlStr) ;
	} catch (Exception ex) {
       ex.printStackTrace();
       throw new ServletException(ex.getMessage());
	}
}


/***
	Activates a child in the janus system. default value in Janus is inactivated
*/

	public void activateChild(String server, String meta_id ) throws ServletException {
 		try {
 			imcode.server.IMCServiceInterface imcJanusDB = getInterface( server ) ;
   		imcJanusDB.activateChild(Integer.parseInt(meta_id), user) ;
  	} catch (Exception ex) {
      ex.printStackTrace();
			throw new ServletException(ex.getMessage());
   	}
} // activateChild


// ****** CALLS TO THE CONFERENCE DB *****************

/***
	Executes a READ SQL stored procedure and returns a string
**/

public static String execSqlProcedureStr(String server, String sqlStr ) throws ServletException {
 	try {
	  imcode.server.IMCPoolInterface imc = getPoolInterface( server ) ;
	  return imc.sqlProcedureStr(sqlStr) ;
  } catch (Exception ex) {
    ex.printStackTrace();
    throw new ServletException(ex.getMessage());
 	}
} // execSql

/**
	Executes a READ SQL stored procedure and returns a String array
**/

public static String[] execSqlProcedure(String server, String sqlStr ) throws ServletException {
	try {
	  imcode.server.IMCPoolInterface imc = getPoolInterface( server ) ;
  	String[] answer = imc.sqlProcedure(sqlStr) ;
   	return answer ;
 	} catch (Exception ex) {
       ex.printStackTrace();
       throw new ServletException(ex.getMessage());
 	}
}


/**
	Executes a READ SQL stored procedure and returns a String array with the
	following syntax.
	In the first position, a string with the (n) nbr of fields each records has.
	In the n following positions, the name of the columns in the sql query will be.
	Position [0] = n nbr of columns
	Position [1- n] = Metadata, the names of the columns
	Position [n + 1] = Data

	Example: The stored procedure returns 3 rows with for each row, 3 columns named
	name, age, length
	[3, name, age, length, Kalle, 20, 1.80, Pelle, 30, 1.90, Nisse, 40, 1.60]
*/

public static String[] execSqlProcedureExt(String server, String sqlStr ) throws ServletException {
  try {
    imcode.server.IMCPoolInterface imc = getPoolInterface( server ) ;
    return imc.sqlProcedureExt(sqlStr) ;
  } catch (java.rmi.RemoteException ex) {
      log(ex.getMessage()) ;
      ex.printStackTrace();
      throw new ServletException(ex.getMessage());
  } catch (java.io.IOException ex) {
     log(ex.getMessage()) ;
     ex.printStackTrace();
     throw new ServletException(ex.getMessage());
  }
} // execSql


/**
	Executes an stored procedure on the database.
	Updates or WRITE statement on the database without any returnvalue
*/

public static void execSqlUpdateProcedure(String server, String sqlStr ) throws ServletException {
 	 try {
 	 	  imcode.server.IMCPoolInterface imc = getPoolInterface( server ) ;
 	   	imc.sqlUpdateProcedure(sqlStr) ;
    	return ;
		} catch (Exception ex) {
        ex.printStackTrace();
        throw new ServletException(ex.getMessage());
   	}
}

/**
	Executes a SQL statement and returns a string
*/

public static String execSqlQueryStr(String server, String sqlStr ) throws ServletException {
	try {
 	 	  imcode.server.IMCPoolInterface imc = getPoolInterface( server ) ;
    	return imc.sqlQueryStr(sqlStr) ;
  } catch (Exception ex) {
       ex.printStackTrace();
       throw new ServletException(ex.getMessage());
	}
} // execSql


/**
	Executes a SQL statement and returns a string array
	Ex. "select * from myTable"
*/

public static String[] execSqlQuery(String server, String sqlStr ) throws ServletException {
	try {
 	 	 imcode.server.IMCPoolInterface imc = getPoolInterface( server ) ;
     return imc.sqlQuery(sqlStr) ;
	} catch (Exception ex) {
      ex.printStackTrace();
      throw new ServletException(ex.getMessage());
	}
}


/**
	Executes a SQL statement and returns a string array
	Ex. "select * from myTable"	following syntax.
	In the first position, a string with the (n) nbr of fields each records has.
	In the n following positions, the name of the columns in the sql query will be.
	Example: The stored procedure returns 3 rows with for each row, 3 columns named
	name, age, length
	Executes a READ SQL stored procedure and returns a String array with the
	following syntax.
	In the first position, a string with the (n) nbr of fields each records has.
	In the n following positions, the name of the columns in the sql query will be.
	Position [0] = n nbr of columns
	Position [1- n] = Metadata, the names of the columns
	Position [n + 1] = Data

	3, name, age, length, Kalle, 20, 1.80, Pelle, 30, 1.90, Nisse, 40, 1.60
*/

public static String[] execSqlQueryExt(String server, String sqlStr ) throws ServletException {
		try {
 	 	  imcode.server.IMCPoolInterface imc = getPoolInterface( server ) ;
                  return imc.sqlQueryExt(sqlStr) ;
 		} catch (Exception ex) {
     		ex.printStackTrace();
     		throw new ServletException(ex.getMessage());
 	 	}
}


/**
	Executes a SQL statement without any returnvalue
*/

public static void execSqlUpdateQuery(String server, String sqlStr ) throws ServletException {
 		try {
 		  imcode.server.IMCPoolInterface imc = getPoolInterface( server ) ;
 			imc.sqlUpdateQuery(sqlStr) ;
    	return ;
		} catch (Exception ex) {
        ex.printStackTrace();
        throw new ServletException(ex.getMessage());
		}
}

/**
	Executes a SQL statement and returns a hashtable
	Ex. "select * from myTable"
*/

public static Hashtable execSqlQueryHash(String server, String sqlStr ) throws ServletException {
	try {
	  	imcode.server.IMCPoolInterface imc = getPoolInterface( server ) ;
	 		return imc.sqlQueryHash(sqlStr) ;
 	} catch (Exception ex) {
       ex.printStackTrace();
       throw new ServletException(ex.getMessage());
  }
}

/**
	Executes a SQL stored procedure and returns a hashtable. The keys in the table
	are the column names. The value is a string with all fields in that column
*/

public static Hashtable execSqlProcedureHash(String server, String sqlStr ) throws ServletException {
	try {
	  imcode.server.IMCPoolInterface imcConferenceDB = getPoolInterface( server ) ;
		return imcConferenceDB.sqlProcedureHash(sqlStr) ;
  } catch (Exception ex) {
       ex.printStackTrace();
       throw new ServletException(ex.getMessage());
  }
}


/**
	Executes a SQL stored procedure and returns an multidimensional string array.
*/

public static String[][] execProcedureMulti(String server, String sqlStr ) throws ServletException {
	try {
	  imcode.server.IMCPoolInterface imcConferenceDB = getPoolInterface( server ) ;
	 	return imcConferenceDB.sqlProcedureMulti(sqlStr) ;
  } catch (Exception ex) {
       ex.printStackTrace();
       throw new ServletException(ex.getMessage());
  }
}


// ******************* TEMPLATE FOLDER FUNCTIONS ****************



/**
	Returns the physical path to the folder where the templates are located.
	observer that if janus cant recognize the metaId were sending, it returns the
  path to its own template catalogue
*/

public static String getExternalTemplateFolder(String imcServer, int meta_id) throws ServletException {
  try {
  		imcode.server.IMCServiceInterface imcJanusDB = getInterface( imcServer ) ;
    	return imcJanusDB.getExternalTemplateFolder(meta_id) ;
		} catch (Exception ex) {
       ex.printStackTrace();
       throw new ServletException(ex.getMessage());
 		}
}


/**
	Returns the physical path to the folder where the internal templates are located.
	The function takes the meta id as argument, or use -1 if no metaid is known
*/

public static String getInternalTemplateFolder(String imcServer, int meta_id) throws ServletException {
	try {
		imcode.server.IMCServiceInterface imcJanusDB = getInterface( imcServer ) ;
    return imcJanusDB.getInternalTemplateFolder(meta_id) ;
	} catch (Exception ex) {
       ex.printStackTrace();
       throw new ServletException(ex.getMessage());
 	}
}


/**
	Returns the physical path to the folder where the internal templates are located.
*/

public static String getInternalTemplateFolder(HttpServletRequest req) throws ServletException {
  try {
 		String host = req.getHeader("Host") ;
  	String imcServer = imcode.util.Utility.getDomainPref("userserver",host) ;
  	imcode.server.IMCServiceInterface imcJanusDB = getInterface( imcServer ) ;

  // Lets get the metaid
   	String meta_id = req.getParameter("meta_id") ;
		if(meta_id == null )	return "" ;
		int aMeta_id = Integer.parseInt("meta_id") ;

  // Lets get the templatefolder from Janus,
   	return imcJanusDB.getInternalTemplateFolder(aMeta_id) ;

		} catch (Exception ex) {
       ex.printStackTrace();
       throw new ServletException(ex.getMessage());
  	}
}

/**
	Returns the physical path to the imagefolder of the system.
	Ex http://dev.imcode.com/images
*/

public static String getInternalImageFolder(String imcServer) throws ServletException {
	try {
		imcode.server.IMCServiceInterface imcJanusDB = getInterface( imcServer ) ;

    // Lets get the imageTemplateFolder from Janus,
    return imcJanusDB.getImageHome() ;
	} catch (Exception ex) {
       ex.printStackTrace();
       throw new ServletException(ex.getMessage());
 	}
}

/**
	Returns the logical path to the imagefolder from the servlets folder.
	Ex: ../images/se/
*/

public static String getExternalImageFolder(String imcServer, String metaId) throws ServletException {
	String theFolder = "" ;
  try {
 //  	IMCServiceInterface imcJanusDB = getInterface( imcServer ) ;

  // Lets get the ExternalimageTemplateFolder for a certain metaId,
    theFolder = getInternalImageFolder(imcServer) ;
		theFolder += execJanusSqlProcedureStr(imcServer, "GetMetaPathInfo " + metaId) ;

	} catch (Exception ex) {
       ex.printStackTrace();
       throw new ServletException(ex.getMessage());
   	}
 	return theFolder ;
}


/**
	Checks whether an user is administrator for a meta id or not, Returns true if the user has
	rights to administrate the document, and and false if he is not
*/

public static boolean checkAdminRights(String imcServer, String metaId, imcode.server.User user) throws ServletException {
	boolean admin = false ;
	try {
	 	imcode.server.IMCServiceInterface imcJanusDB = getInterface( imcServer ) ;

	// Lets check if the user is an admin
		int newMetaId = Integer.parseInt(metaId) ;
		admin = imcJanusDB.checkDocAdminRights(newMetaId, user, 65536) ;
		return admin ;
	} catch (Exception ex) {
       ex.printStackTrace();
       throw new ServletException(ex.getMessage());
	}
} // checkAdminRights

/**
	Checks whether an user has the right to administrate
**/

public static boolean checkDocRights(String imcServer, String metaId, imcode.server.User user) throws ServletException {

	try {
	 	imcode.server.IMCServiceInterface imcJanusDB = getInterface( imcServer ) ;
	// Lets check if the user is an admin
		int newMetaId = Integer.parseInt(metaId) ;
		return imcJanusDB.checkDocRights(newMetaId, user) ;
	} catch (Exception ex) {
        ex.printStackTrace();
        throw new ServletException(ex.getMessage());
	}
} // checkDocRights

/**
	Parses a doc on the server.
*/

public static String parseDoc(String imcServer, String htmlStr, java.util.Vector variables,
	java.util.Vector data ) throws ServletException {

  try {
   	imcode.server.IMCServiceInterface imcJanusDB = getInterface( imcServer ) ;
   	return imcJanusDB.parseDoc(htmlStr, variables, data) ;
	 } catch (Exception ex) {
       ex.printStackTrace();
       throw new ServletException(ex.getMessage());
 	}
} // parseDoc



/**
	Parses a doc on the server.
*/

public static String parseDoc(String imcServer, String htmlStr, java.util.Vector variables )
	throws ServletException {

  try {
   	imcode.server.IMCServiceInterface imcJanusDB = getInterface( imcServer ) ;

  // Lets parse
    	return imcJanusDB.parseDoc(htmlStr, variables) ;
 	} catch (Exception ex) {
      ex.printStackTrace();
      throw new ServletException(ex.getMessage());
  }
} // parseDoc



/**
	Reads an Imcode templatefile and interpret IMSCRIPT and output a HTML String obj.

*/

/*public String interpretTemplate(String imcServer, int meta_id ) throws ServletException {

 try {
	 	IMCServiceInterface imcJanusDB = getInterface( imcServer ) ;

		 // Lets parse the document
		return imcJanusDB.interpretTemplate(meta_id, user) ;
	} catch (Exception ex) {
        ex.printStackTrace();
        throw new ServletException(ex.getMessage());
  }
} // parseDoc
*/

// *********************** CONFERENCE URL'S *****************************
/**
	Returns the conferences from the host.cfg file.
*/

 public static String getLoginUrl( String host) {
  try {
    //String host = req.getHeader("Host") ;
    // log("start_url: " + imcode.util.Utility.getDomainPref("start_url",host)) ;
    return imcode.util.Utility.getDomainPref("start_url",host) ;
  } catch(Exception e) {
      log("Error in the getLoginUrl method") ;
      return "Error" ;
  }
 		//return getPrefs("start_url" , "CONFERENCE.CFG" ) ;
 } // end getLoginUrl

/**
	Returns the name on the file used to login to the conference from.
  Takes the property from the host config file.
  Example. magnumsoft.properties
*/

 public static String getLoginPage(String host) {
	 // Lets get the path to the diagramfiles
 		// String host = req.getHeader("Host") ;
 		try {
 			return imcode.util.Utility.getDomainPref("login_page",host) ;
    } catch(Exception e) {
			log("Error in the getLoginUrl method") ;
 			return "Error" ;
  	}
 } // end getLoginPage

/**
	Returns the imageHomefolder. Takes the property image_path
  from the host config file.
	Example: D:\apache\
**/

 public static String getImageHomeFolder(String host ) {
 		try {
 			return imcode.util.Utility.getDomainPref("image_path",host) ;
 		} catch(Exception e) {
 			log("Error in the getImageHomeFolder method") ;
 			return "Error" ;
 		}
} // end getImageFolder

/**
	Returns the imageHomefolder from the imcserver.cfg file.
	Example :D:\apache\
*/

 public static String getExternalImageHomeFolder(String host, String imcServer, String metaId ) {

 		String imageFolder = getImageHomeFolder(host) ;
		imageFolder += getLanguage(imcServer, metaId) + "/";
		imageFolder += getDocType(imcServer, metaId) + "/" ;
		return imageFolder ;
 } // end getExternalImageHomeFolder


/**
	Returns the langprefix for a meta id, for example "SE" for swedish
*/

public static String getLanguage(String imcServer, String metaId) {
	String str = "" ;
	try {
		str = execJanusSqlProcedureStr(imcServer, "GetLangPrefix " + metaId ) ;
	} catch( Exception e) {
			return str ;
	}
	return str ;
}

/**
	Returns the doctype for a meta id, for example 102 for Conference
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

/**
	Returns a logical name of the imagefolder from the imcserver.cfg file.
	This function is caching the imagepath for each server.
	Example /../images/

  Example on the argument host: apache.magnumsoft.se
*/

public static String getImageFolder(String host) {
 	try {
  	return getImageFolderPath(host) ;
  } catch(Exception e) {
	 		log("Error in getImageFolder") ;
      return "Error" ;
  }

  /*
  try {
    JanusPrefs prop = new JanusPrefs() ;
  	prop.loadConfig("imcserver.cfg") ;
    String serverNo = prop.getProperty(host) ;
  	return prop.getProperty(serverNo + "." + "image_folder") ;
  } catch(Exception e) {
  		log("Error in getImageFolder") ;
      return "Error" ;
  }
  */
} // end getImageFolder

// *************** FUNCTIONS TO CACHE THE PATHS ***************'


/**
	GetInterface. Returns an interface to the host db. The JanusDB
*/
	private static String getImageFolderPath(String server) throws IOException {
		if ( server == null ) {
    	log("Invalid server argument") ;
			throw new IllegalArgumentException("Server == null") ;
		}

		String str = (String) paths.get(server) ;
		if (str == null) {
			str = lookForImageFolder(server) ;
		}
		return str;
	}


/**
	RenewInterface. Returns a renewed interface towards the host DB
*/

	private static String lookForImageFolder(String server) throws IOException {
		if ( server == null ) {
			throw new IllegalArgumentException("Server == null") ;
		}

  // Lets look in the file for the path
    String aPathFromFile = "" ;
    try {
      JanusPrefs prop = new JanusPrefs() ;
    	prop.loadConfig("imcserver.cfg") ;
      String serverNo = prop.getProperty(server) ;
    	aPathFromFile = prop.getProperty(serverNo + "." + "image_folder") ;
    } catch(Exception e) {
    		log("Error in getImageFolder") ;
        //return "Error" ;
    }

 		paths.put(server, aPathFromFile) ;
		return aPathFromFile ;
	}

// ******************* HELP METHODS  **********************
/**
	Log. logs to the err file
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




