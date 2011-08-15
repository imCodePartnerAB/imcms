package com.imcode.imcms.addon.imagearchive.json;

import java.util.List;


public class UploadResponse {
    private List<String> errors;
    private String redirect;
    private String redirectOnAllComplete;

    public String getRedirect() {
        return redirect;
    }

    public void setRedirect(String redirect) {
        this.redirect = redirect;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

    public String getRedirectOnAllComplete() {
        return redirectOnAllComplete;
    }

    public void setRedirectOnAllComplete(String redirectOnAllComplete) {
        this.redirectOnAllComplete = redirectOnAllComplete;
    }
}
