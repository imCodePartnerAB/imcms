

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

*/

import imcode.external.chat.*;



public class ChatControl extends ChatBase
{
	String HTML_TEMPLATE ;
	//String A_HREF_HTML ;   // The code snippet where the aHref list with all discussions
	//	int DISCSHOWCOUNTER = 20 ;
	// will be placed.
	
	
	/**
	doGet
	*/
	public void doGet(HttpServletRequest req, HttpServletResponse res)
	throws ServletException, IOException
	{
	
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
		String aChatId = params.getProperty("CHAT_ID") ; //vet ej om denna behövs
		
		//lets get the Chat object from the servletContext
		ServletContext myContext = getServletContext();
		log("myContext = "+myContext);
		Chat myChat = (Chat) myContext.getAttribute("CHAT");
		if(myChat == null) return;
		
		//lets's get all messagetypes for this room
		
		
		
		//let's get all the users in this room
		ChatGroup myGroup = (ChatGroup) session.getValue("ChatGroup");
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
		vm.addProperty("alias", "" ) ;	
		vm.addProperty("chatRoom", "" ) ;	
		vm.addProperty("msgTypes", "" ) ;
		vm.addProperty("recipient", group_members ) ;
		vm.addProperty("CHAT_ROOMS", chat_rooms ) ;
		//we probobly needs a few more
			
		//create the page
		this.sendHtml(req,res,vm, HTML_TEMPLATE) ;

	} //**** end doGet ***** end doGet ***** end doGet ******


	

	public void doPost(HttpServletRequest req, HttpServletResponse res)
	throws ServletException, IOException
	{
		log("start doPost");
		
		// Lets validate the session, e.g has the user logged in to Janus?
		if (super.checkSession(req,res) == false)	return ;

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
		
		
		//lets get the Chat object from the servletContext
		ServletContext myContext = getServletContext();
		log("myContext = "+myContext);
		//lets get the Chat
		Chat myChat = (Chat) myContext.getAttribute("CHAT");
		if(myChat == null) return;
		
		
		
		if (req.getParameter("sendMSG") != null)
		{
			
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











