package imcode.util;

import imcode.server.user.UserDomainObject;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.io.IOException;
import java.util.Properties;
import java.util.Map;
import java.util.HashMap;

import org.apache.commons.lang.NullArgumentException;
import org.apache.commons.lang.UnhandledException;

public class LocalizedMessage implements Serializable {

    private final static Map LANGUAGE_PROPERTIES_MAP = new HashMap();
    
    private final String languageKey;

    public LocalizedMessage( String languageKey ) {
        if (null == languageKey) {
            throw new NullArgumentException("languageKey");
        }
        this.languageKey = languageKey;
    }

    public String toLocalizedString(HttpServletRequest request) {
        UserDomainObject user = Utility.getLoggedOnUser( request );
        return toLocalizedString( user );
    }

    public String toLocalizedString( UserDomainObject user ) {
        return toLocalizedString(user.getLanguageIso639_2());
    }

    public String toLocalizedString(String languageIso639_2) {
        return getLanguageProperties(languageIso639_2).getProperty( languageKey ) ;
    }

    public boolean equals(Object o) {
        if ( this == o ) {
            return true;
        }
        if ( o == null || getClass() != o.getClass() ) {
            return false;
        }

        return languageKey.equals(( (LocalizedMessage) o ).languageKey);

    }

    public int hashCode() {
        return languageKey.hashCode() ;
    }

    public static Properties getLanguageProperties(String languageIso639_2) {
        Properties languageProperties = (Properties) LANGUAGE_PROPERTIES_MAP.get(languageIso639_2);
        if ( null == languageProperties ) {
            String propertiesFilename = languageIso639_2 + ".properties";
            try {
                languageProperties = Prefs.getProperties(propertiesFilename);
                LANGUAGE_PROPERTIES_MAP.put(languageIso639_2, languageProperties);
            } catch ( IOException e ) {
                throw new UnhandledException(e);
            }
        }
        return languageProperties;
    }

    public static Properties getLanguageProperties(UserDomainObject user) {
        String languageIso639_2 = user.getLanguageIso639_2();
        return getLanguageProperties(languageIso639_2);
    }
}
