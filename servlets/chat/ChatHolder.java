

import java.util.*;
import imcode.external.chat.*;

class ChatHolder
{
	private static Hashtable _allChatts;
	
	static
	{
		_allChatts = new Hashtable();
	}
	static boolean hasAChat(String name)
	{
		return _allChatts.containsKey(name);
	}
	
	static void addNewChat(String name, Chat chat)
	{
		_allChatts.put(name, chat);
	}
	
	static Chat getAChat(String name)
	{
		return (Chat)_allChatts.get(name);
	}
	
	static void removeAChat(String name)
	{
		_allChatts.remove(name);
	}
	static void removeAllChats()
	{
		_allChatts.clear();
	}
	
	static Enumeration getAllTheChats()
	{
		return _allChatts.elements();
	}
}