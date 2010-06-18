package com.imcode.imcms.addon.imagearchive.util;

import javax.servlet.http.HttpSession;

import com.imcode.imcms.addon.imagearchive.SessionConstants;

public class SessionUtils {
    public static String getImcmsReturnToUrl(HttpSession session) {
        return (String) session.getAttribute(SessionConstants.IMCMS_RETURN_URL);
    }
    
    private SessionUtils() {
    }
}
