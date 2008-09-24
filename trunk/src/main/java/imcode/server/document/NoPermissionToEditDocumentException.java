package imcode.server.document;

import com.imcode.imcms.mapping.NoPermissionInternalException;

public class NoPermissionToEditDocumentException extends NoPermissionInternalException {
    public NoPermissionToEditDocumentException(String message) {
        super(message) ;
    }
}
