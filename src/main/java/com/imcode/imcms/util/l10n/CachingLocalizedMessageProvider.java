package com.imcode.imcms.util.l10n;

import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class CachingLocalizedMessageProvider extends LocalizedMessageProvider {

    private final LocalizedMessageProvider provider ;
    private final Map<String, ResourceBundle> bundleMap = new HashMap();

    public CachingLocalizedMessageProvider(LocalizedMessageProvider provider) {
        this.provider = provider;
    }

    public CachingLocalizedMessageProvider() {
        this(new ImcmsPrefsLocalizedMessageProvider());
    }

    public ResourceBundle getResourceBundle(String languageIso639_2) {
        ResourceBundle resourceBundle = bundleMap.get(languageIso639_2);
        if ( null == resourceBundle ) {
            resourceBundle = provider.getResourceBundle(languageIso639_2);
            bundleMap.put(languageIso639_2, resourceBundle);
        }
        return resourceBundle ;
    }

}
