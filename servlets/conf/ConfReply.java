/*
 *
 * @(#)ConfReply.java
 *
 *
 *
 * Copyright (c)
 *
*/

import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import imcode.external.diverse.* ;
import java.rmi.* ;
import java.rmi.registry.* ;
import imcode.util.*;

/**
 *
 *
 * Html template in use:
 * Conf_Reply_New_Comment.htm
 *
 * Html parstags in use:
 * #NEW_COMMENT#
 *
 * stored procedures in use:
 * -
 *
 * @version 1.5 21 Nov 2000
 * @author Rickard Larsson, Jerker Drottenmyr
 *
*/


public class ConfReply extends Conference {
	private final static String CVS_REV = "$Revision$" ;
	private final static String CVS_DATE = "$Date$" ;

	private final static String NEW_COMMENT_TEMPLATE =  "Conf_Reply_New_Comment.htm";
	private final static String ADMIN_LINK_TEMPLATE = "Conf_Reply_Admin_Link.htm";

	String HTML_TEMPLATE ;
	String RECS_HTML;

	/**
		DoPost
	**/

	public void doPost(HttpServletRequest req, HttpServletResponse res)
	throws ServletException, IOException {

		// Lets validate the session, e.g has the user logged in to Janus?
		if (super.checkSession(req,res) == false)	return ;

		// Lets get the parameters
		Properties params = this.getParameters(req) ;

		// Lets get an user object
		imcode.server.User user = super.getUserObj(req,res) ;
		if(user == null) return ;

		if ( !isUserAuthorized( req, res, user ) ) {
			return;
		}

		// Lets get serverinformation
		String host = req.getHeader("Host") ;
		String imcServer = Utility.getDomainPref("userserver",host) ;
		String confPoolServer = Utility.getDomainPref("conference_server",host) ;

		// ********* UPDATE DISCUSSIONS ********
		if(req.getParameter("UPDATE") !=null ) {
			// Lets get the users userId, the metaId and sortorder
			// Ok, lets save the users sortorder if he has change it
			Properties userParams = super.getUserParameters(user) ;

			// Lets get ourselves a userid. we cant use the userparams id
			// since we got external users. so the userid could be an ip access nbr
			String userId = userParams.getProperty("USER_ID") ;
			HttpSession session = req.getSession(false) ;
			if (session != null) {
				userId = (String) session.getAttribute("Conference.user_id") ;
			}

			String metaId = params.getProperty("META_ID") ;

			/* // THIS CODE IS USED IF WE WANT A CHECKOBOX INSTEAD
			String newSortOrder = (req.getParameter("SORT_ORDER")==null) ? "" : (req.getParameter("SORT_ORDER")) ;
			// SortOrder is a checkbox, and if no checkboxvalue is found the sortorder will return null
			// otherwise it will return 1.that
			if(newSortOrder.equals(""))
			 		newSortOrder = "0" ;
			else
			newSortOrder = "1" ;
			*/

			// THIS CODE IS USED IF WE WANT RADIOBUTTONS
			String ascSortOrder = (req.getParameter("SORT_ORDER")==null) ? "0" : (req.getParameter("SORT_ORDER")) ;
			//log("AscSortOrder: " + ascSortOrder) ;

			// Ok, Lets set the users sortorder preference
			RmiConf rmi = new RmiConf(user) ;
			String sqlQ = "A_ConfUsersSetReplyOrder " + metaId + ", " + userId ;
			sqlQ += ", " + ascSortOrder ;
			//	log("Sql quest: " + sqlQ) ;
			rmi.execSqlUpdateProcedure(confPoolServer, sqlQ) ;
			this.doGet(req, res) ;
			return ;
		}

	}


	/**
		DoGet
	**/

	public void doGet(HttpServletRequest req, HttpServletResponse res)
	throws ServletException, IOException {

		// Lets validate the session, e.g has the user logged in to Janus?
		if (super.checkSession(req,res) == false)	return ;

		// Lets get the parameters and validate them
		Properties params = this.getParameters(req) ;
		if (checkParameters(req, res, params) == false) return ;

		// Lets get an user object
		imcode.server.User user = super.getUserObj(req,res) ;
		if(user == null) return ;

		if ( !isUserAuthorized( req, res, user ) ) {
			return;
		}

		// Lets get the users userId
		Properties userParams = super.getUserParameters(user) ;
		String userId = "" ;

		// Lets get the replylist from DB
		String discId = params.getProperty("DISC_ID") ;

		// Lets update the sessions DISC_ID
		HttpSession session = req.getSession(false) ;
		if(session != null  ) {
			session.setAttribute("Conference.disc_id", discId) ;
			userId = (String) session.getAttribute("Conference.user_id") ;
		}

		// Lets get serverinformation
		String host = req.getHeader("Host") ;
		String imcServer = Utility.getDomainPref("userserver",host) ;
		String confPoolServer = Utility.getDomainPref("conference_server",host) ;
                //log("ConfPoolServer: " + confPoolServer) ;

		RmiConf rmi = new RmiConf(user) ;
                String sqlQ = "A_GetAllRepliesInDisc " + discId + ", " + userId ;
                //log("SQLQ: " + sqlQ ) ;
		String sqlAnswer[] = rmi.execSqlProcedureExt(confPoolServer, sqlQ) ;

                //log("sqlAnswer: " + sqlAnswer) ;
		// Lets get the discussion header
		String discHeader = rmi.execSqlProcedureStr(confPoolServer, "A_GetDiscussionHeader " + discId ) ;
                if (discHeader == null || discId.equalsIgnoreCase("-1") )
			discHeader = " " ;
		//log("discHeader: " + discHeader) ;
		/*
		// THIS CODE IS USED IF WE WANT A CHECKOBOX INSTEAD
			// Lets get the users sortorder from DB
		String metaId = params.getProperty("META_ID") ;
		String sqlQ = "ConfUsersGetReplyOrderSel " + metaId + ", " + userId  ;
		//log("Sql: " + sqlQ) ;
		String sortOrderVal = (String) rmi.execSqlProcedureStr(sqlQ) ;
		String checkBoxStr = "" ;
		// log("Sortorder: " + sortOrderVal) ;
		if( sortOrderVal.equalsIgnoreCase("1")) checkBoxStr = "checked" ;
			  // log("CheckBoxStr: " + checkBoxStr) ;
		*/

		// THIS CODE IS USED IF WE WANT RADIOBUTTONS
		// UsersSortOrderRadioButtons
		String metaId = params.getProperty("META_ID") ;
		int intMetaId = Integer.parseInt( metaId );
		String sql = "A_ConfUsersGetReplyOrderSel " + metaId + ", " + userId  ;
		String sortOrderValue = (String) rmi.execSqlProcedureStr(confPoolServer, sql) ;
		String ascState = "" ;
		String descState = "" ;
		String ascVal = "0" ;
		if( sortOrderValue.equalsIgnoreCase("1"))
			ascState = "checked" ;
		else
			descState = "checked" ;

		// SYNTAX: date  first_name  last_name  headline   text reply_level
		// Lets build our variable list
		Vector tagsV = new Vector() ;
		tagsV.add("#REPLY_DATE#") ;
		tagsV.add("#FIRST_NAME#") ;
		tagsV.add("#LAST_NAME#") ;
		tagsV.add("#REPLY_HEADER#") ;
		tagsV.add("#REPLY_TEXT#") ;
		tagsV.add("#REPLY_LEVEL#") ;

		// Lets get path to the imagefolder. http://dev.imcode.com/images/102/ConfDiscNew.gif
		String imagePath = super.getExternalImageFolder(req) + "ConfExpert.gif" ;
		// log("ImagePath: " + imagePath) ;

		// Lets get the part of the expert html
		//		String templateLib = super.getExternalTemplateFolder(req) ;
		//		String expertHtm = templateLib + "CONF_EXPERT.HTM" ;

		// Lets get the part of an html page, wich will be parsed for every a Href reference
		File templateLib = super.getExternalTemplateFolder(req) ;
		File aSnippetFile = new File(templateLib, RECS_HTML) ;
		//	log("SnippetFile: " + aSnippetFile) ;



		// Lets get the current time from the sql server
		String sqlTimeStr = rmi.execSqlProcedureStr(confPoolServer, "A_GetTime") ;

		// String dateString = formatter.format(sqlTime);
		// Lets update the discussion list
		this.updateDiscFlagList(req,discId,sqlTimeStr) ;

		// Lets preparse all records
		String allRecs = " " ;
		if (sqlAnswer != null) allRecs = preParse(req, sqlAnswer, tagsV, aSnippetFile, imagePath) ;

		// Lets build the Responsepage

		//lets generate the buttons that should appear
		String commentButton = "&nbsp;";

		//lets show comment button if user has more than readrights
		if ( IMCServiceRMI.checkDocRights( imcServer, intMetaId, user ) &&
			 IMCServiceRMI.checkDocAdminRights( imcServer, intMetaId, user ) ) {

			VariableManager vmButtons = new VariableManager();
			vmButtons.addProperty( "#SERVLET_URL#", MetaInfo.getServletPath( req ) );
			vmButtons.addProperty( "#IMAGE_URL#", this.getExternalImageFolder( req ) );
			HtmlGenerator commentButtonHtmlObj = new HtmlGenerator( templateLib, this.NEW_COMMENT_TEMPLATE );
			commentButton = commentButtonHtmlObj.createHtmlString( vmButtons, req );
		}

		VariableManager vm = new VariableManager() ;
		vm.addProperty("NEW_COMMENT", commentButton ) ;
		vm.addProperty("USER_SORT_ORDER", ascVal ) ;
		vm.addProperty("CHECKBOX_STATE_ASC", ascState ) ;
		vm.addProperty("CHECKBOX_STATE_DESC", descState ) ;
		vm.addProperty("REPLIES_RECORDS", allRecs  ) ;
		vm.addProperty("CURRENT_DISCUSSION_HEADER", discHeader  ) ;
		vm.addProperty( "ADMIN_LINK_HTML", this.ADMIN_LINK_TEMPLATE );

		this.sendHtml(req,res,vm, HTML_TEMPLATE) ;

		// 	log("Get är klar") ;
		return ;
	}


	/**
	Takes the discussion id from the request object and moves ít to
	the sessions list over viewed discussions.
	**/

	public void updateDiscFlagList( HttpServletRequest req, String discId, String now)	throws ServletException, IOException {

		// Lets get the newDiscsList

		// Get the session and add the clicked discussion to the list. Put list back
		HttpSession session = req.getSession(true);
		Properties viewedDiscs = (Properties) session.getAttribute("Conference.viewedDiscList") ;
		// Lets check if we got a list, if not, then create one
		if( viewedDiscs == null) {
			log("ViewedDiscs == null") ;
			viewedDiscs = new Properties() ;
		}

		// Lets create a date from the sqlstring

		//log("SQLTIME: " + java.sql.Date.valueOf(now).toString());
		java.text.SimpleDateFormat formatter= new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss") ;
		String dateString = formatter.format(new Date());

		// log("discId: " + discId) ;
		//log("dateString: " + dateString) ;
		if(discId == null || dateString == null) {
			log("Error i updateDiscFlagList") ;
			log("discId: " + discId) ;
			log("dateString: " + dateString) ;
			discId = "" + discId ;
			dateString = "" + dateString ;
		}
		viewedDiscs.setProperty(discId, dateString) ; // id
		session.setAttribute("Conference.viewedDiscList", viewedDiscs) ;
		// Ok, Lets print what we just updated
		viewedDiscs = (Properties) session.getAttribute("Conference.viewedDiscList") ;
		//log("*** ConfReply ***" + "\n") ;
		//log(props2String(viewedDiscs)) ;
	}



	/**
	Parses the Extended array with the htmlcode, which will be parsed
	for all records in the array
	*/
	public String preParse (HttpServletRequest req, String[] DBArr, Vector tagsV,
		File htmlCodeFile, String imagePath)  throws ServletException, IOException {

		String htmlStr = "" ;
		try {
			// Lets get the url to the servlets directory
			String servletHome = MetaInfo.getServletPath(req) ;

			// Lets get the part of the expert html
			File templateLib = super.getExternalTemplateFolder(req) ;
			File expertHtmFile = new File(templateLib, "CONF_EXPERT.HTM") ;

			// Lets get the nbr of cols
			int nbrOfCols = Integer.parseInt(DBArr[0]) ;
			//log("Number of cols: " + nbrOfCols) ;

			// Lets build an tagsArray with the tags from the DBarr, if
			// null was passed to us instead of a vector

			if( tagsV == null) {
				tagsV = new Vector() ;
				for(int k = 1; k<nbrOfCols; k++) {
					tagsV.add(DBArr[k]) ;
					// log("Counter: "+ k + " Tagvärde: " + DBArr[k] ) ;
				}
			}

			// Lets do for all records...
			for(int i = nbrOfCols+1; i<DBArr.length; i += nbrOfCols) {
				String oneParsedRecordStr = "" ;
				Vector dataV = new Vector() ;

				// Lets do for one record... Get all fields for that record
				// Lets go through the array and see if we can found an '\n' and
				// Replace it with a <BR>
				for(int j=i; j<i+nbrOfCols ; j++) {
					StringBuffer b = new StringBuffer(DBArr[j]) ;
					String s = this.replace(b, '\n' , "<BR>" ).toString() ;
					// String s = Html.replace(b, '\n' , "<BR>" ).toString() ;
					dataV.add(s) ;
					// dataV.add(DBArr[j]) ;
				} // End of one records for

				// Lets check if the user is some kind of "Master" eg. if he's
				// reply_level is equal to 1 and add the code returned to data.
				dataV = this.getReplyLevelCode(req, dataV, imagePath) ;

				// Ok, Lets go through this vector and see if we can found a '\n' and
				// Replace it with a <BR>
				//StringBuffer strBuff = new StringBuffer(dataV.toString()) ;
				//strBuff = Html.replace(strBuff, "'\'", "<BR>") ;
				//dataV = Html.replace(dataV, '\n', "<BR>") ;
				//log("RadKoll: " + dataV.toString() ) ;

				// Lets parse one record
				oneParsedRecordStr = this.parseOneRecord(tagsV, dataV, htmlCodeFile) ;
				htmlStr += oneParsedRecordStr ;
				//	log("Ett record: " + oneParsedRecordStr);
			} // end of the big for

		} catch(Exception e) {
			log("Error in Preparse") ;
			return null ;
		}
		return htmlStr ;
	} // End of


	/**
	Loops throug a vector and looks out for a character and replaces this
	character to a string .
	**/
	public static StringBuffer replace (StringBuffer strBuff, char lookFor, String replacement) {
		for( int i = 0 ; i < strBuff.length(); i++ ) {
			char aChar = strBuff.charAt(i) ;
			if('\n' == aChar) {
				strBuff = strBuff.replace(i,i,replacement) ;
				i+=replacement.length() ;
			}
		}

		return strBuff ;
	} // End of replace


	/**
	Parses one record.
	*/
	public String parseOneRecord (String[] tags, String[] data, File htmlCodeFile) {

		Vector tagsV = super.convert2Vector(tags) ;
		Vector dataV = super.convert2Vector(data) ;
		return this.parseOneRecord(tagsV, dataV, htmlCodeFile) ;
	}


	/**
	Parses one record.
	*/
	public String parseOneRecord (Vector tagsV, Vector dataV, File htmlCodeFile) {

		// Lets parse one aHref reference
		ParseServlet parser = new ParseServlet(htmlCodeFile, tagsV, dataV) ;
		String oneRecordsHtmlCode = parser.getHtmlDoc() ;
		//	log("OneRecords html: " + oneRecordsHtmlCode) ;

		return oneRecordsHtmlCode ;
	} // End of parseOneRecord

	/**
	Returns the users Replylevel htmlcode. If the user is marked with something
	a bitmap will occur, otherwise nothing will occur.
	*/
	protected static Vector getReplyLevelCode (HttpServletRequest req, Vector dataV, String ImagePath)
	throws ServletException, IOException {

		// Lets get the information regarding the replylevel
		int index = 5 ;
		String replyLevel = (String) dataV.elementAt(index) ;
		String htmlCode = "" ;
		String imageStart = "<img src=\"" ;
		String imageEnd = "\">" ;

		if (replyLevel.equals("1"))
			htmlCode = imageStart + ImagePath + imageEnd;
		else
			htmlCode = "" ;
		//	log("HtmlCode: " + htmlCode) ;
		// Lets add the htmlcode in to the vector at place index
		dataV.insertElementAt(htmlCode, index) ;
		return dataV ;
	}


	/**
	Collects the parameters from the request object. If a discId is found in the
	request object, then that discId will be used instead of the session parameter.
	**/

	public Properties getParameters( HttpServletRequest req)
	throws ServletException, IOException {

		// Lets get the standard metainformation
		Properties reqParams  = super.getSessionParameters(req) ;

		/* Lets get our own variables. We will first look for the discussion_id
		 in the request object, other wise, we will get the one from our session object
		*/
		String confDiscId = (req.getParameter("disc_id")==null) ? "" : (req.getParameter("disc_id")) ;
		if( confDiscId.equals("") ) {
			HttpSession session = req.getSession(false) ;
			if (session != null) {
				confDiscId =	(String) session.getAttribute("Conference.disc_id") ;
			}
		}
		//log("GetParameters: " + confDiscId) ;
		reqParams.setProperty("DISC_ID", confDiscId) ;
		return reqParams ;
	}

	/**
	Detects paths and filenames.
	*/

		public void init(ServletConfig config) throws ServletException {
		super.init(config);
		RECS_HTML = "Conf_reply_list.htm" ;
		HTML_TEMPLATE = "Conf_Reply.htm" ;
	}

	/**
	Log function, will work for both servletexec and Apache
	**/

	public void log( String str) {
		super.log(str) ;
		// System.out.println("ConfReply: " + str ) ;
	}
} // End of class


