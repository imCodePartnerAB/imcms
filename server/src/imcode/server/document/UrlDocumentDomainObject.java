/*
 * Created by IntelliJ IDEA.
 * User: kreiger
 * Date: 2004-feb-28
 * Time: 20:38:07
 */
package imcode.server.document;

import imcode.server.user.UserDomainObject;

public class UrlDocumentDomainObject extends DocumentDomainObject {

    private String urlDocumentUrl = "";

    public String getUrl() {
        return urlDocumentUrl;
    }

    public void setUrlDocumentUrl( String urlDocumentUrl ) {
        this.urlDocumentUrl = urlDocumentUrl;
    }

    protected void loadAllLazilyLoadedDocumentTypeSpecificAttributes() {
        // nothing lazily loaded
    }

    public int getDocumentTypeId() {
        return DOCTYPE_URL;
    }

    public void saveDocument( DocumentMapper documentMapper, UserDomainObject user ) {
        documentMapper.saveUrlDocument( this );
    }

    public void saveNewDocument( DocumentMapper documentMapper, UserDomainObject user ) {
        documentMapper.saveNewUrlDocument( this );
    }

    public void initDocument( DocumentMapper documentMapper ) {
        documentMapper.initUrlDocument( this );
    }

}