package com.imcode.imcms.util.l10n;

import imcode.server.Imcms;
import imcode.server.LanguageMapper;
import imcode.server.user.UserDomainObject;
import imcode.util.Utility;
import org.apache.commons.lang.NullArgumentException;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;

public class LocalizedMessage implements Serializable {

    private final String languageKey;
    private final LocalizedMessageProvider provider;

    public LocalizedMessage(String languageKey) {
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
        UserDomainObject user = Utility.getLoggedOnUser(request);
        return toLocalizedString(user);
    }

    public final String toLocalizedString(UserDomainObject user) {
        return toLocalizedStringByIso639_2(user.getLanguageIso639_2());
    }

    public String toLocalizedStringByIso639_2(String languageIso639_2) {
        LocalizedMessageProvider localProvider = provider;
        if (null == localProvider) {
            localProvider = Imcms.getServices().getLocalizedMessageProvider();
        }
        return StringUtils.defaultString(localProvider.getResourceBundle(languageIso639_2).getString(languageKey));
    }

	public String toLocalizedStringByIso639_1(String languageIso639_1) {
		String languageIso639_2 = LanguageMapper.convert639_1to639_2(languageIso639_1);
		return toLocalizedStringByIso639_2(languageIso639_2);
	}

    public String getLanguageKey() {
        return languageKey;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final LocalizedMessage that = (LocalizedMessage) o;

        if (!languageKey.equals(that.languageKey)) {
            return false;
        }
        return !(provider != null ? !provider.equals(that.provider) : that.provider != null);

    }

    public int hashCode() {
        int result = languageKey.hashCode();
        result = 29 * result + (provider != null ? provider.hashCode() : 0);
        return result;
    }
}
