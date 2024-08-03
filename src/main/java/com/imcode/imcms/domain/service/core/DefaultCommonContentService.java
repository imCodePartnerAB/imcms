package com.imcode.imcms.domain.service.core;

import com.imcode.imcms.api.DocumentVersion;
import com.imcode.imcms.domain.dto.CommonContentDTO;
import com.imcode.imcms.domain.exception.DocumentNotExistException;
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
import static java.util.stream.Collectors.groupingBy;
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
    public Map<Integer, List<CommonContent>> getOrCreateCommonContents(Collection<Integer> docIds){
        final List<LanguageJPA> availableLanguages = languageService.getAvailableLanguages().stream()
                .map(LanguageJPA::new)
                .toList();

        final Map<Integer, List<CommonContent>> docIdCommonContentMap = repository.findByDocIdsAndLangsAndLatestVersion(docIds, availableLanguages).stream()
                .map(CommonContentDTO::new)
                .collect(groupingBy(CommonContent::getDocId));

        // Check the availability of common content for all requested documents
        Set<Integer> missingDocs = new HashSet<>(docIds);
        missingDocs.removeAll(docIdCommonContentMap.keySet());
        missingDocs.forEach(id -> {
            try{
                final Version latestVersion = versionService.getLatestVersion(id);

                List<CommonContent> missingCommonContent = new ArrayList<>();
                availableLanguages.forEach(lang -> {
                    missingCommonContent.add(createByVersion(id, latestVersion.getNo(), lang));
                });

                docIdCommonContentMap.put(id, missingCommonContent);
            }catch (DocumentNotExistException e){
                //just skip non-existent doc
            }
        });

        // Check that each document has common content in all available languages
        docIdCommonContentMap.forEach((key, value) -> {
            availableLanguages.stream()
                    .filter(lang -> value.stream().noneMatch(content -> content.getLanguage().getCode().equals(lang.getCode())))
                    .forEach(missingLang -> {
                        final int versionNo = value.get(0).getVersionNo();
                        value.add(createByVersion(key, versionNo, missingLang));
                    });
        });

        return docIdCommonContentMap;
    }

    @Override
    public CommonContent getOrCreate(int docId, int versionNo, Language language) {
        final Optional<CommonContent> oCommonContent = getCommonContent(docId, versionNo, language);

        return oCommonContent.orElseGet(() -> createByVersion(docId, versionNo, language));
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

        removeDuplicateAlias(toSave);

	    repository.saveAll(toSave);
	    super.updateWorkingVersion(docId);
    }

    private <T extends CommonContent> void removeDuplicateAlias(Collection<T> commonContentCollection){
        final String emptyAlias = "";

        //Check for duplicate aliases in the received data
        final Map<String, List<CommonContent>> duplicates = commonContentCollection.stream()
                .filter(cc -> StringUtils.isNotBlank(cc.getAlias()))
                .collect(Collectors.groupingBy(CommonContent::getAlias));
        duplicates.forEach((key, values) -> {
            if (values.size() > 1) values.forEach(value -> value.setAlias(emptyAlias));
        });

        commonContentCollection.forEach(commonContent -> {
            final String alias = commonContent.getAlias();
            if (StringUtils.isNotBlank(alias)) {
                //Check for duplication of aliases in other documents
                boolean hasDuplicateAliasInAnotherDocs = getByAliasAndMaxWorkingVersion(alias).stream()
                        .anyMatch(c -> !c.getDocId().equals(commonContent.getDocId()));

                if (hasDuplicateAliasInAnotherDocs) {
                    commonContent.setAlias(emptyAlias);
                }
            }
        });
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
        save(version.getDocId(), saveCommonContents);
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
        return repository.findAll().stream()
                .map(CommonContentDTO::new)
                .collect(toList());
    }

    @Override
    public List<CommonContent> getByAlias(String alias) {
        return repository.findByAliasAndLatestAndWorkingVersions(alias).stream()
                .map(CommonContentDTO::new)
                .collect(toList());
    }

    @Override
    public Optional<CommonContent> getPublicByAlias(String alias) {
        return repository.findByAliasAndLatestVersion(alias).map(CommonContentDTO::new);
    }

    @Override
	public Boolean existsByAlias(String alias) {
		return repository.existsByAliasAndLatestAndWorkingVersions(alias);
	}

    @Override
    public Boolean existsPublicByAlias(String alias) {
        return repository.existsByAliasAndLatestVersion(alias);
    }

	@Override
	public Optional<Integer> getDocIdByPublicAlias(String alias) {
		return repository.getDocIdByAliasAndLatestVersion(alias);
	}

	@Override
	public List<String> getAllAliases() {
		return repository.findAllAliasesByLatestAndWorkingVersions();
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

    private List<CommonContent> getByAliasAndMaxWorkingVersion(String alias){
        return repository.findByAliasAndLatestAndWorkingVersions(alias).stream()
                .map(CommonContentDTO::new)
                .collect(toList());
    }

    private Optional<CommonContent> getCommonContent(int docId, int versionNo, Language language) {
        final CommonContentJPA commonContentJPA = repository.findByDocIdAndVersionNoAndLanguage(
                docId, versionNo, new LanguageJPA(language)
        );

        return Optional.ofNullable(commonContentJPA).map(CommonContentDTO::new);
    }

    private CommonContent createByVersion(int docId, int versionNo, Language language){
        if (versionNo != WORKING_VERSION_INDEX) {
            return createFromWorkingVersion(docId, versionNo, language);
        }

        return Value.with(new CommonContentDTO(), commonContentDTO -> {
            commonContentDTO.setEnabled(true);
            commonContentDTO.setLanguage(language);
            commonContentDTO.setDocId(docId);
            commonContentDTO.setVersionNo(versionNo);
        });
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
