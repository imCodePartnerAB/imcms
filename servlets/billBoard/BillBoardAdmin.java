import imcode.server.* ;
import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import imcode.external.diverse.* ;
import java.rmi.* ;
import java.rmi.registry.* ;
import imcode.util.*;

/**
 *
 * Html template in use:
 * BillBoard_Section_Unadmin_Link.htm
 * BillBoard_Section_Template1_Unadmin_Link.htm
 * BillBoard_Section_Template2_Unadmin_Link.htm
 * BillBoard_Disc_Unadmin_Link.htm
 * BillBoard_Reply_Unadmin_Link.htm
 *
 * Html parstags in use:
 * #EXTERNAL_PATH#
 * #UNADMIN_LINK_HTML#
 * #TEMPLATE_LIST#
 * #A_META_ID#
 * #CURRENT_TEMPLATE_SET#
 * #SECTION_LIST#
 * #NBR_OF_DISCS_TO_SHOW_LIST#
 * #NBR_OF_DAYS_TO_SHOW_LIST#
 * #NEW_A_HREF_LIST#
 * #REPLY_ID#
 * #REPLY_HEADER#
 * #REPLY_TEXT#
 * #REPLY_COUNT#
 * #REPLY_EMAIL#
 * #REPLY_DATE#
 * #REPLY_IPADR#
 * #REPLIES_RECORDS#
 * #DISC_DEL_ID#
 * #HEADLINE#
 * #COUNT_REPLIES#
 * #ARCHIVE_DATE#
 *
 * stored procedures in use:
 * B_AddNewSection
 * B_DeleteBill
 * B_DeleteSection
 * B_UpdateBill
 * B_FindSectionName
 * B_GetAllTemplateLibs
 * B_GetAllSection
 * B_GetAllNbrOfDaysToShow
 * B_GetAllNbrOfDiscsToShow
 * B_GetAllBillsToShow
 * B_GetAllOldBills
 * B_GetAdminBill
 * B_GetSectionName
 * B_GetNbrOfDiscsToShow
 * B_GetTemplateIdFromName
 * B_RenameSection
 * B_SetNbrOfDiscsToShow
 * B_SetNbrOfDaysToShow
 * B_SetTemplateLib
 *
 *
 * @version 1.2 20 Aug 2001
 * @author Rickard Larsson, Jerker Drottenmyr, REBUILD TO BillBoardAdmin BY Peter Östergren
 */

public class BillBoardAdmin extends BillBoard {//ConfAdmin
    private final static String CVS_REV = "$Revision$" ;
    private final static String CVS_DATE = "$Date$" ;

    private final static String FORUM_UNADMIN_LINK_TEMPLATE = "BillBoard_Section_Unadmin_Link.htm";//"Conf_Forum_Unadmin_Link.htm";
    private final static String FORUM_TEMPLATE1_UNADMIN_LINK_TEMPLATE = "BillBoard_Section_Template1_Unadmin_Link.htm";//"Conf_Forum_Template1_Unadmin_Link.htm";
    private final static String FORUM_TEMPLATE2_UNADMIN_LINK_TEMPLATE = "BillBoard_Section_Template2_Unadmin_Link.htm";//"Conf_Forum_Template2_Unadmin_Link.htm";
    private final static String DISC_UNADMIN_LINK_TEMPLATE = "BillBoard_Disc_Unadmin_Link.htm";//"Conf_Disc_Unadmin_Link.htm";
    private final static String REPLY_UNADMIN_LINK_TEMPLATE = "BillBoard_Reply_Unadmin_Link.htm";//"Conf_Reply_Unadmin_Link.htm";

    String HTML_TEMPLATE ;

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
	String host = req.getHeader("Host") ;
	IMCServiceInterface imcref = IMCServiceRMI.getIMCServiceInterface(req) ;
	IMCPoolInterface billref = IMCServiceRMI.getBillboardIMCPoolInterface(req) ;

	// Lets check that the user is an administrator
	if( super.getAdminRights(imcref, params.getProperty("META_ID"), user) == false ) {
	    String header = "BillBoardAdmin servlet. " ;
	    String msg = params.toString() ;
	    BillBoardError err = new BillBoardError(req,res,header,6) ;
	    return ;
	}

	// ******** SHOW ARCHIVES BILLS *********
	if (req.getParameter("ADMIN_OLD_BILLS") != null) {
	    res.sendRedirect("BillBoardAdmin?ADMIN_TYPE=OLD_DISCUSSION") ;
	    return ;
	}
	if (req.getParameter("ADMIN_NEW_BILLS") != null) {
	    res.sendRedirect("BillBoardAdmin?ADMIN_TYPE=DISCUSSION") ;
	    return ;
	}


	// ********* REGISTER NEW TEMPLATE ********
	if (req.getParameter("REGISTER_TEMPLATE_LIB") != null) {
	    if (super.checkParameters(req, res, params) == false ) {
		return ;
	    }

	    // Lets get the new library name and validate it
	    String newLibName = req.getParameter("TEMPLATE_LIB_NAME")  ;
	    if (newLibName == null) {
		String header = "BillBoardAdmin servlet. " ;
		String msg = params.toString() ;
		BillBoardError err = new BillBoardError(req,res,header, 80) ;
		return ;
	    }
	    newLibName = super.verifySqlText(newLibName) ;

	    // Lets check if we already have a templateset with that name
	    String sql = "B_FindTemplateLib " + newLibName ;
	    String libNameExists = billref.sqlProcedureStr(sql) ;
	    if( !libNameExists.equalsIgnoreCase("-1") ) {
		String header = "BillBoardAdmin servlet. " ;
		String msg = params.toString() ;
		BillBoardError err = new BillBoardError(req,res,header, 84) ;
		return ;
	    }

	    String sqlQ = "B_AddTemplateLib '" + newLibName + "'" ;
	    billref.sqlUpdateProcedure(sqlQ) ;

	    // Lets copy the original folders to the new foldernames
	    String metaId = super.getMetaId(req) ;
	    FileManager fileObj = new FileManager() ;

	    File templateSrc = new File(imcref.getExternalTemplateFolder(Integer.parseInt(metaId)), "original") ;
	    File imageSrc = new File(RmiConf.getExternalImageFolder(imcref, metaId), "original") ;
	    File templateTarget = new File(imcref.getExternalTemplateFolder(Integer.parseInt(metaId)), newLibName) ;
	    File imageTarget = new File(RmiConf.getExternalImageFolder(imcref, metaId), newLibName) ;

	    fileObj.copyDirectory(templateSrc, templateTarget) ;
	    fileObj.copyDirectory(imageSrc, imageTarget) ;

	    res.sendRedirect("BillBoardAdmin?ADMIN_TYPE=META") ;
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

	    String url = "BillBoardAdmin?ADMIN_TYPE=META" ;
	    url += "&setname=" + libName + "&UPLOAD_TYPE=" + uploadType ;
	    res.sendRedirect(url) ;
	    return ;
	}

	// ********* SET TEMPLATE LIB FOR A CONFERENCE  ********
	if (req.getParameter("SET_TEMPLATE_LIB") != null) {
	    if (super.checkParameters(req, res, params) == false ) {
		return ;
	    }

	    // Lets get the new library name and validate it
	    String newLibName = req.getParameter("TEMPLATE_ID")  ;
	    if (newLibName == null) {
		String header = "BillBoardAdmin servlet. " ;
		String msg = params.toString() ;
		BillBoardError err = new BillBoardError(req,res,header, 80) ;
		return ;
	    }

	    // Lets find the selected template in the database and get its id
	    // if not found, -1 will be returned
	    String sqlQ = "B_GetTemplateIdFromName '" + newLibName + "'" ;//GetTemplateIdFromName
	    String templateId = billref.sqlProcedureStr(sqlQ) ;
	    if(templateId.equalsIgnoreCase("-1")) {
		String header = "BillBoardAdmin servlet. " ;
		String msg = params.toString() ;
		BillBoardError err = new BillBoardError(req,res,header,81) ;
		return ;
	    }
	    // Ok, lets update the conference with this new templateset.
	    String updateSql = "B_SetTemplateLib " + params.getProperty("META_ID") ;//SetTemplateLib
	    updateSql += ", '" + newLibName + "'" ;
	    billref.sqlUpdateProcedure(updateSql) ;

	    res.sendRedirect("BillBoardAdmin?ADMIN_TYPE=META") ;
	    return ;
	} // SET TEMPLATE LIB

	// ********* DELETE REPLY ********  Peter says OK!!!!
	if (req.getParameter("DELETE_REPLY") != null)
	    {
		if (super.checkParameters(req, res, params) == false ) {
		    return ;
		}
		// Lets get the discusssion id
		String discId = params.getProperty("DISC_ID") ;

		// Lets get all the replies id:s
		String repliesId = req.getParameter("bill_id") ;

		// Lets delete all marked replies. Observe that the first one wont be deleted!
		// if the user wants to delete the first one then he has to delete the discussion
		if( repliesId != null ) {
		    String sqlQ = "B_DeleteBill "  + repliesId ;//
		    billref.sqlUpdateProcedure(sqlQ) ;
		}

		//***
		HttpSession session = req.getSession(false);
		String aSectionId = (String) session.getAttribute("BillBoard.section_id");
		String sqlStr = "B_GetLastDiscussionId " +params.getProperty("META_ID") + ", " + aSectionId;

		String aDiscId = billref.sqlProcedureStr(sqlStr) ;

		session.setAttribute("BillBoard.disc_id", aDiscId) ;
		String param="";
		if (!aDiscId.equals("-1"))
		    {
			param = "?DISC_ID="+aDiscId;
		    }
		//***

		res.sendRedirect("BillBoardDiscView"+param) ;
		return ;
	    }

	// ********* RESAVE REPLY ********  Peter says OK!!!!
	if (req.getParameter("RESAVE_REPLY") != null) {
	    if (super.checkParameters(req, res, params) == false ) {
		return ;
	    }
	    // Lets get the discusssion id
	    String discId = params.getProperty("DISC_ID") ;

	    // Lets get all the replies id:s
	    String updateId = req.getParameter("bill_id") ;


	    // Lets get the seleted textboxes headers and texts values.
	    if( updateId != null ) {
		String newText = req.getParameter("TEXT_BOX") ;
		if( newText.equals("") || newText == null) {
		    BillBoardError err = new BillBoardError() ;
		    newText = err.getErrorMessage(req, 70) ;
		}

		String newHeader = req.getParameter("REPLY_HEADER") ;
		if( newHeader.equals("") || newHeader == null) {
		    BillBoardError err = new BillBoardError() ;
		    newHeader = err.getErrorMessage(req, 71) ;
		}

		String newEmail = req.getParameter("EPOST") ;
		if( newEmail.equals("") || newEmail == null) {
		    BillBoardError err = new BillBoardError() ;
		    newEmail = err.getErrorMessage(req, 74) ;
		}

		// Lets validate the new text for the sql question
		newHeader = super.verifySqlText(newHeader) ;
		newText = super.verifySqlText(newText) ;

		String sqlQ = "B_UpdateBill "  + updateId + ", '" ;
		sqlQ += newHeader + "', '" + newText + "', '"+ newEmail +"'" ;
		billref.sqlUpdateProcedure(sqlQ) ;
	    }
	    res.sendRedirect("BillBoardDiscView") ;
	    return ;
	}

	// ********* DELETE DISCUSSION ********   Peter says OK!!!!
	if (req.getParameter("DELETE_DISCUSSION") != null) {

	    if (super.checkParameters(req, res, params) == false ) {
		return ;
	    }

	    // Lets get all the discussion id:s
	    String discIds[] = this.getDelDiscParameters(req) ;

	    // Lets delete all the bills and all the replies to that bill.
	    if( discIds != null ) {

		for(int i = 0 ; i < discIds.length ; i++ ) {
		    String sqlQ = "B_DeleteBill " + discIds[i] ;//DeleteDiscussion
		    billref.sqlUpdateProcedure(sqlQ) ;
		}
	    }
	    res.sendRedirect("BillBoardAdmin?ADMIN_TYPE=DISCUSSION") ;
	    return ;
	}

	// ********* MOVE BILLS TO ANITHER SECTION ******** Peter says OK!!!!
	if (req.getParameter("MOVE_BILLS") != null)
	    {
		if (super.checkParameters(req, res, params) == false ) {
		    return ;
		}
		String redirectParam = req.getParameter("BILLTYPES");
		if (redirectParam.equalsIgnoreCase("OLD_ONES"))
		    {
			redirectParam = "OLD_DISCUSSION";
		    }else
			{
			    redirectParam = "DISCUSSION";
			}
		// Lets get the section_id and set our session object before updating
		String aSectionId = params.getProperty("SECTION_ID") ;

		String discIds[] = this.getDelDiscParameters(req) ;
		if (discIds == null) {
		    discIds = new String[0];
		}

		String moveToId = req.getParameter("MOVE_TO_SECTION") == null ? aSectionId:req.getParameter("MOVE_TO_SECTION");

		//Lets move all the bills to the section admin wants
		for(int i = 0 ; i < discIds.length ; i++ )
		    {
			String sqlQ = "B_ChangeSection " + discIds[i] +", "+moveToId ;//DeleteDiscussion
			billref.sqlUpdateProcedure(sqlQ) ;
		    }

		//Lets update the session in case we moved the shown bill
		HttpSession session = req.getSession(false);
		String sqlStr = "B_GetLastDiscussionId " +params.getProperty("META_ID") + ", " + aSectionId;
		String aDiscId = billref.sqlProcedureStr(sqlStr) ;
		session.setAttribute("BillBoard.disc_id", aDiscId) ;

		//ok lets rebuild the page
		res.sendRedirect("BillBoardAdmin?ADMIN_TYPE="+redirectParam) ;
		return ;

	    }

	// ********* MOVE A BILL TO ANITHER SECTION ******** Peter says OK!!!!
	if (req.getParameter("MOVE_A_BILL") != null) {
		if (super.checkParameters(req, res, params) == false ) {
		    return ;
		}

		// Lets get the section_id and set our session object before updating
		String aSectionId = params.getProperty("SECTION_ID") ;

		//lets get the bill_id
		String repliesId = req.getParameter("bill_id") ;


		String moveToId = req.getParameter("MOVE_TO_SECTION") == null ? aSectionId:req.getParameter("MOVE_TO_SECTION");

		//Lets move all the bills to the section admin wants

		String sqlQ = "B_ChangeSection " + repliesId +", "+moveToId ;//DeleteDiscussion
		billref.sqlUpdateProcedure(sqlQ) ;


		//Lets update the session in case we moved the shown bill
		HttpSession session = req.getSession(false);
		String sqlStr = "B_GetLastDiscussionId " +params.getProperty("META_ID") + ", " + aSectionId;
		String aDiscId = billref.sqlProcedureStr(sqlStr) ;
		session.setAttribute("BillBoard.disc_id", aDiscId) ;

		//ok lets rebuild the page
		res.sendRedirect("BillBoardDiscView") ;
		return ;

	    }


	// ********* DELETE SECTION ********	Peter says OK!!!!
	if (req.getParameter("DELETE_SECTION") != null) {

	    params = this.getDelSectionParameters(req, params) ;
	    if (super.checkParameters(req, res, params) == false) {
		return ;
	    }

	    // Lets get the section_id and set our session object before updating
	    String aSectionId = params.getProperty("SECTION_ID") ;

	    // Lets get all discussions for that setion and delete those before deleting the section
	    // B_GetAllBillsInSection @aSectionId int
	    String discs[] = billref.sqlProcedure("B_GetAllBillsInSection " + aSectionId) ;
	    if( discs != null) {
		for( int i = 0 ; i < discs.length; i++ ) {
		    String sqlQ = "B_DeleteBill " + discs[i] ;//DeleteDiscussion
		    billref.sqlUpdateProcedure(sqlQ) ;
		}
	    }

	    // B_DeleteSection @aSectionId int
	    String sqlQ = "B_DeleteSection " + params.getProperty("SECTION_ID") ;
	    billref.sqlUpdateProcedure(sqlQ) ;

	    //ok lets update the session incase we deleted the current one
	    String sqlGetFirst = "B_GetFirstSection " + params.getProperty("META_ID") ;//GetAllDiscussions
	    String first = billref.sqlProcedureStr(sqlGetFirst ) ;
	    HttpSession session = req.getSession(false);
	    session.setAttribute("BillBoard.section_id", first) ;

	    this.doGet(req, res) ;
	    return ;
	}

	// ********* ADD SECTION ********		Peter says OK!!!!
	if (req.getParameter("ADD_SECTION") != null) {
	    // Lets get addForum parameters
	    params = this.getAddSectionParameters(req, params) ;
	    if (super.checkParameters(req, res, params) == false) {
		return ;
	    }

	    // Lets verify the parameters for the sql questions.
	    params = super.verifyForSql(params) ;

	    // Lets check if a forum with that name exists
	    String findSql = "B_FindSectionName " + params.getProperty("META_ID") + ", '" ;
	    findSql += params.get("NEW_SECTION_NAME") + "'" ;

	    String foundIt = billref.sqlProcedureStr(findSql) ;

	    if( !foundIt.equalsIgnoreCase("-1") ) {
		String header = "BillBoardAdmin servlet. " ;
		String msg = params.toString() ;
		BillBoardError err = new BillBoardError(req,res,header,85) ;
		return ;
	    }

	    //B_AddNewSection @metaId @section_name @archive_mode @discs_to_show @days_to_show
	    String sqlAddQ = "B_AddNewSection " + params.getProperty("META_ID") + ", '" ;
	    sqlAddQ += params.getProperty("NEW_SECTION_NAME") + "', 'A', 30, 14" ;

	    billref.sqlUpdateProcedure(sqlAddQ) ;
	    this.doGet(req, res) ;
	    return ;
	}

	// ********* CHANGE SECTION NAME ********  Peter says OK!!!!
	if (req.getParameter("CHANGE_SECTION_NAME") != null) {
	    // Lets get addForum parameters
	    params = this.getRenameSectionParameters(req, params) ;
	    if (super.checkParameters(req, res, params) == false) {
		return ;
	    }

	    // Lets verify the parameters for the sql questions.
	    params = super.verifyForSql(params) ;

	    // Lets check if a forum with that name exists
	    String findSql = "B_FindSectionName " + params.getProperty("META_ID") + ", '" ;
	    findSql += params.get("NEW_SECTION_NAME") + "'" ;

	    String foundIt = billref.sqlProcedureStr(findSql) ;

	    if( !foundIt.equalsIgnoreCase("-1") ) {
		String header = "BillBoardAdmin servlet. " ;
		String msg = params.toString() ;
		BillBoardError err = new BillBoardError(req,res,header,85) ;
		return ;
	    }

	    String sqlAddQ = "B_RenameSection " + params.getProperty("SECTION_ID") + ", '" ;
	    sqlAddQ += params.getProperty("NEW_SECTION_NAME") + "'" ;
	    billref.sqlUpdateProcedure(sqlAddQ) ;
	    this.doGet(req, res) ;
	    return ;
	}

	// ********* CHANGE SUBJECT STRING ********
	if (req.getParameter("CHANGE_SUBJECT_NAME") != null) {
	    // Lets get the meta_id and the new subject string
	    String meta_id = req.getParameter("meta_id");
	    String new_subject = req.getParameter("NEW_SUBJECT_NAME");
	    if (meta_id == null || new_subject == null) {
		return ;
	    }

	    String sqlSubjAddQ = "B_SetNewSubjectString " + meta_id + ", '"+new_subject +"'";

	    billref.sqlUpdateProcedure(sqlSubjAddQ) ;
	    res.sendRedirect("BillBoardAdmin?ADMIN_TYPE=SECTION") ;
	    return ;
	}

	// ********* SET SHOW_DISCUSSION_COUNTER ******** Peter says OK!!!!
	if (req.getParameter("SHOW_DISCUSSION_NBR") != null) {
	    // Lets get addForum parameters
	    params = this.getShowDiscussionNbrParameters(req, params) ;
	    if (super.checkParameters(req, res, params) == false) {
		return ;
	    }

	    String sqlAddQ = "B_SetNbrOfDiscsToShow " + params.getProperty("SECTION_ID") + ", " ;
	    sqlAddQ += params.getProperty("NBR_OF_DISCS_TO_SHOW") ;

	    billref.sqlUpdateProcedure(sqlAddQ) ;
	    res.sendRedirect("BillBoardAdmin?ADMIN_TYPE=SECTION") ;
	    return ;
	}
	// ********* SET SHOW NUMBERS OF DAYS ********	Peter says OK!!!!
	if (req.getParameter("SHOW_DISCUSSION_DAYS") != null) {
	    // Lets get addForum parameters
	    params = this.getShowDiscussionDaysParameters(req, params) ;
	    if (super.checkParameters(req, res, params) == false) {
		return ;
	    }

	    String sqlAddQ = "B_SetNbrOfDaysToShow " + params.getProperty("SECTION_ID") + ", " ;
	    sqlAddQ += params.getProperty("NBR_OF_DAYS_TO_SHOW") ;

	    billref.sqlUpdateProcedure(sqlAddQ) ;
	    res.sendRedirect("BillBoardAdmin?ADMIN_TYPE=SECTION") ;
	    return ;
	}

    } // DoPost


    /**
       doGet
    **/
    public void doGet(HttpServletRequest req, HttpServletResponse res)
	throws ServletException, IOException
    {

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
	String host = req.getHeader("Host") ;
	IMCServiceInterface imcref = IMCServiceRMI.getIMCServiceInterface(req) ;
	IMCPoolInterface billref = IMCServiceRMI.getBillboardIMCPoolInterface(req) ;

	// Lets check that the user is an administrator
	if( super.getAdminRights(imcref, params.getProperty("META_ID"), user) == false ) {
	    String header = "BillBoardAdmin servlet. " ;
	    String msg = params.toString() ;
	    BillBoardError err = new BillBoardError(req,res,header,6) ;
	    log("nu small det i BillBoardAdmin doGet super.getAdminRights");
	    return ;
	}

	// Lets get the admintype from the requestobject
	String adminWhat = (req.getParameter("ADMIN_TYPE")==null) ? "" : (req.getParameter("ADMIN_TYPE")) ;
	//	log("GET: AdminType=" + adminWhat) ;

	VariableManager vm = new VariableManager();
	String htmlFile = "";


	// *********** ADMIN META *************
	if (adminWhat.equalsIgnoreCase("META") ) {

	    // Lets check which page we should show, the standard meta page or
	    // the upload image/template page

	    String setName = req.getParameter("setname") ;
	    if(	setName != null ) {
		HTML_TEMPLATE ="BillBoard_admin_template2.htm" ;

		String uploadType = req.getParameter("UPLOAD_TYPE") ;
		if( uploadType == null ) {
		    String header = "BillBoardAdmin servlet. " ;
		    String msg = params.toString() ;
		    BillBoardError err = new BillBoardError(req,res,header,83) ;
		    return ;
		}

		// Ok, Lets get root path to the external type
		String metaId= params.getProperty("META_ID") ;

		// Lets build the Responsepage
		vm.addProperty("UPLOAD_TYPE", uploadType );
		vm.addProperty("FOLDER_NAME", setName );
		vm.addProperty("META_ID", metaId);
		vm.addProperty("UNADMIN_LINK_HTML", this.FORUM_TEMPLATE2_UNADMIN_LINK_TEMPLATE );

		htmlFile = HTML_TEMPLATE;

		// Ok, were gonna show our standard meta page
	    }
	    else {
		HTML_TEMPLATE ="BillBoard_admin_template1.htm" ;//Conf_admin_template1.htm

		// Lets get the current template set for this metaid
		String sqlStoredProc = "B_GetTemplateLib " + params.getProperty("META_ID") ;
		String currTemplateSet = billref.sqlProcedureStr(sqlStoredProc) ;

		// Lets get all current template sets
		String sqlAll = "B_GetAllTemplateLibs" ;
		String sqlAnswer[] = billref.sqlProcedure( sqlAll ) ;
		Vector templateV = super.convert2Vector(sqlAnswer) ;

		// Lets fill the select box	with forums
		String templateList = Html.createHtmlCode("ID_OPTION", "", templateV ) ;

		// Lets build the Responsepage
		//VariableManager vm = new VariableManager() ;
		vm.addProperty("TEMPLATE_LIST", templateList ) ;
		vm.addProperty("A_META_ID", params.getProperty("META_ID") ) ;
		vm.addProperty("CURRENT_TEMPLATE_SET", currTemplateSet ) ;
		vm.addProperty("UNADMIN_LINK_HTML", this.FORUM_TEMPLATE1_UNADMIN_LINK_TEMPLATE );

		//this.sendHtml(req,res,vm, HTML_TEMPLATE) ;
		htmlFile = HTML_TEMPLATE;
		//return ;
	    }
	}

	// *********** ADMIN SECTION ************* Peter says ok!!!!
	if (adminWhat.equalsIgnoreCase("SECTION") ) {//FORUM
	    HTML_TEMPLATE ="BillBoard_admin_section.htm" ;//Conf_admin_forum.htm

	    // Lets get the information from DB
	    String sqlStoredProc = "B_GetAllSection " + params.getProperty("META_ID") ;//GetAllForum
	    String sqlAnswer[] = billref.sqlProcedure( sqlStoredProc ) ;
	    Vector sectionV = super.convert2Vector(sqlAnswer) ;

	    // Lets fill the select box with forums
	    String forumList = Html.createHtmlCode("ID_OPTION", "", sectionV ) ;

	    // Lets get the name of the currently selected forum
	    String sectionNameSql = "B_GetSectionName " + params.getProperty("SECTION_ID") ;
	    String sectionName = billref.sqlProcedureStr( sectionNameSql ) ;

	    // Lets get the name of the currently selected forum
	    String nbrOfDiscsToShowSql = "B_GetNbrOfDiscsToShow " + params.getProperty("SECTION_ID") ;//GetNbrOfDiscsToShow
	    String nbrOfDiscsToShow = billref.sqlProcedureStr( nbrOfDiscsToShowSql ) ;


	    //lets get all the daysnumber values
	    String sqlAllDaysSql = "B_GetAllNbrOfDaysToShow " + params.getProperty("META_ID") ;
	    String sqlAllDays[] = billref.sqlProcedure( sqlAllDaysSql ) ;

	    //lets get the startstring of the mail subject
	    String sqlSubjStr = "B_GetStartSubjectString " + params.getProperty("META_ID") ;
	    String subject_name = billref.sqlProcedureStr( sqlSubjStr ) ;
	    if(subject_name ==null)subject_name = "";


	    Vector sqlAllDaysV = new Vector() ;
	    if (sqlAllDays != null) {
		sqlAllDaysV = super.convert2Vector(sqlAllDays) ;
	    }
	    String daysToShowList = Html.createHtmlCode("ID_OPTION", "", sqlAllDaysV ) ;



	    // Lets get all the showDiscs values
	    String sqlAllDiscsSql = "B_GetAllNbrOfDiscsToShow " + params.getProperty("META_ID") ;
	    String sqlAllDiscs[] = billref.sqlProcedure( sqlAllDiscsSql ) ;

	    Vector sqlAllDiscsV = new Vector() ;
	    if (sqlAllDiscs != null) {
		sqlAllDiscsV = super.convert2Vector(sqlAllDiscs) ;
	    }
	    String discToShowList = Html.createHtmlCode("ID_OPTION", "", sqlAllDiscsV ) ;

	    // Lets build the Responsepage
	    //VariableManager vm = new VariableManager() ;
	    vm.addProperty("SUBJECT_NAME",subject_name);
	    vm.addProperty("META_ID",params.getProperty("META_ID"));
	    vm.addProperty("SECTION_LIST", forumList ) ;//FORUM_LIST", forumList
	    vm.addProperty("NBR_OF_DISCS_TO_SHOW_LIST", discToShowList );
	    vm.addProperty("NBR_OF_DAYS_TO_SHOW_LIST", daysToShowList  );
	    vm.addProperty("UNADMIN_LINK_HTML", this.FORUM_UNADMIN_LINK_TEMPLATE );
	    //this.sendHtml(req,res,vm, HTML_TEMPLATE) ;
	    htmlFile = HTML_TEMPLATE;
	    //return ;
	}

	// *********** ADMIN DISCUSSION *************
	if (adminWhat.equalsIgnoreCase("DISCUSSION") ) {
	    HTML_TEMPLATE ="BillBoard_admin_disc.htm" ;//Conf_admin_disc.htm
	    String adminDiscList = "BillBoard_admin_disc_list.htm" ;//Conf_admin_disc_list.htm

	    // Lets get parameters
	    String aMetaId = params.getProperty("META_ID") ;
	    String aSectionId = params.getProperty("SECTION_ID") ;
	    String aLoginDate = params.getProperty("LAST_LOGIN_DATE") ;

	    // Lets get path to the imagefolder. http://dev.imcode.com/images/se/102/ConfDiscNew.gif
	    String imagePath = super.getExternalImageFolder(req) + "BillBoardDiscNew.gif" ;//ConfDiscNew.gif

	    // Lets get the part of an html page, wich will be parsed for every a Href reference
	    File aHrefHtmlFile = new File(super.getExternalTemplateFolder(req), adminDiscList) ;

	    // Lets get all New Discussions
	    String sqlStoredProc = "B_GetAllBillsToShow " +aMetaId+ ", "+ aSectionId;//GetAllNewDiscussions

	    String sqlAnswerNew[] = billref.sqlProcedureExt( sqlStoredProc ) ;

	    //lets get all the sections and the code for the selectlist
	    sqlStoredProc = "B_GetAllSection "+aMetaId;
	    String sqlSections[] = billref.sqlProcedure( sqlStoredProc ) ;
	    Vector sectionV = super.convert2Vector(sqlSections) ;
	    String sectionListStr = Html.createHtmlCode("ID_OPTION", aSectionId, sectionV ) ;

	    // Lets build our tags vector.
	    Vector tagsV = this.buildAdminTags() ;

	    // Lets preparse all NEW records
	    String allNewRecs  = "" ;
	    if( sqlAnswerNew != null ) {
		if( sqlAnswerNew.length > 0)
		    allNewRecs = discPreParse(req, sqlAnswerNew, tagsV, aHrefHtmlFile) ;
	    }

	    // Lets build the Responsepage

	    vm.addProperty("SECTION_LIST", sectionListStr  ) ;
	    vm.addProperty("NEW_A_HREF_LIST", allNewRecs  ) ;
	    vm.addProperty("UNADMIN_LINK_HTML", this.DISC_UNADMIN_LINK_TEMPLATE );

	    htmlFile = HTML_TEMPLATE;
	} // End admin discussion


	// *********** ADMIN OLD DISCUSSION *************
	if (adminWhat.equalsIgnoreCase("OLD_DISCUSSION") ) {
	    HTML_TEMPLATE ="BillBoard_admin_disc2.htm" ;//Conf_admin_disc.htm
	    String adminDiscList = "BillBoard_admin_disc_list.htm" ;//Conf_admin_disc_list.htm

	    // Lets get parameters
	    String aMetaId = params.getProperty("META_ID") ;
	    String aSectionId = params.getProperty("SECTION_ID") ;

	    // Lets get path to the imagefolder. http://dev.imcode.com/images/se/102/ConfDiscNew.gif

	    // Lets get the part of an html page, wich will be parsed for every a Href reference
	    File aHrefHtmlFile = new File(super.getExternalTemplateFolder(req), adminDiscList) ;

	    // Lets get all New Discussions
	    String sqlStoredProc = "B_GetAllOldBills " +aMetaId+ ", "+ aSectionId;//GetAllNewDiscussions

	    String sqlAnswerNew[] = billref.sqlProcedureExt( sqlStoredProc ) ;

	    //lets get all the sections and the code for the selectlist
	    sqlStoredProc = "B_GetAllSection "+aMetaId;
	    String sqlSections[] = billref.sqlProcedure( sqlStoredProc ) ;
	    Vector sectionV = super.convert2Vector(sqlSections) ;
	    String sectionListStr = Html.createHtmlCode("ID_OPTION", aSectionId, sectionV ) ;

	    // Lets build our tags vector.
	    Vector tagsV = this.buildAdminTags() ;

	    // Lets preparse all OLD records
	    String allNewRecs  = "" ;
	    if( sqlAnswerNew != null ) {
		if( sqlAnswerNew.length > 0)
		    allNewRecs = discPreParse(req, sqlAnswerNew, tagsV, aHrefHtmlFile) ;
	    }


	    // Lets build the Responsepage
	    vm.addProperty("SECTION_LIST", sectionListStr  ) ;
	    vm.addProperty("NEW_A_HREF_LIST", allNewRecs  ) ;
	    vm.addProperty("UNADMIN_LINK_HTML", this.DISC_UNADMIN_LINK_TEMPLATE );

	    //this.sendHtml(req,res,vm, HTML_TEMPLATE) ;
	    htmlFile = HTML_TEMPLATE;
	    //return ;
	} // End admin discussion



	// *********** ADMIN REPLIES *************
	if (adminWhat.equalsIgnoreCase("REPLY") ) {
	    HTML_TEMPLATE ="BillBoard_admin_reply.htm" ;//Conf_admin_reply.htm
	    String adminReplyList = "BillBoard_admin_reply_list.htm" ;//Conf_admin_reply_list.htm
	    log("OK, Administrera replies") ;

	    // Lets get the replylist from DB
	    String discId = params.getProperty("DISC_ID") ;

	    String sqlAnswer[] = billref.sqlProcedureExt("B_GetAdminBill " + discId) ;//GetAllRepliesInDiscAdmin

	    // SYNTAX: date  first_name  last_name  headline   text reply_level
	    // Lets build our variable list
	    Vector tagsV = new Vector() ;
	    tagsV.add("#REPLY_ID#") ;
	    //tagsV.add("#REPLY_ID2#");
	    tagsV.add("#REPLY_HEADER#") ;
	    tagsV.add("#REPLY_TEXT#") ;
	    tagsV.add("#REPLY_COUNT#") ;
	    tagsV.add("#REPLY_EMAIL#") ;
	    tagsV.add("#REPLY_DATE#") ;
	    tagsV.add("#REPLY_IPADR#") ;


	    // Lets get the part of an html page, wich will be parsed for every a Href reference
	    File templateLib = super.getExternalTemplateFolder(req) ;
	    File aSnippetFile = new File(templateLib, adminReplyList) ;

	    //lets get all the sections and the code for the selectlist
	    String sqlStoredProc = "B_GetAllSection "+params.getProperty("META_ID");
	    String sqlSections[] = billref.sqlProcedure(sqlStoredProc ) ;
	    Vector sectionV = super.convert2Vector(sqlSections) ;
	    String sectionListStr = Html.createHtmlCode("ID_OPTION", params.getProperty("SECTION_ID"), sectionV ) ;

	    // Lets preparse all records
	    String allRecs = " " ;
	    if (sqlAnswer != null) allRecs = replyPreParse(req, sqlAnswer, tagsV, aSnippetFile) ;

	    // Lets build the Responsepage
	    vm.addProperty("SECTION_LIST", sectionListStr  ) ;
	    vm.addProperty("REPLIES_RECORDS", allRecs  ) ;
	    vm.addProperty("UNADMIN_LINK_HTML", this.REPLY_UNADMIN_LINK_TEMPLATE );

	    htmlFile = HTML_TEMPLATE;
	} // End admin Reply

	this.sendHtml(req,res,vm, htmlFile ) ;

    } //DoGet


    // ****************** PARSE REPLIES FUNCTIONS ************************
    /**
       Parses the Extended array with the htmlcode, which will be parsed
       for all records in the array
    */
    public String replyPreParse (HttpServletRequest req, String[] DBArr, Vector tagsV,
				 File htmlCodeFile)  throws ServletException, IOException {

	String htmlStr = "" ;
	try {

	    // Lets get the nbr of cols
	    int nbrOfCols = Integer.parseInt(DBArr[0]) ;

	    // Lets do for all records...
	    for(int i = nbrOfCols+1; i<DBArr.length; i += nbrOfCols) {
		String oneParsedRecordStr = "" ;
		Vector dataV = new Vector() ;

		// Lets do for one record... Get all fields for that record
		for(int j=i; j<i+nbrOfCols ; j++) {
		    dataV.add(DBArr[j]) ;
		} // End of one records for



		// Lets parse one record
		oneParsedRecordStr = this.parseOneRecord(tagsV, dataV, htmlCodeFile) ;
		htmlStr += oneParsedRecordStr ;
	    } // end of the big for

	} catch(Exception e) {
	    log("Error in REPLIES Preparse") ;
	    return null ;
	}
	return htmlStr ;
    } // End of


    // ****************** END OF PARSE REPLIES FUNCTIONS ****************

    // ****************** PARSE DISCUSSIONS FUNCTIONS *******************
    /**
       Parses the Extended array with the htmlcode, which will be parsed
       for all records in the array
    *///(HttpServletRequest req, String[] DBArr, Vector tagsV,String htmlCodeFile)
    public String discPreParse (HttpServletRequest req, String[] DBArr, Vector tagsV,
				File htmlCodeFile)  throws ServletException, IOException {

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
		}
	    }

	    // Lets do for all records...
	    for(int i = nbrOfCols+1; i<DBArr.length; i += nbrOfCols) {
		// Lets prepare the dataVector with some values before were read
		// what we got in the db

		// Lets check if the discussions should have a new bitmap in front of them
		Vector dataV = new Vector() ;
		String oneParsedRecordStr = "" ;

		// Lets do for one record... Get all fields for that record
		for(int j=i; j<i+nbrOfCols ; j++) {
		    dataV.add(DBArr[j]) ;
		} // End of one records for

		//ok lets fix the sectionlist

		// Lets parse one record
		oneParsedRecordStr = this.parseOneRecord(tagsV, dataV, htmlCodeFile) ;
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
       Parses one record.
    */
    public String parseOneRecord (String[] tags, String[] data, File htmlCodeFile) {

	Vector tagsV = super.convert2Vector(tags) ;
	Vector dataV = super.convert2Vector(data) ;
	return this.parseOneRecord(tagsV, dataV, htmlCodeFile) ;
    }


    /**
       Parses one record.
    */
    public String parseOneRecord (Vector tagsV, Vector dataV, File htmlCodeFile) {

	String htmlStr = "" ;
	// Lets parse one aHref reference
	ParseServlet parser = new ParseServlet(htmlCodeFile, tagsV, dataV) ;
	String oneRecordsHtmlCode = parser.getHtmlDoc() ;
	return oneRecordsHtmlCode ;
    } // End of parseOneRecord


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
       Collects the parameters used for setting the number of days to show in a forum
    **/

    protected Properties getShowDiscussionDaysParameters( HttpServletRequest req, Properties params)
	throws ServletException, IOException {
	// Lets check if we shall create a new properties

	if( params == null ) params = new Properties() ;
	// Lets get the standard metainformation
	String newNbr = (req.getParameter("NBR_OF_DAYS_TO_SHOW")==null) ? "" : (req.getParameter("NBR_OF_DAYS_TO_SHOW")) ;//NBR_OF_DISCS_TO_SHOW
	String sectionId = (req.getParameter("SECTION_ID")==null) ? "" : (req.getParameter("SECTION_ID")) ;//FORUM_ID
	params.setProperty("SECTION_ID", sectionId) ;
	params.setProperty("NBR_OF_DAYS_TO_SHOW", newNbr) ;
	return params ;
    }


    /**
       Collects the parameters used for setting the number of discussions to show in a section. OBS Petsr says ok!!!
    **/

    protected Properties getShowDiscussionNbrParameters( HttpServletRequest req, Properties params)
	throws ServletException, IOException {
	// Lets check if we shall create a new properties

	if( params == null ) params = new Properties() ;
	// Lets get the standard metainformation
	String newNbr = (req.getParameter("NBR_OF_DISCS_TO_SHOW")==null) ? "" : (req.getParameter("NBR_OF_DISCS_TO_SHOW")) ;//NBR_OF_DISCS_TO_SHOW
	String sectionId = (req.getParameter("SECTION_ID")==null) ? "" : (req.getParameter("SECTION_ID")) ;//FORUM_ID
	params.setProperty("SECTION_ID", sectionId) ;
	params.setProperty("NBR_OF_DISCS_TO_SHOW", newNbr) ;
	return params ;
    }

    /**
       Collects the parameters used for adding a forum parametersfrom the SESSION object.
    **/

    protected Properties getAddSectionParameters( HttpServletRequest req, Properties params)
	throws ServletException, IOException {
	// Lets check if we shall create a new properties

	if( params == null ) params = new Properties() ;
	// Lets get the standard metainformation
	String newSectionName = (req.getParameter("NEW_SECTION_NAME")==null) ? "" : (req.getParameter("NEW_SECTION_NAME")) ;//NEW_FORUM_NAME
	params.setProperty("NEW_SECTION_NAME", newSectionName) ;
	return params ;
    }

    /**
       Collects the standard parameters from the SESSION object.
    **/

    protected Properties getRenameSectionParameters( HttpServletRequest req, Properties params)
	throws ServletException, IOException {
	// Lets check if we shall create a new properties

	if( params == null ) params = new Properties() ;
	// Lets get the standard metainformation
	String newConfName = (req.getParameter("NEW_SECTION_NAME")==null) ? "" : (req.getParameter("NEW_SECTION_NAME")) ;
	String renForumId = (req.getParameter("SECTION_ID")==null) ? "" : (req.getParameter("SECTION_ID")) ;
	params.setProperty("NEW_SECTION_NAME", newConfName) ;
	params.setProperty("SECTION_ID", renForumId) ;
	return params ;
    }

    /**
       Collects the parameters used to delete a forum
    **/

    public Properties getDelSectionParameters( HttpServletRequest req, Properties params)
	throws ServletException, IOException {
	// Lets check if we shall create a new properties

	if( params == null ) params = new Properties() ;
	// Lets get the standard metainformation
	String forumId = (req.getParameter("SECTION_ID")==null) ? "" : (req.getParameter("SECTION_ID")) ;
	params.setProperty("SECTION_ID", forumId) ;
	return params ;
    }

    /**
       Collects the parameters used to delete a discussion
    **/

    public String[] getDelDiscParameters( HttpServletRequest req )
	throws ServletException, IOException {

	// Lets get the standard discussion_id to delete
	String[] discId = (req.getParameterValues("DISC_DEL_BOX")) ;//DISC_DEL_BOX
	return discId ;
    }

    /**
       Collects the parameters used to delete a reply
    **/

    public String[] getDelReplyParameters( HttpServletRequest req )
	throws ServletException, IOException {

	// Lets get the standard discussion_id to delete
	String[] replyId = (req.getParameterValues("REPLY_DEL_BOX")) ;//REPLY_DEL_BOX
	return replyId ;
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
	    String sectionId = ( (String) session.getAttribute("BillBoard.section_id")==null) ? "" : ((String) session.getAttribute("BillBoard.section_id")) ;//Conference.forum_id
	    String discId = (	(String) session.getAttribute("BillBoard.disc_id")==null) ? "" : ((String) session.getAttribute("BillBoard.disc_id")) ;//Conference.disc_id
	    //			String lastLogindate = (	(String) session.getAttribute("BillBoard.last_login_date")==null) ? "" : ((String) session.getAttribute("BillBoard.last_login_date")) ;//Conference.last_login_date"
	    //			reqParams.setProperty("LAST_LOGIN_DATE", lastLogindate) ;
	    reqParams.setProperty("SECTION_ID", sectionId) ;
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
	tagsV.add("#HEADLINE#") ;
	tagsV.add("#COUNT_REPLIES#") ;
	tagsV.add("#ARCHIVE_DATE#") ;


	return tagsV ;
    } // End of buildstags


    /**
       Detects paths and filenames.
    */

    public void init(ServletConfig config) throws ServletException {
	super.init(config);
	HTML_TEMPLATE ="BillBoard_admin_section.htm" ;//Conf_admin_forum.htm
    }

    /**
       Log function, will work for both servletexec and Apache
    **/

    public void log( String msg) {
	super.log("BillBoardAdmin: " + msg ) ;
    }


} // End of class
