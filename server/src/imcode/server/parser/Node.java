package imcode.server.parser ;

public interface Node {

    final static short TEXT_NODE    = 0 ;
    final static short ELEMENT_NODE = 1 ;

    short getNodeType() ;

}
