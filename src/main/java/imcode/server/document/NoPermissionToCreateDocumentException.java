package imcode.server.document;

import com.imcode.imcms.controller.exception.NoPermissionInternalException;

public class NoPermissionToCreateDocumentException extends NoPermissionInternalException {
    public NoPermissionToCreateDocumentException(String message) {
        super(message);
    }
}
