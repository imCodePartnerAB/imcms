package imcode.server.parser ;

import java.util.List ;
import java.util.LinkedList ;
import java.util.Properties ;
import java.util.Enumeration ;
import java.util.Iterator ;

public class SimpleElement implements Element {

    private String name ;
    private Properties attributes ;
    private List children ;

    public SimpleElement(String name, Properties attributes, List children) {
	this.name = name != null ? name : "" ;
	this.attributes = attributes != null ? attributes : new Properties() ;
	this.children = children != null ? children : new LinkedList() ;
    }

    public short getNodeType() {
	return Node.ELEMENT_NODE ;
    }

    public String getName() {
	return name ;
    }

    public Properties getAttributes() {
	return attributes ;
    }

    public List getChildren() {
	return children ;
    }

    public Element getChildElement(String name) {
	Iterator childrenIterator = children.iterator() ;
	while (childrenIterator.hasNext()) {
	    Node childNode = (Node) childrenIterator.next() ;
	    if (childNode.getNodeType() == Node.ELEMENT_NODE) {
		if (((Element)childNode).getName().equals(name)) {
		    return (Element)childNode ;
		}
	    }
	}
	return null ;
    }

    public String getTextContent() {
	StringBuffer result = new StringBuffer() ;
	getTextContent(this,result) ;
	return result.toString() ;
    }

    private void getTextContent(Node node, StringBuffer result) {
	if (node.getNodeType() == TEXT_NODE) {
	    result.append(((Text)node).getContent()) ;
	    return ;
	} else if (node.getNodeType() == ELEMENT_NODE) {
	    Iterator childIterator = ((Element)node).getChildren().iterator() ;
	    while ( childIterator.hasNext() ) {
		getTextContent((Node)childIterator.next(),result) ;
	    }
	}
    }

    public String toString() {

	StringBuffer string = new StringBuffer() ;

	string.append("<?imcms:").append(getName()) ;
	Enumeration enumeration = getAttributes().propertyNames() ;
	while(enumeration.hasMoreElements()) {
	    String key = (String)enumeration.nextElement() ;
	    String value = getAttributes().getProperty(key) ;
	    char quote = (value.indexOf('"') == -1) ? '"' : '\'' ;
	    string.append(' ').append(key).append('=').append(quote).append(value).append(quote) ;
	}
	string.append("?>") ;
	Iterator childIterator = children.iterator() ;
	while(childIterator.hasNext()) {
	    string.append(childIterator.next().toString()) ;
	}
	string.append("<?/imcms:").append(getName()).append("?>") ;
	return string.toString() ;
    }
}
