package imcode.server.document;

import com.imcode.imcms.controller.exception.NoPermissionInternalException;

public class NoPermissionToEditDocumentException extends NoPermissionInternalException {
    private static final long serialVersionUID = 1637111449471043129L;

    public NoPermissionToEditDocumentException(String message) {
        super(message);
    }
}
