package imcode.external.chat;

import imcode.server.Imcms;
import imcode.server.ImcmsServices;
import imcode.server.WebAppGlobalConstants;
import imcode.server.document.DocumentDomainObject;
import imcode.server.document.DocumentMapper;
import imcode.server.user.UserDomainObject;
import imcode.util.Utility;
import imcode.util.log.DailyRollingFileAppender;
import org.apache.log4j.*;
import org.apache.log4j.spi.LoggingEvent;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.*;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

/**
 * superclas for chat servlets.
 * <p/>
 * Html template in use:
 * Chat_Admin_Button.htm????
 * <p/>
 * Html parstags in use:
 * #ADMIN_TYPE#???
 * #TARGET#???
 * <p/>
 * stored procedures in use:
 * -
 */

public class ChatBase extends HttpServlet implements ChatConstants {

    private static final String DATE_PATTERN = ".yyyy-MM-dd"; // chatlog rotate pattern

    private static final int CHAT_PROPERTY_USER_MAY_CHOOSE = 3;
    private static final int CHAT_PROPERTY_DISABLED = 2;

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
        File filePath = new File( WebAppGlobalConstants.getInstance().getAbsoluteWebAppPath() + "/WEB-INF/logs/", "chat-" + metaId + ".log" );
        return filePath;
    }

    private class MockLogger extends Logger {

        private MockLogger( String name ) {
            super( name );
        }
    }

    protected void chatlog( String metaId, String msg ) throws IOException {
        File logFile = getLogFile( metaId );
        DailyRollingFileAppender appender = createAppender( logFile );
        appender.doAppend( getLoggingEvent( msg ) );
    }

    protected Chat createChat( int metaId ) {

        ImcmsServices imcref = Imcms.getServices() ;
        //lets get the standard stuff
        Vector msgTypes = convert2Vector( imcref.getDatabase().execute2dArrayProcedure( "C_GetTheMsgTypesBase", new String[0] ) );
        Vector autTypes = convert2Vector( imcref.getDatabase().execute2dArrayProcedure( "C_GetAuthorizationTypes", new String[0] ) );
        Chat myChat = new Chat( metaId, autTypes, msgTypes );

        String[] selAuto = imcref.getDatabase().executeArrayProcedure( "C_GetChatAutoTypes", new String[] {""
                                                                                                                              + metaId} );
        if ( selAuto == null ) {
            selAuto = new String[1];
            selAuto[0] = "1";
        } else if ( selAuto.length == 0 ) {
            selAuto = new String[1];
            selAuto[0] = "1";
        }
        myChat.setSelectedAuto( selAuto );

        String[][] messages = imcref.getDatabase().execute2dArrayProcedure( "C_GetMsgTypes", new String[] {""
                                                                                                                              + metaId} );
        if ( messages != null ) {
            if ( messages.length > 0 ) {
                myChat.setMsgTypes( convert2Vector( messages ) );
            }
        }


        //updateTime,reload,inOut,privat,publik,dateTime,font
        String[] params = imcref.getDatabase().executeArrayProcedure( "C_GetChatParameters ", new String[] {""
                                                                                                                               + metaId} );
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

        return myChat;
    }

    protected static String createOptionCode( Vector selected, Vector data ) {
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

    protected static String createOptionCode( String selected, Vector data ) {
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

    protected static String createRadioButton( String buttonName, Vector data, String selected ) {
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
     * Returns the metaId from a request object, if not found, we will
     * get the one from our session object. If still not found then null is returned.
     */
    protected int getMetaId( HttpServletRequest req ) {
        String metaId = req.getParameter( "meta_id" );
        if ( metaId == null ) {
            HttpSession session = req.getSession( false );
            if ( session != null ) {
                metaId = (String)session.getAttribute( "Chat.meta_id" );
            }
        }
        return Integer.parseInt(metaId);
    }

    /**
     * Returns an user object. If an error occurs, an errorpage will be generated.
     */

    protected imcode.server.user.UserDomainObject getUserObj( HttpServletRequest req ) {

        UserDomainObject user = Utility.getLoggedOnUser( req );
        return user;
    }

    // *************** LETS HANDLE THE SESSION META PARAMETERS *********************


    /**
     * Collects the standard parameters from the session object
     */

    protected Properties getSessionParameters( HttpServletRequest req ) {
        // Get the session
        HttpSession session = req.getSession( true );
        String metaId = ( (String)session.getAttribute( "Chat.meta_id" ) == null ) ? "" : ( (String)session.getAttribute( "Chat.meta_id" ) );
        String parentId = ( (String)session.getAttribute( "Chat.parent_meta_id" ) == null ) ? "" : ( (String)session.getAttribute( "Chat.parent_meta_id" ) );

        Properties reqParams = new Properties();
        reqParams.setProperty( "META_ID", metaId );
        reqParams.setProperty( "PARENT_META_ID", parentId );
        return reqParams;
    }

    /**
     * TODO;
     * Skall vi inte kapa bort allt med chatHelp?????
     * <p/>
     * Collects the EXTENDED parameters from the session object. As extended paramters are we
     * counting:
     * <p/>
     * Chat.forum_id
     * Chat.discussion_id
     * <p/>
     * _@Parameter: Properties params, if a properties object is passed, we will fill the
     * object with the extended paramters, otherwise we will create one.
     */

    protected void getExtSessionParameters( HttpServletRequest req, Properties params ) {

        // Get the session
        HttpSession session = req.getSession( true );
        String forumId = ( (String)session.getAttribute( "Chat.forum_id" ) == null ) ? "" : ( (String)session.getAttribute( "Chat.forum_id" ) );
        String discId = ( (String)session.getAttribute( "Chat.disc_id" ) == null ) ? "" : ( (String)session.getAttribute( "Chat.disc_id" ) );

        if ( params == null )
            params = new Properties();
        params.setProperty( "FORUM_ID", forumId );
        params.setProperty( "DISC_ID", discId );
    }

    /**
     * Returns the foldername where the templates are situated for a certain metaid.
     * <p/>
     * peter uses this
     * <p/>
     * peter uses this
     */
    //peter uses this
    protected static String getTemplateSetDirectoryName( int meta_id ) {
        String libName = Imcms.getServices().getDatabase().executeStringProcedure( "C_GetTemplateLib", new String[] {""
                                                                                                                                        + meta_id} );
        if ( libName == null ) {
            libName = "original";
        }
        return libName;
    } // End of getTemplateSetDirectoryName



    //************************ END GETEXTERNAL TEMPLATE FUNCTIONS ***************

    /**
     * SendHtml. Generates the html page to the browser. Uses the templatefolder
     * by taking the metaid from the request object to determind the templatefolder.
     * Will by default handle maximum 3 servletadresses.
     */
    protected void sendHtml( HttpServletRequest req, HttpServletResponse res,
                             Vector vect, String template, Chat chat ) throws IOException {

        ImcmsServices imcref = Imcms.getServices();

        UserDomainObject user = Utility.getLoggedOnUser( req );

        int metaId;
        if ( chat != null ) {
            metaId = chat.getChatId();
        } else {
            metaId = this.getMetaId( req );
        }
        // Lets get the TemplateFolder  and the foldername used for this certain metaid
        String templateSet = getTemplateSetDirectoryName( metaId );

        Utility.setDefaultHtmlContentType( res );
        ServletOutputStream out = res.getOutputStream();
        final String htmlStr = imcref.getTemplateFromSubDirectoryOfDirectory( template, user, vect, "103", templateSet );
        out.print( htmlStr );
        out.flush();
        out.close();
    }

    /**
     * Converts array to vector
     */

    protected Vector convert2Vector( String[] arr ) {
        Vector rolesV = new Vector();
        for ( int i = 0; i < arr.length; i++ )
            rolesV.add( arr[i] );
        return rolesV;
    }

    protected synchronized Vector convert2Vector( String[][] arr ) {
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
     * Gives the folder where All the html templates for a language are located.
     * This method will call its helper method getTemplateSetDirectoryName to get the
     * name of the folder which contains the templates for a certain meta id
     */

    protected String getExternalImageFolder( HttpServletRequest req ) {
        ImcmsServices imcref = Imcms.getServices();

        UserDomainObject user = Utility.getLoggedOnUser( req );

        String lang_prefix = imcref.getDefaultLanguage();
        if(user != null){
            lang_prefix = user.getLanguageIso639_2();
        }

        int metaId = getMetaId( req );

        String extFolder = "/imcms/" + lang_prefix + "/images/"
                        + imcref.getDocType(metaId) + '/';
        return extFolder += getTemplateSetDirectoryName( metaId );
    }

    /**
     * checks if user is authorized
     *
     * @param req
     * @param res  is used if error (send user to conference_starturl )
     * @param user används av bla ChatViewer
     *             <p/>
     *             används av bla ChatViewer
     */

    //används av bla ChatViewer
    protected boolean isUserAuthorized( HttpServletRequest req, HttpServletResponse res, UserDomainObject user )
            throws IOException {

        HttpSession session = req.getSession( true );

        //lets get if user authorized or not
        boolean authorized;

        //OBS "Chat.meta_id" ska bytas ut mot en konstant senare
        String stringMetaId = (String)session.getAttribute( "Chat.meta_id" );
        if ( stringMetaId == null ) {
            authorized = false;

            Utility.redirectToStartDocument( req, res );
        } else {
            int metaId = Integer.parseInt( stringMetaId );
            authorized = isUserAuthorized( res, metaId, user, req );
        }

        return authorized;
    }

    /**
     * checks if user is authorized
     *
     * @param res    is used if error (send user to conference_starturl )
     * @param metaId conference metaId
     * @param user
     */
    protected boolean isUserAuthorized( HttpServletResponse res, int metaId,
                                        UserDomainObject user, HttpServletRequest req )
            throws IOException {

        ImcmsServices imcref = Imcms.getServices();

        //is user authorized?
        DocumentMapper documentMapper = imcref.getDocumentMapper();
        DocumentDomainObject document = documentMapper.getDocument(metaId);
        boolean authorized = user.canAccess( document );

        //lets send unauthorized users out
        if ( !authorized ) {
            Utility.redirectToStartDocument( req, res );
        }

        return authorized;
    }

    //**************** does the setup for chatboard  **********************
    //lets get the settings for the chat and convert them
    //and add them into HashTable and add it into the session
    //ugly it should moves into the ChatMember obj, but i haven't got the time to do it now
    protected synchronized void prepareChatBoardSettings( ChatMember member, HttpServletRequest req, boolean memberIsInChat ) {
        //now we sets up the settings for this chat
        HttpSession session = req.getSession( true );

        Chat chat = member.getParent();

        member.setShowDateTimesEnabled( isShowDateTimesEnabled( chat, memberIsInChat, req ) );
        member.setShowPrivateMessagesEnabled( isShowPrivateMessagesEnabled( chat, memberIsInChat, req ) );
        member.setShowEnterAndLeaveMessagesEnabled( isShowEnterAndLeaveMessagesEnabled( chat, memberIsInChat, req ) );
        member.setAutoRefreshEnabled( isAutoRefreshEnabled( chat, memberIsInChat, req ) );
        if ( member.isAutoRefreshEnabled() ) {
            if ( req.getParameter( "updateinterval" ) != null ) {
                member.setRefreshTime( Integer.parseInt( req.getParameter( "updateinterval" ) ) );
            }
        }
        member.setFontSize( getFontSize( chat, req ) );

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

    protected static void cleanUpSessionParams( HttpSession session ) {

        if ( session.getAttribute( "theChatMember" ) != null ) {
            //System.out.println("ok we have the member so lets rock");
            ChatMember myMember = (ChatMember)session.getAttribute( "theChatMember" );
            Chat myChat = myMember.getParent();
            ChatGroup myGroup = myMember.getGroup();

            int senderNr = myMember.getMemberId();
            ChatSessionsSingleton.removeSession( myMember );

            myGroup.removeGroupMember( myMember );
            myChat.removeChatMember( senderNr );
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

    void createLeaveMessageAndAddToGroup( ChatMember myMember, ChatSystemMessage systemMessage,
                                          ImcmsServices imcref ) throws ServletException, IOException {
        Chat theChat = myMember.getParent();
        UserDomainObject user = myMember.getUser();
        ChatGroup myGroup = myMember.getGroup();
        String metaId = "" + theChat.getChatId();
        //lets send the message
        myGroup.addNewMsg( this, systemMessage, imcref );
        String libName = getTemplateSetDirectoryName( theChat.getChatId() );
        chatlog( metaId, systemMessage.getLogMsg( imcref, user, libName ) );
    }

    protected void createEnterMessageAndAddToGroup( Chat theChat, ImcmsServices imcref,
                                                    UserDomainObject user, ChatMember myMember, ChatGroup myGroup,
                                                    String metaId ) throws ServletException, IOException {

        ChatSystemMessage systemMessage = new ChatSystemMessage( myMember, ChatSystemMessage.ENTER_MSG );
        //lets send the message
        myGroup.addNewMsg( this, systemMessage, imcref );
        String libName = getTemplateSetDirectoryName( theChat.getChatId() );

        chatlog( metaId, systemMessage.getLogMsg( imcref, user, libName ) );
    }

    protected void createKickOutMessageAndAddToGroup( ChatMember kickedOutPerson,
                                                      Chat myChat, ImcmsServices imcref, UserDomainObject user,
                                                      ChatGroup myGroup, String metaId ) throws ServletException, IOException {

        ChatSystemMessage systemMessage = new ChatSystemMessage( kickedOutPerson, ChatSystemMessage.KICKOUT_MSG );
        myGroup.addNewMsg( this, systemMessage, imcref );
        String libName = getTemplateSetDirectoryName( myChat.getChatId() );

        chatlog( metaId, systemMessage.getLogMsg( imcref, user, libName ) );
    }

    protected void logOutMember( ChatMember myMember, ChatSystemMessage systemMessage, ImcmsServices imcref ) throws IOException, ServletException {

        HttpSession session = ChatSessionsSingleton.removeSession( myMember );

        if ( session != null ) {
            cleanUpSessionParams( session );
            if ( systemMessage != null ) {
                createLeaveMessageAndAddToGroup( myMember, systemMessage, imcref );
            }
        }

    }

    protected String getTemplate( String htmlFile, UserDomainObject user, List tagsAndData ) {
        return Imcms.getServices().getTemplateFromSubDirectoryOfDirectory( htmlFile, user, tagsAndData, "103", "original") ;
    }

} // End class
