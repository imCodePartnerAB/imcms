

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
	

	String HTML_TEMPLATE ;
	String SETTINGS_TEMPLATE;


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
		Html ht = new Html();

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

		String[] bruse =   user.getBrowserInfo();
		for(int i = 0; i < bruse.length; i++)
		{
			log("getBrowserInfo() ="+bruse[i]);
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
		String aMetaId = params.getProperty("META_ID") ;
		//log("aMetaId = "+aMetaId);
		int metaId = Integer.parseInt( aMetaId );
		//String aChatId = params.getProperty("CHAT_ID") ; funkar ej använd metoden getChatId i Chat.java
		
		
		
		//lets get the Chat
		Chat myChat = (Chat)session.getValue("theChat");
		if (myChat == null)
		{
			log("myChat was null so return");
			return;
		}
		String chatName = (String)session.getValue("chat_name");;
		if(chatName == null)chatName ="";
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

				
		//lets get the userlangue if we dont have it
		String userLangId = (String) session.getValue("chatUserLangue");
		if (userLangId == null)
		{
			//we dont have it so we have to getit from somwhere
			//OBS OBS temp solution
			userLangId = "1";
		}

		//ok lets se if the user wants the change setting page
		if (req.getParameter("settings")!= null)
		{
			this.createSettingsPage(req,res,session,chatPoolServer,userLangId, servletHome, myChat);
			return;
		}//end 

		//strings needed to set up the page
		String chatRoom = myGroup.getGroupName();
		String alias = myMember.getName();
		String selected = (req.getParameter("msgTypes") == null ? "" : req.getParameter("msgTypes").trim());
			
		String msgTypes = ht.createHtmlCode("ID_OPTION", selected, myChat.getMsgTypes() ) ;

		//let's get all the users in this room		
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
		String chatAdminLink =  "";;
		if(userHasAdminRights( imcServer, metaId, user ))
		{
			//lets get the kickout button
			chatAdminLink = this.createChatAdminLink(servletHome, chatName);
						
			//lets set up the kick out button
			adminButtonKickOut ="<INPUT name=kickOut type=submit value=\" Kicka ut chattare \"> ";
						
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
		// Lets get the url to the servlets directory
		String servletHome = MetaInfo.getServletPath(req) ;
		// Lets get parameters
		String aMetaId = params.getProperty("META_ID") ;
		int metaId = Integer.parseInt( aMetaId );
		String aChatId = params.getProperty("CHAT_ID") ; //vet ej om denna behövs????


		//*** *** ok lets handle the useCases *** ***


		//lets get the Chat ChatGroup and ChatMember
		Chat myChat = (Chat)session.getValue("theChat");
		if (myChat == null)
		{
			log("RETURN myChat is null");
			return;
		}
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



		//**** ok the user wants to send a message ****
		
		
		
		
		log("sendMsg = "+req.getParameter("sendMsg") );
		if (req.getParameter("sendMsg") != null)
		{
			
			//the user wants to send a message
			log("ok lets try and send a message");
			String senderName = myMember.getName();

			//lets get the message and all the needed params add it into the msgpool
			String newMessage = (req.getParameter("msg") == null ? "" : req.getParameter("msg").trim());
			if (newMessage.length() != 0)
			{		
				
				newMessage = HTMLConv.toHTMLSpecial(newMessage);
				log(newMessage);	
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
				//log("recieverNr = "+recieverNr);
				//log("msgTypeNr = "+msgTypeNr);
				
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
				String recieverStr = "Alla"; //the receiver in text
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
					myMember.addNewMsg(newChatMsg);
					//log("antal msg = "+myGroup.getNoOffMessages());	
					//log("ok msg has been sent");
					
					//ok lets log the message
					this.logItToDisc(newChatMsg, myMember);
				}
			}
			
			
			//ok now lets build the page in doGet but
			doGet(req,res);
			
			
		
			return;
		}//end if (req.getParameter("sendMSG") != null)




		//*** the user wants too change chat room *****
		
				
		if (req.getParameter("changeRoom") != null)
		{
			log("ok lets try and change chatRoom");
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
			myMember.addNewMsg(newLeaveMsg);

			//ok lets leave the current group
			myGroup.removeGroupMember(myMember);
			
			//ok lets add member in to the new group			
			mewGroup.addNewGroupMember(myMember);

			//ok lets send a enter group msg
			ChatMsg newEnterMsg = new ChatMsg(ENTER_MSG, "", CHAT_ENTER_LEAVE_INT, CHAT_ENTER_LEAVE_INT,"", senderName, -1, theDateTime);
			myMember.addNewMsg(newEnterMsg);
			
			//lets update the session
			session.putValue("theRoom", mewGroup);
			session.putValue("currentRoomId", roomNrStr);

			//ok lets build the page in doGet
			log("ok the room is now changed");
			doGet(req, res);		
			return;
		}//end if (req.getParameter("changeRoom") != null)



		//*****   the user want to change settings    *****
		
		
		if (req.getParameter("controlOK") != null || req.getParameter("fontInc")!= null ||
													req.getParameter("fontDec")!= null)
		{
			log("ok lets change some settings");
			
			Hashtable hash = super.prepareChatBoardSettings(myChat, req, true);
			session.putValue("ChatBoardHashTable", hash);

			doGet(req, res);
			return;
		}//end if (req.getParameter("controlOK") != null)
		
		
		//the admin wants to kick some one out
		
		
		
		//******     chatmember logout    ******	
		
		
		//ok the user wants to logOut so lets send the user to the start page
		if (req.getParameter("logOut") != null)
		{
			//ok lets cleare the session 
			//the logot from chat and group and send leaveMsg
			//takes the bindinglistener care of
			super.cleanUpSessionParams(session);
			log("lets get rid of the user");
			String lastPage = user.getString("last_page");
			if(lastPage.equals("1001"))
			{
				lastPage = RmiConf.getLoginUrl(host);
			}
			res.sendRedirect(servletHome+"GetDoc?meta_id="+lastPage);
		
		}//end logout
		
		
		//******   kickout a member ******
		//ok lets kick out a messy chat member
		if (req.getParameter("kickOut") != null && userHasAdminRights( imcServer, metaId, user))
		{
			log("ok lets try and kick one out");
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
			HttpSession kickOut = ChatBindingListener.getASession(idNr);
			
			ChatMember memb = (ChatMember)kickOut.getValue("theChatMember");
			//log("=========="+memb.getUserId());
			super.cleanUpSessionParams(kickOut);
			
			doGet(req, res);
			return;
		}//end kickout		
		return;


	} // DoPost
	
	private String[] getCheckedOrNot(HttpServletRequest req)
	{
		String[] checked = new String[6];
		checked[0] = (req.getParameter("reload") 	== null ? "" : "CHECKED");
		checked[1] = (req.getParameter("inOut") 	== null ? "" : "CHECKED");
		checked[2] = (req.getParameter("privat") 	== null ? "" : "CHECKED");
		checked[3] = (req.getParameter("publik") 	== null ? "" : "CHECKED");
		checked[4] = (req.getParameter("dateTime") 	== null ? "" : "CHECKED");
		checked[5] = (req.getParameter("font") 		== null ? "" : "CHECKED");
		return checked;
	}
	
	//this method will create an usersettings page
	//
	public synchronized void createSettingsPage(HttpServletRequest req, HttpServletResponse res, HttpSession session,
									String chatPoolServer, String userLangId, String servletHome, Chat myChat)
									throws ServletException, IOException
	{
		VariableManager vm = new VariableManager() ;
		Html htm = new Html() ;	
		String checkboxText= "";//= (String) session.getValue("chatUserCheckBoxes");
		if (true)//(checkboxText == null)
		{			
			checkboxText = "";
			//we dont have them so we have to get them from db
			//lets get thecheckboxes and the text to go with them
			String[] checkBoxTextarr = (String[]) session.getValue("checkBoxTextarr");
			String[] chatParams = (String[]) session.getValue("chatParams");
			if (checkBoxTextarr == null || chatParams == null)
			{
				//we dont have them so we have to get them from db
				RmiConf rmi = new RmiConf();
				checkBoxTextarr = rmi.execSqlProcedure(chatPoolServer, "getCheckboxText "+ userLangId );
				chatParams =  rmi.execSqlProcedure(chatPoolServer, "getParamsToCheckbox "+myChat.getChatId());
				session.putValue("checkBoxTextarr",checkBoxTextarr);
				session.putValue("chatParams", chatParams);
			}			
			
			
			String[] checked = (String[]) session.getValue("chatChecked");
			if (checked == null)
			{	//ok its first time so lets check them all			
				checked = new String[6];
				for(int i=0;i<checked.length;i++)
				{
					checked[i] = "CHECKED";
				}
			}
			checkboxText = this.createUserSettingsHtml(chatParams, checkBoxTextarr, checked, servletHome);
		}
	
		vm.addProperty("CHAT_CHECK_BOXES", checkboxText);
		this.sendHtml(req,res,vm, SETTINGS_TEMPLATE) ;
		return;
	}//end createSettingsPage
	


	/**
	Creates the user settings htmlpage
	not nice but it works
	*/
	

	private String createUserSettingsHtml(String[] chatParams, String[] checkBoxTextarr,String[] checked, String servlet_url)
	{
		StringBuffer htmlStr = new StringBuffer();
		htmlStr.append("<form method=post action="+servlet_url+"ChatControl name=botten>");
		htmlStr.append("<TABLE border=0 cellPadding=0 cellSpacing=0 width=400>");
		htmlStr.append("<tr><td><TABLE border=0 cellPadding=0 cellSpacing=0 width=300>");
		
		boolean okSoFar = false;
		
		for(int i=0; i<checkBoxTextarr.length-2;i+=2)
		{			
			int e = 0;
			if(i != 0)e= i /2;		
			if (chatParams[e].equals("3"))
			{	
				okSoFar = true;
				htmlStr.append("<tr>\n<td width=\"300\">\n");
				htmlStr.append(checkBoxTextarr[i+1]+"</td>");
				htmlStr.append("<td>\n<input type=checkbox name="+checkBoxTextarr[i]+" "+checked[e]+"></td>");				
				htmlStr.append("</tr>");
			}
		}
		htmlStr.append("</table></td><td><TABLE border=0 cellPadding=0 cellSpacing=0 width=200><tr><td>");
		if (chatParams[5].equals("3"))
		{	
			okSoFar = true;
			htmlStr.append(checkBoxTextarr[checkBoxTextarr.length-1]);			
			htmlStr.append("&nbsp;<INPUT name=fontInc type=submit value=\"+\"> <INPUT name=fontDec type=submit value=\"&nbsp;&nbsp;-&nbsp;&nbsp;\">");
		
		}
		if (!okSoFar)		
		{
			htmlStr.append("<center>Inga<br>inställningsmöjligheter<br>tillåtna</center>");
			htmlStr.append("</td></tr><tr><td><br><br><INPUT name=controlOK type=submit value=\"Tillbaka till chatten\"> ");	
	
		}else
		{
			htmlStr.append("</td></tr><tr><td><br><br><INPUT name=controlOK type=submit value=\"Uppdatera inställningar\"> ");	
		}
			
		htmlStr.append("</td></tr></table>");	
		htmlStr.append("</td></tr></form></table>");	
		return htmlStr.toString();	
	}//slut createUserSettingsHtml	
	
	
	
	private String createChatAdminLink(String servletHome, String chatName)
	{
		//adminButton 
			StringBuffer buff = new StringBuffer("");
			buff.append("<form action=\""+servletHome+"ChatCreator\" target=\"_parent\" method=\"get\">");
			buff.append("<input type=\"hidden\" name=\"action\" value=\"admin_chat\">"); 
        	buff.append("<input type=\"submit\" name=\"send\" value=\"admin\">");
			buff.append("<input type=\"hidden\" name=\"chatName\" value=\""+chatName+"\">");
			buff.append("</form>");
			return buff.toString();
	}
	
	
	
	/**
	Detects paths and filenames.
	*/

	public void init(ServletConfig config)
	throws ServletException
	{
		super.init(config);
		HTML_TEMPLATE = "theChat.htm" ;
		SETTINGS_TEMPLATE = "ChatSettings.htm" ;
	}

	/**
	Log function, will work for both servletexec and Apache
	only for internal use
	**/
	public void log(String str)
	{
		super.log(str) ;
	//	System.out.println("ChatControl: " + str ) ;
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











