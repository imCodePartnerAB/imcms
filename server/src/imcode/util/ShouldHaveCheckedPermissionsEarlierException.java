package imcode.util;

import imcode.server.document.NoPermissionToEditDocumentException;

public class ShouldHaveCheckedPermissionsEarlierException extends ShouldNotBeThrownException {
    public ShouldHaveCheckedPermissionsEarlierException(NoPermissionToEditDocumentException e) {
        super(e);
    }
}
