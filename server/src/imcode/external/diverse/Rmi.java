package imcode.external.diverse ;
import java.util.*;
import java.rmi.*;
import java.rmi.registry.*;
import javax.servlet.*;
import javax.servlet.http.*;


/**
	This class is intended to be a layer between the clients (servlets) and 
	the RMI interface to connect to the database.
*/

public class Rmi /* implements imcode.server.IMCServiceInterface*/ { 
//	static imcode.server.IMCPoolInterface imcConferenceDB ;
	static imcode.server.IMCServiceInterface imcJanusDB ;

	imcode.server.User user ;	
	
	
	/** This is a static initializer, which runs when the class is loaded!
		  Notice the lack of any method declaration
	*/
	
	static {
		// Lets get an interface
		imcJanusDB = initJanusDB() ;
	}
	
	/**
		Constructor
	*/
		
	public Rmi( imcode.server.User aUser) {
		this.user = aUser ;
	} 

		
// ****** INITIALIZES A DB CONNECTION *********************	
	
	
/**
	Reads the conference.cfg file and returns an imcode.server.IMCServiceInterface.
	This interface is for the Janus DB
*/

public static imcode.server.IMCServiceInterface initJanusDB() throws ServletException {
	try { 
			JanusPrefs prop = new JanusPrefs() ;
     	prop.loadConfig("rmi.cfg") ;
    	Registry reg = LocateRegistry.getRegistry(prop.getServerIP() ,prop.getServerPort()) ;
    	prop = null ;
    	imcode.server.IMCServiceInterface imc = 
       	(imcode.server.IMCServiceInterface)reg.lookup("IMCService") ; 
			return imc ;
			
	} catch (Exception ex) {
  		return null ;
		}
}  

/*********************** INIT DB CONNECTION ************/
	

// ****** CALL AN STORED PROCEDURE IN DB FUNCTIONS *********************	
	
/***
	Executes a READ SQL stored procedure and returns a string

public String execSqlProcedureStr(String sqlStr ) 
	throws ServletException {
	 	
 	for(int i = 1 ; i < 3; i++) {	
		try {
 		  String answer = "" ;
			answer = imcConferenceDB.sqlProcedureStr(sqlStr) ;
			return answer ;
		
 		} catch (Exception ex) {
 				imcConferenceDB = initConferenceDB() ;
 				ex.printStackTrace();
      	throw new ServletException(ex.getMessage());
   		}
 	}
 return null ;
} // execSql

*/


	
/**
	Executes a READ SQL stored procedure and returns a String array
*/

public String[] execSqlProcedure(String sqlStr ) 
	throws ServletException {

	for(int i = 1 ; i < 3; i++) {	
 		try {
 	  	String[] answer = imcConferenceDB.sqlProcedure(sqlStr) ;
    	return answer ;
    	
 		} catch (Exception ex) {
        ex.printStackTrace();
        throw new ServletException(ex.getMessage());
   		}
	}
	return null ;
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

public String[] execSqlProcedureExt(String sqlStr ) 
	throws ServletException {
	
	String[] answer ;
	for(int i = 1 ; i < 3; i++) {	
 		try { 
 	   	answer = imcConferenceDB.sqlProcedureExt(sqlStr) ;
    	return answer ;
    } catch (Exception ex) {
        ex.printStackTrace();
        throw new ServletException(ex.getMessage());
   		}
 } // End for
 	return null ;	
} // execSql



/**
	Executes an stored procedure on the database.
	Updates or WRITE statement on the database without any returnvalue
*/

public void execSqlUpdateProcedure(String sqlStr ) 
	throws ServletException {

	for(int i = 1 ; i < 3; i++) {	
		try {
    	imcConferenceDB.sqlUpdateProcedure(sqlStr) ;
    	return ;
		} catch (Exception ex) {
        ex.printStackTrace();
        throw new ServletException(ex.getMessage());
   		}
	}
} 
	
		
// ****** HERE IS THE SQL QUERIES FUNCTIONS *********************	
	
/**
	Executes a SQL statement and returns a string
*/

public String execSqlQueryStr(String sqlStr ) 
	throws ServletException {

	for(int i = 1 ; i < 3; i++) {	
 	 try { 		
    	String answer = imcConferenceDB.sqlQueryStr(sqlStr) ;
    	return answer ;
		
 	 } catch (Exception ex) {
       ex.printStackTrace();
       throw new ServletException(ex.getMessage());
   	 }
	}
 return null ;
} // execSql


/**
	Executes a SQL statement and returns a string array
	Ex. "select * from myTable"
*/

public String[] execSqlQuery(String sqlStr ) 
	throws ServletException {

	for(int i = 1 ; i < 3; i++) {	
 	  try {
    	String[] answer = imcConferenceDB.sqlQuery(sqlStr) ;
    	return answer ;
		
 		} catch (Exception ex) {
        ex.printStackTrace();
        throw new ServletException(ex.getMessage());
   		}
	}
	return null ;
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

public String[] execSqlQueryExt(String sqlStr ) 
	throws ServletException {

	for(int i = 1 ; i < 3; i++) {	
 		try { 
    	String[] answer = imcConferenceDB.sqlQueryExt(sqlStr) ;
    	return answer ;
		
 		} catch (Exception ex) {
     		ex.printStackTrace();
     		throw new ServletException(ex.getMessage());
  	 	}
	}
	return null ;
} 


/**
	Executes a SQL statement without any returnvalue
*/

public void execSqlUpdateQuery(String sqlStr ) 
	throws ServletException {

	for(int i = 1 ; i < 3; i++) {	
	 	try { 
 			imcConferenceDB.sqlUpdateQuery(sqlStr) ;
    	return ;
		} catch (Exception ex) {
        ex.printStackTrace();
        throw new ServletException(ex.getMessage());
   		}
	}
} 

 public static void log( String str) {
	  System.out.println("RmiConf: " + str ) ;	
	}
 


// ******************* TEMPLATE FOLDER FUNCTIONS ****************
/**
	Returns the physical path to the folder where the external doctypes templates are
	located. Observe that if janus cant recognize the metaId were sending, it returns the
  path to its own template catalogue!
*/

public String getExternalTemplateFolder(int meta_id) /* throws ServletException */{
	String theFolder = "" ;
	for(int i = 1 ; i < 3; i++) {	
		try {     
     	theFolder = imcJanusDB.getExternalTemplateFolder(meta_id) ;   
			return theFolder ;
		} catch (Exception ex) {
       ex.printStackTrace();
      // throw new ServletException(ex.getMessage());
   		}
  	} 
 	return null ;
}



/**
	Returns the physical path to the folder where the internal templates are located.
	The function takes the meta id as argument, or use -1 if no metaid is known
*/

public String getInternalTemplateFolder(int meta_id) /*throws ServletException*/ {
	String theFolder = "" ;
	for(int i = 1 ; i < 3; i++) {
		try {    
		   theFolder = imcJanusDB.getInternalTemplateFolder(meta_id) ;   
		} catch (Exception ex) {
       ex.printStackTrace();
     //  throw new ServletException(ex.getMessage());
   		}
	}
 	return theFolder ;
}


/**
	Returns the physical path to the folder where the internal templates are located.
*/

public static String getInternalTemplateFolder(HttpServletRequest req) throws ServletException {
	String theFolder = "" ;
	for(int i = 1 ; i < 3; i++) {
		try { 
		  // Lets get the metaid 
    	String meta_id = req.getParameter("meta_id") ;
			if(meta_id == null )	
				return "" ;
			
			int aMeta_id = Integer.parseInt("meta_id") ; 
    // Lets get the templatefolder from Janus, 
     	theFolder = imcJanusDB.getInternalTemplateFolder(aMeta_id) ;   
		
		} catch (Exception ex) {
       ex.printStackTrace();
       throw new ServletException(ex.getMessage());
   		}
	}
 	return theFolder ;
}



/**
	Checks whether an user is administrator or not
*/

public static boolean checkAdminRights(imcode.server.User user) throws ServletException {
	
	boolean admin = false ;
	for(int i = 1 ; i < 3; i++) {
 		try { 
 			// Lets check if the user is an admin
 			admin = imcJanusDB.userIsAdmin(user) ;   
			return admin ;
 		} catch (Exception ex) {
        ex.printStackTrace();
        throw new ServletException(ex.getMessage());
   		}
	}
	return admin ;
} // checkAdminRights
 
 
 
/**
	Parses a doc on the server. 
*/

public String parseDoc(String htmlStr, java.util.Vector variables,
	java.util.Vector data ) throws java.rmi.RemoteException
 {
	
	for(int i = 1 ; i < 3; i++) {
 	 	try {  
    	String anHtmlStr = imcJanusDB.parseDoc(htmlStr, variables, data) ; 
    	return anHtmlStr ;
	 } catch (Exception ex) {
        ex.printStackTrace();
        throw new ServletException(ex.getMessage());
   	}
	}
	return null ;
} // parseDoc
	


/**
	Parses a doc on the server.
*/

public String parseDoc(String htmlStr, java.util.Vector variables ) 
	throws java.rmi.RemoteException {

	for(int i = 1 ; i < 3; i++) {
		try { 
      // Lets parse
    	String anHtmlStr = imcJanusDB.parseDoc(htmlStr, variables) ; 
    	return anHtmlStr ;
		} catch (Exception ex) {
        ex.printStackTrace();
        throw new ServletException(ex.getMessage());
   		}
	}
	return null ;
} // parseDoc
	
	

/**
	Reads an Imcode templatefile and interpret IMSCRIPT and output a HTML String obj.
*/

public String interpretTemplate(int meta_id ) 
	throws ServletException {
	
	for(int i = 1 ; i < 3; i++) {
		try {  
		 // Lets parse the document 
    	String anHtmlStr = imcJanusDB.interpretTemplate(meta_id, user) ; 
    	return anHtmlStr ;
		} catch (Exception ex) {
        ex.printStackTrace();
        throw new ServletException(ex.getMessage());
   }
	}
	return null ;
} // parseDoc

	
} // End class



