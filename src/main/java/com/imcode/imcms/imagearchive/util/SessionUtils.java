package com.imcode.imcms.imagearchive.util;

import com.imcode.imcms.imagearchive.SessionConstants;

import javax.servlet.http.HttpSession;

public class SessionUtils {
    /** Transfer selected image to Vaadin image handling window. */
    public static final String TRANSFER_TO_PICKER = "transferToPicker";

    private SessionUtils() {
    }

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
}
