package imcode.server.document;

import imcode.server.user.UserDomainObject;

public abstract class FormerExternalDocumentDomainObject extends DocumentDomainObject {

    public void saveDocument( DocumentMapper documentMapper, UserDomainObject user ) {
    }

    public void saveNewDocument( DocumentMapper documentMapper, UserDomainObject user ) {
    }

    public void initDocument( DocumentMapper documentMapper ) {
    }

    public void accept( DocumentVisitor documentVisitor ) {
        documentVisitor.visitFormerExternalDocument(this);
    }

}
