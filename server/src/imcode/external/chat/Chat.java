
package imcode.external.chat;

import java.util.*;

/**
*A Chat can contain zero or many ChatGroups.
*A Chat can contain zero or many ChatMembers.
*
*From the Chat you can create ChatGroups and ChatMembers
*/
public class Chat
{
	private int _chatId;
	private String _name;
//	private Hashtable _chatMembers;
	private Hashtable _chatMsgTypes;
	private Hashtable _chatGroups;
//	private Counter _memberCounter;
	private Counter _roomCounter;
	private	int _updateTime = 30;
	private	int _reload = 2;
	private int _inOut = 2;
	private	int _privat = 2;
	private	int _publik = 2;
	private	int _dateTime = 2;
	private	int _font = 2;


	/**
	*Default constructor.
	*/
	public Chat()
	{
	//	_memberCounter = new Counter();
		_roomCounter = new Counter();
	//	_chatMembers = new Hashtable();
		_chatGroups = new Hashtable();	
	}

	public Chat(int id, String name, Vector groups)
	{
		_chatId=id;
		_name=name;
	//	_memberCounter = new Counter();
		_roomCounter = new Counter();
	//	_chatMembers = new Hashtable();
		_chatGroups = new Hashtable();
		
		for(int i=0;i<groups.size();i+=2)
		{
			//create group
			ChatGroup tempGroup = new ChatGroup( ((Integer)groups.get(i)).intValue(),(String)groups.get(i+1));
			_roomCounter.increment();
			//add group to grouplist
			_chatGroups.put(  (Integer)groups.get(i)  , tempGroup );
		}
			
	}
	
	public Chat(int id, Vector groups, Vector msgTypes, Properties params)
	{
		_chatId=id;
	//	_memberCounter = new Counter();
		_roomCounter = new Counter();
	//	_chatMembers = new Hashtable();
		_chatGroups = new Hashtable();
		_chatMsgTypes = new Hashtable();
		
		for(int i=0;i<groups.size();i+=2)
		{
			//create group
			ChatGroup tempGroup = new ChatGroup( ((Integer)groups.get(i)).intValue(),(String)groups.get(i+1) );
			_roomCounter.increment();
			//add group to grouplist
			_chatGroups.put( (Integer)groups.get(i) ,tempGroup );
			
		}
		
		for(int i=0;i<msgTypes.size();i+=2)
		{
			_chatMsgTypes.put(msgTypes.get(i),msgTypes.get(i+1));
		}
		
		_name =  params.getProperty("chatName");
		_updateTime = Integer.parseInt( params.getProperty("updateTime" , "30" ) );
		_reload = Integer.parseInt( params.getProperty("reload" , "2" ) );
	    _inOut = Integer.parseInt( params.getProperty("inOut" , "2" ) );
		_privat = Integer.parseInt( params.getProperty("privat" , "2" ) );
		_publik = Integer.parseInt( params.getProperty("publik" , "2" ) );
		_dateTime = Integer.parseInt( params.getProperty("dateTime" , "2" ) );
		_font = Integer.parseInt( params.getProperty("font" , "2" ) );
			
	}
	

	/**
	*Creates and "register" a new ChatMember in this chat
	*the is initially not a member of any ChatGroup;
	*@return The Created ChatMember
	*/
/*	public ChatMember createChatMember()
	{
		_memberCounter.increment();
		int memberNumber = _memberCounter.getValue();
		ChatMember newMember = new ChatMember(memberNumber);
		_chatMembers.put(String.valueOf(memberNumber), newMember);
		return newMember;
	}
	
	/**
	*Removes a ChatMember from this chat
	*@param memberNumber The membernumber of the ChatMember you want to remove
	*If no ChatMember exists with the supplied memberNumber, no action is taken.
	*/
/*	public void removeChatMember(int memberNumber)
	{
		String memberNumberString = String.valueOf(memberNumber);
		_chatMembers.remove(memberNumberString);
	}
	
	/**
	*Gets the ChatMember with the supplied memberNumber
	*@param memberNumber
	*@return The ChatMember with the supplied memberNumber or 
	*null if no registerd ChatMember matches the given memberNumber
	*/
/*	public ChatMember getChatMember(int memberNumber)
	{
		String memberNumberString = String.valueOf(memberNumber);		
		return (ChatMember)_chatMembers.get(memberNumberString);
	}

	/**
	*Creates and "register" a new ChatGroup in this chat
	*the ChatGroup is initially not a member of any chatRoom;
	*@return The Created ChatGroup
	*/
/*	public ChatGroup createNewChatGroup()
	{
		_roomCounter.increment();
	//	int groupNumber = _roomCounter.getValue();
		ChatGroup newGroup = new ChatGroup(groupNumber);
		_chatGroups.put(String.valueOf(groupNumber), newGroup);
		return newGroup;
	}

	/**
	*Removes a ChatGroup from this chat
	*@param groupNumber The groupNumber of the ChatGroup you want to remove
	*If no ChatGroup exists with the supplied groupNumber, no action is taken.
	*/
	public void removeChatGroup(int groupNumber)
	{
		String groupNumberString = String.valueOf(groupNumber);
		_chatGroups.remove(groupNumberString);
	}
	
	/**Gets all groups
	*@return An Enumeration of all avalible groups
	*/
	public Enumeration getAllChatGroups()
	{
		return _chatGroups.elements();
	}

	/**
	*Gets the 
	*@param groupNumber
	*@return The ChatGroup with the supplied groupNumber or 
	*null if no registerd ChatGroup matches the given groupNumber
	*/
	public ChatGroup getChatGroup(int groupNumber)
	{
		String groupNumberString = String.valueOf(groupNumber);
		return (ChatGroup) _chatGroups.get(groupNumberString);	
	}


}
