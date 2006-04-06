package imcode.server.document;

import com.imcode.imcms.api.NoPermissionException;

public class NoPermissionToCreateDocumentException extends NoPermissionException {
    public NoPermissionToCreateDocumentException(String message) {
        super(message) ;
    }
}
