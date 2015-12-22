package com.imcode.imcms.imagearchive.util;

import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.Errors;

public class ValidatorUtils {
    private ValidatorUtils() {
    }

    public static void rejectValueIfLonger(String field, int maxLength, String code, Errors errors) {
        String value = (String) errors.getFieldValue(field);
        value = StringUtils.trimToNull(value);

        if (value != null && value.length() > maxLength) {
            errors.rejectValue(field, code, new Object[]{maxLength}, "???");
        }
    }
}
