
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
	
	private final static String HTML_TEMPLATE = "admin_chat.html" ;
	private final static String HTML_TEMPLATES_BUTTON = "chat_template_admin.html";
	
	private final static String ADMIN_TEMPLATE = "adminChat.htm";
	private final static String ADMIN_TEMPLATES_TEMPLATE= "chat_admin_template1.html";
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
		if (super.checkSession(req,res) == false){
			log("checkSession == false");
			return ;
		}
		HttpSession session = req.getSession(false);
		
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

		if(action == null){
			action = "" ;
			String header = "ChatCreator servlet. " ;
			ChatError err = new ChatError(req,res,header,3) ;
			log(header + err.getErrorMsg()) ;
			return ;
		}
		
		//peter jobbar här
		Chat myChat = (Chat) session.getAttribute("myChat");
		if (myChat == null) {
			myChat = createChat(req, user, Integer.parseInt(getMetaId(req)));
		}
		int metaId = myChat.getChatId();
		
		if(action.equalsIgnoreCase("admin_chat")) {
			log("admin_chat");
			//ok nu hämtar vi in all data från formuläret och updaterar chat-objektet
			if ( req.getParameter("addRoom") != null && ( !req.getParameter("chatRoom").trim().equals("") )){
				myChat.createNewChatGroup(req.getParameter("chatRoom"));	
			}
			if ( req.getParameter("removeRoom") != null && req.getParameter("roomList")!=null){
				if ((myChat.getAllChatGroupsIdAndNameV()).size() > 2) {
					myChat.removeChatGroup(Integer.parseInt(req.getParameter("roomList")));	
				}
			}
			if ( req.getParameter("addMsgType") != null && (!req.getParameter("msgType").trim().equals(""))){
				myChat.addMsgType(req.getParameter("msgType"));	
			}
			if ( req.getParameter("removeMsgType") != null && req.getParameter("msgTypes")!=null) {
				int type= Integer.parseInt(req.getParameter("msgTypes"));
				if (type <100 || type >=104) {
					myChat.removeMsgType(type);	
				}
			}
			if (req.getParameterValues("authorized")!= null) {
				myChat.setSelectedAuto(req.getParameterValues("authorized"));
			}
			if (req.getParameter("update") != null ) {
				myChat.setupdateTime(Integer.parseInt(req.getParameter("update")));
			}
			if (req.getParameter("reload") != null) {
				myChat.setreload(Integer.parseInt(req.getParameter("reload")));
			}
			if (req.getParameter("inOut") != null) {
				myChat.setinOut(Integer.parseInt(req.getParameter("inOut")));
			}
			if (req.getParameter("private") != null) {
				myChat.setprivate(Integer.parseInt(req.getParameter("private")));
			}
			if (req.getParameter("public") != null) {
				myChat.setpublic(Integer.parseInt(req.getParameter("public")));
			}
			if (req.getParameter("dateTime") != null) {
				myChat.setdateTime(Integer.parseInt(req.getParameter("dateTime")));
			}
			if (req.getParameter("font") != null) {
				myChat.setfont(Integer.parseInt(req.getParameter("font")));
			}
		
			//lets save to db
			if (req.getParameter("okChat") != null) {
				log("okChat");
			
				String sqlStr = "C_AddNewChat " + metaId + ",'" + myChat.getChatName() + "',3" ;
				log(sqlStr) ;
				rmi.execSqlUpdateProcedure(chatPoolServer, sqlStr) ;
			   
			   	//lets delete old-ones if any
			  	sqlStr = "C_DeleteChatRooms " +metaId;
				//System.out.println(sqlStr);
				rmi.execSqlUpdateProcedure(chatPoolServer, sqlStr) ;
				
				//lets save the rooms
				Vector vect = myChat.getAllChatGroupsIdAndNameV();
				//System.out.println("asasd "+vect.size());
				for (int i=0;i<vect.size();i+=2){
					sqlStr = "C_AddNewChatRoom "+ metaId + ", "+ vect.get(i)+",'"+vect.get(i+1)+"'";
					//System.out.println(sqlStr);
					rmi.execSqlUpdateProcedure(chatPoolServer, sqlStr) ;			
				}
				sqlStr = "C_Delete_MsgTypes "+metaId;
				rmi.execSqlUpdateProcedure(chatPoolServer,sqlStr ) ;
								
				//lets connect the standard msgTypes with the chat					
				String[] tempTypes = rmi.execSqlProcedure(chatPoolServer, "C_GetBaseMsgTypes");
				for(int i=0;i<tempTypes.length;i++)	{
					String tempTypeId = rmi.execSqlProcedureStr(chatPoolServer, "C_GetMsgTypeId " + "'"+ tempTypes[i] +"'");
					rmi.execSqlUpdateProcedure(chatPoolServer,"C_AddNewChatMsg " + tempTypeId + " , " + metaId ) ;
				}
				
				// Lets add the new msgTypes to the db /ugly but it works
				Vector msgV = myChat.getMsgTypes();
				for (int i=0;i<msgV.size();i+=2){				
					sqlStr = "C_AddMessageType " +  " '" + metaId + "', '" + msgV.get(i)+"','"+msgV.get(i+1);
					log(sqlStr);
					rmi.execSqlUpdateProcedure(chatPoolServer,sqlStr);
				}
				
				//	Vector valuesV = new Vector();
				String valuesS = "C_AddChatParams ";
				valuesS = valuesS + metaId +"," 
					+ myChat.getupdateTime()+"," 
					+ myChat.getreload()+"," 
					+ myChat.getinOut()+"," 
					+ myChat.getprivate()+"," 
					+ myChat.getpublic()+"," 
					+ myChat.getdateTime()+"," 
					+ myChat.getfont() ;
					
				rmi.execSqlUpdateProcedure(chatPoolServer,valuesS);
				
				//ok lets add the authorization types
				Vector autoV = myChat.getSelectedAuto();	
				for(int i=0; i<autoV.size(); i++){
					sqlStr = "C_ChatAutoTypes "  + autoV.elementAt(i) + ", " + metaId;
					log(sqlStr);
					rmi.execSqlUpdateProcedure(chatPoolServer,sqlStr);
				}
				//ok now we have saved the stuff to the db so lets set up the chat and put it in the context
				sqlStr = "C_GetMsgTypes "+metaId ;
				String[][] messages = rmi.execProcedureMulti(chatPoolServer,sqlStr);
				if (messages != null) {
					myChat.setMsgTypes(convert2Vector(messages));
				}
				ServletContext myContext = getServletContext();
				myContext.setAttribute("theChat"+metaId, myChat);
			
				// Ok, we're done creating the chat. Lets tell imCMS system to show this child.
				rmi.activateChild(imcServer, metaId+"") ;

				// Ok, we're done adding the chat, Lets log in to it!
				String loginPage = MetaInfo.getServletPath(req) + "ChatLogin?login_type=login" ;
				log(loginPage);
				res.sendRedirect(loginPage) ;				
				//log("end ADD_CHAT");
				return ;	
							
			}//end if(action.equalsIgnoreCase("okChat"))
			
			//check if template adminpage is wanted
			if (req.getParameter("admin_templates_meta") != null){
				log("admin_templates_meta");
				if(req.getParameter("add_templates")!= null){
					String newLibName  = req.getParameter("template_lib_name");					
					newLibName = super.verifySqlText(newLibName) ;
					if (newLibName==null){	
						String header = "ChatCreator servlet. " ;
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
					String sqlQ = "C_AddTemplateLib '" + newLibName + "'" ;
					rmi.execSqlUpdateProcedure(chatPoolServer, sqlQ) ;
					// Lets copy the original folders to the new foldernames
					FileManager fileObj = new FileManager() ;
					File templateSrc = new File(MetaInfo.getExternalTemplateFolder(imcServer, metaId+""), "original") ;
					File imageSrc = new File(rmi.getExternalImageHomeFolder(host,imcServer, metaId+""), "original") ;
					File templateTarget = new File(MetaInfo.getExternalTemplateFolder(imcServer, metaId+""), newLibName) ;
					File imageTarget = new File(rmi.getExternalImageHomeFolder(host,imcServer, metaId+""), newLibName) ;

					fileObj.copyDirectory(templateSrc, templateTarget) ;
					fileObj.copyDirectory(imageSrc, imageTarget) ;									
				}//done add new template lib
				
				if (req.getParameter("change_templatelib")!=null){//ok lets handle the change set case
					log("change_templatelib");
						// Lets get the new library name and validate it
						String newLibName = req.getParameter("new_templateset_name")  ;
						//log("newLibName: "+newLibName);
						if (newLibName == null) {
							String header = "ChatCreator servlet. " ;
							ChatError err = new ChatError(req,res,header, 80) ;//obs kolla om rätt nr
							return ;
						}
						// Lets find the selected template in the database and get its id
						// if not found, -1 will be returned
						String sqlQ = "C_GetTemplateIdFromName '" + newLibName + "'" ;//GetTemplateIdFromName
						String templateId = rmi.execSqlProcedureStr(chatPoolServer, sqlQ) ;
						if(templateId.equalsIgnoreCase("-1")) {
							String header = "ChatCreator servlet. " ;
							ChatError err = new ChatError(req,res,header,81) ;
							return ;
						}
						// Ok, lets update the chat with this new templateset.
						//but first lets delete the old one.
						String delString = "C_deleteChatTemplateset "+ metaId;
						rmi.execSqlUpdateProcedure(chatPoolServer, delString) ;
						
						String updateSql = "C_SetNewTemplateLib " + metaId ;//SetTemplateLib
						updateSql += ", '" + newLibName + "'" ;
						rmi.execSqlUpdateProcedure(chatPoolServer, updateSql) ;
				}
				if (req.getParameter("UPLOAD_CHAT")!=null){
					log("UPLOAD_CHAT");
					//ok lets handle the upload of templates and images
					String folderName = req.getParameter("TEMPLATE_NAME");
					String uploadType = req.getParameter("UPLOAD_TYPE");
					//log(folderName +" "+uploadType+" "+metaId);
					if (folderName == null || uploadType == null ) {
						return;
					}	
					Vector tags = new Vector();				
					tags.add("#META_ID#"); 		tags.add( metaId+"" ) ;
					tags.add("#UPLOAD_TYPE#");	tags.add(uploadType);
					tags.add("#FOLDER_NAME#");	tags.add(folderName);
					//sendHtml(req,res,vm, ADMIN_TEMPLATES_TEMPLATE_2) ;
					sendHtml(req,res,tags, ADMIN_TEMPLATES_TEMPLATE_2, null) ;
					return;
				}				
			}
			//check if template adminpage is wanted
			if (req.getParameter("adminTemplates")!=null){
				log("adminTemplates");
				//ok now lets get the template set name
				String templateSetName = rmi.execSqlProcedureStr(chatPoolServer, "C_GetTemplateLib '"+ metaId  + "' ");
				if (templateSetName == null) {
					templateSetName="";
				}
				//ok lets get all the template set there is
				String[] templateLibs =  rmi.execSqlProcedure(chatPoolServer, "C_GetAllTemplateLibs");
				Vector vect = new Vector();
				if (templateLibs != null){
					vect = super.convert2Vector(templateLibs);
				}
				Vector tags = new Vector();				
				tags.add("#TEMPLATE_LIST#");		tags.add(createOptionCode(templateSetName, vect) );
				tags.add("#CURRENT_TEMPLATE_SET#");	tags.add(templateSetName ) ;
				sendHtml(req,res,tags, ADMIN_TEMPLATES_TEMPLATE,null) ;
				return;
				
			}
			
				
		}//end if(action.equalsIgnoreCase("ADD_CHAT"))
		log("default köret");
		sendHtml(req,res,createTaggs(req, myChat),HTML_TEMPLATE,myChat);
		return ;	
	
		//slut peter jobbar		

	} // End POST


	/**
	The GET method creates the html page when this side has been
	redirected from somewhere else.
	**/
	//laddar admin sida osv
	public void doGet(HttpServletRequest req, HttpServletResponse res)throws ServletException, IOException{
		log("startar do get");
		// Lets validate the session, e.g has the user logged in to Janus?
		if (super.checkSession(req,res) == false)	return ;
		HttpSession session = req.getSession(false);

		// Lets get an user object
		imcode.server.User user = super.getUserObj(req,res) ;
		if(user == null) return ;

		if ( !isUserAuthorized( req, res, user ) ){
			return;
		}

		// Lets get serverinformation
		String host = req.getHeader("Host") ;
		String imcServer = Utility.getDomainPref("userserver",host) ;
		String chatPoolServer = Utility.getDomainPref("chat_server",host) ;

		//RmiConf rmi = new RmiConf(user) ;

		String action = req.getParameter("action") ;
		if(action == null){
			action = (String)req.getAttribute("action");
			if(action == null){
				action = "" ;
				String header = "ChatCreator servlet. " ;
				ChatError err = new ChatError(req,res,header,3) ;
				log(header + err.getErrorMsg()) ;
				return ;
			}
		}
		RmiConf rmi = new RmiConf(user);

		// ********* Create NEW Chat *********************************************************
		if(action.equalsIgnoreCase("NEW")){
			log("NEW");
			//vi måste hämta allt som behövs från databasen och sedan fixa till mallen
				
			//skapa en temp chat
			int meta_id = Integer.parseInt((String)session.getAttribute("Chat.meta_id"));
			Chat myChat = createChat(req, user, meta_id);
			session.setAttribute("myChat",myChat);
			// Lets build the Responsepage to the loginpage						
			Vector vect = createTaggs(req, myChat);
			sendHtml(req,res,vect,HTML_TEMPLATE,myChat);
			return ;
		}
		
		String templateAdmin = req.getParameter("ADMIN_TEMPLATES");
		if (templateAdmin != null)
		{//ok we have done upload template or image lets get back to the adminpage
			this.doPost(req,res);
			return;
		}

		if (action.equalsIgnoreCase("admin_chat")){
			log("action =  admin_chat");

			//check which chat we have
			String chatName = req.getParameter("chatName");
			log("ChatName: " + chatName);
			Vector tags = new Vector();	
			tags.add("#chatName#"); tags.add( chatName ) ;

			String metaId = (String)session.getAttribute("Chat.meta_id");
			log("MetaId: " + metaId);
			
			ServletContext myContext = getServletContext();
			Chat myChat = (Chat)myContext.getAttribute("theChat"+metaId);

			log("Chat: " + myChat);
			
			Vector vect = createTaggs(req, myChat);
			sendHtml(req,res,vect,HTML_TEMPLATE,myChat);
			return ;

		}

	} // End doGet
	
	public String getTemplateButtonHtml(HttpServletRequest req,String metaId) throws ServletException, IOException {
		String host = req.getHeader("Host") ;
		String imcServer = Utility.getDomainPref("userserver",host) ;
		String chatserver = Utility.getDomainPref("chat_server",host) ;
		return IMCServiceRMI.parseExternalDoc(imcServer,null, HTML_TEMPLATES_BUTTON , "se", "103", getTemplateLibName(chatserver,metaId));
	}
	

	//peter keep
	public Vector createTaggs(HttpServletRequest req,Chat chat)throws ServletException, IOException{
				
		Vector bv = new Vector();bv.add("1");bv.add("2");bv.add("3");					
		Vector taggs = new Vector();
		taggs.add("#msgTypes#");	taggs.add(createOptionCode("säger till",chat.getMsgTypes() ) ) ;
		taggs.add("#authorized#");	taggs.add(createOptionCode(chat.getSelectedAuto(),chat.getAuthorizations() ) ) ;
		taggs.add("#roomList#");	taggs.add(createOptionCode("",chat.getAllChatGroupsIdAndNameV()));
		//taggs.add("#chatName#");	taggs.add(chat.getChatName());
		taggs.add("#chatRoom#");	taggs.add("");
		taggs.add("#msgType#");		taggs.add("");
		taggs.add("#updateTime#");	taggs.add(createOptionCode(chat.getupdateTime()+"",createUpdateTimeV()));
		taggs.add("#reload#");		taggs.add(createRadioButton("reload",bv,chat.getreload()+"" ) );
		taggs.add("#inOut#");		taggs.add(createRadioButton("inOut",bv,chat.getinOut()+"" ) );
		taggs.add("#private#");		taggs.add(createRadioButton("private",bv,chat.getprivate()+"" ) );
		taggs.add("#public#");		taggs.add(createRadioButton("public",bv,chat.getpublic()+"" ) );
		taggs.add("#dateTime#");	taggs.add(createRadioButton("dateTime",bv,chat.getdateTime()+"" ) );
		taggs.add("#font#");		taggs.add(createRadioButton("font",bv,chat.getfont()+"" ) );
		
		taggs.add("#templates#");	taggs.add(getTemplateButtonHtml(req,chat.getChatId()+""));	
		
		return taggs;
	}
	
	private Vector createUpdateTimeV() {
		Vector vect = new Vector();
		for(int i=10; i<100;i+=10) {
			vect.add(i+"");
			vect.add(i+"");
		}
		return vect;
	}
	
	//peter keep
	public void init(ServletConfig config) throws ServletException{
		super.init(config);	
		log("init");
	} // End of INIT

	//peter keep
	public void log( String str){
		super.log("ChatCreator: " + str) ;
	}


} // End class



/*
		
		// ********* If a NEW CHAT is created  ********
		if(action.equalsIgnoreCase("ADD_CHAT"))
		{
			//log("action = ADD_CHAT") ;
		
			//*************** Get all New Chatparameters ********************

			//get the msgTypes
			Vector msgTypesV ;
			if( (Vector)session.getAttribute("msgTypesV")==null ){
				String[][] msgTypes = rmi.execProcedureMulti(chatPoolServer, "C_GetTheMsgTypesBase");
				msgTypesV = super.convert2Vector(msgTypes);
				session.setAttribute("msgTypesV",msgTypesV);
			}
			msgTypesV = (Vector)session.getAttribute("msgTypesV");

			//get the authorization types 
			Vector autTypeV ;
			if( (Vector)session.getAttribute("autTypesV")==null ){
				String[][] autTypes = rmi.execProcedureMulti(chatPoolServer, "C_GetAuthorizationTypes");
				autTypeV = super.convert2Vector(autTypes);
				session.setAttribute("autTypesV",autTypeV);
			}
			autTypeV = (Vector)session.getAttribute("autTypesV");
	   		
			//lets get the selected autotypes
			String[] selAuto = (req.getParameterValues("authorized")==null) ? new String[0] : (req.getParameterValues("authorized"));
			Vector selAutoV = super.convert2Vector(selAuto);			

			//get existing rooms
			Vector roomsV = ( (Vector)session.getAttribute("roomList")==null ) ? new Vector() : (Vector)session.getAttribute("roomList");

			//get existing new msgTypes
			Vector newMsgTypeV = ( (Vector)session.getAttribute("newMsgTypes")==null ) ? new Vector() : (Vector)session.getAttribute("newMsgTypes");


			//****************If newRoom or newMsgTypebutton is pressed:************************
		
			if ( req.getParameter("addRoom") != null || req.getParameter("addMsgType") != null)
			{
				
				log("addRoom or addMsgType" );
			
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
						session.setAttribute("roomList",roomsV);
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
						session.setAttribute("newMsgTypes",newMsgTypeV);
						session.setAttribute("msgTypesV",msgTypesV);
					}
				}//end while
				
				//ok lets set up the page
				String updateTime = chatParams.getProperty("updateTime");
				//log("Updatetime: 1 " + updateTime);
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
			//	sendHtml(req,res,vm, HTML_TEMPLATE) ;				
				sendHtml(req,res,new Vector(), HTML_TEMPLATE, null) ;	
				//log("end addRoom or addMsgType");
				return ;
			}//end adding new msgTypes or rooms
		   
			//get the new metaId
			String metaId = params.getProperty("META_ID") ;
			//log("metaid: "+ metaId);

			//check that its really a new metaId
			String foundMetaId = rmi.execSqlProcedureStr(chatPoolServer, "C_MetaIdExists " + metaId) ;

			if( !foundMetaId.equals("1") ) {
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
			session.setAttribute("chatId",metaId);
			//session.setAttribute("roomList",roomsV);
			//session.setAttribute("msgTypes",msgTypesV);
			//session.setAttribute("chatParams",chatParams);			
			
			Enumeration e = chatParams.propertyNames();
			while (e.hasMoreElements())
			{
				String temp = (String)e.nextElement();
				//log( "testParams: " + temp + " Value: " + chatParams.getProperty( temp) );
			} 

			//ok lets create the chat object and its rooms
			String[] types = rmi.execSqlProcedure(chatPoolServer, "C_GetChatAutoTypes '"+ metaId  + "' ");
			
//			Chat theChat = new Chat( Integer.parseInt(metaId), msgTypesV, chatParams, types );
			Chat theChat = new Chat();
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
	/*	
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
					//sendHtml(req,res,vm, ADMIN_TEMPLATES_TEMPLATE_2) ;
					sendHtml(req,res,new Vector(), ADMIN_TEMPLATES_TEMPLATE_2, null) ;
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
				//sendHtml(req,res,vm, ADMIN_TEMPLATES_TEMPLATE) ;
				sendHtml(req,res,new Vector(), ADMIN_TEMPLATES_TEMPLATE,null) ;
				return;
				
			}
			
			
			
//FIX ugly		//lets get the chatMember
			//check which chat we have
			String chatName = req.getParameter("chatName");
			if(chatName==null)chatName ="";
			//log("ChatName: " + chatName);
			vm.addProperty("chatName", chatName ) ;

			String metaId = (String)session.getAttribute("Chat.meta_id");
			//log("MetaId: " + metaId);
			ChatMember myMember = (ChatMember) session.getAttribute("theChatMember");
			if (myMember == null)
			{
				log("chatMembern was null so return");
				return;
			}
//			Chat myChat = myMember.getMyParent();
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

			Vector addGroups = (Vector)session.getAttribute("NewRooms") == null ? new Vector() : (Vector)session.getAttribute("NewRooms") ;//sessionen
			Vector delGroups = (Vector)session.getAttribute("DelRooms") == null ? new Vector() : (Vector)session.getAttribute("DelRooms") ;//sessionen

			if ( req.getParameter("addRoom") != null )
			{
				//log("*** start addRoom ***");
				//lets get the room to add
				String newRoom = req.getParameter("chatRoom");
				addGroups.add(newRoom);
				addGroups.add(newRoom);
				
				session.setAttribute("NewRooms",addGroups);
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

				session.setAttribute("DelRooms",delGroups);
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
			Vector addTypes = (Vector)session.getAttribute("NewTypes") == null ? new Vector() : (Vector)session.getAttribute("NewTypes") ;
			Vector delTypes = (Vector)session.getAttribute("DelTypes") == null ? new Vector() : (Vector)session.getAttribute("DelTypes") ;
			
			Vector autTypeV = (Vector) session.getAttribute("Chat_autTypeV");
			String[] selAuto = (req.getParameterValues("authorized")==null) ? new String[0] : (req.getParameterValues("authorized"));
			Vector selAutoV = super.convert2Vector(selAuto);
			
			
			//add new msgType
			if ( req.getParameter("addMsgType") != null )
			{
				String newMsgType = req.getParameter("msgType");

				//lägg till ny typ
				addTypes.add(newMsgType);
				addTypes.add(newMsgType);

				session.setAttribute("NewTypes",addTypes);
			} 

			//*************** delete msgType **********
			if ( req.getParameter("removeMsgType") != null )
			{
				//vilket rum ska tas bort
				String delType = req.getParameter("msgTypes");

				//deleta typen i listan
				delTypes.add(delType);
				delTypes.add(delType);

				session.setAttribute("DelTypes",delTypes);

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

			//sendHtml(req,res,vm, ADMIN_TEMPLATE) ;
			sendHtml(req,res,new Vector(), ADMIN_TEMPLATES_TEMPLATE,null) ;
			//###################################################################			
			if (req.getParameter("okChat") != null )
			{
				//log("*** start okChat ***");
				//spara värden till databas 

				
				//ta bort alla rum tillhörande den här chatten
				//String delete = "C_DeleteConnections " + metaId;
				//rmi.execSqlUpdateProcedure(chatPoolServer , delete );

				//lets get all current connected rooms 
				Vector currGroupsV 		= (Vector)session.getAttribute("chat_V_room");
				Vector currMsgTypesV 	= (Vector)session.getAttribute("chat_V_msgTypes");
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
				session.removeAttribute("NewRooms");
				//lets see if there is any to delete
				Vector dellV = (Vector) session.getAttribute("DelRooms");
				session.removeAttribute("DelRooms");
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
				
				session.removeAttribute("NewTypes");
				//lets see if there is any to delete
				Vector dellMsgV = (Vector) session.getAttribute("DelTypes");
				session.removeAttribute("DelTypes");
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
//				myChat.setAuthorizations(types);
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
					//	myChat.createNewChatGroup(Integer.parseInt(roomIdNr[i][0]), roomIdNr[i][1]);
					}
				}				
				//ok lets get the propertys from db
				String getParamsSql = "C_GetChatParameters "+metaId;
				String[] param = rmi.execSqlProcedure(chatPoolServer, "C_GetChatParameters '"+ metaId  + "' ");	
						
				chatParams = super.getChatParams(param);
				String[] chatData = rmi.execSqlProcedure(chatPoolServer, "C_GetChatNameAndPerm '"+ metaId  + "' ");	
				chatParams.setProperty("chatName",chatData[0]);
				chatParams.setProperty("permission",chatData[1]);
				
//				myChat.setParams(chatParams);				
				String sqlQ = "C_GetTheMsgTypes " + metaId;
				String[][] msgTypes = rmi.execProcedureMulti(chatPoolServer,sqlQ);
				Vector theMsgTypesV = super.convert2Vector(msgTypes);
				myChat.setMsgTypes(theMsgTypesV);
				
//				myMember.setProperties(myChat.getChatParameters());
				
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

*/
