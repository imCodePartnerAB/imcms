

import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import imcode.external.diverse.* ;
import java.rmi.* ;
import java.rmi.registry.* ;
import imcode.util.* ;
import imcode.server.HTMLConv;
import imcode.util.IMCServiceRMI;

/*
tags in use so far
#CHAT_ROOMS#
#recipient#

*/

import imcode.external.chat.*;



public class ChatControl extends ChatBase
{
	private final static String CVS_REV = "$Revision$" ;
	private final static String CVS_DATE = "$Date$" ;
	private final String HTML_TEMPLATE = "theChat.htm";
	private final String SETTINGS_TEMPLATE = "ChatSettings.htm" ;
	private final static String ADMIN_GET_RID_OF_A_SESSION = "Chat_Admin_End_A_Session.htm";
	private final static String ADMIN_BUTTON = "Chat_Admin_Button.htm";
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
		ServletContext myContext = getServletContext();

		// Lets get the standard SESSION parameters and validate them
		Properties params = this.getSessionParameters(req) ;
		if (super.checkParameters(req, res, params) == false)
		{			
			log("RETURN the checkParameters == false");
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
		String chatPoolServer = Utility.getDomainPref("chat_server",host) ;
		// Lets get the url to the servlets directory
		String servletHome = MetaInfo.getServletPath(req) ;
		// Lets get parameters
		String metaId = params.getProperty("META_ID") ;
		//log("aMetaId = "+aMetaId);
		int meta_Id = Integer.parseInt( metaId );
		
		//lets get the chatmember 
		ChatMember myMember = (ChatMember) session.getValue("theChatMember");
		if (myMember == null)
		{
			log("myMember was null so return");
			return;
		}		
		//lets get the Chat
		Chat myChat = myMember.getMyParent();
		if (myChat == null)
		{
			log("myChat was null so return");
			return;
		}
		
		String chatName = myChat.getChatName();
		if(chatName == null)chatName ="";
		
			
		//lets get the room
		ChatGroup myGroup = myMember.getMyGroup();
		if (myGroup == null)
		{
			log("myGroup was null so return");
			return;
		}
		
		//lets get the userlangue if we dont have it OBS must fix this somwhere else
		String userLangId = (String) session.getValue("chatUserLangue");
		if (userLangId == null)
		{
			//we dont have it so we have to get it from somwhere
			//OBS OBS temp solution
			userLangId = "1";
		}

		//ok lets se if the user wants the change setting page
		if (req.getParameter("settings")!= null)
		{	
			//ok we have to fix this method
			this.createSettingsPage(req,res,session,chatPoolServer,metaId, servletHome, user);
			return;
		}//end 

		//strings needed to set up the page
		String chatRoom = myGroup.getGroupName();
		String alias = myMember.getName();
		String selected = (req.getParameter("msgTypes") == null ? "" : req.getParameter("msgTypes").trim());
			
		String msgTypes = htm.createHtmlCode("ID_OPTION", selected, myChat.getMsgTypes() ) ;

		//let's get all the users in this room, for the selectList		
		StringBuffer group_members = new StringBuffer("");
		Iterator iter = myGroup.getAllGroupMembers();
		String selectMemb = (req.getParameter("recipient") == null ? "0" :  req.getParameter("recipient").trim());
		int selNr = Integer.parseInt(selectMemb);		
		while (iter.hasNext())
		{
			ChatMember tempMember = (ChatMember) iter.next();
			String sel = "";
			if(tempMember.getUserId() == selNr)sel = " selected";
			group_members.append("<option value=\""+tempMember.getUserId() + "\""+sel+">" + tempMember.getName()+"</option>\n" );
		}

		//ok lets get all names of chatGroups
		StringBuffer chat_rooms = new StringBuffer("");
		Enumeration enum = myChat.getAllChatGroups();	
		while (enum.hasMoreElements())
		{
			ChatGroup tempGroup = (ChatGroup) enum.nextElement();
			chat_rooms.append("<option value=\""+ tempGroup.getGroupId() +"\">" +tempGroup.getGroupName()+"</option>\n" );	
		}
		
		//let's see if user has adminrights
		String adminButtonKickOut = "";
		String chatAdminLink =  "";
		File templateLib = super.getExternalTemplateFolder(req) ;
		
		if(userHasAdminRights( imcServer, meta_Id, user ))
		{
			//lets get the kickout button
			log("chatName = "+chatName);
			chatAdminLink = createAdminButton(req, ADMIN_BUTTON,metaId,chatName);						
			//lets set up the kick out button OBS fixa detta
			adminButtonKickOut = createAdminButton(req, ADMIN_GET_RID_OF_A_SESSION,metaId,"");						
		}
		
		//lets set up the page to send

		//lets add all the needed tags 
		vm.addProperty("chatName",chatName);
		vm.addProperty("alias", alias ) ;	
		vm.addProperty("chatRoom", chatRoom ) ;	
		vm.addProperty("MSG_PREFIX", msgTypes ) ;
		vm.addProperty("MSG_RECIVER", group_members.toString() ) ;
		vm.addProperty("CHAT_ROOMS", chat_rooms.toString() ) ;

		
		vm.addProperty("CHAT_ADMIN_LINK", chatAdminLink );
		vm.addProperty("CHAT_ADMIN_DISCUSSION", adminButtonKickOut  );

		this.sendHtml(req,res,vm, HTML_TEMPLATE) ;
		log("ChatControl doGet klar");

		return;

	} //**** end doGet ***** end doGet ***** end doGet ******

	
	//doPost
	public void doPost(HttpServletRequest req, HttpServletResponse res)
	throws ServletException, IOException
	{
		log("doPost start");

		// Lets validate the session, e.g has the user logged in to Janus?
		if (super.checkSession(req,res) == false)
		{
			log("super.check session return");
			return ;
		}

		HttpSession session = req.getSession(false);
		ServletContext myContext = getServletContext();
		
		// Lets get the standard SESSION parameters and validate them
		Properties params = this.getSessionParameters(req) ;
		if (super.checkParameters(req, res, params) == false)
		{
			log("super.checkparams return");			
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
		// Lets get the url to the servlets directory
		String servletHome = MetaInfo.getServletPath(req) ;
		// Lets get parameters
		String metaID = params.getProperty("META_ID") ;
		int meta_Id = Integer.parseInt( metaID );
		String aChatId = params.getProperty("CHAT_ID") ; //vet ej om denna behövs????


		//*** *** ok lets handle the useCases *** ***
		
		//lets get the Chat ChatGroup and ChatMember  
		ChatMember myMember = (ChatMember) session.getValue("theChatMember");
		if (myMember == null)
		{
			log("RETURN myMember is null");
			return;
		}
		Chat myChat = myMember.getMyParent();
		if (myChat == null)
		{
			log("RETURN myChat is null");
			return;
		}
		
		ChatGroup myGroup = myMember.getMyGroup();
		if(myGroup == null)
		{
			log("RETURN myGroup is null");
			return;
		}


		
		if (req.getParameter("sendMsg") != null)
		{//**** ok the user wants to send a message ****
			log("*** start sendMsg ***");
			
			String senderName = myMember.getName();

			//lets get the message and all the needed params add it into the msgpool
			String newMessage = (req.getParameter("msg") == null ? "" : req.getParameter("msg").trim());
			if (newMessage.length() != 0)
			{	
				//lets get rid all html tags
				newMessage = HTMLConv.toHTMLSpecial(newMessage);
				//log("newMessage: "+newMessage);
				//lets get the recipient 0 = alla 
				String recieverNrStr = (req.getParameter("recipient") == null ? "" :  req.getParameter("recipient").trim());
				//	log("recieverNrStr = "+recieverNrStr);
				if(recieverNrStr.length() == 0) recieverNrStr = "0"; //it was empty, lets send it too all

				//lets get the messageType fore the message 0 = inget
				String msgTypeNrStr = (req.getParameter("msgTypes") == null ? "" : req.getParameter("msgTypes").trim());
				//	log("msgTypeNrStr = "+msgTypeNrStr);
				if(msgTypeNrStr.length() == 0) msgTypeNrStr = "0";
				//ok lets parse those to int
				int recieverNr, msgTypeNr;
				try
				{
					recieverNr = Integer.parseInt(recieverNrStr);
					msgTypeNr = Integer.parseInt(msgTypeNrStr);

				}catch (NumberFormatException nfe)
				{
					log("NumberFormatException while try to send msg");
					recieverNr = 0;
					msgTypeNr = 0;
				}
								
				String msgTypeStr = ""; //the msgType in text
				if (msgTypeNr != 0)
				{
					Vector vect = myChat.getMsgTypes();
					for(int i = 0; i < vect.size(); i +=2)
					{
						String st = (String) vect.get(i);
						if (st.equals(Integer.toString(msgTypeNr)))
						{
							msgTypeStr = (String) vect.get(i+1);
							break;
						}
					}					
				}
				String recieverStr = "Alla"; //the receiver in text FIX ugly
				if (recieverNr != 0)
				{
					boolean found = false;
					Iterator iter = myGroup.getAllGroupMembers();
					while (iter.hasNext() && !found)
					{
						ChatMember memb = (ChatMember)iter.next();
						if (recieverNr == memb.getUserId())
						{
							recieverStr = memb.getName();
							found = true;
						}
					}
				}
				
				//lets see if it was a private msg to all then wee dont send it
				if (msgTypeNr == 101 && recieverNr == 0)
				{
					doGet(req,res);
					return;
				}else
				{
					int senderNr = myMember.getUserId();
					String senderStr = myMember.getName();
					String theDateTime = (super.getDateToday() +" : "+ super.getTimeNow());	
					
					ChatMsg newChatMsg = new ChatMsg(newMessage,recieverStr,recieverNr,msgTypeNr,msgTypeStr,senderStr,senderNr,theDateTime );
					//log("ChatMsg = "+newChatMsg.getMessage());
					//ok now lets send it "boolean addNewMsg(ChatMsg msg)"
					myMember.addNewChatMsg(newChatMsg);
					//log("antal msg = "+myGroup.getNoOffMessages());	
					//log("ok msg has been sent");
					
					//ok lets log the message
					this.logItToDisc(newChatMsg, myMember);
				}
			}
			
			
			//ok now lets build the page in doGet but
			log("*** end sendMsg ***");	
			doGet(req,res);		
			return;
		
		}//end if (req.getParameter("sendMSG") != null)




		//*** the user wants too change chat room *****
		
				
		if (req.getParameter("changeRoom") != null)
		{
			log("*** start changeRoom ***");
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
			session.putValue("theRoom", mewGroup);
			session.putValue("currentRoomId", roomNrStr);

			//ok lets build the page in doGet
			log("*** end changeRoom ***");	
			doGet(req, res);				
			return;
		}//end if (req.getParameter("changeRoom") != null)



		//*****   the user want to change settings    *****
		
		
		if (req.getParameter("controlOK") != null || req.getParameter("fontInc")!= null ||
													req.getParameter("fontDec")!= null)
		{
			log("*** start changeParams ***");
			//lets collect the new settings
			Hashtable hash = super.prepareChatBoardSettings(myChat, req, true);
			session.putValue("ChatBoardHashTable", hash);
			
			log("*** end changeParams ***");
			doGet(req, res);
			return;
		}//end if (req.getParameter("controlOK") != null)
		
		
		//the admin wants to kick some one out
		
		
		
		//******     chatmember logout    ******	
		
		
		//ok the user wants to logOut so lets send the user to the start page
		if (req.getParameter("logOut") != null)
		{
			log("*** start logOut ***");
			super.cleanUpSessionParams(session);
			log("lets get rid of the user");
			String lastPage = user.getString("last_page");
			if(lastPage.equals("1001"))
			{
				lastPage = RmiConf.getLoginUrl(host);
			}
			log("*** end logOut ***");
			res.sendRedirect(servletHome+"GetDoc?meta_id="+lastPage);			
			return;
		}//end logout
		
		
		//******   kickout a member ******
		//ok lets kick out a messy chat member
		if (req.getParameter("kickOut") != null && userHasAdminRights( imcServer, meta_Id, user))
		{
			log("*** start kickOut ***");
			//lets get the membernumber
			String memberNrStr = (req.getParameter("recipient") == null ? "" :  req.getParameter("recipient").trim());
			log("memberNrStr = "+memberNrStr);
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
			
			//ok now we have the id number, so now lets clean up his session
			ChatBindingListener.getKickoutSession(idNr);
			
//			ChatMember memb = (ChatMember)kickOut.getValue("theChatMember");
			//log("=========="+memb.getUserId());
//			super.cleanUpSessionParams(kickOut);
			log("*** end kickOut ***");
			doGet(req, res);
			return;
		}//end kickout		
		return;


	} // DoPost
	
		
	//this method will create an usersettings page
	//
	public synchronized void createSettingsPage(HttpServletRequest req, HttpServletResponse res, HttpSession session,
									String chatPoolServer, String metaId, String servletHome, imcode.server.User user)
									throws ServletException, IOException
	{
		log("*** start createSettingsPage ***");
		VariableManager vm = new VariableManager() ;

	        File templetUrl =	super.getExternalTemplateFolder(req);
		//HtmlGenerator generator = new HtmlGenerator(templetUrl,HTML_LINE);
		
		Html htm = new Html() ;
		String[] arr;
		if (true)//(checkboxText == null)
		{			
			//we dont have them so we have to get them from db
			RmiConf rmi = new RmiConf(user);
			arr = rmi.execSqlProcedure(chatPoolServer, "C_GetChatParameters "+ metaId );		
			if (arr.length < 8)
			{
				log("arrayen var för liten så return");
				return;
			}
			vm.addProperty("",	""	); 
			String reload = "";
			if(arr[2].equals("3"))
			{
			//	vm.addProperty("",	""	); 
				HtmlGenerator linkHtmlObj = new HtmlGenerator( templetUrl, "checkbox_reload.html" );
				reload = linkHtmlObj.createHtmlString( vm, req );
			}
			String entrance = "";
			if(arr[3].equals("3"))
			{
			//	vm.addProperty("",	""	); 
				HtmlGenerator linkHtmlObj = new HtmlGenerator( templetUrl, "checkbox_entrance.html" );
				entrance = linkHtmlObj.createHtmlString( vm, req );
			}
			String privat = "";
			if(arr[4].equals("3"))
			{
			//	vm.addProperty("",	""	); 
				HtmlGenerator linkHtmlObj = new HtmlGenerator( templetUrl, "checkbox_private.html" );
				privat = linkHtmlObj.createHtmlString( vm, req );
			}
			String publik = "";
			if(arr[5].equals("3"))
			{
			//	vm.addProperty("",	""	); 
				HtmlGenerator linkHtmlObj = new HtmlGenerator( templetUrl, "checkbox_public.html" );
				publik = linkHtmlObj.createHtmlString( vm, req );
			}
			String datetime = "";
			if(arr[6].equals("3"))
			{
			//	vm.addProperty("",	""	); 
				HtmlGenerator linkHtmlObj = new HtmlGenerator( templetUrl, "checkbox_datetime.html" );
				datetime = linkHtmlObj.createHtmlString( vm, req );
			}
			String font = "";
			if(arr[7].equals("3"))
			{
			//	vm.addProperty("",	""	); 
				HtmlGenerator linkHtmlObj = new HtmlGenerator( templetUrl, "buttons_font.html" );
				font = linkHtmlObj.createHtmlString( vm, req );
			}
			
			
			vm.addProperty("reload",	reload	); 
			vm.addProperty("entrance",	entrance); 
			vm.addProperty("private",	privat	); 
			vm.addProperty("public",	publik	); 			
			vm.addProperty("datetime",	datetime); 
			vm.addProperty("font",		font	); 			
		}
		
		log("*** end createSettingsPage ***");
		this.sendHtml(req,res,vm, "chat_settings.html") ;
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

	/**
	This log function is used to log all the discussions in the chatroom
	if the administrator wants it to be done
	*/
	public void logItToDisc(ChatMsg msg, ChatMember member)
	{
		/*obs not in use yet
		StringBuffer logString = new StringBuffer();
		logString.append(member.getIPNr());
		logString.append(msg.getDateTime());
		logString.append(msg.getMessage());
		*/
	}




} // End of class











