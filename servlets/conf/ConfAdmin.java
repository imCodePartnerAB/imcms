import imcode.server.* ;
import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import imcode.external.diverse.* ;
import java.rmi.* ;
import java.rmi.registry.* ;
import imcode.util.*;

public class ConfAdmin extends Conference {
    private final static String CVS_REV = "$Revision$" ;
    private final static String CVS_DATE = "$Date$" ;

    private final static String FORUM_UNADMIN_LINK_TEMPLATE = "Conf_Forum_Unadmin_Link.htm";
    private final static String FORUM_TEMPLATE1_UNADMIN_LINK_TEMPLATE = "Conf_Forum_Template1_Unadmin_Link.htm";
    private final static String FORUM_TEMPLATE2_UNADMIN_LINK_TEMPLATE = "Conf_Forum_Template2_Unadmin_Link.htm";
    private final static String DISC_UNADMIN_LINK_TEMPLATE = "Conf_Disc_Unadmin_Link.htm";
    private final static String REPLY_UNADMIN_LINK_TEMPLATE = "Conf_Reply_Unadmin_Link.htm";

    /**
       DoPost
    **/
    public void doPost(HttpServletRequest req, HttpServletResponse res)
	throws ServletException, IOException {

	// Lets validate the session, e.g has the user logged in to Janus?
	if (super.checkSession(req,res) == false)	return ;

	// Lets get the user object
	imcode.server.User user = super.getUserObj(req,res) ;
	if(user == null) return ;

	if ( !isUserAuthorized( req, res, user ) ) {
	    return;
	}

	// Lets get the standard SESSION parameters
	Properties params = this.getStandardParameters(req) ;

	// Lets get serverinformation
	IMCServiceInterface imcref = IMCServiceRMI.getIMCServiceInterface(req) ;
	IMCPoolInterface confref = IMCServiceRMI.getConfIMCPoolInterface(req) ;

	// Lets check that the user is an administrator
	if( super.getAdminRights(imcref, params.getProperty("META_ID"), user) == false ) {
	    String header = "ConfAdmin servlet. " ;
	    String msg = params.toString() ;
	    ConfError err = new ConfError(req,res,header,6) ;
	    return ;
	}

	// ********* ADD SELF_REGISTERED USERS ********
	if (req.getParameter("ADD_SELF_REG_ROLE") != null) {
	    if (super.checkParameters(req, res, params) == false ) {
		return ;
	    }

	    // Lets check if the user is a superadmin
	    if( Administrator.checkAdminRights(req,res)== false ) {
		ConfError err = new ConfError(req,res,"ConfAdmin",64) ;
		return ;
	    }

	    // Lets get the role id the user wants to add
	    String selfRegRoleId = req.getParameter("ALL_SELF_REGISTER_ROLES")  ;
	    if (selfRegRoleId == null) {
		String header = "ConfAdmin servlet. " ;
		String msg = params.toString() ;
		ConfError err = new ConfError(req,res,header, 86) ;
		return ;
	    }

	    String roleName = imcref.sqlProcedureStr("RoleGetName " + selfRegRoleId) ;
	    String sqlSproc = "A_SelfRegRoles_AddNew " + params.getProperty("META_ID") ;
	    sqlSproc += ", " + selfRegRoleId + ", '" + roleName + "'" ;

	    confref.sqlUpdateProcedure(sqlSproc) ;

	    res.sendRedirect("ConfAdmin?ADMIN_TYPE=SELF_REGISTER") ;
	    return ;
	}

	// ********* DELETE A SELF_REGISTERED ROLE ********
	if (req.getParameter("DEL_SELF_REG_ROLE") != null) {
	    if (super.checkParameters(req, res, params) == false ) {
		return ;
	    }

	    // Lets check if the user is a superadmin
	    if( Administrator.checkAdminRights(req,res)== false ) {
		ConfError err = new ConfError(req,res,"ConfAdmin",64) ;
		return ;
	    }


	    // Lets get the role id the user wants to delete
	    String selfRegRoleId = req.getParameter("CURR_SELF_REGISTER_ROLES")  ;
	    if (selfRegRoleId == null) {
		String header = "ConfAdmin servlet. " ;
		String msg = params.toString() ;
		ConfError err = new ConfError(req,res,header, 87) ;
		return ;
	    }

	    String roleName = imcref.sqlProcedureStr("RoleGetName " + selfRegRoleId) ;
	    String sqlSproc = "A_SelfRegRoles_Delete " + params.getProperty("META_ID") ;
	    sqlSproc += ", " + selfRegRoleId ;
	    confref.sqlUpdateProcedure(sqlSproc) ;
	    res.sendRedirect("ConfAdmin?ADMIN_TYPE=SELF_REGISTER") ;
	    return ;
	}

	// ********* REGISTER NEW TEMPLATE ********
	if (req.getParameter("REGISTER_TEMPLATE_LIB") != null) {
	    log("Nu lägger vi till ett nytt set") ;
	    if (super.checkParameters(req, res, params) == false ) {
		return ;
	    }

	    // Lets get the new library name and validate it
	    String newLibName = req.getParameter("TEMPLATE_LIB_NAME")  ;
	    if (newLibName == null) {
		String header = "ConfAdmin servlet. " ;
		String msg = params.toString() ;
		ConfError err = new ConfError(req,res,header, 80) ;
		return ;
	    }
	    newLibName = super.verifySqlText(newLibName) ;

	    // Lets check if we already have a templateset with that name
	    String sql = "A_FindTemplateLib " + newLibName ;
	    String libNameExists = confref.sqlProcedureStr(sql) ;
	    if( !libNameExists.equalsIgnoreCase("-1") ) {
		String header = "ConfAdmin servlet. " ;
		String msg = params.toString() ;
		ConfError err = new ConfError(req,res,header, 84) ;
		return ;
	    }

	    String sqlQ = "A_AddTemplateLib '" + newLibName + "'" ;
	    confref.sqlUpdateProcedure(sqlQ) ;

	    // Lets copy the original folders to the new foldernames
	    String metaId = super.getMetaId(req) ;
	    FileManager fileObj = new FileManager() ;

	    File templateSrc = new File(imcref.getExternalTemplateFolder(Integer.parseInt(metaId)), "original") ;
	    File imageSrc = new File(RmiConf.getImagePathForExternalDocument(imcref, Integer.parseInt(metaId)), "original") ;
	    File templateTarget = new File(imcref.getExternalTemplateFolder(Integer.parseInt(metaId)), newLibName) ;
	    File imageTarget = new File(RmiConf.getImagePathForExternalDocument(imcref, Integer.parseInt(metaId)), newLibName) ;

	    fileObj.copyDirectory(templateSrc, templateTarget) ;
	    fileObj.copyDirectory(imageSrc, imageTarget) ;

	    res.sendRedirect("ConfAdmin?ADMIN_TYPE=META") ;
	    return ;
	}

	// ********* PREPARE ADMIN TEMPLATES ********
	if (req.getParameter("UPLOAD_CONF") != null) {
	    String libName = (req.getParameter("TEMPLATE_NAME")==null) ? "" : (req.getParameter("TEMPLATE_NAME")) ;
	    String uploadType = (req.getParameter("UPLOAD_TYPE")==null) ? "" : (req.getParameter("UPLOAD_TYPE")) ;

	    params.setProperty("TEMPLATE_NAME" ,libName) ;
	    params.setProperty("UPLOAD_TYPE" ,uploadType) ;
	    if (super.checkParameters(req, res, params) == false ) {
		return ;
	    }

	    String url = "ConfAdmin?ADMIN_TYPE=META" ;
	    url += "&setname=" + libName + "&UPLOAD_TYPE=" + uploadType ;
	    res.sendRedirect(url) ;
	    return ;
	}

	// ********* SET TEMPLATE LIB FOR A CONFERENCE  ********
	if (req.getParameter("SET_TEMPLATE_LIB") != null) {
	    log("Lets set a new template set for the conference") ;
	    if (super.checkParameters(req, res, params) == false ) {
		return ;
	    }

	    // Lets get the new library name and validate it
	    String newLibName = req.getParameter("TEMPLATE_ID")  ;
	    if (newLibName == null) {
		String header = "ConfAdmin servlet. " ;
		String msg = params.toString() ;
		ConfError err = new ConfError(req,res,header, 80) ;
		return ;
	    }

	    // Lets find the selected template in the database and get its id
	    // if not found, -1 will be returned
	    String sqlQ = "A_GetTemplateIdFromName '" + newLibName + "'" ;
	    String templateId = confref.sqlProcedureStr(sqlQ) ;
	    if(templateId.equalsIgnoreCase("-1")) {
		String header = "ConfAdmin servlet. " ;
		String msg = params.toString() ;
		ConfError err = new ConfError(req,res,header,81) ;
		return ;
	    }
	    // Ok, lets update the conference with this new templateset.
	    String updateSql = "A_SetTemplateLib " + params.getProperty("META_ID") + ", " + templateId ;
	    confref.sqlUpdateProcedure(updateSql) ;

	    res.sendRedirect("ConfAdmin?ADMIN_TYPE=META") ;
	    return ;
	} // SET TEMPLATE LIB

	// ********* DELETE REPLY ********
	if (req.getParameter("DELETE_REPLY") != null) {
	    log("Nu tar vi bort inlägg") ;
	    if (super.checkParameters(req, res, params) == false ) {
		return ;
	    }
	    // Lets get the discusssion id
	    String discId = params.getProperty("DISC_ID") ;

	    // Lets get all the replies id:s
	    String repliesIds[] = this.getDelReplyParameters(req) ;

	    // Lets delete all marked replies. Observe that the first one wont be deleted!
	    // if the user wants to delete the first one then he has to delete the discussion
	    if( repliesIds != null ) {
		for(int i = 0 ; i < repliesIds.length ; i++ ) {
		    String sqlQ = "A_DeleteReply " + discId + ", " + repliesIds[i] ;
		    confref.sqlUpdateProcedure(sqlQ) ;
		}
	    }
	    res.sendRedirect("ConfAdmin?ADMIN_TYPE=REPLY") ;
	    return ;
	}

	// ********* RESAVE REPLY ********
	if (req.getParameter("RESAVE_REPLY") != null) {
	    if (super.checkParameters(req, res, params) == false ) {
		return ;
	    }
	    // Lets get the discusssion id
	    String discId = params.getProperty("DISC_ID") ;

	    // Lets get all the replies id:s
	    String repliesIds[] = this.getDelReplyParameters(req) ;

	    // Lets get the seleted textboxes headers and texts values.
	    if( repliesIds != null ) {
		for(int i = 0 ; i < repliesIds.length ; i++ ) {
		    String newText = req.getParameter("TEXT_BOX_" + repliesIds[i]) ;
		    if( newText.equals("") || newText == null) {
			ConfError err = new ConfError() ;
			newText = err.getErrorMessage(req, 70) ;
		    }

		    String newHeader = req.getParameter("REPLY_HEADER_" + repliesIds[i]) ;
		    if( newHeader.equals("") || newHeader == null) {
			ConfError err = new ConfError() ;
			newHeader = err.getErrorMessage(req, 71) ;
		    }

		    // Lets validate the new text for the sql question
		    newHeader = super.verifySqlText(newHeader) ;
		    newText = super.verifySqlText(newText) ;

		    String sqlQ = "A_UpdateReply " + repliesIds[i] + ", '" ;
		    sqlQ += newHeader + "', '" + newText + "'" ;
		    confref.sqlUpdateProcedure(sqlQ) ;

		}
	    }
	    res.sendRedirect("ConfAdmin?ADMIN_TYPE=REPLY") ;
	    return ;
	}

	// ********* DELETE DISCUSSION ********
	if (req.getParameter("DELETE_DISCUSSION") != null) {

	    if (super.checkParameters(req, res, params) == false ) {
		return ;
	    }

	    // Lets get all the discussion id:s
	    String discIds[] = this.getDelDiscParameters(req) ;

	    // Lets delete all the discussion and all the replies in that discussion.
	    if( discIds != null ) {
		for(int i = 0 ; i < discIds.length ; i++ ) {
		    String sqlQ = "A_DeleteDiscussion " + discIds[i] ;
		    confref.sqlUpdateProcedure(sqlQ) ;
		}
	    }
	    res.sendRedirect("ConfAdmin?ADMIN_TYPE=DISCUSSION") ;
	    return ;
	}

	// ********* DELETE FORUM ********
	if (req.getParameter("DELETE_FORUM") != null) {
	    log("Nu tar vi bort ett forum") ;
	    params = this.getDelForumParameters(req, params) ;
	    if (super.checkParameters(req, res, params) == false) {
		return ;
	    }

	    // Lets get the forum_id and set our session object before updating
	    String aForumId = params.getProperty("FORUM_ID") ;

	    // Lets get all discussions for that forum and delete those before deleting the forum
	    // GetAllDiscsInForum @aForumId int
	    String discs[] = confref.sqlProcedure("A_GetAllDiscsInForum " + aForumId) ;
	    if( discs != null) {
		for( int i = 0 ; i < discs.length; i++ ) {
		    String sqlQ = "A_DeleteDiscussion " + discs[i] ;
		    confref.sqlUpdateProcedure(sqlQ) ;
		}
	    }

	    // DeleteForum @aForumId int
	    String sqlQ = "A_DeleteForum " + params.getProperty("FORUM_ID") ;
	    confref.sqlUpdateProcedure(sqlQ) ;
	    this.doGet(req, res) ;
	    return ;
	}

	// ********* ADD FORUM ********
	if (req.getParameter("ADD_FORUM") != null) {
	    log("Lets add a forum") ;
	    // Lets get addForum parameters
	    params = this.getAddForumParameters(req, params) ;
	    if (super.checkParameters(req, res, params) == false) {
		return ;
	    }

	    // Lets verify the parameters for the sql questions.
	    params = super.verifyForSql(params) ;

	    // Lets check if a forum with that name exists
	    String findSql = "A_FindForumName " + params.getProperty("META_ID") + ", '" ;
	    findSql += params.get("NEW_FORUM_NAME") + "'" ;

	    String foundIt = confref.sqlProcedureStr(findSql) ;

	    if( !foundIt.equalsIgnoreCase("-1") ) {
		String header = "ConfAdmin servlet. " ;
		String msg = params.toString() ;
		ConfError err = new ConfError(req,res,header,85) ;
		return ;
	    }

	    //	AddNewForum @meta_id int, @forum_name varchar(255), @archive_mode char, @archive_time int
	    String sqlAddQ = "A_AddNewForum " + params.getProperty("META_ID") + ", '" ;
	    sqlAddQ += params.getProperty("NEW_FORUM_NAME") + "', 'A', 30" ;

	    confref.sqlUpdateProcedure(sqlAddQ) ;
	    this.doGet(req, res) ;
	    return ;
	}

	// ********* CHANGE FORUM NAME ********
	if (req.getParameter("CHANGE_FORUM_NAME") != null) {

	    // Lets get addForum parameters
	    params = this.getRenameForumParameters(req, params) ;
	    if (super.checkParameters(req, res, params) == false) {
		return ;
	    }

	    // Lets verify the parameters for the sql questions.
	    params = super.verifyForSql(params) ;

	    String sqlAddQ = "A_RenameForum " + params.getProperty("FORUM_ID") + ", '" ;
	    sqlAddQ += params.getProperty("NEW_FORUM_NAME") + "'" ;

	    confref.sqlUpdateProcedure(sqlAddQ) ;
	    this.doGet(req, res) ;
	    return ;
	}
	// ********* SET SHOW_DISCUSSION_COUNTER ********
	if (req.getParameter("SHOW_DISCUSSION_NBR") != null) {
	    log("Lets set the nbr of discussions to show") ;
	    // Lets get addForum parameters
	    params = this.getShowDiscussionNbrParameters(req, params) ;
	    if (super.checkParameters(req, res, params) == false) {
		return ;
	    }

	    String sqlAddQ = "A_SetNbrOfDiscsToShow " + params.getProperty("FORUM_ID") + ", " ;
	    sqlAddQ += params.getProperty("NBR_OF_DISCS_TO_SHOW") ;
	    log("SetShowDiscussionNbrSql: " + sqlAddQ) ;
	    confref.sqlUpdateProcedure(sqlAddQ) ;
	    res.sendRedirect("ConfAdmin?ADMIN_TYPE=FORUM") ;
	    return ;
	}

    } // DoPost


    /**
       doGet
    **/
    public void doGet(HttpServletRequest req, HttpServletResponse res)
	throws ServletException, IOException {

	// Lets validate the session, e.g has the user logged in to Janus?
	if (super.checkSession(req,res) == false)	return ;

	// Lets get the standard SESSION parameters
	Properties params = this.getStandardParameters(req) ;

	if (super.checkParameters(req, res, params) == false) {
	    return ;
	}

	// Lets get the user object
	imcode.server.User user = super.getUserObj(req,res) ;
	if(user == null) return ;

	if ( !isUserAuthorized( req, res, user ) ) {
	    return;
	}

	// Lets get serverinformation
	IMCServiceInterface imcref = IMCServiceRMI.getIMCServiceInterface(req) ;
	IMCPoolInterface confref = IMCServiceRMI.getConfIMCPoolInterface(req) ;

	// Lets check that the user is an administrator
	if( super.getAdminRights(imcref, params.getProperty("META_ID"), user) == false ) {
	    String header = "ConfAdmin servlet. " ;
	    String msg = params.toString() ;
	    ConfError err = new ConfError(req,res,header,6) ;
	    return ;
	}

	// Lets get the admintype from the requestobject
	String adminWhat = (req.getParameter("ADMIN_TYPE")==null) ? "" : (req.getParameter("ADMIN_TYPE")) ;

	VariableManager vm = new VariableManager();
	String htmlFile = "";

	// *********** ADMIN SELF_REGISTER *************
	// Lets build the selfregister page to the user
	if (adminWhat.equalsIgnoreCase("SELF_REGISTER") ) {

	    // Lets check if the user is a superadmin
	    if( Administrator.checkAdminRights(req,res)== false ) {
		ConfError err = new ConfError(req,res,"ConfAdmin",64) ;
		return ;
	    }


	    // Lets get the current self register roles from DB
	    String sqlSproc = "A_SelfRegRoles_GetAll " + params.getProperty("META_ID") ;
	    String sqlAnswer[] = confref.sqlProcedure(sqlSproc ) ;
	    Vector selfRegV = super.convert2Vector(sqlAnswer) ;
	    String selfRegList = Html.createHtmlCode("ID_OPTION", "", selfRegV ) ;

	    // Lets ALL avaible self_register roles from DB
	    String langPrefix = user.getLangPrefix() ;

	    String sqlSproc2 = "RoleGetConferenceAllowed '" + langPrefix + "'";
	    String sqlAnswer2[] = imcref.sqlProcedure( sqlSproc2 ) ;
	    Vector allSelfRegV = super.convert2Vector(sqlAnswer2) ;
	    String allSelfRegList = Html.createHtmlCode("ID_OPTION", "", allSelfRegV ) ;

	    // Lets build the Responsepage

	    vm.addProperty("SELFREG_ROLES_LIST", selfRegList ) ;
	    vm.addProperty("ALL_SELFREG_ROLES_LIST", allSelfRegList ) ;
	    vm.addProperty("UNADMIN_LINK_HTML", this.FORUM_TEMPLATE2_UNADMIN_LINK_TEMPLATE );

	    htmlFile = "Conf_Admin_Template3.htm";
	    //return ;
	}

	// *********** ADMIN META *************
	if (adminWhat.equalsIgnoreCase("META") ) {

	    // Lets check which page we should show, the standard meta page or
	    // the upload image/template page

	    String setName = req.getParameter("setname") ;
	    if(	setName != null ) {

		String uploadType = req.getParameter("UPLOAD_TYPE") ;
		if( uploadType == null ) {
		    String header = "ConfAdmin servlet. " ;
		    String msg = params.toString() ;
		    ConfError err = new ConfError(req,res,header,83) ;
		    return ;
		}

		// Ok, Lets get root path to the external type
		String metaId= params.getProperty("META_ID") ;

		vm.addProperty("UPLOAD_TYPE", uploadType );
		vm.addProperty("FOLDER_NAME", setName );
		vm.addProperty("META_ID", metaId);
		vm.addProperty("UNADMIN_LINK_HTML", this.FORUM_TEMPLATE2_UNADMIN_LINK_TEMPLATE );

		htmlFile = "Conf_Admin_Template2.htm" ;

		// Ok, were gonna show our standard meta page
	    }
	    else {

		// Lets get the current template set for this metaid
		String sqlStoredProc = "A_GetTemplateLib " + params.getProperty("META_ID") ;
		String currTemplateSet = confref.sqlProcedureStr(sqlStoredProc ) ;

		// Lets get all current template sets
		String sqlAll = "A_GetAllTemplateLibs" ;
		String sqlAnswer[] = confref.sqlProcedure(sqlAll) ;
		Vector templateV = super.convert2Vector(sqlAnswer) ;

		// Lets fill the select box	with forums
		String templateList = Html.createHtmlCode("ID_OPTION", "", templateV ) ;

		// Lets build the Responsepage
		//VariableManager vm = new VariableManager() ;
		vm.addProperty("TEMPLATE_LIST", templateList ) ;
		vm.addProperty("A_META_ID", params.getProperty("META_ID") ) ;
		vm.addProperty("CURRENT_TEMPLATE_SET", currTemplateSet ) ;
		vm.addProperty("UNADMIN_LINK_HTML", this.FORUM_TEMPLATE1_UNADMIN_LINK_TEMPLATE );

		htmlFile = "Conf_Admin_Template1.htm" ;
		//return ;
	    }
	}

	// *********** ADMIN FORUM *************
	if (adminWhat.equalsIgnoreCase("FORUM") ) {

	    // Lets get the information from DB
	    String sqlStoredProc = "A_GetAllForum " + params.getProperty("META_ID") ;
	    String sqlAnswer[] = confref.sqlProcedure(sqlStoredProc ) ;
	    Vector forumV = super.convert2Vector(sqlAnswer) ;

	    // Lets fill the select box with forums
	    String forumList = Html.createHtmlCode("ID_OPTION", "", forumV ) ;

	    // Lets get the name of the currently selected forum
	    String forumNameSql = "A_GetForumName " + params.getProperty("FORUM_ID") ;
	    String forumName = confref.sqlProcedureStr(forumNameSql) ;

	    // Lets get the name of the currently selected forum
	    String nbrOfDiscsToShowSql = "A_GetNbrOfDiscsToShow " + params.getProperty("FORUM_ID") ;
	    String nbrOfDiscsToShow = confref.sqlProcedureStr(nbrOfDiscsToShowSql) ;

	    // Lets get all the showDiscs values
	    String sqlAllDiscsSql = "A_GetAllNbrOfDiscsToShow " + params.getProperty("META_ID") ;
	    String sqlAllDiscs[] = confref.sqlProcedure(sqlAllDiscsSql) ;

	    Vector sqlAllDiscsV = new Vector() ;
	    if (sqlAllDiscs != null) {
		sqlAllDiscsV = super.convert2Vector(sqlAllDiscs) ;
	    }
	    String discToShowList = Html.createHtmlCode("ID_OPTION", "", sqlAllDiscsV ) ;

	    // Lets build the Responsepage
	    vm.addProperty("FORUM_LIST", forumList ) ;
	    vm.addProperty("NBR_OF_DISCS_TO_SHOW_LIST", discToShowList );
	    vm.addProperty("UNADMIN_LINK_HTML", this.FORUM_UNADMIN_LINK_TEMPLATE );
	    htmlFile = "Conf_Admin_Forum.htm" ;
	}

	// *********** ADMIN DISCUSSION *************
	if (adminWhat.equalsIgnoreCase("DISCUSSION") ) {
	    String adminDiscList = "Conf_Admin_Disc_List.htm" ;
	    log("OK, Administrera Discussions") ;

	    // Lets get parameters
	    String aMetaId = params.getProperty("META_ID") ;
	    String aForumId = params.getProperty("FORUM_ID") ;
	    String aLoginDate = params.getProperty("LAST_LOGIN_DATE") ;

	    // Lets get path to the imagefolder. http://dev.imcode.com/images/se/102/ConfDiscNew.gif
	    String imagePath = super.getExternalImageFolder(req) + "ConfDiscNew.gif" ;

	    // Lets get the part of an html page, wich will be parsed for every a Href reference
	    File aHrefHtmlFile = new File(super.getExternalTemplateFolder(req), adminDiscList) ;

	    // Lets get all New Discussions
	    String sqlStoredProc = "A_GetAllNewDiscussions " + aMetaId + ", " + aForumId +", '"+ aLoginDate + "'"  ;
	    String sqlAnswerNew[] = confref.sqlProcedureExt(sqlStoredProc) ;

	    // Lets get all Old Discussions
	    String sqlStoredProcOld = "A_GetAllOldDiscussions " + aMetaId + ", " + aForumId +", '"+ aLoginDate + "'"  ;
	    String sqlAnswerOld[] = confref.sqlProcedureExt(sqlStoredProcOld) ;

	    // Lets build our tags vector.
	    Vector tagsV = this.buildAdminTags() ;

	    // Lets preparse all NEW records
	    String allNewRecs  = "" ;
	    if( sqlAnswerNew != null ) {
		if( sqlAnswerNew.length > 0)
		    allNewRecs = discPreParse(req, sqlAnswerNew, tagsV, aHrefHtmlFile, imagePath, 1 ) ;
	    }
	    // Lets preparse all OLD records
	    String allOldRecs  = "" ;
	    if( sqlAnswerOld != null ) {
		if( sqlAnswerOld.length > 0)
		    allOldRecs = discPreParse(req, sqlAnswerOld, tagsV, aHrefHtmlFile, imagePath , 0) ;
	    }

	    // Lets build the Responsepage
	    vm.addProperty("NEW_A_HREF_LIST", allNewRecs  ) ;
	    vm.addProperty("OLD_A_HREF_LIST", allOldRecs  ) ;
	    vm.addProperty("UNADMIN_LINK_HTML", this.DISC_UNADMIN_LINK_TEMPLATE );

	    htmlFile = "Conf_Admin_Disc.htm" ;
	} // End admin discussion

	// *********** ADMIN REPLIES *************
	if (adminWhat.equalsIgnoreCase("REPLY") ) {
	    String adminReplyList = "Conf_Admin_Reply_List.htm" ;

	    // Lets get the users userId
	    String userId = ""+user.getUserId() ;

	    // Lets get the replylist from DB
	    String discId = params.getProperty("DISC_ID") ;

	    String sqlAnswer[] = confref.sqlProcedureExt("A_GetAllRepliesInDiscAdmin " + discId + ", " + userId) ;

	    // Lets get the users sortorder from DB
	    String metaId = params.getProperty("META_ID") ;
	    String sqlQ = "A_ConfUsersGetReplyOrderSel " + metaId + ", " + userId  ;

	    String sortOrderVal = (String) confref.sqlProcedureStr(sqlQ) ;
	    String checkBoxStr = "" ;

	    if( sortOrderVal.equalsIgnoreCase("1")) checkBoxStr = "checked" ;

	    // SYNTAX: date  first_name  last_name  headline   text reply_level
	    // Lets build our variable list
	    Vector tagsV = new Vector() ;
	    tagsV.add("#REPLY_DATE#") ;
	    tagsV.add("#FIRST_NAME#") ;
	    tagsV.add("#LAST_NAME#") ;
	    tagsV.add("#REPLY_HEADER#") ;
	    tagsV.add("#REPLY_TEXT#") ;
	    tagsV.add("#REPLY_LEVEL#") ;
	    tagsV.add("#REPLY_ID#") ;
	    tagsV.add("#REPLY_ID2#") ;
	    tagsV.add("#REPLY_ID3#") ;

	    // Lets get path to the imagefolder. http://dev.imcode.com/images/102/ConfDiscNew.gif
	    String imagePath = super.getExternalImageFolder(req) + "ConfExpert.gif" ;
	    // log("ImagePath: " + imagePath) ;

	    // Lets get the part of an html page, wich will be parsed for every a Href reference
	    File templateLib = super.getExternalTemplateFolder(req) ;
	    File aSnippetFile = new File(templateLib, adminReplyList) ;

	    // Lets preparse all records
	    String allRecs = " " ;
	    if (sqlAnswer != null) allRecs = replyPreParse(req, sqlAnswer, tagsV, aSnippetFile, imagePath) ;

	    // Lets build the Responsepage
	    //VariableManager vm = new VariableManager() ;
	    vm.addProperty("USER_SORT_ORDER", sortOrderVal) ;
	    vm.addProperty("CHECKBOX_STATE", checkBoxStr) ;
	    vm.addProperty("REPLIES_RECORDS", allRecs  ) ;
	    vm.addProperty("UNADMIN_LINK_HTML", this.REPLY_UNADMIN_LINK_TEMPLATE );
	    //return ;
	    htmlFile = "Conf_Admin_Reply.htm" ;
	} // End admin Reply

	this.sendHtml(req,res,vm, htmlFile ) ;

    } //DoGet


    // ****************** PARSE REPLIES FUNCTIONS ************************
    /**
       Parses the Extended array with the htmlcode, which will be parsed
       for all records in the array
    */
    public String replyPreParse (HttpServletRequest req, String[] DBArr, Vector tagsV,
				 File htmlCodeFile, String imagePath)  throws ServletException, IOException {

	String htmlStr = "" ;
	try {
	    // Lets get the nbr of cols
	    int nbrOfCols = Integer.parseInt(DBArr[0]) ;

	    // Lets build an tagsArray with the tags from the DBarr, if
	    // null was passed to us instead of a vector

	    if( tagsV == null) {
		tagsV = new Vector() ;
		for(int k = 1; k<nbrOfCols; k++) {
		    tagsV.add(DBArr[k]) ;
		    //	log("Counter: "+ k + " Tagvärde: " + DBArr[k] ) ;
		}
	    }

	    // Lets do for all records...
	    for(int i = nbrOfCols+1; i<DBArr.length; i += nbrOfCols) {
		String oneParsedRecordStr = "" ;
		Vector dataV = new Vector() ;

		// Lets do for one record... Get all fields for that record
		for(int j=i; j<i+nbrOfCols ; j++) {
		    dataV.add(DBArr[j]) ;
		    // log("VÄRDE: " + j + " : " +  DBArr[j]) ;
		} // End of one records for

		// Lets check if the user is some kind of "Master" eg. if he's
		// reply_level is equal to 1 and add the code returned to data.
		dataV.set(5, this.getReplyLevelCode(req, dataV, imagePath)) ;
		//	for(int m=0 ; m < dataV.size() ; m++) {
		//		log( (String)tagsV.get(m) + " :" + (String)dataV.get(m) ) ;
		//	}

		// Lets the id two more times, since our parser cant replace one tag
		// more than once
		log("Här är replyId: " + dataV.get(6)) ;
		dataV.add(dataV.get(6)) ;
		dataV.add(dataV.get(6)) ;

		// Lets parse one record
		oneParsedRecordStr = this.parseOneRecord(tagsV, dataV, htmlCodeFile) ;
		htmlStr += oneParsedRecordStr ;
		// log("Ett record: " + oneParsedRecordStr);
	    } // end of the big for

	} catch(Exception e) {
	    log("Error in REPLIES Preparse") ;
	    return null ;
	}
	return htmlStr ;
    } // End of

    /**
       Returns the users Replylevel htmlcode. If the user is marked with something
       a bitmap will occur, otherwise nothing will occur.
    */
    protected String getReplyLevelCode (HttpServletRequest req, Vector dataV, String ImagePath)
	throws ServletException, IOException {

	// Lets get the information regarding the replylevel
	int index = 5 ;
	String replyLevel = (String) dataV.elementAt(index) ;
	String htmlCode = "" ;
	String imageStart = "<img src=\"" ;
	String imageEnd = "\">" ;

	if (replyLevel.equals("1"))
	    htmlCode = imageStart + ImagePath + imageEnd;
	else
	    htmlCode = "" ;
	return htmlCode ;
    }

    // ****************** END OF PARSE REPLIES FUNCTIONS ****************

    // ****************** PARSE DISCUSSIONS FUNCTIONS *******************
    /**
       Parses the Extended array with the htmlcode, which will be parsed
       for all records in the array
    */
    public String discPreParse (HttpServletRequest req, String[] DBArr, Vector tagsV,
				File htmlCodeFile, String imagePath, int newDiscFlag)  throws ServletException, IOException {

	String htmlStr = "" ;
	try {
	    // Lets get the nbr of cols
	    int nbrOfCols = Integer.parseInt(DBArr[0]) ;
	    // Lets build an tagsArray with the tags from the DBarr, if
	    // null was passed to us instead of a vector

	    if( tagsV == null) {
		tagsV = new Vector() ;
		for(int k = 1; k<nbrOfCols; k++) {
		    tagsV.add(DBArr[k]) ;
		    // log("COunter: "+ k + " Tagvärde: " + DBArr[k] ) ;
		}
	    }

	    // Lets do for all records...
	    for(int i = nbrOfCols+1; i<DBArr.length; i += nbrOfCols) {
		// Lets prepare the dataVector with some values before were read
		// what we got in the db

		// Lets check if the discussions should have a new bitmap in front of them
		Vector dataV = new Vector(6) ;
		String oneParsedRecordStr = "" ;

		// Lets do for one record... Get all fields for that record
		for(int j=i; j<i+nbrOfCols ; j++) {
		    dataV.add(DBArr[j]) ;
		} // End of one records for

		// Lets parse one record
		oneParsedRecordStr = this.parseOneRecord(tagsV, dataV, htmlCodeFile) ;
		//log("Ett record: " + oneParsedRecordStr);
		htmlStr += oneParsedRecordStr ;
	    } // end of the big for

	} catch(Exception e) {
	    log("Error in DISCPreparse") ;
	    return null ;
	}
	return htmlStr ;
    } // End of

    // ****************** END OF PARSE REPLIES FUNCTIONS ****************

    /**
       Returns the users NewDiscussionFlag htmlcode. If something has happened in a discussion
       or a new discussion has took place, a bitmap will be shown in front of the discussion,
       otherwise nothing will occur.
    */

    protected String setNewDiscFlag (Vector dataV, String ImagePath)
	throws ServletException, IOException {

	// Lets get the information regarding the replylevel
	int index = 0 ;
	String htmlCode = "" ;
	String imageStart = "<img src=\"" ;
	String imageEnd = "\">" ;

	htmlCode = imageStart + ImagePath + imageEnd;
	return htmlCode ;
    }


    // ****************** GET PARAMETER FUNCTIONS ************************

    /**
       Collects the parameters used for setting the number of discussions to show in a forum
    **/

    protected Properties getShowDiscussionNbrParameters( HttpServletRequest req, Properties params)
	throws ServletException, IOException {
	// Lets check if we shall create a new properties

	if( params == null ) params = new Properties() ;
	// Lets get the standard metainformation
	String newNbr = (req.getParameter("NBR_OF_DISCS_TO_SHOW")==null) ? "" : (req.getParameter("NBR_OF_DISCS_TO_SHOW")) ;
	String forumId = (req.getParameter("FORUM_ID")==null) ? "" : (req.getParameter("FORUM_ID")) ;
	params.setProperty("FORUM_ID", forumId) ;
	params.setProperty("NBR_OF_DISCS_TO_SHOW", newNbr) ;
	return params ;
    }

    /**
       Collects the parameters used for adding a forum parametersfrom the SESSION object.
    **/

    protected Properties getAddForumParameters( HttpServletRequest req, Properties params)
	throws ServletException, IOException {
	// Lets check if we shall create a new properties

	if( params == null ) params = new Properties() ;
	// Lets get the standard metainformation
	String newConfName = (req.getParameter("NEW_FORUM_NAME")==null) ? "" : (req.getParameter("NEW_FORUM_NAME")) ;
	params.setProperty("NEW_FORUM_NAME", newConfName) ;
	return params ;
    }

    /**
       Collects the standard parameters from the SESSION object.
    **/

    protected Properties getRenameForumParameters( HttpServletRequest req, Properties params)
	throws ServletException, IOException {
	// Lets check if we shall create a new properties

	if( params == null ) params = new Properties() ;
	// Lets get the standard metainformation
	String newConfName = (req.getParameter("NEW_FORUM_NAME")==null) ? "" : (req.getParameter("NEW_FORUM_NAME")) ;
	String renForumId = (req.getParameter("FORUM_ID")==null) ? "" : (req.getParameter("FORUM_ID")) ;
	params.setProperty("NEW_FORUM_NAME", newConfName) ;
	params.setProperty("FORUM_ID", renForumId) ;
	return params ;
    }

    /**
       Collects the parameters used to delete a forum
    **/

    public Properties getDelForumParameters( HttpServletRequest req, Properties params)
	throws ServletException, IOException {
	// Lets check if we shall create a new properties

	if( params == null ) params = new Properties() ;
	// Lets get the standard metainformation
	String forumId = (req.getParameter("FORUM_ID")==null) ? "" : (req.getParameter("FORUM_ID")) ;
	params.setProperty("FORUM_ID", forumId) ;
	return params ;
    }

    /**
       Collects the parameters used to delete a discussion
    **/

    public String[] getDelDiscParameters( HttpServletRequest req )
	throws ServletException, IOException {

	// Lets get the standard discussion_id to delete
	String[] discId = (req.getParameterValues("DISC_DEL_BOX")) ;
	return discId ;
    }

    /**
       Collects the parameters used to delete a reply
    **/

    public String[] getDelReplyParameters( HttpServletRequest req )
	throws ServletException, IOException {

	// Lets get the standard discussion_id to delete
	String[] replyId = (req.getParameterValues("REPLY_DEL_BOX")) ;
	/*		for(int j=i; j<i+nbrOfCols ; j++) {
			dataV.add(DBArr[j]) ;
			//log("VÄRDE: " + j + " : " +  DBArr[j]) ;
			}
	*/return replyId ;
    }

    /**
       Collects the standard parameters from the SESSION object.
    **/

    public Properties getStandardParameters(HttpServletRequest req)
	throws ServletException, IOException {


	// Lets get the standard metainformation
	Properties reqParams = super.getSessionParameters(req) ;

	// Lets get the session
	HttpSession session = req.getSession(false) ;
	if(session != null) {
	    // Lets get the parameters we know we are supposed to get from the request object
	    String forumId = ( (String) session.getAttribute("Conference.forum_id")==null) ? "" : ((String) session.getAttribute("Conference.forum_id")) ;
	    String discId = (	(String) session.getAttribute("Conference.disc_id")==null) ? "" : ((String) session.getAttribute("Conference.disc_id")) ;
	    String lastLogindate = (	(String) session.getAttribute("Conference.last_login_date")==null) ? "" : ((String) session.getAttribute("Conference.last_login_date")) ;
	    reqParams.setProperty("LAST_LOGIN_DATE", lastLogindate) ;
	    reqParams.setProperty("FORUM_ID", forumId) ;
	    reqParams.setProperty("DISC_ID", discId) ;
	}
	return reqParams ;
    }

    // ******************	END GET PARAMETER FUNCTIONS *******************

    /**
       Builds the tagvector used for parse one record.
    */
    protected Vector buildAdminTags() {

	// Lets build our tags vector.
	Vector tagsV = new Vector() ;
	tagsV.add("#DISC_DEL_ID#") ;
	tagsV.add("#ARCHIVE_DATE#") ;
	tagsV.add("#HEADLINE#") ;
	tagsV.add("#COUNT_REPLIES#") ;
	tagsV.add("#FIRST_NAME#") ;
	tagsV.add("#LAST_NAME#") ;
	return tagsV ;
    } // End of buildstags


    /**
       Detects paths and filenames.
    */

    public void init(ServletConfig config) throws ServletException {
	super.init(config);
    }

    /**
       Log function, will work for both servletexec and Apache
    **/

    public void log( String str) {
	super.log(str) ;
	System.out.println("ConfAdmin: " + str ) ;
    }


} // End of class
