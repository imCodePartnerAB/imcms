

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



public class ChatBoard extends ChatBase
{
	private final static String CVS_REV = "$Revision$" ;
	private final static String CVS_DATE = "$Date$" ;


	String HTML_TEMPLATE ;
	private final static String HTML_LINE = "Chat_line.html";

	public void doPost(HttpServletRequest req, HttpServletResponse res)
	throws ServletException, IOException
	{		
		log("someone is trying to acces by doPost!!! It's not allowed yet!");
		return;
	} // DoPost

	//*****  doGet  *****  doGet  *****  doGet  *****  doGet  *****  doGet  *****

	public void doGet(HttpServletRequest req, HttpServletResponse res)
	throws ServletException, IOException
	{
		// Lets validate the session, e.g has the user logged in to Janus?
		//måste kolla så att metoden funkar i ChatBase
		if (super.checkSession(req,res) == false)	return ;

		// Lets get the standard SESSION parameters and validate them
		//måste oxå kollas så att de funkar
		Properties params = this.getSessionParameters(req) ;
		if (params == null)
		{
			log("the params was null so return");
			return;
		}
		log("params :"+params);

		if (super.checkParameters(req, res, params) == false)
		{
			log("return i checkparameters");
			return ;
		}

		// Lets get the user object
		//måste kolla så att metoden funkar i ChatBase
		imcode.server.User user = super.getUserObj(req,res) ;
		if(user == null) return ;

		if ( !isUserAuthorized( req, res, user ) )
		{
			log("user not authorized");
			return;
		}

		// Lets get serverinformation
		String host = req.getHeader("Host") ;
		String imcServer = Utility.getDomainPref("userserver",host) ;
		String confPoolServer = Utility.getDomainPref("chat_server",host) ;

		// Lets get the url to the servlets directory
		//måste kolla så att det är till rätt server
		String servletHome = MetaInfo.getServletPath(req) ;
		servletHome += "ChatBoard";

		// Lets get parameters
		String metaId = params.getProperty("META_ID") ;
		//log("metaId = "+metaId);
		//		int metaId = Integer.parseInt( metaId );
		String aChatId = params.getProperty("CHAT_ID") ;//=id strängen för chatten ????
		//	log("aChatId = "+aChatId);
		HttpSession session = req.getSession(false) ;
		ServletContext myContext = getServletContext();

		//ok lets get the settings



		//this buffer is used to store all the msgs to send to the page
		StringBuffer sendMsgString = new StringBuffer("");	
		String chatRefresh = "";	

		//ok let's get all the messages and add them into the buffer			
		if (true)//(req.getParameter("ROOM_ID") != null )
		{ 
			//log("nu är vi inne");	

			if(session == null)
			{
				log("session was null so return");
				return;
			}

			ChatMember myMember = (ChatMember)session.getValue("theChatMember");
			if (myMember==null)
			{
				log("membern was null so return");
				return;
			}
			Chat myChat = myMember.getMyParent();
			if (myChat==null)
			{
				log("myChat was null so return");
				return;
			}
			ChatGroup myGrupp = myMember.getMyGroup();
			if (myGrupp==null)
			{
				log("myGrupp was null so return");
				return;
			}
			File templetUrl =	super.getExternalTemplateFolder(req);
			HtmlGenerator generator = new HtmlGenerator(templetUrl,HTML_LINE);
			
			//lets get all the settings for this page
			Hashtable theSettings = (Hashtable) session.getValue("ChatBoardHashTable");
			if (theSettings == null)
			{
				log("chatHashTable was null so return");
				return;
			}
			//lets get it all out from it
			boolean dateOn = ((Boolean)theSettings.get("dateTimeBoolean")).booleanValue();
			boolean publicMsg = ((Boolean)theSettings.get("publicMsgBoolean")).booleanValue();
			boolean privateMsg = ((Boolean)theSettings.get("privateMsgBoolean")).booleanValue();
			boolean autoReload = ((Boolean)theSettings.get("reloadBoolean")).booleanValue();
			boolean inOut = ((Boolean)theSettings.get("inOutBoolean")).booleanValue();
			int fontSizeInt = ((Integer)theSettings.get("fontSizeInteger")).intValue();
			log("%%%= "+ fontSizeInt);
			String time = ((Integer)theSettings.get("reloadInteger")).toString();
			log("reload"+time);
			String fontSize = Integer.toString(fontSizeInt);

			log("autoReload = "+autoReload);
			//lets set up the autoreload or not
			if (autoReload)
			{
				chatRefresh = "<META HTTP-EQUIV=\"Refresh\" CONTENT=\""+time+";URL="+servletHome +"\">";
			}

			//lets get the ignore-list
			//doesnt have one yet

			int lastMsgInt = myMember.getLastMsgNr();
			//let's get all the messages		
			ListIterator msgIter =  myMember.getMessages();
			Vector dataV = new Vector();
			//lets fix the html-string containing all messags			
			while(msgIter.hasNext())
			{
				VariableManager vm = new VariableManager();
				boolean parse = false;
				ChatMsg tempMsg = (ChatMsg) msgIter.next();
				//must check if it is a public msg
				if (tempMsg.getMsgType() == 101)
				{
					if (privateMsg)//show private messages
					{
						if (tempMsg.getReciever() == myMember.getUserId())//ok it's to mee
						{	
							vm.addProperty("color","Green");
							vm.addProperty("size",fontSize);
							if (dateOn)//show dateTime
							{
								vm.addProperty("date",tempMsg.getDateTime());	
							}
							else
							{
								vm.addProperty("date","");
							}
							vm.addProperty("sender",tempMsg.getSenderStr());
							vm.addProperty("msgType",tempMsg.getMsgTypeStr());
							vm.addProperty("reciever",tempMsg.getRecieverStr());
							vm.addProperty("message",tempMsg.getMessage() );
							parse = true;
						}else if(tempMsg.getSender()== myMember.getUserId())//it's was I who sent it
						{
							vm.addProperty("color","Blue");
							vm.addProperty("size",fontSize);
							if (dateOn)//show dateTime
							{
								vm.addProperty("date",tempMsg.getDateTime());	
							}
							else
							{
								vm.addProperty("date","");
							}
							vm.addProperty("sender",tempMsg.getSenderStr());
							vm.addProperty("msgType",tempMsg.getMsgTypeStr());
							vm.addProperty("reciever",tempMsg.getRecieverStr());
							vm.addProperty("message",tempMsg.getMessage() );
							parse = true;						
						}
					}//end privateMsg
				}else
					//it was a public message
				{
					if (tempMsg.getMsgType() == CHAT_ENTER_LEAVE_INT)//it's a enter/leave msg
					{
						if (inOut)//show enter/leave messages
						{
							vm.addProperty("color","Black");
							vm.addProperty("size",fontSize);
							if (dateOn)//show dateTime
							{
								vm.addProperty("date",tempMsg.getDateTime());	
							}
							else
							{
								vm.addProperty("date","");
							}
							vm.addProperty("sender",tempMsg.getSenderStr());
							vm.addProperty("msgType",tempMsg.getMsgTypeStr());
							vm.addProperty("reciever",tempMsg.getRecieverStr());
							vm.addProperty("message",tempMsg.getMessage() );
							parse = true;
						}					
					}else
					{
						if (true)//(publicMsg)//show public messages
						{
							vm.addProperty("color","Black");
							vm.addProperty("size",fontSize);
							if (dateOn)//show dateTime
							{
								vm.addProperty("date",tempMsg.getDateTime());	
							}
							else
							{
								vm.addProperty("date","");
							}
							vm.addProperty("sender",tempMsg.getSenderStr());
							vm.addProperty("msgType",tempMsg.getMsgTypeStr());
							vm.addProperty("reciever",tempMsg.getRecieverStr());
							vm.addProperty("message",tempMsg.getMessage() );
							parse = true;
						}
					}				
				}//end it was a public message
				//lets parse this line
				if (parse)
				{
					sendMsgString.append(generator.createHtmlString(vm, req)+"<br>\n");
					if (lastMsgInt == tempMsg.getIdNumber())
					{
						sendMsgString.append("<hr>\n");
					}
				}
				
			}//end while loop
		
		}//end if (req.getParameter("ROOM_ID") != null )	

		
		//<META HTTP-EQUIV="Refresh" CONTENT="3;URL=http://www.????.com">
		//log("chatRefresh = "+chatRefresh);
		VariableManager vm = new VariableManager() ;
		vm.addProperty("CHAT_REFRESH", chatRefresh);
		vm.addProperty("CHAT_MESSAGES", sendMsgString.toString()  );


		this.sendHtml(req,res,vm, HTML_TEMPLATE) ;
		//	this.showSession(req) ;
		log("ChatBoard doGet är färdig") ;

	} //***  end DoGet  *****  end doGet  *****  end doGet *****  end doGet ***

	protected Vector createTags()
	{
		Vector tags = new Vector();
		tags.add("#color#");
		tags.add("#date#");
		tags.add("#sender#");
		tags.add("#msgType#");
		tags.add("#reciever#");
		tags.add("#message#");
		return tags;		
	}
	
	
	/**
	Collects the standard parameters from the SESSION object.
	**/

	public Properties getSessionParameters( HttpServletRequest req)
	throws ServletException, IOException
	{

		//		log("OBS!!! getSessionParameters( HttpServletRequest req)");
		// Lets get the standard metainformation
		Properties reqParams  = super.getSessionParameters(req) ;

		// Lets get the session
		HttpSession session = req.getSession(false) ;
		if(session != null)
		{
			/*		// Lets get the parameters we know we are supposed to get from the request object
			String forumId = ( (String) session.getValue("Conference.forum_id")==null) ? "" : ((String) session.getValue("Conference.forum_id")) ;
			//	String discId = (	(String) session.getValue("Conference.forum_id")==null) ? "" : ((String) session.getValue("Conference.forum_id")) ;
			String discId = (	(String) session.getValue("Conference.disc_id")==null) ? "" : ((String) session.getValue("Conference.disc_id")) ;
			String lastLogindate = (	(String) session.getValue("Conference.last_login_date")==null) ? "" : ((String) session.getValue("Conference.last_login_date")) ;
			String discIndex = (	(String) session.getValue("Conference.disc_index")==null) ? "" : ((String) session.getValue("Conference.disc_index")) ;

			reqParams.setProperty("DISC_INDEX", discIndex) ;
			reqParams.setProperty("LAST_LOGIN_DATE", lastLogindate) ;
			reqParams.setProperty("FORUM_ID", forumId) ;
			reqParams.setProperty("DISC_ID", discId) ;
			 */
		}
		return reqParams ;
	}

	/**
	Collects the parameters from the request object. This function will get all the possible
	parameters this servlet will be able to get. If a parameter wont be found, the session
	parameter will be used instead, or if no such parameter exist in the session object,
	a key with no value = "" will be used instead.
	**/

	public Properties getRequestParameters( HttpServletRequest req)
	throws ServletException, IOException
	{

		log("OBS!!!Properties getRequestParameters( HttpServletRequest req)");
		Properties reqParams = new Properties() ;

		// Lets get our own variables. We will first look for the discussion_id
		// in the request object, if not found, we will get the one from our session object

		String confForumId = req.getParameter("forum_id");
		String discIndex = "" ;
		HttpSession session = req.getSession(false) ;
		if (session != null)
		{
			if(confForumId == null)
				confForumId =	(String) session.getValue("Conference.forum_id") ;
			discIndex = (String) session.getValue("Conference.disc_index") ;
			if(discIndex == null || discIndex.equalsIgnoreCase("null"))	discIndex = "0" ;
		}
		reqParams.setProperty("FORUM_ID", confForumId) ;
		reqParams.setProperty("DISC_INDEX", discIndex) ;
		return reqParams ;
	}


	/**
	Detects paths and filenames.
	*/
	public void init(ServletConfig config)
	throws ServletException
	{
		super.init(config);
		HTML_TEMPLATE = "Chat_Messages.htm" ;
	}

	/**
	Log function, will work for both servletexec and Apache
	**/
	public void log( String str)
	{
		super.log("ChatBoard: " + str) ;
		//System.out.println("ChatBoard: " + str ) ;
	}



} // End of class











