

import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import imcode.external.diverse.* ;
import java.rmi.* ;
import java.rmi.registry.* ;
import imcode.util.* ;
import imcode.util.IMCServiceRMI;

/*
tags in use so far
#CHAT_ROOMS#
#recipient#

*/

import imcode.external.chat.*;



public class ChatControl extends ChatBase
{
	private final static String LEAVE_MSG = "lämnar rummet";
	private final static String ENTER_MSG = "stiger in";
	
	String HTML_TEMPLATE ;

	
//OBS OBS OBS har inte fixat kontroll om det är administratör eller användare
//för det är ju lite mera knappar och metoder som ska med om det är en admin
//tex en knapp för att kicka ut användare
//ev ska oxå tidtaggen på medelanden fixas till här
//även loggningen ska fixas här om sådan efterfrågas
//vidare måste silning av åäö och taggar fixas 
	
	/**
	doGet
	*/
	public void doGet(HttpServletRequest req, HttpServletResponse res)
	throws ServletException, IOException
	{
		//ok lets load the page for first time
		
		VariableManager vm = new VariableManager() ;
		Html htm = new Html() ;
		
		// Lets validate the session, e.g has the user logged in to Janus?
		if (super.checkSession(req,res) == false)	return ;
	
		HttpSession session = req.getSession(false);

		// Lets get the standard SESSION parameters and validate them
		Properties params = this.getSessionParameters(req) ;
		if (super.checkParameters(req, res, params) == false)
		{
			/*
			String header = "ConfDisc servlet. " ;
			String msg = params.toString() ;
			ChatError err = new ChatError(req,res,header,1) ;
			*/
			return ;
		}
	
		// Lets get the user object
		imcode.server.User user = super.getUserObj(req,res) ;
		if(user == null) return ;

		if ( !isUserAuthorized( req, res, user ) )
		{
			return;
		}
	
		// Lets get serverinformation
		String host = req.getHeader("Host") ;
		String imcServer = Utility.getDomainPref("userserver",host) ;
		String confPoolServer = Utility.getDomainPref("chat_server",host) ;

		//RmiConf rmi = new RmiConf(user) ;

		// Lets get the url to the servlets directory
		String servletHome = MetaInfo.getServletPath(req) ;

		// Lets get parameters
		String aMetaId = params.getProperty("META_ID") ;
		int metaId = Integer.parseInt( aMetaId );
		String aChatId = params.getProperty("CHAT_ID") ; 
		//man skulle kanske kunna använda meta_id i stället
		//men det är ju inte säkert att det är det värdet som kommer att användas
		
		
		//lets get the Chat object from the servletContext
		ServletContext myContext = getServletContext();
		log("myContext = "+myContext);
		
		Chat myChat = (Chat) myContext.getAttribute(aChatId);
		if(myChat == null) return;
		
		//lets get my room and my alias
		ChatMember myMember = (ChatMember) session.getValue("ChatMember");
		if(myMember == null) return;
		ChatGroup myGroup = myMember.getMyGroup();
		if(myGroup == null) return;
		String chatRoom = myGroup.getChatGroupName();
		String alias = myMember.getName();
		
		//lets's get all messagetypes for this room
		StringBuffer msgTypes = new StringBuffer();
		ListIterator listIter = myGroup.getAllMsgTypes();
	
		while (listIter.hasNext())
		{
			MsgType tempType = (MsgType) listIter.next();
			msgTypes.append("<option value=\""+tempType.getIdNr() + "\">" + tempType.getName()+"</option>\n" );
		}
		
		
		//let's get all the users in this room		
		StringBuffer group_members = new StringBuffer();
		Iterator iter = myGroup.getAllGroupMembers();
		while (iter.hasNext())
		{
			ChatMember tempMember = (ChatMember) iter.next();
			group_members.append("<option value=\""+tempMember.getUserId() + "\">" + tempMember.getName()+"</option>\n" );
		}
			
		//ok lets get all names of chatGroups
		StringBuffer chat_rooms = new StringBuffer();
		Enumeration enum = myChat.getAllChatGroups();
		//let's build the html code fore this select-list
		//should maby moves to the Html-class but it doesn't takes enumerations
		//an alternity is to move it to the ChatBase	
		while (enum.hasMoreElements())
		{
			ChatGroup tempGroup = (ChatGroup) enum.nextElement();
			chat_rooms.append("<option value=\""+ tempGroup.getGroupId() +"\">" +tempGroup.getChatGroupName()+"</option>\n" );	
		}
		
			
		//lets set up the page to send
		
		//lets add all the needed tags 
		vm.addProperty("alias", alias ) ;	
		vm.addProperty("chatRoom", chatRoom ) ;	
		vm.addProperty("msgTypes", msgTypes ) ;
		vm.addProperty("recipient", group_members ) ;
		vm.addProperty("CHAT_ROOMS", chat_rooms ) ;
		//we probobly needs a few more
			
		//create the page
		this.sendHtml(req,res,vm, HTML_TEMPLATE) ;
		return;

	} //**** end doGet ***** end doGet ***** end doGet ******


	

	public void doPost(HttpServletRequest req, HttpServletResponse res)
	throws ServletException, IOException
	{
		
		log("start doPost");
		
		// Lets validate the session, e.g has the user logged in to Janus?
		if (super.checkSession(req,res) == false)	return ;
		
		HttpSession session = req.getSession(false);

		// Lets get the standard SESSION parameters and validate them
		Properties params = this.getSessionParameters(req) ;
		if (super.checkParameters(req, res, params) == false)
		{
			/*
			String header = "ConfDisc servlet. " ;
			String msg = params.toString() ;
			ChatError err = new ChatError(req,res,header,1) ;
			*/
			return ;
		}

		// Lets get the user object
		imcode.server.User user = super.getUserObj(req,res) ;
		if(user == null) return ;

		if ( !isUserAuthorized( req, res, user ) )
		{
			return;
		}

		// Lets get serverinformation
		String host = req.getHeader("Host") ;
		String imcServer = Utility.getDomainPref("userserver",host) ;
		String confPoolServer = Utility.getDomainPref("chat_server",host) ;

		//RmiConf rmi = new RmiConf(user) ;

		// Lets get the url to the servlets directory
		String servletHome = MetaInfo.getServletPath(req) ;

		// Lets get parameters
		String aMetaId = params.getProperty("META_ID") ;
		int metaId = Integer.parseInt( aMetaId );
		String aChatId = params.getProperty("CHAT_ID") ; //vet ej om denna behövs
		
		//*** *** ok lets handle the useCases *** ***
		
		
		
		//lets get the ChatObject from ChatBase
		imcode.external.chat.Chat myChat = super.getChat(aChatId);
		if(myChat == null) return;
		
		//lets get the member and group from the session
		imcode.external.chat.ChatMember myMember = (ChatMember) session.getValue("ChatMember");
		if(myMember == null) return;
		imcode.external.chat.ChatGroup myGroup = myMember.getMyGroup();
		if(myGroup == null) return;
	
		//**** ok the user wants to send a message ****
		if (req.getParameter("sendMSG") != null)
		{
			//the user wants to send a message
			
			//lets get the message and all the needed params add it into the msgpool
			String newMessage = (req.getParameter("msg") == null ? "" : req.getParameter("msg").trim());
			if(newMessage.length() == 2) return; //there was an empty string
			
			//lets get the recipient 1 = all 
			String recieverNrStr = (req.getParameter("recipient") == null ? "" :  req.getParameter("recipient").trim());
			if(recieverNrStr.length() == 2) recieverNrStr = "1"; //it was empty lets send it too all
			
			//lets get the messageType fore the message 0 = none
			String msgTypeNrStr = (req.getParameter("msgTypes") == null ? "" : req.getParameter("msgTypes").trim());
			if(msgTypeNrStr.length() == 2) msgTypeNrStr = "0";
			
			//ok lets parse those to int
			int recieverNr, msgTypeNr;
			try
			{
				recieverNr = Integer.parseInt(recieverNrStr);
				msgTypeNr = Integer.parseInt(msgTypeNrStr);
			}catch (NumberFormatException nfe)
			{
				log("NumberFormatException while try to send msg");
				recieverNr = 1;
				msgTypeNr = 0;
			}
			
			int senderNr = myMember.getUserId();
			
			String theDateTime = (super.getDateToday() +" : "+ super.getTimeNow());
			
			//ok lets create the message  "ChatMsg(String chatMsg, int reciever, int msgType, int sender)"
			imcode.external.chat.ChatMsg newChatMsg = new ChatMsg(newMessage, recieverNr, msgTypeNr, senderNr, theDateTime);
			
			//ok now lets send it "boolean addNewMsg(ChatMsg msg)"
			myMember.addNewMsg(newChatMsg);		
			
			log("ok now has the msg been sent");
			
			//ok now lets build the page in doGet
			doGet(req,res);
		}//end if (req.getParameter("sendMSG") != null)
		
		
		//*** the user wants too change chat room *****
		if (req.getParameter("changeRoom") != null)
		{
			//ok first lets send a msg to tell everybody that the user has left the room
			int senderNr = myMember.getUserId();
			ChatMsg newLeaveMsg = new ChatMsg(LEAVE_MSG, 1, 0, senderNr, "");
			myMember.addNewMsg(newLeaveMsg);
			
			//ok lets get the "new room number"
			String roomNrStr = (req.getParameter("changeRoom") == null ? "" : req.getParameter("changeRoom").trim()); 
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
			mewGroup.addNewGroupMember(myMember);
			
			//ok lets send a enter group msg
			ChatMsg newEnterMsg = new ChatMsg(ENTER_MSG, 1, 0, senderNr, "");
			myMember.addNewMsg(newEnterMsg);
			
			//ok lets build the page in doGet
			log("ok the room is now changed");
			doGet(req, res);		
		}
	
		
		

	} // DoPost

	/**
	Detects paths and filenames.
	*/

	public void init(ServletConfig config)
	throws ServletException
	{
		super.init(config);
		HTML_TEMPLATE = "theChat.htm" ;
		//	A_HREF_HTML = "Conf_Disc_List.htm" ;
	}

	/**
	Log function, will work for both servletexec and Apache
	only for internal use
	**/
	public void log(String str)
	{
		super.log(str) ;
		System.out.println("ChatControl: " + str ) ;
	}

	/**
	This log function is used to log all the discussions in the chatroom
	if the administrator wants it to be done
	*/
	public void systemLog()
	{

	}




} // End of class











