/******************************************************************************************
* IMCServiceInterface.java                                                                *
* Copyright Magnum Software 1998,1999                                                     *
*-----------------------------------------------------------------------------------------*
* SYNOPSIS:                                                                               *
* Outline     : The interface for the Imcode Net Server database pool                     *
*-----------------------------------------------------------------------------------------*
* Author      : Magnus Isenberg : Magnum Software (c) 1998,1999                           *
*-----------------------------------------------------------------------------------------*
* PLATFORM    : PC/MAC/SOLARIS					              *
* ENVIRONMENT : WINDOWS 95/98/NT MacOS UNIX OS2 LINUX runs from command line.             *
* TOOLS       : JavaSoft JDK1.2, KAWA IDE				              *
* REFERENCE   : The Java Class Libraries 1 & 2		                          *
*               Getting Staring Using RMI (www.javasoft.com)                              *
* Thanks to   : Andreas Bengtsson : Software Engineer : Entra Memtek Education AB         *
*             : Hasse Brattberg   : Software Engineer : Entra Memtek Education AB         *
*             : Roger Larsson     : HTML Programmer   : Visby Interactive Studio AB       *
*-----------------------------------------------------------------------------------------*
* Last Update : 09:00 06-12-1999                                                          *
*-----------------------------------------------------------------------------------------*
* REVISION HISTORY :                                                                      *
* 06-12-1999 : MI  : First Written                                                        *
******************************************************************************************/
package imcode.server ;

import java.util.Vector ;
import imcode.server.User ;


/******************************************************************************************
* INTERFACE: IMCPoolInterface                                                             *
*-----------------------------------------------------------------------------------------*
* SYNOPSIS :                                                                              *
* Interface for the Imcode Net Server.                                                    *
*-----------------------------------------------------------------------------------------*
* REVISION HISTORY :                                                                      *
* 30-09-1999 : MI  : parseDoc                                                             *
* 30-09-1999 : MI  : sqlQuery    return String array                                      *
* 30-09-1999 : MI  : sqlUpdate                                                            *
* 30-09-1999 : MI  : sqlQueryStr   return String                                          *
* 30-09-1999 : MI  : parseDoc, uses two vectors                                           *
* 08-10-1999 : MI  : sqlProcedure()     return String array                               *
* 08-10-1999 : MI  : sqlProcedureStr()  return String                                     *
* 08-10-1999 : MI  : sqlUpdateProcedure()                                                 *
* 18-11-1999 : MI  : sqlQuery(String sqlQuery,String catalog) return String array         *
******************************************************************************************/
public interface IMCPoolInterface {



	// Parse doc replace variables with data
	String  parseDoc(String htmlStr,java.util.Vector variables)
	;

	// Send a sqlquery to the database and return a string array
	String[] sqlQuery(String sqlQuery)
	;

	// Send a sqlquery to the database/set database and return a string array
	String[] sqlQuery(String sqlQuery,String catalog)
	;

	// Send a sql update query to the database
	void sqlUpdateQuery(String sqlStr) ;


	// Send a sqlquery to the database and return a string
	String sqlQueryStr(String sqlQuery)
	;


	// Send a procedure to the database and return a string array
	public String[] sqlProcedure(String procedure)
	;

	// Send a procedure to the database and return a string
	public String sqlProcedureStr(String procedure)
	;

	// Send a update procedure to the database
	public void sqlUpdateProcedure(String procedure)
	;


	// Parse doc replace variables with data, uses two vectors
	String  parseDoc(String htmlStr,java.util.Vector variables,java.util.Vector data)
	;

	// Send a sqlquery to the database and return a string array and metadata
	String[] sqlQueryExt(String sqlQuery)
	;

	// Send a procedure to the database and return a string array
	public String[] sqlProcedureExt(String procedure)
	;

	// Send a sqlquery to the database and return a Hashtable
	public java.util.Hashtable sqlQueryHash(String sqlQuery)
	;

	// Send a procedure to the database and return a Hashtable
	public java.util.Hashtable sqlProcedureHash(String procedure)
	;

	// check document rights
	public boolean checkDocAdminRights(int meta_id, User user)
	;

	// Send a procedure to the database and return a multistring array
	public String[][] sqlProcedureMulti(String procedure)
	;

}
