
import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

import imcode.external.diverse.*;
import imcode.util.*;
import imcode.server.*;
import imcode.util.IMCServiceRMI;


import imcode.external.chat.*;
import org.apache.log4j.Logger;

public class ChatControl extends ChatBase {

    private final static String HTML_TEMPLATE = "theChat.htm";
    private final static String SETTINGS_TEMPLATE = "chat_settings.html";
    private final static String ADMIN_GET_RID_OF_A_SESSION = "Chat_Admin_End_A_Session.htm";
    private final static String ADMIN_BUTTON = "Chat_Admin_Button.htm";
    private final static String SETTINGS_BUTTON = "chat_settings_button.html";
    private final static String CHAT_LOGOUT_TEMPLATE = "chat_logout_message.html";
    private final static String CHAT_AUTOLOGOUT_TEMPLATE = "chat_autologout_message.html";

    Logger log = Logger.getLogger( "ChatControl" );

    //OBS OBS OBS har inte fixat kontroll om det är administratör eller användare
    //för det är ju lite mera knappar och metoder som ska med om det är en admin
    //tex en knapp för att kicka ut användare
    //ev ska oxå tidtaggen på medelanden fixas till här
    //även loggningen ska fixas här om sådan efterfrågas
    //vidare måste silning av åäö och taggar fixas

    /**
     doGet
     */
    public void doGet( HttpServletRequest req, HttpServletResponse res ) throws ServletException, IOException {

        res.setContentType("text/html") ;
        // Lets validate the session, e.g has the user logged in to imCMS?
        if ( super.checkSession( req, res ) == false ) {
            log( "RETURN super.checksession" );
            return;
        }

        HttpSession session = req.getSession( false );

        // Lets get the standard SESSION parameters and validate them
        Properties params = this.getSessionParameters( req );

        // Lets get the user object
        imcode.server.User user = super.getUserObj( req, res );
        if ( user == null ) {
            log( "RETURN usern is null" );
            return;
        }

        if ( !isUserAuthorized( req, res, user ) ) {
            log( "RETURN user is not authorized" );
            return;
        }


        // Lets get parameters
        String metaId = params.getProperty( "META_ID" );
        //log("aMetaId = "+aMetaId);
        int meta_Id = Integer.parseInt( metaId );

        //lets get the chatmember
        ChatMember myMember = (ChatMember)session.getAttribute( "theChatMember" );
        if ( myMember == null ) {
            log( "myMember was null so return" );
            return;
        }

        //lets get the Chat
        Chat myChat = myMember.getParent();
        if ( myChat == null ) {
            log( "myChat was null so return" );
            return;
        }

        //String chatName = myChat.getChatName();
        String chatName = params.getProperty( "chatName" );
        if ( chatName == null ) chatName = "";


        //lets get the room
        ChatGroup myGroup = myMember.getGroup();

        //ok lets se if the user wants the change setting page
        if ( req.getParameter( "settings" ) != null ) {
            //ok we have to fix this method
            this.createSettingsPage( req, res, session, metaId, user, myMember );
            return;
        }//end

        //strings needed to set up the page
        String alias = myMember.getName();
        String selected = ( req.getParameter( "msgTypes" ) == null ? "" : req.getParameter( "msgTypes" ).trim() );

        String msgTypes = createOptionCode( selected, myChat.getMsgTypes() );

        //let's get all the users in this room, for the selectList
        StringBuffer group_members = new StringBuffer( "" );
        List _groupMembers = new LinkedList(myGroup.getGroupMembers());
        Collections.sort(_groupMembers);
        Iterator iter = _groupMembers.iterator();
        String selectMemb = ( req.getParameter( "recipient" ) == null ? "0" : req.getParameter( "recipient" ).trim() );
        int selNr = Integer.parseInt( selectMemb );
        while ( iter.hasNext() ) {
            ChatMember tempMember = (ChatMember)iter.next();
            String sel = "";
            if ( tempMember.getMemberId() == selNr ) sel = " selected";
            group_members.append( "<option value=\"" + tempMember.getMemberId() + "\"" + sel + ">" + tempMember.getName() + "</option>\n" );
        }

        //let's see if user has adminrights
        String adminButtonKickOut = "";
        String chatAdminLink = "";

        IMCServiceInterface imcref = IMCServiceRMI.getIMCServiceInterface( req );

        if ( userHasAdminRights( imcref, meta_Id, user ) ) {
            chatAdminLink = createAdminButton( req, ADMIN_BUTTON, metaId, chatName );
            //lets set up the kick out button OBS fixa detta
            adminButtonKickOut = createAdminButton( req, ADMIN_GET_RID_OF_A_SESSION, metaId, "" );
        }


        //lets set up the page to send
        Vector tags = new Vector();
        //lets add all the needed tags
        tags.add( "#chatName#" );
        tags.add( chatName );
        tags.add( "#alias#" );
        tags.add( alias );
        tags.add( "#MSG_PREFIX#" );
        tags.add( msgTypes );
        tags.add( "#MSG_RECIVER#" );
        tags.add( group_members.toString() );
        tags.add( "#CHAT_ADMIN_LINK#" );
        tags.add( chatAdminLink );
        tags.add( "#CHAT_ADMIN_DISCUSSION#" );
        tags.add( adminButtonKickOut );
        tags.add( "#SETTINGS#" );
        tags.add( settingsButton( req, myChat ) );

        this.sendHtml( req, res, tags, HTML_TEMPLATE, null );
        return;
    } //**** end doGet ***** end doGet ***** end doGet ******


    private String settingsButton( HttpServletRequest req, imcode.external.chat.Chat chat ) throws ServletException, IOException {
        if ( chat.settingsPage() ) {
            IMCServiceInterface imcref = IMCServiceRMI.getIMCServiceInterface( req );
            IMCPoolInterface chatref = IMCServiceRMI.getChatIMCPoolInterface( req );
            int metaId = chat.getChatId();
            return imcref.parseExternalDoc( null, SETTINGS_BUTTON, imcref.getLanguage(), "103", getTemplateLibName( chatref, metaId + "" ) );
        } else {
            return "&nbsp;";
        }
    }

    public void doPost( HttpServletRequest req, HttpServletResponse res )
            throws ServletException, IOException {

        res.setContentType("text/html") ;

        // Lets validate the session, e.g has the user logged in to imCMS
        if ( super.checkSession( req, res ) == false ) {
            log( "super.check session return" );
            return;
        }

        HttpSession session = req.getSession( false );

        // Lets get the standard SESSION parameters and validate them
        Properties params = this.getSessionParameters( req );

        // Lets get the user object
        imcode.server.User user = super.getUserObj( req, res );
        if ( user == null ) {
            log( "user is null return" );
            return;
        }
        if ( !isUserAuthorized( req, res, user ) ) {
            log( "user is not autorized return" );
            return;
        }
        IMCServiceInterface imcref = IMCServiceRMI.getIMCServiceInterface( req );
        IMCPoolInterface chatref = IMCServiceRMI.getChatIMCPoolInterface( req );

        // Lets get parameters
        String metaId = params.getProperty( "META_ID" );
        int meta_Id = Integer.parseInt( metaId );

        //lets get the Chat ChatGroup and ChatMember
        ChatMember myMember = (ChatMember)session.getAttribute( "theChatMember" );

        log( myMember.toString() );
        Chat myChat = myMember.getParent();

        ChatGroup myGroup = myMember.getGroup();

        if ( req.getParameter( "sendMsg" ) != null ) {//**** ok the user wants to send a message ****
            if ( myMember.isTimedOut()){
                String result = ChatControl.getParsedChatLeavePage(myMember,chatref, imcref, CHAT_AUTOLOGOUT_TEMPLATE);
                res.getWriter().write(result);
                super.logOutMember(myMember, null, imcref, chatref);
                return;
            }
            sendMessage( myMember, req, myChat, myGroup, res, metaId );
            return;
        } else if ( req.getParameter( "controlOK" ) != null || req.getParameter( "fontInc" ) != null || req.getParameter( "fontDec" ) != null ) {
            //lets collect the new settings
            super.prepareChatBoardSettings( myMember, req, true );
            // lets reload the chatboard with the new settings
            RequestDispatcher requestDispatcher = req.getRequestDispatcher( "ChatViewer" );
            requestDispatcher.forward( req, res );
            return;
        } else if ( req.getParameter( "logOut" ) != null ) {
            logOut( session, res, imcref, chatref );
        } else if ( req.getParameter( "kickOut" ) != null && userHasAdminRights( imcref, meta_Id, user ) ) {
            kickOut( req, myChat, myGroup, myMember, chatref, imcref, user, metaId, res );
            return;
        } else {
            log.error( "Fallthrough in ChatControl" );
            throw new RuntimeException( "Fallthrough in ChatControl" );
        }
    } // DoPost

    private void logOut( HttpSession session, HttpServletResponse res,  IMCServiceInterface imcref, IMCPoolInterface chatref) throws ServletException, IOException {

        ChatMember myMember = (ChatMember)session.getAttribute( "theChatMember" );
        ChatSystemMessage systemMessage = new ChatSystemMessage(myMember, ChatSystemMessage.LEAVE_MSG) ;
        logOutMember( myMember, systemMessage, imcref, chatref );
        String result = getParsedChatLeavePage(myMember, chatref, imcref, CHAT_LOGOUT_TEMPLATE);
        res.getWriter().write(result);
        return;
    }

    static String getParsedChatLeavePage(ChatMember myMember, IMCPoolInterface chatref, IMCServiceInterface imcref, String leaveTemplate) throws ServletException, IOException {
        int chatMetaId = myMember.getParent().getChatId();
        String templateSetName = getTemplateLibName( chatref, chatMetaId+"");
        List tags = new ArrayList();
        tags.add("#chat_return_meta_id#");
        tags.add(myMember.getReferrerMetaId()+"");
        tags.add("#chat_meta_id#");
        tags.add(chatMetaId+"");
        String result = imcref.parseExternalDoc(tags, leaveTemplate, myMember.getUser().getLangPrefix(),"103",templateSetName);
        return result;
    }


    private void kickOut( HttpServletRequest req, Chat myChat, ChatGroup myGroup, ChatMember myMember, IMCPoolInterface chatref, IMCServiceInterface imcref, User user, String metaId, HttpServletResponse res ) throws ServletException, IOException {

        //lets get the membernumber
        String memberNrStr = ( req.getParameter( "recipient" ) == null ? "" : req.getParameter( "recipient" ).trim() );


        int idNr = Integer.parseInt( memberNrStr );
        kickOutMemberFromGroup( myChat, idNr, myGroup, chatref, imcref, user, myMember, metaId );

        RequestDispatcher requestDispatcher = req.getRequestDispatcher( "ChatViewer" );
        requestDispatcher.forward( req, res );
        return;
    }

    private void kickOutMemberFromGroup( Chat myChat, int idNr, ChatGroup myGroup, IMCPoolInterface chatref, IMCServiceInterface imcref, User user, ChatMember myMember, String metaId ) throws ServletException, IOException {
        ChatMember personToKickOut = myChat.getChatMember( idNr );
        if ( personToKickOut != null ) {
            personToKickOut.setKickedOut(true);
            //HttpSession session = ChatSessionsSingleton.getSession(personToKickOut);
            //cleanUpSessionParams(session);
            createKickOutMessageAndAddToGroup( personToKickOut, chatref, myChat, imcref, user, myMember, myGroup, metaId );
            myGroup.removeGroupMember( personToKickOut );
        }
    }

    private void sendMessage( ChatMember myMember, HttpServletRequest req, Chat myChat, ChatGroup myGroup, HttpServletResponse res, String metaId ) throws ServletException, IOException {
        IMCServiceInterface imcref = IMCServiceRMI.getIMCServiceInterface( req );
        IMCPoolInterface chatref = IMCServiceRMI.getChatIMCPoolInterface( req );

        //lets get the message and all the needed params add it into the msgpool
        String newMessage = ( req.getParameter( "msg" ) == null ? "" : req.getParameter( "msg" ).trim() );
        if ( newMessage.length() != 0 ) {
            //lets get rid all html tags
            newMessage = HTMLConv.toHTMLSpecial( newMessage );

            //lets get the recipient 0 = alla
            String recieverNrStr = ( req.getParameter( "recipient" ) == null ? "0" : req.getParameter( "recipient" ).trim() );

            //lets get the messageType fore the message 0 = inget
            String msgTypeNrStr = ( req.getParameter( "msgTypes" ) == null ? "0" : req.getParameter( "msgTypes" ).trim() );

            //ok lets parse those to int
            int recieverNr, msgTypeNr;
            try {
                recieverNr = Integer.parseInt( recieverNrStr );
                msgTypeNr = Integer.parseInt( msgTypeNrStr );

            } catch ( NumberFormatException nfe ) {
                log( "ChatControl, NumberFormatException while try to send msg" );
                recieverNr = 0;
                msgTypeNr = 0;
            }

            String msgTypeStr = ""; //the msgType in text
            if ( msgTypeNr != 0 ) {
                Vector vect = myChat.getMsgTypes();
                for ( int i = 0; i < vect.size(); i += 2 ) {
                    String st = (String)vect.get( i );
                    if ( st.equals( Integer.toString( msgTypeNr ) ) ) {
                        msgTypeStr = (String)vect.get( i + 1 );
                        break;
                    }
                }
            }
            String recieverStr = "Alla"; //the receiver in text FIX ugly
            if ( recieverNr != 0 ) {
                boolean found = false;
                Iterator iter = myGroup.getAllGroupMembers();
                while ( iter.hasNext() && !found ) {
                    ChatMember memb = (ChatMember)iter.next();
                    if ( recieverNr == memb.getMemberId() ) {
                        recieverStr = memb.getName();
                        found = true;
                    }
                }
            }

            //lets see if it was a private msg to all then wee dont send it
            if ( msgTypeNr == MSG_TYPE_PRIVATE && recieverNr == MSG_RECIPIENT_ALL ) {
                doGet( req, res );
                return;
            } else {
                ChatNormalMessage newChatMsg = new ChatNormalMessage( newMessage, myMember, recieverNr, recieverStr, msgTypeNr, msgTypeStr );
                myMember.getGroup().addNewMsg( this, newChatMsg, imcref, chatref );
                try {
                    chatlog( metaId, newChatMsg.getLogMsg() );
                } catch ( InterruptedException ie ) {
                    log( "ChatControl, InterruptedException when loging message" );
                }
            }
        }

        //ok now lets build the page in doGet
        RequestDispatcher requestDispatcher = req.getRequestDispatcher( "ChatViewer" );
        requestDispatcher.forward( req, res );
        return;
    }


    //this method will create an usersettings page
    //
    public synchronized void createSettingsPage( HttpServletRequest req, HttpServletResponse res, HttpSession session,
                                                 String metaId, imcode.server.User user, ChatMember member )
            throws ServletException, IOException {
        Vector vect = new Vector();
        File templetUrl = super.getExternalTemplateFolder( req );
        IMCServiceInterface imcref = IMCServiceRMI.getIMCServiceInterface( req );
        IMCPoolInterface chatref = IMCServiceRMI.getChatIMCPoolInterface( req );
        String[] arr;
        if ( true )//(checkboxText == null)
        {
            //we dont have them so we have to get them from db
            arr = chatref.sqlProcedure( "C_GetChatParameters", new String[]{"" + metaId} );
            if ( arr.length != 7 ) {
                return;
            }

            StringBuffer reload = new StringBuffer("");
            if ( arr[1].equals( "3" ) ) {
                boolean autoRefreshEnabled = member.isAutoRefreshEnabled();
                Vector tempV = new Vector();
                tempV.add( "#checked#" );
                if ( autoRefreshEnabled ) {
                    tempV.add( "checked" );
                } else {
                    tempV.add( "" );
                }
                reload.append(imcref.parseExternalDoc( tempV, "checkbox_reload.html", user.getLangPrefix(), "103", templetUrl.getName() ) );
                reload.append(createOptionCode( member.getRefreshTime() + "", ChatCreator.createUpdateTimeV() ) );
                reload.append("</select> sekund <br>");
            }

            String entrance = "";
            if ( arr[2].equals( "3" ) ) {
                boolean showEnterAndLeaveMessages = member.isShowEnterAndLeaveMessagesEnabled();
                Vector tempV = new Vector();
                tempV.add( "#checked#" );
                if ( showEnterAndLeaveMessages ) {
                    tempV.add( "checked" );
                } else {
                    tempV.add( "" );
                }
                entrance = imcref.parseExternalDoc( tempV, "checkbox_entrance.html", user.getLangPrefix(), "103", templetUrl.getName() );
            }
            String privat = "";
            if ( arr[3].equals( "3" ) ) {
                boolean showPrivateMessagesEnabled = member.isShowPrivateMessagesEnabled();
                Vector tempV = new Vector();
                tempV.add( "#checked#" );
                if ( showPrivateMessagesEnabled ) {
                    tempV.add( "checked" );
                } else {
                    tempV.add( "" );
                }
                privat = imcref.parseExternalDoc( tempV, "checkbox_private.html", user.getLangPrefix(), "103", templetUrl.getName() );
            }
            String datetime = "";
            if ( arr[5].equals( "3" ) ) {
                boolean showDateTimesEnabled = member.isShowDateTimesEnabled();
                Vector tempV = new Vector();
                tempV.add( "#checked#" );
                if ( showDateTimesEnabled ) {
                    tempV.add( "checked" );
                } else {
                    tempV.add( "" );
                }
                datetime = imcref.parseExternalDoc( tempV, "checkbox_datetime.html", user.getLangPrefix(), "103", templetUrl.getName() );
            }
            String font = "";
            if ( arr[6].equals( "3" ) ) {
                int fontSize = member.getFontSize();
                Vector tempV = new Vector();
                for ( int i = 1; i < 8; i++ ) {
                    tempV.add( "#" + i + "#" );
                    if ( i == fontSize ) {
                        tempV.add( "checked" );
                    } else {
                        tempV.add( "" );
                    }
                }
                font = imcref.parseExternalDoc( tempV, "buttons_font.html", user.getLangPrefix(), "103", templetUrl.getName() );
            }

            vect.add( "#reload#" );
            vect.add( reload.toString() );
            vect.add( "#entrance#" );
            vect.add( entrance );
            vect.add( "#private#" );
            vect.add( privat );
            vect.add( "#datetime#" );
            vect.add( datetime );
            vect.add( "#font#" );
            vect.add( font );
        }
        this.sendHtml( req, res, vect, SETTINGS_TEMPLATE, null );
        return;
    }//end createSettingsPage


    private synchronized String createAdminButton( HttpServletRequest req, String template, String chatId, String name )
            throws ServletException, IOException {
        VariableManager vm = new VariableManager();
        vm.addProperty( "chatId", chatId );
        vm.addProperty( "chatName", name );

        //lets create adminbuttonhtml
        File templateLib = super.getExternalTemplateFolder( req );
        HtmlGenerator htmlObj = new HtmlGenerator( templateLib, template );
        return htmlObj.createHtmlString( vm, req );
    }


    /**
     Detects paths and filenames.
     */

    public void init( ServletConfig config )
            throws ServletException {
        super.init( config );
    }

    /**
     Log function, will work for both servletexec and Apache
     only for internal use
     **/
    public void log( String str ) {
        log.debug( "ChatControl: " + str );
        //System.out.println("ChatControl: " + str ) ;
    }

} // End of class
