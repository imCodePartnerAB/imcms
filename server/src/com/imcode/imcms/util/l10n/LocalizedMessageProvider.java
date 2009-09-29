package com.imcode.imcms.util.l10n;

import java.util.ResourceBundle;

public abstract class LocalizedMessageProvider {

    public boolean supportsLanguage(String languageIso639_2) {
        try {
            getResourceBundle(languageIso639_2);
            return true;
        }
        catch (Exception ex) {
           return false; 
        }
    }

    public LocalizedMessage get(String key) {
        return new LocalizedMessage(key, this);
    }

    public abstract ResourceBundle getResourceBundle(String languageIso639_2);
}