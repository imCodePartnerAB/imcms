package imcode.server.document;

import com.imcode.imcms.api.NoPermissionException;

public class NoPermissionToEditDocumentException extends NoPermissionException {
    public NoPermissionToEditDocumentException(String message) {
        super(message) ;
    }
}
