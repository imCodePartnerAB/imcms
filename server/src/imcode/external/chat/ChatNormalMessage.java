package imcode.external.chat;

import imcode.server.IMCServiceInterface;
import imcode.server.User;

import java.util.Vector;
import java.text.DateFormat;

public class ChatNormalMessage extends ChatMessage {

    private String _senderStr;
    private int _msgType;
    private String _msgTypeStr;
    private String _chatMsg;
    private String _recieverStr;
    private int recipient;
    private int _sender;
    private String _senderHost;

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
        _senderHost = senderMember.getIpNr();
    }

    public String toString() {
        return getLogMsg();
    }

    public String getLogLeaveMsg() {
        return getLogEnterMsg();
    }

    public String getLogEnterMsg() {
        return "<" + _senderStr + "/#" + _senderHost + "> " + "[" + _msgTypeStr + "]";
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

    public String getMessage() {
        return _chatMsg;
    }

    public int getMsgType() {
        return _msgType;
    }

    public int getRecipient() {
        return recipient;
    }

    public String getRecipientStr() {
        return _recieverStr;
    }

    public String getMsgTypeStr() {
        return _msgTypeStr;
    }

    public String getSenderStr() {
        return _senderStr;
    }

    public int getSender() {
        return _sender;
    }

    public String getLine( boolean showPrivateMessages, ChatMember myMember, StringBuffer sendMsgString, IMCServiceInterface imcref, User user, String libName ) {
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

    private static String parseChatTemplate( IMCServiceInterface imcref, Vector vLine, String templateName, User user, String libName ) {
        return imcref.parseExternalDoc( vLine, templateName, user.getLangPrefix(), "103", libName ).trim();
    }
}//end class
