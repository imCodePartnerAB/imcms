/*
 * Created by IntelliJ IDEA.
 * User: kreiger
 * Date: 2004-feb-28
 * Time: 20:38:07
 */
package imcode.server.document;

import imcode.util.LocalizedMessage;

public class BillboardDocumentDomainObject extends FormerExternalDocumentDomainObject {

    public int getDocumentTypeId() {
        return DOCTYPE_BILLBOARD;
    }

    public LocalizedMessage getDocumentTypeName() {
        return new LocalizedMessage( DOCUMENT_TYPE_NAME_LOCALIZED_MESSAGE_PREFIX+"billboard");
    }
}