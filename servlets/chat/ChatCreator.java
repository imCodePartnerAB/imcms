
import java.io.*;
import java.util.*;
import java.lang.*;
import javax.servlet.*;
import javax.servlet.http.*;
import imcode.external.diverse.*;
import imcode.external.chat.*;
import imcode.util.* ;


//obs här ska det byggas om rejält
//har dock haft problem med att jag får olika servletContext
//vilket innebär att jag tappar chatten och användar sessionen därför
//använder jag jsdk2.0 så långt det går, i väntan på att jag eller någon löst 
//servletContext problemet

public class ChatCreator extends ChatBase
{
	private final static String CVS_REV = "$Revision$" ;
	private final static String CVS_DATE = "$Date$" ;
	
	private final static String HTML_TEMPLATE = "createChat.htm" ;
	private final static String ADMIN_TEMPLATE = "adminChat.htm";
	private final static String ADMIN_TEMPLATES_TEMPLATE= "chat_admin_template1.html";
	private final static String ADMIN_TEMPLATES_BUTTON = "chat_template_admin.html";
	private final static String ADMIN_TEMPLATES_TEMPLATE_2 ="chat_admin_template2.html"; 

	/**
	The POST method creates the html page when this side has been
	redirected from somewhere else.
	**/

	public void doPost(HttpServletRequest req, HttpServletResponse res)
	throws ServletException, IOException
	{
		//log("start doPost");
		// Lets validate the session and get the session
		if (super.checkSession(req,res) == false)
		{
			log("checkSession == false");
			return ;
		}
		HttpSession session = req.getSession(false);


		// Lets get the standard parameters and validate them
		Properties params = super.getSessionParameters(req) ;
		if (super.checkParameters(req, res, params) == false)
		{
			log("getSessionParameters==false");
			return ;
		}

		// Lets get the new chat parameters
		Properties chatParams = super.getNewChatParameters(req) ;
		if (super.checkParameters(req, res, chatParams) == false) 
		{
			log("checkParameters==false");
			//return ;
		}

		// Lets get an user object
		imcode.server.User user = super.getUserObj(req,res) ;
		if(user == null) return ;
		if ( !isUserAuthorized( req, res, user ) ) 
		{
			log("isUserAuthorized==false");
			return ;
		}

		// Lets get serverinformation
		String host = req.getHeader("Host") ;
		String imcServer = Utility.getDomainPref("userserver",host) ;
		String chatPoolServer = Utility.getDomainPref("chat_server",host) ;
		RmiConf rmi = new RmiConf(user) ;

		String action = req.getParameter("action");

		if(action == null)
		{
			action = "" ;
			String header = "ChatCreator servlet. " ;
			ChatError err = new ChatError(req,res,header,3) ;
			log(header + err.getErrorMsg()) ;
			return ;
		}
		
		
		// ********* If a NEW CHAT is created  ********
		if(action.equalsIgnoreCase("ADD_CHAT"))
		{
			//log("action = ADD_CHAT") ;
		
			//*************** Get all New Chatparameters ********************

			//get the msgTypes
			Vector msgTypesV ;
			if( (Vector)session.getValue("msgTypesV")==null ){
				String[][] msgTypes = rmi.execProcedureMulti(chatPoolServer, "C_GetTheMsgTypesBase");
				msgTypesV = super.convert2Vector(msgTypes);
				session.putValue("msgTypesV",msgTypesV);
			}
			msgTypesV = (Vector)session.getValue("msgTypesV");

			//get the authorization types 
			Vector autTypeV ;
			if( (Vector)session.getValue("autTypesV")==null ){
				String[][] autTypes = rmi.execProcedureMulti(chatPoolServer, "C_GetAuthorizationTypes");
				autTypeV = super.convert2Vector(autTypes);
				session.putValue("autTypesV",autTypeV);
			}
			autTypeV = (Vector)session.getValue("autTypesV");
	   		
			//lets get the selected autotypes
			String[] selAuto = (req.getParameterValues("authorized")==null) ? new String[0] : (req.getParameterValues("authorized"));
			Vector selAutoV = super.convert2Vector(selAuto);			

			//get existing rooms
			Vector roomsV = ( (Vector)session.getValue("roomList")==null ) ? new Vector() : (Vector)session.getValue("roomList");

			//get existing new msgTypes
			Vector newMsgTypeV = ( (Vector)session.getValue("newMsgTypes")==null ) ? new Vector() : (Vector)session.getValue("newMsgTypes");


			//****************If newRoom or newMsgTypebutton is pressed:************************
		
			if ( req.getParameter("addRoom") != null || req.getParameter("addMsgType") != null)
			{
				//log("addRoom or addMsgType" );
			
				VariableManager vm = new VariableManager() ;
				Html htm = new  Html();

				//get new parameters
				if ( req.getParameter("chatRoom")==null ){
					chatParams.setProperty("chatRoom",""); 
				}else{
					chatParams.setProperty("chatRoom",req.getParameter("chatRoom").trim());
				}

				if ( req.getParameter("msgType")==null ){
					chatParams.setProperty("msgType"," ") ;
				}else{
					chatParams.setProperty("msgType",req.getParameter("msgType").trim());
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
						//log("Rum: " + theRoom );

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
						//log("MsgTyp: " +  theType);

						msgTypesV.add(" ");
						msgTypesV.add(theType);
						newMsgTypeV.add( theType );

						//add type to session
						session.putValue("newMsgTypes",newMsgTypeV);
						session.putValue("msgTypesV",msgTypesV);
					}
				}//end while
				
				//ok lets set up the page
				String updateTime = chatParams.getProperty("updateTime");
				//log("Updatetime: 1 " + updateTime);
				vm.addProperty("SERVLET_URL", MetaInfo.getServletPath(req)) ;
				vm.addProperty("chatRoom",chatParams.getProperty("chatRoom"));
				vm.addProperty("roomList", htm.createHtmlCode("ID_OPTION",theRoom, roomsV) ) ;
				vm.addProperty("msgType"," ");
				vm.addProperty("msgTypes", htm.createHtmlCode("ID_OPTION",theType, msgTypesV) ) ;
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
				
			
				//log("end addRoom or addMsgType");
				return ;
			}//end adding new msgTypes or rooms
		   
			//get the new metaId
			String metaId = params.getProperty("META_ID") ;
			//log("metaid: "+ metaId);

			//check that its really a new metaId
			String foundMetaId = rmi.execSqlProcedureStr(chatPoolServer, "C_MetaIdExists " + metaId) ;

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
			//log("chatName: " + chatName);
		   
			String permission = chatParams.getProperty("permission");
			//log("permission: "+permission);
		   
			String sqlQ = "C_AddNewChat " + metaId + ",'" + chatName + "'," + permission;
			//log("C_AddNewChat sql:" + sqlQ ) ;
			rmi.execSqlUpdateProcedure(chatPoolServer, sqlQ) ;
		   
			
			//lets save the rooms and assosiate them with this chat
			for (int i=0;i<roomsV.size();i+=2)
			{
				String newRsql = "C_AddNewChatRoom "  +metaId + ", '" + roomsV.get(i+1)+"'";
				//log("C_AddNewChatRoom sql:" + newRsql ) ;
				rmi.execSqlUpdateProcedure(chatPoolServer, newRsql) ;			
			}
			
			//lets connect the standard msgTypes with the chat		
			String[] tempTypes = rmi.execSqlProcedure(chatPoolServer, "C_GetBaseMsgTypes");
			for(int i=0;i<tempTypes.length;i++)
			{
				String tempTypeId = rmi.execSqlProcedureStr(chatPoolServer, "C_GetMsgTypeId " + "'"+ tempTypes[i] +"'");
				rmi.execSqlUpdateProcedure(chatPoolServer,"C_AddNewChatMsg " + tempTypeId + " , " + metaId ) ;
			}
			
			// Lets add the new msgTypes to the db /ugly but it works
			for (int i=0;i<newMsgTypeV.size();i++)
			{
				//save newMsgTypes to db
				//Lets get the highest msgId
				//String msgTypeId = rmi.execSqlProcedureStr(chatPoolServer, "C_GetMaxMsgTypeId");
				
				String newMsql = "C_AddMessageType " +  " '" + metaId + "', " + newMsgTypeV.get(i);
				//log("AddNewMsgType sql:" + newMsql ) ;
				rmi.execSqlUpdateProcedure(chatPoolServer,newMsql);
			}
			
			//	Vector valuesV = new Vector();
			String valuesS = "C_AddChatParams ";

			valuesS = valuesS + metaId +"," 
				+ chatParams.getProperty("updateTime")+"," 
				+ chatParams.getProperty("reload")+"," 
				+ chatParams.getProperty("inOut")+"," 
				+ chatParams.getProperty("privat")+"," 
				+ chatParams.getProperty("publik")+"," 
				+ chatParams.getProperty("dateTime")+"," 
				+ chatParams.getProperty("font") ;

			//log("valuesS: " + valuesS );

			rmi.execSqlUpdateProcedure(chatPoolServer,valuesS);
			
			//ok lets add the authorization types
			//if there isnt any lets give everybody access
			//log("selAutoV.size()= "+selAutoV.size());
			if (selAutoV.size() == 0)
			{
				selAutoV.addElement(new String("1"));
			}
	
			for(int i=0; i<selAutoV.size(); i++)
			{
				String newAutoSql = "C_ChatAutoTypes " +  " '" + selAutoV.elementAt(i) + "', " + metaId;
				//System.out.println("ChatAutoTypes sql:" + newAutoSql ) ;
				rmi.execSqlUpdateProcedure(chatPoolServer,newAutoSql);
			}
			
			//put the parameters needed to create a chat in the session
			session.putValue("chatId",metaId);
			//session.putValue("roomList",roomsV);
			//session.putValue("msgTypes",msgTypesV);
			//session.putValue("chatParams",chatParams);			
			
			Enumeration e = chatParams.propertyNames();
			while (e.hasMoreElements())
			{
				String temp = (String)e.nextElement();
				//log( "testParams: " + temp + " Value: " + chatParams.getProperty( temp) );
			} 

			//ok lets create the chat object and its rooms
			String[] types = rmi.execSqlProcedure(chatPoolServer, "C_GetChatAutoTypes '"+ metaId  + "' ");
			
			Chat theChat = new Chat( Integer.parseInt(metaId), msgTypesV, chatParams, types );
			//lets get the rooms data from db
			String[][] roomIdNr = rmi.execProcedureMulti(chatPoolServer, "C_GetRooms " + metaId) ;
			//log("C_GetRooms " + metaId);
			for(int i=0; i<roomIdNr.length;i++)
			{
				//log("#########"+roomIdNr[i][0]);
				theChat.createNewChatGroup(Integer.parseInt(roomIdNr[i][0]), roomIdNr[i][1]);
			}
			
			
			ServletContext myContext = getServletContext();
			myContext.setAttribute("theChat"+metaId, theChat);
			
			// Ok, we're done creating the chat. Lets tell imCMS system to show this child.
			rmi.activateChild(imcServer, metaId) ;

			// Ok, we're done adding the chat, Lets log in to it!
			String loginPage = MetaInfo.getServletPath(req) + "ChatLogin?login_type=login" ;
			res.sendRedirect(loginPage) ;
			
			//log("end ADD_CHAT");
			return ;			
		}

		/****************************************************************************************************
		* admin chat
		****************************************************************************************************/
		
		if (action.equalsIgnoreCase("admin_chat"))
		{
			//log("*** start admin_chat ***");
			VariableManager vm = new VariableManager() ;
			Html htm = new  Html();
			
			//this method handle the stuff we can do in the template admin page
			if (req.getParameter("admin_templates_meta") != null)
			{
				//log("admin_templates_meta");
				if(req.getParameter("add_templates")!= null)
				{
					//log("add_templates");
					//lets add a new template set if we got some thing to add
					String newLibName  = req.getParameter("template_lib_name");
					
					newLibName = super.verifySqlText(newLibName) ;
					if (newLibName==null)				
					{	
						String header = "ChatCreator servlet. " ;
						String msg = params.toString() ;
						ChatError err = new ChatError(req,res,header, 80) ; //obs kolla om rätt nr
						return ;
					}
					// Lets check if we already have a templateset with that name
					String sql = "C_FindTemplateLib " + newLibName ;
					String libNameExists = rmi.execSqlProcedureStr(chatPoolServer, sql) ;
					
					if( !libNameExists.equalsIgnoreCase("-1") ) {
						String header = "ChatCreator servlet. " ;
						ChatError err = new ChatError(req,res,header, 84) ;//obs kolla om rätt nr
						return ;
					}
					
					
			//*********
					String sqlQ = "C_AddTemplateLib '" + newLibName + "'" ;
					rmi.execSqlUpdateProcedure(chatPoolServer, sqlQ) ;

					// Lets copy the original folders to the new foldernames
					String metaId = super.getMetaId(req) ;
					FileManager fileObj = new FileManager() ;
					File templateSrc = new File(MetaInfo.getExternalTemplateFolder(imcServer, metaId), "original") ;
					File imageSrc = new File(rmi.getExternalImageHomeFolder(host,imcServer, metaId), "original") ;
					File templateTarget = new File(MetaInfo.getExternalTemplateFolder(imcServer, metaId), newLibName) ;
					File imageTarget = new File(rmi.getExternalImageHomeFolder(host,imcServer, metaId), newLibName) ;

					fileObj.copyDirectory(templateSrc, templateTarget) ;
					fileObj.copyDirectory(imageSrc, imageTarget) ;
						
									
				}//done add new template lib
				
				if (req.getParameter("change_templatelib")!=null)
				{//ok lets handle the change set case
					//log("change_templatelib");

						// Lets get the new library name and validate it
						String newLibName = req.getParameter("new_templateset_name")  ;
						//log("newLibName: "+newLibName);
						if (newLibName == null) {
							String header = "ChatCreator servlet. " ;
							String msg = params.toString() ;
							ChatError err = new ChatError(req,res,header, 80) ;//obs kolla om rätt nr
							return ;
						}

						// Lets find the selected template in the database and get its id
						// if not found, -1 will be returned
						String sqlQ = "C_GetTemplateIdFromName '" + newLibName + "'" ;//GetTemplateIdFromName
						String templateId = rmi.execSqlProcedureStr(chatPoolServer, sqlQ) ;
						if(templateId.equalsIgnoreCase("-1")) {
							String header = "ChatCreator servlet. " ;
							String msg = params.toString() ;
							ChatError err = new ChatError(req,res,header,81) ;
							return ;
						}
						// Ok, lets update the chat with this new templateset.
						//but first lets delete the old one.
						String delString = "C_deleteChatTemplateset "+ params.getProperty("META_ID") ;
						rmi.execSqlUpdateProcedure(chatPoolServer, delString) ;
						
						String updateSql = "C_SetNewTemplateLib " + params.getProperty("META_ID") ;//SetTemplateLib
						updateSql += ", '" + newLibName + "'" ;
						rmi.execSqlUpdateProcedure(chatPoolServer, updateSql) ;


				}
				if (req.getParameter("UPLOAD_CHAT")!=null)
				{//ok lets handle the upload of templates and images
					//lets load the page to handle this
					String folderName = req.getParameter("TEMPLATE_NAME");
					String uploadType = req.getParameter("UPLOAD_TYPE");
					String metaId = params.getProperty("META_ID");
					//log(folderName +" "+uploadType+" "+metaId);
					if(folderName == null || uploadType == null || metaId == null)
						return;
					
					vm.addProperty("META_ID", metaId ) ;
					vm.addProperty("UPLOAD_TYPE", uploadType);
					vm.addProperty("FOLDER_NAME", folderName);
					sendHtml(req,res,vm, ADMIN_TEMPLATES_TEMPLATE_2) ;
					return;
				}
				
			}
			
			
			//check if template adminpage is wanted
			if (req.getParameter("adminTemplates")!=null)
			{
				//log("jippikaayeeee");
				//ok we need to create the admin templates page, and to be
				//able to do that, we need the name and number of the current template set
				//but we also need a select list of all template set there is...
				//so what keeping us from doing it...coffe-break maby...
				//back from the coffe-break, now lets rock!
				
				//lets get the meta_id
				String metaId = params.getProperty("META_ID") ;
				//log("metaid: "+ metaId);
				//ok now lets get the template set name
				String templateSetName = rmi.execSqlProcedureStr(chatPoolServer, "C_GetTemplateLib '"+ metaId  + "' ");
				if (templateSetName == null) 
				{
					templateSetName="";
				}
				//ok lets get all the template set there is
				String[] templateLibs =  rmi.execSqlProcedure(chatPoolServer, "C_GetAllTemplateLibs");
				//lets check if we got something 
				//log(""+templateLibs.length); 
				Vector vect = new Vector();
				if (templateLibs != null)
				{
					vect = super.convert2Vector(templateLibs);
				}
				
				vm.addProperty("TEMPLATE_LIST",htm.createHtmlCode("ID_OPTION",templateSetName, vect) );
				vm.addProperty("CURRENT_TEMPLATE_SET", templateSetName ) ;
				sendHtml(req,res,vm, ADMIN_TEMPLATES_TEMPLATE) ;
				return;
				
			}
			
			
			
//FIX ugly		//lets get the chatMember
			//check which chat we have
			String chatName = req.getParameter("chatName");
			if(chatName==null)chatName ="";
			//log("ChatName: " + chatName);
			vm.addProperty("chatName", chatName ) ;

			String metaId = (String)session.getValue("Chat.meta_id");
			//log("MetaId: " + metaId);
			ChatMember myMember = (ChatMember) session.getValue("theChatMember");
			if (myMember == null)
			{
				log("chatMembern was null so return");
				return;
			}
			Chat myChat = myMember.getMyParent();
			if (myChat == null)
			{
				log("theChat was null so return");
				return;
			}
//FIX end
			//************** add new room *************************
			//get chatrooms
			Enumeration groupE = myChat.getAllChatGroups();
			Vector groupsV = new Vector();
			while (groupE.hasMoreElements())
			{
				ChatGroup temp = (ChatGroup)groupE.nextElement(); 
				groupsV.add( temp.getGroupName() );
				groupsV.add( temp.getGroupName() );
				//log("grupp: " + temp );
			}

			Vector addGroups = (Vector)session.getValue("NewRooms") == null ? new Vector() : (Vector)session.getValue("NewRooms") ;//sessionen
			Vector delGroups = (Vector)session.getValue("DelRooms") == null ? new Vector() : (Vector)session.getValue("DelRooms") ;//sessionen

			if ( req.getParameter("addRoom") != null )
			{
				//log("*** start addRoom ***");
				//lets get the room to add
				String newRoom = req.getParameter("chatRoom");
				addGroups.add(newRoom);
				addGroups.add(newRoom);
				
				session.putValue("NewRooms",addGroups);
				//log("*** end addRoom ***");
			}

			//********* delete room *****************
			if ( req.getParameter("removeRoom") != null )
			{
				//log("*** start removeRoom ***");
				//vilket rum ska tas bort
				String delRoom = req.getParameter("roomList");
				//log("roomList: " +  delRoom);

				//deleta rummet i listan
				delGroups.add(delRoom);
				delGroups.add(delRoom);

				session.putValue("DelRooms",delGroups);
				//log("*** end removeRoom ***");
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

			Vector theTypes = myChat.getMsgTypes();

			Vector msgTypeV	= new Vector();
			for(int i = 0; i<theTypes.size();i+=2)
			{
				msgTypeV.add(theTypes.get(i+1));
				msgTypeV.add(theTypes.get(i+1));				
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

			//lägg ihop msgTyperna
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
			
			File templatePath = new File(super.getExternalTemplateFolder (req),ADMIN_TEMPLATES_BUTTON);
			Vector tempV = new Vector();
			ParseServlet parser = new ParseServlet(templatePath, tempV,tempV) ;
			String templateButton = parser.getHtmlDoc() ;			
			vm.addProperty("templates", templateButton);
			
			vm.addProperty("authorized", htm.createHtmlCode("ID_OPTION",selAutoV, autTypeV) ) ;
			vm.addProperty("chatName",chatName );
			vm.addProperty("","");
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
				//log("*** start okChat ***");
				//spara värden till databas 

				
				//ta bort alla rum tillhörande den här chatten
				//String delete = "C_DeleteConnections " + metaId;
				//rmi.execSqlUpdateProcedure(chatPoolServer , delete );

				//lets get all current connected rooms 
				Vector currGroupsV 		= (Vector)session.getValue("chat_V_room");
				Vector currMsgTypesV 	= (Vector)session.getValue("chat_V_msgTypes");
				if (currGroupsV == null)
				{
					//fixa så att de hämtas från db #### OBS ####
				}
				if (currMsgTypesV == null)
				{
					//fixa så att de hämtas från db #### OBS ####
					//eller från chatten direct kanske är smartare
				}
				
				//now we have to see if we need to do anything to the db
				//lets start whit the Groups
				for(int i=0;i<groupsV.size();i+=2)
				{
					boolean found=false;
					String name 	= (String)	groupsV.get(i+1);
					for(int e=0;e< currGroupsV.size();e+=2)
					{
						String name2 = (String) currGroupsV.get(e+1);
						if (name.equals(name2))
						{
							found = true;
							break;
						}
					}
					if ( !found )//then there is a new one lets save it to db
					{
						rmi.execSqlUpdateProcedure(chatPoolServer, "C_AddNewChatRoom '"+metaId+"','"+name+"'");
					}					
				}
				session.removeValue("NewRooms");
				//lets see if there is any to delete
				Vector dellV = (Vector) session.getValue("DelRooms");
				session.removeValue("DelRooms");
				if (dellV != null)//ok we have some to delete
				{
					for(int i=0; i<dellV.size();i+=2)
					{
						boolean found = false;
						String name 	= (String)	dellV.get(i+1);
						//log("name1= "+name);
						for(int e=0;e<currGroupsV.size();e+=2)
						{
							String name2 = (String)currGroupsV.get(e+1);
							//log("name2 = "+name2);
							if (name.equals(name2))//ok lets leave this one
							{
								found = true;
								break;
							}
						}
						if (found)//lets delete this on
						{
							String roomId = rmi.execSqlProcedureStr(chatPoolServer, "C_GetTheRoomId '"+metaId+"','"+name+"'");
							rmi.execSqlUpdateProcedure(chatPoolServer, "C_DeleteChatRoom '"+metaId+"','"+roomId+"'");
						}
					}
				}

				//now lets se how it is whith the messageTypes
				for(int i=0;i<msgTypeV.size();i+=2)
				{
					boolean found=false;
					String name 	= (String)	msgTypeV.get(i+1);
					for(int e=0;e< currMsgTypesV.size();e+=2)
					{
						String name2 = (String) currMsgTypesV.get(e+1);
						if (name.equals(name2))
						{
							found = true;
							break;
						}
					}
					if ( !found )//then there is a new one lets save it to db
					{
						//log("add new msgtype stringen C_AddMessageType '"+metaId+"','"+name+"'");
						rmi.execSqlUpdateProcedure(chatPoolServer, "C_AddMessageType '"+metaId+"','"+name+"'");
					}					
				}
				
				session.removeValue("NewTypes");
				//lets see if there is any to delete
				Vector dellMsgV = (Vector) session.getValue("DelTypes");
				session.removeValue("DelTypes");
				if (dellMsgV != null)//ok we have some to delete
				{
					for(int i=0; i<dellMsgV.size();i+=2)
					{
						boolean found = false;
						String name 	= (String)	dellMsgV.get(i+1);
						//log("name1= "+name);
						for(int e=0;e<currMsgTypesV.size();e+=2)
						{
							String name2 = (String)currMsgTypesV.get(e+1);
							//log("name2 = "+name2);
							if (name.equals(name2))//ok lets leave this one
							{
								found = true;
								break;
							}
						}
						if (found)//lets delete this on
						{
							String msgId = rmi.execSqlProcedureStr(chatPoolServer, "C_GetMessageId "+name);
							rmi.execSqlUpdateProcedure(chatPoolServer, "C_DeleteMessage '"+metaId+"','"+msgId+"'");
						}
					}
				}

				//uppdatera databasen med chatP
				StringBuffer update = new StringBuffer("C_UpdateChatParams '" + metaId);
				update.append("','"+ chatP.getProperty("updateTime"));
				update.append("','"+ chatP.getProperty("reload"));
				update.append("','"+ chatP.getProperty("inOut"));
				update.append("','"+ chatP.getProperty("privat"));
				update.append("','"+ chatP.getProperty("publik"));
				update.append("','"+ chatP.getProperty("dateTime"));
				update.append("','"+ chatP.getProperty("font")+"'");
				
				//log(update.toString());
				rmi.execSqlUpdateProcedure(chatPoolServer, update.toString() );

				//lets get rid off old authorizations if there is any
				String delAutho = "C_DeleteAuthorizations " + metaId;
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
					String newAutoSql = "C_ChatAutoTypes " +  " '" + selAutoV.elementAt(i) + "', " + metaId;
					//log("C_ChatAutoTypes sql:" + newAutoSql ) ;
					rmi.execSqlUpdateProcedure(chatPoolServer,newAutoSql);
				}
							
				//ok lets set up the chat whith the new settings	
				String[] types = rmi.execSqlProcedure(chatPoolServer, "C_GetChatAutoTypes '"+ metaId  + "' ");
				myChat.setAuthorizations(types);
				//Chat theChat = new Chat( Integer.parseInt(metaId), msgTypesV, chatParams, types );
				//lets get the rooms data from db
				String[][] roomIdNr = rmi.execProcedureMulti(chatPoolServer, "C_GetRooms " + metaId) ;
				//ok lets see if there is any rooms we have to get rid of
				Enumeration enum = myChat.getAllChatGroups();
				while (enum.hasMoreElements())
				{
					ChatGroup temp = (ChatGroup) enum.nextElement();
					boolean found=false;
					int x=0;
					while (!found && x<roomIdNr.length)
					{
						if (temp.getGroupId() == Integer.parseInt(roomIdNr[x][0]))
						{
							temp.setChatGroupName(roomIdNr[x][1]);//just incase
							found = true;
						}
						x++;
					}
					if (!found)
					{//lets remove it
						myChat.removeChatGroup(temp.getGroupId());
						temp = null;//ugly if there is any members in it they will have a null pointer i think
					}
					
				}
				//ok lets add new groups if there is any lets get the existing groups 
				enum = myChat.getAllChatGroups();
				for(int i=0; i<roomIdNr.length;i++)//ugly but it works
				{
					boolean found=false;
					while (enum.hasMoreElements() && !found)
					{
						ChatGroup temp = (ChatGroup) enum.nextElement();
						if (temp.getGroupId() == Integer.parseInt(roomIdNr[i][0]))
						{
							found = true;
						}
					}
					if (!found)
					{
						myChat.createNewChatGroup(Integer.parseInt(roomIdNr[i][0]), roomIdNr[i][1]);
					}
				}				
				//ok lets get the propertys from db
				String getParamsSql = "C_GetChatParameters "+metaId;
				String[] param = rmi.execSqlProcedure(chatPoolServer, "C_GetChatParameters '"+ metaId  + "' ");	
						
				chatParams = super.getChatParams(param);
				String[] chatData = rmi.execSqlProcedure(chatPoolServer, "C_GetChatNameAndPerm '"+ metaId  + "' ");	
				chatParams.setProperty("chatName",chatData[0]);
				chatParams.setProperty("permission",chatData[1]);
				
				myChat.setParams(chatParams);				
				String sqlQ = "C_GetTheMsgTypes " + metaId;
				String[][] msgTypes = rmi.execProcedureMulti(chatPoolServer,sqlQ);
				Vector theMsgTypesV = super.convert2Vector(msgTypes);
				myChat.setMsgTypes(theMsgTypesV);
				
				myMember.setProperties(myChat.getChatParameters());
				
				//redirect to chatViewer
				String url = MetaInfo.getServletPath(req) ;
				url += "ChatViewer" ;
				//log("*** end okChat ***");
				res.sendRedirect(url) ;

				return;
			}//end okChat
			
			//log("*** end admin_chat ***");
			return ;
		}


	} // End POST


	/**
	The GET method creates the html page when this side has been
	redirected from somewhere else.
	**/
	//laddar admin sida osv
	public void doGet(HttpServletRequest req, HttpServletResponse res)
	throws ServletException, IOException
	{
		//log("start doGet");
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
			//log("action = NEW");
//#ugly			//Rensa sessionen från gamla rum och meddelandetyper
			session.removeValue("roomList");
			session.removeValue("msgTypesV");
			
			//lets get some data from db
			String[][] msgTypes = rmi.execProcedureMulti(chatPoolServer, "C_GetTheMsgTypesBase");
			String[][] autTypes = rmi.execProcedureMulti(chatPoolServer, "C_GetAuthorizationTypes");
			
			//lets convert it to vector
			Vector msgTypesV = super.convert2Vector(msgTypes);			
			Vector autTypeV = super.convert2Vector(autTypes);
			
			//add it into the session
			session.putValue("msgTypesV",msgTypesV);
			session.putValue("autTypeV",autTypeV);

			// Lets build the Responsepage to the loginpage
			Html htm = new Html();
			VariableManager vm = new VariableManager() ;

			Vector buttonValues = new Vector();buttonValues.add("1");buttonValues.add("2");buttonValues.add("3");

			vm.addProperty("SERVLET_URL", MetaInfo.getServletPath(req)) ;
			vm.addProperty("msgTypes", htm.createHtmlCode("ID_OPTION","säger till", msgTypesV) ) ;
			vm.addProperty("authorized", htm.createHtmlCode("ID_OPTION","1", autTypeV) ) ;
			vm.addProperty("chatName", "") ;
			vm.addProperty("chatRoom", "") ;
			vm.addProperty("templates", "");
			vm.addProperty("msgType", "") ;
			vm.addProperty("updateTime", "30") ;
			vm.addProperty("reload", htm.createRadioButton("reload",buttonValues,chatParams.getProperty("reload") ) );
			vm.addProperty("inOut", htm.createRadioButton("inOut",buttonValues,chatParams.getProperty("inOut") ) );
			vm.addProperty("private", htm.createRadioButton("private",buttonValues,chatParams.getProperty("privat") ) );
			vm.addProperty("public", htm.createRadioButton("public",buttonValues,chatParams.getProperty("publik") ) );
			vm.addProperty("dateTime", htm.createRadioButton("dateTime",buttonValues, chatParams.getProperty("dateTime") ) );
			vm.addProperty("font", htm.createRadioButton("font",buttonValues,chatParams.getProperty("font") ) );

			sendHtml(req,res,vm, HTML_TEMPLATE) ;
			//log("end NEW");
			return ;
		}
		
		String templateAdmin = req.getParameter("ADMIN_TEMPLATES");
		if (templateAdmin != null)
		{//ok we have done upload template or image lets get back to the adminpage
			this.doPost(req,res);
			return;
		}

		if (action.equalsIgnoreCase("admin_chat"))
		{
			//öppna administrationssida
			//log("action =  admin_chat");

			VariableManager vm = new VariableManager() ;
			Html htm = new  Html();

			//lägg in lämpliga parametrar
			//check which chat we have
			String chatName = req.getParameter("chatName");
			//log("ChatName: " + chatName);

			vm.addProperty("chatName", chatName ) ;

			String metaId = (String)session.getValue("Chat.meta_id");
			//log("MetaId: " + metaId);
			
			ServletContext myContext = getServletContext();
			Chat theChat = (Chat)myContext.getAttribute("theChat"+metaId);

			//log("Chat: " + theChat);
			
			//lets get the selected authorization types
			
//###############
			//get the authorization types 
			String[][] autTypes = rmi.execProcedureMulti(chatPoolServer, "C_GetAuthorizationTypes");
			Vector autTypeV = super.convert2Vector(autTypes);
			
			session.putValue("Chat_autTypeV",autTypeV);
			
			//lets get the selected authorization types
			String[] types = rmi.execSqlProcedure(chatPoolServer, "C_GetChatAutoTypes '"+ metaId  + "' ");
			
			Vector selAutoV = super.convert2Vector(types);
			vm.addProperty("authorized", htm.createHtmlCode("ID_OPTION",selAutoV, autTypeV) ) ;
		
			//get rooms
			vm.addProperty("chatRoom", "" ) ;
			Enumeration groupE = theChat.getAllChatGroups();
			Vector groupsV = new Vector();
			while (groupE.hasMoreElements())
			{
				ChatGroup temp = (ChatGroup)groupE.nextElement(); 
				groupsV.add( temp.getGroupName() );
				groupsV.add( temp.getGroupName() );
				//log("grupp: " + temp );
			}
			vm.addProperty("roomList", htm.createHtmlCode("ID_OPTION","", groupsV) ) ;

			//get msgTypes
			vm.addProperty("msgType", "" ) ;
			Vector msgTypesV1 = theChat.getMsgTypes();
			Vector msgTypesV = new Vector();
			for(int i=0;i<msgTypesV1.size(); i+=2)
			{
			
				msgTypesV.add(msgTypesV1.get(i+1));
				msgTypesV.add(msgTypesV1.get(i+1));				
			}
			vm.addProperty("msgTypes", htm.createHtmlCode("ID_OPTION","", msgTypesV) ) ;
			
			
			Properties chatP = theChat.getChatParameters();
			Enumeration propE = chatP.propertyNames();
			while (propE.hasMoreElements())
			{
				String value = (String)propE.nextElement();
				//log( "props: " + value +" value: " + chatP.getProperty( value ) );
			}
			
			//lets save the current rooms and msg types into the session
			session.putValue("chat_V_room", groupsV);
			session.putValue("chat_V_msgTypes",msgTypesV);
			
			
			// Lets parse one aHref reference
			
			File templatePath = new File(super.getExternalTemplateFolder (req) ,ADMIN_TEMPLATES_BUTTON);
			Vector tempV = new Vector();
			ParseServlet parser = new ParseServlet(templatePath, tempV,tempV) ;
			String templateButton = parser.getHtmlDoc() ;

			
			vm.addProperty("templates", templateButton);
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
		
	} // End of INIT

	/**
	Log function, will work for both servletexec and Apache
	**/

	public void log( String str)
	{
		super.log("ChatCreator: " + str) ;
		//System.out.println("ChatCreator: " + str ) ;
	}


} // End class
