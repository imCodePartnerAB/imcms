package imcode.server.document;

import imcode.server.user.UserDomainObject;

public abstract class FormerExternalDocumentDomainObject extends DocumentDomainObject {

    protected void loadAllLazilyLoadedDocumentTypeSpecificAttributes() {
        // nothing lazily loaded
    }

    public void saveDocument( DocumentMapper documentMapper, UserDomainObject user ) {
    }

    public void saveNewDocument( DocumentMapper documentMapper, UserDomainObject user ) {
    }

    public void initDocument( DocumentMapper documentMapper ) {
    }

}
