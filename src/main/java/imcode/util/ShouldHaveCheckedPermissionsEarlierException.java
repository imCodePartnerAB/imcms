package imcode.util;

import com.imcode.imcms.controller.exception.NoPermissionInternalException;

public class ShouldHaveCheckedPermissionsEarlierException extends ShouldNotBeThrownException {
    public ShouldHaveCheckedPermissionsEarlierException(NoPermissionInternalException e) {
        super(e);
    }
}
