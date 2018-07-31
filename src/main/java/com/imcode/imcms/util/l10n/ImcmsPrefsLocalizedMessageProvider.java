package com.imcode.imcms.util.l10n;

import imcode.server.LanguageMapper;
import imcode.server.user.UserDomainObject;
import imcode.util.PropertyManager;
import org.apache.commons.collections4.iterators.IteratorEnumeration;

import java.util.Enumeration;
import java.util.Properties;
import java.util.ResourceBundle;

public class ImcmsPrefsLocalizedMessageProvider extends LocalizedMessageProvider {

    public static Properties getLanguageProperties(UserDomainObject user) {
        final String languageIso639_2 = user.getLanguageIso639_2();
        final ResourceBundle resourceBundle = new CachingLocalizedMessageProvider().getResourceBundle(languageIso639_2);
        return new Properties() {
            public String getProperty(String key) {
                return resourceBundle.getString(key);
            }
        };
    }

    public ResourceBundle getResourceBundle(String languageIso639_2) {
        final String propertiesFilename = "WEB-INF/conf/imcms_" + LanguageMapper.convert639_2to639_1(languageIso639_2)
                + ".properties";

        final Properties languageProperties;
        Properties properties = null;

        try {
            properties = PropertyManager.getPropertiesFrom(propertiesFilename);
        } catch (NullPointerException e) { // props for specified lang not found, using default
            properties = PropertyManager.getPropertiesFrom("WEB-INF/conf/imcms.properties");
        } finally {
            languageProperties = properties;
        }

        return new ResourceBundle() {
            protected Object handleGetObject(String key) {
                return languageProperties.getProperty(key);
            }

            public Enumeration<String> getKeys() {
                return new IteratorEnumeration(languageProperties.keySet().iterator());
            }
        };
    }
}
