package com.imcode.imcms.domain.service.core;

import com.imcode.imcms.api.DocumentVersion;
import com.imcode.imcms.domain.dto.CommonContentDTO;
import com.imcode.imcms.domain.service.AbstractVersionedContentService;
import com.imcode.imcms.domain.service.CommonContentService;
import com.imcode.imcms.domain.service.LanguageService;
import com.imcode.imcms.domain.service.VersionService;
import com.imcode.imcms.model.CommonContent;
import com.imcode.imcms.model.Language;
import com.imcode.imcms.persistence.entity.CommonContentJPA;
import com.imcode.imcms.persistence.entity.DocumentMetadataJPA;
import com.imcode.imcms.persistence.entity.LanguageJPA;
import com.imcode.imcms.persistence.entity.Version;
import com.imcode.imcms.persistence.repository.CommonContentRepository;
import com.imcode.imcms.util.Value;
import imcode.server.Config;
import imcode.server.LanguageMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static com.imcode.imcms.persistence.entity.Version.WORKING_VERSION_INDEX;
import static java.util.stream.Collectors.toList;

@Service
@Transactional
public class DefaultCommonContentService
        extends AbstractVersionedContentService<CommonContentJPA, CommonContentRepository>
        implements CommonContentService {

    private final VersionService versionService;
    private final LanguageService languageService;
    private final Config config;

    DefaultCommonContentService(CommonContentRepository commonContentRepository,
                                VersionService versionService,
                                LanguageService languageService,
                                Config config) {

        super(commonContentRepository);
        this.versionService = versionService;
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

        return languageService.getAvailableLanguages()
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
	public Optional<CommonContent> getByAlias(String alias) {
		return repository.findFirstByAlias(alias).map(CommonContentDTO::new);
	}

	@Override
    public <T extends CommonContent> void save(int docId, Collection<T> saveUs) {
        final Set<CommonContentJPA> toSave = saveUs.stream().map(commonContent -> {
	        final String headline = commonContent.getHeadline();
	        final String alias = commonContent.getAlias();

	        if (StringUtils.isNotBlank(headline)) commonContent.setHeadline(headline.trim());
	        if (StringUtils.isNotBlank(alias)) commonContent.setAlias(alias.trim());

	        return new CommonContentJPA(commonContent);
        }).collect(Collectors.toSet());

	    repository.saveAll(toSave);
	    super.updateWorkingVersion(docId);
    }

    @Override
    public void setAsWorkingVersion(Version version) {
        final List<CommonContentJPA> commonContentsByVersion = repository.findByVersion(version);

        final List<CommonContentJPA> saveCommonContents = new ArrayList<>();
        commonContentsByVersion.forEach(commonContentByVersion -> {
            List<DocumentMetadataJPA> metadataListCopy = commonContentByVersion.getDocumentMetadataList().stream()
                    .map(DocumentMetadataJPA::new)
                    .collect(toList());

            CommonContentJPA commonContentCopy = new CommonContentJPA(commonContentByVersion);
            commonContentCopy.setId(null);
            commonContentCopy.setVersionNo(DocumentVersion.WORKING_VERSION_NO);
            commonContentCopy.setDocumentMetadataList(metadataListCopy);
            saveCommonContents.add(commonContentCopy);
        });


        repository.deleteByVersion(versionService.getDocumentWorkingVersion(version.getDocId()));
        repository.flush();
        repository.saveAll(saveCommonContents);
    }

    @Override
    public Set<CommonContent> getByVersion(Version version) {
        return repository.findByVersion(version)
                .stream()
                .map(CommonContentDTO::new)
                .collect(Collectors.toSet());
    }

	@Override
	public Optional<CommonContentDTO> getByVersionAndLanguage(Version version, Language language) {
		return Optional.ofNullable(repository.findByVersionAndLanguage(version, new LanguageJPA(language)))
				.map(CommonContentDTO::new);
	}

	@Override
    public List<CommonContent> getAll() {
        return repository.findAll()
                .stream()
                .map(CommonContentDTO::new)
                .collect(toList());
    }

	@Override
	public Boolean existsByAlias(String alias) {
		return repository.existsByAlias(alias);
	}

	@Override
	public Integer getDocIdByAlias(String alias) {
		return repository.findDocIdByAlias(alias);
	}

	@Override
	public List<String> getAllAliases() {
		return repository.findAllAliases();
	}

	@Override
	public void removeAlias(String alias) {
		repository.removeAlias(alias);
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
