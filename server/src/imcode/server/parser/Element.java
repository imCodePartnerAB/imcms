package imcode.server.parser ;

import java.util.List ;
import java.util.Properties ;

public interface Element extends Node {

    /** @return The name of this element. **/
    public String getName() ;

    /** @return A Properties containing the attributes of this element. **/
    public Properties getAttributes() ;

    /** @return A List of Nodes below this Node. **/
    public List getChildren() ;

    /** @return The first child-element with the given name, or null. **/
    public Element getChildElement(String name) ;

    /** @return The plain text-content of this element and all child-elements, without tags. **/
    public String getTextContent() ;
}
