

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



public class ChatBoard extends ChatBase {

	
	String HTML_TEMPLATE ;
	
	public void doPost(HttpServletRequest req, HttpServletResponse res)
									throws ServletException, IOException 
	{
		//här ska det ännu fixas lite det är nog hit vi komer om användaren vill byta font storlek
		//har dock skrivit lite om det i do get men det bör nog flyttas hit
		log("someone is trying to acces by doPost!!! It's not allowed yet!");
		
		
		return;
		
	} // DoPost

	//*****  doGet  *****  doGet  *****  doGet  *****  doGet  *****  doGet  *****
		
	public void doGet(HttpServletRequest req, HttpServletResponse res)
	throws ServletException, IOException {
	
	

		// Lets validate the session, e.g has the user logged in to Janus?
		//måste kolla så att metoden funkar i ChatBase
		if (super.checkSession(req,res) == false)	return ;

		// Lets get the standard SESSION parameters and validate them
		//måste oxå kollas så att de funkar
		Properties params = this.getSessionParameters(req) ;
		if (super.checkParameters(req, res, params) == false) {
			/*
			String header = "ConfDisc servlet. " ;
			String msg = params.toString() ;
			ChatError err = new ChatError(req,res,header,1) ;
			*/
			return ;
		}

		// Lets get the user object
		//måste kolla så att metoden funkar i ChatBase
		imcode.server.User user = super.getUserObj(req,res) ;
		if(user == null) return ;

		if ( !isUserAuthorized( req, res, user ) ) {
			return;
		}

		// Lets get serverinformation
		String host = req.getHeader("Host") ;
		String imcServer = Utility.getDomainPref("userserver",host) ;
		String confPoolServer = Utility.getDomainPref("chat_server",host) ;

		// Lets get the url to the servlets directory
		//måste kolla så att det är till rätt server
		String servletHome = MetaInfo.getServletPath(req) ;

		// Lets get parameters
		String aMetaId = params.getProperty("META_ID") ;
		int metaId = Integer.parseInt( aMetaId );
		String aChatId = params.getProperty("CHAT_ID") ;//=id strängen för chatten
		
			
		//used to store all params to resend
		//är inte så snygg men får duga så länge
		StringBuffer paramString = new StringBuffer("?");
		
		//ok lets set up the font font size
		//det här med ändringarna ska flyttas till doPost
		int fontSize=3;
		paramString.append("FONT_SIZE=\""+ fontSize +"\"");
		/*if (req.getParameter("FONT_SIZE") != null)
		{
			String sizeStr = req.getParameter("FONT_SIZE");
			try
			{	
				fontSize = Integer.parseInt(sizeStr);
				
				String sizeAction = req.getParameter("FONT");
				if(sizeAction.equalsIgnoreCase("Inc"))
				{
					if(fontSize < 7) fontSize++;
				}
				if(sizeAction.equalsIgnoreCase("Dec"))
				{
					if(fontSize > 1) fontSize--;
				}
				
			}catch(NumberFormatException nfe)
			{
				fontSize = 3;
				log("NumberFormatException in fontsize");
			}
			paramString.append("FONT_SIZE=\""+ fontSize +"\"");
				
		}
		else
		{
			String sizeStr = req.getParameter("FONT_SIZE");
			try
			{	
				fontSize = Integer.parseInt(sizeStr);
				
			}catch(NumberFormatException nfe)
			{
				fontSize = 3;
				log("NumberFormatException in fontsize");
			}
			paramString.append("FONT_SIZE=\""+ fontSize +"\"");
		}//end setting up the font size*/
	
		
		//this buffer is used to store all the msgs to send to the page
		StringBuffer sendMsgString = new StringBuffer();		
		
		//ok let's get all the messages and add them into the buffer			
		if (req.getParameter("ROOM_ID") != null )	
		{ 
		
			HttpSession session = req.getSession(false) ;
			if(session != null) 
			{
				return;
			}
			
					
			//lets get the Chat from ChatBase
			imcode.external.chat.Chat myChat = super.getChat(aChatId);
			if(myChat == null) return;
			
			//ok let's get the ChatMember from the session
			ChatMember myMember = (ChatMember)session.getValue("ChatMember");
			if(myMember == null) return;
			//lets get the current group
			ChatGroup myGroup = myMember.getMyGroup();
			if( myGroup == null) return;
			
			
			//lets get the ignore-list
			 //doesnt have one yet
			 
			
			//let's get all the messages		
			ListIterator msgIter =  myMember.getMessages();		
			
			//lets fix the html-string to send
			sendMsgString.append("<font size=\""+ fontSize+">");
			while(msgIter.hasNext())
			{
				ChatMsg tempMsg = (ChatMsg) msgIter.next();
				//must check if it is a public msg
				if (tempMsg.getReciever() == 1 ) 
				{
					sendMsgString.append(tempMsg.getDateTime() +" : " +
										myMember.getName() +"  "+
										myGroup.getMsgTypeName(tempMsg.getMsgType()) +"  "+
										myChat.getChatMember(tempMsg.getReciever()).getName() +" : "+
										tempMsg.getMessage()	);
				}
				//or if its a private one to this user
				else if (tempMsg.getReciever() == myMember.getUserId())
				{
					//obs här ska det nog in någon färgtagg på texten
					sendMsgString.append(tempMsg.getDateTime() +" : " +
										myMember.getName() +"  "+
										myGroup.getMsgTypeName(tempMsg.getMsgType()) +"  "+
										myChat.getChatMember(tempMsg.getReciever()).getName() +" : "+
										tempMsg.getMessage()	);
				}
				
				sendMsgString.append("<br>");
			}
			sendMsgString.append("</font>");
			paramString.append("&ROOM_ID=\""+ req.getParameter("ROOM_ID") +"\"");				
		}//end if (req.getParameter("ROOM_ID") != null )	
		
	

		VariableManager vm = new VariableManager() ;
		vm.addProperty("CHAT_MESSAGES", sendMsgString  );
		
		this.sendHtml(req,res,vm, HTML_TEMPLATE) ;
		//	this.showSession(req) ;
		log("ChatBoard doGet är färdig") ;
		
	} //***  end DoGet  *****  end doGet  *****  end doGet *****  end doGet ***

	/**
	Returns the current discussion index. If somethings happens, zero will be returned.
	*/
/*	public int getDiscIndex( HttpServletRequest req) {
		try {
			HttpSession session = req.getSession(false) ;
			if(session != null) {
				String indexStr = (String) session.getValue("Conference.disc_index") ;
				int anInt = Integer.parseInt(indexStr) ;
				return anInt ;
			}
		} catch(Exception e ) {
			log("GetDiscIndex failed!") ;
			return 0 ;
		}
		return 0 ;
	}
*/


	/**
	Collects the standard parameters from the SESSION object.
	**/

	public Properties getSessionParameters( HttpServletRequest req)
	throws ServletException, IOException {
		
		log("OBS!!! getSessionParameters( HttpServletRequest req)");
		// Lets get the standard metainformation
		Properties reqParams  = super.getSessionParameters(req) ;

		// Lets get the session
		HttpSession session = req.getSession(false) ;
		if(session != null) {
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
	throws ServletException, IOException {

		log("OBS!!!Properties getRequestParameters( HttpServletRequest req)");
		Properties reqParams = new Properties() ;

		// Lets get our own variables. We will first look for the discussion_id
		// in the request object, if not found, we will get the one from our session object

		String confForumId = req.getParameter("forum_id");
		String discIndex = "" ;
		HttpSession session = req.getSession(false) ;
		if (session != null) {
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
	Collects the parameters used to detect the buttons from the request object. Checks
	if the Properties object is null, if so it creates one, otherwise it uses the
	object passed to it.
	**/
/*
	public Properties getSearchParameters( HttpServletRequest req, Properties params)
	throws ServletException, IOException {

		//Lets get the search criterias
		String cat = (req.getParameter("CATEGORY")==null) ? "" : (req.getParameter("CATEGORY")) ;
		String search = (req.getParameter("SEARCH")==null) ? "" : (req.getParameter("SEARCH")) ;
		String fromDate = (req.getParameter("FR_DATE")==null) ? "" : (req.getParameter("FR_DATE")) ;
		String fromVal = (req.getParameter("FR_VALUE")==null) ? "" : (req.getParameter("FR_VALUE")) ;

			String toDate = (req.getParameter("TO_DATE")==null) ? "" : (req.getParameter("TO_DATE")) ;
		String toVal = (req.getParameter("TO_VALUE")==null) ? "" : (req.getParameter("TO_VALUE")) ;
		//	String searchButton = (req.getParameter("BUTTON_SEARCH")==null) ? "" : (req.getParameter("BUTTON_SEARCH")) ;

		params.setProperty("CATEGORY", super.verifySqlText(cat.trim())) ;
		params.setProperty("SEARCH", super.verifySqlText(search.trim())) ;
		params.setProperty("FR_DATE", super.verifySqlText(fromDate.trim())) ;
		params.setProperty("TO_DATE", super.verifySqlText(toDate.trim())) ;
		params.setProperty("FR_VALUE", super.verifySqlText(fromVal.trim())) ;
		params.setProperty("TO_VALUE", super.verifySqlText(toVal.trim())) ;

		//	params.setProperty("BUTTON_SEARCH", searchButton) ;
		return params ;
	}

*/
	/**
	Builds the tagvector used for parse one record.
	*/
/*	protected Vector buildTags() {

		// Lets build our tags vector.
		Vector tagsV = new Vector() ;
		tagsV.add("#NEW_DISC_FLAG#") ;
		tagsV.add("#DISC_ID#") ;
		tagsV.add("#A_DATE#") ;
		tagsV.add("#HEADLINE#") ;
		tagsV.add("#C_REPLIES#") ;
		tagsV.add("#FIRST_NAME#") ;
		tagsV.add("#LAST_NAME#") ;
		tagsV.add("#LAST_UPDATED#") ;    // The discussion_update date
		tagsV.add("#REPLY_URL#") ;
		return tagsV ;
	} // End of buildstags
*/

	/**
	show the tag and the according data
	**/
/*	protected void showIt(Vector tags, Vector data) {

		log("***********") ;
		if(tags.size() != data.size()) {
			log("Antalet stämmer inte ") ;
			log("Tags: " + tags.size()) ;
			log("Data: " + data.size()) ;
			// return ;
		}

		for (int i = 0 ; i < tags.size() ; i++) {
			String aTag = ( String) tags.elementAt(i) ;
			String aData = ( String) data.elementAt(i) ;
			log("" + i + ": " + aTag +" --> " + aData) ;

		}


	} // End of showit

*/
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











