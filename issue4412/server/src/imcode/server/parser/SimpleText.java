package imcode.server.parser ;

import java.util.List ;

public class SimpleText implements Text {

    private String content ;

    public SimpleText(String text) {
	content = text ;
    }

    public String getContent() {
	return content ;
    }

    public String toString() {
	return getContent() ;
    }

    public short getNodeType() {
	return Node.TEXT_NODE ;
    }

}
