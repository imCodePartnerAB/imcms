package com.imcode.imcms.util.l10n;

import imcode.server.user.UserDomainObject;
import imcode.util.Prefs;
import org.apache.commons.collections.iterators.IteratorEnumeration;
import org.apache.commons.lang.UnhandledException;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Properties;
import java.util.ResourceBundle;

public class ImcmsPrefsLocalizedMessageProvider extends LocalizedMessageProvider {

    public ResourceBundle getResourceBundle(String languageIso639_2) {
        String propertiesFilename = "imcms_"+languageIso639_2 + ".properties";
        try {
            final Properties languageProperties = Prefs.getProperties(propertiesFilename);
            return new ResourceBundle() {
                protected Object handleGetObject(String key) {
                    return languageProperties.getProperty(key);
                }

                public Enumeration<String> getKeys() {
                    return new IteratorEnumeration(languageProperties.keySet().iterator());
                }
            };
        } catch ( IOException e ) {
            throw new UnhandledException(e);
        }
    }

    public static Properties getLanguageProperties(UserDomainObject user) {
        String languageIso639_2 = user.getLanguageIso639_2();
        final ResourceBundle resourceBundle = new CachingLocalizedMessageProvider().getResourceBundle(languageIso639_2);
        return new Properties() {
            public String getProperty(String key) {
                return resourceBundle.getString(key);
            }
        };
    }
}
