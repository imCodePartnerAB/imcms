
package imcode.external.chat;

import imcode.server.IMCPoolInterface;
import imcode.server.IMCServiceInterface;
import javax.servlet.ServletException;
import java.util.*;
import java.io.IOException;

public class ChatGroup{

	private int _groupId;
	private String _name;
	private List _groupMembers;
	private Counter _msgNrCounter;
    private static final int AUTO_LOGOUT_TIME = 30;
    private List _msgBuffer;
    private int _maxBufferSize = 200;


    /**
	*Default constructor
	*@param groupNumber The groupNumber that this ChatGroup will have
	*/

	protected ChatGroup(int groupNumber,String groupName){
		_groupId = groupNumber;
		_name = groupName;
		_groupMembers = Collections.synchronizedList(new LinkedList());
		_msgNrCounter = new Counter();
        _msgBuffer = Collections.synchronizedList(new LinkedList());


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
	*Adds a ChatNormalMessage into all members of this ChatGroup
	*@param msg The ChatNormalMessage you want to add
	*/
	public synchronized void addNewMsg( ChatBase chatBase, ChatMessage msg, IMCServiceInterface imcref, IMCPoolInterface chatref) throws IOException, ServletException{
        _msgNrCounter.increment();
		msg.setIdNumber(_msgNrCounter.getValue());
		Iterator iter = _groupMembers.iterator();

        // add message to the group
        addNewMsgToGroup( msg );

        //spred the msg to the members
        List membersWhichHaveTimedOut = new ArrayList() ;
        while (iter.hasNext()){
			ChatMember tempMember = (ChatMember) iter.next();
            Calendar autoLogOutTime = Calendar.getInstance() ;
            autoLogOutTime.setTime(tempMember.getLastRequest()) ;
            autoLogOutTime.add(Calendar.MINUTE, AUTO_LOGOUT_TIME) ;

            if (new Date().after(autoLogOutTime.getTime())) {
                membersWhichHaveTimedOut.add(tempMember) ;
                continue;
            }

            tempMember.addNewMsg(msg);
		}

        for (Iterator it = membersWhichHaveTimedOut.iterator(); it.hasNext(); ) {
            ChatMember theMember = (ChatMember)it.next();
            ChatSystemMessage systemMessage = new ChatSystemMessage(theMember, ChatSystemMessage.USER_TIMEDOUT_MSG) ;
            chatBase.logOutMember(theMember, systemMessage, imcref, chatref);
        }

	}


    public List get_msgBuffer() {
        return _msgBuffer;
    }

    protected void addNewMsgToGroup( ChatMessage msg ) {

        synchronized ( _msgBuffer ) {
            pruneBuffer();
            _msgBuffer.add( 0, msg );
        }
    }

    private void pruneBuffer() {
        if ( _msgBuffer.size() > _maxBufferSize ) {
            _msgBuffer.remove( _msgBuffer.size() - 1 );
        }
    }


    public String toString(){
		return "Group: " + _name + " GroupId: " + _groupId;
	}
}//end class
