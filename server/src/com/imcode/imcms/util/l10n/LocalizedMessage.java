package com.imcode.imcms.util.l10n;

import imcode.server.Imcms;
import imcode.server.user.UserDomainObject;
import imcode.util.Utility;
import org.apache.commons.lang.NullArgumentException;
import org.apache.commons.lang.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;

public class LocalizedMessage implements Serializable {

    private final String languageKey;
    private final LocalizedMessageProvider provider;

    public LocalizedMessage( String languageKey  ) {
        this(languageKey, null);
    }

    LocalizedMessage(String languageKey, LocalizedMessageProvider provider) {
        if (null == languageKey) {
            throw new NullArgumentException("languageKey");
        }
        this.provider = provider;
        this.languageKey = languageKey;
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
        return StringUtils.defaultString(localProvider.getResourceBundle(languageIso639_2).getString(languageKey));
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
        return !( provider != null ? !provider.equals(that.provider) : that.provider != null );

    }

    public int hashCode() {
        int result = languageKey.hashCode();
        result = 29 * result + ( provider != null ? provider.hashCode() : 0 );
        return result;
    }
}
