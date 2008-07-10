package imcode.util;

import com.imcode.imcms.mapping.NoPermissionInternalException;

public class ShouldHaveCheckedPermissionsEarlierException extends ShouldNotBeThrownException {
    public ShouldHaveCheckedPermissionsEarlierException(NoPermissionInternalException e) {
        super(e);
    }
}
