
package imcode.external.chat;

import java.util.*;

public class ChatGroup{
	private final static String CVS_REV = "$Revision$" ;
	private final static String CVS_DATE = "$Date$" ;
	
	private int _groupId;
	private String _name;
	private List _groupMembers;
	private Counter _msgNrCounter;
	private Counter _membersCounter;

	/**
	*Default constructor
	*@param groupNumber The groupNumber that this ChatGroup will have
	*/

	protected ChatGroup(int groupNumber,String groupName){
		_groupId = groupNumber;
		_name = groupName;
		_groupMembers = Collections.synchronizedList(new LinkedList());
		_msgNrCounter = new Counter();
		_membersCounter = new Counter();

	}

	/**
	*Gets the id for this group
	*@return The idnumber for this group
	*/

	public int getGroupId(){
		return _groupId;
	}

	/**
	*Sets the name of the ChatGroup
	*@param chatGroupName The name of the ChatGroup
	*/
	public synchronized void setChatGroupName(String chatGroupName){
		_name = chatGroupName;
	}

	/**
	*Gets the name of this ChatGroup
	*return The name of this ChatGroup or an empty string if the name hasn't
	*been set
	*/
	public String getGroupName(){
		return (_name == null) ? "" : _name;
	}

	/**
	*Gets the currently number of ChatMembers in this ChatGroup. 
	*@return The currently number of ChatMembers in this ChatGrou.
	*/
	public int getNrOfGroupMembers(){
		return _groupMembers.size();
	}

	/**
	*Gets an Iterator of all GroupMembers currently in this ChatGroup
	*@return An Iterator of all GroupMembers currently in this ChatGroup
	*/
	public Iterator getAllGroupMembers(){
		return _groupMembers.iterator();
	}

	/**
	*Adds a ChatMember to this ChatGroup
	*@param member The ChatMember to add into the ChatGroup
	*/
	public synchronized void addNewGroupMember(ChatMember member){
		_groupMembers.add(member);
		member.setCurrentGroup(this);
	}

	/**
	*Removes a ChatMember from this ChatGroup
	*@param member The ChatMember you want to remove
	*If not the  ChatMember exists in this group no action is taken.
	*/
	public synchronized void removeGroupMember(ChatMember member){
		_groupMembers.remove(member);
	}


	/**
	*Adds a ChatMsg into all members of this ChatGroup
	*@param msg The ChatMsg you want to add
	*/
	public synchronized void addNewMsg(ChatMsg msg){
		_msgNrCounter.increment();
		msg.setIdNumber(_msgNrCounter.getValue());
		Iterator iter = _groupMembers.iterator();
		//spred the msg to the members
		while (iter.hasNext()){
			ChatMember tempMember = (ChatMember) iter.next();
			tempMember.addNewMsg(msg);
		}
	}

	public String toString(){
		return "Group: " + _name + " GroupId: " + _groupId;
	}
}//end class
