

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

	public final int CHATBOARD_ALLA_INT = 1;

	String HTML_TEMPLATE ;

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

		//log("params :"+params);

		if (super.checkParameters(req, res, params) == false)
		{

			String header = "ChatBoard servlet. " ;
			String msg = params.toString() ;
			ChatError err = new ChatError(req,res,header,1212) ;

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
		String aMetaId = params.getProperty("META_ID") ;
		log("aMetaId = "+aMetaId);
		//		int metaId = Integer.parseInt( aMetaId );
		String aChatId = params.getProperty("CHAT_ID") ;//=id strängen för chatten ????
		log("aChatId = "+aChatId);
		HttpSession session = req.getSession(false) ;

		//ok lets get the settings



		//this buffer is used to store all the msgs to send to the page
		StringBuffer sendMsgString = new StringBuffer("");	
		String chatRefresh = "";	

		//ok let's get all the messages and add them into the buffer			
		if (true)//(req.getParameter("ROOM_ID") != null )
		{ 
			log("nu är vi inne");	

			if(session == null)
			{
				log("session was null so return");
				return;
			}

			//ok lets get the Chat and stuff
			Chat myChat = (Chat)session.getValue("theChat");
			log("myChat = "+myChat);
			ChatMember myMember = (ChatMember)session.getValue("theChatMember");
			log("myMember = "+myMember);	
			ChatGroup myGrupp = (ChatGroup)session.getValue("theRoom");		
			log("myGrupp = "+myGrupp);


			//lets get all the settings for this page
			boolean dateOn = true;
			boolean publicMsg = true;
			boolean privateMsg = true;
			boolean autoscroll = false;
			boolean inOut = true;
			int fontSize = 3;
			String time = "30";
			Integer fontSizeIn = (Integer)session.getValue("chatFontSize");
			fontSizeIn = (fontSizeIn == null ? new Integer(3) : fontSizeIn);
			try
			{
				fontSize = fontSizeIn.intValue();
			}catch(Exception e)
			{
				fontSize = 3;
				log(e.getMessage());
			}

			Properties adminPropps = myChat.getChatParameters();
			Properties settings = ((Properties)session.getValue("chatUserSettings")) == null ? new Properties() :
			(Properties)session.getValue("chatUserSettings");

			log("**** settings ****\n"+settings);

			log("*** adminPropps ***\n"+adminPropps +"\n **** end adminpropps ***");
			
			//sets up show datTime or not
			if (adminPropps.getProperty("dateTime").equals("3"))
			{log("step 4");
				if (settings.getProperty("dateTime") != null)
				{log("step 5");
					if (settings.getProperty("dateTime").equals("off"))
					{
						log("dateOn = false");
						dateOn = false;				
					}
				}	
			}else if(adminPropps.getProperty("dateTime").equals("2"))
			{log("step 6");
				dateOn = false;	
			}

			//sets up show public msg or not
			if (adminPropps.getProperty("publik").equals("3"))
			{log("step 7");
				if (settings.getProperty("publik")!= null)
				{log("step 8");
					if (settings.getProperty("publik").equals("off"))
					{
						log("publicMsg = false");
						publicMsg = false;	
					}
				}			
			}else if(adminPropps.getProperty("publik").equals("2"))
			{log("step 9");
				publicMsg = false;	
			}

			//sets up show private msg or not
			if (adminPropps.getProperty("privat").equals("3"))
			{log("step 10");
				if (settings.getProperty("privat")!= null)
				{log("step 11");
					if (settings.getProperty("privat").equals("off"))
						privateMsg = false;		
				}		
			}else if(adminPropps.getProperty("privat").equals("2"))
			{log("step 12");
				privateMsg = false;	
			}

			//sets up show entrense and exits, or not
			if (adminPropps.getProperty("inOut").equals("3"))
			{log("step 13");
				if (settings.getProperty("inOut")!= null)
				{log("step 14");
					if (settings.getProperty("inOut").equals("off"))
						inOut = false;
				}	
			}else if(adminPropps.getProperty("inOut").equals("2"))
			{log("step 15");
				inOut = false;	
			}

			//sets up autoreload on off
			if (adminPropps.getProperty("reload").equals("3"))
			{log("step 16");
				if (settings.getProperty("autoReload")!= null)
				{	log("step 17");
					log("autoReload = "+settings.getProperty("autoReload"));	
					if (settings.getProperty("autoReload").equals("on"))
					{log("step 18");				
						time = (adminPropps.getProperty("updateTime") == null ? "30" :adminPropps.getProperty("updateTime"));
						chatRefresh = "<META HTTP-EQUIV=\"Refresh\" CONTENT=\""+time+";URL="+servletHome +"\">";
						log("chatRefresh ="+chatRefresh.toString());			
					}
				}else
				{log("step 19");
					time = (adminPropps.getProperty("updateTime") == null ? "30" :adminPropps.getProperty("updateTime"));
					chatRefresh = "<META HTTP-EQUIV=\"Refresh\" CONTENT=\""+time+";URL="+servletHome +"\">";
				}

			}else if(adminPropps.getProperty("reload").equals("1"))
			{log("step 20");
				time = (adminPropps.getProperty("updateTime") == null ? "30" :adminPropps.getProperty("updateTime"));
				chatRefresh = "<META HTTP-EQUIV=\"Refresh\" CONTENT=\""+time+";URL="+servletHome +"\">";						
			}

			//lets get the ignore-list
			//doesnt have one yet


			//let's get all the messages		
			ListIterator msgIter =  myMember.getMessages();

			//lets fix the html-string containing all messags			
			sendMsgString.append("<font size=\""+ fontSize+"\">");
			while(msgIter.hasNext())
			{
				log("step 21");
				ChatMsg tempMsg = (ChatMsg) msgIter.next();
				//must check if it is a public msg
				if (tempMsg.getReciever() == CHATBOARD_ALLA_INT )
				{
					log("step 22");
					if (publicMsg)//show public messages
					{log("step 23");
						if (dateOn)//show dateTime
						{log("step 24");
							sendMsgString.append(tempMsg.getDateTime());	
						}
						sendMsgString.append(" "+tempMsg.getSender());
						sendMsgString.append(" "+tempMsg.getMsgTypeStr());
						sendMsgString.append(" "+tempMsg.getRecieverStr());
						sendMsgString.append(" "+tempMsg.getMessage() );
						//sendMsgString.append("<br>");
					}
				}
				//or if its a private one to this user
				else if (tempMsg.getReciever() == myMember.getUserId())
				{log("step 25");
					if (privateMsg)//show private messages
					{log("step 26");
						if (dateOn)//show dateTime
						{log("step 27");
							sendMsgString.append(tempMsg.getDateTime());	
						}
						sendMsgString.append(" "+tempMsg.getSender());
						sendMsgString.append(" "+tempMsg.getMsgTypeStr());
						sendMsgString.append(" "+tempMsg.getRecieverStr());
						sendMsgString.append(" "+tempMsg.getMessage() );
						//sendMsgString.append("<br>");
					}
				}

				sendMsgString.append("<br>");
			}
			sendMsgString.append("</font>");			
		}//end if (req.getParameter("ROOM_ID") != null )	

		log("!!!!!! sendMsgString !!!!!!!\n"+sendMsgString);
		log("!!!!!!  chatRefresh  !!!!!!!!\n "+chatRefresh);	

		//<META HTTP-EQUIV="Refresh" CONTENT="3;URL=http://www.????.com">
		VariableManager vm = new VariableManager() ;
		vm.addProperty("CHAT_REFRESH", chatRefresh);
		vm.addProperty("CHAT_MESSAGES", sendMsgString.toString()  );
		//vm.addProperty("CHAT_MESSAGES", "stick"  );


		this.sendHtml(req,res,vm, HTML_TEMPLATE) ;
		//	this.showSession(req) ;
		log("ChatBoard doGet är färdig") ;

	} //***  end DoGet  *****  end doGet  *****  end doGet *****  end doGet ***


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
		super.log(str) ;
		System.out.println("ChatBoard: " + str ) ;
	}



} // End of class











