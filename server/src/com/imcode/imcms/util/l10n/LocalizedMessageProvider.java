package com.imcode.imcms.util.l10n;

import java.util.ResourceBundle;

public abstract class LocalizedMessageProvider {

    public LocalizedMessage get(String key) {
        return new LocalizedMessage(key, this);
    }

    public abstract ResourceBundle getResourceBundle(String languageIso639_2);
}