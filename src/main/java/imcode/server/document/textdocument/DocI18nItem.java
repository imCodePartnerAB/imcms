package imcode.server.document.textdocument;

import com.imcode.imcms.api.I18nLanguage;

public interface DocI18nItem {
    
    I18nLanguage getLanguage();

    void setLanguage(I18nLanguage language);
}
