import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import imcode.external.diverse.* ;
import java.rmi.* ;
import java.rmi.registry.* ;
import imcode.util.* ;
import imcode.util.IMCServiceRMI;

import imcode.external.*;
import imcode.external.chat.*;
import imcode.server.* ;


public class ChatBoard extends ChatBase
{
    private final static String CVS_REV = "$Revision$" ;
    private final static String CVS_DATE = "$Date$" ;


    private final static String HTML_TEMPLATE	= "chat_messages.html" ; ;
    private final static String HTML_LINE		= "chat_line.html";

    public void doPost(HttpServletRequest req, HttpServletResponse res)	throws ServletException, IOException{
	log("someone is trying to acces by doPost!!! It's not allowed yet!");
	return;
    } // DoPost

    //*****  doGet  *****  doGet  *****  doGet  *****  doGet  *****  doGet  *****

    public void doGet(HttpServletRequest req, HttpServletResponse res)throws ServletException, IOException{
	if (super.checkSession(req,res) == false)	return ;
	Properties params = this.getSessionParameters(req) ;
	if (params == null)	{
	    log("the params was null so return");
	    return;
	}
	if (super.checkParameters(req, res, params) == false){
	    log("return i checkparameters");
	    return ;
	}

	// Lets get the user object
	imcode.server.User user = super.getUserObj(req,res) ;
	if(user == null) return ;

	if ( !isUserAuthorized( req, res, user ) ){
	    log("user not authorized");
	    return;
	}

	// Lets get serverinformation
	String host = req.getHeader("Host") ;
	IMCServiceInterface imcref = IMCServiceRMI.getIMCServiceInterface(req) ;


	// Lets get parameters
	String metaId = params.getProperty("META_ID") ;
	log("metaId = "+metaId);
	HttpSession session = req.getSession(false) ;
	ServletContext myContext = getServletContext();

	//this buffer is used to store all the msgs to send to the page
	StringBuffer sendMsgString = new StringBuffer("");
	String chatRefresh = "";

	//ok let's get all the messages and add them into the buffer
	if (true){
	    //log("nu är vi inne");
	    if(session == null)	{
		log("session was null so return");
		return;
	    }

	    ChatMember myMember = (ChatMember)session.getAttribute("theChatMember");
	    if (myMember==null){
		log("membern was null so return");
		return;
	    }
	    log(myMember.toString());
	    Chat myChat = myMember.getMyParent();
	    if (myChat==null){
		log("myChat was null so return");
		return;
	    }
	    ChatGroup myGrupp = myMember.getMyGroup();
	    if (myGrupp==null){
		log("myGrupp was null so return");
		return;
	    }
	    File templetUrl =	super.getExternalTemplateFolder(req);

	    IMCPoolInterface chatref = IMCServiceRMI.getChatIMCPoolInterface(req) ;
	    String libName = super.getTemplateLibName(chatref,myChat.getChatId()+"");
	    HtmlGenerator generator = new HtmlGenerator(templetUrl,HTML_LINE);

	    //lets get all the settings for this page
	    Hashtable theSettings = myMember.getProperties();
	    if (theSettings == null){
		log("chatHashTable was null so return");
		return;
	    }

	    //lets get it all out from it
	    boolean dateOn		=	((Boolean)theSettings.get("dateTimeBoolean")).booleanValue();
	    boolean publicMsg	=	((Boolean)theSettings.get("publicMsgBoolean")).booleanValue();
	    boolean privateMsg	=	((Boolean)theSettings.get("privateMsgBoolean")).booleanValue();
	    boolean autoReload	=	((Boolean)theSettings.get("reloadBoolean")).booleanValue();
	    boolean inOut		=	((Boolean)theSettings.get("inOutBoolean")).booleanValue();
	    int fontSizeInt	=	((Integer)theSettings.get("fontSizeInteger")).intValue();
	    //String time		=	((Integer)theSettings.get("reloadInteger")).toString();
	    String time = myChat.getupdateTime()+"";
	    //log("reload"+time);
	    String fontSize = Integer.toString(fontSizeInt);
	    if (autoReload){
		chatRefresh = "<META HTTP-EQUIV=\"Refresh\" CONTENT=\""+time+";URL="+req.getRequestURI()+"\">";
	    }

	    //lets get the ignore-list
	    //doesnt have one yet

	    int lastMsgInt = myMember.getLastMsgNr();
	    //let's get all the messages
	    ListIterator msgIter =  myMember.getMessages();
	    Vector dataV = new Vector();
	    //lets fix the html-string containing all messags

	    while(msgIter.hasNext()){
		VariableManager vm = new VariableManager();
		Vector vLine = new Vector();

		boolean parse = false;
		boolean parsed = false;
		ChatMsg tempMsg = (ChatMsg) msgIter.next();
		//must check if it is a public msg
		if (tempMsg.getMsgType() == 101){
		    if (privateMsg){ //show private messages
			if (tempMsg.getReciever() == myMember.getUserId()){ //ok it's to mee
			    vLine.add("#size#"); vLine.add(fontSize);
			    vLine.add("#date#");
			    if (dateOn)	{ //show dateTime
				vLine.add(tempMsg.getDateTime());
			    }else{
				vLine.add("");
			    }
			    vLine.add("#sender#");		vLine.add(tempMsg.getSenderStr());
			    vLine.add("#msgType#");	vLine.add(tempMsg.getMsgTypeStr());
			    vLine.add("#reciever#");	vLine.add(tempMsg.getRecieverStr());
			    vLine.add("#message#");		vLine.add(tempMsg.getMessage() );
			    sendMsgString.append(imcref.parseExternalDoc(vLine, "private_to_msg.html",user.getLangPrefix(), "103", libName)+"<br>\n");
			    parse = true;
			    parsed = true;
			}else if(tempMsg.getSender()== myMember.getUserId()){ //it's was I who sent it
			    vLine.add("#size#");	vLine.add(fontSize);
			    vLine.add("#date#");
			    if (dateOn){ //show dateTime
				vLine.add(tempMsg.getDateTime());
			    }else{
				vLine.add("");
			    }
			    vLine.add("#sender#");		vLine.add(tempMsg.getSenderStr());
			    vLine.add("#msgType#");		vLine.add(tempMsg.getMsgTypeStr());
			    vLine.add("#reciever#");	vLine.add(tempMsg.getRecieverStr());
			    vLine.add("#message#");		vLine.add(tempMsg.getMessage() );
			    sendMsgString.append(imcref.parseExternalDoc(vLine, "private_from_msg.html",user.getLangPrefix(), "103", libName)+"<br>\n");
			    parse = true;
			    parsed = true;
			}
		    }//end privateMsg
		}else{ //it was a public message
		    if (tempMsg.getMsgType() == CHAT_ENTER_LEAVE_INT){ //it's a enter/leave msg
			if (inOut){//show enter/leave messages
			    if (tempMsg.getSender() != myMember.getUserId()) {
				vLine.add("#size#");	vLine.add(fontSize);
				vLine.add("#date#");
				if (dateOn)	{//show dateTime
				    vLine.add(tempMsg.getDateTime());
				}else{
				    vLine.add("");
				}
				vLine.add("#sender#");		vLine.add(tempMsg.getSenderStr());
				vLine.add("#msgType#");		vLine.add(tempMsg.getMsgTypeStr());
				vLine.add("#reciever#");	vLine.add(tempMsg.getRecieverStr());
				vLine.add("#message#");		vLine.add(tempMsg.getMessage() );
				parse = true;
			    }
			}
		    }else{
			if (true){  //(publicMsg)//show public messages
			    vLine.add("#size#");	vLine.add(fontSize);
			    vLine.add("#date#");
			    if (dateOn){//show dateTime
				vLine.add(tempMsg.getDateTime());
			    }else{
				vLine.add("");
			    }
			    vLine.add("#sender#");		vLine.add(tempMsg.getSenderStr());
			    vLine.add("#msgType#");		vLine.add(tempMsg.getMsgTypeStr());
			    vLine.add("#reciever#");	vLine.add(tempMsg.getRecieverStr());
			    vLine.add("#message#");		vLine.add(tempMsg.getMessage() );
			    parse = true;
			}
		    }
		}//end it was a public message
		//lets parse this line
		if (parse){
		    if ( !parsed ) {
			sendMsgString.append(imcref.parseExternalDoc(vLine, "standard_msg.html",user.getLangPrefix(), "103", libName)+"<br>\n");
		    }
		    if (lastMsgInt == tempMsg.getIdNumber()){
			sendMsgString.append("<a name=\"bottom\"></a><hr>\n");
		    }
		}

	    }//end while loop

	}//end if (req.getParameter("ROOM_ID") != null )
	res.setHeader("Cashe-Control","no-cache");
	res.setHeader("Pragma","no-cache");

	Vector tags = new Vector();
	tags.add("#CHAT_REFRESH#");  tags.add(chatRefresh);
	tags.add("#CHAT_MESSAGES#"); tags.add(sendMsgString.toString()  );
	log(sendMsgString.toString() );
	this.sendHtml(req,res,tags, HTML_TEMPLATE,null) ;
    }

    /**
       Log function, will work for both servletexec and Apache
    **/
    public void log( String str){
	super.log("ChatBoard: " + str) ;
    }



} // End of class
