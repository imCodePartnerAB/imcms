package imcode.util;

import imcode.server.user.UserDomainObject;
import imcode.server.ApplicationServer;
import imcode.util.Utility;

import javax.servlet.http.HttpServletRequest;

public class LocalizedMessage {

    private String languageKey;

    public LocalizedMessage( String languageKey ) {
        this.languageKey = languageKey;
    }

    public String toLocalizedString(HttpServletRequest request) {
        UserDomainObject user = Utility.getLoggedOnUser( request );
        return ApplicationServer.getIMCServiceInterface().getLanguageProperties( user ).getProperty( languageKey ) ;
    }
}
