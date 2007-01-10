package com.imcode.imcms.util.l10n;

import java.util.Properties;

public abstract class LocalizedMessageProvider {

    public LocalizedMessage get(String key) {
        return new LocalizedMessage(key, this);
    }

    protected abstract Properties getLanguageProperties(String languageIso639_2);
}