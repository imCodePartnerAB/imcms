package com.imcode.imcms.util.l10n;

import imcode.server.user.UserDomainObject;
import imcode.util.Prefs;

import java.io.IOException;
import java.util.Properties;

import org.apache.commons.lang.UnhandledException;
import com.imcode.imcms.util.l10n.CachingLocalizedMessageProvider;
import com.imcode.imcms.util.l10n.LocalizedMessageProvider;

public class ImcmsPrefsLocalizedMessageProvider extends LocalizedMessageProvider {

    public Properties getLanguageProperties(String languageIso639_2) {
        String propertiesFilename = languageIso639_2 + ".properties";
        try {
            return Prefs.getProperties(propertiesFilename);
        } catch ( IOException e ) {
            throw new UnhandledException(e);
        }
    }

    public static Properties getLanguageProperties(UserDomainObject user) {
        String languageIso639_2 = user.getLanguageIso639_2();
        return new CachingLocalizedMessageProvider().getLanguageProperties(languageIso639_2);
    }
}
