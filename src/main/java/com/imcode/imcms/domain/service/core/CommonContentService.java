package com.imcode.imcms.domain.service.core;

import com.imcode.imcms.domain.dto.CommonContentDTO;
import com.imcode.imcms.domain.dto.LanguageDTO;
import com.imcode.imcms.domain.service.api.LanguageService;
import com.imcode.imcms.persistence.entity.CommonContentJPA;
import com.imcode.imcms.persistence.entity.LanguageJPA;
import com.imcode.imcms.persistence.repository.CommonContentRepository;
import com.imcode.imcms.util.Value;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class CommonContentService {

    private static final int WORKING_VERSION_INDEX = 0;

    private final CommonContentRepository commonContentRepository;
    private final Function<CommonContentJPA, CommonContentDTO> commonContentToDTO;
    private final Function<LanguageDTO, LanguageJPA> languageDtoToLanguage;
    private final LanguageService languageService;
    private final Function<CommonContentDTO, CommonContentJPA> commonContentSaver;

    CommonContentService(CommonContentRepository commonContentRepository,
                         Function<CommonContentJPA, CommonContentDTO> commonContentToDTO,
                         Function<CommonContentDTO, CommonContentJPA> commonContentDtoToCommonContent,
                         Function<LanguageDTO, LanguageJPA> languageDtoToLanguage,
                         LanguageService languageService) {

        this.commonContentRepository = commonContentRepository;
        this.commonContentToDTO = commonContentToDTO;
        this.languageDtoToLanguage = languageDtoToLanguage;
        this.languageService = languageService;
        this.commonContentSaver = commonContentDtoToCommonContent.andThen(commonContentRepository::save);
    }

    /**
     * Get document's common contents for all languages
     * If common content of non working version is {@code null} it creates new common content based on working.
     *
     * @param docId     of document
     * @param versionNo version no
     * @return a {@code List} of all common contents
     */
    public List<CommonContentDTO> getOrCreateCommonContents(int docId, int versionNo) {
        return languageService.getAll()
                .stream()
                .map(languageDTO -> getOrCreate(docId, versionNo, languageDTO))
                .collect(Collectors.toList());
    }

    /**
     * Gets common content for working or published versions.
     * If common content of non working version is {@code null} it creates new common content based on working.
     *
     * @param docId       of document
     * @param versionNo   version no
     * @param languageDTO to get language code
     * @return common content of docId, versionNo and user language.
     */
    public CommonContentDTO getOrCreate(int docId, int versionNo, LanguageDTO languageDTO) {
        final LanguageJPA language = languageDtoToLanguage.apply(languageDTO);
        final CommonContentJPA commonContent = commonContentRepository.findByDocIdAndVersionNoAndLanguage(
                docId, versionNo, language
        );

        if (commonContent != null) {
            return commonContentToDTO.apply(commonContent);

        } else if (versionNo == WORKING_VERSION_INDEX) {
            return Value.with(new CommonContentDTO(), commonContentDTO -> {
                commonContentDTO.setEnabled(true);
                commonContentDTO.setLanguage(languageDTO);
                commonContentDTO.setDocId(docId);
                commonContentDTO.setVersionNo(versionNo);
            });
        }

        return createFromWorkingVersion(docId, versionNo, language);
    }

    public void save(Collection<CommonContentDTO> saveUs) {
        saveUs.forEach(this::save);
    }

    public void save(CommonContentDTO saveMe) {
        commonContentSaver.apply(saveMe);
    }

    private CommonContentDTO createFromWorkingVersion(int docId, int versionNo, LanguageJPA language) {
        final CommonContentJPA commonContent = commonContentRepository.findByDocIdAndVersionNoAndLanguage(
                docId, WORKING_VERSION_INDEX, language
        );

        final CommonContentJPA newCommonContent = new CommonContentJPA();
        newCommonContent.setVersionNo(versionNo);
        newCommonContent.setDocId(docId);
        newCommonContent.setEnabled(commonContent.isEnabled());
        newCommonContent.setHeadline(commonContent.getHeadline());
        newCommonContent.setLanguage(commonContent.getLanguage());
        newCommonContent.setMenuImageURL(commonContent.getMenuImageURL());
        newCommonContent.setMenuText(commonContent.getMenuText());

        return commonContentToDTO.apply(commonContentRepository.saveAndFlush(commonContent));
    }

}
