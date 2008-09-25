package imcode.server.parser;

import imcode.server.DocumentRequest;

public class MockParserParameters extends ParserParameters {

    private boolean menuMode;

    public MockParserParameters( DocumentRequest documentRequest ) {
        super( documentRequest );
    }

    public boolean isMenuMode() {
        return menuMode ;
    }

    public void setMenuMode( boolean menuMode ) {
        this.menuMode = menuMode;
    }

}
