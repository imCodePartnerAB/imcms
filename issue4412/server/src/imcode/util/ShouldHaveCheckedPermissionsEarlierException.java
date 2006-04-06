package imcode.util;

import com.imcode.imcms.api.NoPermissionException;

public class ShouldHaveCheckedPermissionsEarlierException extends ShouldNotBeThrownException {
    public ShouldHaveCheckedPermissionsEarlierException(NoPermissionException e) {
        super(e);
    }
}
