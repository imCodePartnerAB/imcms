package com.imcode.imcms.domain.factory;

import com.imcode.imcms.domain.dto.CommonContentDTO;
import com.imcode.imcms.domain.service.LanguageService;
import com.imcode.imcms.model.Language;
import com.imcode.imcms.persistence.entity.Version;
import com.imcode.imcms.util.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

import static com.imcode.imcms.persistence.entity.Version.WORKING_VERSION_INDEX;

/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 14.02.18.
 */
@Component
public class CommonContentFactory {

    private final LanguageService languageService;

    CommonContentFactory(LanguageService languageService) {
        this.languageService = languageService;
    }

    /**
     * Creates empty CommonContent for non-existing document and for all
     * languages with {@link Version#WORKING_VERSION_INDEX}.
     */
    public List<CommonContentDTO> createCommonContents() {
        return languageService.getAll()
                .stream()
                .map(this::createFrom)
                .collect(Collectors.toList());
    }

    private CommonContentDTO createFrom(Language languageDTO) {
        return Value.with(new CommonContentDTO(), commonContentDTO -> {
            commonContentDTO.setEnabled(true);
            commonContentDTO.setLanguage(languageDTO);
            commonContentDTO.setVersionNo(WORKING_VERSION_INDEX);
        });
    }

}
