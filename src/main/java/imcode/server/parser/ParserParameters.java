package imcode.server.parser;

import imcode.server.DocumentRequest;

import javax.servlet.http.HttpServletRequest;

@Deprecated
public class ParserParameters implements Cloneable {
    private static final String ATTRIBUTE_NAME = ParserParameters.class.getName();
    private DocumentRequest documentRequest;
    private int flags;

    @Deprecated
    public ParserParameters(DocumentRequest documentRequest) {
        this.documentRequest = documentRequest;
    }

    @Deprecated
    public static ParserParameters putInRequest(ParserParameters parserParameters) {
        HttpServletRequest request = parserParameters.getDocumentRequest().getHttpServletRequest();
        Object attribute = request.getAttribute(ATTRIBUTE_NAME);
        request.setAttribute(ATTRIBUTE_NAME, parserParameters);
        return (ParserParameters) attribute;
    }

    private DocumentRequest getDocumentRequest() {
        return this.documentRequest;
    }

    @Deprecated
    public int getFlags() {
        return this.flags;
    }

    @Deprecated
    public void setFlags(int flags) {
        this.flags = flags;
    }
}