package imcode.server.document;

public class HtmlDocumentDomainObject extends DocumentDomainObject {

    private String html = "";

    public void setHtml( String html ) {
        this.html = html;
    }

    public String getHtml() {
        return html;
    }

    public DocumentTypeDomainObject getDocumentType() {
        return DOCTYPE_HTML;
    }

    public void accept( DocumentVisitor documentVisitor ) {
        documentVisitor.visitHtmlDocument( this );
    }

}