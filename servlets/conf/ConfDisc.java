/*
 *
 * @(#)ConfDisc.java
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
import imcode.util.* ;
import imcode.util.IMCServiceRMI;

/**
 *
 *
 * Html template in use:
 * Conf_Disc_List_New.htm
 * Conf_Disc_List_Previous.htm
 * Conf_Disc_List_Next.htm
 * Conf_Disc_New_Button.htm
 *
 * Html parstags in use:
 * #ADMIN_TYPE#
 * #TARGET#
 * #PREVIOUS_BUTTON#
 * #NEXT_BUTTON#
 * #PREVIOUS_BUTTON#
 * #NEW_DISC_BUTTON#
 * #A_HREF_LIST#
 *
 * stored procedures in use:
 * -
 *
 * @version 1.6 21 Nov 2000
 * @author Rickard Larsson, Jerker Drottenmyr
 *
*/


public class ConfDisc extends Conference {

	private final static String NEW_DISC_FLAG_TEMPLATE = "Conf_Disc_List_New.htm";
	private final static String PREVIOUS_DISC_LIST_TEMPLATE = "Conf_Disc_List_Previous.htm";
	private final static String NEXT_DISC_LIST_TEMPLATE = "Conf_Disc_List_Next.htm";
	private final static String NEW_DISC_TEMPLATE = "Conf_Disc_New_Button.htm";
	private final static String ADMIN_LINK_TEMPLATE = "Conf_Disc_Admin_Link.htm";

	String HTML_TEMPLATE ;
	String A_HREF_HTML ;   // The code snippet where the aHref list with all discussions
	//	int DISCSHOWCOUNTER = 20 ;
	// will be placed.

	public void doPost(HttpServletRequest req, HttpServletResponse res)
	throws ServletException, IOException {

		// Lets validate the session, e.g has the user logged in to Janus?
		if (super.checkSession(req,res) == false)	return ;

		// Lets get the user object
		imcode.server.User user = super.getUserObj(req,res) ;
		if(user == null) return ;

		if ( !isUserAuthorized( req, res, user ) ) {
			return;
		}

		// Lets add the standard SESSION parameters
		Properties params = this.getSessionParameters(req) ;

		// Lets get the buttonparameters  and validate all parameters
		if (super.checkParameters(req, res, params) == false) {
			/*
			String header = "ConfDisc servlet. " ;
			String msg = params.toString() ;
			ConfError err = new ConfError(req,res,header,1) ;
			*/
			return ;
		}

		// Lets get serverinformation
		String host = req.getHeader("Host") ;
		String imcServer = Utility.getDomainPref("userserver",host) ;
		String confPoolServer = Utility.getDomainPref("conference_server",host) ;

		RmiConf rmi = new RmiConf(user) ;

		// ********* UPDATE DISCUSSIONS ********
		if (req.getParameter("UPDATE") != null) {
			// log("NU uppdaterar vi discussions") ;
			// Lets get the forum_id and set our session object before updating
			Properties reqParams = this.getRequestParameters(req) ;
			String aForumId = reqParams.getProperty("FORUM_ID") ;
			String discIndex = params.getProperty("DISC_INDEX") ;
			String changeForum = req.getParameter("CHANGE_FORUM");

			//	RmiConf rmi = new RmiConf(user) ;
			HttpSession session = req.getSession(false) ;
			if(session != null) {
				String latestDiscId = rmi.execSqlProcedureStr(confPoolServer, "GetLastDiscussionId " +
					params.getProperty("META_ID") + ", " + aForumId) ;

				if(latestDiscId == null) {
					log("LatestDiscID saknas, det kan saknas diskussioner i forumet:" + aForumId) ;
					latestDiscId  = "-1" ;
				}
				if(discIndex == null) {
					log("DiscIndex var null:" + discIndex) ;
					discIndex  = "0" ;
				} else {
					//lets set disc index to 0 then changing forum
					if ( changeForum != null ) {
						discIndex  = "0";
					}
				}
				session.putValue("Conference.disc_id", latestDiscId) ;
				session.putValue("Conference.forum_id", aForumId) ;
				session.putValue("Conference.disc_index", discIndex) ;
			}

			//	log("LastLoginDate: " + session.getValue("Conference.last_login_date")) ;
			// Lets redirect to the servlet which holds in us.
			String where = MetaInfo.getServletPath(req) ;

			res.sendRedirect(where + "ConfDiscView") ;
			return ;
		}

		// ********* ADD DISCUSSIONS ********
		if (req.getParameter("ADD") != null) {
			// log("Nu redirectar vi till ConfAdd") ;
			String where = MetaInfo.getServletPath(req) ;
			res.sendRedirect(where + "ConfAdd?ADDTYPE=Discussion") ;
			return ;
		}

		// ********* VIEW NEXT DISCUSSIONS ********
		if(( req.getParameter("NEXT") != null || req.getParameter("NEXT.x") != null ) ) {
			//if (req.getParameter("NEXT") != null) {

			// Lets get the total nbr of discs in the forum
			// RmiConf rmi = new RmiConf(user) ;
			String sql = "GetNbrOfDiscs " + params.getProperty("FORUM_ID") ;
			String nbrOfDiscsStr = rmi.execSqlProcedureStr(confPoolServer, sql) ;
			int nbrOfDiscs = 0 ;

			// Lets get the nbr of discussions to show. If it does not contain any
			// discussions, 20 will be returned by default from db
			String showDiscsStr = rmi.execSqlProcedureStr(confPoolServer, "GetNbrOfDiscsToShow " +
				params.getProperty("FORUM_ID"))  ;
			int showDiscsCounter = Integer.parseInt(showDiscsStr) ;
			// log("Nbr of discs to show: " + showDiscsCounter) ;

			try {
				nbrOfDiscs = Integer.parseInt(nbrOfDiscsStr) ;
			} catch (Exception e) {
				nbrOfDiscs = 0 ;
				log("GetNbrOfDiscs returned null") ;
			}

			int currIndex = this.getDiscIndex(req) ;
			if( currIndex + showDiscsCounter < nbrOfDiscs ) {
				this.increaseDiscIndex(req, showDiscsCounter) ;
				// log("Ok, vi höjer indexräknaren") ;
			}

			// Lets redirect to the servlet which holds in us.
			String where = MetaInfo.getServletPath(req) ;
			res.sendRedirect(where + "ConfDiscView") ;
			return ;
		}

		// ********* VIEW PREVIOUS DISCUSSIONS ********
		if(( req.getParameter("PREVIOUS") != null || req.getParameter("PREVIOUS.x") != null ) ) {
			//if (req.getParameter("PREVIOUS") != null) {
			//RmiConf rmi = new RmiConf(user) ;

			// Lets get the nbr of discussions to show. If it does not contain any
			// discussions, 20 will be returned by default from db
			String showDiscsStr = rmi.execSqlProcedureStr(confPoolServer, "GetNbrOfDiscsToShow " +
				params.getProperty("FORUM_ID"))  ;
			int showDiscsCounter = Integer.parseInt(showDiscsStr) ;
			// log("Nbr of discs to show: " + showDiscsCounter) ;

			this.decreaseDiscIndex(req, showDiscsCounter) ;
			// log("Currindex efter sänkning: " + this.getDiscIndex(req)) ;

			// Lets redirect to the servlet which holds in us.
			String where = MetaInfo.getServletPath(req) ;
			res.sendRedirect(where + "ConfDiscView") ;
			return ;
		}

		// ********* SEARCH ********
		if (req.getParameter("SEARCH") != null) {
			//	log("Nu är vi i search") ;
			params = this.getSearchParameters(req, params) ;
			String searchMsg = "" ;
			String sqlAnswer[] = null ;
			boolean searchParamsOk  = true ;
			String currForum = "" ;

			// Lets get the forumname for the current forum

			String aForumId = params.getProperty("FORUM_ID") ;
			String sqlForumName = "GetForumName " + aForumId ;
			currForum = "" + rmi.execSqlProcedureStr(confPoolServer, "GetForumName " + aForumId) ;

			//lets get metaId befor buildSearchDateParams destroys that info (happens if error in dateformat)
			String metaId = params.getProperty("META_ID");

			// Lets validate the searchdates. If not correct then get a message and show user
			// 42=En sökdatumsträng var felaktig!
			params = this.buildSearchDateParams(params) ;
			//log("Efter buildSearchDateParams: " + params) ;

			if (params == null) {
				log("An illegal searchdateparameter was sent to server") ;
				ConfError msgErr = new ConfError() ;
				searchMsg = msgErr.getErrorMessage(req, 42) ;
				searchParamsOk = false ;
			}


			// Lets validate the searchwords. If not correct then get a message and show user
			// 40=En sökparameter saknades! Du måste ange minst ett sökord!
			if( searchParamsOk) {
				boolean itsOk = this.checkSearchWords(params) ;
				//this.log("ItsOk: " + itsOk) ;
				if (!itsOk ) {
					ConfError msgErr = new ConfError() ;
					searchMsg = msgErr.getErrorMessage(req, 40) ;
					//log("searchMsg: " + searchMsg) ;
					searchParamsOk = false ;
				}
			}

			//log("Ok, we have passed test 1 and 2") ;
			//log("searchParamsOk: " + searchParamsOk) ;
			// this.log("SEARCHWORD: " + params.getProperty("SEARCH").trim()) ;


			// Lets check if everything is alright
			if( searchParamsOk ) {
				//String metaId = params.getProperty("META_ID") ;
				//aForumId = params.getProperty("FORUM_ID") ;
				String searchW = params.getProperty("SEARCH") ;
				String category = params.getProperty("CATEGORY") ;
				String frDate = params.getProperty("FR_DATE") ;
				String toDate = params.getProperty("TO_DATE") ;


				// IF WE ARE LOOKING FOR USERS ACTIVITY
				if(params.getProperty("CATEGORY").equals("2")) {
					String sqlQ = this.buildSearchUserSql(params) ;
					//log("SQL: " + sqlQ) ;
					//RmiConf rmi = new RmiConf(user) ;
					sqlAnswer = rmi.execSqlQueryExt(confPoolServer, sqlQ) ;
				}
				else {
					// Ok, Lets build the search string
					//RmiConf rmi = new RmiConf(user) ;
					String sqlQ = "SearchText " + metaId +", "+ aForumId + ", " ;
					sqlQ += category  + ", " + "'" + searchW + "'" + " ," ;
					sqlQ += "'" + frDate  + "', '" + toDate + " 23:59:59" + "'" ;

					// log("Search SQL: " + sqlQ) ;
					sqlAnswer = rmi.execSqlProcedureExt(confPoolServer, sqlQ) ;
				} // End if
			} // End if

			//log("Ok, we have done a search!") ;
			//log("sqlAnswer:" + sqlAnswer) ;

			// Lets get the part of an html page, wich will be parsed for every a Href reference
			String templateLib = super.getExternalTemplateFolder(req) ;
			//	templateLib += getTemplateLibName(params.getProperty("META_ID")) ;
			String aHreHtmlFile = templateLib + A_HREF_HTML ;


			// Lets build our tags vector.
			Vector tagsV = this.buildTags() ;

			// Lets preparse all records, if any returned get an error mesage
			String allRecs = "" ;
			//	log("SqlAnswer: " + sqlAnswer) ;
			if( sqlAnswer != null ) {
				if( sqlAnswer.length > 0) {
					//for(int i = 0 ; i < sqlAnswer.length ; i++){
					//	log("SqlAnswer: " + i + " : " + sqlAnswer[i]) ;
					//}
					allRecs = preParse(req, sqlAnswer, tagsV, aHreHtmlFile, "" ) ;
					if(allRecs == null ) {
						ConfError msgErr = new ConfError() ;
						allRecs = msgErr.getErrorMessage(req, 41) ;
						msgErr = null ;
					}
					else{
					}
				}
				else {
				}
			}
			else {
				// log("SqlAnswer = null") ;
				// Ok, we coulnt find anything
				if (searchParamsOk ) {
					ConfError msgErr = new ConfError() ;
					allRecs = msgErr.getErrorMessage(req, 41) ;
					msgErr = null ;
				}
			}

			//log("Ok, we passed the sqlquestioning") ;
			//log("ALLRECS: " + allRecs) ;
			//log("searchMsg: " + searchMsg) ;

			// Lets build the Responsepage
			VariableManager vm = new VariableManager() ;
			if (allRecs == null || allRecs.equals("")  )
				vm.addProperty("A_HREF_LIST", searchMsg) ;
			else
				vm.addProperty("A_HREF_LIST", allRecs) ;

			//lets show newbutton if user has more than readrights
			String newDiscButton = "&nbsp;";
			int intMetaId = Integer.parseInt( metaId );
			if ( IMCServiceRMI.checkDocRights( imcServer, intMetaId, user ) &&
			   IMCServiceRMI.checkDocAdminRights( imcServer, intMetaId, user ) ) {

				VariableManager vmButtons = new VariableManager();
				vmButtons.addProperty( "#SERVLET_URL#", MetaInfo.getServletPath( req ) );
				vmButtons.addProperty( "#IMAGE_URL#", this.getExternalImageFolder( req ) );
				HtmlGenerator newButtonHtmlObj = new HtmlGenerator( templateLib, this.NEW_DISC_TEMPLATE );
				newDiscButton = newButtonHtmlObj.createHtmlString( vmButtons, req );
			}

			vm.addProperty("CURRENT_FORUM_NAME", currForum  );
			vm.addProperty("PREVIOUS_BUTTON", "&nbsp;"  );
			vm.addProperty("NEXT_BUTTON", "&nbsp;"  );
			vm.addProperty("NEW_DISC_BUTTON", newDiscButton );
			vm.addProperty( "ADMIN_LINK_HTML", this.ADMIN_LINK_TEMPLATE );
			this.sendHtml(req,res,vm, HTML_TEMPLATE) ;
			// log("ConfDisc doPost är färdig") ;
			return ;
		}
	} // DoPost

	/**
	doGet
	*/
	public void doGet(HttpServletRequest req, HttpServletResponse res)
	throws ServletException, IOException {

		// Lets validate the session, e.g has the user logged in to Janus?
		if (super.checkSession(req,res) == false)	return ;

		// Lets get the standard SESSION parameters and validate them
		Properties params = this.getSessionParameters(req) ;
		if (super.checkParameters(req, res, params) == false) {
			/*
			String header = "ConfDisc servlet. " ;
			String msg = params.toString() ;
			ConfError err = new ConfError(req,res,header,1) ;
			*/
			return ;
		}

		// Lets get the user object
		imcode.server.User user = super.getUserObj(req,res) ;
		if(user == null) return ;

		if ( !isUserAuthorized( req, res, user ) ) {
			return;
		}

		// Lets get serverinformation
		String host = req.getHeader("Host") ;
		String imcServer = Utility.getDomainPref("userserver",host) ;
		String confPoolServer = Utility.getDomainPref("conference_server",host) ;

		RmiConf rmi = new RmiConf(user) ;

		// Lets get the url to the servlets directory
		String servletHome = MetaInfo.getServletPath(req) ;

		// Lets get parameters
		String aMetaId = params.getProperty("META_ID") ;
		int metaId = Integer.parseInt( aMetaId );
		String aForumId = params.getProperty("FORUM_ID") ;
		String aLoginDate = params.getProperty("LAST_LOGIN_DATE") ;
		// log("GetLastLoginDate: " + aLoginDate ) ;

		// Lets get path to the imagefolder. http://dev.imcode.com/images/se/102/ConfDiscNew.gif
		String imagePath = super.getExternalImageFolder(req) + "ConfDiscNew.gif";

		// Lets get the part of an html page, wich will be parsed for every a Href reference
		String aHrefHtmlFile = super.getExternalTemplateFolder(req) + A_HREF_HTML ;

		// Lets get all Discussions
		String sqlStoredProcOld = "GetAllDiscussions " + aMetaId + ", " + aForumId +", '"+ aLoginDate + "'"  ;
		//log("GetAllDiscussionsSQL: " + sqlStoredProcOld) ;
		String sqlAnswer[] = rmi.execSqlProcedureExt(confPoolServer, sqlStoredProcOld ) ;

		//lets generate the buttons that should appear
		String templateLib = this.getExternalTemplateFolder( req );
		String previousButton = "&nbsp;";
		String nextButton = "&nbsp;";
		String newDiscButton = "&nbsp;";

                // Lets bee ready to create button and new flags
		VariableManager vmButtons = new VariableManager();
		vmButtons.addProperty( "#SERVLET_URL#", MetaInfo.getServletPath( req ) );
		vmButtons.addProperty( "#IMAGE_URL#", this.getExternalImageFolder( req ) );

		// Lets preparse all records
		String allRecs  = "" ;
		// Lets get the start record position in the array
		int discIndexPos = this.getDiscIndex(req);
		int showDiscsCounter = 0;
		if( sqlAnswer != null ) {

			// Lets build our tags vector.
			Vector tagsV = this.buildTags() ;

			// Lets get the start record position in the array
			//int discIndexPos = this.getDiscIndex(req) ;
			// log("DOGET discindex: " + discIndexPos) ;

			// Lets get the nbr of discussions to show. If it does not contain any
			// discussions, 20 will be returned by default from db
			String showDiscsStr = rmi.execSqlProcedureStr(confPoolServer, "GetNbrOfDiscsToShow " +
				params.getProperty("FORUM_ID"))  ;
			//int showDiscsCounter = Integer.parseInt(showDiscsStr) ;
			showDiscsCounter = Integer.parseInt(showDiscsStr) ;
			int recStartPos = this.getRecordPos(sqlAnswer, discIndexPos, req ) ;
			int recStopPos = this.getRecordPos(sqlAnswer, discIndexPos + showDiscsCounter, req ) ;

			// log("StartRecordPos: " + recStartPos ) ;
			// log("StopRecordPos: " + recStopPos ) ;

			// Lets create an array
			String[] newArr = this.buildArray(sqlAnswer, recStartPos, recStopPos) ;
			// log("NEW ARR LENGTH: " + newArr.length ) ;
			if( newArr.length > 0)
				allRecs = preParse(req, newArr, tagsV, aHrefHtmlFile, imagePath ) ;

			//lets show previousbutton if not first set of discussions
			if ( discIndexPos != 0 ) {
				HtmlGenerator previousButtonHtmlObj = new HtmlGenerator( templateLib, this.PREVIOUS_DISC_LIST_TEMPLATE );
				previousButton = previousButtonHtmlObj.createHtmlString( vmButtons, req );
			}
			//lets show nextbutton if not last set of discussions
			if ( ( sqlAnswer.length/8 -1 ) > (discIndexPos + showDiscsCounter )  ) {
				HtmlGenerator nextButtonHtmlObj = new HtmlGenerator( templateLib, this.NEXT_DISC_LIST_TEMPLATE );
				nextButton = nextButtonHtmlObj.createHtmlString( vmButtons, req );
			}

		}

		// Lets get the forumname for the current forum
		String currForum = "" + rmi.execSqlProcedureStr(confPoolServer, "GetForumName " + params.getProperty("FORUM_ID")) ;
		// log("CurrentForum: " + currForum) ;

		//lets show newdiscbutton if user has more than readrights
		if ( IMCServiceRMI.checkDocRights( imcServer, metaId, user ) &&
		   IMCServiceRMI.checkDocAdminRights( imcServer, metaId, user ) ) {
			HtmlGenerator newButtonHtmlObj = new HtmlGenerator( templateLib, this.NEW_DISC_TEMPLATE );
			newDiscButton = newButtonHtmlObj.createHtmlString( vmButtons, req );
		}

		VariableManager vm = new VariableManager() ;
		vm.addProperty("PREVIOUS_BUTTON", previousButton  );
		vm.addProperty("NEXT_BUTTON", nextButton  );
		vm.addProperty("NEW_DISC_BUTTON", newDiscButton );
		vm.addProperty("A_HREF_LIST", allRecs  ) ;
		vm.addProperty("CURRENT_FORUM_NAME", currForum) ;
		vm.addProperty( "ADMIN_LINK_HTML", this.ADMIN_LINK_TEMPLATE );
		this.sendHtml(req,res,vm, HTML_TEMPLATE) ;
		//	this.showSession(req) ;
		//log("ConfDisc doGet är färdig") ;
	} //DoGet

	/**
	Parses the Extended array with the htmlcode, which will be parsed
	for all records in the array
	*/
	public String preParse (HttpServletRequest req, String[] DBArr, Vector tagsV,
		String htmlCodeFile, String imagePath)  throws ServletException, IOException {
		String htmlStr = "" ;
		try {
			// Lets get the nbr of cols
			int nbrOfCols = Integer.parseInt(DBArr[0]) ;
			// log("NbrOfCols: " + nbrOfCols) ;

			// Lets do for all records...
			for(int i = nbrOfCols+1; i<DBArr.length; i += nbrOfCols) {
				Vector dataV = new Vector(9) ;
				String oneParsedRecordStr = "" ;

				// Lets create one record... Get all fields for that record
				for(int j=i; j<i+nbrOfCols ; j++) {
					dataV.add(DBArr[j]) ;
				}
				// Lets insert the aHrefCode to the end of the vector
				dataV.add( MetaInfo.getServletPath(req) + "ConfReply?") ;

				// Lets check if the discussions should have a new bitmap in front of them
				// Lets first check against the logindate in the sql, then check the
				// list with users clicked ones
				// this.checkViewStatus(req, dataV)  ; // the discId
				if (this.discViewStatus(req, dataV) == true ) {
					//log("Show the NewFlag!") ;
					//dataV.setElementAt(this.setNewDiscFlag(imagePath) , 0) ;
					String templateLib = this.getExternalTemplateFolder( req );
					HtmlGenerator htmlObj = new HtmlGenerator( templateLib, this.NEW_DISC_FLAG_TEMPLATE );
					VariableManager newFlagVM = new VariableManager();
					newFlagVM.addProperty( "#IMAGE_URL#", imagePath );
					String newFlag = htmlObj.createHtmlString( newFlagVM, req );

					dataV.setElementAt( newFlag , 0) ;
				}
				else
					dataV.setElementAt("" , 0) ;

				// Lets show the vectors
				// this.showIt(tagsV, dataV) ;

				// Lets parse one record
				oneParsedRecordStr = this.parseOneRecord(tagsV, dataV, htmlCodeFile) ;
				// log("Ett record: " + oneParsedRecordStr);
				htmlStr += oneParsedRecordStr ;
			} // end of the big for
		} catch(Exception e) {
			log("Error in Preparse") ;
			log(e.getMessage()) ;
			return null ;
		}
		return htmlStr ;
	} // End of

	/**
	Returns true if we have seen the discussion before, otherwise false.
	Updates the sessionobject as well
	**/

	public boolean discViewStatus( HttpServletRequest req, Vector dataV) throws ServletException, IOException {

		// Get the session and the list
		HttpSession session = req.getSession(true);
		Properties viewedDiscs = (Properties) session.getValue("Conference.viewedDiscList") ;

		// Lets get info from the db. the format on the vector is:
		// newFlag, discussion_id, create_date, headline, count_replies, first_name, last_name , updated_date
		String sqlNewDiscFlag = dataV.get(0).toString() ;
		String sqlDiscId = dataV.get(1).toString() ;
		String sqlDiscDate = dataV.get(7).toString() ;

		if(sqlNewDiscFlag.equals("1") ) {
			// log("NY diskussion") ;
			// Lets check if we have seen the discussion in this session, if we have
			// not seen it, return true
			if( viewedDiscs.get(sqlDiscId) == null ) {
				return true ;
			}
			else { // we have seen it in this session, lets check the date when we
				// saw it against when it was updated
				// Lets get the date when we viewed the discussion
				boolean newerDisc = compareDates(viewedDiscs.getProperty(sqlDiscId), sqlDiscDate) ;
				if(newerDisc) {
					// log("Gamlare diskussion") ;
					return true ;
				}
				//log("SAG: " + sqlDiscId + ": " + sqlDiscDate) ;
				return false ;
			}
		}
		return false ;
	}

	/**
	Compare dates. Takes an string of the expected form and compares if its newer or later
	than the other. returns true if the firstdate is before the second date. If its
	equals or later it returns false. Observe the precision which is down to
	minutes, not seconds. It means that comparing two dates with the same minute wil
	l return false!
	*/

	protected boolean compareDates(String date1, String date2) {
		// Lets fix the date
		// java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat() ;
		java.text.SimpleDateFormat formatter= new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss") ;
		GregorianCalendar firstDate = new GregorianCalendar() ;
		GregorianCalendar secDate = new GregorianCalendar() ;

		try {
			// Create a calendar, by parse to a date and then create calendar
			firstDate.setTime(formatter.parse(date1)) ;
			secDate.setTime(formatter.parse(date2)) ;

			// Get the seconds from the datestring,
			firstDate.set(Calendar.SECOND, this.getDateItem(date1,":", 3)) ;
			secDate.set(Calendar.SECOND, this.getDateItem(date2, ":", 3)) ;
			//secDate = formatter.parse(date2) ;

			//log("firstDate: " + firstDate.toString() ) ;
			//log("secDate: " + firstDate.toString() ) ;
		} catch(java.text.ParseException e) {
			log(e.getMessage()) ;
			log("Invalid date1: " + date1) ;
			log("Invalid date2: " + date2) ;
			return true ;
		}
		//
		if ( firstDate.before(secDate)) {
			//log( date1 + " innan " + date2) ;
			return true ;
		}

		return false ;
		// return firstDate.before(secDate) ;
	}

	/**
	Returns wanted item from string, returns empty if itemnumber not found
	*/

	public int getDateItem(String str, String delim, int itemNbr) {
		String tmpVal = "";
		itemNbr = itemNbr - 1 ;
		StringTokenizer st = new StringTokenizer(str, delim) ;
		int counter = 0 ;
		int retVal = 0 ;

		while (st.hasMoreTokens()) {
			String tmp = st.nextToken() ;
			if( counter == itemNbr)
				tmpVal = tmp ;
			counter = counter + 1 ;
		}

		try {
			retVal = Integer.parseInt(tmpVal) ;
		} catch(NumberFormatException e) {
			log("Error in getDateItem!") ;
			retVal = 0 ;
		}
		return retVal ;
	}


	/**
	Shows the session variables
	**/
	public boolean showSession( HttpServletRequest req) {
		HttpSession session = null ;
		try {
			session = req.getSession(false) ;
			if(session != null) {
				String[] arr = session.getValueNames() ;
				for( int i = 0 ; i < arr.length ; i++ ) {
					log(arr[i] + " : " + session.getValue(arr[i]).toString()) ;
					// log("Value: " + arr[i]) ;
				}

			}
			} catch(Exception e ) {
				log("Showsession failed!") ;
			return false ;
			}
		return true ;
	}

	/**
	Increases the current discussion index. If somethings happens, zero will be set.
	**/
	public boolean increaseDiscIndex( HttpServletRequest req, int incFactor ) {
		HttpSession session = null ;
		try {
			session = req.getSession(false) ;
			if(session != null) {
				String indexStr = (String) session.getValue("Conference.disc_index") ;
				int anInt = Integer.parseInt(indexStr) + incFactor ;
				session.putValue("Conference.disc_index" , "" + anInt) ;
			}
			} catch(Exception e ) {
				session.putValue("Conference.disc_index" , "0") ;
			log("IncreaseIndex failed!") ;
			return false ;
			}
		return true ;
	}

	/**
	Decreases the current discussion index. If somethings happens, zero will be set.
	**/
	public boolean decreaseDiscIndex( HttpServletRequest req, int incFactor ) {
		HttpSession session = null ;
		try {
			session = req.getSession(false) ;
			if(session != null) {
				String indexStr = (String) session.getValue("Conference.disc_index") ;
				int anInt = Integer.parseInt(indexStr) - incFactor ;
				if (anInt < 0) anInt = 0 ;
				session.putValue("Conference.disc_index" , "" + anInt) ;
			}
			} catch(Exception e ) {
			session.putValue("Conference.disc_index" , "0") ;
			log("DecreaseIndex failed!") ;
			return false ;
			}
		return true ;
	}

	/**
	Returns the current discussion index. If somethings happens, zero will be returned.
	*/
	public int getDiscIndex( HttpServletRequest req) {
		try {
			HttpSession session = req.getSession(false) ;
			if(session != null) {
				String indexStr = (String) session.getValue("Conference.disc_index") ;
				int anInt = Integer.parseInt(indexStr) ;
				return anInt ;
			}
		} catch(Exception e ) {
			log("GetDiscIndex failed!") ;
			return 0 ;
		}
		return 0 ;
	}

	/**
	Sets the current discussion index.
	*/
	public boolean setDiscIndex( HttpServletRequest req, int newIndex) {
		try {
			HttpSession session = req.getSession(false) ;
			if(session != null) {
				session.putValue("Conference.disc_index", "" + newIndex ) ;
				return true ;
			}
		} catch(Exception e ) {
			log("SetDiscIndex failed!") ;
			return false ;
		}
		return false ;
	}


	/**
	Returns the startPos for a record in an extended array. observe
	that the extended information wont be included in this information
	**/
	public int getRecordPos( String[] allRecs, int recordNum, HttpServletRequest req ) {
		int oneRecordLen = Integer.parseInt(allRecs[0]) ;
		int extendedInfoLen = oneRecordLen +1 ;

		int nbrOfRecords = ( allRecs.length - extendedInfoLen) / oneRecordLen ;
		int recordPos =  recordNum * oneRecordLen + extendedInfoLen ;

		//	log("*********************") ;
		//	log("WANTED Recordnumber: " + recordNum) ;
		//	log("Total NbrOfRecords: " + nbrOfRecords) ;
		//	log("Total arraylength: " + allRecs.length ) ;
		//	log("OneRecordLen: " + oneRecordLen) ;
		//	log("RecordPos before validation: " + recordPos) ;

		// Lets check if recordPos is bigger than the array size
		if( recordNum > nbrOfRecords ) {
			// log("Det finns inte så många records") ;
			recordPos = allRecs.length ;
			// this.setDiscIndex(req , nbrOfRecords) ;
		}

		// Lets check if recordPos is smaller than the array size
		if( recordPos < 0 ) {
			//log("recordPos får inte vara mindre än extendedInfoLen") ;
			recordPos = extendedInfoLen ;
		}

		//		log("RecordPos after validation: " + recordPos) ;
		return recordPos ;
	}

	/**
	Creates a new array with the according size. Inclusive extended information
	*/
	public String[] buildArray( String[] arr, int start, int stop) {
		String[] retArr = null ;
		// log("StartPos: " + start ) ;
		// log("StopPos: " + stop ) ;
		// log("Arraysize: " + arr.length ) ;
		int counter = 0 ;

		try {
			// Ok, its an extended array, lets first copy the extended information
			int extendedInfoLen = Integer.parseInt(arr[0]) + 1 ;
			retArr = new String[stop + extendedInfoLen - start] ;

			// Lets copy the extended information
			for( counter = 0 ; counter < extendedInfoLen ; counter++ ) {
				retArr[counter] = arr[counter] ;
			}


			// Lets copy the information
			for(int i = start ; i < stop ; i++ ) {
				//	log("i: " + i + " counter: " + counter) ;
				retArr[counter] = arr[i] ;
				counter++ ;
			}

		} catch(ArrayIndexOutOfBoundsException e) {
			log("Index out of bounds! ") ;
			log("StartPos: " + start ) ;
			log("StopPos: " + stop ) ;
			log("SRC Arraysize: " + arr.length ) ;
			log("TARGET Arraysize: " + retArr.length ) ;
			return null ;
		}
		return retArr ;
	}




	/**
	Parses one record.
	*/
	public String parseOneRecord (String[] tags, String[] data, String htmlCodeFile) {

		Vector tagsV = super.convert2Vector(tags) ;
		Vector dataV = super.convert2Vector(data) ;
		return this.parseOneRecord(tagsV, dataV, htmlCodeFile) ;
	}


	/**
	Parses one record.
	*/
	public String parseOneRecord (Vector tagsV, Vector dataV, String htmlCodeFile) {

		String htmlStr = "" ;
		// Lets parse one aHref reference
		ParseServlet parser = new ParseServlet(htmlCodeFile, tagsV, dataV) ;
		String oneRecordsHtmlCode = parser.getHtmlDoc() ;
		return oneRecordsHtmlCode ;
	} // End of parseOneRecord


	/**
	Returns the users NewDiscussionFlag htmlcode. If something has happened in a discussion
	or a new discussion has took place, a bitmap will be shown in front of the discussion,
	otherwise nothing will occur.
	*/

	protected String setNewDiscFlag (String ImagePath)
	throws ServletException, IOException {

		// Lets get the information regarding the replylevel
		String imageStart = "<img src=\"" ;
		String imageEnd = "\"> " ;
		return imageStart + ImagePath + imageEnd ;
	}

	/**
	Collects the standard parameters from the SESSION object.
	**/

	public Properties getSessionParameters( HttpServletRequest req)
	throws ServletException, IOException {

		// Lets get the standard metainformation
		Properties reqParams  = super.getSessionParameters(req) ;

		// Lets get the session
		HttpSession session = req.getSession(false) ;
		if(session != null) {
			// Lets get the parameters we know we are supposed to get from the request object
			String forumId = ( (String) session.getValue("Conference.forum_id")==null) ? "" : ((String) session.getValue("Conference.forum_id")) ;
			//	String discId = (	(String) session.getValue("Conference.forum_id")==null) ? "" : ((String) session.getValue("Conference.forum_id")) ;
			String discId = (	(String) session.getValue("Conference.disc_id")==null) ? "" : ((String) session.getValue("Conference.disc_id")) ;
			String lastLogindate = (	(String) session.getValue("Conference.last_login_date")==null) ? "" : ((String) session.getValue("Conference.last_login_date")) ;
			String discIndex = (	(String) session.getValue("Conference.disc_index")==null) ? "" : ((String) session.getValue("Conference.disc_index")) ;

			reqParams.setProperty("DISC_INDEX", discIndex) ;
			reqParams.setProperty("LAST_LOGIN_DATE", lastLogindate) ;
			reqParams.setProperty("FORUM_ID", forumId) ;
			reqParams.setProperty("DISC_ID", discId) ;

		}
		return reqParams ;
	}

	/**
	Collects the parameters from the request object. This function will get all the possible
	parameters this servlet will be able to get. If a parameter wont be found, the session
	parameter will be used instead, or if no such parameter exist in the session object,
	a key with no value = "" will be used instead.
	**/

	public Properties getRequestParameters( HttpServletRequest req)
	throws ServletException, IOException {

		Properties reqParams = new Properties() ;

		// Lets get our own variables. We will first look for the discussion_id
		// in the request object, if not found, we will get the one from our session object

		String confForumId = req.getParameter("forum_id");
		String discIndex = "" ;
		HttpSession session = req.getSession(false) ;
		if (session != null) {
			if(confForumId == null)
				confForumId =	(String) session.getValue("Conference.forum_id") ;
			discIndex = (String) session.getValue("Conference.disc_index") ;
			if(discIndex == null || discIndex.equalsIgnoreCase("null"))	discIndex = "0" ;
		}
		reqParams.setProperty("FORUM_ID", confForumId) ;
		reqParams.setProperty("DISC_INDEX", discIndex) ;
		return reqParams ;
	}

	/**
	Collects the parameters used to detect the buttons from the request object. Checks
	if the Properties object is null, if so it creates one, otherwise it uses the
	object passed to it.
	**/

	public Properties getSearchParameters( HttpServletRequest req, Properties params)
	throws ServletException, IOException {

		//Lets get the search criterias
		String cat = (req.getParameter("CATEGORY")==null) ? "" : (req.getParameter("CATEGORY")) ;
		String search = (req.getParameter("SEARCH")==null) ? "" : (req.getParameter("SEARCH")) ;
		String fromDate = (req.getParameter("FR_DATE")==null) ? "" : (req.getParameter("FR_DATE")) ;
		String fromVal = (req.getParameter("FR_VALUE")==null) ? "" : (req.getParameter("FR_VALUE")) ;

			String toDate = (req.getParameter("TO_DATE")==null) ? "" : (req.getParameter("TO_DATE")) ;
		String toVal = (req.getParameter("TO_VALUE")==null) ? "" : (req.getParameter("TO_VALUE")) ;
		//	String searchButton = (req.getParameter("BUTTON_SEARCH")==null) ? "" : (req.getParameter("BUTTON_SEARCH")) ;

		params.setProperty("CATEGORY", super.verifySqlText(cat.trim())) ;
		params.setProperty("SEARCH", super.verifySqlText(search.trim())) ;
		params.setProperty("FR_DATE", super.verifySqlText(fromDate.trim())) ;
		params.setProperty("TO_DATE", super.verifySqlText(toDate.trim())) ;
		params.setProperty("FR_VALUE", super.verifySqlText(fromVal.trim())) ;
		params.setProperty("TO_VALUE", super.verifySqlText(toVal.trim())) ;

		//	params.setProperty("BUTTON_SEARCH", searchButton) ;
		return params ;
	}


	/**
	Builds the tagvector used for parse one record.
	*/
	protected Vector buildTags() {

		// Lets build our tags vector.
		Vector tagsV = new Vector() ;
		tagsV.add("#NEW_DISC_FLAG#") ;
		tagsV.add("#DISC_ID#") ;
		tagsV.add("#A_DATE#") ;
		tagsV.add("#HEADLINE#") ;
		tagsV.add("#C_REPLIES#") ;
		tagsV.add("#FIRST_NAME#") ;
		tagsV.add("#LAST_NAME#") ;
		tagsV.add("#LAST_UPDATED#") ;    // The discussion_update date
		tagsV.add("#REPLY_URL#") ;
		return tagsV ;
	} // End of buildstags


	/**
	show the tag and the according data
	**/
	protected void showIt(Vector tags, Vector data) {

		log("***********") ;
		if(tags.size() != data.size()) {
			log("Antalet stämmer inte ") ;
			log("Tags: " + tags.size()) ;
			log("Data: " + data.size()) ;
			// return ;
		}

		for (int i = 0 ; i < tags.size() ; i++) {
			String aTag = ( String) tags.elementAt(i) ;
			String aData = ( String) data.elementAt(i) ;
			log("" + i + ": " + aTag +" --> " + aData) ;

		}


	} // End of showit




	/**
	Detects paths and filenames.
	*/

	public void init(ServletConfig config)
	throws ServletException {
		super.init(config);
		HTML_TEMPLATE = "Conf_Disc.htm" ;
		A_HREF_HTML = "Conf_Disc_List.htm" ;
	}

	/**
	Log function, will work for both servletexec and Apache
	**/

	public void log( String str) {
		super.log(str) ;
		System.out.println("ConfDisc: " + str ) ;
	}

	/**
	check the Search date Parameters
	- if startdate is empty, OR the standardvalue is sent, yesterdays date will be used
	- if enddate is empty, OR the standardvalue is sent,todays date will be used instead.
	Wont fix time parameters!!!
	does not fix form '2000-02--04'
	*/

	protected Properties buildSearchDateParams(Properties p) {

		// Lets take care of "igår" and "idag" todays date in a GregorianCalendar object
		// if "idag" is 2000-05-01, then it means at the time 00.00.
		// So thats why we add a day, so 2000-05-01 -> 2000-05-02 to cover the hole day!
		GregorianCalendar today = new GregorianCalendar();
		today.set(Calendar.DATE, (today.get(Calendar.DATE)+1)) ;
		int tYear = today.get(Calendar.YEAR) ;
		int tMonth = 1 + today.get(Calendar.MONTH) ;
		int tDay = today.get( Calendar.DATE ) ;
		//log("tDay is: " + tDay) ;

		// Lets change to yesterday
		today.set(Calendar.DATE, (today.get(Calendar.DATE)-2)) ;
		int yYear = today.get(Calendar.YEAR) ;
		int yMonth = 1 + today.get(Calendar.MONTH ) ;
		int yDay = today.get(Calendar.DATE) ;

		// Lets analyze the startdate params. if it is "idag" resp "igår"
		if(p.getProperty("TO_DATE").equals("") || p.getProperty("TO_DATE").equalsIgnoreCase(p.getProperty("TO_VALUE")) )
			p.setProperty("TO_DATE", "" + tYear +"-"+ tMonth +"-"+ tDay  /*+ " 23:59:59"*/) ;
		if(p.getProperty("FR_DATE").equals("") || p.getProperty("FR_DATE").equalsIgnoreCase(p.getProperty("FR_VALUE")) )
			p.setProperty("FR_DATE", "" + yYear +"-"+ yMonth +"-"+ yDay /*+ " 00:00"*/ ) ;

		// Lets check if we can create a valid sql date from our date params
		java.sql.Date fromDate = null ;
		java.sql.Date toDate = null ;

		try {
			fromDate = java.sql.Date.valueOf(p.getProperty("FR_DATE")) ;
			toDate = java.sql.Date.valueOf(p.getProperty("TO_DATE")) ;
			//log("fromdate: " + fromDate) ;
			// log("toDate: " + toDate) ;
			} catch(Exception e ) {
       // log("Exception: " + e.getMessage()) ;
				log("Invalid FROM date: " + fromDate) ;
			log("Invalid TO date: " + toDate) ;
			// p = null ;
			return null ;
			}

		//log("FROM: " + p.getProperty("FR_DATE")) ;
		//log("TO: " + p.getProperty("TO_DATE")) ;

		return p ;
	}



	/**
	check the SearchWord Parameters
	*/

	protected boolean checkSearchWords(Properties p) {
		// Lets analyze the searchword
		String str = p.getProperty("SEARCH").trim() ;
		//this.log("SEARCHWORD: " + str) ;
		if(str.equalsIgnoreCase("")) {
			//this.log("No searchword was entered!") ;
			return false ;
		}

		if(str.length() <=2) {
			//this.log("No searchword was entered!") ;
			return false ;
		}

		return true ;
	}

	/**
	Builds the big sql string which is used when the user wants to search for a certain
	User.
	*/

	protected String buildSearchUserSql( Properties params) {
		String metaId = params.getProperty("META_ID") ;
		String aForumId = params.getProperty("FORUM_ID") ;
		String searchW = params.getProperty("SEARCH") ;
		String category = params.getProperty("CATEGORY") ;
		String frDate = params.getProperty("FR_DATE") ;
		String toDate = params.getProperty("TO_DATE") ;
		String sqlQ = "" ;

			sqlQ += "SELECT DISTINCT '0' as 'newflag', disc.discussion_id, SUBSTRING( CONVERT(char(16), rep.create_date,20), 6, 16) AS 'create_date' , rep.headline, disc.count_replies, usr.first_name, usr.last_name " + ", SUBSTRING( CONVERT(char(20), disc.last_mod_date,20),1, 20) as 'updated_date'" + '\n' ;
		sqlQ += "FROM replies rep, discussion disc, conf_users usr, conference conf, conf_forum cf, forum, conf_users_crossref crossref \n" ;
		sqlQ += "WHERE  rep.parent_id = disc.discussion_id \n" ;
		sqlQ += "AND disc.forum_id = forum.forum_id \n" ;
		sqlQ += "AND forum.forum_id = " + aForumId + "\n" ;
		sqlQ += "AND forum.forum_id = cf.forum_id \n" ;
		sqlQ += "AND cf.conf_id =" + metaId +"\n" ;
		// Lets check for the date
		sqlQ += "AND rep.create_date > '" + frDate + "' AND rep.create_date < '" + toDate + " 23:59:59' \n"	;
		//	sqlQ += "AND rep.user_id = usr.user_id \n" ;
		//	sqlQ += "AND usr.conf_id = conf.meta_id \n" ;
		//	sqlQ += "AND conf.meta_id = " + metaId +"\n" ;
		//	sqlQ += "AND rep.user_id IN \n" ;
		sqlQ += "AND rep.user_id = usr.user_id \n" ;
		sqlQ += "AND usr.user_id = crossref.user_id \n" ;
		sqlQ += "AND crossref.conf_id = " + metaId +"\n" ;
		sqlQ += "AND rep.user_id IN \n" ;
		sqlQ += buildSearchWordsSql(searchW) ;
		return sqlQ ;
	}

	/**
	Help method to build the search for user sql string. Builds the sql string
	which is used when the user wants to search for a certain	User.
	*/

	protected String buildSearchWordsSql( String str) {
		StringManager strMan = new StringManager(str, " ") ;
		String tmpStr = "" ;
		String tmpItem = "" ;
		String sqlStr = "(\n SELECT usr.user_id \n FROM conf_users usr \n WHERE " ;
		//sqlStr += "usr.first_name LIKE 'Rickard%' OR usr.first_name LIKE 'Larsson%' \n" ;
		// sqlStr += "OR usr.last_name LIKE 'Rickard%' OR usr.last_name LIKE 'Larsson%') \n " ;


			StringTokenizer st = new StringTokenizer(str);
		int counter = st.countTokens() ;
		//log("Counter: " + counter) ;
		for(int i = 0 ; i < counter ; i++ ) {
			tmpItem = st.nextToken() ;
			tmpStr += "usr.first_name LIKE '" + tmpItem + "%' OR usr.last_name LIKE '" + tmpItem + "%' \n ";
			if( (i+1) < counter )
				tmpStr += " OR " ;
		}
		sqlStr += tmpStr + ")" ;
		//	log("SqlStr: " + sqlStr) ;
		return sqlStr ;
	}





} // End of class











