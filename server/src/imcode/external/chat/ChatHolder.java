
package imcode.external.chat;

import java.util.*;

public class ChatHolder{
	private final static String CVS_REV = "$Revision$" ;
	private final static String CVS_DATE = "$Date$" ;
	
	private static Hashtable _allChatts;

	static{
		_allChatts = new Hashtable();
	}

	public static boolean hasAChat(String name){
		return _allChatts.containsKey(name);
	}

	public static void addNewChat(String name, Chat chat){
		_allChatts.put(name, chat);
	}

	public static Chat getAChat(String name){
		return (Chat)_allChatts.get(name);
	}

	public static void removeAChat(String name){
		_allChatts.remove(name);
	}

	public static void removeAllChats(){
		_allChatts.clear();
	}

	public static Enumeration getAllTheChats(){
		return _allChatts.elements();
	}
}//end class