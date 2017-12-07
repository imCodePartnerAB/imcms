package com.imcode.imcms.domain.service.core;

import com.imcode.imcms.domain.dto.CommonContentDTO;
import com.imcode.imcms.domain.service.LanguageService;
import com.imcode.imcms.model.Language;
import com.imcode.imcms.persistence.entity.CommonContentJPA;
import com.imcode.imcms.persistence.entity.LanguageJPA;
import com.imcode.imcms.persistence.entity.Version;
import com.imcode.imcms.persistence.repository.CommonContentRepository;
import com.imcode.imcms.util.Value;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.imcode.imcms.persistence.entity.Version.WORKING_VERSION_INDEX;

@Service
public class CommonContentService {

    private final CommonContentRepository commonContentRepository;
    private final LanguageService languageService;

    CommonContentService(CommonContentRepository commonContentRepository,
                         LanguageService languageService) {

        this.commonContentRepository = commonContentRepository;
        this.languageService = languageService;
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
                .map(language -> getOrCreate(docId, versionNo, language))
                .collect(Collectors.toList());
    }

    /**
     * Gets common content for working or published versions.
     * If common content of non working version is {@code null} it creates new common content based on working.
     *
     * @param docId       of document
     * @param versionNo   version no
     * @param language to get language code
     * @return common content of docId, versionNo and user language.
     */
    public CommonContentDTO getOrCreate(int docId, int versionNo, Language language) {
        final Optional<CommonContentDTO> oCommonContent = getCommonContent(docId, versionNo, language);

        if (oCommonContent.isPresent()) {
            return oCommonContent.get();

        } else if (versionNo == WORKING_VERSION_INDEX) {
            return Value.with(new CommonContentDTO(), commonContentDTO -> {
                commonContentDTO.setEnabled(true);
                commonContentDTO.setLanguage(language);
                commonContentDTO.setDocId(docId);
                commonContentDTO.setVersionNo(versionNo);
            });
        }

        return createFromWorkingVersion(docId, versionNo, language);
    }

    public List<CommonContentDTO> getCommonContents(int docId, int versionNo) {
        return languageService.getAll()
                .stream()
                .map(languageDTO -> getCommonContent(docId, versionNo, languageDTO))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    private Optional<CommonContentDTO> getCommonContent(int docId, int versionNo, Language language) {
        final CommonContentJPA commonContentJPA = commonContentRepository.findByDocIdAndVersionNoAndLanguage(
                docId, versionNo, new LanguageJPA(language)
        );

        return Optional.ofNullable(commonContentJPA).map(CommonContentDTO::new);
    }

    public void save(Collection<CommonContentDTO> saveUs) {
        saveUs.forEach(this::save);
    }

    public void save(CommonContentDTO saveMe) {
        commonContentRepository.save(new CommonContentJPA(saveMe));
    }

    private CommonContentDTO createFromWorkingVersion(int docId, int versionNo, Language language) {
        final Optional<CommonContentDTO> oCommonContent = getCommonContent(docId, WORKING_VERSION_INDEX, language);
        final CommonContentJPA newCommonContent = oCommonContent.map(CommonContentJPA::new)
                .orElseGet(() -> Value.with(new CommonContentJPA(), commonContentJPA -> {
                    commonContentJPA.setEnabled(true);
                    commonContentJPA.setLanguage(new LanguageJPA(language));
                    commonContentJPA.setDocId(docId);
                    commonContentJPA.setVersionNo(versionNo);
                }));

        newCommonContent.setVersionNo(versionNo);

        return new CommonContentDTO(commonContentRepository.saveAndFlush(newCommonContent));
    }

    /**
     * Creates empty CommonContent for non-existing document and for all
     * languages with {@link Version#WORKING_VERSION_INDEX}.
     * Not saves to DB.
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

    public void deleteByDocId(int docId) {
        commonContentRepository.deleteByDocId(docId);
    }
}
