package imcode.server.document;

import imcode.util.LocalizedMessage;

public class HtmlDocumentDomainObject extends DocumentDomainObject {

    private String html = "";

    public void setHtml( String html ) {
        this.html = html;
    }

    public String getHtml() {
        return html;
    }

    public int getDocumentTypeId() {
        return DOCTYPE_HTML;
    }

    public void accept( DocumentVisitor documentVisitor ) {
        documentVisitor.visitHtmlDocument( this );
    }

    public LocalizedMessage getDocumentTypeName() {
        return new LocalizedMessage( DOCUMENT_TYPE_NAME_LOCALIZED_MESSAGE_PREFIX+"html" );
    }

}