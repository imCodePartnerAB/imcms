import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import imcode.external.diverse.* ;
import java.rmi.* ;
import java.rmi.registry.* ;
import java.net.InetAddress;
import imcode.util.* ;
import imcode.server.HTMLConv;

public class BillBoardAdd extends BillBoard
{//ConfAdd

	String HTML_TEMPLATE ;
	String SERVLET_NAME ;  // The name on this servlet

	public void doPost(HttpServletRequest req, HttpServletResponse res)
	throws ServletException, IOException
	{
		//log("START BillBoardAdd doPost");

		// Lets validate the session, e.g has the user logged in to Janus?
		if (super.checkSession(req,res) == false)	return ;

		// Lets get all parameters for this servlet
		Properties params = this.getParameters(req) ;
		if (super.checkParameters(req, res, params) == false)
		{
			String header = SERVLET_NAME + " servlet. " ;
			String msg = params.toString() ;
			BillBoardError err = new BillBoardError(req,res,header,1) ;
			return ;
		}

		// Lets get the user object
		imcode.server.User user = super.getUserObj(req,res) ;
		if(user == null) return ;

		if ( !isUserAuthorized( req, res, user ) )
		{
			return;
		}

		// Lets get the userinformation
		Properties userParams = getUserParameters(user) ;

		// Lets detect which addtype we have
		String addType = "" ;
		addType = req.getParameter("ADDTYPE") ;
		

		// Lets get serverinformation
		String host = req.getHeader("Host") ;
		String imcServer = Utility.getDomainPref("userserver",host) ;
		String confPoolServer = Utility.getDomainPref("billboard_server",host) ;//conference_server

		int metaId = Integer.parseInt( params.getProperty("META_ID") );
		if ( userHasRightToEdit( imcServer, metaId, user ) )
		{

			// ********* CANCEL ********
			if( req.getParameter("CANCEL") != null || req.getParameter("CANCEL.x") != null )
			{
				// if(req.getParameter("CANCEL") != null ) {
				// Lets redirect to the servlet which holds in us.
				res.sendRedirect(MetaInfo.getServletPath(req)  + "BillBoardDiscView") ;//ConfDiscView
				return ;
			}

			// ********* ADD DISCUSSION ********
			if(addType.equalsIgnoreCase("DISCUSSION") && ( req.getParameter("ADD") != null || req.getParameter("ADD.x") != null ) )
			{
				//log("Nu är vi i AddDiscussion") ;

				// Lets add a new discussion to the database
				String aSectionId = params.getProperty("SECTION_ID") ;
				//	String userId = userParams.getProperty("USER_ID") ;
				RmiConf rmi = new RmiConf(user) ;
				String userId = "" ;
				HttpSession session = req.getSession(false) ;
				if (session != null)
				{
					userId = (String) session.getValue("BillBoard.user_id") ;//Conference.user_id
				}
				
				//String addHeader = super.verifySqlText(params.getProperty("ADD_HEADER")) ;
				String addHeader = (params.getProperty("ADD_HEADER")).trim();
				String addText = (params.getProperty("ADD_TEXT")).trim();
				String addEpost =(params.getProperty("ADD_EPOST")).trim();
				if (addHeader.equals("")||addText.equals("") ||addEpost.equals(""))
				{
					//BillBoardError(HttpServletRequest req, HttpServletResponse res, String header, int errorCode)
					log("some fields was empty");	
					BillBoardError err = new BillBoardError(req,res,51);
					return;	
				}
				
				addText = super.verifySqlText(textMailLinkFix(addText));
				addHeader = super.verifySqlText(HTMLConv.toHTMLSpecial(addHeader));
				//addText = super.verifySqlText(HTMLConv.toHTMLSpecial(addText));
				addEpost = super.verifySqlText(HTMLConv.toHTMLSpecial(addEpost));
				
				if (!validateEmail( addEpost ))
				{
					log("invalid epostadress");
					String header = SERVLET_NAME + " servlet. " ;
					BillBoardError err = new BillBoardError(req,res,header,76) ;
					return ;			
				}
				
				// Lets check the data size
				if(addText.length() > 32000)
				{
					String header = SERVLET_NAME + " servlet. " ;
					String msg = params.toString() ;
					BillBoardError err = new BillBoardError(req,res,header,75) ;
					return ;
				}

				// Ok, Lets add the discussion to DB  
				String sqlQuest = "B_AddNewBill " + aSectionId + ", " + userId + ", " ;
				sqlQuest += sqlPDelim(addHeader);
				sqlQuest += sqlP(addText)+", ";
				sqlQuest +=	sqlPDelim(addEpost)+req.getRemoteAddr();//AddNewDisc
				//log("B_AddNewBillSQL: " + sqlQuest) ;
				rmi.execSqlUpdateProcedure(confPoolServer, sqlQuest) ;

				// Lets add the new discussion id to the session object
				// Ok, Lets get the last discussion in that section
				// HttpSession session = req.getSession(false) ;
				if (session != null)
				{
					String latestDiscId = rmi.execSqlProcedureStr(confPoolServer, "B_GetLastDiscussionId " +
						params.getProperty("META_ID") + ", " + aSectionId) ;
					session.putValue("BillBoard.disc_id", latestDiscId) ;//Conference.disc_id
					//	userId = (String) session.getValue("Conference.user_id") ;
				}

				// Lets redirect to the servlet which holds in us.
				res.sendRedirect(MetaInfo.getServletPath(req)  + "BillBoardDiscView") ;//ConfDiscView
				return ;
			}

			// ********* ADD REPLY ********
			// This is a workaround to fix the possibility to use gifs OR submit buttons

			if(addType.equalsIgnoreCase("REPLY") && ( req.getParameter("ADD") != null || req.getParameter("ADD.x") != null ) )
			{
				// if(req.getParameter("ADD") != null && addType.equalsIgnoreCase("REPLY")) {
				//log("Nu är vi i AddReply") ;

				RmiConf rmi = new RmiConf(user) ;
				
				// Lets add a new Reply to the database
				String discId = params.getProperty("DISC_ID") ;
				String userId = "" ;
				HttpSession session = req.getSession(false) ;
				if (session != null)
				{
					userId = (String) session.getValue("BillBoard.user_id") ;//Conference.user_id
				}

				
				// Lets verify the textfields
				//String addHeader = super.verifySqlText(params.getProperty("ADD_HEADER")) ;
				//String addText = super.verifySqlText(params.getProperty("ADD_TEXT")) ;
				String addHeader = (params.getProperty("ADD_HEADER")).trim();
				String addText = (params.getProperty("ADD_TEXT")).trim();
				String addEpost =(params.getProperty("ADD_EPOST")).trim();
				if (addHeader.equals("")||addText.equals("") ||addEpost.equals(""))
				{
					//BillBoardError(HttpServletRequest req, HttpServletResponse res, String header, int errorCode)	
					BillBoardError err = new BillBoardError(req,res,51);
					return;	
				}

				addHeader = super.verifySqlText(HTMLConv.toHTMLSpecial(addHeader));
				addText = super.verifySqlText(HTMLConv.toHTMLSpecial(addText));
				addEpost = super.verifySqlText(HTMLConv.toHTMLSpecial(addEpost));
				
				// Lets check the data size
				if(addText.length() > 32000)
				{
					String header = SERVLET_NAME + " servlet. " ;
					String msg = params.toString() ;
					BillBoardError err = new BillBoardError(req,res,header,74) ;
					return ;
				}
				
				//ok now we have to send the mail to right email adr that we vill get from the db
				String toEmail = rmi.execSqlProcedureStr(confPoolServer, "B_GetEmail " + discId);
				if (toEmail== null)
				{
					log("OBS! No fn email found!");
					return;
				}							
				String sqlQuest = "B_GetSubjectStr " + discId+", "+params.getProperty("META_ID")+", "+params.getProperty("SECTION_ID");
				String subjectStr = rmi.execSqlProcedureStr(confPoolServer, sqlQuest);				
				this.sendReplieEmail(req,res,toEmail,addEpost,subjectStr,addText);

				// Ok, Lets add the discussion to DB  
				sqlQuest = "B_AddReply " + discId + ", " + userId + ", " ;
				sqlQuest += sqlPDelim(addHeader);
				sqlQuest += sqlP(addText)+", ";
				sqlQuest +=	sqlPDelim(addEpost)+req.getRemoteAddr();//AddNewDisc
				//log("B_AddNewBillSQL: " + sqlQuest) ;
				rmi.execSqlUpdateProcedure(confPoolServer, sqlQuest) ;

				// Lets redirect to the servlet which holds in us.
				res.sendRedirect(MetaInfo.getServletPath(req)  + "BillBoardDiscView") ;//ConfDiscView
				//	log("AddReply är klar") ;
				return ;
			}
		}
		else
		{
			String header = SERVLET_NAME + " servlet. " ;
			BillBoardError err = new BillBoardError( req, res ,header , 100 ) ;
			return ;
		}

	} // DoPost

	/**
	DoGet
	*/

	public void doGet(HttpServletRequest req, HttpServletResponse res)
	throws ServletException, IOException
	{
		//log("START BillBoardAdd doGet");

		// Lets validate the session, e.g has the user logged in to Janus?
		if (super.checkSession(req,res) == false)	return ;

		// Lets get all parameters for this servlet
		Properties params = this.getParameters(req) ;
		if (super.checkParameters(req, res, params) == false)
		{
			String header = SERVLET_NAME + " servlet. " ;
			String msg = params.toString() ;
			BillBoardError err = new BillBoardError(req,res,header,1) ;
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
		String confPoolServer = Utility.getDomainPref("billboard_server",host) ;//conference_server

		int metaId = Integer.parseInt( params.getProperty("META_ID") );
		if ( userHasRightToEdit( imcServer, metaId, user ) )
		{
			// Lets Get the session user id
			// Ok, Lets get the last discussion in that forum
			String loginUserId = "" ;
			HttpSession session = req.getSession(false) ;
			if (session != null)
			{
				loginUserId = (String) session.getValue("BillBoard.user_id") ;//Conference.user_id
			}
			//log("INLOGGAD ANVÄNDARES ID: " + loginUserId) ;

			// Lets get a VariableManager
			VariableManager vm = new VariableManager() ;

			// Lets get the userinformation and put the username in the variable manager
			Properties userParams = getUserParameters(user) ;

			// Lets get the users first and last names
			RmiConf rmi = new RmiConf(user) ;
			//String sqlName = "GetBillBoardLoginNames " + params.getProperty("META_ID") ;//GetConfLoginNames
			//sqlName += ", " + loginUserId + ", " +  1 ;
			//String firstName = (String) rmi.execSqlProcedureStr(confPoolServer, sqlName ) ;
			//sqlName = "GetBillBoardLoginNames " + params.getProperty("META_ID") ;
			//sqlName += ", " + loginUserId + ", " +  2 ;
			//String lastName = (String) rmi.execSqlProcedureStr(confPoolServer, sqlName ) ;

			// 	log("Användare: " + firstName + " " + lastName) ;

			//vm.addProperty("FIRST_NAME", firstName ) ;
			//vm.addProperty("LAST_NAME",	lastName ) ;
			vm.addProperty("ADD_TYPE", params.getProperty("ADD_TYPE")) ;

			// Lets add the current forum name
			String currSection = "" + rmi.execSqlProcedureStr(confPoolServer, "B_GetSectionName " + params.getProperty("SECTION_ID")) ;//GetForumName, FORUM_ID
			vm.addProperty("CURRENT_SECTION_NAME", currSection) ;//CURRENT_FORUM_NAME

			// Lets get the addtype and add it to the page
			String addTypeHeader = "" ;
			if(params.getProperty("ADD_TYPE").equalsIgnoreCase("REPLY"))
			{
				//BillBoardError err = new BillBoardError() ;
				//addTypeHeader = err.getErrorMessage(req, 72) ;
				//OBS HÅRD KODAR MÅSTE ÄNDRAS
				addTypeHeader = "SKICKA SVAR";
			}
			else
			{
				//BillBoardError err = new BillBoardError() ;
				//addTypeHeader = err.getErrorMessage(req, 73) ;
				//OBS HÅRD KODAR MÅSTE ÄNDRAS
				addTypeHeader = "SKAPA NYTT ANSLAG";
			}

			vm.addProperty("ADD_TYPE_HEADER", addTypeHeader) ;

			// If addtype is reply, then lets get the header for the discussion
			// from the db and suggest it to the user
			//String discHeader = "" ;
			//if( params.getProperty("ADD_TYPE").equalsIgnoreCase("REPLY") ) {
			//	String aDiscId = params.getProperty("DISC_ID") ;
			//	String sqlQ = "GetDiscussionHeader " + aDiscId ;
			//	String arr[] = rmi.execSqlProcedure(confPoolServer, sqlQ) ;
			//	if( arr != null) {
			//		if(arr.length > 0) {
			//			discHeader = (String) arr[0] ;
			//		}
			//	}
			//}
			//vm.addProperty("DISC_HEADER", discHeader) ;
			this.sendHtml(req,res,vm, HTML_TEMPLATE) ;
			//	log("ConfAdd OK") ;
			return ;
		}
		else
		{
			String header = SERVLET_NAME + " servlet. " ;
			BillBoardError err = new BillBoardError( req, res ,header , 100 ) ;
			return ;
		}

	} //DoGet

	/**
	Collects all the parameters used by this servlet
	**/

	public Properties getParameters( HttpServletRequest req)
	throws ServletException, IOException
	{

		// Lets get the standard SESSION metainformation
		Properties params = super.getSessionParameters(req) ;

		// Lets get the EXTENDED SESSION PARAMETERS
		super.getExtSessionParameters(req, params) ;

		// Lets get our REQUESTPARAMETERS
		String addType = (req.getParameter("ADDTYPE")==null) ? " " : (req.getParameter("ADDTYPE")) ;
		String addHeader = (req.getParameter("ADDHEADER")==null) ? " " : (req.getParameter("ADDHEADER")) ;
		String addText = (req.getParameter("ADDTEXT")==null) ? " " : (req.getParameter("ADDTEXT")) ;
		String addEpost = (req.getParameter("ADDEPOST")==null) ? " " : (req.getParameter("ADDEPOST"));
		
		// Alright, these parameters are userdefined text, and if the user hasnt filled something in them
		// then the checkparamters will warn for this. The thing is that we dont care if the
		// user passes a text or not, so lets look if the variable is empty, and if it is
		// just put " " in it!
		//Not yet anyway but soon we vill care!!!
		//log("ADD_EPOST:"+ addEpost +" ADD_HEADER:"+ addHeader +" ADD_TEXT:"+ addText+" ADD_TYPE:"+ addType);
		if(addText.equals("")) addText= " ";
		if(addHeader.equals("")) addHeader = " ";
		if(addEpost.equals("")) addEpost = " ";
		if(addType.equals("")) addType = " ";

		params.setProperty("ADD_EPOST", addEpost);
		params.setProperty("ADD_HEADER", addHeader) ;
		params.setProperty("ADD_TEXT", addText) ;
		params.setProperty("ADD_TYPE", addType) ;

		return params ;
	}
	
	/**
	*Sends a replie mail
	**/
	//##########//this.sendReplieEmail(req,res,toEmail,addEpost,addHeader,addText);
	public void sendReplieEmail( HttpServletRequest req,
								 HttpServletResponse res,
								 String toEmail,
								 String fromEmail,
								 String header,
								 String text ) 
	throws ServletException, IOException
	{

		String emptyString = "";

		/* server info */
		String host = req.getHeader("Host") ;
		String imcserver = Utility.getDomainPref( "adminserver", host );//?????????
		String hostName = emptyString;
		try {
			hostName = InetAddress.getLocalHost().getHostName();
			} catch ( SecurityException e ) {
			log( "checkConnect doesn't allow the operation" );
			}
		
		/* mailserver info */
		String mailserver = Utility.getDomainPref( "smtp_server", host );
		String eMailServerMaster = Utility.getDomainPref( "servermaster_email", host );
		String emailFromServer = Utility.getDomainPref( "system_email", host );;
		String mailFrom = eMailServerMaster;
		String deafultLanguagePrefix = IMCServiceRMI.getLanguage( imcserver );
		String stringMailPort = Utility.getDomainPref( "smtp_port", host );
		String stringMailtimeout = Utility.getDomainPref( "smtp_timeout", host );

		// Handling of default-values is another area where java can't hold a candle to perl.
		int mailport = 25 ;
		try
		{
			mailport = Integer.parseInt( stringMailPort );
		} catch (NumberFormatException ignored)
		{
			// Do nothing, let mailport stay at default.
		}

		int mailtimeout = 10000 ;
		try
		{
			mailtimeout = Integer.parseInt( stringMailtimeout );
		} catch (NumberFormatException ignored)
		{
			// Do nothing, let mailtimeout stay at default.
		}

		
		Vector errorParsVector = new Vector();
		


		/* send mail */
		//			try {
		SMTP smtp = new SMTP( mailserver, mailport, mailtimeout );					

			smtp.sendMailWait( fromEmail, toEmail ,header , text );
			
			//smtp.sendMailWait (String from, String to, String subject, String msg)

				
	}


	/* checks if email address is valid (abc@x)*/
	private boolean validateEmail( String eMail )
	{
		int stringLength = eMail.length();
		int indexAt = eMail.indexOf( "@" );

		if ( indexAt > 0 && indexAt < (stringLength - 1) )
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	/*
	*Takes a string and takes out substrings marked #MAIL# to #/MAIL# or #LINK# to #/LINK#
	*and convert everything else to html-safe code, Fore the substrings it creates 
	*proper html-code 
	*/
	private String textMailLinkFix(String text)
	{
		//super.verifySqlText(HTMLConv.toHTMLSpecial(addText));
		boolean done = false;
		
		StringBuffer sendStr = new StringBuffer();
		String hackStr = text;
		
		while (!done)
		{
			boolean linkB = false;
			boolean mailB = false;
			String tempStr="";
			int textLength = hackStr.length();//???
			int lStart =hackStr.indexOf("#LINK#");
			//log("lStart = "+lStart);
			int lEnd = hackStr.indexOf("#/LINK#");
			//log("lEnd = "+lEnd);
			int mStart = hackStr.indexOf("#MAIL#");
			//log("mStart = "+mStart);
			int mEnd = hackStr.indexOf("#/MAIL#");
			//log("mEnd ="+ mEnd);
			//ok lets do some if cases to figure out what to do
			if (lStart != -1 && lEnd != -1)
			{
				linkB = true;	
			}
			if (mStart != -1 && mEnd != -1)
			{
				mailB = true;
			}
			if (linkB && mailB)//ok we have at least one of each
			{
				if (lStart < mStart)//lets do link stuff //substring(int beginIndex, int endIndex)
				{
					//ok lets convert everything before the first #LINK# mark
					if(lStart > 0)
					sendStr.append(HTMLConv.toHTMLSpecial(hackStr.substring(0, lStart)));
					
					//ok lets append the link string
					sendStr.append(doLinkStuff(hackStr.substring(lStart+6, lEnd)));
					
					hackStr = hackStr.substring(lEnd+7);
					
				}else //ok lets do mailstuff
				{
					//ok lets convert everything before the first #MAIL# mark
					if(mStart > 0)
					sendStr.append(HTMLConv.toHTMLSpecial(hackStr.substring(0, mStart)));
					
					//ok lets append the mail string
					sendStr.append(doMailStuff(hackStr.substring(mStart+6, mEnd)));
					
					hackStr = hackStr.substring(mEnd+7);				
				}
			}else if(linkB)//ok lets do some linkstuff
			{
				//ok lets convert everything before the first #LINK# mark
				if (lStart > 0)
					sendStr.append(HTMLConv.toHTMLSpecial(hackStr.substring(0, lStart)));										
			
				//ok lets append the link string				
				sendStr.append(doLinkStuff(hackStr.substring(lStart+6, lEnd)));
				
				hackStr = hackStr.substring(lEnd+7);				
			}else if(mailB)	//lets do some mailstuff
			{	//ok lets convert everything before the first #MAIL# mark
				if(mStart > 0)
				sendStr.append(HTMLConv.toHTMLSpecial(hackStr.substring(0, mStart)));
					
				//ok lets append the mail string
				sendStr.append(doMailStuff(hackStr.substring(mStart+6, mEnd)));
					
				hackStr = hackStr.substring(mEnd+7);
			
			}else//ok we are done doing stuff 
			{
				sendStr.append(HTMLConv.toHTMLSpecial(hackStr));
				done = true;	
			}			
			
		}
		return sendStr.toString();		
	}
	
	/*
	*Takes a string and makes an html-link-tag
	*/
	private String doLinkStuff(String str)
	{
		//<A HRef=' + AddStr + DaLink + ' Target=_new>' + DisplayURL + '</A>' + '&nbsp;
		String daLink = "";
		int i = str.indexOf("HTTP://");
		if(i < 0) daLink = "HTTP://";		
		 
		return	" <A HRef=" + daLink + str +" Target=_new>"+str + "</A> ";
	}
	
	/*
	*Takes a string and makes an html-mailto-tag
	*/
	private String doMailStuff(String str)
	{
		//<A HRef="mailto:' + MailLink + '"' + 'Target=_new>' +' ' + MailLink + '</A>' + '&nbsp
		return "<A HRef=\"mailto:" + str + "\" Target=_new> " +str + "</A> ";
	}
	
	
	/**
	Init
	*/

	public void init(ServletConfig config)
	throws ServletException
	{
		super.init(config);
		HTML_TEMPLATE = "BillBoard_Add.htm" ;//Conf_Add.htm
		SERVLET_NAME = "BillBoardAdd" ;//ConfAdd
		/*
		HTML_TEMPLATE = getInitParameter("html_template") ;
		SERVLET_NAME = "ConfAdd" ;

		if( HTML_TEMPLATE == null) {
		    Enumeration initParams = getInitParameterNames();
		    System.err.println(SERVLET_NAME + " The init parameters were: ");
		    while (initParams.hasMoreElements()) {
		System.err.println(initParams.nextElement());
		    }
		    System.err.println(SERVLET_NAME + " Should have seen one parameter name");
		    throw new UnavailableException (this,
		SERVLET_NAME + " Should have seen one parameter name");
		}

		  this.log("HtmlTemplate:" + getInitParameter("html_template")) ;
		*/
	}

	/**
	Log function, will work for both servletexec and Apache
	**/

	public void log( String str)
	{
		super.log(str) ;
		System.out.println(SERVLET_NAME + " " + str ) ;
	}

} // End of class
