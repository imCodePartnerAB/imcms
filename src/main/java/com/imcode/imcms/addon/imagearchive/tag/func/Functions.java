package com.imcode.imcms.addon.imagearchive.tag.func;

import java.util.Collection;
import java.util.Iterator;
import java.util.regex.Pattern;
import org.apache.commons.lang.StringUtils;

public class Functions {
    private static final Pattern NEWLINE_PATTERN = Pattern.compile("\r\n?");
    
    public static String newlineToBr(String value) {
        if (value == null) {
            return null;
        } else {
            return NEWLINE_PATTERN.matcher(value).replaceAll("<br/>");
        }
    }
    
    public static String abbreviate(String value, int maxLength) {
        return StringUtils.abbreviate(value, maxLength);
    }
    
    public static String join(Collection<Object> elements, String separator) {
        if (elements == null || elements.isEmpty()) {
            return "";
        }
        
        StringBuilder builder = new StringBuilder();
        
        Iterator<Object> it = elements.iterator();
        while (it.hasNext()) {
            builder.append(it.next());
            
            if (it.hasNext()) {
                builder.append(separator);
            }
        }
        
        return builder.toString();
    }
}
