package imcode.external.chat;

import imcode.server.ImcmsServices;
import imcode.server.user.UserDomainObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author kreiger
 */
public abstract class ChatMessage {

    private int idNumber;
    private Date dateTime = new Date() ;
    private static final String DATE_FORMAT_ISO8601 = "yyyy-MM-dd HH:mm:ss";

    void setIdNumber( int number ) {
        idNumber = number;
    }

    public int getIdNumber() {
        return idNumber;
    }

    public Date getDateTime() {
        return dateTime;
    }

    String formattedDateTime() {
        DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_ISO8601) ;
        return dateFormat.format(dateTime) ;
    }

    public abstract String getLine( boolean showPrivateMessages, ChatMember myMember, ImcmsServices imcref,
                                    UserDomainObject user, String libName ) ;

}
