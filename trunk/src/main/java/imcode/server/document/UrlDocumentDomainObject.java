package imcode.server.document;

import org.apache.oro.text.perl.Perl5Util;

public class UrlDocumentDomainObject extends DocumentDomainObject {

    private String url = "";

    public String getUrl() {
        Perl5Util regexp = new Perl5Util();
        if ( !regexp.match( "m!^\\w+:|^[/.]!", url ) ) {
            String scheme = "http";
            if (url.toLowerCase().startsWith( "ftp." )) {
                scheme = "ftp" ;
            }
            return scheme + "://" + url ;
        }
        return url;
    }

    public void setUrl( String url ) {
        this.url = url;
    }

    public DocumentTypeDomainObject getDocumentType() {
        return DocumentTypeDomainObject.URL ;
    }

    public void accept( DocumentVisitor documentVisitor ) {
        documentVisitor.visitUrlDocument( this );
    }

}