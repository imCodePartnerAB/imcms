package imcode.server.document.textdocument;

import com.imcode.imcms.api.NoPermissionException;

public class NoPermissionToAddDocumentToMenuException extends NoPermissionException {
    public NoPermissionToAddDocumentToMenuException(String s) {
        super(s);
    }
}
