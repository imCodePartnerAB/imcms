import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import imcode.external.diverse.* ;
import java.rmi.* ;
import java.rmi.registry.* ;
import imcode.util.* ;
import imcode.server.* ;
import imcode.util.IMCServiceRMI;

/*
  tags in use so far
  #CHAT_ROOMS#
  #recipient#

*/

import imcode.external.chat.*;



public class ChatControl extends ChatBase
{

    private final static String HTML_TEMPLATE = "theChat.htm";
    private final static String SETTINGS_TEMPLATE = "chat_settings.html" ;
    private final static String ADMIN_GET_RID_OF_A_SESSION = "Chat_Admin_End_A_Session.htm";
    private final static String ADMIN_BUTTON = "Chat_Admin_Button.htm";
    private final static String SETTINGS_BUTTON = "chat_settings_button.html";

    //OBS OBS OBS har inte fixat kontroll om det är administratör eller användare
    //för det är ju lite mera knappar och metoder som ska med om det är en admin
    //tex en knapp för att kicka ut användare
    //ev ska oxå tidtaggen på medelanden fixas till här
    //även loggningen ska fixas här om sådan efterfrågas
    //vidare måste silning av åäö och taggar fixas

    /**
       doGet
    */
    public void doGet(HttpServletRequest req, HttpServletResponse res)throws ServletException, IOException{

	// Lets validate the session, e.g has the user logged in to Janus?
	if (super.checkSession(req,res) == false){
	    log("RETURN super.checksession");
	    return ;
	}

	HttpSession session = req.getSession(false);
	ServletContext myContext = getServletContext();

	// Lets get the standard SESSION parameters and validate them
	Properties params = this.getSessionParameters(req) ;
	if (super.checkParameters(req, res, params) == false){
	    log("RETURN the checkParameters == false");
	    return ;
	}
	// Lets get the user object
	imcode.server.user.UserDomainObject user = super.getUserObj(req,res) ;
	if(user == null){
	    log("RETURN usern is null");
	    return ;
	}

	if ( !isUserAuthorized( req, res, user ) ){
	    log("RETURN user is not authorized");
	    return;
	}

	// Lets get parameters
	String metaId = params.getProperty("META_ID") ;
	//log("aMetaId = "+aMetaId);
	int meta_Id = Integer.parseInt( metaId );

	//lets get the chatmember
	ChatMember myMember = (ChatMember) session.getAttribute("theChatMember");
	if (myMember == null){
	    log("myMember was null so return");
	    return;
	}
	log(myMember.toString());
	//lets get the Chat
	Chat myChat = myMember.getMyParent();
	if (myChat == null){
	    log("myChat was null so return");
	    return;
	}

	String chatName = myChat.getChatName();
	if(chatName == null)chatName ="";


	//lets get the room
	ChatGroup myGroup = myMember.getMyGroup();
	if (myGroup == null){
	    log("myGroup was null so return");
	    return;
	}

	//lets get the userlangue if we dont have it OBS must fix this somwhere else
	String userLangId = (String) session.getAttribute("chatUserLangue");
	if (userLangId == null){
	    //we dont have it so we have to get it from somwhere
	    //OBS OBS temp solution
	    userLangId = "1";
	}

	//ok lets se if the user wants the change setting page
	if (req.getParameter("settings")!= null){
	    //ok we have to fix this method
	    this.createSettingsPage(req,res,session,metaId, user, myMember);
	    return;
	}//end

	//strings needed to set up the page
	String chatRoom = myGroup.getGroupName();
	String alias = myMember.getName();
	String selected = (req.getParameter("msgTypes") == null ? "" : req.getParameter("msgTypes").trim());

	String msgTypes = createOptionCode(selected, myChat.getMsgTypes() );

	//let's get all the users in this room, for the selectList
	StringBuffer group_members = new StringBuffer("");
	Iterator iter = myGroup.getAllGroupMembers();
	String selectMemb = (req.getParameter("recipient") == null ? "0" :  req.getParameter("recipient").trim());
	int selNr = Integer.parseInt(selectMemb);
	while (iter.hasNext()){
	    ChatMember tempMember = (ChatMember) iter.next();
	    String sel = "";
	    if(tempMember.getUserId() == selNr)sel = " selected";
	    group_members.append("<option value=\""+tempMember.getUserId() + "\""+sel+">" + tempMember.getName()+"</option>\n" );
	}

	//ok lets get all names of chatGroups
	StringBuffer chat_rooms = new StringBuffer("");
	Enumeration enum = myChat.getAllChatGroups();
	while (enum.hasMoreElements()){
	    ChatGroup tempGroup = (ChatGroup) enum.nextElement();
	    chat_rooms.append("<option value=\""+ tempGroup.getGroupId() +"\">" +tempGroup.getGroupName()+"</option>\n" );
	}

	//let's see if user has adminrights
	String adminButtonKickOut = "";
	String chatAdminLink =  "";
	File templateLib = super.getExternalTemplateFolder(req) ;

        IMCServiceInterface imcref = ApplicationServer.getIMCServiceInterface() ;

	if(userHasAdminRights( imcref, meta_Id, user )){
	    chatAdminLink = createAdminButton(req, ADMIN_BUTTON,metaId,chatName);
	    //lets set up the kick out button OBS fixa detta
	    adminButtonKickOut = createAdminButton(req, ADMIN_GET_RID_OF_A_SESSION,metaId,"");
	}

	//lets set up the page to send
	Vector tags = new Vector();
	//lets add all the needed tags
	tags.add("#chatName#");				tags.add( chatName );
	tags.add("#alias#");				tags.add( alias ) ;
	tags.add("#chatRoom#");				tags.add( chatRoom ) ;
	tags.add("#MSG_PREFIX#");			tags.add( msgTypes ) ;
	tags.add("#MSG_RECIVER#");			tags.add( group_members.toString() ) ;
	tags.add("#CHAT_ROOMS#");			tags.add( chat_rooms.toString() ) ;
	tags.add("#CHAT_ADMIN_LINK#");		tags.add( chatAdminLink );
	tags.add("#CHAT_ADMIN_DISCUSSION#");tags.add( adminButtonKickOut  );
	tags.add("#SETTINGS#");				tags.add( settingsButton(req, myChat));

	this.sendHtml(req,res,tags, HTML_TEMPLATE, null) ;
	log("ChatControl doGet klar");
	return;
    } //**** end doGet ***** end doGet ***** end doGet ******


    private String settingsButton(HttpServletRequest req, imcode.external.chat.Chat chat)throws ServletException, IOException {
	if (chat.settingsPage() ) {
        IMCServiceInterface imcref = ApplicationServer.getIMCServiceInterface() ;
	    IMCPoolInterface chatref = IMCServiceRMI.getChatIMCPoolInterface(req) ;
	    int metaId = chat.getChatId();
	    return imcref.parseExternalDoc(null, SETTINGS_BUTTON , imcref.getDefaultLanguageAsIso639_1(), "103", getTemplateLibName(chatref,metaId+""));
	}else {
	    return "&nbsp;";
	}
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res)
	throws ServletException, IOException
    {

	// Lets validate the session, e.g has the user logged in to Janus?
	if (super.checkSession(req,res) == false){
	    log("super.check session return");
	    return ;
	}

	HttpSession session = req.getSession(false);
	ServletContext myContext = getServletContext();

	// Lets get the standard SESSION parameters and validate them
	Properties params = this.getSessionParameters(req) ;
	if (super.checkParameters(req, res, params) == false){
	    log("super.checkparams return");
	    return ;
	}
	// Lets get the user object
	imcode.server.user.UserDomainObject user = super.getUserObj(req,res) ;
	if (user == null){
	    log("user is null return");
	    return ;
	}
	if ( !isUserAuthorized( req, res, user ) ){
	    log("user is not autorized return");
	    return;
	}
	// Lets get serverinformation


        IMCServiceInterface imcref = ApplicationServer.getIMCServiceInterface() ;
	IMCPoolInterface chatref = IMCServiceRMI.getChatIMCPoolInterface(req) ;

	// Lets get parameters
	String metaID = params.getProperty("META_ID") ;
	int meta_Id = Integer.parseInt( metaID );
	String aChatId = params.getProperty("CHAT_ID") ; //vet ej om denna behövs????


	//*** *** ok lets handle the useCases *** ***

	//lets get the Chat ChatGroup and ChatMember
	ChatMember myMember = (ChatMember) session.getAttribute("theChatMember");
	if (myMember == null){
	    log("RETURN myMember is null");
	    return;
	}
	log(myMember.toString());
	Chat myChat = myMember.getMyParent();
	if (myChat == null){
	    log("RETURN myChat is null");
	    return;
	}

	ChatGroup myGroup = myMember.getMyGroup();
	if(myGroup == null){
	    log("RETURN myGroup is null");
	    return;
	}

	if (req.getParameter("sendMsg") != null){//**** ok the user wants to send a message ****
	    log("*** start sendMsg ***");
	    String senderName = myMember.getName();

	    //lets get the message and all the needed params add it into the msgpool
	    String newMessage = (req.getParameter("msg") == null ? "" : req.getParameter("msg").trim());
	    if (newMessage.length() != 0){
		//lets get rid all html tags
		newMessage = HTMLConv.toHTMLSpecial(newMessage);

		//lets get the recipient 0 = alla
		String recieverNrStr = (req.getParameter("recipient") == null ? "0" :  req.getParameter("recipient").trim());

		//lets get the messageType fore the message 0 = inget
		String msgTypeNrStr = (req.getParameter("msgTypes") == null ? "0" : req.getParameter("msgTypes").trim());

		//ok lets parse those to int
		int recieverNr, msgTypeNr;
		try	{
		    recieverNr = Integer.parseInt(recieverNrStr);
		    msgTypeNr = Integer.parseInt(msgTypeNrStr);

		}catch (NumberFormatException nfe){
		    log("NumberFormatException while try to send msg");
		    recieverNr = 0;
		    msgTypeNr = 0;
		}

		String msgTypeStr = ""; //the msgType in text
		if (msgTypeNr != 0)	{
		    Vector vect = myChat.getMsgTypes();
		    for(int i = 0; i < vect.size(); i +=2){
			String st = (String) vect.get(i);
			if (st.equals(Integer.toString(msgTypeNr))){
			    msgTypeStr = (String) vect.get(i+1);
			    break;
			}
		    }
		}
		String recieverStr = "Alla"; //the receiver in text FIX ugly
		if (recieverNr != 0){
		    boolean found = false;
		    Iterator iter = myGroup.getAllGroupMembers();
		    while (iter.hasNext() && !found){
			ChatMember memb = (ChatMember)iter.next();
			if (recieverNr == memb.getUserId())	{
			    recieverStr = memb.getName();
			    found = true;
			}
		    }
		}

		//lets see if it was a private msg to all then wee dont send it
		if (msgTypeNr == 101 && recieverNr == 0) {
		    doGet(req,res);
		    return;
		}else{
		    int senderNr = myMember.getUserId();
		    String senderStr = myMember.getName();
		    String theDateTime = (super.getDateToday() +" : "+ super.getTimeNow());

		    ChatMsg newChatMsg = new ChatMsg(newMessage,recieverStr,recieverNr,msgTypeNr,msgTypeStr,senderStr,senderNr,theDateTime );
		    log("ChatMsg = "+newChatMsg.getMessage());
		    //ok now lets send it "boolean addNewMsg(ChatMsg msg)"
		    myMember.addNewChatMsg(newChatMsg);

		    //ok lets log the message
		}
	    }


	    //ok now lets build the page in doGet
	    doGet(req,res);
	    return;

	}//end if (req.getParameter("sendMSG") != null)


	//*** the user wants too change chat room *****

	if (req.getParameter("changeRoom") != null)	{
	    //log("*** start changeRoom ***");
	    //ok lets get the "new room number"
	    String roomNrStr = (req.getParameter("newRooms") == null ? "" : req.getParameter("newRooms").trim());

	    int roomNr;
	    try
		{
		    roomNr = Integer.parseInt(roomNrStr);
		}catch(NumberFormatException nfe)
		    {
			log("NumberFormatException when trying to change room");
			return;
		    }
	    //ok lets get the room
	    ChatGroup mewGroup = myChat.getChatGroup(roomNr);
	    if (mewGroup == null)
		{
		    //it was null so lets try another way
		    Enumeration enum = myChat.getAllChatGroups();
		    boolean found = false;
		    while (enum.hasMoreElements() && !found)
			{
			    ChatGroup tempGr = (ChatGroup) enum.nextElement();
			    if (roomNr == tempGr.getGroupId())
				{
				    mewGroup = tempGr;
				    found = true;
				}
			}
		    if ( !found )
			{
			    log("newGroup was still null so return");
			    return;
			}
		}

	    String theDateTime = (super.getDateToday() +" : "+ super.getTimeNow());
	    //ok lets send a msg to tell everybody that the user has left the room
	    int senderNr = myMember.getUserId();
	    String senderName = myMember.getName();
	    ChatMsg newLeaveMsg = new ChatMsg(LEAVE_MSG,"", CHAT_ENTER_LEAVE_INT, CHAT_ENTER_LEAVE_INT,"", senderName, -1, theDateTime);
	    myMember.addNewChatMsg(newLeaveMsg);

	    //ok lets leave the current group
	    myGroup.removeGroupMember(myMember);

	    //ok lets add member in to the new group
	    mewGroup.addNewGroupMember(myMember);

	    //ok lets send a enter group msg
	    ChatMsg newEnterMsg = new ChatMsg(ENTER_MSG, "", CHAT_ENTER_LEAVE_INT, CHAT_ENTER_LEAVE_INT,"", senderName, -1, theDateTime);
	    myMember.addNewChatMsg(newEnterMsg);

	    //lets update the session
	    session.setAttribute("theRoom", mewGroup);

	    //ok lets build the page in doGet
	    //log("*** end changeRoom ***");
	    doGet(req, res);
	    return;
	}//end if (req.getParameter("changeRoom") != null)


	//*****   the user want to change settings    *****

	if (req.getParameter("controlOK") != null || req.getParameter("fontInc")!= null ||req.getParameter("fontDec")!= null){
	    //lets collect the new settings
	    super.prepareChatBoardSettings(myMember, req, true);
	    doGet(req, res);
	    return;
	}//end if (req.getParameter("controlOK") != null)


	//the admin wants to kick some one out



	//******     chatmember logout    ******


	//ok the user wants to logOut so lets send the user to the start page
	if (req.getParameter("logOut") != null){
	    log("*** start logOut ***");
	    myMember = (ChatMember)session.getAttribute("theChatMember");
	    myChat = myMember.getMyParent();
	    myGroup = myMember.getMyGroup();

	    String theDateTime = ChatBase.getDateToday() +" : "+ ChatBase.getTimeNow();

	    String senderName = myMember.getName();
	    String libName = super.getTemplateLibName(chatref,myChat.getChatId()+"");
	    String leave_msg = imcref.parseExternalDoc(new Vector(), "leave_msg.html",user.getLangPrefix(), "103", libName);

	    ChatMsg newLeaveMsg = new ChatMsg(	leave_msg,"",
						ChatBase.CHAT_ENTER_LEAVE_INT,
						ChatBase.CHAT_ENTER_LEAVE_INT,"",
						senderName, -1, theDateTime);
	    //lets send the message
	    myGroup.addNewMsg(newLeaveMsg);
	    int senderNr = myMember.getUserId();
	    myGroup.removeGroupMember(myMember);
	    myChat.removeChatMember(senderNr);

	    super.cleanUpSessionParams(session);
	    res.sendRedirect("StartDoc");
	    return;
	}//end logout


	//******   kickout a member ******
	//ok lets kick out a messy chat member
	if (req.getParameter("kickOut") != null && userHasAdminRights( imcref, meta_Id, user))
	    {
		//log("*** start kickOut ***");
		//lets get the membernumber
		String memberNrStr = (req.getParameter("recipient") == null ? "" :  req.getParameter("recipient").trim());
		//log("memberNrStr = "+memberNrStr);
		int idNr = 0;
		if (memberNrStr.length() == 0)
		    {
			log("no member coosen so lets return");
			return;
		    }
		try
		    {
			idNr = Integer.parseInt(memberNrStr);
		    }catch(NumberFormatException nfe)
			{
			    log("NumberFormatException while kicking out member");
			    return;
			}

		ChatMember memb = myChat.getChatMember(idNr) ;
		if (memb != null) {
		    int senderNr = memb.getUserId();
		    myGroup.removeGroupMember(memb);
		    myChat.removeChatMember(senderNr);
		}
		//System.out.println(memb.getUserId()+"");
		//ok now we have the id number, so now lets clean up his session
		ChatBindingListener.getKickoutSession(idNr);

		doGet(req, res);
		return;
	    }//end kickout
	return;


    } // DoPost


    //this method will create an usersettings page
    //
    public synchronized void createSettingsPage(HttpServletRequest req, HttpServletResponse res, HttpSession session,
						String metaId, imcode.server.user.UserDomainObject user, ChatMember member)
	throws ServletException, IOException
    {
	Vector vect = new Vector();
	File templetUrl =	super.getExternalTemplateFolder(req);
        IMCServiceInterface imcref = ApplicationServer.getIMCServiceInterface() ;
	IMCPoolInterface chatref = IMCServiceRMI.getChatIMCPoolInterface(req) ;
	Hashtable hash = member.getProperties();
	if (hash == null) {
	    hash = new Hashtable();
	}
	boolean bool;
	String[] arr;
	if (true)//(checkboxText == null)
	    {
		//we dont have them so we have to get them from db
		arr = chatref.sqlProcedure("C_GetChatParameters "+ metaId );
		if (arr.length != 7)	{
		    return;
		}

		String reload = "";
		if(arr[1].equals("3")){
		    bool = ((Boolean)hash.get("reloadBoolean")).booleanValue();
		    log(""+bool);
		    Vector tempV = new Vector();
		    tempV.add("#checked#");
		    if (bool) {
			tempV.add("checked");
		    }else {
			tempV.add("");
		    }
		    reload = imcref.parseExternalDoc(tempV, "checkbox_reload.html" , user.getLangPrefix(), "103", templetUrl.getName());
		}

		String entrance = "";
		if(arr[2].equals("3")){
		    bool = ((Boolean)hash.get("inOutBoolean")).booleanValue();log(""+bool);
		    Vector tempV = new Vector();
		    tempV.add("#checked#");
		    if (bool) {
			tempV.add("checked");
		    }else {
			tempV.add("");
		    }
		    entrance = imcref.parseExternalDoc(tempV, "checkbox_entrance.html" , user.getLangPrefix(), "103", templetUrl.getName());
		}
		String privat = "";
		if(arr[3].equals("3")){
		    bool = ((Boolean)hash.get("privateMsgBoolean")).booleanValue();log(""+bool);
		    Vector tempV = new Vector();
		    tempV.add("#checked#");
		    if (bool) {
			tempV.add("checked");
		    }else {
			tempV.add("");
		    }
		    privat = imcref.parseExternalDoc(tempV, "checkbox_private.html" , user.getLangPrefix(), "103", templetUrl.getName());
		}
		String publik = "";
		if(arr[4].equals("3")){
		    bool = ((Boolean)hash.get("publicMsgBoolean")).booleanValue();log(""+bool);
		    Vector tempV = new Vector();
		    tempV.add("#checked#");
		    if (bool) {
			tempV.add("checked");
		    }else {
			tempV.add("");
		    }
		    publik = imcref.parseExternalDoc(tempV, "checkbox_public.html" , user.getLangPrefix(), "103", templetUrl.getName());
		}
		String datetime = "";
		if(arr[5].equals("3")){
		    bool = ((Boolean)hash.get("dateTimeBoolean")).booleanValue();
		    log("dateTimeBoolean "+bool);
		    Vector tempV = new Vector();
		    tempV.add("#checked#");
		    if (bool) {
			tempV.add("checked");
		    }else {
			tempV.add("");
		    }
		    datetime = imcref.parseExternalDoc(tempV, "checkbox_datetime.html" , user.getLangPrefix(), "103", templetUrl.getName());
		}
		String font = "";
		if(arr[6].equals("3")){
		    Integer mark = (Integer)hash.get("fontSizeInteger");
		    int e = 3;
		    if (mark != null) {
			e = mark.intValue();
		    }
		    Vector tempV = new Vector();
		    for(int i=1;i<8;i++) {
			tempV.add("#"+i+"#");
			if (i==e) {
			    tempV.add("checked");
			}else {
			    tempV.add("");
			}
		    }
		    font = imcref.parseExternalDoc(tempV, "buttons_font.html" , user.getLangPrefix(), "103", templetUrl.getName());
		}

		vect.add("#reload#");		vect.add(reload);
		vect.add("#entrance#");		vect.add(entrance);
		vect.add("#private#");		vect.add(privat);
		vect.add("#public#");		vect.add(publik);
		vect.add("#datetime#");		vect.add(datetime);
		vect.add("#font#");             vect.add(font);
	    }
	this.sendHtml(req,res,vect, SETTINGS_TEMPLATE, null) ;
	return;
    }//end createSettingsPage



    private synchronized String createAdminButton(HttpServletRequest req, String template,String chatId, String name)
	throws ServletException, IOException
    {
	VariableManager vm = new VariableManager();
	vm.addProperty( "chatId", chatId );
	vm.addProperty( "chatName", name );

	//lets create adminbuttonhtml
	File templateLib = super.getExternalTemplateFolder( req );
	HtmlGenerator htmlObj = new HtmlGenerator( templateLib, template );
	return htmlObj.createHtmlString( vm, req );
    }



    /**
       Detects paths and filenames.
    */

    public void init(ServletConfig config)
	throws ServletException
    {
	super.init(config);
    }

    /**
       Log function, will work for both servletexec and Apache
       only for internal use
    **/
    public void log(String str)
    {
	super.log("ChatControl: " + str ) ;
	//System.out.println("ChatControl: " + str ) ;
    }

} // End of class
