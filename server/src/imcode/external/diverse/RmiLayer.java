package imcode.external.diverse ;
import java.util.*;
import java.rmi.*;
import java.rmi.registry.*;
import javax.servlet.*;
import javax.servlet.http.*;
import imcode.server.* ;
import java.io.* ;
import java.net.* ;
import imcode.util.* ;
import imcode.server.* ;

public class RmiLayer {
	imcode.server.User user ;
	static Hashtable interfaces ;	// Keeps track of servers. "ip:port"=interface

	static {
		interfaces = new Hashtable() ;
	}

	public RmiLayer( imcode.server.User user) {
     this.user = user ;
	}

/**
	GetInterface.
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
	RenewInterface.
**/

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



// ****** HERE IS THE CHILD ACTIVATION FUNCTIONS *********************

/***
	Activates a child in the janus system. default value in Janus is inactivated


	public void removeChild( String server, int meta_id, int parent_meta_id, User user ) throws IOException {
		IMCServiceInterface imc = getInterface( server ) ;
		try {
			imc.removeChild(meta_id,parent_meta_id,user) ;
		} catch ( IOException ex ) {
			imc = renewInterface(server) ;
			imc.removeChild(meta_id,parent_meta_id,user) ;
		}
	}
**/


public void activateChild(String server, String meta_id ) throws ServletException, IOException {
  try {
	  imcode.server.IMCServiceInterface imc = getInterface( server ) ;
 		imc.activateChild(Integer.parseInt(meta_id), user) ;
    return ;
 	} catch (Exception ex) {
      ex.printStackTrace();
      throw new ServletException(ex.getMessage());
   }
} // activateChild


/***
	Inactivates a child in the janu system. default value in Janus is inactivated.
	This func makes the child invisible in the Janus System
*/

public void inActivateChild(String server, String meta_id) throws ServletException {
  try {
  	imcode.server.IMCServiceInterface imc = getInterface( server ) ;
    imc.inActiveChild(Integer.parseInt(meta_id), user) ;
    return ;
 	} catch (Exception ex) {
      ex.printStackTrace();
      throw new ServletException(ex.getMessage());
   }
} // inActivateChild


// ************* DELETE METAID *************

/**
	Deletes a meta id in the db
*/

public boolean deleteDocAll(String server, int metaId) {
 try {
  	imcode.server.IMCServiceInterface imc = getInterface( server ) ;
    imc.deleteDocAll(metaId, this.user) ;
		return true ;
 	} catch (Exception ex) {
      ex.printStackTrace();
      return false ;
  }
}

// ************* COUNTER FUNCTIONS *************

/**
	Sets the counter on janus. returns the newValue the counter obj.
**/

public boolean setCounter(String server, int value) throws ServletException {
	try {
	 	imcode.server.IMCServiceInterface imc = getInterface( server ) ;
	  int newValue = imc.setCounter(value) ;
		return true ;
		} catch (Exception ex) {
	     ex.printStackTrace();
	     throw new ServletException(ex.getMessage());
	 }
 }

/**
	Increases the counter on janus with 1. returns the newValue the counter obj.
*/

public int incCounter(String server) throws ServletException {
	try {
	 	imcode.server.IMCServiceInterface imc = getInterface( server ) ;
	  int newValue = imc.incCounter() ;
		return newValue ;
		} catch (Exception ex) {
	     ex.printStackTrace();
	     throw new ServletException(ex.getMessage());
	 }

/*

	try {
      JanusPrefs prop = new JanusPrefs() ;
      prop.loadConfig("rmi.cfg") ;
    	Registry reg = LocateRegistry.getRegistry(prop.getServerIP() ,prop.getServerPort()) ;
    	prop = null ;
    	imcode.server.IMCServiceInterface imc =
        (imcode.server.IMCServiceInterface)reg.lookup("IMCService") ;
     	int newValue = imc.incCounter() ;
			imc = null ;
			return newValue ;
	} catch (Exception ex) {
       ex.printStackTrace();
       throw new ServletException(ex.getMessage());
  	}
*/
 	}



/**
	Returns the current counter value
*/

public int getCounter(String server) throws ServletException {
	try {
	 	imcode.server.IMCServiceInterface imc = getInterface( server ) ;
	 	int newValue = imc.getCounter() ;
		return newValue ;
	} catch (Exception ex) {
	     ex.printStackTrace();
	     throw new ServletException(ex.getMessage());
	}
/*
	try {
      JanusPrefs prop = new JanusPrefs() ;
      prop.loadConfig("rmi.cfg") ;
    	Registry reg = LocateRegistry.getRegistry(prop.getServerIP() ,prop.getServerPort()) ;
    	prop = null ;
    	imcode.server.IMCServiceInterface imc =
        (imcode.server.IMCServiceInterface)reg.lookup("IMCService") ;
     	int newValue = imc.getCounter() ;
			imc = null ;
			return newValue ;
	} catch (Exception ex) {
       ex.printStackTrace();
       throw new ServletException(ex.getMessage());
  	} */
 	}


/**
	Sets the counterdate on janus
*/

public boolean setCounterDate(String server, String date) throws ServletException {
/*	try {
      JanusPrefs prop = new JanusPrefs() ;
      prop.loadConfig("rmi.cfg") ;
    	Registry reg = LocateRegistry.getRegistry(prop.getServerIP() ,prop.getServerPort()) ;
    	prop = null ;
    	imcode.server.IMCServiceInterface imc =
        (imcode.server.IMCServiceInterface)reg.lookup("IMCService") ;
     	imc.setCounterDate(date) ;
			imc = null ;
	} catch (Exception ex) {
       ex.printStackTrace();
       throw new ServletException(ex.getMessage());
   }
   */
   try {
    	imcode.server.IMCServiceInterface imc = getInterface( server ) ;
    	imc.setCounterDate(date) ;
   } catch (Exception ex) {
       ex.printStackTrace();
       throw new ServletException(ex.getMessage());
   }
   return true;
}

/**
	Sets the counterdate on janus
*/

public String getCounterDate(String server) throws ServletException {
/*	try {
      JanusPrefs prop = new JanusPrefs() ;
      prop.loadConfig("rmi.cfg") ;
    	Registry reg = LocateRegistry.getRegistry(prop.getServerIP() ,prop.getServerPort()) ;
    	prop = null ;
    	imcode.server.IMCServiceInterface imc =
        (imcode.server.IMCServiceInterface)reg.lookup("IMCService") ;
     	String date  = imc.getCounterDate() ;
			imc = null ;
			return date ;
	} catch (Exception ex) {
       ex.printStackTrace();
       throw new ServletException(ex.getMessage());
   }
   */
  try {
   	imcode.server.IMCServiceInterface imc = getInterface( server ) ;
   	String date  = imc.getCounterDate() ;
		return date ;
  } catch (Exception ex) {
      ex.printStackTrace();
      throw new ServletException(ex.getMessage());
  }

 	}


/***************************	END COUNTER FUNCTIONS *********


// ******************* TEMPLATE FOLDER FUNCTIONS ****************
/**
	Returns the physical path to the folder where the templates are located.
	observer that if janus cant recognize the metaId were sending, it returns the
  path to its own template catalogue
*/

public static String getExternalTemplateFolder(String server, int meta_id) throws IOException {
		String theFolder = "" ;
    imcode.server.IMCServiceInterface imc = getInterface( server ) ;
  	try {
   		theFolder = imc.getExternalTemplateFolder(meta_id) ;
  	} catch (IOException ex) {
	    	imc = renewInterface(server) ;
				theFolder = imc.getExternalTemplateFolder(meta_id) ;
  	}
  	return theFolder ;
}

/**
	Returns the physical path to the folder where the internal templates are located.
*/

public static String getInternalTemplateFolder(String server) throws IOException {
	String theFolder = "" ;
  imcode.server.IMCServiceInterface imc = getInterface( server ) ;
  try {
 // Lets get the templatefolder from Janus,
     	theFolder = imc.getInternalTemplateFolder(-1) ;
    } catch (IOException ex) {
    		imc = renewInterface(server) ;
        theFolder = imc.getInternalTemplateFolder(-1) ;
        //ex.printStackTrace();
        //throw new ServletException(ex.getMessage());
    }
 	return theFolder ;
}


/**
	Returns the physical path to the folder where the internal templates are located.
	The function takes the meta id as argument, or use -1 if no metaid is known
*/

public static String getInternalTemplateFolder(String server, int meta_id) throws IOException {
	String theFolder = "" ;
	imcode.server.IMCServiceInterface imc = getInterface( server ) ;
  try {

 // Lets get the templatefolder from Janus,
     	theFolder = imc.getInternalTemplateFolder(meta_id) ;
    } catch (IOException ex) {
   			imc = renewInterface(server) ;
        theFolder = imc.getInternalTemplateFolder(meta_id) ;
    }
 	return theFolder ;
}


/**
	Returns the physical path to the folder where the internal templates are located.
*/

public static String getInternalTemplateFolder(HttpServletRequest req) throws IOException {

  	String theFolder = "" ;
  // Lets get the server information from request object
   	String host 				= req.getHeader("Host") ;
   	String server 			= imcode.util.Utility.getDomainPref("userserver",host) ;

  // Lets get the metaid and convert it to an int
    String meta_id = req.getParameter("meta_id") ;
		if(meta_id == null ) return "" ;
		int aMeta_id ;
  	try {
  		aMeta_id = Integer.parseInt("meta_id") ;
  	} catch(NumberFormatException e) {
    	return "" ;
    }

  	imcode.server.IMCServiceInterface imc = getInterface( server ) ;

  	try {
    // Lets get the templatefolder from Janus,
     	theFolder = imc.getInternalTemplateFolder(aMeta_id) ;

    } catch (IOException ex) {
        theFolder = imc.getInternalTemplateFolder(aMeta_id) ;
    }
 	return theFolder ;
}



/**
	Checks whether an user is administrator or not
*/

public static boolean checkAdminRights(String server, String metaId, imcode.server.User user) throws ServletException {
/*
 try {
      JanusPrefs prop = new JanusPrefs() ;
      prop.loadConfig("rmi.cfg") ;
    	Registry reg = LocateRegistry.getRegistry(prop.getServerIP() ,prop.getServerPort()) ;
    	prop = null ;
    	imcode.server.IMCServiceInterface imc =
        (imcode.server.IMCServiceInterface)reg.lookup("IMCService") ;

			int newMetaId = Integer.parseInt(metaId) ;

    // Lets check if the user is an admin
 			boolean admin = imc.userIsAdmin(newMetaId, user) ;
			imc = null ;
			return admin ;
 } catch (Exception ex) {
        ex.printStackTrace();
        throw new ServletException(ex.getMessage());
   }
 */

  try {
  	imcode.server.IMCServiceInterface imc = getInterface( server ) ;
		int newMetaId = Integer.parseInt(metaId) ;

  // Lets check if the user is an admin
 		boolean admin = imc.checkDocAdminRights(newMetaId, user, 65536) ;
		return admin ;
  } catch (Exception ex) {
     ex.printStackTrace();
     throw new ServletException(ex.getMessage());
  }

} // checkAdminRights



/**
	Parses a doc on the server.
**/

public String parseDoc(String server, String htmlStr, java.util.Vector variables,
	java.util.Vector data ) throws ServletException {
/*
 try {
      JanusPrefs prop = new JanusPrefs() ;
      prop.loadConfig("rmi.cfg") ;
    	Registry reg = LocateRegistry.getRegistry(prop.getServerIP() ,prop.getServerPort()) ;
    	prop = null ;
    	imcode.server.IMCServiceInterface imc =
        (imcode.server.IMCServiceInterface)reg.lookup("IMCService") ;


      // Lets parse

    	String anHtmlStr = imc.parseDoc(htmlStr, variables, data) ;
    	imc = null ;
    	return anHtmlStr ;

 } catch (Exception ex) {
        ex.printStackTrace();
        throw new ServletException(ex.getMessage());
   }
 */

 try {
 		imcode.server.IMCServiceInterface imc = getInterface( server ) ;

	// Lets parse
   	String anHtmlStr = imc.parseDoc(htmlStr, variables, data) ;
   	return anHtmlStr ;
 	} catch (Exception ex) {
    ex.printStackTrace();
    throw new ServletException(ex.getMessage());
 	}

} // parseDoc



/**
	Parses a doc on the server.
*/

public String parseDoc(String server, String htmlStr, java.util.Vector variables )
	throws ServletException {
/*
 try {
      JanusPrefs prop = new JanusPrefs() ;
      prop.loadConfig("rmi.cfg") ;
    	Registry reg = LocateRegistry.getRegistry(prop.getServerIP() ,prop.getServerPort()) ;
    	prop = null ;
    	imcode.server.IMCServiceInterface imc =
        (imcode.server.IMCServiceInterface)reg.lookup("IMCService") ;


      // Lets parse
    	String anHtmlStr = imc.parseDoc(htmlStr, variables) ;
    	imc = null ;
    	return anHtmlStr ;

 } catch (Exception ex) {
        ex.printStackTrace();
        throw new ServletException(ex.getMessage());
   }
 */
 try {
 		imcode.server.IMCServiceInterface imc = getInterface( server ) ;

	 // Lets parse
    	String anHtmlStr = imc.parseDoc(htmlStr, variables) ;
    	return anHtmlStr ;
 	} catch (Exception ex) {
    ex.printStackTrace();
    throw new ServletException(ex.getMessage());
 	}
} // parseDoc



/**
	Reads an Imcode templatefile and interpret IMSCRIPT and output a HTML String obj.
*/
/*
public String interpretTemplate(String server,int meta_id ) throws ServletException {
/*
 try {
      JanusPrefs prop = new JanusPrefs() ;
      prop.loadConfig("rmi.cfg") ;
    	Registry reg = LocateRegistry.getRegistry(prop.getServerIP() ,prop.getServerPort()) ;
    	prop = null ;
    	imcode.server.IMCServiceInterface imc =
        (imcode.server.IMCServiceInterface)reg.lookup("IMCService") ;


    // Lets parse the document
    	String anHtmlStr = imc.interpretTemplate(meta_id, user) ;
    	imc = null ;
    	return anHtmlStr ;

 } catch (Exception ex) {
        ex.printStackTrace();
        throw new ServletException(ex.getMessage());
   }
 */
/*  try {
 		IMCServiceInterface imc = getInterface( server ) ;

	 // Lets parse the document
    	String anHtmlStr = imc.interpretTemplate(meta_id, user) ;
    	return anHtmlStr ;
 	} catch (Exception ex) {
    ex.printStackTrace();
    throw new ServletException(ex.getMessage());
 	}
} // parseDoc
*/
// ****** CALL AN STORED PROCEDURE IN DB FUNCTIONS *********************

/***
	Executes a READ SQL stored procedure and returns a string
*/

public String execSQlProcedureStr(String server, String sqlStr ) throws ServletException {
	return this.execSqlProcedureStr(server, sqlStr) ;
/*
 try {
      JanusPrefs prop = new JanusPrefs() ;
      prop.loadConfig("rmi.cfg") ;
    	Registry reg = LocateRegistry.getRegistry(prop.getServerIP() ,prop.getServerPort()) ;
    	prop = null ;
    	imcode.server.IMCServiceInterface imc =
        (imcode.server.IMCServiceInterface)reg.lookup("IMCService") ;

      String answer = "" ;
    	if(imc.userIsAdmin(user) )
    		answer = imc.sqlProcedureStr(sqlStr) ;

    	imc = null ;
    	return answer ;

 } catch (Exception ex) {
        ex.printStackTrace();
        throw new ServletException(ex.getMessage());
   }
 */
 /* try {
 		IMCServiceInterface imc = getInterface( server ) ;

	  String answer = "" ;
    if(imc.userIsAdmin(user))
    	answer = imc.sqlProcedureStr(sqlStr) ;
   	return answer ;

 	} catch (Exception ex) {
    ex.printStackTrace();
    throw new ServletException(ex.getMessage());
 	}
  */
} // execSql

  /***
	Executes a READ SQL stored procedure and returns a string
*/

public String execSqlProcedureStr(String server, String sqlStr ) throws ServletException {

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

public String[] execSqlProcedure(String server, String sqlStr ) throws ServletException {
/*
 try {
      JanusPrefs prop = new JanusPrefs() ;
      prop.loadConfig("rmi.cfg") ;
    	Registry reg = LocateRegistry.getRegistry(prop.getServerIP() ,prop.getServerPort()) ;
    	prop = null ;
    	imcode.server.IMCServiceInterface imc =
        (imcode.server.IMCServiceInterface)reg.lookup("IMCService") ;

    	String[] answer = imc.sqlProcedure(sqlStr) ;
    	imc = null ;
    	return answer ;

 } catch (Exception ex) {
        ex.printStackTrace();
        throw new ServletException(ex.getMessage());
   }
 */
   try {
 		imcode.server.IMCServiceInterface imc = getInterface( server ) ;

	  String[] answer = imc.sqlProcedure(sqlStr) ;
    return answer ;

 		} catch (Exception ex) {
    	ex.printStackTrace();
    	throw new ServletException(ex.getMessage());
 		}
} // execSql


/**
	Executes an WRITE statement on the database without any returnvalue
*/

public void execSqlUpdateProcedure(String server, String sqlStr )
	throws ServletException {
/*
 try {
      JanusPrefs prop = new JanusPrefs() ;
      prop.loadConfig("rmi.cfg") ;
    	Registry reg = LocateRegistry.getRegistry(prop.getServerIP() ,prop.getServerPort()) ;
    	prop = null ;
    	imcode.server.IMCServiceInterface imc =
        (imcode.server.IMCServiceInterface)reg.lookup("IMCService") ;

    	imc.sqlUpdateProcedure(sqlStr) ;
    	imc = null ;
    	return ;

 } catch (Exception ex) {
        ex.printStackTrace();
        throw new ServletException(ex.getMessage());
   }
 */

 try {
 imcode.server.IMCServiceInterface imc = getInterface( server ) ;

  imc.sqlUpdateProcedure(sqlStr) ;
  return ;

	} catch (Exception ex) {
  	ex.printStackTrace();
  	throw new ServletException(ex.getMessage());
	}

} // execSql




// ****** HERE IS THE SQL QUERIES FUNCTIONS *********************

/**
	Executes a SQL statement and returns a string
*/

public String execSqlQueryStr(String server, String sqlStr ) throws ServletException {
/*
 try {
      JanusPrefs prop = new JanusPrefs() ;
      prop.loadConfig("rmi.cfg") ;
    	Registry reg = LocateRegistry.getRegistry(prop.getServerIP() ,prop.getServerPort()) ;
    	prop = null ;
    	imcode.server.IMCServiceInterface imc =
        (imcode.server.IMCServiceInterface)reg.lookup("IMCService") ;

    	String answer = imc.sqlQueryStr(sqlStr) ;
    //	imc = null ;
    	return answer ;

 } catch (Exception ex) {
        ex.printStackTrace();
        throw new ServletException(ex.getMessage());
   }
*/
 try {
 	imcode.server.IMCServiceInterface imc = getInterface( server ) ;

 	String answer = imc.sqlQueryStr(sqlStr) ;
  return answer ;

	} catch (Exception ex) {
  	ex.printStackTrace();
  	throw new ServletException(ex.getMessage());
	}

} // execSql


/**
	Executes a SQL statement and returns a string array
*/

public String[] execSqlQuery(String server, String sqlStr ) throws ServletException {
/*
 try {
      JanusPrefs prop = new JanusPrefs() ;
      prop.loadConfig("rmi.cfg") ;
    	Registry reg = LocateRegistry.getRegistry(prop.getServerIP() ,prop.getServerPort()) ;
    	prop = null ;
    	imcode.server.IMCServiceInterface imc =
        (imcode.server.IMCServiceInterface)reg.lookup("IMCService") ;

    	String[] answer = imc.sqlQuery(sqlStr) ;
    	imc = null ;
    	return answer ;

 } catch (Exception ex) {
        ex.printStackTrace();
        throw new ServletException(ex.getMessage());
   }
 */
  try {
 		imcode.server.IMCServiceInterface imc = getInterface( server ) ;

 		String[] answer = imc.sqlQuery(sqlStr) ;
    return answer ;

	} catch (Exception ex) {
  	ex.printStackTrace();
  	throw new ServletException(ex.getMessage());
	}
} // execSql


/**
	Executes a SQL statement without any returnvalue
*/

public void execSqlUpdateQuery(String server, String sqlStr ) throws ServletException {
/*
 try {
      JanusPrefs prop = new JanusPrefs() ;
      prop.loadConfig("rmi.cfg") ;
    	Registry reg = LocateRegistry.getRegistry(prop.getServerIP() ,prop.getServerPort()) ;
    	prop = null ;
    	imcode.server.IMCServiceInterface imc =
        (imcode.server.IMCServiceInterface)reg.lookup("IMCService") ;

    	imc.sqlUpdateQuery(sqlStr) ;
    	imc = null ;
    	return ;

 } catch (Exception ex) {
        ex.printStackTrace();
        throw new ServletException(ex.getMessage());
   }
 */

  try {
 		imcode.server.IMCServiceInterface imc = getInterface( server ) ;

 		imc.sqlUpdateQuery(sqlStr) ;
    return ;

	} catch (Exception ex) {
  	ex.printStackTrace();
  	throw new ServletException(ex.getMessage());
	}

} // execSql


/**
	Executes a SQL stored procedure and returns an multidimensional string array.
*/

public static String[][] execProcedureMulti(String server, String sqlStr ) throws ServletException {

 try {
 		imcode.server.IMCServiceInterface imc = getInterface( server ) ;
 		return imc.sqlProcedureMulti(sqlStr) ;

	} catch (Exception ex) {
  	ex.printStackTrace();
  	throw new ServletException(ex.getMessage());
	}
}


 public static void log(String str) {
	 System.out.println("RmiLayer: " + str ) ;
 }



} // End class