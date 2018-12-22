package com.imcode.imcms.domain.component;

import com.imcode.imcms.domain.exception.IpAccessRuleValidationException;
import com.imcode.imcms.model.IpAccessRule;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class AccessRuleValidationActionConsumer {

    private final AccessRuleValidator ruleValidator;

    protected AccessRuleValidationActionConsumer(AccessRuleValidator ruleValidator) {
        this.ruleValidator = ruleValidator;
    }

    public IpAccessRule doIfValid(IpAccessRule rule, Function<IpAccessRule, IpAccessRule> doIfValid) throws IpAccessRuleValidationException {
        final AccessRuleValidationResult validationResult = ruleValidator.validate(rule);

        if (validationResult.isValidAccessRuleData()) {
            return doIfValid.apply(rule);

        }

        throw new IpAccessRuleValidationException(validationResult);
    }
}
