

import javax.servlet.http.*;
import java.util.Hashtable;
import imcode.external.chat.*;

import java.util.*;




public class ChatBindingListener implements HttpSessionBindingListener
{ 
	private static Hashtable _theSessions;
	static
	{
		_theSessions = new Hashtable();
	}
	
	public ChatBindingListener(){}
	
	
	//****  methods  ***
	public void valueBound(HttpSessionBindingEvent event)
	{
		HttpSession session = event.getSession();
		
		ChatMember member = (ChatMember) session.getValue("theChatMember");
		int idNr = member.getUserId();
		
		_theSessions.put(String.valueOf(idNr), session);	
	} 
	
	public void valueUnbound(HttpSessionBindingEvent event)
	{
		HttpSession session = event.getSession();
		
		if (session.getValue("theChat") != null)
		{
			System.out.println("theChat wasn't null so lets rock");
			Chat chat = (Chat)session.getValue("theChat");
			ChatGroup group = (ChatGroup) session.getValue("theRoom");
			ChatMember member = (ChatMember)session.getValue("theChatMember");

			String theDateTime = ChatBase.getDateToday() +" : "+ ChatBase.getTimeNow();
			int senderNr = member.getUserId();
			String senderName = member.getName();
			ChatMsg newLeaveMsg = new ChatMsg(	ChatBase.LEAVE_MSG,"",
											ChatBase.CHAT_ENTER_LEAVE_INT,
											ChatBase.CHAT_ENTER_LEAVE_INT,"",
											senderName, -1, theDateTime);
			//lets send the message									   
			member.addNewMsg(newLeaveMsg);
		
			group.removeGroupMember(member);
			chat.removeChatMember(senderNr);
			
			_theSessions.remove(String.valueOf(senderNr));
		}else
		{
			System.out.println("theChat null so it doesn't rock at all");
		}
	}
	
	public static java.util.Enumeration getKeys()
	{	
		System.out.println("get keys in listenern");
		return _theSessions.keys();
		
	}
	
	public static HttpSession getASession(int memberNr)
	{
		return (HttpSession)_theSessions.get(String.valueOf(memberNr));
		
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