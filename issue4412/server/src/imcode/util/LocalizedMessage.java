package imcode.util;

import imcode.server.user.UserDomainObject;
import imcode.server.Imcms;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;

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
        return Imcms.getServices().getLanguageProperties( user ).getProperty( languageKey ) ;
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
