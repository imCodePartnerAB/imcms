package imcode.server.parser;

import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

public class SimpleElement implements Element {

    private String name;
    private Properties attributes;
    private List<Element> children;

    public SimpleElement(String name, Properties attributes, List children) {
        this.name = name != null ? name : "";
        this.attributes = attributes != null ? attributes : new Properties();
        this.children = children != null ? children : new LinkedList();
    }

    public short getNodeType() {
        return Node.ELEMENT_NODE;
    }

    public String getName() {
        return name;
    }

    public Properties getAttributes() {
        return attributes;
    }

    public List<Element> getChildren() {
        return children;
    }

    public Element getChildElement(String name) {
        for (Element childNode : children) {
            if (childNode.getNodeType() == Node.ELEMENT_NODE && childNode.getName().equals(name)) {
                return childNode;
            }
        }
        return null;
    }

    public String getTextContent() {
        StringBuffer result = new StringBuffer();
        getTextContent(this, result);
        return result.toString();
    }

    private void getTextContent(Node node, StringBuffer result) {
        if (node.getNodeType() == TEXT_NODE) {
            result.append(((Text) node).getContent());
        } else if (node.getNodeType() == ELEMENT_NODE) {
            for (Object o : ((Element) node).getChildren()) {
                getTextContent((Node) o, result);
            }
        }
    }

    public String toString() {
        StringBuffer string = new StringBuffer();

        string.append("<?imcms:").append(getName());
        Enumeration enumeration = getAttributes().propertyNames();
        while (enumeration.hasMoreElements()) {
            String key = (String) enumeration.nextElement();
            String value = getAttributes().getProperty(key);
            char quote = (value.indexOf('"') == -1) ? '"' : '\'';
            string.append(' ').append(key).append('=').append(quote).append(value).append(quote);
        }
        string.append("?>");
        for (Element aChildren : children) {
            string.append(aChildren.toString());
        }
        string.append("<?/imcms:").append(getName()).append("?>");
        return string.toString();
    }
}
