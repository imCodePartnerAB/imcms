package com.imcode.imcms.domain.dto;

import com.imcode.imcms.persistence.entity.CommonContent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommonContentDTO extends CommonContent<LanguageDTO> {

    private Integer id;
    private Integer docId;
    private LanguageDTO language;
    private String headline;
    private String menuText;
    private String menuImageURL;
    private boolean isEnabled;
    private Integer versionNo;

    public CommonContentDTO(CommonContent from, LanguageDTO language) {
        super(from, language);
    }
}
