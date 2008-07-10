package imcode.server.document;

import com.imcode.imcms.mapping.NoPermissionInternalException;

public class NoPermissionToCreateDocumentException extends NoPermissionInternalException {
    public NoPermissionToCreateDocumentException(String message) {
        super(message) ;
    }
}
