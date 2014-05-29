package com.imcode.imcms.addon.imagearchive.util;

import javax.servlet.http.HttpSession;

import com.imcode.imcms.addon.imagearchive.SessionConstants;

public class SessionUtils {
    /** Transfer selected image to Vaadin image handling window. */
    public static final String TRANSFER_TO_PICKER = "transferToPicker";

    public static String getImcmsReturnToUrl(HttpSession session) {
        return (String) session.getAttribute(SessionConstants.IMCMS_RETURN_URL);
    }

    public static void setTransferToPicker(HttpSession session) {
        session.setAttribute(TRANSFER_TO_PICKER, true);
    }

    public static boolean isTransferToPicker(HttpSession session) {
        return session.getAttribute(TRANSFER_TO_PICKER) != null;
    }

    public static void removeTransferToPicker(HttpSession session) {
        session.removeAttribute(TRANSFER_TO_PICKER);
    }

    private SessionUtils() {
    }
}
