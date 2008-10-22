package com.imcode.imcms.util.l10n;

import java.text.MessageFormat;

public class LocalizedMessageFormat extends LocalizedMessage {

    private Object[] arguments;

    public LocalizedMessageFormat(String messageKey, Object... arguments) {
        super(messageKey);
        this.arguments = arguments;
    }

    public String toLocalizedString(String languageIso639_2) {
        return new MessageFormat(super.toLocalizedString(languageIso639_2)).format(arguments) ;

    }
}
