package imcode.util.dom ;

import java.util.* ;

import org.w3c.dom.* ;

public class NodeListIterator implements Iterator {

    protected NodeList nodeList ;
    protected int nodeListIndex = 0 ;

    public NodeListIterator (NodeList nodeList) {
	this.nodeList = nodeList ;
    }

    public boolean hasNext() {
	return nodeListIndex < nodeList.getLength() ;
    }

    public Object next() {
	return nodeList.item(nodeListIndex++) ;
    }

    public void remove() {
	throw new UnsupportedOperationException() ;
    }

}
