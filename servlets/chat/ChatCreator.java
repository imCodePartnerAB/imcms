
import java.io.*;
import java.util.*;
import java.lang.*;
import javax.servlet.*;
import javax.servlet.http.*;

import imcode.external.diverse.*;
import imcode.external.chat.*;
import imcode.util.*;
import imcode.server.*;
import org.apache.log4j.Logger;

//obs här ska det byggas om rejält
//har dock haft problem med att jag får olika servletContext
//vilket innebär att jag tappar chatten och användar sessionen därför
//använder jag jsdk2.0 så långt det går, i väntan på att jag eller någon löst
//servletContext problemet

public class ChatCreator extends ChatBase {

    private final static String HTML_TEMPLATE = "admin_chat.html";
    private final static String HTML_TEMPLATES_BUTTON = "chat_template_admin.html";

    private final static String ADMIN_TEMPLATES_TEMPLATE = "chat_admin_template1.html";
    private final static String ADMIN_TEMPLATES_TEMPLATE_2 = "chat_admin_template2.html";
    private static final String MSG_TYPE_SAYS_TO = "100";
    private static final String MSG_TYPE_ASKS = "102";
    private static final int MAX_REFRESH_TIME = 180;

    private final static Logger log = Logger.getLogger("ChatCreator") ;

    /**
     The POST method creates the html page when this side has been
     redirected from somewhere else.
     **/

    public void doPost( HttpServletRequest req, HttpServletResponse res )
            throws ServletException, IOException {
        //log("start doPost");
        // Lets validate the session and get the session
        if ( super.checkSession( req, res ) == false ) {
            log( "checkSession == false" );
            return;
        }
        HttpSession session = req.getSession( false );

        // Lets get an user object
        imcode.server.User user = super.getUserObj( req, res );
        if ( user == null ) return;
        if ( !isUserAuthorized( req, res, user ) ) {
            log( "isUserAuthorized==false" );
            return;
        }

        String action = req.getParameter( "action" );

        if ( action == null ) {
            action = "";
            String header = "ChatCreator servlet. ";
            ChatError err = new ChatError( req, res, header, 3 );
            log( header + err.getErrorMsg() );
            return;
        }

        //peter jobbar här
        Chat myChat = (Chat)session.getAttribute( "myChat" );
        if ( myChat == null ) {
            myChat = createChat( req, user, Integer.parseInt( getMetaId( req ) ) );
        }
        int metaId = myChat.getChatId();

        if ( action.equalsIgnoreCase( "admin_chat" ) ) {
            log( "admin_chat" );
            //ok nu hämtar vi in all data från formuläret och updaterar chat-objektet
            if ( req.getParameter( "addMsgType" ) != null && ( !req.getParameter( "msgType" ).trim().equals( "" ) ) ) {
                myChat.addMsgType( req.getParameter( "msgType" ) );
            }
            if ( req.getParameter( "removeMsgType" ) != null && req.getParameter( "msgTypes" ) != null ) {
                int type = Integer.parseInt( req.getParameter( "msgTypes" ) );
                if ( type < 100 || type >= 104 ) {
                    myChat.removeMsgType( type );
                }
            }
            if ( req.getParameterValues( "authorized" ) != null ) {
                myChat.setSelectedAuto( req.getParameterValues( "authorized" ) );
            }
            if ( req.getParameter( "update" ) != null ) {
                myChat.setRefreshTime( Integer.parseInt( req.getParameter( "update" ) ) );
            }
            if ( req.getParameter( "reload" ) != null ) {
                myChat.setAutoRefreshEnabled( Integer.parseInt( req.getParameter( "reload" ) ) );
            }
            if ( req.getParameter( "inOut" ) != null ) {
                myChat.setShowEnterAndLeaveMessagesEnabled( Integer.parseInt( req.getParameter( "inOut" ) ) );
            }
            if ( req.getParameter( "private" ) != null ) {
                myChat.setShowPrivateMessagesEnabled( Integer.parseInt( req.getParameter( "private" ) ) );
            }
            if ( req.getParameter( "dateTime" ) != null ) {
                myChat.setShowDateTimesEnabled( Integer.parseInt( req.getParameter( "dateTime" ) ) );
            }
            if ( req.getParameter( "font" ) != null ) {
                myChat.setFontSize( Integer.parseInt( req.getParameter( "font" ) ) );
            }

            IMCServiceInterface imcref = IMCServiceRMI.getIMCServiceInterface( req );
            IMCPoolInterface chatref = IMCServiceRMI.getChatIMCPoolInterface( req );

            //lets save to db
            if ( req.getParameter( "okChat" ) != null ) {
                createChat( metaId, myChat, chatref, imcref, user, res );
                return;

            }//end if(action.equalsIgnoreCase("okChat"))

            //check if template adminpage is wanted
            if ( req.getParameter( "admin_templates_meta" ) != null ) {
                log( "admin_templates_meta" );
                if ( req.getParameter( "add_templates" ) != null ) {
                    String newLibName = req.getParameter( "template_lib_name" );
                    if ( newLibName == null ) {
                        String header = "ChatCreator servlet. ";
                        new ChatError( req, res, header, 80 ); //obs kolla om rätt nr
                        return;
                    }
                    // Lets check if we already have a templateset with that name
                    String libNameExists = chatref.sqlProcedureStr( "C_FindTemplateLib", new String[]{newLibName} );
                    if ( !libNameExists.equalsIgnoreCase( "-1" ) ) {
                        String header = "ChatCreator servlet. ";
                        new ChatError( req, res, header, 84 );//obs kolla om rätt nr
                        return;
                    }
                    chatref.sqlUpdateProcedure( "C_AddTemplateLib", new String[]{newLibName} );
                    // Lets copy the original folders to the new foldernames
                    FileManager fileObj = new FileManager();
                    File templateSrc = new File( imcref.getExternalTemplateFolder( metaId ), "original" );
                    File imageSrc = new File( RmiConf.getImagePathForExternalDocument( imcref, metaId ), "original" );
                    File templateTarget = new File( imcref.getExternalTemplateFolder( metaId ), newLibName );
                    File imageTarget = new File( RmiConf.getImagePathForExternalDocument( imcref, metaId ), newLibName );

                    fileObj.copyDirectory( templateSrc, templateTarget );
                    fileObj.copyDirectory( imageSrc, imageTarget );
                }//done add new template lib

                if ( req.getParameter( "change_templatelib" ) != null ) {//ok lets handle the change set case
                    log( "change_templatelib" );
                    // Lets get the new library name and validate it
                    String newLibName = req.getParameter( "new_templateset_name" );
                    //log("newLibName: "+newLibName);
                    if ( newLibName == null ) {
                        String header = "ChatCreator servlet. ";
                        new ChatError( req, res, header, 80 );//obs kolla om rätt nr
                        return;
                    }
                    // Lets find the selected template in the database and get its id
                    // if not found, -1 will be returned
                    String templateId = chatref.sqlProcedureStr( "C_GetTemplateIdFromName", new String[]{newLibName} );
                    if ( templateId.equalsIgnoreCase( "-1" ) ) {
                        String header = "ChatCreator servlet. ";
                        new ChatError( req, res, header, 81 );
                        return;
                    }
                    // Ok, lets update the chat with this new templateset.
                    //but first lets delete the old one.
                    chatref.sqlUpdateProcedure( "C_deleteChatTemplateset", new String[]{"" + metaId} );

                    chatref.sqlUpdateProcedure( "C_SetNewTemplateLib", new String[]{"" + metaId, newLibName} );
                }
                if ( req.getParameter( "UPLOAD_CHAT" ) != null ) {
                    log( "UPLOAD_CHAT" );
                    //ok lets handle the upload of templates and images
                    String folderName = req.getParameter( "TEMPLATE_NAME" );
                    String uploadType = req.getParameter( "UPLOAD_TYPE" );
                    //log(folderName +" "+uploadType+" "+metaId);
                    if ( folderName == null || uploadType == null ) {
                        return;
                    }
                    Vector tags = new Vector();
                    tags.add( "#META_ID#" );
                    tags.add( metaId + "" );
                    tags.add( "#UPLOAD_TYPE#" );
                    tags.add( uploadType );
                    tags.add( "#FOLDER_NAME#" );
                    tags.add( folderName );
                    //sendHtml(req,res,vm, ADMIN_TEMPLATES_TEMPLATE_2) ;
                    sendHtml( req, res, tags, ADMIN_TEMPLATES_TEMPLATE_2, null );
                    return;
                }
            }
            //check if template adminpage is wanted
            if ( req.getParameter( "adminTemplates" ) != null ) {
                log( "adminTemplates" );
                //ok now lets get the template set name
                String templateSetName = chatref.sqlProcedureStr( "C_GetTemplateLib", new String[]{"" + metaId} );
                if ( templateSetName == null ) {
                    templateSetName = "";
                }
                //ok lets get all the template set there is
                String[] templateLibs = chatref.sqlProcedure( "C_GetAllTemplateLibs" );
                Vector vect = new Vector();
                if ( templateLibs != null ) {
                    vect = super.convert2Vector( templateLibs );
                }
                Vector tags = new Vector();
                tags.add( "#TEMPLATE_LIST#" );
                tags.add( createOptionCode( templateSetName, vect ) );
                tags.add( "#CURRENT_TEMPLATE_SET#" );
                tags.add( templateSetName );
                sendHtml( req, res, tags, ADMIN_TEMPLATES_TEMPLATE, null );
                return;

            }

        }//end if(action.equalsIgnoreCase("ADD_CHAT"))
        log( "default köret" );
        sendHtml( req, res, createTaggs( req, myChat ), HTML_TEMPLATE, myChat );
        return;

        //slut peter jobbar

    } // End POST

    private void createChat( int metaId, Chat myChat, IMCPoolInterface chatref, IMCServiceInterface imcref, User user, HttpServletResponse res ) throws IOException {
        log( "okChat" );

         chatref.sqlUpdateProcedure( "C_AddNewChat", new String[]{"" + metaId, myChat.getChatName(), "3"} );

        chatref.sqlUpdateProcedure( "C_Delete_MsgTypes", new String[]{"" + metaId} );

        //lets connect the standard msgTypes with the chat
        String[] tempTypes = chatref.sqlProcedure( "C_GetBaseMsgTypes" );
        for ( int i = 0; i < tempTypes.length; i++ ) {
            String tempTypeId = chatref.sqlProcedureStr( "C_GetMsgTypeId", new String[]{tempTypes[i]} );
            /*TODO
            This is only a temporary blocking of standard message types
            we only select message type " säger till" and "frågar"
            */
            if ( MSG_TYPE_SAYS_TO.equals( tempTypeId ) || MSG_TYPE_ASKS.equals( tempTypeId ) ) {
                chatref.sqlUpdateProcedure( "C_AddNewChatMsg", new String[]{tempTypeId, "" + metaId} );
            }
        }

        // Lets add the new msgTypes to the db /ugly but it works
        Vector msgV = myChat.getMsgTypes();
        for ( int i = 0; i < msgV.size(); i += 2 ) {
            chatref.sqlUpdateProcedure( "C_AddMessageType", new String[]{"" + metaId, "" + msgV.get( i ), "" + msgV.get( i + 1 )} );
        }

        //	Vector valuesV = new Vector();

        chatref.sqlUpdateProcedure( "C_AddChatParams", new String[]{
            "" + metaId,
            "" + myChat.getRefreshTime(),
            "" + myChat.isAutoRefreshEnabled(),
            "" + myChat.isShowEnterAndLeaveMessagesEnabled(),
            "" + myChat.isShowPrivateMessagesEnabled(),
            "" + myChat.isShowPublicMessagesEnabled(),
            "" + myChat.isShowDateTimesEnabled(),
            "" + myChat.getfont()} );


        // lets delete all authorization for one meta
        chatref.sqlUpdateProcedure( "C_DeleteAuthorizations", new String[]{ "" + metaId} );

        //ok lets add the authorization types
        Vector autoV = myChat.getSelectedAuto();
        for ( int i = 0; i < autoV.size(); i++ ) {
            chatref.sqlUpdateProcedure( "C_ChatAutoTypes", new String[]{"" + autoV.elementAt( i ), "" + metaId} );
        }
        //ok now we have saved the stuff to the db so lets set up the chat and put it in the context
        String[][] messages = chatref.sqlProcedureMulti( "C_GetMsgTypes", new String[]{"" + metaId} );
        if ( messages != null ) {
            myChat.setMsgTypes( convert2Vector( messages ) );
        }
        ServletContext myContext = getServletContext();
        myContext.setAttribute( "theChat" + metaId, myChat );

        // Ok, we're done creating the chat. Lets tell imCMS system to show this child.
        imcref.activateChild( metaId, user );

        // Ok, we're done adding the chat, Lets log in to it!

        res.sendRedirect( "ChatLogin?login_type=login&meta_id="+metaId );

        return;
    }


    /**
     The GET method creates the html page when this side has been
     redirected from somewhere else.
     **/
    //laddar admin sida osv
    public void doGet( HttpServletRequest req, HttpServletResponse res ) throws ServletException, IOException {
        log( "startar do get" );
        // Lets validate the session, e.g has the user logged in to Janus?
        if ( super.checkSession( req, res ) == false ) return;
        HttpSession session = req.getSession( false );

        // Lets get an user object
        imcode.server.User user = super.getUserObj( req, res );
        if ( user == null ) return;

        if ( !isUserAuthorized( req, res, user ) ) {
            return;
        }

        String action = req.getParameter( "action" );
        if ( action == null ) {
            action = (String)req.getAttribute( "action" );
            if ( action == null ) {
                action = "";
                String header = "ChatCreator servlet. ";
                ChatError err = new ChatError( req, res, header, 3 );
                log( header + err.getErrorMsg() );
                return;
            }
        }

        // ********* Create NEW Chat *********************************************************
        if ( action.equalsIgnoreCase( "NEW" ) ) {
            log( "NEW" );
            //vi måste hämta allt som behövs från databasen och sedan fixa till mallen

            //skapa en temp chat
            int meta_id = Integer.parseInt( (String)session.getAttribute( "Chat.meta_id" ) );
            Chat myChat = createChat( req, user, meta_id );
            session.setAttribute( "myChat", myChat );
            // Lets build the Responsepage to the loginpage
            Vector vect = createTaggs( req, myChat );
            sendHtml( req, res, vect, HTML_TEMPLATE, myChat );
            return;
        }

        String templateAdmin = req.getParameter( "ADMIN_TEMPLATES" );
        if ( templateAdmin != null ) {//ok we have done upload template or image lets get back to the adminpage
            this.doPost( req, res );
            return;
        }

        if ( action.equalsIgnoreCase( "admin_chat" ) ) {
            log( "action =  admin_chat" );

            //check which chat we have
            String chatName = req.getParameter( "chatName" );
            log( "ChatName: " + chatName );
            Vector tags = new Vector();
            tags.add( "#chatName#" );
            tags.add( chatName );

            String metaId = (String)session.getAttribute( "Chat.meta_id" );
            log( "MetaId: " + metaId );

            ServletContext myContext = getServletContext();
            Chat myChat = (Chat)myContext.getAttribute( "theChat" + metaId );

            log( "Chat: " + myChat );

            Vector vect = createTaggs( req, myChat );
            sendHtml( req, res, vect, HTML_TEMPLATE, myChat );
            return;

        }

    } // End doGet

    public String getTemplateButtonHtml( HttpServletRequest req, String metaId ) throws ServletException, IOException {
        IMCServiceInterface imcref = IMCServiceRMI.getIMCServiceInterface( req );
        IMCPoolInterface chatref = IMCServiceRMI.getChatIMCPoolInterface( req );
        return imcref.parseExternalDoc( null, HTML_TEMPLATES_BUTTON, imcref.getLanguage(), "103", getTemplateLibName( chatref, metaId ) );
    }

    //peter keep
    public Vector createTaggs( HttpServletRequest req, Chat chat ) throws ServletException, IOException {

        Vector bv = new Vector();
        bv.add( "1" );
        bv.add( "2" );
        bv.add( "3" );
        Vector taggs = new Vector();
        taggs.add( "#msgTypes#" );
        taggs.add( createOptionCode( "säger till", chat.getMsgTypes() ) );
        taggs.add( "#authorized#" );
        taggs.add( createOptionCode( chat.getSelectedAuto(), chat.getAuthorizations() ) );
        taggs.add( "#msgType#" );
        taggs.add( "" );
        taggs.add( "#updateTime#" );
        taggs.add( createOptionCode( chat.getRefreshTime() + "", createUpdateTimeV() ) );
        taggs.add( "#reload#" );
        taggs.add( createRadioButton( "reload", bv, chat.isAutoRefreshEnabled() + "" ) );
        taggs.add( "#inOut#" );
        taggs.add( createRadioButton( "inOut", bv, chat.isShowEnterAndLeaveMessagesEnabled() + "" ) );
        taggs.add( "#private#" );
        taggs.add( createRadioButton( "private", bv, chat.isShowPrivateMessagesEnabled() + "" ) );
        taggs.add( "#dateTime#" );
        taggs.add( createRadioButton( "dateTime", bv, chat.isShowDateTimesEnabled() + "" ) );
        taggs.add( "#font#" );
        taggs.add( createRadioButton( "font", bv, chat.getfont() + "" ) );

        taggs.add( "#templates#" );
        taggs.add( getTemplateButtonHtml( req, chat.getChatId() + "" ) );

        return taggs;
    }

    public static Vector createUpdateTimeV() {
        Vector vect = new Vector();
        for ( int i = 10; i < MAX_REFRESH_TIME + 10 ; i += 10 ) {
            vect.add( i + "" );
            vect.add( i + "" );
        }
        return vect;
    }

    //peter keep
    public void init( ServletConfig config ) throws ServletException {
        super.init( config );
        log( "init" );
    } // End of INIT

    //peter keep
    public void log( String str ) {
        log.debug( str );
    }

} // End class

