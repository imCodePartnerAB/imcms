package imcode.server.document;

import imcode.util.LocalizedMessage;

public class UrlDocumentDomainObject extends DocumentDomainObject {

    private String url = "";

    public String getUrl() {
        return url;
    }

    public void setUrl( String url ) {
        this.url = url;
    }

    public DocumentTypeDomainObject getDocumentType() {
        return DOCTYPE_URL ;
    }

    public void accept( DocumentVisitor documentVisitor ) {
        documentVisitor.visitUrlDocument( this );
    }

}