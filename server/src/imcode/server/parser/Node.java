package imcode.server.parser ;

public interface Node {
    private final static String CVS_REV = "$Revision$" ;
    private final static String CVS_DATE = "$Date$" ;

    final static short TEXT_NODE    = 0 ;
    final static short ELEMENT_NODE = 1 ;

    short getNodeType() ;

}
