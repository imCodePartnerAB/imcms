package imcode.server.parser ;

public interface Node {
    final static String CVS_REV = "$Revision$" ;
    final static String CVS_DATE = "$Date$" ;

    final static short TEXT_NODE    = 0 ;
    final static short ELEMENT_NODE = 1 ;

    short getNodeType() ;

}
