package com.imcode.imcms.servlet;

public class ServletFacade {
    private String forwardReturnUrl;

    public final void setForwardReturnUrl( String forwardReturnUrl ) {
        this.forwardReturnUrl = forwardReturnUrl;
    }

    public final String getForwardReturnUrl() {
        return forwardReturnUrl;
    }
}
