package com.imcode.imcms.util.l10n;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class CachingLocalizedMessageProvider extends LocalizedMessageProvider {

    private final LocalizedMessageProvider provider ;
    private final Map<String, Properties> languagePropertiesMap = new HashMap();

    public CachingLocalizedMessageProvider(LocalizedMessageProvider provider) {
        this.provider = provider;
    }

    public CachingLocalizedMessageProvider() {
        this(new ImcmsPrefsLocalizedMessageProvider());
    }

    public Properties getLanguageProperties(String languageIso639_2) {
        Properties languageProperties = languagePropertiesMap.get(languageIso639_2);
        if ( null == languageProperties ) {
            languageProperties = provider.getLanguageProperties(languageIso639_2);
            languagePropertiesMap.put(languageIso639_2, languageProperties);
        }
        return languageProperties;
    }

}
