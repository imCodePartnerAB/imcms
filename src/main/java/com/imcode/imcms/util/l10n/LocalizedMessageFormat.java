package com.imcode.imcms.util.l10n;

import java.text.MessageFormat;

public class LocalizedMessageFormat extends LocalizedMessage {

    private Object[] arguments;

    public LocalizedMessageFormat(String messageKey, Object... arguments) {
        super(messageKey);
        this.arguments = arguments;
    }

    public String toLocalizedString(String languageIso639_2) {
        return new MessageFormat(super.toLocalizedStringByIso639_2(languageIso639_2)).format(arguments);
    }

	public LocalizedMessageFormat setArguments(Object[] arguments) {
		this.arguments = arguments;
		return this;
	}
}
