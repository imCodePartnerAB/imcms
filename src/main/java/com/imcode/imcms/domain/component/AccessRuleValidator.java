package com.imcode.imcms.domain.component;

import com.imcode.imcms.model.IpAccessRule;
import org.springframework.stereotype.Component;

@Component
public class AccessRuleValidator {

    AccessRuleValidationResult validate(IpAccessRule rule) {
        return new AccessRuleValidationResult(rule);
    }

}
