/*
 *
 * @(#)Chat.java
 *
 *
 * Copyright (c)
 *
 */

import java.io.*;
import java.util.*;

import javax.servlet.*;
import javax.servlet.http.*;

import imcode.external.chat.*;
import imcode.external.diverse.* ;

import imcode.util.*;
import imcode.util.IMCServiceRMI;
import imcode.util.Parser;

import imcode.server.* ;

/**
 * superclas for chat servlets.
 *
 * Html template in use:
 * Chat_Admin_Button.htm????
 *
 * Html parstags in use:
 * #ADMIN_TYPE#???
 * #TARGET#???
 *
 * stored procedures in use:
 * -
 *
 *
 */


public class ChatBase extends HttpServlet {

    private final static String CVS_REV = "$Revision$" ;
    private final static String CVS_DATE = "$Date$" ;
    private final static String ADMIN_BUTTON_TEMPLATE = "Chat_Admin_Button.htm";
    private final static String UNADMIN_BUTTON_TEMPLATE = "Chat_Unadmin_Button.htm";

    public final static int CHAT_ALLA_INT = 0;
    public final static int CHAT_ENTER_LEAVE_INT = -32;
    public final static String LEAVE_MSG = "lämnar rummet";
    public final static String ENTER_MSG = "stiger in";


    /**
     *	Does the things that has to bee done only ones
     */
    public void init(ServletConfig config)
	throws ServletException
    {
	super.init(config);
    }

    /**
       Collects the parameters from the request object
    **/

    protected Properties getNewChatParameters( HttpServletRequest req) throws ServletException, IOException	{
	Properties chatP = new Properties();

	return chatP ;
    }

    //peter keep
    protected Chat createChat(HttpServletRequest req, imcode.server.User user, int metaId)throws ServletException, IOException{
	IMCPoolInterface chatref = IMCServiceRMI.getChatIMCPoolInterface(req) ;

	//lets get the standard stuff
	Vector msgTypes = convert2Vector(chatref.sqlProcedureMulti("C_GetTheMsgTypesBase"));
	Vector autTypes = convert2Vector(chatref.sqlProcedureMulti("C_GetAuthorizationTypes"));
	Chat myChat = new Chat(metaId,autTypes,msgTypes);

	String[] selAuto =  chatref.sqlProcedure("C_GetChatAutoTypes "+metaId);
	if (selAuto == null) {
	    selAuto = new String[1];
	    selAuto[0]="1";
	}else if(selAuto.length == 0) {
	    selAuto = new String[1];
	    selAuto[0]="1";
	}
	myChat.setSelectedAuto(selAuto);

	String sqlStr = "C_GetMsgTypes "+metaId ;

	String[][] messages = chatref.sqlProcedureMulti(sqlStr);
	if (messages != null) {
	    if (messages.length > 0) {
		myChat.setMsgTypes(convert2Vector(messages));
	    }
	}

	//updateTime,reload,inOut,privat,publik,dateTime,font
	String[] params = chatref.sqlProcedure("C_GetChatParameters "+metaId);
	if (params != null) {
	    if (params.length == 7) {
		myChat.setupdateTime(Integer.parseInt(params[0]));
		myChat.setreload(Integer.parseInt(params[1]));
		myChat.setinOut(Integer.parseInt(params[2]));
		myChat.setprivate(Integer.parseInt(params[3]));
		myChat.setpublic(Integer.parseInt(params[4]));
		myChat.setdateTime(Integer.parseInt(params[5]));
		myChat.setfont(Integer.parseInt(params[6]));
	    }
	}


	sqlStr = "C_GetRooms " + metaId ;
	String[][] rooms = chatref.sqlProcedureMulti(sqlStr);
	if (rooms != null) {
	    for (int i=0;i<rooms.length;i++) {
		myChat.createNewChatGroup(Integer.parseInt(rooms[i][0]), rooms[i][1]);
	    }
	}
	return myChat;
    }

    //peter keep
    public static String createOptionCode(Vector selected, Vector data ) {
	StringBuffer buff = new StringBuffer("");
	for( int i = 0 ; i < data.size() ; i+=2 ) {
	    buff.append("<option value=\"" + data.elementAt(i).toString() + "\"") ;
	    for(int e=0; e<selected.size();e++) {
		String sel = selected.elementAt(e).toString();
		if (sel != null) {
		    if (data.elementAt(i).toString().equals(sel)) {
			buff.append(" selected ") ;
		    }
		}
	    }
	    buff.append(">") ;
	    buff.append(data.elementAt(i+1).toString() + "</option>\n") ;
	}
	return buff.toString() ;
    }

    //peter keep
    public static String createOptionCode(String selected, Vector data ) {
	StringBuffer buff = new StringBuffer("");
	for( int i = 0 ; i < data.size() ; i+=2 ) {
	    buff.append("<option value=\"" + data.elementAt(i).toString() + "\"") ;
	    if (selected != null) {
		if (data.elementAt(i).toString().equals(selected)) {
		    buff.append(" selected ") ;
		}
	    }
	    buff.append(">") ;
	    buff.append(data.elementAt(i+1).toString() + "</option>\n") ;
	}
	return buff.toString() ;
    }

    //peter keep
    public static String createRadioButton(String buttonName,Vector data,String selected ){
	StringBuffer buff = new StringBuffer(""); ;
	for( int i = 0 ; i < data.size() ; i++)	{
	    buff.append("<input type=\"radio\" name=\"" + buttonName + "\" value=\"") ;
	    buff.append(data.elementAt(i).toString() + "\"") ;

	    if (selected == null)
		selected = "2";
	    if (data.elementAt(i).toString().equals(selected)){
		buff.append(" checked ") ;
	    }
	    buff.append(">\n") ;
	}
	return buff.toString() ;
    }



    /**
       Returns the metaId from a request object, if not found, we will
       get the one from our session object. If still not found then null is returned.
    */
    public String getMetaId (HttpServletRequest req)throws ServletException, IOException{
	String metaId = req.getParameter("meta_id") ;
	if( metaId == null ){
	    HttpSession session = req.getSession(false) ;
	    if (session != null){
		metaId =	(String) session.getAttribute("Chat.meta_id") ;
	    }
	}
	return metaId ;
    }



    /**
       Collects all information from the user object. To get information from
       the userobject.
       * userObject.getString(String theKey)
       * userObject.getInt(String theKey)
       * userObject.getBoolean(String theKey)

    **/
    public Properties getUserParameters(imcode.server.User user)
    {
	Properties userParams= new Properties() ;
	userParams.setProperty("USER_ID", user.getString("user_id")) ;
	userParams.setProperty("LOGIN_NAME", user.getString("login_name")) ;
	userParams.setProperty("LOGIN_PASSWORD", user.getString("login_password")) ;
	userParams.setProperty("FIRST_NAME", user.getString("first_name")) ;
	userParams.setProperty("LAST_NAME", user.getString("last_name")) ;
	userParams.setProperty("ADDRESS", user.getString("address")) ;
	userParams.setProperty("CITY", user.getString("city")) ;
	userParams.setProperty("ZIP", user.getString("zip")) ;
	userParams.setProperty("COUNTRY", user.getString("country")) ;
	userParams.setProperty("COUNTY_COUNCIL", user.getString("county_council")) ;
	userParams.setProperty("EMAIL", user.getString("email")) ;
	userParams.setProperty("ADMIN_MODE", user.getString("admin_mode")) ;
	userParams.setProperty("LAST_PAGE", user.getString("last_page")) ;
	userParams.setProperty("ARCHIVE_MODE", user.getString("archive_mode")) ;
	userParams.setProperty("USER_TYPE", user.getString("user_type")) ;
	userParams.setProperty("LOGIN_TYPE", user.getLoginType()) ;

	return userParams ;
    }

    /**
       Returns an user object. If an error occurs, an errorpage will be generated.
    */

    protected imcode.server.User getUserObj(HttpServletRequest req,
					    HttpServletResponse res) throws ServletException, IOException
    {

	if(checkSession(req,res) == true)
	    {

		// Get the session
		HttpSession session = req.getSession(true);
		// Does the session indicate this user already logged in?
		Object done = session.getAttribute("logon.isDone");  // marker object
		imcode.server.User user = (imcode.server.User) done ;

		return user ;
	    }
	else
	    {
		String header = "Chat servlet." ;
		ChatError err = new ChatError(req,res,header, 2) ;
		log(err.getErrorMsg()) ;
		return null ;
	    }
    }

    // *************** LETS HANDLE THE SESSION META PARAMETERS *********************


    /**
       Collects the standard parameters from the session object
    **/

    public Properties getSessionParameters( HttpServletRequest req)	throws ServletException, IOException{
	// Get the session
	HttpSession session = req.getSession(true);
	String metaId = (	(String) session.getAttribute("Chat.meta_id")==null) ? "" : ((String) session.getAttribute("Chat.meta_id")) ;
	String parentId = (	(String) session.getAttribute("Chat.parent_meta_id")==null) ? "" : ((String) session.getAttribute("Chat.parent_meta_id")) ;

	Properties reqParams= new Properties() ;
	reqParams.setProperty("META_ID", metaId) ;
	reqParams.setProperty("PARENT_META_ID", parentId) ;
	return reqParams ;
    }


    /**
       Collects the EXTENDED parameters from the session object. As extended paramters are we
       counting:

       Chat.forum_id
       Chat.discussion_id

       @Parameter: Properties params, if a properties object is passed, we will fill the
       object with the extended paramters, otherwise we will create one.
    **/

    public Properties getExtSessionParameters( HttpServletRequest req, Properties params)
	throws ServletException, IOException
    {

	// Get the session
	HttpSession session = req.getSession(true);
	String forumId = (	(String) session.getAttribute("Chat.forum_id")==null) ? "" : ((String) session.getAttribute("Chat.forum_id")) ;
	String discId = (	(String) session.getAttribute("Chat.disc_id")==null) ? "" : ((String) session.getAttribute("Chat.disc_id")) ;

	if( params == null)
	    params = new Properties() ;
	params.setProperty("FORUM_ID", forumId) ;
	params.setProperty("DISC_ID", discId) ;
	return params ;
    }



    /**
       Verifies that the user has logged in. If he hasnt, he will be redirected to
       an url which we get from a init file name conference.
    */

    protected boolean checkSession(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

	// Get the session
	HttpSession session = req.getSession(true);
	// Does the session indicate this user already logged in?
	Object done = session.getAttribute("logon.isDone");  // marker object
	imcode.server.User user = (imcode.server.User) done ;

	if (done == null) {
	    // No logon.isDone means he hasn't logged in.
	    // Save the request URL as the true target and redirect to the login page.
	    session.setAttribute("login.target", HttpUtils.getRequestURL(req).toString());
	    String serverName = MetaInfo.getServerName(req) ;
	    String host = req.getHeader("host") ;

	    IMCServiceInterface imcref = IMCServiceRMI.getIMCServiceInterface(req) ;
	    String startUrl = imcref.getStartUrl() ;
	    res.sendRedirect(serverName + startUrl) ;
	    return false;
	}
	return true ;
    }

    // *************** LETS HANDLE THE STANDARD META PARAMETERS *********************


    /**
       check the meta Parameters
    */

    public boolean checkParameters(HttpServletRequest req,HttpServletResponse res) throws ServletException, IOException {
	Properties params = MetaInfo.getParameters(req) ;
	if( MetaInfo.checkParameters(params) == false)
	    {
		log("checkParameters had a null value") ;
		return false;
	    }
	return true ;
    }

    public boolean checkParameters(HttpServletRequest req,HttpServletResponse res,
				   Properties params) throws ServletException, IOException
    {

	if( MetaInfo.checkParameters(params) == false)
	    {

		log("checkParameters had a null value") ;
		return false;
	    }
	return true ;
    }

    // *************************** END OF META PARAMETER FUNCTIONS *****************


    // *************************** ADMIN RIGHTS FUNCTIONS **************************

    protected boolean getAdminRights(IMCServiceInterface imcref, String metaId, imcode.server.User user)
    {
	try {
	    return userHasAdminRights( imcref, Integer.parseInt( metaId ), user );
	} catch ( IOException e ) {
	    return false ;
	}
    } // End GetAdminRights


    /**
       CheckAdminRights, returns true if the user is an superadmin. Only an superadmin
       is allowed to create new users
       False if the user isn't an administrator.
       1 = administrator
       0 = superadministrator
    */
    protected boolean checkAdminRights(IMCServiceInterface imcref, imcode.server.User user)
    {
	// Lets verify that the user who tries to add a new user is an SUPERADMIN
	int currUser_id = user.getInt("user_id") ;
	String checkAdminSql = "CheckAdminRights " + currUser_id ;
	String[] roles = imcref.sqlProcedure(checkAdminSql) ;
	boolean returnValue = false;
	for(int i = 0 ; i< roles.length; i++ )
	    {
		String aRole = roles[i] ;
		if(aRole.equalsIgnoreCase("0") )
		    returnValue = true ;
	    }
	return returnValue ;

    } // checkAdminRights

    /**
       CheckAdminRights, returns true if the user is an admin.
       False if the user isn't an administrator
    */
    protected boolean checkAdminRights(HttpServletRequest req, HttpServletResponse res)
	throws ServletException, IOException
    {

	// Lets get serverinformation
	IMCServiceInterface imcref = IMCServiceRMI.getIMCServiceInterface(req) ;

	imcode.server.User user = getUserObj(req,res) ;
	if(user == null)
	    {
		this.log("CheckadminRights: an error occured, getUserObj") ;
		return false ;
	    }
	else
	    return imcref.checkAdminRights(user) ;
    }

    // *********************** GETEXTERNAL TEMPLATE FUNCTIONS *********************

    /**
       Gives the folder to the root external folder,Example /templates/se/102/
    */
    public File getExternalTemplateRootFolder (HttpServletRequest req) throws ServletException, IOException	{

	IMCServiceInterface imcref = IMCServiceRMI.getIMCServiceInterface(req) ;
	// Lets get serverinformation
	String metaId = this.getMetaId(req) ;
	if( metaId == null)	{
	    log("No meta_id could be found! Error in Chat.class") ;
	    throw new IllegalArgumentException() ;
	}
	return imcref.getExternalTemplateFolder(Integer.parseInt(metaId)) ;
    }


    /**
       Gives the folder where All the html templates for a language are located.
       This method will call its helper method getTemplateLibName to get the
       name of the folder which contains the templates for a certain meta id
    */
    public File getExternalTemplateFolder (HttpServletRequest req) throws ServletException, IOException	{
	IMCServiceInterface imcref = IMCServiceRMI.getIMCServiceInterface(req) ;
	IMCPoolInterface chatref = IMCServiceRMI.getChatIMCPoolInterface(req) ;

	String externalTemplateLib = "" ;
	String metaId = this.getMetaId(req) ;
	if( metaId == null)	{
	    log("No meta_id could be found! Error in Chat.class") ;
	    throw new IllegalArgumentException() ;
	}
	// Lets get serverinformation
	return new File( imcref.getExternalTemplateFolder(Integer.parseInt(metaId)), this.getTemplateLibName(chatref, metaId)) ;
    }


    /**
       Returns the foldername where the templates are situated for a certain metaid.
    **/ //peter uses this
    protected String getTemplateLibName(IMCPoolInterface chatref, String meta_id)
	throws ServletException, IOException	{
	String sqlQ = "C_GetTemplateLib " + meta_id ;
	String libName = chatref.sqlProcedureStr(sqlQ) ;
	if( libName == null){
	    libName = "original" ;
	}
	return libName ;
    } // End of getTemplateLibName


    /**
       Collects the parameters from the request object. This function will get all the possible
       parameters this servlet will be able to get. If a parameter wont be found, the session
       parameter will be used instead, or if no such parameter exist in the session object,
       a key with no value = "" will be used instead.
       Since this method is used. it means
       that this servlet will take more arguments than the standard ones.
    **/

    public Properties getRequestParameters( HttpServletRequest req)
	throws ServletException, IOException
    {

	Properties reqParams = new Properties() ;

	// Lets get our own variables. We will first look for the discussion_id
	//	 in the request object, if not found, we will get the one from our session object
	String confForumId = req.getParameter("forum_id") ;
	if( confForumId == null ) {
	    HttpSession session = req.getSession(false) ;
	    if (session != null) {
		confForumId =	(String) session.getAttribute("Chat.forum_id") ;
	    }
	}
	reqParams.setProperty("FORUM_ID", confForumId) ;
	return reqParams ;
    }



    //************************ END GETEXTERNAL TEMPLATE FUNCTIONS ***************

    /**
       SendHtml. Generates the html page to the browser. Uses the templatefolder
       by taking the metaid from the request object to determind the templatefolder.
       Will by default handle maximum 3 servletadresses.
    */
    public void sendHtml (HttpServletRequest req, HttpServletResponse res,
			  Vector vect, String template, Chat chat) throws ServletException, IOException {

	IMCServiceInterface imcref = IMCServiceRMI.getIMCServiceInterface(req) ;
	IMCPoolInterface chatref = IMCServiceRMI.getChatIMCPoolInterface(req) ;

	String metaId;
	if (chat != null){
	    metaId = chat.getChatIdStr();
	}else {
	    metaId = this.getMetaId(req);
	}
	// Lets get the TemplateFolder  and the foldername used for this certain metaid
	String templateSet = this.getTemplateLibName(chatref, metaId);

	res.setContentType("text/html");
	ServletOutputStream out = res.getOutputStream();
	out.print(imcref.parseExternalDoc(vect, template , imcref.getLanguage(), "103", templateSet));
	out.flush();
	out.close();
    }

    /**
       Log function. Logs the message to the log file and console
    */

    public void log(String msg)
    {
	if(msg == null)msg="the msg who come in to ChatBase.log was was null";
	super.log(""+msg) ;
    }

    /**
       Date function. Returns the current date and time in the swedish style
    */


    public static String getDateToday()
    {
	java.util.Calendar cal = java.util.Calendar.getInstance() ;

	String year  = Integer.toString(cal.get(Calendar.YEAR)) ;
	int month = Integer.parseInt(Integer.toString(cal.get(Calendar.MONTH))) + 1;
	int day   = Integer.parseInt(Integer.toString(cal.get(Calendar.DAY_OF_MONTH))) ;
	int hour  = Integer.parseInt(Integer.toString(cal.get(Calendar.HOUR_OF_DAY))) ;
	int min   = Integer.parseInt(Integer.toString(cal.get(Calendar.MINUTE))) ;

	String dateToDay  = year ;
	dateToDay += "-" ;
	dateToDay += month < 10 ? "0" + Integer.toString(month) : Integer.toString(month) ;
	dateToDay += "-" ;
	dateToDay += day < 10 ? "0" + Integer.toString(day) : Integer.toString(day) ;

	return dateToDay ;
    }

    /**
       Date function. Returns the current time in the swedish style
    */

    public static String getTimeNow()
    {
	java.util.Calendar cal = java.util.Calendar.getInstance() ;

	int hour  = Integer.parseInt(Integer.toString(cal.get(Calendar.HOUR_OF_DAY))) ;
	int min   = Integer.parseInt(Integer.toString(cal.get(Calendar.MINUTE))) ;
	int sec   = Integer.parseInt(Integer.toString(cal.get(Calendar.SECOND))) ;

	String timeNow  = "" ;
	timeNow += hour < 10 ? "0" + Integer.toString(hour) : Integer.toString(hour) ;
	timeNow += ":" ;
	timeNow += min < 10 ? "0" + Integer.toString(min) : Integer.toString(min) ;
	timeNow += ":" ;
	timeNow += sec < 10 ? "0" + Integer.toString(sec) : Integer.toString(sec) ;

	return timeNow ;
    }


    /**
       Converts array to vector
    */

    public Vector convert2Vector(String[] arr)
    {
	Vector rolesV  = new Vector() ;
	for(int i = 0; i<arr.length; i++)
	    rolesV.add(arr[i]) ;
	return rolesV ;
    }

    public synchronized Vector convert2Vector(String[][] arr)
    {
	Vector rolesV  = new Vector() ;
	for(int i = 0; i<arr.length; i++){
	    for(int e=0;e<arr[i].length;e++){
		rolesV.add(arr[i][e]);
	    }
	}
	return rolesV ;
    }


    // ****************** GetImageFolder Functions *********************

    /**
       Gives the folder where All the html templates for a language are located.
       This method will call its helper method getTemplateLibName to get the
       name of the folder which contains the templates for a certain meta id
    */

    public String getExternalImageFolder (HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
	IMCServiceInterface imcref = IMCServiceRMI.getIMCServiceInterface(req) ;
	IMCPoolInterface chatref = IMCServiceRMI.getChatIMCPoolInterface(req) ;

	imcode.server.User user = getUserObj(req,res) ;
	String metaId = this.getMetaId(req) ;
	if( metaId == null)
	    {
		log("No meta_id could be found! Error in Chat.class") ;
		return "No meta_id could be found!" ;
	    }

	String extFolder = RmiConf.getExternalImageFolder(imcref,metaId) ;
	return extFolder += this.getTemplateLibName(chatref, metaId) ;
    }

    /**
       Examines a text, and watches for ' signs, which will extended with another ' sign
    */
    public String verifySqlText(String str )//in use by verifyForSql
    {
	StringBuffer buf =  new StringBuffer(str) ;
	char apostrof = '\'' ;
	for(int i = 0 ; i < buf.length() ; i++)
	    {
		//log(""+ buf.charAt(i)) ;
		if (buf.charAt(i) == apostrof )
		    {
			buf.insert(i,apostrof) ;
			i+=1 ;
		    }
	    }
	str = buf.toString() ;
	return str ;

    } // End CreateAdminHtml


    /**
       Checks for illegal sql parameters.
    **/
    public Properties verifyForSql(Properties aPropObj)//in use by ChatManager
    {
	// Ok, Lets find all apostrofes and if any,add another one
	Enumeration enumValues = aPropObj.elements() ;
	Enumeration enumKeys = aPropObj.keys() ;
	while((enumValues.hasMoreElements() && enumKeys.hasMoreElements()))
	    {
		Object oKeys = (enumKeys.nextElement()) ;
		Object oValue = (enumValues.nextElement()) ;
		String theVal = oValue.toString() ;
		String theKey = oKeys.toString() ;
		aPropObj.setProperty(theKey, verifySqlText(theVal)) ;
	    }
	return aPropObj ;
    } // verifyForSql



    /**
     * checks if user is authorized
     * @param req
     * @param res is used if error (send user to conference_starturl )
     * @param user
     */

    //används av bla ChatViewer
    protected boolean isUserAuthorized( HttpServletRequest req, HttpServletResponse res, imcode.server.User user )
	throws ServletException, IOException
    {

	// Lets get serverinformation
	String host = req.getHeader( "Host" ) ;

	HttpSession session = req.getSession( true );

	//lets get if user authorized or not
	boolean authorized = true;

	//OBS "Chat.meta_id" ska bytas ut mot en konstant senare
	String stringMetaId = (String)session.getAttribute( "Chat.meta_id" );
	if ( stringMetaId == null )
	    {
		authorized = false;
		//lets send unauthorized users out
		String serverName = MetaInfo.getServerName(req) ;
		IMCServiceInterface imcref = IMCServiceRMI.getIMCServiceInterface(req) ;
		String startUrl = imcref.getStartUrl() ;
		res.sendRedirect(serverName + startUrl) ;
	    }
	else
	    {
		int metaId = Integer.parseInt( stringMetaId );
		authorized = isUserAuthorized( req, res, metaId, user );
	    }


	return authorized;
    }

    /**
     * checks if user is authorized
     * @param req is used for collecting serverinfo and session
     * @param res is used if error (send user to conference_starturl )
     * @param metaId conference metaId
     * @param user
     */
    protected boolean isUserAuthorized( HttpServletRequest req, HttpServletResponse res, int metaId, imcode.server.User user )
	throws ServletException, IOException
    {

	// Lets get serverinformation
	String host = req.getHeader( "Host" ) ;
	IMCServiceInterface imcref = IMCServiceRMI.getIMCServiceInterface(req) ;

	HttpSession session = req.getSession( true );

	//is user authorized?
	boolean authorized = imcref.checkDocRights( metaId, user );

	//lets send unauthorized users out
	if ( !authorized )
	    {
		String serverName = MetaInfo.getServerName(req) ;
		String startUrl = imcref.getStartUrl() ;
		res.sendRedirect(serverName + startUrl) ;
	    }

	return authorized;
    }

    /**
     * check if user has right to edit
     * @param imcref imCMS IMCServiceInterface instance
     * @param metaId metaId for conference
     * @param user
     */
    protected boolean userHasRightToEdit( IMCServiceInterface imcref, int metaId,
					  imcode.server.User user ) throws java.io.IOException
    {

	return ( imcref.checkDocRights( metaId, user ) &&
		 imcref.checkDocAdminRights( metaId, user ) );
    }

    /**
     * check if user is admin and has rights to edit
     * @param imcref imCMS IMCServiceInterface instance
     * @param metaId metaId for conference
     * @param user
     */
    protected boolean userHasAdminRights( IMCServiceInterface imcref, int metaId,
					  imcode.server.User user ) throws java.io.IOException
    {
	return ( imcref.checkDocAdminRights( metaId, user ) &&
		 imcref.checkDocAdminRights( metaId, user, 65536 ) );

    }


    //**************** does the setup for chatboard  **********************
    //lets get the settings for the chat and convert them
    //and add them into HashTable and add it into the session
    //ugly it should moves into the ChatMember obj, but i haven't got the time to do it now
    public synchronized void prepareChatBoardSettings(ChatMember member, HttpServletRequest req, boolean bool){
	//now we sets up the settings for this chat
	HttpSession session = req.getSession(true);

	Hashtable hash = member.getProperties();
	if (hash==null){
	    hash = new Hashtable();
	}
	Chat chat = member.getMyParent();
	boolean onOff = false;

	//sets up show datTime or not
	int dateTime = chat.getdateTime();
	if(dateTime == 2){
	    onOff = false;
	}else{
	    if (bool){
		if (dateTime == 3){
		    onOff = req.getParameter("dateTime") == null ? false : true;
		}else{
		    onOff = true;
		}
	    }else{
		onOff = true;
	    }
	}

	//log("1dateTime = "+onOff);
	hash.put("dateTimeBoolean", new Boolean(onOff));


	//sets up show public msg or not
	int publik = chat.getpublic();
	if(publik == 2){
	    onOff = false;
	}else{
	    if (bool){
		if(publik == 3){
		    onOff = req.getParameter("public") == null ? false : true;
		}else{
		    onOff = true;
		}
	    }else{
		onOff = true;
	    }
	}
	//log("1publik = "+onOff);
	hash.put("publicMsgBoolean", new Boolean(onOff));


	//sets up show private msg or not
	int privat = chat.getprivate();
	if(privat == 2){
	    onOff = false;
	}else{
	    if (bool){
		if (privat == 3){
		    onOff = req.getParameter("private") == null ? false : true;
		}else{
		    onOff = true;
		}
	    }else{
		onOff = true;
	    }
	}
	//log("1privat = "+onOff);
	hash.put("privateMsgBoolean", new Boolean(onOff));

	//sets up show entrense and exits, or not
	int inOut = chat.getinOut();
	if(inOut == 2){
	    onOff = false;
	}else{
	    if (bool){
		if (inOut == 3){
		    onOff = req.getParameter("inOut") == null ? false : true;
		}else{
		    onOff = true;
		}
	    }else{
		onOff = true;
	    }
	}
	//log("1inOut = "+onOff);
	hash.put("inOutBoolean", new Boolean(onOff));

	//sets up autoreload on off
	int reload = chat.getreload();
	int updateTime = chat.getupdateTime();
	String timeStr = "30";

	if(reload == 2){
	    onOff = false;
	    timeStr = "30";
	}else{
	    if (bool){
		if (reload == 3){
		    onOff = req.getParameter("reload") == null ? false : true;
		    timeStr = "30";
		}else{
		    onOff = false;
		    timeStr = "30";
		}
	    }else{
		onOff = true;
		timeStr = "30";
	    }
	}
	try{
	    hash.put("reloadInteger", new Integer(timeStr));
	}catch(NumberFormatException nfe){
	    log(nfe.getMessage());
	    hash.put("reloadInteger", new Integer("30"));
	}

	//log("reloadBoolean = "+onOff);
	hash.put("reloadBoolean", new Boolean(onOff));

	int size = chat.getfont();
	String fontSizeInteger = "fontSizeInteger";
	if (req.getParameter("font")!= null) {
	    size = Integer.parseInt(req.getParameter("font"));
	}



	Integer inten = new Integer(size);
	hash.put(fontSizeInteger, inten);

	//member.setProperties(hash);
	session.setAttribute("theChatMember",member);

    }//end prepareSettings


    //*****************cleares the session from all chat params ***************
    //the only ones left is
    //logon_isDone and browser_id

    public void cleanUpSessionParams(HttpSession session){

	Enumeration enum = session.getAttributeNames();

	while(enum.hasMoreElements()){
	    String name = (String) enum.nextElement();
	    if (name.equals("chatBinding")) {
		ChatBindingListener binLaban = (ChatBindingListener) session.getAttribute(name);
		binLaban.valueUnbound(new HttpSessionBindingEvent(session, "ChatBindingListener"));
	    }else {
		if(name.equals("logon.isDone") || name.equals("browser_id")){
		    //do nothing
		}else{
		    session.removeAttribute(name);
		}
	    }
	}
    }


} // End class
