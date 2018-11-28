package com.imcode.imcms.domain.component;

import com.imcode.imcms.model.IpAccessRule;
import lombok.Data;
import org.apache.commons.validator.routines.InetAddressValidator;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

@Data
public class AccessRuleValidationResult {

    private boolean wrongIpRange;

    private boolean validAccessRuleData;

    AccessRuleValidationResult(IpAccessRule accessRule) {
        validateIpRange(accessRule.getIpRange(), this::setWrongIpRange);
        sumUpValidation();
    }

    private void sumUpValidation() {
        this.validAccessRuleData = !wrongIpRange;
    }

    private void validateIpRange(String ipAccessRule, Consumer<Boolean> wrongIpRange) {

        boolean isValid = false;
        if (null == ipAccessRule) {
            isValid = true;
        } else {
            final InetAddressValidator inetAddressValidator = InetAddressValidator.getInstance();

            List<String> ipRange = Arrays.asList(ipAccessRule.split("-"));
            if (ipRange.size() == 1) {
                isValid = inetAddressValidator.isValid(ipRange.get(0));
            }

            if (ipRange.size() == 2) {
                isValid = inetAddressValidator.isValid(ipRange.get(0)) && inetAddressValidator.isValid(ipRange.get(1));
            }
        }

        wrongIpRange.accept(!isValid);
    }

}
