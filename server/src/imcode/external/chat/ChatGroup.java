
package imcode.external.chat;

import imcode.server.IMCServiceInterface;

import javax.servlet.ServletException;
import java.io.IOException;
import java.util.*;

public class ChatGroup{

    private List _groupMembers;
	private Counter _msgNrCounter;
    private static final int AUTO_LOGOUT_TIME = 30;
    private List _msgBuffer;
    private int _maxBufferSize = 200;



    /**
	*Default constructor
     */

    ChatGroup(){
		_groupMembers = Collections.synchronizedList(new LinkedList());
		_msgNrCounter = new Counter();
        _msgBuffer = Collections.synchronizedList(new LinkedList());
 	}

    /**
	*Gets an Iterator of all GroupMembers currently in this ChatGroup
	*@return An Iterator of all GroupMembers currently in this ChatGroup
	*/
	public Iterator getAllGroupMembers(){
        return _groupMembers.iterator();
	}

    public List getGroupMembers() {
        return _groupMembers;
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
	public synchronized void addNewMsg( ChatBase chatBase, ChatMessage msg, IMCServiceInterface imcref ) throws IOException, ServletException{
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
            if (!theMember.isTimedOut()){
                theMember.setTimedOut(true);
                chatBase.createLeaveMessageAndAddToGroup( theMember, systemMessage, imcref );
            }

            //chatBase.logOutMember(theMember, systemMessage, imcref, chatref);
        }

	}


    public List get_msgBuffer() {
        return _msgBuffer;
    }

    private void addNewMsgToGroup( ChatMessage msg ) {

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

}//end class
