package imcode.server.parser;

public interface Node {

    short TEXT_NODE = 0;
    short ELEMENT_NODE = 1;

    short getNodeType();
}
