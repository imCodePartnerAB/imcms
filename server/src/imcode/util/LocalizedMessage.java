package imcode.util;

import imcode.server.user.UserDomainObject;
import imcode.server.Imcms;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;

public class LocalizedMessage implements Serializable {

    private String languageKey;

    public LocalizedMessage( String languageKey ) {
        this.languageKey = languageKey;
    }

    public String toLocalizedString(HttpServletRequest request) {
        UserDomainObject user = Utility.getLoggedOnUser( request );
        return toLocalizedString( user );
    }

    public String toLocalizedString( UserDomainObject user ) {
        return Imcms.getServices().getLanguageProperties( user ).getProperty( languageKey ) ;
    }
}
