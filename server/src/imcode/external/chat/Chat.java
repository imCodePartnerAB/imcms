
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

	private Hashtable _chatMembers;
	private String _name;
	private Hashtable _chatGroups;
	private Counter _memberCounter;
	private Counter _roomCounter;


	/**
	*Default constructor.
	*/
	public Chat()
	{
		_memberCounter = new Counter();
		_roomCounter = new Counter();
		_chatMembers = new Hashtable();
		_chatGroups = new Hashtable();	
	}

	/**
	*Creates and "register" a new ChatMember in this chat
	*the is initially not a member of any ChatGroup;
	*@return The Created ChatMember
	*/
	public ChatMember createChatMember()
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
	public void removeChatMember(int memberNumber)
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
	public ChatMember getChatMember(int memberNumber)
	{
		String memberNumberString = String.valueOf(memberNumber);		
		return (ChatMember)_chatMembers.get(memberNumberString);
	}

	/**
	*Creates and "register" a new ChatGroup in this chat
	*the ChatGroup is initially not a member of any chatRoom;
	*@return The Created ChatGroup
	*/
	public ChatGroup createNewChatGroup()
	{
		_roomCounter.increment();
		int groupNumber = _roomCounter.getValue();
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
