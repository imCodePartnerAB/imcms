package com.imcode.imcms.servlet.billboard;

import imcode.external.diverse.MetaInfo;
import imcode.external.diverse.VariableManager;
import imcode.server.Imcms;
import imcode.server.ImcmsServices;
import imcode.server.user.UserDomainObject;
import imcode.server.document.DocumentDomainObject;
import imcode.server.document.DocumentMapper;
import imcode.util.Utility;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Vector;
import java.util.List;

/**
 * Html template in use:
 * BillBoard_Reply_New_Comment.htm
 * BillBoard_Reply_Admin_Link.htm
 * <p/>
 * Html parstags in use:
 * #REPLY_BILL_ID#
 * #REPLY_HEADER#
 * #REPLY_TEXT#
 * #C_REPLIES#
 * #REPLY_DATE#
 * #SERVLET_URL#
 * #IMAGE_URL#
 * #NEW_REPLIE#
 * #REPLIE_RECORD#
 * #CURRENT_BILL_HEADER#
 * #ADMIN_LINK_HTML#
 * <p/>
 * stored procedures in use:
 * B_GetCurrentBill
 * B_GetBillHeader
 *
 * @author Rickard Larsson
 * @author Jerker Drottenmyr REBUILD TO BillBoardReply BY Peter Östergren
 * @version 1.2 20 Aug 2001
 */

public class BillBoardReply extends BillBoard {//ConfReply

    private final static String NEW_COMMENT_TEMPLATE = "billboard_reply_new_comment.htm";//Conf_Reply_New_Comment.htm
    private final static String ADMIN_LINK_TEMPLATE = "billboard_reply_admin_link.htm";//Conf_Reply_Admin_Link.htm
    private final static String HTML_TEMPLATE_MAIL_SENT = "billboard_reply_mail_sent.htm";
    private final static String HTML_TEMPLATE = "billboard_reply.htm";
    private final static String RECS_HTML = "billboard_reply_list.htm";
    private final static String RECS_PREV_HTML = "billboard_reply_list_prev.htm";
    private final static String HTML_TEMPLATE_START = "billboard_reply_welcome.htm";

    private final static String header = "header";
    private final static String text = "text";

    /**
     * DoPost
     */

    public void doPost( HttpServletRequest req, HttpServletResponse res )
            throws ServletException, IOException {

        // Lets get an user object

        imcode.server.user.UserDomainObject user = Utility.getLoggedOnUser( req );
        if ( user == null ) {
            return;
        }

        if ( !isUserAuthorized( req, res, user ) ) {
            return;
        }

        // ********* UPDATE DISCUSSIONS ********
        if ( req.getParameter( "UPDATE" ) != null ) {
            this.doGet( req, res );
            return;
        }

    }

    /**
     * DoGet
     */

    public void doGet( HttpServletRequest req, HttpServletResponse res )
            throws ServletException, IOException {

        HttpSession session = req.getSession( false );

        // Lets get the parameters and validate them
        Properties params = this.getParameters( req );

        // Lets get an user object

        imcode.server.user.UserDomainObject user = Utility.getLoggedOnUser( req );
        if ( user == null ) {
            return;
        }

        if ( !isUserAuthorized( req, res, user ) ) {
            return;
        }

        // Lets get serverinformation

        ImcmsServices imcref = Imcms.getServices();

        // Lets get path to the imagefolder. http://dev.imcode.com/images/102/ConfDiscNew.gif

        // Lets get the part of an html page, wich will be parsed for every a Href reference
        File templateLib = super.getExternalTemplateFolder( req );
        File aSnippetFile = new File( templateLib, RECS_PREV_HTML );

        //ok here we see     if we have a prevue to handle
        Hashtable billPrevData = (Hashtable)session.getAttribute( "billPrevData" );
        //log("PREVIEWMODE: "+req.getParameter("PREVIEWMODE"));
        if ( billPrevData != null && req.getParameter( "PREVIEWMODE" ) != null ) { //ok PREVIEW-mode
            //log("ok PREVIEW-mode");
            String addHeader = (String)billPrevData.get( header );
            String addText = (String)billPrevData.get( text );
            String datum = imcref.sqlProcedureStr( "B_GetTime", new String[]{} );
            //log(addHeader+"\n"+addText+"\n"+datum);
            String addType = req.getParameter( "ADDTYPE" );
            String addType2 = req.getParameter( "ADDTYPE" );
            //log("aaaaaaaaa: "+addType);
            //lets simulate the original sql answer
            String[][] tempArr = {{"", addHeader, addText, "", datum, addType, addType2}};
            //log("aSnippetFile: "+aSnippetFile);
            Vector tags = buildTagsV();
            tags.add( "#ADD_TYPE#" );
            tags.add( "#ADD_TYPE2#" );
            String currRec1 = preParse( tempArr, tags, aSnippetFile );
            //	log(currRec1);
            VariableManager vm1 = new VariableManager();
            //vm1.addProperty("NEW_REPLIE", commentButton ) ;//ska bort
            vm1.addProperty( "REPLIE_RECORD", currRec1 );
            vm1.addProperty( "CURRENT_BILL_HEADER", billPrevData.get( header ) );
            vm1.addProperty( "ADMIN_LINK_HTML", "" );//måste byta template

            this.sendHtml( req, res, vm1, HTML_TEMPLATE );
            return;
        }//end PREVIEW-mode

        // Lets get the users userId

        String discId = params.getProperty( "DISC_ID" );

        // Lets update the sessions DISC_ID

        if ( session != null ) {
            session.setAttribute( "BillBoard.disc_id", discId );
        }

        if ( discId.equals( "-1" ) ) { //ok lets get the start page
            VariableManager vm = new VariableManager();
            this.sendHtml( req, res, vm, HTML_TEMPLATE_START );
            return;
        }

        if ( req.getParameter( "MAIL_SENT" ) != null ) {
            //ok lets get the sent msg page
            VariableManager vm = new VariableManager();
            this.sendHtml( req, res, vm, HTML_TEMPLATE_MAIL_SENT );
            return;
        }

        String[][] sqlAnswer = imcref.sqlProcedureMulti( "B_GetCurrentBill", new String[]{discId} );

        // Lets get the discussion header
        String discHeader = imcref.sqlProcedureStr( "B_GetBillHeader", new String[]{discId} );//GetDiscussionHeader

        if ( discHeader == null || discId.equalsIgnoreCase( "-1" ) ) {
            discHeader = " ";
        }


        // UsersSortOrderRadioButtons
        String metaId = params.getProperty( "META_ID" );
        int intMetaId = Integer.parseInt( metaId );

        // Lets preparse all records
        aSnippetFile = new File( templateLib, RECS_HTML );
        String currentRec = " ";
        if ( sqlAnswer != null ) {
            currentRec = preParse( sqlAnswer, buildTagsV(), aSnippetFile );
        }

        // Lets build the Responsepage

        //lets generate the buttons that should appear
        String commentButton = "&nbsp;";

        //lets show comment button if user has more than readrights
        DocumentMapper documentMapper = imcref.getDocumentMapper();
        DocumentDomainObject document = documentMapper.getDocument( intMetaId );
        if ( user.canAccess( document ) &&
             imcref.checkDocAdminRights( intMetaId, user ) ) {

            VariableManager vmButtons = new VariableManager();
            vmButtons.addProperty( "#SERVLET_URL#", "" );
            vmButtons.addProperty( "#IMAGE_URL#", this.getExternalImageFolder( req ) );

            commentButton = getTemplate( NEW_COMMENT_TEMPLATE, user, vmButtons.getTagsAndData() );
        }

        VariableManager vm = new VariableManager();
        vm.addProperty( "NEW_REPLIE", commentButton );
        vm.addProperty( "REPLIE_RECORD", currentRec );
        vm.addProperty( "CURRENT_BILL_HEADER", discHeader );
        vm.addProperty( "ADMIN_LINK_HTML", ADMIN_LINK_TEMPLATE );

        this.sendHtml( req, res, vm, HTML_TEMPLATE );

    }

    private Vector buildTagsV() {
        // SYNTAX: id  headline  text replies date
        // Lets build our variable list
        Vector tagsV = new Vector();
        tagsV.add( "#REPLY_BILL_ID#" );
        tagsV.add( "#REPLY_HEADER#" );
        tagsV.add( "#REPLY_TEXT#" );
        tagsV.add( "#C_REPLIES#" );
        tagsV.add( "#REPLY_DATE#" );
        return tagsV;
    }

    /**
     * Parses the Extended array with the htmlcode, which will be parsed
     * for all records in the array
     */
    private String preParse( String[][] DBArr, Vector tagsV,
                             File htmlCodeFile ) throws IOException {

        StringBuffer htmlStr = new StringBuffer( "" );
        // Lets do for all records...
        for ( int i = 0; i < DBArr.length; i++ ) {
            Vector dataV = new Vector();

            for ( int j = 0; j < DBArr[i].length; j++ ) {
                dataV.add( DBArr[i][j] );
            }

            htmlStr.append( this.parseOneRecord( tagsV, dataV, htmlCodeFile ) );
            //	log("Ett record: " + oneParsedRecordStr);
        } // end of the big for
        return htmlStr.toString();
    } // End of

    /**
     * Collects the parameters from the request object. If a discId is found in the
     * request object, then that discId will be used instead of the session parameter.
     */

    private Properties getParameters( HttpServletRequest req ) {

        // Lets get the standard metainformation
        Properties reqParams = MetaInfo.createPropertiesFromMetaInfoParameters( super.getBillBoardSessionParameters( req ) );

        /* Lets get our own variables. We will first look for the discussion_id
           in the request object, other wise, we will get the one from our session object
        */
        String confDiscId = ( req.getParameter( "disc_id" ) == null ) ? "" : ( req.getParameter( "disc_id" ) );
        if ( confDiscId.equals( "" ) ) {
            HttpSession session = req.getSession( false );
            if ( session != null ) {
                confDiscId = (String)session.getAttribute( "BillBoard.disc_id" );
            }
        }
        //log("GetParameters: " + confDiscId) ;
        reqParams.setProperty( "DISC_ID", confDiscId );
        return reqParams;
    }

    /**
     * Log function, will work for both servletexec and Apache
     */

    public void log( String msg ) {
        super.log( "BillBoardReply: " + msg );

    }
} // End of class
