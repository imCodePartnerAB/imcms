package imcode.server.document.textdocument;

import com.imcode.imcms.mapping.NoPermissionInternalException;

public class NoPermissionToAddDocumentToMenuException extends NoPermissionInternalException {
    public NoPermissionToAddDocumentToMenuException(String s) {
        super(s);
    }
}
