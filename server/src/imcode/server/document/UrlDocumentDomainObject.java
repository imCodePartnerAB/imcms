package imcode.server.document;

public class UrlDocumentDomainObject extends DocumentDomainObject {

    private String url = "";

    public String getUrl() {
        return url;
    }

    public void setUrl( String url ) {
        this.url = url;
    }

    public int getDocumentTypeId() {
        return DOCTYPE_URL;
    }

    public void accept( DocumentVisitor documentVisitor ) {
        documentVisitor.visitUrlDocument( this );
    }

}