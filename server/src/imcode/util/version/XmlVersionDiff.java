package imcode.util.version ;

import java.io.* ;
import java.util.* ;
import java.util.zip.* ;
import java.text.* ;

import javax.xml.parsers.* ;

import org.w3c.dom.* ;
import org.w3c.dom.* ;
import org.xml.sax.* ;

import imcode.util.* ;
import imcode.util.dom.* ;

public class XmlVersionDiff implements NodeList {

    private Document diffDocument ;

    public XmlVersionDiff (Document fromDocument, Document toDocument) throws ParserConfigurationException, VersionParseException {
	// Create a new diff-document
	DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance() ;
	DocumentBuilder builder = factory.newDocumentBuilder() ;
	diffDocument = builder.newDocument() ;
	String version = toDocument.getDocumentElement().getAttribute("version") ;
	Element diffElement = diffDocument.createElement("diff") ;
	diffElement.setAttribute("version",version) ;
	diffDocument.appendChild(diffElement) ;

	diffTree(fromDocument.getDocumentElement().getChildNodes(),toDocument.getDocumentElement().getChildNodes(),diffElement) ;
    }

    public void diffTree(NodeList fromChildren, NodeList toChildren, Element diffParentElement) {
	Map fromChildrenMap = getMapFromNodeList(fromChildren) ;
	Map toChildrenMap   = getMapFromNodeList(toChildren) ;

	Map removed = new HashMap() ;
	Map added = new HashMap() ;
	Map changed = new HashMap() ;

	for (Iterator iterator = fromChildrenMap.keySet().iterator() ; iterator.hasNext() ; ) {
	    String name = (String) iterator.next() ;
	    Element fromChild = (Element)fromChildrenMap.get(name) ;
	    Element toChild = (Element)toChildrenMap.get(name) ;
	    if (toChild == null) {
		removed.put(name,fromChild) ;
	    } else {
		changed.put(name,new Element[] {fromChild, toChild}) ;
	    }
	}

	for (Iterator iterator = toChildrenMap.keySet().iterator() ; iterator.hasNext() ; ) {
	    String name = (String) iterator.next() ;
	    Element fromChild = (Element)fromChildrenMap.get(name) ;
	    Element toChild = (Element)toChildrenMap.get(name) ;
	    if (fromChild == null) {
		added.put(name,toChild) ;
	    }
	}

	// Now all elements are in added, removed, or changed.
	for (Iterator iterator = changed.keySet().iterator() ; iterator.hasNext() ; ) {
	    String name = (String) iterator.next() ;
	    Element[] fromAndTo = (Element[])changed.get(name) ;
	    diffElements(fromAndTo[0],fromAndTo[1],diffParentElement) ;
	}

	for (Iterator iterator = removed.keySet().iterator() ; iterator.hasNext() ; ) {
	    String name = (String) iterator.next() ;
	    Element element = (Element)removed.get(name) ;
	    Element diffElement = (Element)diffDocument.importNode(element, false) ;
	    diffElement.setAttribute("diff", "removed") ;
	    diffParentElement.appendChild(diffElement) ;
	    if (element.getTagName().equals("directory")) {
		diffTree(element.getChildNodes(),this,diffElement) ;
	    }
	}

	for (Iterator iterator = added.keySet().iterator() ; iterator.hasNext() ; ) {
	    String name = (String) iterator.next() ;
	    Element element = (Element)added.get(name) ;
	    Element diffElement = (Element)diffDocument.importNode(element, false) ;
	    diffElement.setAttribute("diff", "added") ;
	    diffParentElement.appendChild(diffElement) ;
	    if (element.getTagName().equals("directory")) {
		diffTree(this,element.getChildNodes(),diffElement) ;
	    }
	}
    }

    private void diffElements(Element fromElement, Element toElement, Element diffParentElement) {
	NamedNodeMap nodeMap = fromElement.getAttributes() ;
	Element diffElement = (Element)diffDocument.importNode(toElement, false) ;
	for (int i = 0; i < nodeMap.getLength(); ++i) {
	    Attr attr = (Attr)nodeMap.item(i) ;
	    String fromAttrName = attr.getName() ;
	    String fromAttrValue = attr.getValue() ;
	    String toAttrValue = toElement.getAttribute(fromAttrName) ;
	    if (!fromAttrValue.equals(toAttrValue)) {
		diffElement.setAttribute("diff", "changed") ;
		diffElement.setAttribute("previous-"+fromAttrName, fromAttrValue) ;
	    }
	}
	diffParentElement.appendChild(diffElement) ;
	if (diffElement.getTagName().equals("directory")) {
	    diffTree(fromElement.getChildNodes(),toElement.getChildNodes(),diffElement) ;
	}
    }

    private Map getMapFromNodeList(NodeList nodeList) {
	Map theMap = new HashMap() ;
	Iterator iterator = new NodeTypeListIterator(nodeList,Node.ELEMENT_NODE) ;
	while (iterator.hasNext()) {
	    Element element = (Element)iterator.next() ;
	    String name = element.getAttribute("name") ;
	    theMap.put(name,element) ;
	}
	return theMap ;
    }

    private Element findElementByAttribute(NodeList nodeList, String attributeName, String attributeValue) {
	for (int i = 0; i < nodeList.getLength(); ++i) {
	    Node node = nodeList.item(i) ;
	    if (node.getNodeType() == Node.ELEMENT_NODE) {
		Element element = (Element)node ;
		if (element.getAttribute(attributeName).equals(attributeValue)) {
		    return element ;
		}
	    }
	}
	return null ;
    }

    public int getLength() {
	return 0 ;
    }

    public Node item(int i) {
	return null ;
    }

    public Document getDiffDocument() {
	return diffDocument ;
    }

    public static void main (String[] args) throws ParserConfigurationException, IOException, SAXException, VersionParseException {
	XmlVersion v1 = new XmlVersion(new File(args[0])) ;
	XmlVersion v2 = new XmlVersion(new File(args[1])) ;
	XmlVersionDiff d = new XmlVersionDiff(v1.getDocument(),v2.getDocument()) ;
    }

}
