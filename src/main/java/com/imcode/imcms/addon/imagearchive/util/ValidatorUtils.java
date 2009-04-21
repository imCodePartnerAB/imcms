package com.imcode.imcms.addon.imagearchive.util;

import org.apache.commons.lang.StringUtils;
import org.springframework.validation.Errors;

public class ValidatorUtils {
    public static void rejectValueIfLonger(String field, int maxLength, String code, Errors errors) {
        String value = (String) errors.getFieldValue(field);
        value = StringUtils.trimToNull(value);
        
        if (value != null && value.length() > maxLength) {
            errors.rejectValue(field, code, new Object[] {maxLength}, "???");
        }
    }
    
    private ValidatorUtils() {
    }
}
