
import javax.servlet.http.*;
import java.util.Hashtable;
import imcode.external.chat.*;

import java.util.*;




public class ChatBindingListener implements HttpSessionBindingListener
{ 
	private final static String CVS_REV = "$Revision$" ;
	private final static String CVS_DATE = "$Date$" ;
	private static Hashtable _sessions;
	static
	{
		_sessions = new Hashtable();
	} 
	
	//****  methods  ***
	public void valueBound(HttpSessionBindingEvent event)
	{
		HttpSession session = event.getSession();
		
		ChatMember member = (ChatMember) session.getValue("theChatMember");
		int idNr = member.getUserId();
		_sessions.put(Integer.toString(idNr), session);
		//System.out.println("ok value bound");	
	} 
	
	public void valueUnbound(HttpSessionBindingEvent event)
	{
		HttpSession session = event.getSession();
		
		if (session.getValue("theChatMember") != null)
		{
			//System.out.println("ok we have the member so lets rock");
			ChatMember myMember = (ChatMember)session.getValue("theChatMember");
			Chat myChat = myMember.getMyParent();
			ChatGroup myGroup = myMember.getMyGroup();
			
			String theDateTime = ChatBase.getDateToday() +" : "+ ChatBase.getTimeNow();
			int senderNr = myMember.getUserId();
			_sessions.remove(Integer.toString(senderNr));
			
			String senderName = myMember.getName();
			ChatMsg newLeaveMsg = new ChatMsg(	ChatBase.LEAVE_MSG,"",
											ChatBase.CHAT_ENTER_LEAVE_INT,
											ChatBase.CHAT_ENTER_LEAVE_INT,"",
											senderName, -1, theDateTime);
			//lets send the message									   
			myGroup.addNewMsg(newLeaveMsg);
		
			myGroup.removeGroupMember(myMember);
			myChat.removeChatMember(senderNr);
			//System.out.println("OK WE HAVE GOT RID OF THE USER");
		}else
		{
			System.out.println("theChat null so it doesn't rock at all");
		}
	
	}
	
	public static void getKickoutSession(int memberNr)
	{
		HttpSession session = (HttpSession)_sessions.get(Integer.toString(memberNr));
		if (session != null)
		{
			session.invalidate();
		}else
		{
			System.out.println("something got wrong, when kick out");
		}
	}
	
	
}//end class

/*
	//servlet class 
	{
		ChatBindingListener bindLyss = new ChatBindingListener(...);
	    session.putValue("chatBinding",bindLyss);
	}
	// when session is invalidated valueUnbound function gets the event
*/
