package com.imcode.imcms.domain.dto;

import com.imcode.imcms.model.CommonContent;
import com.imcode.imcms.model.Language;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommonContentDTO extends CommonContent {

    private Integer id;
    private Integer docId;
    private LanguageDTO language;
    private String headline;
    private String menuText;
    private String menuImageURL;
    private boolean isEnabled;
    private Integer versionNo;

    public CommonContentDTO(CommonContent from) {
        super(from);
    }

    @Override
    public void setLanguage(Language language) {
        this.language = (language == null) ? null : new LanguageDTO(language);
    }

}
