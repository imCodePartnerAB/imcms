

package imcode.external.chat;

import java.util.*;

public class MsgBuffer{

	private List _msgBuffer;
	private final int _maxSize = 100;
	
	/**
	*Default constructor
	*/
	protected MsgBuffer(){
		_msgBuffer = Collections.synchronizedList(new LinkedList());
	}

	/**
	*Gets an Iterator of all ChatNormalMessage in the list
	*@return An Iterator of all ChatNormalMessage Object in the list
	*/
	public ListIterator getAllMsg()	{
		return _msgBuffer.listIterator();
	}

	/**
	*Gets an Iterator of ChatNormalMessage
	*@return An Iterator of ChatNormalMessage Object in the list
	*/
	public ListIterator getMessages(ChatMessage lastMsg, int nrOfOldOnes){
		//get the number for the last read msg
		int start = _msgBuffer.indexOf(lastMsg);
		start = start - nrOfOldOnes;
		if (start < 0) start = 0;
		return _msgBuffer.listIterator(start);
	}

	/**
	*Gets the Number of messages in the list
	*@return The number of messages in the list
	*/
	public int getNrOfMsg(){
		return _msgBuffer.size();
	}

	/**
	*Adds the supplied ChatMessage into the list
	*if the list has reashed the max size then the oldest is removed
	*@param msg The ChatMessage object you want to add to the list.
	*/

    protected void addNewMsg( ChatMessage msg ) {

        synchronized ( _msgBuffer ) {
            pruneBuffer();
            _msgBuffer.add( 0, msg );
        }
    }

    private void pruneBuffer() {
        if ( _msgBuffer.size() > _maxSize ) {
            _msgBuffer.remove( _msgBuffer.size() - 1 );
        }
    }

    public List get_msgBuffer() {
        return _msgBuffer;
    }


}//end class
