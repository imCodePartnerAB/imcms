
import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

import imcode.util.*;
import imcode.util.IMCServiceRMI;
import imcode.external.chat.*;
import imcode.server.*;
import org.apache.log4j.Logger;


public class ChatBoard extends ChatBase {

    private final static String HTML_TEMPLATE = "chat_messages.html";
    private final static String HTML_HR = "last_msg_hr.html";

    private final static Logger log = Logger.getLogger( "ChatBoard" );

    public void doGet( HttpServletRequest req, HttpServletResponse res ) throws ServletException, IOException {
        if ( super.checkSession( req, res ) == false ) return;

        Utility.setNoCache(res);

        // Lets get the user object
        imcode.server.User user = super.getUserObj( req, res );
        if ( user == null ) return;

        if ( !isUserAuthorized( req, res, user ) ) {
            log( "user not authorized" );
            return;
        }

        // Lets get serverinformation
        IMCServiceInterface imcref = IMCServiceRMI.getIMCServiceInterface( req );

        ChatMember myMember = getChatMember( req );

        //this buffer is used to store all the msgs to send to the page
        StringBuffer sendMsgString = new StringBuffer( "" );

        Chat myChat = myMember.getParent();

        IMCPoolInterface chatref = IMCServiceRMI.getChatIMCPoolInterface( req );
        String libName = super.getTemplateLibName( chatref, myChat.getChatId() + "" );

        boolean showPrivateMessages = myMember.isShowPrivateMessagesEnabled();
        boolean autoReload = myMember.isAutoRefreshEnabled();
        String time = myChat.getRefreshTime() + "";
        String chatRefresh = getChatRefresh( autoReload, time, req );

        int lastMsgInt = myMember.getLastMsgNr();
        //let's get all the messages
        ListIterator msgIter = myMember.getMessages();

        //lets fix the html-string containing all messags
        while ( msgIter.hasNext() ) {
            ChatMessage message = (ChatMessage)msgIter.next();

            if ( lastMsgInt == message.getIdNumber() ) {
                sendMsgString.append( imcref.parseExternalDoc( null, HTML_HR, imcref.getLanguage(), "103", getTemplateLibName( chatref, myChat.getChatId() + "" ) ) );
                //sendMsgString.append( "<hr>\n" );

            }

            sendMsgString.append(message.getLine( showPrivateMessages, myMember, sendMsgString, imcref, user, libName ));
        }//end while loop
        Utility.setNoCache( res );

        Vector tags = new Vector();
        tags.add( "#CHAT_REFRESH#" );
        tags.add( chatRefresh );
        tags.add( "#CHAT_MESSAGES#" );
        tags.add( sendMsgString.toString() );

        this.sendHtml( req, res, tags, HTML_TEMPLATE, null );

        myMember.setLastRequest(new Date());

    }

    private static String getChatRefresh( boolean autoReload, String time, HttpServletRequest req ) {
        String chatRefresh = "";
        if ( autoReload ) {
            chatRefresh = "<META HTTP-EQUIV=\"Refresh\" CONTENT=\"" + time + ";URL=" + req.getRequestURI() + "\">";

        }
        return chatRefresh;
    }

    private ChatMember getChatMember( HttpServletRequest req ) {
        // Lets get parameters
        HttpSession session = req.getSession( false );

        ChatMember myMember = (ChatMember)session.getAttribute( "theChatMember" );
        return myMember;
    }

    /**
     Log function, will work for both servletexec and Apache
     **/
    public void log( String str ) {
        log.debug( str );
    }


} // End of class
