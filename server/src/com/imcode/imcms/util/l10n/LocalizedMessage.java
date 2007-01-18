package com.imcode.imcms.util.l10n;

import imcode.server.Imcms;
import imcode.server.user.UserDomainObject;
import imcode.util.Utility;
import org.apache.commons.lang.NullArgumentException;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.text.MessageFormat;

public class LocalizedMessage implements Serializable {

    private final String languageKey;
    private final LocalizedMessageProvider provider;
    private final Object[] arguments;

    public LocalizedMessage( String languageKey, Object... arguments ) {
        this(languageKey, null, arguments);
    }

    LocalizedMessage(String languageKey, LocalizedMessageProvider provider, Object... arguments) {
        if (null == languageKey) {
            throw new NullArgumentException("languageKey");
        }
        this.provider = provider;
        this.languageKey = languageKey;
        this.arguments = arguments;
    }

    public final String toLocalizedString(HttpServletRequest request) {
        UserDomainObject user = Utility.getLoggedOnUser( request );
        return toLocalizedString( user );
    }

    public final String toLocalizedString( UserDomainObject user ) {
        return toLocalizedString(user.getLanguageIso639_2());
    }

    public String toLocalizedString(String languageIso639_2) {
        LocalizedMessageProvider localProvider = provider ;
        if (null == localProvider) {
            localProvider = Imcms.getServices().getLocalizedMessageProvider();
        }
        String pattern = localProvider.getLanguageProperties(languageIso639_2).getProperty(languageKey);
        return new MessageFormat(pattern).format(arguments) ;
    }

    public boolean equals(Object o) {
        if ( this == o ) {
            return true;
        }
        if ( o == null || getClass() != o.getClass() ) {
            return false;
        }

        final LocalizedMessage that = (LocalizedMessage) o;

        if ( !languageKey.equals(that.languageKey) ) {
            return false;
        }
        if ( provider != null ? !provider.equals(that.provider) : that.provider != null ) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int result = languageKey.hashCode();
        result = 29 * result + ( provider != null ? provider.hashCode() : 0 );
        return result;
    }
}
