package com.imcode.imcms.domain.exception;

import com.imcode.imcms.domain.component.AccessRuleValidationResult;

/**
 * When something wrong with user fields while create/edit ip access rule
 */
public class IpAccessRuleValidationException extends RuntimeException {
    private static final long serialVersionUID = 7799856979816003123L;

    public final AccessRuleValidationResult validationResult;

    public IpAccessRuleValidationException(AccessRuleValidationResult validationResult) {
        this.validationResult = validationResult;
    }
}
