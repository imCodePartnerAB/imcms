package imcode.external.chat;

/*
 *
 * @(#)Chat.java
 *
 *
 * Copyright (c)
 *
 */

import java.io.*;
import java.util.*;

import javax.servlet.*;
import javax.servlet.http.*;

import imcode.external.chat.*;
import imcode.external.diverse.*;

import imcode.util.*;
import imcode.util.log.*;
import imcode.util.IMCServiceRMI;

import imcode.server.*;
import imcode.server.WebAppGlobalConstants;
import org.apache.log4j.Layout;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.apache.log4j.spi.LoggingEvent;

/**
 * superclas for chat servlets.
 *
 * Html template in use:
 * Chat_Admin_Button.htm????
 *
 * Html parstags in use:
 * #ADMIN_TYPE#???
 * #TARGET#???
 *
 * stored procedures in use:
 * -
 *
 *
 */


public class ChatBase extends HttpServlet implements ChatConstants {

    private static final String DATE_PATTERN = ".yyyy-MM-dd"; // chatlog rotate pattern

    private static File absoluteWebAppPath = WebAppGlobalConstants.getInstance().getAbsoluteWebAppPath();

    Logger log = Logger.getLogger( "imcode.external.chat.ChatBase" );
    private static final int CHAT_PROPERTY_USER_MAY_CHOOSE = 3;
    private static final int CHAT_PROPERTY_DISABLED = 2;

    /**
     Log function. Logs the message to the log file and console
     */

    public void log( String msg ) {
        log.debug( msg );
    }

    /**
     * Log functions for Chat, log to file " WEB-INF\logs\chat-metaId.log"   ( chat-1003.log   )
     */
    private LoggingEvent getLoggingEvent( String msg ) {
        return new LoggingEvent( MockLogger.class.getName(), new MockLogger( "mock" ), Priority.INFO, msg, null );
    }

    private static DailyRollingFileAppender createAppender( File filePath ) throws IOException {
        String fileName = filePath.getPath();
        Layout layout = new PatternLayout( "%d{ISO8601} %m%n" );
        DailyRollingFileAppender appender = new DailyRollingFileAppender( layout, fileName, DATE_PATTERN );
        return appender;
    }

    private static File getLogFile( String metaId ) {
        File filePath = new File( absoluteWebAppPath + "/WEB-INF/logs/", "chat-" + metaId + ".log" );
        return filePath;
    }

    private class MockLogger extends Logger {

        public MockLogger( String name ) {
            super( name );
        }
    }

    public void chatlog( String metaId, String msg ) throws IOException, InterruptedException {
        File logFile = getLogFile( metaId );
        DailyRollingFileAppender appender = createAppender( logFile );
        appender.doAppend( getLoggingEvent( msg ) );
    }

    /**
     Collects the parameters from the request object
     **/

    protected Properties getNewChatParameters( HttpServletRequest req ) throws ServletException, IOException {
        Properties chatP = new Properties();

        return chatP;
    }

    //peter keep
    protected Chat createChat( HttpServletRequest req, imcode.server.User user, int metaId ) throws ServletException, IOException {
        IMCPoolInterface chatref = IMCServiceRMI.getChatIMCPoolInterface( req );

        //lets get the standard stuff
        Vector msgTypes = convert2Vector( chatref.sqlProcedureMulti( "C_GetTheMsgTypesBase" ) );
        Vector autTypes = convert2Vector( chatref.sqlProcedureMulti( "C_GetAuthorizationTypes" ) );
        Chat myChat = new Chat( metaId, autTypes, msgTypes );

        String[] selAuto = chatref.sqlProcedure( "C_GetChatAutoTypes", new String[]{"" + metaId} );
        if ( selAuto == null ) {
            selAuto = new String[1];
            selAuto[0] = "1";
        } else if ( selAuto.length == 0 ) {
            selAuto = new String[1];
            selAuto[0] = "1";
        }
        myChat.setSelectedAuto( selAuto );


        String[][] messages = chatref.sqlProcedureMulti( "C_GetMsgTypes", new String[]{"" + metaId} );
        if ( messages != null ) {
            if ( messages.length > 0 ) {
                myChat.setMsgTypes( convert2Vector( messages ) );
            }
        }


        //updateTime,reload,inOut,privat,publik,dateTime,font
        String[] params = chatref.sqlProcedure( "C_GetChatParameters ", new String[]{"" + metaId} );
        if ( params != null ) {
            if ( params.length == 7 ) {
                myChat.setRefreshTime( Integer.parseInt( params[0] ) );
                myChat.setAutoRefreshEnabled( Integer.parseInt( params[1] ) );
                myChat.setShowEnterAndLeaveMessagesEnabled( Integer.parseInt( params[2] ) );
                myChat.setShowPrivateMessagesEnabled( Integer.parseInt( params[3] ) );
                myChat.setShowDateTimesEnabled( Integer.parseInt( params[5] ) );
                myChat.setFontSize( Integer.parseInt( params[6] ) );
            }
        }


        String[][] rooms = chatref.sqlProcedureMulti( "C_GetRooms", new String[]{"" + metaId} );
        if ( rooms != null ) {
            for ( int i = 0; i < rooms.length; i++ ) {
                myChat.createNewChatGroup( Integer.parseInt( rooms[i][0] ), rooms[i][1] );
            }
        }
        return myChat;
    }

    //peter keep
    public static String createOptionCode( Vector selected, Vector data ) {
        StringBuffer buff = new StringBuffer( "" );
        for ( int i = 0; i < data.size(); i += 2 ) {
            buff.append( "<option value=\"" + data.elementAt( i ).toString() + "\"" );
            for ( int e = 0; e < selected.size(); e++ ) {
                String sel = selected.elementAt( e ).toString();
                if ( sel != null ) {
                    if ( data.elementAt( i ).toString().equals( sel ) ) {
                        buff.append( " selected " );
                    }
                }
            }
            buff.append( ">" );
            buff.append( data.elementAt( i + 1 ).toString() + "</option>\n" );
        }
        return buff.toString();
    }

    //peter keep
    public static String createOptionCode( String selected, Vector data ) {
        StringBuffer buff = new StringBuffer( "" );
        for ( int i = 0; i < data.size(); i += 2 ) {
            buff.append( "<option value=\"" + data.elementAt( i ).toString() + "\"" );
            if ( selected != null ) {
                if ( data.elementAt( i ).toString().equals( selected ) ) {
                    buff.append( " selected " );
                }
            }
            buff.append( ">" );
            buff.append( data.elementAt( i + 1 ).toString() + "</option>" );
        }
        return buff.toString();
    }

    //peter keep
    public static String createRadioButton( String buttonName, Vector data, String selected ) {
        StringBuffer buff = new StringBuffer( "" );
        ;
        for ( int i = 0; i < data.size(); i++ ) {
            buff.append( "<input type=\"radio\" name=\"" + buttonName + "\" value=\"" );
            buff.append( data.elementAt( i ).toString() + "\"" );

            if ( selected == null )
                selected = "3";
            if ( data.elementAt( i ).toString().equals( selected ) ) {
                buff.append( " checked " );
            }
            buff.append( ">\n" );
        }
        return buff.toString();
    }


    /**
     Returns the metaId from a request object, if not found, we will
     get the one from our session object. If still not found then null is returned.
     */
    public String getMetaId( HttpServletRequest req ) throws ServletException, IOException {
        String metaId = req.getParameter( "meta_id" );
        if ( metaId == null ) {
            HttpSession session = req.getSession( false );
            if ( session != null ) {
                metaId = (String)session.getAttribute( "Chat.meta_id" );
            }
        }
        return metaId;
    }


    /**
     Returns an user object. If an error occurs, an errorpage will be generated.
     */

    protected imcode.server.User getUserObj( HttpServletRequest req,
                                             HttpServletResponse res ) throws ServletException, IOException {

        if ( checkSession( req, res ) == true ) {

            // Get the session
            HttpSession session = req.getSession( true );
            // Does the session indicate this user already logged in?
            Object done = session.getAttribute( "logon.isDone" );  // marker object
            imcode.server.User user = (imcode.server.User)done;

            return user;
        } else {
            String header = "Chat servlet.";
            ChatError err = new ChatError( req, res, header, 2 );
            log( err.getErrorMsg() );
            return null;
        }
    }

    // *************** LETS HANDLE THE SESSION META PARAMETERS *********************


    /**
     Collects the standard parameters from the session object
     **/

    public Properties getSessionParameters( HttpServletRequest req ) throws ServletException, IOException {
        // Get the session
        HttpSession session = req.getSession( true );
        String metaId = ( (String)session.getAttribute( "Chat.meta_id" ) == null ) ? "" : ( (String)session.getAttribute( "Chat.meta_id" ) );
        String parentId = ( (String)session.getAttribute( "Chat.parent_meta_id" ) == null ) ? "" : ( (String)session.getAttribute( "Chat.parent_meta_id" ) );

        Properties reqParams = new Properties();
        reqParams.setProperty( "META_ID", metaId );
        reqParams.setProperty( "PARENT_META_ID", parentId );
        return reqParams;
    }


    /** TODO;
     Skall vi inte kapa bort allt med chatHelp?????

     Collects the EXTENDED parameters from the session object. As extended paramters are we
     counting:

     Chat.forum_id
     Chat.discussion_id

     _@Parameter: Properties params, if a properties object is passed, we will fill the
     object with the extended paramters, otherwise we will create one.
     **/

    public Properties getExtSessionParameters( HttpServletRequest req, Properties params )
            throws ServletException, IOException {

        // Get the session
        HttpSession session = req.getSession( true );
        String forumId = ( (String)session.getAttribute( "Chat.forum_id" ) == null ) ? "" : ( (String)session.getAttribute( "Chat.forum_id" ) );
        String discId = ( (String)session.getAttribute( "Chat.disc_id" ) == null ) ? "" : ( (String)session.getAttribute( "Chat.disc_id" ) );

        if ( params == null )
            params = new Properties();
        params.setProperty( "FORUM_ID", forumId );
        params.setProperty( "DISC_ID", discId );
        return params;
    }


    /**
     Verifies that the user has logged in. If he hasnt, he will be redirected to
     an url which we get from a init file name conference.
     */

    protected boolean checkSession( HttpServletRequest req, HttpServletResponse res ) throws ServletException, IOException {

        // Get the session
        HttpSession session = req.getSession( true );
        // Does the session indicate this user already logged in?
        User user = (User)session.getAttribute( "logon.isDone" );  // marker object
        //imcode.server.User user = (imcode.server.User) done ;

        if ( user == null ) {
            // No logon.isDone means he hasn't logged in.
            // Save the request URL as the true target and redirect to the login page.
            session.setAttribute( "login.target", HttpUtils.getRequestURL( req ).toString() );

            IMCServiceInterface imcref = IMCServiceRMI.getIMCServiceInterface( req );
            String startUrl = imcref.getStartUrl();
            res.sendRedirect( startUrl );
            return false;
        }
        return true;
    }

    // *************** LETS HANDLE THE STANDARD META PARAMETERS *********************


    // *************************** END OF META PARAMETER FUNCTIONS *****************


    // *************************** ADMIN RIGHTS FUNCTIONS **************************


    // *********************** GETEXTERNAL TEMPLATE FUNCTIONS *********************

    /**
     Gives the folder to the root external folder,Example /templates/se/102/
     */
    public File getExternalTemplateRootFolder( HttpServletRequest req ) throws ServletException, IOException {

        IMCServiceInterface imcref = IMCServiceRMI.getIMCServiceInterface( req );
        // Lets get serverinformation
        String metaId = this.getMetaId( req );
        if ( metaId == null ) {
            log( "No meta_id could be found! Error in Chat.class" );
            throw new IllegalArgumentException();
        }
        return imcref.getExternalTemplateFolder( Integer.parseInt( metaId ) );
    }


    /**
     Gives the folder where All the html templates for a language are located.
     This method will call its helper method getTemplateLibName to get the
     name of the folder which contains the templates for a certain meta id
     */
    public File getExternalTemplateFolder( HttpServletRequest req ) throws ServletException, IOException {
        IMCServiceInterface imcref = IMCServiceRMI.getIMCServiceInterface( req );
        IMCPoolInterface chatref = IMCServiceRMI.getChatIMCPoolInterface( req );

        String metaId = this.getMetaId( req );
        if ( metaId == null ) {
            log( "No meta_id could be found! Error in Chat.class" );
            throw new IllegalArgumentException();
        }
        // Lets get serverinformation
        return new File( imcref.getExternalTemplateFolder( Integer.parseInt( metaId ) ), this.getTemplateLibName( chatref, metaId ) );
    }


    /**
     Returns the foldername where the templates are situated for a certain metaid.
     **/ //peter uses this
    protected String getTemplateLibName( IMCPoolInterface chatref, String meta_id )
            throws ServletException, IOException {
        String libName = chatref.sqlProcedureStr( "C_GetTemplateLib", new String[]{"" + meta_id} );
        if ( libName == null ) {
            libName = "original";
        }
        return libName;
    } // End of getTemplateLibName



    //************************ END GETEXTERNAL TEMPLATE FUNCTIONS ***************

    /**
     SendHtml. Generates the html page to the browser. Uses the templatefolder
     by taking the metaid from the request object to determind the templatefolder.
     Will by default handle maximum 3 servletadresses.
     */
    public void sendHtml( HttpServletRequest req, HttpServletResponse res,
                          Vector vect, String template, Chat chat ) throws ServletException, IOException {

        IMCServiceInterface imcref = IMCServiceRMI.getIMCServiceInterface( req );
        IMCPoolInterface chatref = IMCServiceRMI.getChatIMCPoolInterface( req );

        String metaId;
        if ( chat != null ) {
            metaId = chat.getChatIdStr();
        } else {
            metaId = this.getMetaId( req );
        }
        // Lets get the TemplateFolder  and the foldername used for this certain metaid
        String templateSet = this.getTemplateLibName( chatref, metaId );

        res.setContentType( "text/html" );
        ServletOutputStream out = res.getOutputStream();
        out.print( imcref.parseExternalDoc( vect, template, imcref.getLanguage(), "103", templateSet ) );
        out.flush();
        out.close();
    }


    /**
     Converts array to vector
     */

    public Vector convert2Vector( String[] arr ) {
        Vector rolesV = new Vector();
        for ( int i = 0; i < arr.length; i++ )
            rolesV.add( arr[i] );
        return rolesV;
    }

    public synchronized Vector convert2Vector( String[][] arr ) {
        Vector rolesV = new Vector();
        for ( int i = 0; i < arr.length; i++ ) {
            for ( int e = 0; e < arr[i].length; e++ ) {
                rolesV.add( arr[i][e] );
            }
        }
        return rolesV;
    }


    // ****************** GetImageFolder Functions *********************

    /**
     Gives the folder where All the html templates for a language are located.
     This method will call its helper method getTemplateLibName to get the
     name of the folder which contains the templates for a certain meta id
     */

    public String getExternalImageFolder( HttpServletRequest req, HttpServletResponse res ) throws ServletException, IOException {
        IMCServiceInterface imcref = IMCServiceRMI.getIMCServiceInterface( req );
        IMCPoolInterface chatref = IMCServiceRMI.getChatIMCPoolInterface( req );

        String metaId = this.getMetaId( req );
        if ( metaId == null ) {
            log( "No meta_id could be found! Error in Chat.class" );
            return "No meta_id could be found!";
        }

        String extFolder = RmiConf.getExternalImageFolder( imcref, metaId );
        return extFolder += this.getTemplateLibName( chatref, metaId );
    }



    /**
     * checks if user is authorized
     * @param req
     * @param res is used if error (send user to conference_starturl )
     * @param user
     */

    //används av bla ChatViewer
    protected boolean isUserAuthorized( HttpServletRequest req, HttpServletResponse res, imcode.server.User user )
            throws ServletException, IOException {

        HttpSession session = req.getSession( true );

        //lets get if user authorized or not
        boolean authorized = true;

        //OBS "Chat.meta_id" ska bytas ut mot en konstant senare
        String stringMetaId = (String)session.getAttribute( "Chat.meta_id" );
        if ( stringMetaId == null ) {
            authorized = false;
            //lets send unauthorized users out
            IMCServiceInterface imcref = IMCServiceRMI.getIMCServiceInterface( req );
            String startUrl = imcref.getStartUrl();
            res.sendRedirect( startUrl );
        } else {
            int metaId = Integer.parseInt( stringMetaId );
            authorized = isUserAuthorized( req, res, metaId, user );
        }


        return authorized;
    }

    /**
     * checks if user is authorized
     * @param req is used for collecting serverinfo and session
     * @param res is used if error (send user to conference_starturl )
     * @param metaId conference metaId
     * @param user
     */
    protected boolean isUserAuthorized( HttpServletRequest req, HttpServletResponse res, int metaId, imcode.server.User user )
            throws ServletException, IOException {

        IMCServiceInterface imcref = IMCServiceRMI.getIMCServiceInterface( req );

        //is user authorized?
        boolean authorized = imcref.checkDocRights( metaId, user );

        //lets send unauthorized users out
        if ( !authorized ) {
            String startUrl = imcref.getStartUrl();
            res.sendRedirect( startUrl );
        }

        return authorized;
    }

    /**
     * check if user is admin and has rights to edit
     * @param imcref imCMS IMCServiceInterface instance
     * @param metaId metaId for conference
     * @param user
     */
    protected boolean userHasAdminRights( IMCServiceInterface imcref, int metaId,
                                          imcode.server.User user ) throws java.io.IOException {
        return ( imcref.checkDocAdminRights( metaId, user ) &&
                imcref.checkDocAdminRights( metaId, user, 65536 ) );

    }


    //**************** does the setup for chatboard  **********************
    //lets get the settings for the chat and convert them
    //and add them into HashTable and add it into the session
    //ugly it should moves into the ChatMember obj, but i haven't got the time to do it now
    public synchronized void prepareChatBoardSettings( ChatMember member, HttpServletRequest req, boolean memberIsInChat ) {
        //now we sets up the settings for this chat
        HttpSession session = req.getSession( true );

        Chat chat = member.getParent();

        member.setShowDateTimesEnabled( isShowDateTimesEnabled( chat, memberIsInChat, req )) ;
        member.setShowPrivateMessagesEnabled(isShowPrivateMessagesEnabled( chat, memberIsInChat, req )) ;
        member.setShowEnterAndLeaveMessagesEnabled(isShowEnterAndLeaveMessagesEnabled( chat, memberIsInChat, req )) ;
        member.setAutoRefreshEnabled(isAutoRefreshEnabled( chat, memberIsInChat, req )) ;
        member.setFontSize(getFontSize( chat, req )) ;

        //member.setProperties(hash);
        session.setAttribute( "theChatMember", member );

    }//end prepareSettings

    private static int getFontSize( Chat chat, HttpServletRequest req ) {
        int fonSize = chat.getfontSize();
        if ( req.getParameter( "font" ) != null ) {
            fonSize = Integer.parseInt( req.getParameter( "font" ) );
        }
        return fonSize;
    }

    private static boolean isAutoRefreshEnabled( Chat chat, boolean memberIsInChat, HttpServletRequest req ) {
        boolean onOff;
        //sets up autoreload on off
        int reload = chat.isAutoRefreshEnabled();

        if ( reload == CHAT_PROPERTY_DISABLED ) {
            onOff = false;
        } else {
            if ( memberIsInChat ) {
                if ( reload == CHAT_PROPERTY_USER_MAY_CHOOSE ) {
                    onOff = req.getParameter( "reload" ) == null ? false : true;
                } else {
                    onOff = false;
                }
            } else {
                onOff = true;
            }
        }
        return onOff;
    }

    private static boolean isShowEnterAndLeaveMessagesEnabled( Chat chat, boolean bool, HttpServletRequest req ) {
        boolean onOff;
        int inOut = chat.isShowEnterAndLeaveMessagesEnabled();
        if ( inOut == CHAT_PROPERTY_DISABLED ) {
            onOff = false;
        } else {
            if ( bool ) {
                if ( inOut == CHAT_PROPERTY_USER_MAY_CHOOSE ) {
                    onOff = req.getParameter( "inOut" ) == null ? false : true;
                } else {
                    onOff = true;
                }
            } else {
                onOff = true;
            }
        }
        return onOff;
    }

    private static boolean isShowDateTimesEnabled( Chat chat, boolean bool, HttpServletRequest req ) {
        boolean onOff;
        //sets up show datTime or not
        int dateTime = chat.isShowDateTimesEnabled();
        if ( dateTime == CHAT_PROPERTY_DISABLED ) {
            onOff = false;
        } else {
            if ( bool ) {
                if ( dateTime == CHAT_PROPERTY_USER_MAY_CHOOSE ) {
                    onOff = req.getParameter( "dateTime" ) == null ? false : true;
                } else {
                    onOff = true;
                }
            } else {
                onOff = false;    //default dateTime will be off
            }
        }
        return onOff;
    }

    private static boolean isShowPrivateMessagesEnabled( Chat chat, boolean bool, HttpServletRequest req ) {
        boolean onOff;
        //sets up show private msg or not
        int privat = chat.isShowPrivateMessagesEnabled();
        if ( privat == CHAT_PROPERTY_DISABLED ) {
            onOff = false;
        } else {
            if ( bool ) {
                if ( privat == CHAT_PROPERTY_USER_MAY_CHOOSE ) {
                    onOff = req.getParameter( "private" ) == null ? false : true;
                } else {
                    onOff = true;
                }
            } else {
                onOff = true;
            }
        }
        return onOff;
    }


    //*****************cleares the session from all chat params ***************
    //the only ones left is
    //logon_isDone and browser_id

    public static void cleanUpSessionParams( HttpSession session ) {

        if ( session.getAttribute( "theChatMember" ) != null ) {
            //System.out.println("ok we have the member so lets rock");
            ChatMember myMember = (ChatMember)session.getAttribute( "theChatMember" );
            Chat myChat = myMember.getParent();
            ChatGroup myGroup = myMember.getGroup();

            int senderNr = myMember.getMemberId();
            ChatSessionsSingleton.removeSession( myMember );

            myGroup.removeGroupMember( myMember );
            myChat.removeChatMember( senderNr );
        } else {
            System.out.println( "theChat null so it doesn't rock at all" );
        }

        //lets clear old session attribute
        try {
            session.removeAttribute( "theChatMember" );
            session.removeAttribute( "theRoom" );
            session.removeAttribute( "myChat" );
            session.removeAttribute( "Chat.meta_id" );
            session.removeAttribute( "Chat.parent_meta_id" );
            session.removeAttribute( "Chat.forum_id" );
            session.removeAttribute( "Chat.disc_id" );
        } catch ( IllegalStateException ise ) {
            // ignored
        }
    }

    protected void createLeaveMessageAndAddToGroup( ChatMember myMember, ChatSystemMessage systemMessage, IMCPoolInterface chatref,  IMCServiceInterface imcref  )throws ServletException, IOException {
        Chat theChat = myMember.getParent();
        User user = myMember.getUser();
        ChatGroup myGroup = myMember.getGroup();
        String metaId = ""+theChat.getChatId();
        //lets send the message
        myGroup.addNewMsg( this, systemMessage, imcref, chatref);
        String libName = getTemplateLibName( chatref, theChat.getChatId() + "" );
        try {
            chatlog( metaId, systemMessage.getLogMsg( imcref, user, libName ) );
        } catch ( InterruptedException ie ) {
            log( "ChatLogin, InterruptedException when loging message" );
        }
    }

    protected void createChangeRoomMessageAndAddToMembersGroup( ChatMember myMember, int message, IMCServiceInterface imcref, IMCPoolInterface chatref  ) throws IOException, ServletException {
        ChatSystemMessage systemMessage = new ChatSystemMessage(myMember,message) ;
        ChatGroup chatGroup = myMember.getGroup();
        chatGroup.addNewMsg( this, systemMessage, imcref, chatref);
    }

    protected void createEnterMessageAndAddToGroup( IMCPoolInterface chatref, Chat theChat, IMCServiceInterface imcref, User user, ChatMember myMember, ChatGroup myGroup, String metaId ) throws ServletException, IOException {

        ChatSystemMessage systemMessage = new ChatSystemMessage(myMember, ChatSystemMessage.ENTER_MSG) ;
        //lets send the message
        myGroup.addNewMsg( this, systemMessage, imcref,chatref);
        String libName = getTemplateLibName( chatref, theChat.getChatId() + "" );

        try {
            chatlog( metaId, systemMessage.getLogMsg( imcref, user, libName ) );
        } catch ( InterruptedException ie ) {
            log( "ChatLogin, InterruptedException when loging message" );
        }
    }

    protected void createKickOutMessageAndAddToGroup( ChatMember kickedOutPerson, IMCPoolInterface chatref, Chat myChat, IMCServiceInterface imcref, User user, ChatMember kicker, ChatGroup myGroup, String metaId ) throws ServletException, IOException {

        ChatSystemMessage systemMessage = new ChatSystemMessage(kickedOutPerson, ChatSystemMessage.KICKOUT_MSG) ;
        myGroup.addNewMsg( this, systemMessage, imcref, chatref);
        String libName = getTemplateLibName( chatref, myChat.getChatId() + "" );

        try {
            chatlog( metaId, systemMessage.getLogMsg( imcref, user, libName ) );
        } catch ( InterruptedException ie ) {
            log( "ChatControl, InterruptedException when login admin kickout message" );
        }
    }

    public String getMessageTypeString( String templateName, IMCPoolInterface chatref, Chat myChat, IMCServiceInterface imcref, User user ) throws ServletException, IOException {
        String libName = getTemplateLibName( chatref, myChat.getChatId() + "" );
        return imcref.parseExternalDoc( new Vector(), templateName, user.getLangPrefix(), "103", libName ).trim();
    }

    public void logOutMember( ChatMember myMember, ChatSystemMessage systemMessage, IMCServiceInterface imcref, IMCPoolInterface chatref ) throws IOException, ServletException {

        HttpSession session = ChatSessionsSingleton.removeSession(myMember) ;

        if ( session != null){
            cleanUpSessionParams( session );
            createLeaveMessageAndAddToGroup( myMember, systemMessage, chatref, imcref );
        }

    }




} // End class
