

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
		if (super.checkSession(req,res) == false)
		{
			log("RETURN super.checksession");
			return ;
		}	

		HttpSession session = req.getSession(false);

		// Lets get the standard SESSION parameters and validate them
		Properties params = this.getSessionParameters(req) ;
		if (super.checkParameters(req, res, params) == false)
		{
			
			String header = "ChatControl servlet. " ;
			String msg = params.toString() ;
			ChatError err = new ChatError(req,res,header,2201) ;
			
			log("RETURN super.checkParameters");
			return ;
		}

		// Lets get the user object
		imcode.server.User user = super.getUserObj(req,res) ;
		if(user == null) 
		{
			log("RETURN usern is null");
		   	return ;
		}

		if ( !isUserAuthorized( req, res, user ) )
		{
			log("RETURN user is not authorized");
			return;
		}

		// Lets get serverinformation
		String host = req.getHeader("Host") ;
		String imcServer = Utility.getDomainPref("userserver",host) ;
		String confPoolServer = Utility.getDomainPref("chat_server",host) ;


		// Lets get the url to the servlets directory
		String servletHome = MetaInfo.getServletPath(req) ;

		// Lets get parameters
		String aMetaId = params.getProperty("META_ID") ;
	//	int metaId = Integer.parseInt( aMetaId );
		String aChatId = params.getProperty("CHAT_ID") ; //vet ej om denna behövs?????????????


		//lets get the Chat
		Chat myChat = (Chat)session.getValue("theChat");
		if (myChat == null)
		{
			log("myChat was null so return");
		 	return;
		}
		//lets get the chatmember
		ChatMember myMember = (ChatMember) session.getValue("theChatMember");
		if (myMember == null)
		{
			log("myMember was null so return");
			return;
		}	
		//lets get the room
		ChatGroup myGroup = myMember.getMyGroup();
		if (myGroup == null)
		{
			log("myGroup was null so return");
			return;
		}
		
		//lets get all Properties from Chat
		//här måste vi skriva en metod som returnerar en Properties från Chat
		//Properties pageOptions = myChat.getProperties();
		//session.putValue("pageOptions");
		
		
		String chatRoom = myGroup.getChatGroupName();
		String alias = myMember.getName();

		//lets's get all messagetypes for this room
		Vector msgTypeVect = myChat.getMsgTypes();
		Html ht = new Html();
		String msgTypes = ht.createHtmlCode("ID_OPTION", "", msgTypeVect ) ;
		
		//let's get all the users in this room		
		StringBuffer group_members = new StringBuffer("");
		Iterator iter = myGroup.getAllGroupMembers();
		while (iter.hasNext())
		{
			log("loopar");
			ChatMember tempMember = (ChatMember) iter.next();
			group_members.append("<option value=\""+tempMember.getUserId() + "\">" + tempMember.getName()+"</option>\n" );
		}

		//ok lets get all names of chatGroups
		StringBuffer chat_rooms = new StringBuffer("");
		Enumeration enum = myChat.getAllChatGroups();
		//let's build the html code fore this select-list
		//should maby moves to the Html-class but it doesn't takes enumerations
		//an alternity is to move it to the ChatBase	
		while (enum.hasMoreElements())
		{
			log("loopar igen");
			ChatGroup tempGroup = (ChatGroup) enum.nextElement();
			chat_rooms.append("<option value=\""+ tempGroup.getGroupId() +"\">" +tempGroup.getChatGroupName()+"</option>\n" );	
		}


		//lets set up the page to send

		//lets add all the needed tags 
		vm.addProperty("alias", alias ) ;	
		vm.addProperty("chatRoom", chatRoom ) ;	
		vm.addProperty("MSG_PREFIX", msgTypes ) ;
		vm.addProperty("MSG_RECIVER", group_members.toString() ) ;
		vm.addProperty("CHAT_ROOMS", chat_rooms.toString() ) ;
		
	
		//we probobly needs a few more

		//create the page
		this.sendHtml(req,res,vm, HTML_TEMPLATE) ;
		log("nu är jag klar");
		
		return;

	} //**** end doGet ***** end doGet ***** end doGet ******




	public void doPost(HttpServletRequest req, HttpServletResponse res)
	throws ServletException, IOException
	{

/*		log("start doPost nu listar vi alla parametrar");
		Enumeration para = req.getParameterNames();
		while (para.hasMoreElements())
		{
			String st =(String) para.nextElement();
			log(st+" = "+req.getParameterValues(st));
		}
*/
		// Lets validate the session, e.g has the user logged in to Janus?
		if (super.checkSession(req,res) == false)
		{
			log("super.check session return");
			return ;
		}

		HttpSession session = req.getSession(false);

		// Lets get the standard SESSION parameters and validate them
		Properties params = this.getSessionParameters(req) ;
		if (super.checkParameters(req, res, params) == false)
		{
			log("super.checkparams return");
			
			String header = "ChatControl servlet. " ;
			String msg = params.toString() ;
			ChatError err = new ChatError(req,res,header,2202) ;
			
			return ;
		}

		// Lets get the user object
		imcode.server.User user = super.getUserObj(req,res) ;
		if (user == null)
		{
			log("user is null return");
			return ;
		}

		if ( !isUserAuthorized( req, res, user ) )
		{
			log("user is not autorized return");
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
	//	int metaId = Integer.parseInt( aMetaId );
		String aChatId = params.getProperty("CHAT_ID") ; //vet ej om denna behövs????

		//*** *** ok lets handle the useCases *** ***



		//lets get the Chat Object
		Chat myChat = (Chat)session.getValue("theChat");
		if (myChat == null)
		{
			log("RETURN myChat is null");
			return;
		}

		//lets get the member and group from the session
		ChatMember myMember = (ChatMember) session.getValue("theChatMember");
		if (myMember == null)
		{
			log("RETURN myMember is null");
			return;
		}
		ChatGroup myGroup = (ChatGroup) session.getValue("theRoom");
		if(myGroup == null) 
		{
			log("RETURN myGroup is null");
			return;
		}
		
	log("OK so far !!!!");
		
		
		//**** ok the user wants to send a message ****
		if (req.getParameter("sendMsg") != null)
		{
			//the user wants to send a message
			log("ok lets try and send a message");
			String senderName = myMember.getName();
						
			//lets get the message and all the needed params add it into the msgpool
			String newMessage = (req.getParameter("msg") == null ? "" : req.getParameter("msg").trim());
			if (newMessage.length() == 0)
			{
			
				//lets get the recipient 1 = all 
				String recieverNrStr = (req.getParameter("recipient") == null ? "" :  req.getParameter("recipient").trim());
				log("recieverNrStr = "+recieverNrStr);
				if(recieverNrStr.length() == 2) recieverNrStr = "1"; //it was empty lets send it too all
		
				//lets get the messageType fore the message 0 = none
				String msgTypeNrStr = (req.getParameter("msgTypes") == null ? "" : req.getParameter("msgTypes").trim());
				log("msgTypeNrStr = "+msgTypeNrStr);
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

				//ok lets create the message  "public ChatMsg(String chatMsg, String recieverStr,int reciever, int msgType, String msgTypeStr, String sender, String dateTime)"
				imcode.external.chat.ChatMsg newChatMsg = new ChatMsg(newMessage, "obs",recieverNr, msgTypeNr,"obs", senderName, theDateTime);
				log("ChatMsg = "+newChatMsg.getMessage());
				//ok now lets send it "boolean addNewMsg(ChatMsg msg)"
				myMember.addNewMsg(newChatMsg);	
				log("antal i listan efter adden= "+myGroup.getNoOffMessages());		

				log("ok now has the msg been sent");
			}
			
			//ok now lets build the page in doGet
			doGet(req,res);
			return;
		}//end if (req.getParameter("sendMSG") != null)


		//*** the user wants too change chat room *****
		log("changeRoom = "+req.getParameter("changeRoom"));
		if (req.getParameter("changeRoom") != null)
		{
			log("ok lets try and change chatRoom");
			//ok first lets send a msg to tell everybody that the user has left the room
			int senderNr = myMember.getUserId();
			String senderName = myMember.getName();
			ChatMsg newLeaveMsg = new ChatMsg(LEAVE_MSG,"", 1, 0,"", senderName, "");
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
			ChatMsg newEnterMsg = new ChatMsg(ENTER_MSG, "", 1, 0,"", senderName, "");
			myMember.addNewMsg(newEnterMsg);

			//ok lets build the page in doGet
			log("ok the room is now changed");
			doGet(req, res);		
		}//end if (req.getParameter("changeRoom") != null)
			
		
		//the user want to change settings
		if (req.getParameter("controlOK") != null)
		{
			log("ok lets change some settings");
			Enumeration ene = req.getParameterNames();
			log("++++++++++++++++++++++++++++");
			while (ene.hasMoreElements())
			{
				String st = (String) ene.nextElement();
				
				log(st+" = "+req. getParameter(st));
			}
			log("++++++++++++++++++++++++++++");
			
			//lets get the parameters
			String showPrivate 	= req.getParameter("private");
			String showInOut 	= req.getParameter("inOut");
			String autoscroll	= req.getParameter("autoscroll");
			String showDateTime = req.getParameter("dateTime");
			String showPublicMsg= req.getParameter("public");			
			String fontInc 		= req.getParameter("fontInc");
			String fontDec		= req.getParameter("fontDec");
			
			//ok lets update the chatSettings in session
			Properties settings = (Properties)session.getValue("chatUserSettings");
			if (settings == null) settings = new Properties();
			{	//if null lets create one
				settings = new Properties();
				session.putValue("chatUserSettings",settings );
			}
			settings.setProperty("private",		(showPrivate 	== null ? "off" : "on"));
			settings.setProperty("inOut",		(showInOut 		== null ? "off" : "on"));
			settings.setProperty("autoscroll",	(autoscroll 	== null ? "off" : "on"));
			settings.setProperty("dateTime",	(showDateTime 	== null ? "off" : "on"));
			settings.setProperty("public",		(showPublicMsg 	== null ? "off" : "on"));
			settings.setProperty("fontInc",		(fontInc 		== null ? "off" : "on"));
			settings.setProperty("fontDec",		(fontDec 		== null ? "off" : "on"));
			
			doGet(req, res);
			return;
		}//end if (req.getParameter("controlOK") != null)
		
		return;


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











