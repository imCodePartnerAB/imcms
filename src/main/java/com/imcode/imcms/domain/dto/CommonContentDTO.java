package com.imcode.imcms.domain.dto;

import com.imcode.imcms.model.CommonContent;
import com.imcode.imcms.model.Language;
import com.imcode.imcms.persistence.entity.Version;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class CommonContentDTO extends CommonContent implements Cloneable {

    private static final long serialVersionUID = -8745654157487154505L;

    private Integer id;
    private Integer docId;
    private LanguageDTO language;
    private String headline;
    private String menuText;
    private boolean isEnabled;
    private Integer versionNo;

    public CommonContentDTO(CommonContent from) {
        super(from);
    }

    @Override
    public void setLanguage(Language language) {
        this.language = (language == null) ? null : new LanguageDTO(language);
    }

    @Override
    public CommonContentDTO clone() {
        try {
            final CommonContentDTO clonedCommonContentDTO = (CommonContentDTO) super.clone();
            clonedCommonContentDTO.setId(null);
            clonedCommonContentDTO.setDocId(null);
            clonedCommonContentDTO.setVersionNo(Version.WORKING_VERSION_INDEX);

            return clonedCommonContentDTO;

        } catch (CloneNotSupportedException e) {
            return null;
        }
    }
}
