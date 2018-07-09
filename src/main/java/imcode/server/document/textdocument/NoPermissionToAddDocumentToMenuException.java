package imcode.server.document.textdocument;

import com.imcode.imcms.controller.exception.NoPermissionInternalException;

public class NoPermissionToAddDocumentToMenuException extends NoPermissionInternalException {
    public NoPermissionToAddDocumentToMenuException(String s) {
        super(s);
    }
}
