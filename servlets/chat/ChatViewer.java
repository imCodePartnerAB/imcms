
import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

import imcode.external.diverse.*;
import imcode.external.chat.*;
import org.apache.log4j.Logger;
import imcode.external.chat.ChatSessionsSingleton;


//meningen är att denna ska ladda framesetet och kolla
//all nödvändig data innan den gör detta

public class ChatViewer extends ChatBase {

    private final static String HTML_TEMPLATE = "Chat_Frameset.htm";

    private final static Logger log = Logger.getLogger( "ChatViewer" );

    public void service( HttpServletRequest req, HttpServletResponse res ) throws ServletException, IOException {
        HttpSession session = req.getSession( true );
        ServletContext myContext = getServletContext();

        // Lets validate the session, e.g has the user logged in to Janus?
        if ( super.checkSession( req, res ) == false ) return;

        // Lets get the standard SESSION parameters and validate them
        Properties params = super.getSessionParameters( req );

        // Lets get an user object
        imcode.server.User user = super.getUserObj( req, res );
        if ( user == null ) return;

        if ( !isUserAuthorized( req, res, user ) ) {
            return;
        }
        String metaId = params.getProperty( "META_ID" );

        // Lets get all parameters in a string which we'll send to every servlet in the frameset
        String paramStr = MetaInfo.passMeta( params );

        //lets clean up some in the session just incase
        session.removeAttribute( "checkBoxTextarr" ); //ska tas bort
        session.removeAttribute( "chatParams" );
        session.removeAttribute( "chatChecked" );

        //lets crete the ChatMember object and add it to the session if there isnt anyone
        ChatMember myMember = (ChatMember)session.getAttribute( "theChatMember" );
        if ( myMember == null ) {
            log( "there wasn't any member so return" );
        }

        //ok lets see if we have room
        ChatGroup myGroup = myMember.getGroup();
        if ( myGroup == null ) {
            log( "there wasn't any group so return" );
        }

        ChatMember member = (ChatMember)session.getAttribute( "theChatMember" );
        ChatSessionsSingleton.putSession( member, session );

        // Lets build the Responsepage
        Vector tags = new Vector();
        tags.add( "#CHAT_MESSAGES#" );
        tags.add( "ChatBoard?" + paramStr );
        tags.add( "#CHAT_CONTROL#" );
        tags.add( "ChatControl?" + paramStr );
        this.sendHtml( req, res, tags, HTML_TEMPLATE, null );
        return;
    }//end doGet


    /**
     Log function, will work for both servletexec and Apache
     **/
    public void log( String str ) {
        log.debug( str );
    }

} // End of class
