import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import imcode.external.diverse.* ;
import java.rmi.* ;
import java.rmi.registry.* ;

import imcode.external.chat.*;


//meningen är att denna ska ladda framesetet och kolla 
//all nödvändig data innan den gör detta

public class ChatViewer extends ChatBase {

	String HTML_TEMPLATE ;


	public void doPost(HttpServletRequest req, HttpServletResponse res)
	throws ServletException, IOException
	{
		//	log("doPost");
		doGet(req,res);
	}

	public void doGet(HttpServletRequest req, HttpServletResponse res)
	throws ServletException, IOException
	{
		HttpSession session = req.getSession(true);
		//		HttpSessionContext _sescon = session.getSessionContext();



		// Lets validate the session, e.g has the user logged in to Janus?
		if (super.checkSession(req,res) == false)	return ;

		// Lets get the standard parameters and validate them
		// Properties params = super.getParameters(req) ;

		// Lets get the standard SESSION parameters and validate them
		Properties params = super.getSessionParameters(req) ;

		if (super.checkParameters(req, res, params) == false)
		{

			String header = "ChatViewer servlet. " ;
			String msg = params.toString() ;
			ChatError err = new ChatError(req,res,header,1313) ;

			return;
		}

		// Lets get an user object
		imcode.server.User user = super.getUserObj(req,res) ;
		if(user == null) return ;

		if ( !isUserAuthorized( req, res, user ) )
		{
			return;
		}

		// Lets get the url to the servlets directory
		String servletHome = MetaInfo.getServletPath(req) ;

		// Lets get all parameters in a string which we'll send to every servlet in the frameset
		MetaInfo metaInfo = new MetaInfo() ;
		String paramStr = metaInfo.passMeta(params) ;
		//log("params: "+paramStr);


		//lets clean up some in the session just incase
		session.removeValue("checkBoxTextarr");
		session.removeValue("chatParams");
		session.removeValue("chatChecked");



		//ok lets get the chat from the session
		Chat chat = (Chat) session.getValue("theChat");
		if (chat == null)
		{
			log("the chat was null so we will return");
		}

		//lets crete the ChatMember object and add it to the session if there isnt anyone
		ChatMember myMember = (ChatMember) session.getValue("theChatMember");
		if (myMember == null)
		{
			myMember = chat.createChatMember();
			String memberName = "Alias ";
			memberName += (String)(session.getValue("chatAlias")== null ? "" : session.getValue("chatAlias"));
			if (memberName.length() <= 7)
			{
				memberName = user.getString("login_name");
			}
			myMember.setName(memberName);

			myMember.setIPNr(req.getRemoteHost());
			session.putValue("theChatMember", myMember);
			//obs ska ännu lägga in all data om användaren
		}

		//ok lets see which room we shall have
		String rumIdStr = (String)session.getValue("currentRoomId");
		int grupId = -1;
		try
		{
			grupId = Integer.parseInt(rumIdStr);
		}catch(NumberFormatException nfe)
		{
			log("NumberFormatException"+ nfe.getMessage());
			return;
		}

		String theDateTime = (super.getDateToday() +" : "+ super.getTimeNow());
		String senderName = myMember.getName();
		if (session.getValue("theRoom") == null)
		{
			//ok now lets get the groups
			Enumeration enum = chat.getAllChatGroups();
			boolean found = false;
			while (enum.hasMoreElements() && !found)
			{
				ChatGroup tempGr = (ChatGroup) enum.nextElement();
				if (tempGr.getGroupId() == grupId)
				{
					tempGr.addNewGroupMember(myMember);
					//lets send a enter msg
					ChatMsg newEnterMsg = new ChatMsg(ENTER_MSG, "", CHAT_ENTER_LEAVE_INT,
						CHAT_ENTER_LEAVE_INT,"", senderName, -1, theDateTime);
					myMember.addNewMsg(newEnterMsg);
					session.putValue("theRoom", tempGr);
					found = true;
				}
			}
		}else
		{
			
			ChatGroup temp = (ChatGroup)session.getValue("theRoom");
			if (temp.getGroupId() != grupId)
			{
				
				Enumeration enum = chat.getAllChatGroups();
				boolean found = false;
				while (enum.hasMoreElements() && !found)
				{
					ChatGroup tempGr = (ChatGroup) enum.nextElement();
					if (tempGr.getGroupId() == grupId)
					{
						ChatMsg newLeaveMsg = new ChatMsg(	LEAVE_MSG, "", CHAT_ENTER_LEAVE_INT,
							CHAT_ENTER_LEAVE_INT,"",
							senderName, -1, theDateTime);
						myMember.addNewMsg(newLeaveMsg);	
						temp.removeGroupMember(myMember);	
						tempGr.addNewGroupMember(myMember);
						ChatMsg newEnterMsg = new ChatMsg(	ENTER_MSG, "", CHAT_ENTER_LEAVE_INT,
							CHAT_ENTER_LEAVE_INT,"",
							senderName, -1, theDateTime);
						myMember.addNewMsg(newEnterMsg);
						session.putValue("theRoom", tempGr);
						found = true;
					}
				}
			}
		}

		//Chat chat, HttpServletRequest req, boolean bool
		Hashtable hash = super.prepareChatBoardSettings(chat, req, false);
		session.putValue("ChatBoardHashTable", hash );

		//ok lets see if we have an bindingListener
		if (session.getValue("chatBinding") == null)
		{
			session.putValue("chatBinding",new ChatBindingListener());
		}
		//log("req.getRequestedSessionId() = "+req.getRequestedSessionId());

		

		// Lets build the Responsepage
		VariableManager vm = new VariableManager() ;
		vm.addProperty("CHAT_MESSAGES", servletHome + "ChatBoard?" + paramStr);

		vm.addProperty("CHAT_CONTROL", servletHome + "ChatControl?" + paramStr ) ;
		this.sendHtml(req,res,vm, HTML_TEMPLATE) ;
		log("Nu är ChatViewer klar") ;
		return ;

	}//end doGet



	/**
	Detects paths and filenames.
	*/

		public void init(ServletConfig config) throws ServletException
	{

		super.init(config);
		HTML_TEMPLATE = "Chat_Frameset.htm" ;
	}

	/**
	Log function, will work for both servletexec and Apache
	**/

	public void log( String str)
	{
		super.log(str) ;
		System.out.println("ChatViewer: " + str );
	}

} // End of class
