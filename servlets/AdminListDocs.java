/*
 *
 * @(#)AdminListDocs.java
 *
 * 
 * 2000-10-20
 *
 * Copyright (c)
 *
*/

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import imcode.external.diverse.VariableManager;
import imcode.external.diverse.Html;
import imcode.util.IMCServiceRMI;
import imcode.util.Utility;
import imcode.util.Parser;
import imcode.external.diverse.MetaInfo;

/**
 * Lists document by create or modified date of choisen document types.
 *
 * Html template in use:
 * AdminListDocs.html
 * AdminListDocs_doclList.html
 * Error.html
 * 
 * Html parstags in use:
 * #DOCUMENT_TYPES#
 * #LIST_DOCUMENT#
 * #META_ID#
 * #HEADER#
 * #DOC_TYPE#
 * #DATE#
 *
 * stored procedures in use:
 * - ListDocsByDate
 * - ListDocsGetInternalDocTypes
 * - ListDocsGetInternalDocTypesValue
 * - GetLangPrefixFromId
 *
 * @version 1.04 11 Nov 2000
 * @author Jerker Drottenmyr
 *
*/
public class AdminListDocs extends Administrator {
	private final static String CVS_REV = "$Revision$" ;
	private final static String CVS_DATE = "$Date$" ;

	private static final String TEMPLATE_LISTDOC = "AdminListDocs.html";
	private static final String TEMPLATE_LISTDOC_LIST_MODIFIED = "AdminListDocs_doclList_modified.html";
	private static final String TEMPLATE_LISTDOC_LIST_CREATED = "AdminListDocs_doclList_created.html";
	private static final String TEMPLATE_LISTDOC_LIST_ELEMENT = "AdminListDocs_doclList_element.html";
	private static final String TEMPLATE_ERROR = "Error.html";
	private static final String ERROR_HEADER = "AdminListDocs";
	
	//required date format
	private static final String DATE_FORMATE = "yyyy-MM-dd";
	

	protected void doGet( HttpServletRequest request, HttpServletResponse response )
	                        throws ServletException, IOException {
		
		String host = request.getHeader("Host") ;
		String imcserver = Utility.getDomainPref("adminserver",host) ;
		
		// Lets validate the session
		if ( super.checkSession( request, response ) == false )
		{
			return ;
		}

		// Lets get an user object 
		imcode.server.User user = super.getUserObj( request, response ) ;

		if(user == null) {

			return ;
		}
			
		// Lets verify that the user who tries to add a new user is an admin  	
		if (super.checkAdminRights( imcserver, user) == false) { 
		
			return ;
		}

		String languagePrefix = getLanguagePrefix( imcserver, user.getInt( "lang_id" ) );
			
		// Lets get all doctypes from DB
		String sqlQ = "ListDocsGetInternalDocTypes '"+languagePrefix+"'" ;
		String[][] queryResult = IMCServiceRMI.sqlQueryMulti( imcserver, sqlQ );

		// Lets generate the html page
		Html htmlCode = new Html();
		String optionList = htmlCode.createListOfOptions( queryResult );
		
		VariableManager vm = new VariableManager();
		vm.addProperty("DOCUMENT_TYPES", optionList );
		
		this.sendHtml( request, response, vm, this.TEMPLATE_LISTDOC );
		return;
		
	}
	
	protected void doPost( HttpServletRequest request, HttpServletResponse response )
	                        throws ServletException, IOException {
							
		String host = request.getHeader("Host") ;
		String imcserver = Utility.getDomainPref("adminserver",host) ;
		String eMailServerMaster = Utility.getDomainPref( "servermaster_email", host );
		
		// Lets validate the session
		if (super.checkSession( request , response ) == false) return ;
		
		// Lets get an user object  
		imcode.server.User user = super.getUserObj( request , response ) ;
		
		// lets get ready for errors
		ErrorMessageGenerator errroMessage = null;
		String languagePrefix = getLanguagePrefix( imcserver, user.getInt( "lang_id" ) );
		
		if(user == null) {
			String header = "Error in AdminRoleBelongings." ;
			String msg = "Couldnt create an user object."+ "<BR>" ;
			this.log(header + msg) ;
			AdminError err = new AdminError( request , response,header,msg) ;
			return ;
		}
			
		// Lets check if the user is an admin, otherwise throw him out.
		if (super.checkAdminRights( imcserver, user) == false) { 
			String header = "Error in AdminRoleBelongings." ;
			String msg = "The user is not an administrator."+ "<BR>" ;
			this.log(header + msg) ;
			AdminError err = new AdminError( request , response,header,msg) ;
			
			return ;
		}

		// *************** RETURN TO ADMINMANAGER *****************
		if( request.getParameter("CANCEL") != null) {
			response.sendRedirect(MetaInfo.getServletPath( request ) + "AdminManager") ;
			return ;
		}
		
		// *************** RETURN TO ADMIN ROLES *****************
		if( request.getParameter("LISTDOC_LIST") != null) {
		
			boolean noErrors = true;
			String parseTemplate = null;
			String[] docTypesToShow = null;
			
			/* 
			 * 0 = startDate to endDate 
			 * 1 = all
			 * 2 = all upp to endDate 
			 * 3 = all down to startDate
			*/
			int listByDateMod = 0;
			String[] docTypes = request.getParameterValues( "DOC_TYPES" );
			
			/* 
			 * 0 = all date !not in use
			 * 1 = create date
			 * 2 = modified date  
			*/
			String listMod = request.getParameter( "LISTMOD" );
			String startDate = request.getParameter( "START_DATE" );
			String endDate = request.getParameter( "END_DATE" );
			
			/* lets se if any errors in requared fields or if some is missing */
			try {
				if ( listMod != null ) {
					int  mod = Integer.parseInt( listMod );
					if ( !(mod == 1 || mod == 2 ) ) {
						noErrors = false;
					} else {
						// lets set htmlTemplate (create or modified )
						switch ( mod ) {
							case ( 1 ):
								parseTemplate = this.TEMPLATE_LISTDOC_LIST_CREATED;
								break;
							default:
								parseTemplate = this.TEMPLATE_LISTDOC_LIST_MODIFIED;
								break;
						}
					}
				} else {
					noErrors = false;
				}
				
				if ( docTypes != null) {
					docTypesToShow = docTypes;
					for ( int i = 0 ; i < docTypes.length  ; i++ ) {
						int testVar = Integer.parseInt( docTypes[i] );
						// if all doctypes choosen then lets get all doctypes
						if ( testVar == 0 ) {
							String sqlQ = "ListDocsGetInternalDocTypesValue";
							docTypesToShow = IMCServiceRMI.sqlQuery( imcserver, sqlQ );
						}
					}
				} else {
					noErrors = false;
				}
			} catch ( NumberFormatException e ) {
				noErrors = false;
			}
			
			if ( startDate != null ) {
				if ( startDate.length() > 0 ) {
				    if ( !isDateInRightFormat( startDate ) ) {
						noErrors = false;
				    }
				} else {
					startDate = "0"; // Stored Procedure expects 0 then no startDate
				}
			} else {
				noErrors = false; // no startDate field submited
			}
			
			if ( endDate != null ) {
				if ( endDate.length() > 0 ) {
				    if ( !isDateInRightFormat( endDate ) ) {
						noErrors = false;
				    }
				} else {
					endDate = "0"; // Stored Procedure expects 0 then no endDate
				}
			} else {
				noErrors = false; // no endDate field submited
			}
			
			// lets generate response page
			if ( noErrors ) {
				
				//lets get htmltemplate for tablerow
				String htmlListElement = IMCServiceRMI.parseDoc( imcserver, null, this.TEMPLATE_LISTDOC_LIST_ELEMENT, languagePrefix );
				
				String[] tagData =	{
										"#META_ID#", null,
										"#DOC_TYPE#", null,
										"#HEADER#", null,
										"#DATE#", null,
									};
				// lets exchange som html codes
				String[] pd =	{
									"&",	"&amp;",
									"<",	"&lt;",
									">",	"&gt;",
					   			};

				StringBuffer listOfDocs = new StringBuffer();
				for ( int i = 0 ; i < docTypesToShow.length ; i++ ) {
					String sqlQ = "ListDocsByDate " + listMod + ", " + docTypesToShow[i] + 
					               ", '" + startDate + "', '" + endDate + "','"+languagePrefix+"'";
					String[][] queryResult = IMCServiceRMI.sqlQueryMulti( imcserver, sqlQ );
					
					for ( int j = 0 ; j < queryResult.length ; j++ ) {
						tagData[1] = queryResult[j][0];
						tagData[3] = queryResult[j][1];
						tagData[5] = Parser.parseDoc( queryResult[j][2], pd );
						tagData[7] = queryResult[j][3];
						listOfDocs.append( Parser.parseDoc( htmlListElement, tagData ) );
					}
				}
				
				//Lets generate the html page
				VariableManager vm = new VariableManager();
				vm.addProperty( "LIST_DOCUMENT", listOfDocs.toString() );
			
				this.sendHtml( request, response, vm, parseTemplate );
				
			} else {
				sendErrorMessage( imcserver, eMailServerMaster, languagePrefix , this.ERROR_HEADER, 10, response );
			}
		}
		
	}
	
	/**
	 * check for right date form
	*/
	private boolean isDateInRightFormat( String date ) {
		 
		// Format the current time.
		SimpleDateFormat formatter = new SimpleDateFormat( this.DATE_FORMATE );
		 
		try {
			formatter.parse( date );
		} catch ( ParseException  e ) {
			return false;
			
		}
		
		return true;
	}
}
