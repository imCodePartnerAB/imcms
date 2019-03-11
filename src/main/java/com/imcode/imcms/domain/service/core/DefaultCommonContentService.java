package com.imcode.imcms.domain.service.core;

import com.imcode.imcms.domain.dto.CommonContentDTO;
import com.imcode.imcms.domain.service.AbstractVersionedContentService;
import com.imcode.imcms.domain.service.CommonContentService;
import com.imcode.imcms.domain.service.LanguageService;
import com.imcode.imcms.model.CommonContent;
import com.imcode.imcms.model.Language;
import com.imcode.imcms.persistence.entity.CommonContentJPA;
import com.imcode.imcms.persistence.entity.LanguageJPA;
import com.imcode.imcms.persistence.entity.Version;
import com.imcode.imcms.persistence.repository.CommonContentRepository;
import com.imcode.imcms.util.Value;
import imcode.server.Config;
import imcode.server.LanguageMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.imcode.imcms.persistence.entity.Version.WORKING_VERSION_INDEX;
import static java.util.stream.Collectors.toList;

@Service
@Transactional
public class DefaultCommonContentService
        extends AbstractVersionedContentService<CommonContentJPA, CommonContentRepository>
        implements CommonContentService {

    private final LanguageService languageService;
    private final Config config;

    DefaultCommonContentService(CommonContentRepository commonContentRepository,
                                LanguageService languageService,
                                Config config) {

        super(commonContentRepository);
        this.languageService = languageService;
        this.config = config;
    }

    @Override
    public List<CommonContent> getOrCreateCommonContents(int docId, int versionNo) {
        Comparator<Language> languageComparator = (lang1, lang2) -> {
            final String mappedLanguage = LanguageMapper.convert639_2to639_1(config.getDefaultLanguage());
            if (lang1.getCode().equals(mappedLanguage)) {
                return -1;
            }
            if (lang2.getCode().equals(mappedLanguage)) {
                return 1;
            }
            return lang1.getCode().compareTo(lang2.getCode());
        };

        return languageService.getAll()
                .stream()
                .sorted(languageComparator)
                .map(language -> getOrCreate(docId, versionNo, language))
                .collect(toList());
    }

    @Override
    public CommonContent getOrCreate(int docId, int versionNo, Language language) {
        final Optional<CommonContent> oCommonContent = getCommonContent(docId, versionNo, language);

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

    @Override
    public <T extends CommonContent> void save(int docId, Collection<T> saveUs) {
        final Set<CommonContentJPA> toSave = saveUs.stream().map(CommonContentJPA::new).collect(Collectors.toSet());
        repository.save(toSave);
        super.updateWorkingVersion(docId);
    }

    @Override
    public Set<CommonContent> getByVersion(Version version) {
        return repository.findByVersion(version)
                .stream()
                .map(CommonContentDTO::new)
                .collect(Collectors.toSet());
    }

    @Override
    public void deleteByDocId(Integer docId) {
        repository.deleteByDocId(docId);
    }

    @Override
    protected CommonContentJPA removeId(CommonContentJPA dto, Version version) {
        final CommonContentJPA newCommonContent = new CommonContentJPA(dto);
        newCommonContent.setId(null);
        newCommonContent.setVersionNo(version.getNo());

        return newCommonContent;
    }

    private Optional<CommonContent> getCommonContent(int docId, int versionNo, Language language) {
        final CommonContentJPA commonContentJPA = repository.findByDocIdAndVersionNoAndLanguage(
                docId, versionNo, new LanguageJPA(language)
        );

        return Optional.ofNullable(commonContentJPA).map(CommonContentDTO::new);
    }

    private CommonContent createFromWorkingVersion(int docId, int versionNo, Language language) {
        final Optional<CommonContent> oCommonContent = getCommonContent(docId, WORKING_VERSION_INDEX, language);
        final CommonContentJPA newCommonContent = oCommonContent.map(CommonContentJPA::new)
                .orElseGet(() -> Value.with(new CommonContentJPA(), commonContentJPA -> {
                    commonContentJPA.setEnabled(true);
                    commonContentJPA.setLanguage(language);
                    commonContentJPA.setDocId(docId);
                    commonContentJPA.setVersionNo(versionNo);
                }));

        newCommonContent.setId(null);
        newCommonContent.setVersionNo(versionNo);

        return new CommonContentDTO(repository.saveAndFlush(newCommonContent));
    }
}
