package imcode.util;

import imcode.server.user.UserDomainObject;
import imcode.server.Imcms;
import imcode.util.Utility;

import javax.servlet.http.HttpServletRequest;

public class LocalizedMessage {

    private String languageKey;

    public LocalizedMessage( String languageKey ) {
        this.languageKey = languageKey;
    }

    public String toLocalizedString(HttpServletRequest request) {
        UserDomainObject user = Utility.getLoggedOnUser( request );
        return Imcms.getServices().getLanguageProperties( user ).getProperty( languageKey ) ;
    }
}
