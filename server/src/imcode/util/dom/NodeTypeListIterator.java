package imcode.util.dom ;

import java.util.* ;

import org.w3c.dom.* ;

public class NodeTypeListIterator extends NodeListIterator {

    protected short nodeType ;
    protected Node nextNode = null ;

    public NodeTypeListIterator(NodeList nodeList, short nodeType) {
	super(nodeList) ;
	this.nodeType = nodeType ;
    }

    public boolean hasNext() {
	if (nextNode == null) {
	    for (Node aNode = null; super.hasNext(); ) {
		aNode = (Node)super.next() ;
		if (aNode.getNodeType() == nodeType) {
		    nextNode = aNode ;
		    return true ;
		}
	    }
	    return false ;
	} else {
	    return true ;
	}
    }

    public Object next() {
	if (!hasNext()) {
	    throw new NoSuchElementException() ;
	}
	Node aNode = nextNode ;
	nextNode = null ;
	return aNode ;
    }
}
