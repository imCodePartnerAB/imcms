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

		// Lets validate the session and get the session
		if (super.checkSession(req,res) == false)	return ;
		HttpSession session = req.getSession(false);


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
		RmiConf rmi = new RmiConf(user) ;
		
		String action=req.getParameter("action");
	
		if(action == null)
		{
			action = "" ;
			String header = "ChatCreator servlet. " ;
			ChatError err = new ChatError(req,res,header,3) ;
			log(header + err.getErrorMsg()) ;
			return ;
		}
		
		//******************************** Get New Chatparameters********************************
		
		//get the msgTypes
		Vector msgTypesV = new Vector();
		
		if( (Vector)session.getValue("msgTypesV")==null )
		{
			String[] msgTypes = rmi.execSqlProcedure(chatPoolServer, "GetMsgTypes");
			String[] msgTypesId = rmi.execSqlProcedure(chatPoolServer, "GetMsgTypesId");
		
			for(int i =0;i<msgTypes.length;i++)
			{
				log("Stringmsgtype: " + msgTypes[i] );
				msgTypesV.add(msgTypesId[i]);
				msgTypesV.add(msgTypes[i]);
			}
			
			session.putValue("msgTypesV",msgTypesV);
			
		}
	
		msgTypesV = (Vector)session.getValue("msgTypesV");
		
		
		

		//get the authorization types 
		Vector autTypeV = new Vector();
		
		if( (Vector)session.getValue("autTypesV")==null )
		{
			String[] autTypes = rmi.execSqlProcedure(chatPoolServer, "GetAuthorizationTypes");
			String[] msgTypesId = {"1","2","3"};
		
			for(int i =0;i<autTypes.length;i++)
			{
				log("Stringauttype: " + autTypes[i] );
				autTypeV.add("i");
				autTypeV.add(autTypes[i]);
			}
			
			session.putValue("autTypesV",autTypeV);
			
		}
		
		autTypeV = (Vector)session.getValue("autTypesV");
		
		
		//get existing rooms
		Vector roomsV = ( (Vector)session.getValue("roomList")==null ) ? new Vector() : (Vector)session.getValue("roomList");
		//get existing msgTypes
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
				
				
				//Add new Room
				if ( req.getParameter("addRoom") != null && paramName.equals("chatRoom") )
				{
					//add new room to roomlist
					log("Rum: " + chatParams.getProperty(paramName) );
					
					roomsV.add(" ");
					roomsV.add( chatParams.getProperty(paramName) );
					
					//add room to session
					session.putValue("roomList",roomsV);					
					
					vm.addProperty("chatRoom"," ");
				}
				//Add new MsgType
				else if ( req.getParameter("addMsgType") != null && paramName.equals("msgType") )
				{
					//add new msgType to msgTypelist
					log("MsgTyp: " + chatParams.getProperty(paramName) );
					
					newMsgTypeV.add( chatParams.getProperty(paramName) );
		////FIX kolla så det verkligen stämmer!
					for(int i=0; i<newMsgTypeV.size();i++)
					{
						log("vector: " + msgTypesV.get(i));
						msgTypesV.add(" ");
						msgTypesV.add(newMsgTypeV.get(i));
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
			vm.addProperty("msgTypes", htm.createHtmlCode("ID_OPTION","", msgTypesV) ) ;
		////FIX selV ska helst bort
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
		
		    //get the new metaId
			String metaId = params.getProperty("META_ID") ;
			log("metaid: "+ metaId);
			
			//check that its really a new metaId
			String foundMetaId = rmi.execSqlProcedureStr(chatPoolServer, "MetaIdExists " + metaId) ;
			
			if(!foundMetaId.equals("1"))
			{
				action = "" ;
				String header = "ChatCreator servlet. " ;
				ChatError err = new ChatError(req,res,header,90) ;
				log(header + err.getErrorMsg());
				return ;
			}

			// Lets add a new Chat to DB
			String chatName = chatParams.getProperty("chatName");
			String sqlQ = "AddNewChat " + metaId + ", '" + chatName + "'" ;
			log("AddNewChat sql:" + sqlQ ) ;
			rmi.execSqlUpdateProcedure(chatPoolServer, sqlQ) ;
			
			// Lets add the new rooms to the db
			for (int i=0;i<roomsV.size();i+=2)
			{
				//Lets get the highest roomId
				String roomId = rmi.execSqlProcedureStr(chatPoolServer, "GetMaxRoomId");
				roomsV.set(i,roomId);

				String newRsql = "AddNewRoom " +  " '" +roomId + "', " + roomsV.get(i+1);
				log("AddNewRoom sql:" + newRsql ) ;
				rmi.execSqlUpdateProcedure(chatPoolServer, newRsql) ;
		
				//add room to connection db
				rmi.execSqlUpdateProcedure(chatPoolServer,"AddNewChatRoom " + " '"+ metaId + "' , '" + roomId + "' ") ;
				
				log("RoomsV: " + roomsV.get(i+1) +" RoomId: " + roomsV.get(i));
				
			}
			
			
			// Lets add the new msgTypes to the db
			for (int i=0;i<newMsgTypeV.size();i++)
			{
				//save newMsgTypes to db
				
				//Lets get the highest msgId
				String msgTypeId = rmi.execSqlProcedureStr(chatPoolServer, "GetMaxMsgTypeId");
				
				String newMsql = "AddNewMsgType " +  " '" + msgTypeId + "', " + newMsgTypeV.get(i);
				log("AddNewMsgType sql:" + newMsql ) ;
				rmi.execSqlUpdateProcedure(chatPoolServer,newMsql);
				
				
				//add newTypes to msgTypesV
				msgTypesV.add( msgTypeId );
				msgTypesV.add( newMsgTypeV.get(i) );

				//add msgType to connection db
//FIXIT	rmi.execSqlUpdateProcedure(chatPoolServer,"AddNewRoomMsg " + " '"+ msgTypeId + "' , '" + metaId + "' ") ;
				
			//	log("newMsgV: " + newMsgTypeV.get(i));// +" RoomId: " + roomsV.get(i));
				
			}
			
			
			// Lets add the new chatParameters to the db
			Properties newChatParams = this.getNewChatParameters(req) ;
			
			Vector valuesV = new Vector();
			String valuesS = "AddChatParams ";
				
			Iterator chatValuesIt = (newChatParams.values()).iterator();
			while( chatValuesIt.hasNext() )
			{
				 valuesS = valuesS + (String)chatValuesIt.next() +",";
			}	
				
			valuesS=valuesS.substring(0,valuesS.length()-1);
			
			log("valuesS: " + valuesS );
			
			rmi.execSqlUpdateProcedure(chatPoolServer,valuesS);
			
			//put the new chat in the session
			//FIX lättare att lägga in chatobjekt?
			session.putValue("chatId",metaId);
			session.putValue("roomList",roomsV);
			session.putValue("msgTypes",msgTypesV);
			session.putValue("chatParams",newChatParams);
			
			// Ok, were done creating the conference. Lets tell imCMS system to show this child.
			rmi.activateChild(imcServer, metaId) ;

			// Ok, Were done adding the chat, Lets log in to it!
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
		HttpSession session = req.getSession(false);
	
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
		RmiConf rmi = new RmiConf(user) ;

		String action = req.getParameter("action") ;
		if(action == null)
		{
			action = "" ;
			String header = "ChatCreator servlet. " ;
			ChatError err = new ChatError(req,res,header,3) ;
			log(header + err.getErrorMsg()) ;
			return ;
		}
		
		
		// ********* Create NEW Chat *********************************************************
		if(action.equalsIgnoreCase("NEW"))
		{
			//******************* Get Chat Standard Parameters ****************
		
			//get the standard msgTypes in the db
			
			String[] msgTypes = rmi.execSqlProcedure(chatPoolServer, "GetMsgTypes");
			String[] msgTypesId = rmi.execSqlProcedure(chatPoolServer, "GetMsgTypesId");
			Vector msgTypesV = new Vector();
	
			for(int i =0;i<msgTypes.length;i++)
			{
				log("Stringmsgtype: " + msgTypes[i] );
				msgTypesV.add(msgTypesId[i]);
				msgTypesV.add(msgTypes[i]);
			}
			
			
			
			session.putValue("msgTypesV",msgTypesV);
			
			//get the authorization types 
			String[] autTypes = rmi.execSqlProcedure(chatPoolServer, "GetAuthorizationTypes");
			Vector autTypeV = new Vector();
			for(int i =0;i<autTypes.length;i++)
			{
				log("StringAuttype: " + autTypes[i] );
				autTypeV.add(" ");
				autTypeV.add(autTypes[i]);
			}
			
			session.putValue("autTypeV",autTypeV);
		
			Vector selV = new Vector();
			selV.add("");selV.add("oregistrerad");

			// Lets build the Responsepage to the loginpage
			Html htm = new Html();
			VariableManager vm = new VariableManager() ;
			
			vm.addProperty("SERVLET_URL", MetaInfo.getServletPath(req)) ;
			vm.addProperty("msgTypes", htm.createHtmlCode("ID_OPTION","säger till", msgTypesV) ) ;
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
		Properties chatP = new Properties();
		
		String chatName = (req.getParameter("chatName")==null) ? "" : (req.getParameter("chatName"));
		String updateTime = (req.getParameter("updateTime")==null ) ? "" :(req.getParameter("updateTime"));		
		String reload = (req.getParameter("reload")==null ) ? "" :(req.getParameter("reload"));
		String inOut = (req.getParameter("inOut")==null ) ? "" :(req.getParameter("inOut"));
		String privat = (req.getParameter("private")==null ) ? "" :(req.getParameter("private"));
		String publik = (req.getParameter("public")==null ) ? "" :(req.getParameter("public"));
		String dateTime = (req.getParameter("dateTime")==null ) ? "" :(req.getParameter("dateTime"));
		String font = (req.getParameter("font")==null ) ? "" :(req.getParameter("font"));

		chatP.setProperty("chatName", chatName.trim());
		chatP.setProperty("updateTime",updateTime.trim());
		chatP.setProperty("reload",reload.trim());
		chatP.setProperty("inOut",inOut.trim());
		chatP.setProperty("privat",privat.trim());
		chatP.setProperty("publik",publik.trim());
		chatP.setProperty("dateTime",dateTime.trim());
		chatP.setProperty("font",font.trim());

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
