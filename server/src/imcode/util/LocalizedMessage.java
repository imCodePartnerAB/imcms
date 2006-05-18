package imcode.util;

import imcode.server.user.UserDomainObject;
import imcode.server.Imcms;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.Properties;

import org.apache.commons.lang.NullArgumentException;

public class LocalizedMessage implements Serializable {

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

    private Properties getLanguageProperties(String languageIso639_2) {
        return Imcms.getServices().getLanguageProperties( languageIso639_2 );
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
}
