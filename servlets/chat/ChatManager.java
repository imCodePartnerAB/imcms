
import imcode.external.chat.ChatBase;
import imcode.external.chat.ChatError;
import imcode.external.chat.ChatMember;
import imcode.external.chat.ChatSystemMessage;
import imcode.external.diverse.MetaInfo;
import imcode.server.ApplicationServer;
import imcode.server.IMCPoolInterface;
import imcode.server.IMCServiceInterface;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

//första gången vi kommer hit har vi doGet parametern  action=new

public class ChatManager extends ChatBase {

    public void doGet( HttpServletRequest req, HttpServletResponse res ) throws ServletException, IOException {

        //RequestDispatcher myDispatcher = req.getRequestDispatcher( "/servlet/StartDoc" );

        // Lets validate the session, e.g has the user logged in to imCms?
        if ( super.checkSession( req, res ) == false ) return;

        // Lets get the standard parameters and validate them
        MetaInfo.Parameters params = MetaInfo.getParameters( req );
        //if (super.checkParameters(req, res, params) == false) return ;

        // Lets get an user object
        imcode.server.user.UserDomainObject user = super.getUserObj( req, res );
        if ( user == null ) return;

        int testMetaId = params.getMetaId();

        if ( !isUserAuthorized( req, res, testMetaId, user ) ) {
            return;
        }

        String action = req.getParameter( "action" );

        if ( action == null ) {
            //OBS FIXA FELMEDELANDENA
            action = "";
            String header = "ChatManager servlet. ";
            ChatError err = new ChatError( req, res, header, 3 );
            log( header + err.getErrorMsg() );
            return;
        } else if ( action.equalsIgnoreCase( "NEW" ) ) {
            //log("Lets add a chat");
            HttpSession session = req.getSession( false );
            if ( session != null ) {
                // log("Ok nu sätter vi metavärdena");
                setSessionAttributes( session, params );
            }

            req.setAttribute( "action", "NEW" );
            RequestDispatcher myDispatcher;
            myDispatcher = req.getRequestDispatcher( "/servlet/ChatCreator" );
            myDispatcher.forward( req, res );
            return;
        } else if ( action.equalsIgnoreCase( "VIEW" ) ) {

            // Lets store  the standard metavalues in his session object
            HttpSession session = req.getSession( false );
            if ( session != null ) {
// lets check if user is active in an other chat, if so then log him out
                ChatMember theChatMember = (ChatMember)session.getAttribute( "theChatMember" );
                if ( theChatMember != null ) {
                    ChatSystemMessage systemMessage = new ChatSystemMessage( theChatMember, ChatSystemMessage.USER_TIMEDOUT_MSG );
                    IMCServiceInterface imcref = ApplicationServer.getIMCServiceInterface();
                    IMCPoolInterface chatref = ApplicationServer.getIMCPoolInterface();
                    logOutMember( theChatMember, systemMessage, imcref, chatref );
                }
                // log("Ok nu sätter vi metavärdena");
                setSessionAttributes( session, params );
            }

            req.setAttribute( "login_type", "login" );
            RequestDispatcher myDispatcher;
            myDispatcher = req.getRequestDispatcher( "/servlet/ChatLogin" );
            myDispatcher.forward( req, res );
            return;

        } else if ( action.equalsIgnoreCase( "CHANGE" ) ) {
            req.setAttribute( "metadata", "meta" );
            RequestDispatcher myDispatcher;
            myDispatcher = req.getRequestDispatcher( "ChangeExternalDoc2" );
            myDispatcher.forward( req, res );
            return;
        } // End if
    } // End doGet

    private void setSessionAttributes( HttpSession session, MetaInfo.Parameters params ) {
        session.setAttribute( "Chat.meta_id", "" + params.getMetaId() );
        session.setAttribute( "Chat.parent_meta_id", "" + params.getParentMetaId() );
    }

} // End of class
