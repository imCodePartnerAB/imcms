/*
 *
 * @(#)BillBoardReply.java
 *
 *
 *
 * Copyright (c)
 *
 */

import imcode.server.* ;
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
 * BillBoard_Reply_New_Comment.htm
 * BillBoard_Reply_Admin_Link.htm
 *
 * Html parstags in use:
 * #REPLY_BILL_ID#
 * #REPLY_HEADER#
 * #REPLY_TEXT#
 * #C_REPLIES#
 * #REPLY_DATE#
 * #SERVLET_URL#
 * #IMAGE_URL#
 * #NEW_REPLIE#
 * #REPLIE_RECORD#
 * #CURRENT_BILL_HEADER#
 * #ADMIN_LINK_HTML#
 *
 * stored procedures in use:
 * B_GetCurrentBill
 * B_GetBillHeader
 *
 * @version 1.2 20 Aug 2001
 * @author Rickard Larsson, Jerker Drottenmyr REBUILD TO BillBoardReply BY Peter Östergren
 *
 */


public class BillBoardReply extends BillBoard {//ConfReply
    private final static String CVS_REV = "$Revision$" ;
    private final static String CVS_DATE = "$Date$" ;

    private final static String NEW_COMMENT_TEMPLATE =  "BillBoard_Reply_New_Comment.htm";//Conf_Reply_New_Comment.htm
    private final static String ADMIN_LINK_TEMPLATE = "BillBoard_Reply_Admin_Link.htm";//Conf_Reply_Admin_Link.htm
    private final static String HTML_TEMPLATE_MAIL_SENT = "BillBoard_Reply_Mail_Sent.htm";
    private final static String HTML_PREVUE_TEMPLATE = "Billboard_forhandsgranska.html";
    private final static String HTML_TEMPLATE = "BillBoard_Reply.htm";
    private final static String RECS_HTML = "BillBoard_reply_list.htm";
    private final static String RECS_PREV_HTML = "BillBoard_Reply_List_prev.htm";
    private final static String HTML_TEMPLATE_START = "BillBoard_Reply_Welcome.htm";

    private final static String sectionId = "sectionId";
    private final static String header = "header";
    private final static String text = "text";
    private final static String email = "email";

    /**
       DoPost
    **/

    public void doPost(HttpServletRequest req, HttpServletResponse res)
	throws ServletException, IOException
    {
	//log("START BillBoardReply doPost");

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
	IMCServiceInterface imcref = IMCServiceRMI.getIMCServiceInterface(req) ;
	IMCPoolInterface billref = IMCServiceRMI.getBillboardIMCPoolInterface(req) ;

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
		userId = (String) session.getAttribute("BillBoard.user_id") ;
	    }

	    String metaId = params.getProperty("META_ID") ;
	    this.doGet(req, res) ;
	    return ;
	}

    }


    /**
       DoGet
    **/

    public void doGet(HttpServletRequest req, HttpServletResponse res)
	throws ServletException, IOException
    {
	//log("START BillBoardReply doGet");

	// Lets validate the session, e.g has the user logged in to Janus?
	if (super.checkSession(req,res) == false)	return ;

	HttpSession session = req.getSession(false) ;

	// Lets get the parameters and validate them
	Properties params = this.getParameters(req) ;
	if (checkParameters(req, res, params) == false) return ;

	// Lets get an user object
	imcode.server.User user = super.getUserObj(req,res) ;
	if(user == null) return ;

	if ( !isUserAuthorized( req, res, user ) ) {
	    return;
	}

	// Lets get serverinformation
	String host = req.getHeader("Host") ;
	IMCServiceInterface imcref = IMCServiceRMI.getIMCServiceInterface(req) ;
	IMCPoolInterface billref = IMCServiceRMI.getBillboardIMCPoolInterface(req) ;

	// Lets get path to the imagefolder. http://dev.imcode.com/images/102/ConfDiscNew.gif
	String imagePath = super.getExternalImageFolder(req) + "BillBoardExpert.gif" ;//
	// log("ImagePath: " + imagePath) ;


	// Lets get the part of an html page, wich will be parsed for every a Href reference
	File templateLib = super.getExternalTemplateFolder(req) ;
	File aSnippetFile = new File(templateLib, RECS_PREV_HTML) ;

	//ok here we se if we have a prevue to handle
	Hashtable billPrevData = (Hashtable) session.getAttribute("billPrevData");
	//log("PREVIEWMODE: "+req.getParameter("PREVIEWMODE"));
	if (billPrevData != null && req.getParameter("PREVIEWMODE")!=null)
	    { //ok PREVIEW-mode
		//log("ok PREVIEW-mode");
		String addHeader = (String)billPrevData.get(header );
		String addText = (String)billPrevData.get(text );
		String datum = billref.sqlProcedureStr("B_GetTime") ;
		//log(addHeader+"\n"+addText+"\n"+datum);
		String addType = req.getParameter("ADDTYPE");
		String addType2 = req.getParameter("ADDTYPE");
		//log("aaaaaaaaa: "+addType);
		//lets simulate the original sql answer
		String[] tempArr = {"7","bill_id","headline","text","repNr","","","","",addHeader,addText,"",datum,addType,addType2};
		//log("aSnippetFile: "+aSnippetFile);
		Vector tags = buildTagsV();
		tags.add("#ADD_TYPE#");
		tags.add("#ADD_TYPE2#");
		String currRec1 = preParse(req, tempArr, tags, aSnippetFile, imagePath) ;
		//	log(currRec1);
		VariableManager vm1 = new VariableManager() ;
		//vm1.addProperty("NEW_REPLIE", commentButton ) ;//ska bort
		vm1.addProperty("REPLIE_RECORD", currRec1  ) ;
		vm1.addProperty( "CURRENT_BILL_HEADER", billPrevData.get(header) ) ;
		vm1.addProperty( "ADMIN_LINK_HTML", "" );//måste byta template

		this.sendHtml(req,res,vm1, HTML_TEMPLATE) ;
		return;
	    }//end PREVIEW-mode

	int aMetaId = Integer.parseInt( params.getProperty("META_ID") );
	// Lets get the users userId
	Properties userParams = super.getUserParameters(user) ;
	String userId = "" ;

	String discId = params.getProperty("DISC_ID") ;

	// Lets update the sessions DISC_ID

	if(session != null  ) {
	    session.setAttribute("BillBoard.disc_id", discId) ;
	    userId = (String) session.getAttribute("BillBoard.user_id") ;
	}

	if (discId.equals("-1"))
	    { //ok lets get the start page
		VariableManager vm = new VariableManager() ;
		this.sendHtml(req,res,vm, HTML_TEMPLATE_START) ;
		return;
	    }

	if (req.getParameter("MAIL_SENT") != null)
	    {
		//ok lets get the sent msg page
		VariableManager vm = new VariableManager() ;
		this.sendHtml(req,res,vm, HTML_TEMPLATE_MAIL_SENT) ;
		return;
	    }


	String sqlQ = "B_GetCurrentBill " + discId ;//GetAllRepliesInDisc
	String sqlAnswer[] = billref.sqlProcedureExt(sqlQ) ;

	// Lets get the discussion header
	String discHeader = billref.sqlProcedureStr("B_GetBillHeader " + discId ) ;//GetDiscussionHeader

	if (discHeader == null || discId.equalsIgnoreCase("-1") )discHeader = " " ;


	// UsersSortOrderRadioButtons
	String metaId = params.getProperty("META_ID") ;
	int intMetaId = Integer.parseInt( metaId );

	// Lets preparse all records
	aSnippetFile = new File(templateLib, RECS_HTML) ;
	String currentRec = " " ;
	if (sqlAnswer != null) currentRec = preParse(req, sqlAnswer, buildTagsV(), aSnippetFile, imagePath) ;

	// Lets build the Responsepage

	//lets generate the buttons that should appear
	String commentButton = "&nbsp;";

	//lets show comment button if user has more than readrights
	if ( imcref.checkDocRights( intMetaId, user ) &&
	     imcref.checkDocAdminRights( intMetaId, user ) ) {

	    VariableManager vmButtons = new VariableManager();
	    vmButtons.addProperty( "#SERVLET_URL#", "" );
	    vmButtons.addProperty( "#IMAGE_URL#", this.getExternalImageFolder( req ) );
	    HtmlGenerator commentButtonHtmlObj = new HtmlGenerator( templateLib, this.NEW_COMMENT_TEMPLATE );
	    commentButton = commentButtonHtmlObj.createHtmlString( vmButtons, req );
	}

	VariableManager vm = new VariableManager() ;
	vm.addProperty("NEW_REPLIE", commentButton ) ;
	vm.addProperty("REPLIE_RECORD", currentRec  ) ;
	vm.addProperty("CURRENT_BILL_HEADER", discHeader  ) ;
	vm.addProperty( "ADMIN_LINK_HTML", this.ADMIN_LINK_TEMPLATE );

	this.sendHtml(req,res,vm, HTML_TEMPLATE) ;

	//	log("Get är klar") ;
	return ;
    }

    private Vector buildTagsV()
    {
	// SYNTAX: id  headline  text replies date
	// Lets build our variable list
	Vector tagsV = new Vector() ;
	tagsV.add("#REPLY_BILL_ID#");
	tagsV.add("#REPLY_HEADER#") ;
	tagsV.add("#REPLY_TEXT#") ;
	tagsV.add("#C_REPLIES#");
	tagsV.add("#REPLY_DATE#") ;
	return tagsV;
    }

    /**
       Takes the discussion id from the request object and moves ít to
       the sessions list over viewed discussions.
    **/

    public void updateDiscFlagList( HttpServletRequest req, String discId, String now)	throws ServletException, IOException {

	// Lets get the newDiscsList

	// Get the session and add the clicked discussion to the list. Put list back
	HttpSession session = req.getSession(true);
	Properties viewedDiscs = (Properties) session.getAttribute("BillBoard.viewedDiscList") ;
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
	session.setAttribute("BillBoard.viewedDiscList", viewedDiscs) ;
	// Ok, Lets print what we just updated
	viewedDiscs = (Properties) session.getAttribute("BillBoard.viewedDiscList") ;
	//log("*** ConfReply ***" + "\n") ;
	//log(props2String(viewedDiscs)) ;
    }



    /**
       Parses the Extended array with the htmlcode, which will be parsed
       for all records in the array
    */
    public String preParse (HttpServletRequest req, String[] DBArr, Vector tagsV,
			    File htmlCodeFile, String imagePath)  throws ServletException, IOException {

	StringBuffer htmlStr = new StringBuffer("") ;
	try {
	    // Lets get the part of the expert html
	    File templateLib = super.getExternalTemplateFolder(req) ;

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

		for(int j=i; j<i+nbrOfCols ; j++) {
		    dataV.add(DBArr[j]) ;
		}

		htmlStr.append(this.parseOneRecord(tagsV, dataV, htmlCodeFile));
		//	log("Ett record: " + oneParsedRecordStr);
	    } // end of the big for

	} catch(Exception e) {
	    log("Error in Preparse") ;
	    return null ;
	}
	return htmlStr.toString() ;
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
		confDiscId =	(String) session.getAttribute("BillBoard.disc_id") ;
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

    }

    /**
       Log function, will work for both servletexec and Apache
    **/

    public void log( String msg) {
	super.log("BillBoardReply: " + msg ) ;

    }
} // End of class
