package imcode.external.chat;

import imcode.server.IMCServiceInterface;
import imcode.server.user.UserDomainObject;

import java.util.Vector;

public class ChatNormalMessage extends ChatMessage {

    private String _senderStr;
    private int _msgType;
    private String _msgTypeStr;
    private String _chatMsg;
    private String _recieverStr;
    private int recipient;
    private int _sender;

    //	private Date date;
    /**
     *Default constructor
     */
    public ChatNormalMessage( String chatMsg, ChatMember senderMember, int reciever, String recieverStr,
                              int msgType,
                              String msgTypeStr
                              ) {
        _chatMsg = chatMsg;
        recipient = reciever;
        _recieverStr = recieverStr;
        _msgType = msgType;
        _msgTypeStr = msgTypeStr;
        _senderStr = senderMember.getName();
        _sender = senderMember.getMemberId();
    }

    public String toString() {
        return getLogMsg();
    }

    public String getLogMsg() {
        String recipientMessage;
        if ( recipient == ChatConstants.MSG_RECIPIENT_ALL ) {
            recipientMessage = "";
        } else {
            recipientMessage = "[" + _msgTypeStr + " " + _recieverStr + "]";
        }

        return "\t<" + _senderStr + "> \t\t" + recipientMessage + "\t"+ _chatMsg.replaceAll("\n<BR>","\n\t\t\t\t");
    }

    private String getMessage() {
        return _chatMsg;
    }

    private int getMsgType() {
        return _msgType;
    }

    private int getRecipient() {
        return recipient;
    }

    private String getRecipientStr() {
        return _recieverStr;
    }

    private String getMsgTypeStr() {
        return _msgTypeStr;
    }

    private String getSenderStr() {
        return _senderStr;
    }

    private int getSender() {
        return _sender;
    }

    public String getLine( boolean showPrivateMessages, ChatMember myMember, IMCServiceInterface imcref,
                           UserDomainObject user, String libName ) {
        Vector vLine = new Vector();
        vLine.add( "#size#" );
        vLine.add( "" + myMember.getFontSize() );
        vLine.add( "#sender#" );
        vLine.add( this.getSenderStr() );
        vLine.add( "#msgType#" );
        vLine.add( this.getMsgTypeStr() );
        vLine.add( "#recipient#" );
        vLine.add( this.getRecipientStr() );
        vLine.add( "#message#" );
        vLine.add( this.getMessage() );
        vLine.add( "#date#" );
        if ( myMember.isShowDateTimesEnabled() ) {//show dateTime
            vLine.add( formattedDateTime() );
        } else {
            vLine.add( "" );
        }

        //must check if it is a public msg
        if ( this.getMsgType() == ChatConstants.MSG_TYPE_PRIVATE ) {
            if ( showPrivateMessages ) { //show private messages
                if ( this.getRecipient() == myMember.getMemberId() ) { //ok it's to mee
                    return parseChatTemplate( imcref, vLine, "private_to_msg.html", user, libName ) + "<br>\n";
                } else if ( this.getSender() == myMember.getMemberId() ) { //it's was I who sent it
                    return parseChatTemplate( imcref, vLine, "private_from_msg.html", user, libName ) + "<br>\n";
                }
            }
        }
        if ( ChatConstants.MSG_RECIPIENT_ALL == recipient ) {
            return parseChatTemplate( imcref, vLine, "standard_msg.html", user, libName ) + "<br>\n";
        } else {
            return parseChatTemplate( imcref, vLine, "directed_msg.html", user, libName ) + "<br>\n";
        }
    }

    private static String parseChatTemplate( IMCServiceInterface imcref, Vector vLine, String templateName, UserDomainObject user, String libName ) {
        return imcref.getAdminTemplateFromSubDirectoryOfDirectory( templateName, user, vLine, "103", libName ).trim();
    }
}//end class
