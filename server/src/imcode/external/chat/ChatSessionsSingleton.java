package imcode.external.chat;

import imcode.external.chat.ChatMember;

import javax.servlet.http.HttpSession;
import java.util.Hashtable;

/**
 * @author kreiger
 */
public class ChatSessionsSingleton {

    private static Hashtable sessions;

    static {
        sessions = new Hashtable();
    }

    public static void putSession( ChatMember member, HttpSession session ) {
        sessions.put(member,session) ;
    }

    public static HttpSession removeSession( ChatMember member ) {
        return (HttpSession)sessions.remove(member) ;
    }

    public static HttpSession getSession( ChatMember member ) {
        return (HttpSession)sessions.get(member) ;
    }
}
