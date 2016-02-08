package com.imcode.imcms.util.l10n;

import imcode.server.user.UserDomainObject;
import imcode.util.PropertyManager;
import org.apache.commons.collections.iterators.IteratorEnumeration;

import java.util.Enumeration;
import java.util.Properties;
import java.util.ResourceBundle;

public class ImcmsPrefsLocalizedMessageProvider extends LocalizedMessageProvider {

    public static Properties getLanguageProperties(UserDomainObject user) {
        String languageIso639_2 = user.getLanguageIso639_2();
        final ResourceBundle resourceBundle = new CachingLocalizedMessageProvider().getResourceBundle(languageIso639_2);
        return new Properties() {
            public String getProperty(String key) {
                return resourceBundle.getString(key);
            }
        };
    }

    public ResourceBundle getResourceBundle(String languageIso639_2) {
        String propertiesFilename = "WEB-INF/conf/imcms_" + languageIso639_2 + ".properties";
		final Properties languageProperties = PropertyManager.getPropertiesFrom(propertiesFilename);
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
