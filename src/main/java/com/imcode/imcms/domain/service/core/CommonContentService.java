package com.imcode.imcms.domain.service.core;

import com.imcode.imcms.domain.dto.CommonContentDTO;
import com.imcode.imcms.domain.dto.LanguageDTO;
import com.imcode.imcms.domain.service.api.LanguageService;
import com.imcode.imcms.persistence.entity.CommonContent;
import com.imcode.imcms.persistence.entity.Language;
import com.imcode.imcms.persistence.repository.CommonContentRepository;
import imcode.server.LanguageMapper;
import imcode.server.user.UserDomainObject;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class CommonContentService {

    private static final int WORKING_VERSION_INDEX = 0;

    private final CommonContentRepository commonContentRepository;
    private final Function<CommonContent, CommonContentDTO> commonContentToDTO;
    private final Function<LanguageDTO, Language> languageDtoToLanguage;
    private final LanguageService languageService;
    private final Function<CommonContentDTO, CommonContent> commonContentSaver;

    public CommonContentService(CommonContentRepository commonContentRepository,
                                Function<CommonContent, CommonContentDTO> commonContentToDTO,
                                Function<CommonContentDTO, CommonContent> commonContentDtoToCommonContent,
                                Function<LanguageDTO, Language> languageDtoToLanguage,
                                LanguageService languageService) {

        this.commonContentRepository = commonContentRepository;
        this.commonContentToDTO = commonContentToDTO;
        this.languageDtoToLanguage = languageDtoToLanguage;
        this.languageService = languageService;
        this.commonContentSaver = commonContentDtoToCommonContent.andThen(commonContentRepository::save);
    }

    /**
     * Gets common content for working or published versions.
     * If common content of non working version is {@code null} it creates new common content based on working.
     *
     * @param docId     of document
     * @param versionNo version no
     * @param userDO    user to get language
     * @return common content of docId, versionNo and user language.
     *
     * @deprecated use {@link CommonContentService#getOrCreate(int, int, com.imcode.imcms.domain.dto.LanguageDTO)}
     */
    @Deprecated
    public CommonContentDTO getOrCreate(int docId, int versionNo, UserDomainObject userDO) {
        final String code = LanguageMapper.convert639_2to639_1(userDO.getLanguageIso639_2());
        final LanguageDTO languageDTO = languageService.findByCode(code);
        return getOrCreate(docId, versionNo, languageDTO);
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
        final Language language = languageDtoToLanguage.apply(languageDTO);
        final CommonContent commonContent = commonContentRepository
                .findByDocIdAndVersionNoAndLanguage(docId, versionNo, language);

        if (commonContent != null) {
            return commonContentToDTO.apply(commonContent);

        } else if (versionNo == WORKING_VERSION_INDEX) {
            return new CommonContentDTO();
        }

        return createFromWorkingVersion(docId, versionNo, language);
    }

    public void save(Collection<CommonContentDTO> saveUs) {
        saveUs.forEach(this::save);
    }

    public void save(CommonContentDTO saveMe) {
        commonContentSaver.apply(saveMe);
    }

    private CommonContentDTO createFromWorkingVersion(int docId, int versionNo, Language language) {
        final CommonContent commonContent = commonContentRepository
                .findByDocIdAndVersionNoAndLanguage(docId, WORKING_VERSION_INDEX, language);

        final CommonContent newCommonContent = new CommonContent();
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
