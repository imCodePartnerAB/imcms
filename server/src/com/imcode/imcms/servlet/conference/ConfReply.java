package com.imcode.imcms.servlet.conference;

/*
 *
 * @(#)ConfReply.java
 *
 *
 *
 * Copyright (c)
 *
 */

import imcode.external.diverse.MetaInfo;
import imcode.external.diverse.ParsedTextFile;
import imcode.external.diverse.VariableManager;
import imcode.server.Imcms;
import imcode.server.HTMLConv;
import imcode.server.ImcmsServices;
import imcode.server.document.DocumentDomainObject;
import imcode.server.document.DocumentMapper;
import imcode.server.user.UserDomainObject;
import imcode.util.Utility;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Properties;
import java.util.Vector;

/**
 * Html template in use:
 * Conf_Reply_New_Comment.htm
 * <p/>
 * Html parstags in use:
 * #NEW_COMMENT#
 * <p/>
 * stored procedures in use:
 * -
 *
 * @author Rickard Larsson
 * @author Jerker Drottenmyr
 * @version 1.5 21 Nov 2000
 */

public class ConfReply extends Conference {

    private final static String NEW_COMMENT_TEMPLATE = "conf_reply_new_comment.htm";
    private final static String ADMIN_LINK_TEMPLATE = "conf_reply_admin_link.htm";

    private final static String HTML_TEMPLATE = "conf_reply.htm";
    private final static String RECS_HTML = "conf_reply_list.htm";

    /**
     * DoPost
     */

    public void doPost( HttpServletRequest req, HttpServletResponse res ) throws ServletException, IOException {

        // Lets get the parameters
        Properties params = this.getParameters( req );

        UserDomainObject user = Utility.getLoggedOnUser( req );
        if ( !isUserAuthorized( req, res, user ) ) {
            return;
        }

        // ********* UPDATE DISCUSSIONS ********
        if ( req.getParameter( "UPDATE" ) != null ) {
            // Lets get the users userId, the metaId and sortorder
            // Ok, lets save the users sortorder if he has change it

            // Lets get ourselves a userid. we cant use the userparams id
            // since we got external users. so the userid could be an ip access nbr
            String userId = "" + user.getId();
            HttpSession session = req.getSession( false );
            if ( session != null ) {
                userId = (String)session.getAttribute( "Conference.user_id" );
            }

            String metaId = params.getProperty( "META_ID" );

            // THIS CODE IS USED IF WE WANT RADIOBUTTONS
            String ascSortOrder = ( req.getParameter( "SORT_ORDER" ) == null ) ? "0" : ( req.getParameter( "SORT_ORDER" ) );

            // Ok, Lets set the users sortorder preference
            Imcms.getServices().sqlUpdateProcedure( "A_ConfUsersSetReplyOrder", new String[]{metaId, userId, ascSortOrder} );
            this.doGet( req, res );
            return;
        }
    }

    /**
     * DoGet
     */
    public void doGet( HttpServletRequest req, HttpServletResponse res ) throws ServletException, IOException {

        // Lets get the parameters and validate them
        Properties params = this.getParameters( req );

        UserDomainObject user = Utility.getLoggedOnUser( req );
        if ( !isUserAuthorized( req, res, user ) ) {
            return;
        }

        // Lets get the replylist from DB
        String discId = params.getProperty( "DISC_ID" );

        String userId = "";

        // Lets update the sessions DISC_ID
        HttpSession session = req.getSession( false );
        if ( session != null ) {
            session.setAttribute( "Conference.disc_id", discId );
            userId = (String)session.getAttribute( "Conference.user_id" );
        }

        // Lets get serverinformation
        ImcmsServices imcref = Imcms.getServices();

        String[][] sqlAnswer = imcref.sqlProcedureMulti( "A_GetAllRepliesInDisc", new String[]{discId, userId} );

        // Lets get the discussion header
        String discHeader = imcref.sqlProcedureStr( "A_GetDiscussionHeader", new String[]{discId} );
        if ( discHeader == null || discId.equalsIgnoreCase( "-1" ) )
            discHeader = " ";

        // THIS CODE IS USED IF WE WANT RADIOBUTTONS
        // UsersSortOrderRadioButtons
        String metaId = params.getProperty( "META_ID" );
        int intMetaId = Integer.parseInt( metaId );
        String sortOrderValue = imcref.sqlProcedureStr( "A_ConfUsersGetReplyOrderSel", new String[]{metaId, userId} );
        String ascState = "";
        String descState = "";
        String ascVal = "0";
        if ( sortOrderValue.equalsIgnoreCase( "1" ) )
            ascState = "checked";
        else
            descState = "checked";

        // SYNTAX: date  first_name  last_name  headline   text reply_level
        // Lets build our variable list
        Vector tagsV = new Vector();
        tagsV.add( "#REPLY_DATE#" );
        tagsV.add( "#FIRST_NAME#" );
        tagsV.add( "#LAST_NAME#" );
        tagsV.add( "#REPLY_HEADER#" );
        tagsV.add( "#REPLY_TEXT#" );
        tagsV.add( "#REPLY_LEVEL#" );

        // Lets get path to the imagefolder. http://dev.imcode.com/images/102/ConfDiscNew.gif
        String imagePath = super.getExternalImageFolder( req ) + "ConfExpert.gif";

        // Lets get the part of the expert html

        // Lets get the part of an html page, wich will be parsed for every a Href reference
        File templateLib = super.getExternalTemplateFolder( req );
        File aSnippetFile = new File( templateLib, RECS_HTML );

        // Lets update the discussion list
        this.updateDiscFlagList( req, discId );

        // Lets preparse all records
        String allRecs = " ";
        if ( sqlAnswer != null ) allRecs = preParse( sqlAnswer, tagsV, aSnippetFile, imagePath );

        // Lets build the Responsepage

        //lets generate the buttons that should appear
        String commentButton = "&nbsp;";

        //lets show comment button if user has more than readrights
        DocumentMapper documentMapper = imcref.getDocumentMapper();
        DocumentDomainObject document = documentMapper.getDocument(intMetaId);
        if ( user.canAccess( document ) && imcref.checkDocAdminRights( intMetaId, user ) ) {

            VariableManager vmButtons = new VariableManager();
            vmButtons.addProperty( "#SERVLET_URL#", "" );
            vmButtons.addProperty( "#IMAGE_URL#", this.getExternalImageFolder( req ) );

            commentButton = getTemplate( NEW_COMMENT_TEMPLATE, user, vmButtons.getTagsAndData() );
        }

        VariableManager vm = new VariableManager();
        vm.addProperty( "NEW_COMMENT", commentButton );
        vm.addProperty( "USER_SORT_ORDER", ascVal );
        vm.addProperty( "CHECKBOX_STATE_ASC", ascState );
        vm.addProperty( "CHECKBOX_STATE_DESC", descState );
        vm.addProperty( "REPLIES_RECORDS", allRecs );
        vm.addProperty( "CURRENT_DISCUSSION_HEADER", discHeader );
        vm.addProperty( "ADMIN_LINK_HTML", ADMIN_LINK_TEMPLATE );

        this.sendHtml( req, res, vm, HTML_TEMPLATE );
    }

    /**
     * Takes the discussion id from the request object and moves ít to
     * the sessions list over viewed discussions.
     */

    private void updateDiscFlagList( HttpServletRequest req, String discId ) {

        // Lets get the newDiscsList

        // Get the session and add the clicked discussion to the list. Put list back
        HttpSession session = req.getSession( true );
        Properties viewedDiscs = (Properties)session.getAttribute( "Conference.viewedDiscList" );
        // Lets check if we got a list, if not, then create one
        if ( viewedDiscs == null ) {
            log( "ViewedDiscs == null" );
            viewedDiscs = new Properties();
        }

        // Lets create a date from the sqlstring

        //log("SQLTIME: " + java.sql.Date.valueOf(now).toString());
        java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );
        String dateString = formatter.format( new Date() );

        // log("discId: " + discId) ;
        //log("dateString: " + dateString) ;
        if ( discId == null || dateString == null ) {
            log( "Error i updateDiscFlagList" );
            log( "discId: " + discId );
            log( "dateString: " + dateString );
            discId = "" + discId;
            dateString = "" + dateString;
        }
        viewedDiscs.setProperty( discId, dateString ); // id
        session.setAttribute( "Conference.viewedDiscList", viewedDiscs );
    }

    /**
     * Parses the Extended array with the htmlcode, which will be parsed
     * for all records in the array
     */
    private String preParse( String[][] DBArr, Vector tagsV, File htmlCodeFile, String imagePath ) throws IOException {

        String htmlStr = "";

        // Lets do for all records...
        for ( int i = 0; i < DBArr.length; i++ ) {
            Vector dataV = new Vector();

            // Lets do for one record... Get all fields for that record
            // Lets go through the array and see if we can found an '\n' and
            // Replace it with a <BR>
            for ( int j = 0; j < DBArr[i].length; j++ ) {
                String s = HTMLConv.toHTMLSpecial( DBArr[i][j] );
                dataV.add( s );
            } // End of one records for

            // Lets check if the user is some kind of "Master" eg. if he's
            // reply_level is equal to 1 and add the code returned to data.
            dataV = getReplyLevelCode( dataV, imagePath );

            // Lets parse one record
            htmlStr += this.parseOneRecord( tagsV, dataV, htmlCodeFile );
            //	log("Ett record: " + oneParsedRecordStr);
        } // end of the big for

        return htmlStr;
    } // End of

    /**
     * Parses one record.
     */
    String parseOneRecord( Vector tagsV, Vector dataV, File htmlCodeFile ) throws IOException {

        // Lets parse one aHref reference
        ParsedTextFile parser = new ParsedTextFile( htmlCodeFile, tagsV, dataV );
        String oneRecordsHtmlCode = parser.toString();

        return oneRecordsHtmlCode;
    } // End of parseOneRecord

    /**
     * Returns the users Replylevel htmlcode. If the user is marked with something
     * a bitmap will occur, otherwise nothing will occur.
     */
    private static Vector getReplyLevelCode( Vector dataV, String ImagePath ) {

        // Lets get the information regarding the replylevel
        int index = 5;
        String replyLevel = (String)dataV.elementAt( index );
        String htmlCode;
        String imageStart = "<img src=\"";
        String imageEnd = "\">";

        if ( replyLevel.equals( "1" ) )
            htmlCode = imageStart + ImagePath + imageEnd;
        else
            htmlCode = "";
        //	log("HtmlCode: " + htmlCode) ;
        // Lets add the htmlcode in to the vector at place index
        dataV.insertElementAt( htmlCode, index );
        return dataV;
    }

    /**
     * Collects the parameters from the request object. If a discId is found in the
     * request object, then that discId will be used instead of the session parameter.
     */

    private Properties getParameters( HttpServletRequest req ) {

        // Lets get the standard metainformation
        Properties reqParams = MetaInfo.createPropertiesFromMetaInfoParameters( super.getConferenceSessionParameters( req ) );

        /* Lets get our own variables. We will first look for the discussion_id
           in the request object, other wise, we will get the one from our session object
        */
        String confDiscId = ( req.getParameter( "disc_id" ) == null ) ? "" : ( req.getParameter( "disc_id" ) );
        if ( confDiscId.equals( "" ) ) {
            HttpSession session = req.getSession( false );
            if ( session != null ) {
                confDiscId = (String)session.getAttribute( "Conference.disc_id" );
            }
        }
        //log("GetParameters: " + confDiscId) ;
        reqParams.setProperty( "DISC_ID", confDiscId );
        return reqParams;
    }

} // End of class
