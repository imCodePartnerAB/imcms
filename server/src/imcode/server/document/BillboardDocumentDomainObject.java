/*
 * Created by IntelliJ IDEA.
 * User: kreiger
 * Date: 2004-feb-28
 * Time: 20:38:07
 */
package imcode.server.document;

import imcode.util.LocalizedMessage;

public class BillboardDocumentDomainObject extends FormerExternalDocumentDomainObject {

    public DocumentTypeDomainObject getDocumentType() {
        return DocumentTypeDomainObject.BILLBOARD;
    }
}