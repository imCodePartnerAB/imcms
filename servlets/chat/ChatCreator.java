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
	String HTML_TEMPLATE ,ADMIN_TEMPLATE;

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
		Properties chatParams = super.getNewChatParameters(req) ;
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

	
	//	String chatName = (String)session.getValue("chatName");
	//	if(chatName == null) chatName = "";

		// ********* If a NEW CHAT is created  ********
		if(action.equalsIgnoreCase("ADD_CHAT"))
		{
			log("OK, nu skapar vi Chatten") ;

			//******************************** Get all New Chatparameters********************************

			//get the msgTypes
			Vector msgTypesV = new Vector();

			if( (Vector)session.getValue("msgTypesV")==null )
			{
				String[] msgTypes = rmi.execSqlProcedure(chatPoolServer, "GetBaseMsgTypes");
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

				for(int i =0;i<autTypes.length;i++)
				{
					log("Stringauttype: " + autTypes[i] );
					autTypeV.add(String.valueOf(i+1));
					autTypeV.add(autTypes[i]);
				}

				session.putValue("autTypesV",autTypeV);
			}
			
			//lets get the selected autotypes
			String[] selAuto = (req.getParameterValues("authorized")==null) ? new String[0] : (req.getParameterValues("authorized"));
			Vector selAutoV = super.convert2Vector(selAuto);

			autTypeV = (Vector)session.getValue("autTypesV");

			//get existing rooms
			Vector roomsV = ( (Vector)session.getValue("roomList")==null ) ? new Vector() : (Vector)session.getValue("roomList");

			//get existing new msgTypes
			Vector newMsgTypeV = ( (Vector)session.getValue("newMsgTypes")==null ) ? new Vector() : (Vector)session.getValue("newMsgTypes");


			//****************If newRoom or newMsgTypebutton is pressed:*********************************

			if ( req.getParameter("addRoom") != null || req.getParameter("addMsgType") != null)
			{

				VariableManager vm = new VariableManager() ;
				Html htm = new  Html();

				//get new parameters

				if ( req.getParameter("chatRoom")==null )
				{chatParams.setProperty("chatRoom",""); 
				}
				else{chatParams.setProperty("chatRoom",req.getParameter("chatRoom").trim());
				}

				if ( req.getParameter("msgType")==null )
				{chatParams.setProperty("msgType"," ") ;
				}
				else{chatParams.setProperty("msgType",req.getParameter("msgType").trim());
				}

				String theRoom = chatParams.getProperty("chatRoom");
				String theType = chatParams.getProperty("msgType");

				//get all chatparameters
				Enumeration chatEnum = chatParams.propertyNames();
				while (chatEnum.hasMoreElements())
				{
					String paramName = (String)chatEnum.nextElement();


					//Add new Room
					if ( req.getParameter("addRoom") != null && paramName.equals("chatRoom") )
					{
						//add new room to roomlist
						theRoom = chatParams.getProperty(paramName) ;
						log("Rum: " + theRoom );

						roomsV.add(" ");
						roomsV.add( theRoom );

						//add room to session
						session.putValue("roomList",roomsV);					


					}
					//Add new MsgType
					else if ( req.getParameter("addMsgType") != null && paramName.equals("msgType") )
					{
						//add new msgType to msgTypelist
						theType = chatParams.getProperty(paramName);
						log("MsgTyp: " +  theType);

						msgTypesV.add(" ");
						msgTypesV.add(theType);


						newMsgTypeV.add( theType );

						//add type to session
						session.putValue("newMsgTypes",newMsgTypeV);

						session.putValue("msgTypesV",msgTypesV);


					}

				}

				String updateTime = chatParams.getProperty("updateTime");
				log("Updatetime: 1 " + updateTime);
				vm.addProperty("SERVLET_URL", MetaInfo.getServletPath(req)) ;
				vm.addProperty("chatRoom",chatParams.getProperty("chatRoom"));
				vm.addProperty("roomList", htm.createHtmlCode("ID_OPTION",theRoom, roomsV) ) ;
				vm.addProperty("msgType"," ");
				vm.addProperty("msgTypes", htm.createHtmlCode("ID_OPTION",theType, msgTypesV) ) ;
				////FIX selV ska helst bort
				//Vector selV = new Vector();
				//selV.add("oregistrerad");
				vm.addProperty("authorized", htm.createHtmlCode("ID_OPTION",selAutoV, autTypeV) ) ;
				vm.addProperty("chatName",chatParams.getProperty("chatName")  );
				vm.addProperty("updateTime",chatParams.getProperty("updateTime") );
				Vector buttonValues = new Vector();buttonValues.add("1");buttonValues.add("2");buttonValues.add("3");
				vm.addProperty("reload", htm.createRadioButton("reload",buttonValues,chatParams.getProperty("reload") ) );
				vm.addProperty("inOut", htm.createRadioButton("inOut",buttonValues,chatParams.getProperty("inOut") ) );
				vm.addProperty("private", htm.createRadioButton("private",buttonValues,chatParams.getProperty("privat") ) );
				vm.addProperty("public", htm.createRadioButton("public",buttonValues,chatParams.getProperty("publik") ) );
				vm.addProperty("dateTime", htm.createRadioButton("dateTime",buttonValues,chatParams.getProperty("dateTime") ) );
				vm.addProperty("font", htm.createRadioButton("font",buttonValues,chatParams.getProperty("font") ) );
				sendHtml(req,res,vm, HTML_TEMPLATE) ;
				return ;

			}

			//get the new metaId
			String metaId = params.getProperty("META_ID") ;
			log("metaid: "+ metaId);

			//check that its really a new metaId
			String foundMetaId = rmi.execSqlProcedureStr(chatPoolServer, "MetaIdExists " + metaId) ;

			if( !foundMetaId.equals("1") )
			{
				action = "" ;
				String header = "ChatCreator servlet. " ;
				ChatError err = new ChatError(req,res,header,90) ;
				log(header + err.getErrorMsg());
				return ;
			}
			

			//******************* save to db **********************************

			// Lets add a new Chat to DB
			String chatName = chatParams.getProperty("chatName");
			log("chatnameCheck: " + chatName);

			String permission = chatParams.getProperty("permission");

			String sqlQ = "AddNewChat " + metaId + ",'" + chatName + "'," + permission;
			log("AddNewChat sql:" + sqlQ ) ;
			rmi.execSqlUpdateProcedure(chatPoolServer, sqlQ) ;

			Vector theRooms = new Vector();

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

				ChatGroup tempGroup = new ChatGroup( Integer.parseInt( (String)roomsV.get(i)  ) , (String)roomsV.get(i+1) );
				theRooms.add(tempGroup);

			}

			//lets connect the standard msgTypes with the chat		
			String[] tempTypes = rmi.execSqlProcedure(chatPoolServer, "GetBaseMsgTypes");

			for(int i=0;i<tempTypes.length;i++)
			{
				String tempTypeId = rmi.execSqlProcedureStr(chatPoolServer, "GetMsgTypeId " + "'"+ tempTypes[i] +"'");
				rmi.execSqlUpdateProcedure(chatPoolServer,"AddNewChatMsg " + tempTypeId + " , " + metaId ) ;
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
				rmi.execSqlUpdateProcedure(chatPoolServer,"AddNewChatMsg " + msgTypeId + " , " + metaId ) ;

			}
			
			//	Vector valuesV = new Vector();
			String valuesS = "AddChatParams ";

			valuesS = valuesS + metaId +"," 
				+ chatParams.getProperty("updateTime")+"," 
				+ chatParams.getProperty("reload")+"," 
				+ chatParams.getProperty("inOut")+"," 
				+ chatParams.getProperty("privat")+"," 
				+ chatParams.getProperty("publik")+"," 
				+ chatParams.getProperty("dateTime")+"," 
				+ chatParams.getProperty("font") ;

			log("valuesS: " + valuesS );

			rmi.execSqlUpdateProcedure(chatPoolServer,valuesS);
			
			//ok lets add the authorization types
			//if there isnt any lets give everybody access
			log("selAutoV.size()= "+selAutoV.size());
			if (selAutoV.size() == 0)
			{
				selAutoV.addElement(new String("1"));
			}
//################
	
			for(int i=0; i<selAutoV.size(); i++)
			{
				String newAutoSql = "ChatAutoTypes " +  " '" + selAutoV.elementAt(i) + "', " + metaId;
				log("ChatAutoTypes sql:" + newAutoSql ) ;
				rmi.execSqlUpdateProcedure(chatPoolServer,newAutoSql);
			}
			
			
//################			

			//put the new chat in the session
			session.putValue("chatId",metaId);
			session.putValue("roomList",roomsV);
			session.putValue("msgTypes",msgTypesV);
			session.putValue("chatParams",chatParams);			

			Enumeration e = chatParams.propertyNames();
			while (e.hasMoreElements())
			{
				String temp = (String)e.nextElement();
				log( "testParams: " + temp + " Value: " + chatParams.getProperty( temp) );
			} 


			Chat theChat = new Chat( Integer.parseInt(metaId), theRooms, msgTypesV, chatParams );

			session.putValue("theChat", theChat);

			session.getValue("theChat");
			log("chat: " + theChat);

			// Ok, we're done creating the conference. Lets tell imCMS system to show this child.
			rmi.activateChild(imcServer, metaId) ;

			// Ok, we're done adding the chat, Lets log in to it!
			String loginPage = MetaInfo.getServletPath(req) + "ChatLogin?login_type=login" ;
			res.sendRedirect(loginPage) ;

			return ;

		}

		/****************************************************************************************************
		* admin chat
		****************************************************************************************************/

		if (action.equalsIgnoreCase("admin_chat"))
		{
			//öppna administrationssida
			log("open adminpage");

			VariableManager vm = new VariableManager() ;
			Html htm = new  Html();

			//lägg in lämpliga parametrar
			//check which chat we have
			String chatName = req.getParameter("chatName");
			if(chatName==null)chatName ="";
			log("ChatName: " + chatName);
			vm.addProperty("chatName", chatName ) ;

			String metaId = (String)session.getValue("Chat.meta_id");
			log("MetaId: " + metaId);

			Chat theChat = (Chat)session.getValue("theChat");
			log("Chat: " + theChat);

			//************** add new room *************************
			//get chatrooms
			Enumeration groupE = theChat.getAllChatGroups();
			Vector groupsV = new Vector();
			while (groupE.hasMoreElements())
			{
				ChatGroup temp = (ChatGroup)groupE.nextElement(); 
				groupsV.add( temp.getGroupName() );
				groupsV.add( temp.getGroupName() );
				log("grupp: " + temp );
			}

			Vector addGroups = (Vector)session.getValue("NewRooms") == null ? new Vector() : (Vector)session.getValue("NewRooms") ;//sessionen
			Vector delGroups = (Vector)session.getValue("DelRooms") == null ? new Vector() : (Vector)session.getValue("DelRooms") ;//sessionen

			if ( req.getParameter("addRoom") != null )
			{
				String newRoom = req.getParameter("chatRoom");
				//hämta redan tillagda rum

				addGroups.add(newRoom);
				addGroups.add(newRoom);

				session.putValue("NewRooms",addGroups);
			}

			//********* delete room *****************
			if ( req.getParameter("removeRoom") != null )
			{
				//vilket rum ska tas bort
				String delRoom = req.getParameter("roomList");
				log("roomList: " +  delRoom);

				//deleta rummet i listan

				delGroups.add(delRoom);
				delGroups.add(delRoom);

				session.putValue("DelRooms",delGroups);

			}

			//lägg ihop rummen
			for(int i=0;i<addGroups.size();i++)
			{
				groupsV.add(addGroups.get(i));
			}

			for(int i=0;i<delGroups.size();i++)
			{
				groupsV.remove(delGroups.get(i));
			}

			//************ add new msgTypes ********************

			Vector theTypes = theChat.getMsgTypes();

			Vector msgTypeV	= new Vector();
			for(int i = 1; i<theTypes.size();i+=2)
			{
				msgTypeV.add(theTypes.get(i));
				msgTypeV.add(theTypes.get(i));				
			}

			//hämta redan tillagda typer
			Vector addTypes = (Vector)session.getValue("NewTypes") == null ? new Vector() : (Vector)session.getValue("NewTypes") ;
			Vector delTypes = (Vector)session.getValue("DelTypes") == null ? new Vector() : (Vector)session.getValue("DelTypes") ;
			
			Vector autTypeV = (Vector) session.getValue("Chat_autTypeV");
			String[] selAuto = (req.getParameterValues("authorized")==null) ? new String[0] : (req.getParameterValues("authorized"));
			Vector selAutoV = super.convert2Vector(selAuto);
			
			
			//add new msgType
			if ( req.getParameter("addMsgType") != null )
			{
				String newMsgType = req.getParameter("msgType");

				//lägg till ny typ
				addTypes.add(newMsgType);
				addTypes.add(newMsgType);

				session.putValue("NewTypes",addTypes);
			} 

			//*************** delete msgType **********
			if ( req.getParameter("removeMsgType") != null )
			{
				//vilket rum ska tas bort
				String delType = req.getParameter("msgTypes");

				//deleta typen i listan
				delTypes.add(delType);
				delTypes.add(delType);

				session.putValue("DelTypes",delTypes);

			}

			//lägg ihop rummen
			for(int i=0;i<addTypes.size();i++)
			{
				msgTypeV.add(addTypes.get(i));
			}

			for(int i=0;i<delTypes.size();i++)
			{
				msgTypeV.remove(delTypes.get(i));
			}		

			//rita om sidan med rätt värden
			vm.addProperty("chatRoom", "" );
			vm.addProperty("msgType", "" );
			vm.addProperty("roomList", htm.createHtmlCode("ID_OPTION","", groupsV) ) ;
			vm.addProperty("msgTypes", htm.createHtmlCode("ID_OPTION","", msgTypeV) ) ;

			//get parameters
			Properties chatP = super.getNewChatParameters(req);
			
			vm.addProperty("authorized", htm.createHtmlCode("ID_OPTION",selAutoV, autTypeV) ) ;
			vm.addProperty("chatName",chatName );
			vm.addProperty("updateTime",chatP.getProperty("updateTime") );
			Vector buttonValues = new Vector();buttonValues.add("1");buttonValues.add("2");buttonValues.add("3");
			vm.addProperty("reload", htm.createRadioButton("reload",buttonValues,chatP.getProperty("reload") ) );
			vm.addProperty("inOut", htm.createRadioButton("inOut",buttonValues,chatP.getProperty("inOut") ) );
			vm.addProperty("private", htm.createRadioButton("private",buttonValues,chatP.getProperty("privat") ) );
			vm.addProperty("public", htm.createRadioButton("public",buttonValues,chatP.getProperty("publik") ) );
			vm.addProperty("dateTime", htm.createRadioButton("dateTime",buttonValues,chatP.getProperty("dateTime") ) );
			vm.addProperty("font", htm.createRadioButton("font",buttonValues,chatP.getProperty("font") ) );

			sendHtml(req,res,vm, ADMIN_TEMPLATE) ;
			//###################################################################			
			if (req.getParameter("okChat") != null )
			{
				//spara värden till databas 

				//rum
				//ta bort alla rum tillhörande den här chatten
				String delete = "DeleteConnections " + metaId;
				rmi.execSqlUpdateProcedure(chatPoolServer , delete );

				//lägga in alla nya
				//hämta alla befintliga rum
				String[] allRooms = rmi.execSqlProcedure(chatPoolServer, "GetAllRooms ");
				//gå igenom alla, jämför med roomsV och lägg in de som inte finns

				for(int j=1;j<groupsV.size();j+=2)
				{
					String tempId;
					boolean found = false;

					for(int i=0;i<allRooms.length;i++)
					{
						if ( ((String)allRooms[i]).equalsIgnoreCase( (String)groupsV.get(j) ) )
						{
							found = true;
						}
					}

					if (!found)
					{
						//add room
						tempId = rmi.execSqlProcedureStr(chatPoolServer, "GetMaxRoomId");

						String newRsql = "AddNewRoom "  + tempId + ", " + groupsV.get(j);
						log("AddNewRoom sql:" + newRsql ) ;


						rmi.execSqlUpdateProcedure(chatPoolServer, newRsql) ;
					}  

					String roomId = rmi.execSqlProcedureStr( chatPoolServer,"GetRoomId " + (String)groupsV.get(j) );
					log("roomId: " + roomId);
					log("AddNewChatRoom " + " '"+ metaId + "' , '" + roomId + "' ");
					rmi.execSqlUpdateProcedure(chatPoolServer,"AddNewChatRoom " + " '"+ metaId + "' , '" + roomId + "' ") ;
					groupsV.add(j-1,roomId);
				}//end for

				//msgTypes
				//lägga in alla nya
				//hämta alla befintliga rum
				String[] allTypes = rmi.execSqlProcedure(chatPoolServer, "GetAllTypes ");
				//gå igenom alla, jämför med roomsV och lägg in de som inte finns
				for(int j=1;j<msgTypeV.size();j+=2)
				{
					boolean found = false;
					String tempId;

					for(int i=0;i<allTypes.length;i++)
					{
						if ( ((String)allTypes[i]).equalsIgnoreCase( (String)msgTypeV.get(j) ) )
						{
							found = true;
						}
					}

					if (!found)
					{
						//add room
						tempId = rmi.execSqlProcedureStr(chatPoolServer, "GetMaxMsgTypeId ");

						String newRsql = "AddNewMsgType " +  " '" +tempId + "', " + msgTypeV.get(j);
						log("AddNewMsgType sql:" + newRsql ) ;
						rmi.execSqlUpdateProcedure(chatPoolServer, newRsql) ;
					} 

					String typeId = rmi.execSqlProcedureStr( chatPoolServer,"GetMsgTypeId " + (String)msgTypeV.get(j) );
					rmi.execSqlUpdateProcedure(chatPoolServer,"AddNewChatMsg " + typeId + " , " + metaId ) ;

				}			

				//uppdatera databasen med chatP
				String update = "UpdateChatParameters " + metaId;
				update=update + chatP.getProperty("updateTime");
				update=update + chatP.getProperty("reload");
				update=update + chatP.getProperty("inOut");
				update=update + chatP.getProperty("privat");
				update=update + chatP.getProperty("publik");
				update=update + chatP.getProperty("dateTime");
				update=update + chatP.getProperty("font");

				rmi.execSqlUpdateProcedure(chatPoolServer, update );

				//lets get rid off old authorizations if there is any
				String delAutho = "DeleteAuthorizations " + metaId;
				rmi.execSqlUpdateProcedure(chatPoolServer , delAutho );			
	   
				//ok lets save the new ones to db
				String[] newAutho = (req.getParameterValues("authorized")==null) ? new String[0] : (req.getParameterValues("authorized"));
				selAutoV = super.convert2Vector(newAutho);			
				if (selAutoV.size() == 0)
				{
					selAutoV.addElement(new String("1"));
				}	
				for(int i=0; i<selAutoV.size(); i++)
				{
					String newAutoSql = "ChatAutoTypes " +  " '" + selAutoV.elementAt(i) + "', " + metaId;
					log("ChatAutoTypes sql:" + newAutoSql ) ;
					rmi.execSqlUpdateProcedure(chatPoolServer,newAutoSql);
				}
			
			
		

				
				//lägg in chatten i sessionen
				Vector groups = new Vector();
				for(int i= 0; i < groupsV.size()-1;i+=2)
				{
					log("grrrr2 " + groupsV.get(i));
					String value = (String)groupsV.get(i+1);
					String id = (String)groupsV.get(i);
					Integer temp = new Integer(id);
					int idT = temp.intValue();	
					log("id: " + id + " 3 Value: " + value);
					ChatGroup tempGroup= new ChatGroup( idT,value );
					groups.add(tempGroup);
				}
				
				super.removeChat(Integer.parseInt(metaId));
				Chat newChat = new Chat( Integer.parseInt(metaId), groups, msgTypeV, chatP );
				super.addChat(Integer.parseInt(metaId), newChat);
				
				session.putValue("theChat", newChat);

				//redirect to chatViewer
				String url = MetaInfo.getServletPath(req) ;
				url += "ChatViewer" ;
				res.sendRedirect(url) ;

				return;
			}//end okChat

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

		Properties chatParams = super.getNewChatParameters(req) ;

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

			//FIXget the standard msgTypes in the db
			String headline = (String)session.getValue("meta_headline");
			log("metaheadline: " + session.getValueNames());

			//Rensa sessionen från gamla rum och meddelandetyper
			session.putValue("roomList",null);
			session.putValue("msgTypesV",null);

			String[] msgTypes = rmi.execSqlProcedure(chatPoolServer, "GetBaseMsgTypes");
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
				autTypeV.add(String.valueOf(i+1));
				autTypeV.add(autTypes[i]);
			}

			session.putValue("autTypeV",autTypeV);

		//	Vector selV = new Vector();					    
		//	selV.add("");selV.add("oregistrerad");

			// Lets build the Responsepage to the loginpage
			Html htm = new Html();
			VariableManager vm = new VariableManager() ;


			Vector buttonValues = new Vector();buttonValues.add("1");buttonValues.add("2");buttonValues.add("3");

			vm.addProperty("SERVLET_URL", MetaInfo.getServletPath(req)) ;
			vm.addProperty("msgTypes", htm.createHtmlCode("ID_OPTION","säger till", msgTypesV) ) ;
			vm.addProperty("authorized", htm.createHtmlCode("ID_OPTION","1", autTypeV) ) ;
			vm.addProperty("chatName", "") ;
			vm.addProperty("chatRoom", "") ;
			vm.addProperty("msgType", "") ;
			vm.addProperty("updateTime", "30") ;
			vm.addProperty("reload", htm.createRadioButton("reload",buttonValues,chatParams.getProperty("reload") ) );
			vm.addProperty("inOut", htm.createRadioButton("inOut",buttonValues,chatParams.getProperty("inOut") ) );
			vm.addProperty("private", htm.createRadioButton("private",buttonValues,chatParams.getProperty("privat") ) );
			vm.addProperty("public", htm.createRadioButton("public",buttonValues,chatParams.getProperty("publik") ) );
			vm.addProperty("dateTime", htm.createRadioButton("dateTime",buttonValues, chatParams.getProperty("dateTime") ) );
			vm.addProperty("font", htm.createRadioButton("font",buttonValues,chatParams.getProperty("font") ) );

			sendHtml(req,res,vm, HTML_TEMPLATE) ;
			return ;
		}

		if (action.equalsIgnoreCase("admin_chat"))
		{
			//öppna administrationssida
			log("open adminpage");

			VariableManager vm = new VariableManager() ;
			Html htm = new  Html();

			//lägg in lämpliga parametrar
			//check which chat we have
			String chatName = req.getParameter("chatName");
			log("ChatName: " + chatName);

			vm.addProperty("chatName", chatName ) ;

			String metaId = (String)session.getValue("Chat.meta_id");
			log("MetaId: " + metaId);

			Chat theChat = (Chat)session.getValue("theChat");

			log("Chat: " + theChat);
			
			//lets get the selected authorization types
			
//###############
			//get the authorization types 
			String[] autTypes = rmi.execSqlProcedure(chatPoolServer, "GetAuthorizationTypes");
			Vector autTypeV = new Vector();
			for(int i =0;i<autTypes.length;i++)
			{
				log("StringAuttype: " + autTypes[i] );
				autTypeV.add(String.valueOf(i+1));
				autTypeV.add(autTypes[i]);
			}
			session.putValue("Chat_autTypeV",autTypeV);
			
			//lets get the selected authorization types
			String[] types = rmi.execSqlProcedure(chatPoolServer, "GetChatAutoTypes '"+ metaId  + "' ");
			for(int i=0; i<types.length;i++)
			{
				log("i doGeten first time = "+types[i]);
			}
			Vector selAutoV = super.convert2Vector(types);
			vm.addProperty("authorized", htm.createHtmlCode("ID_OPTION",selAutoV, autTypeV) ) ;
		
			//get rooms
			vm.addProperty("chatRoom", "" ) ;
			Enumeration groupE = theChat.getAllChatGroups();
			Vector groupsV = new Vector();
			while (groupE.hasMoreElements())
			{
				ChatGroup temp = (ChatGroup)groupE.nextElement(); 
				groupsV.add( Integer.toString(temp.getGroupId()) );
				groupsV.add( temp.getGroupName() );
				log("grupp: " + temp );
			}
			vm.addProperty("roomList", htm.createHtmlCode("ID_OPTION","", groupsV) ) ;

			//get msgTypes
			vm.addProperty("msgType", "" ) ;
			Vector msgTypesV = theChat.getMsgTypes();
			vm.addProperty("msgTypes", htm.createHtmlCode("ID_OPTION","", msgTypesV) ) ;


			Properties chatP = theChat.getChatParameters();
			Enumeration propE = chatP.propertyNames();
			while (propE.hasMoreElements())
			{
				String value = (String)propE.nextElement();
				log( "props: " + value +" value: " + chatP.getProperty( value ) );
			}
			//	vm.addProperty("chatName",chatP.getProperty("chatName") );
			vm.addProperty("updateTime",chatP.getProperty("updateTime") );
			Vector buttonValues = new Vector();buttonValues.add("1");buttonValues.add("2");buttonValues.add("3");
			vm.addProperty("reload", htm.createRadioButton("reload",buttonValues,chatP.getProperty("reload") ) );
			vm.addProperty("inOut", htm.createRadioButton("inOut",buttonValues,chatP.getProperty("inOut") ) );
			vm.addProperty("private", htm.createRadioButton("private",buttonValues,chatP.getProperty("privat") ) );
			vm.addProperty("public", htm.createRadioButton("public",buttonValues,chatP.getProperty("publik") ) );
			vm.addProperty("dateTime", htm.createRadioButton("dateTime",buttonValues,chatP.getProperty("dateTime") ) );
			vm.addProperty("font", htm.createRadioButton("font",buttonValues,chatP.getProperty("font") ) );

			sendHtml(req,res,vm, ADMIN_TEMPLATE) ;
			return ;

		}

	} // End doGet




	/**
	Detects paths and filenames.
	*/

	public void init(ServletConfig config) throws ServletException
	{
		super.init(config);
		HTML_TEMPLATE = "createChat.HTM" ;
		ADMIN_TEMPLATE = "adminChat.HTM";

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
