package imcode.external.chat;

import imcode.server.IMCServiceInterface;
import imcode.server.User;

import java.util.Vector;

/**
 * @author kreiger
 */
public class ChatSystemMessage extends ChatMessage {

    public final static int ENTER_MSG = 1;
    public final static int LEAVE_MSG = 2;
    public final static int KICKOUT_MSG = 3;
    public final static int USER_TIMEDOUT_MSG = 4;

    private ChatMember member;
    private int message;

    public ChatSystemMessage( ChatMember member, int message ) {
        this.member = member;
        this.message = message;
    }

    public ChatMember getMember() {
        return member;
    }

    public int getMessage() {
        return message;
    }

    public String getLine( boolean showPrivateMessages, ChatMember lineViewer, StringBuffer sendMsgString, IMCServiceInterface imcref, User user, String libName ) {
        if (!lineViewer.isShowEnterAndLeaveMessagesEnabled() && (message == ENTER_MSG || message == LEAVE_MSG)) {
            return "" ;
        }

        String message = getMessageString(imcref,user,libName);

        Vector vLine = new Vector();
        vLine.add( "#size#" );
        vLine.add( ""+lineViewer.getFontSize() );
        vLine.add( "#member#" );
        vLine.add( this.getMember().getName() );
        vLine.add( "#message#" );
        vLine.add( message );
        vLine.add( "#date#" );
        if ( lineViewer.isShowDateTimesEnabled() ) {//show dateTime
            vLine.add( formattedDateTime() );
        } else {
            vLine.add( "" );
        }

        return imcref.parseExternalDoc( vLine, "system_msg.html", user.getLangPrefix(), "103", libName ).trim() + "<br>\n";
    }

    private String getMessageString( IMCServiceInterface imcref, User user, String libName ) {
        String templateName = null;
        switch ( message ) {
            case ENTER_MSG:
                templateName = "enter_msg.html";
                break;
            case LEAVE_MSG:
                templateName = "leave_msg.html";
                break;
            case KICKOUT_MSG:
                templateName = "kickout_msg.html";
                break;
            case USER_TIMEDOUT_MSG:
                templateName = "user_timedout_msg.html";

        }
        return imcref.parseExternalDoc( null, templateName, user.getLangPrefix(), "103", libName ).trim();
    }

    public String getLogMsg( IMCServiceInterface imcref, User user, String libName ) {
        return "\t<" + member.getName()+">\t("+member.getIpNr()+")\t[--- " + getMessageString(imcref,user,libName)+ "]";
    }

    public String toString() {
        String event ;
        switch (message) {
            case ENTER_MSG:
                event = "entered";
                break;
            case LEAVE_MSG:
                event = "left";
                break;
            case KICKOUT_MSG:
                event = "was kicked out";
                break;
            case USER_TIMEDOUT_MSG:
                event = "timed out";
                break ;
            default:
                event = "bad system message" ;
        }
        return member.getName()+" "+event ;
    }
    
}
