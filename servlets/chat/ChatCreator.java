import java.io.*;
import java.util.*;
import java.lang.*;
import javax.servlet.*;
import javax.servlet.http.*;
import imcode.external.diverse.*;
import imcode.external.chat.*;
import imcode.util.* ;

public class ChatCreator extends ChatBase
{
	String HTML_TEMPLATE ;

	/**
	The POST method creates the html page when this side has been
	redirected from somewhere else.
	**/

	public void doPost(HttpServletRequest req, HttpServletResponse res)
	throws ServletException, IOException
	{

		// Lets validate the session, e.g has the user logged in to Janus?
		if (super.checkSession(req,res) == false)	return ;
		HttpSession session = req.getSession(true);


		// Lets get the standard parameters and validate them
		Properties params = super.getSessionParameters(req) ;
		if (super.checkParameters(req, res, params) == false) return ;

		// Lets get the new chat parameters
		Properties chatParams = this.getNewChatParameters(req) ;
		if (super.checkParameters(req, res, chatParams) == false) return ;

		// Lets get an user object
		imcode.server.User user = super.getUserObj(req,res) ;
		if(user == null) return ;
		if ( !isUserAuthorized( req, res, user ) ) return;
		
		// Lets get serverinformation
		String host = req.getHeader("Host") ;
		String imcServer = Utility.getDomainPref("userserver",host) ;
		String chatPoolServer = Utility.getDomainPref("chat_server",host) ;
		
		String action=chatParams.getProperty("action");
	
		if(action == null)
		{
			action = "" ;
			String header = "ChatCreator servlet. " ;
			ChatError err = new ChatError(req,res,header,3) ;
			log(header + err.getErrorMsg()) ;
			return ;
		}
		
		//get the msgTypes
		RmiConf rmi = new RmiConf(user) ;
		String[] msgTypes = rmi.execSqlProcedure(chatPoolServer, "GetMsgTypes");
		String[] msgTypesId = rmi.execSqlProcedure(chatPoolServer, "GetMsgTypesId");
		Vector msgTypeV = new Vector();
	
		for(int i =0;i<msgTypes.length;i++)
		{
			log("Stringmsgtype: " + msgTypes[i] );
			msgTypeV.add(msgTypesId[i]);
			msgTypeV.add(msgTypes[i]);
		}

		//get the authorization types with "oregistrerad" default
		String[] autTypes = rmi.execSqlProcedure(chatPoolServer, "GetAuthorizationTypes");
		Vector autTypeV = new Vector();
		for(int i =0;i<autTypes.length;i++)
		{
			log("StringAuttype: " + autTypes[i] );
			autTypeV.add(" ");
			autTypeV.add(autTypes[i]);
		}
		
		//get existing rooms
		Vector roomsV = ( (Vector)session.getValue("roomList")==null ) ? new Vector() : (Vector)session.getValue("roomList");
		
		Vector newMsgTypeV = ( (Vector)session.getValue("newMsgTypes")==null ) ? new Vector() : (Vector)session.getValue("newMsgTypes");
		
		
		
		//****************If newRoom or newMsgTypebutton is pressed:*********************************
		
		if ( req.getParameter("addRoom") != null || req.getParameter("addMsgType") != null)
		{
			
			VariableManager vm = new VariableManager() ;
			Html htm = new  Html();
			
			//get new parameters
			chatParams.setProperty("chatRoom",req.getParameter("chatRoom").trim());
			chatParams.setProperty("msgType",req.getParameter("msgType").trim());
			
			//get all chatparameters
			Enumeration chatEnum = chatParams.propertyNames();
			while (chatEnum.hasMoreElements())
			{
				String paramName = (String)chatEnum.nextElement();
				//log("ParamName: " + paramName);
				
				if ( req.getParameter("addRoom") != null && paramName.equals("chatRoom") )
				{
					//add new room to roomlist
					log("Rum: " + chatParams.getProperty(paramName) );
					
					roomsV.add(" ");
					roomsV.add( chatParams.getProperty(paramName) );
					
					for(int i=0; i<roomsV.size();i++)
					{
						log("vector: " + roomsV.get(i));
					}
					
					//add room to session
					session.putValue("roomList",roomsV);					
					
					vm.addProperty("chatRoom"," ");
				}
				else if ( req.getParameter("addMsgType") != null && paramName.equals("msgType") )
				{
					//add new msgType to msgTypelist
					log("MsgTyp: " + chatParams.getProperty(paramName) );
					
					
					newMsgTypeV.add( chatParams.getProperty(paramName) );
					
					for(int i=0; i<newMsgTypeV.size();i++)
					{
						log("vector: " + msgTypeV.get(i));
						msgTypeV.add(" ");
						msgTypeV.add(newMsgTypeV.get(i));
					}
					
					//add type to session
					session.putValue("newMsgTypes",newMsgTypeV);
				
					vm.addProperty("msgType"," ");
				}
				else
				{
					vm.addProperty(paramName,chatParams.getProperty(paramName));
				}
			}
			
			vm.addProperty("SERVLET_URL", MetaInfo.getServletPath(req)) ;
			vm.addProperty("roomList", htm.createHtmlCode("ID_OPTION","", roomsV) ) ;
			vm.addProperty("msgTypes", htm.createHtmlCode("ID_OPTION","", msgTypeV) ) ;
			Vector selV = new Vector();
			selV.add("1");selV.add("oregistrerad");
			vm.addProperty("authorized", htm.createHtmlCode("ID_OPTION",selV, autTypeV) ) ;
			sendHtml(req,res,vm, HTML_TEMPLATE) ;
			return ;

		}
		
		
		// ********* NEW ********
		if(action.equalsIgnoreCase("ADD_CHAT"))
		{
			log("OK, nu skapar vi Chatten") ;

			// Added 000608
			// Ok, Since the chat db can be used from different servers
			// we have to check when we add a new chat that such an meta_id
			// doesnt already exists.
		
			String metaId = params.getProperty("META_ID") ;
			log("metaid: "+ metaId);
			
			String foundMetaId = rmi.execSqlProcedureStr(chatPoolServer, "MetaIdExists " + metaId) ;
			
			log("Found metaid: " + foundMetaId);
			
			if(!foundMetaId.equals("1"))
			{
				action = "" ;
				String header = "ChatCreator servlet. " ;
				ChatError err = new ChatError(req,res,header,90) ;
				log(header + err.getErrorMsg());
				return ;
			}

			// Lets add a new Chat to DB
			// AddNewChat @meta_id int, @chatName varchar(255)
			String chatName = chatParams.getProperty("chatName");
			String sqlQ = "AddNewChat " + metaId + ", '" + chatName + "'" ;
			log("AddNewChat sql:" + sqlQ ) ;
			rmi.execSqlUpdateProcedure(chatPoolServer, sqlQ) ;
			
			session.putValue("chatName",chatName);
			session.putValue("chatId",metaId);
			
			// Lets add the new rooms to the chat
			for (int i=0;i<roomsV.size();i+=2)
			{
				//Lets get the highest roomId
				String roomId = rmi.execSqlProcedureStr(chatPoolServer, "GetMaxRoomId");
				roomsV.set(i,roomId);

				String newRsql = "AddNewRoom " +  " '" +roomId + "', " + roomsV.get(i+1);//chatParams.getProperty("roomList");
				log("AddNewRoom sql:" + newRsql ) ;
				rmi.execSqlUpdateProcedure(chatPoolServer, newRsql) ;
		
				//add room to connection db
				rmi.execSqlUpdateProcedure(chatPoolServer,"AddNewChatRoom " + " '"+ metaId + "' , '" + roomId + "' ") ;
				
				log("RoomsV: " + roomsV.get(i+1) +" RoomId: " + roomsV.get(i));
				
			}
			
			session.putValue("roomList",roomsV);
		
		/*	// Lets get the administrators user_id
			String user_id = user.getString("user_id") ;

			// Lets get the recently added forums id
			String forum_id = rmi.execSqlProcedureStr(chatPoolServer, "GetFirstForum " + metaId) ;

			// Lets add this user into the conference if hes not exists there before were
			// adding the discussion
			String confUsersAddSql = "ChatUsersAdd "+ user_id +", "+ metaId +", '"+ user.getString("first_name") + "', '";
			confUsersAddSql += user.getString("last_name") + "'";
			rmi.execSqlUpdateProcedure(chatPoolServer, confUsersAddSql) ;

		*/
			// Lets add the new msgTypes to the chat
			for (int i=0;i<newMsgTypeV.size();i++)
			{
				//save newMsgTypes to db
				
				//Lets get the highest msgId
				String msgTypeId = rmi.execSqlProcedureStr(chatPoolServer, "GetMaxMsgTypeId");
				
				String newMsql = "AddNewMsgType " +  " '" + msgTypeId + "', " + newMsgTypeV.get(i);
				log("AddNewMsgType sql:" + newMsql ) ;
				rmi.execSqlUpdateProcedure(chatPoolServer, newMsql) ;
				
				
				//add newTypes to msgTypesV
				msgTypeV.add( msgTypeId );
				msgTypeV.add( newMsgTypeV.get(i) );

				//add msgType to connection db
//FIXIT	rmi.execSqlUpdateProcedure(chatPoolServer,"AddNewRoomMsg " + " '"+ msgTypeId + "' , '" + metaId + "' ") ;
				
			//	log("newMsgV: " + newMsgTypeV.get(i));// +" RoomId: " + roomsV.get(i));
				
			}
			
			for (int i=0;i<msgTypeV.size();i++)
			{
				log("msgType: " + msgTypeV.get(i) );
			}
			
			session.putValue("msgTypes",msgTypeV);
			
		//	Chat theChat = new Chat((Integer.valueOf(metaId)).intValue(),chatName,roomsV);
		//	theChat._name=chatName;
			
		//	ServletContext context = getServletContext() ;
		//	context.setAttribute(metaId,theChat);
			
			// Ok, were done creating the conference. Lets tell Janus system to show this child.
			rmi.activateChild(imcServer, metaId) ;

			// Ok, Were done adding the conference, Lets go back to the Manager
			String loginPage = MetaInfo.getServletPath(req) + "ChatLogin?login_type=login" ;
			res.sendRedirect(loginPage) ;

			return ;
		
		}

	} // End POST


	/**
	The GET method creates the html page when this side has been
	redirected from somewhere else.
	**/

	public void doGet(HttpServletRequest req, HttpServletResponse res)
	throws ServletException, IOException
	{

		// Lets validate the session, e.g has the user logged in to Janus?
		if (super.checkSession(req,res) == false)	return ;

		// Lets get the standard parameters and validate them
		Properties params = super.getSessionParameters(req) ;
		if (super.checkParameters(req, res, params) == false) return ;

		// Lets get an user object
		imcode.server.User user = super.getUserObj(req,res) ;
		if(user == null) return ;

		if ( !isUserAuthorized( req, res, user ) )
		{
			return;
		}
		
		// Lets get serverinformation
		String host = req.getHeader("Host") ;
		String imcServer = Utility.getDomainPref("userserver",host) ;
		String chatPoolServer = Utility.getDomainPref("chat_server",host) ;

		String action = req.getParameter("action") ;
		if(action == null)
		{
			action = "" ;
			String header = "ChatCreator servlet. " ;
			ChatError err = new ChatError(req,res,header,3) ;
			log(header + err.getErrorMsg()) ;
			return ;
		}
		
		//get the msgTypes
		RmiConf rmi = new RmiConf(user) ;
		String[] msgTypes = rmi.execSqlProcedure(chatPoolServer, "GetMsgTypes");
		Vector msgTypeV = new Vector();
		for(int i =0;i<msgTypes.length;i++)
		{
			log("Stringmsgtype: " + msgTypes[i] );
			msgTypeV.add(" ");
			msgTypeV.add(msgTypes[i]);
		}

		//get the authorization types with "oregistrerad" default
		String[] autTypes = rmi.execSqlProcedure(chatPoolServer, "GetAuthorizationTypes");
		Vector autTypeV = new Vector();
		for(int i =0;i<autTypes.length;i++)
		{
			log("StringAuttype: " + autTypes[i] );
			autTypeV.add(" ");
			autTypeV.add(autTypes[i]);
		}
		
	
		Vector selV = new Vector();
		selV.add("1");selV.add("oregistrerad");

		// ********* NEW ********
		if(action.equalsIgnoreCase("NEW"))
		{
			// Lets build the Responsepage to the loginpage
			
			Html htm = new Html();
			VariableManager vm = new VariableManager() ;
			vm.addProperty("SERVLET_URL", MetaInfo.getServletPath(req)) ;
			vm.addProperty("msgTypes", htm.createHtmlCode("ID_OPTION","säger till", msgTypeV) ) ;
			vm.addProperty("authorized", htm.createHtmlCode("ID_OPTION",selV, autTypeV) ) ;
			sendHtml(req,res,vm, HTML_TEMPLATE) ;
			return ;
		}
	} // End doGet


	/**
	Collects the parameters from the request object
	**/

	protected Properties getNewChatParameters( HttpServletRequest req) throws ServletException, IOException
	{
		//log("Parameter Names: "+req.getParameterNames());
		
		Properties chatP = new Properties();
		
		String action = (req.getParameter("action")==null) ? "" : (req.getParameter("action"));		
		String chatName = (req.getParameter("chatName")==null) ? "" : (req.getParameter("chatName"));
		
	//	String chatRoom = (req.getParameter("chatRoom") == null )? "" : (req.getParameter("chatRoom"));
	String roomList= (req.getParameter("roomList")==null) ? "" : (req.getParameter("roomList"));
		
	//	String msgType= (req.getParameter("msgType")==null) ? "" : (req.getParameter("msgType"));
	String msgList = (req.getParameter("messageTypes")==null ) ? "" :(req.getParameter("messageTypes"));
		
//	String authorized = (req.getParameter("authorized")==null ) ? "3":(req.getParameter("authorized"));
		String template = (req.getParameter("template")==null ) ? "ORIGINAL" : (req.getParameter("template"));
		String updateTime = (req.getParameter("updateTime")==null ) ? "" :(req.getParameter("updateTime"));
		String reload = (req.getParameter("reload")==null ) ? "" :(req.getParameter("reload"));
		String inOut = (req.getParameter("inOut")==null ) ? "" :(req.getParameter("inOut"));
		String privat = (req.getParameter("private")==null ) ? "" :(req.getParameter("private"));
		String publik = (req.getParameter("public")==null ) ? "" :(req.getParameter("public"));
		String dateTime = (req.getParameter("dateTime")==null ) ? "" :(req.getParameter("dateTime"));
		String font = (req.getParameter("font")==null ) ? "" :(req.getParameter("font"));

//template=,  authorized=, 
		chatP.setProperty("action",action.trim());
		chatP.setProperty("chatName", chatName.trim());
	//	chatP.setProperty("chatRoom",chatRoom.trim());
	//	chatP.setProperty("msgType",msgType.trim());
//	chatP.setProperty("authorized",authorized.trim());
		chatP.setProperty("template",template.trim());
		chatP.setProperty("updateTime",updateTime.trim());
		chatP.setProperty("reload",reload.trim());
		chatP.setProperty("inOut",inOut.trim());
		chatP.setProperty("privat",privat.trim());
		chatP.setProperty("publik",publik.trim());
		chatP.setProperty("dateTime",dateTime.trim());
		chatP.setProperty("font",font.trim());

	
//	chatP.setProperty("roomList", roomList.trim());
	
//	this.log("Chat paramters:" + confP.toString()) ;
		return chatP ;
	}

	/**
	Detects paths and filenames.
	*/

	public void init(ServletConfig config) throws ServletException
	{
		super.init(config);
		HTML_TEMPLATE = "createChat.HTM" ;

		// log("Nu init vi") ;
		/*
		HTML_TEMPLATE = getInitParameter("html_template") ;

		if( HTML_TEMPLATE == null ) {
		Enumeration initParams = getInitParameterNames();
		System.err.println("ChatCreator: The init parameters were: ");
		while (initParams.hasMoreElements()) {
		System.err.println(initParams.nextElement());
		}
		System.err.println("ChatCreator: Should have seen one parameter name");
		throw new UnavailableException (this,
		"Not given a path to the asp diagram files");
		}

		log("HTML_TEMPLATE:" + HTML_TEMPLATE ) ;
		*/
	} // End of INIT

	/**
	Log function, will work for both servletexec and Apache
	**/

	public void log( String str)
	{
		super.log(str) ;
		System.out.println("ChatCreator: " + str ) ;
	}


} // End class
