package imcode.server.parser ;

import java.util.List ;
import java.util.Properties ;

public interface Element extends Node {
    final static String CVS_REV = "$Revision$" ;
    final static String CVS_DATE = "$Date$" ;

    /** @return The name of this element. **/
    public String getName() ;

    /** @return A Properties containing the attributes of this element. **/
    public Properties getAttributes() ;

    /** @return A List of Nodes below this Node. **/
    List getChildren() ;

    /** @return The first child-element with the given name, or null. **/
    Element getChildElement(String name) ;

    String getTextContent() ;
}
