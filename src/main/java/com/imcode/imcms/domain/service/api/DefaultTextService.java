package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.domain.component.TextContentFilter;
import com.imcode.imcms.domain.dto.TextDTO;
import com.imcode.imcms.domain.service.*;
import com.imcode.imcms.enums.SaveMode;
import com.imcode.imcms.model.Language;
import com.imcode.imcms.model.LoopEntryRef;
import com.imcode.imcms.model.Text;
import com.imcode.imcms.persistence.entity.*;
import com.imcode.imcms.persistence.repository.TextRepository;
import com.imcode.imcms.persistence.repository.UserRepository;
import imcode.server.Imcms;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static com.imcode.imcms.model.Text.HtmlFilteringPolicy.RELAXED;
import static com.imcode.imcms.model.Text.HtmlFilteringPolicy.RESTRICTED;
import static com.imcode.imcms.model.Text.Type.EDITOR;
import static com.imcode.imcms.model.Text.Type.HTML;

@Service("textService")
@Transactional
class DefaultTextService extends AbstractVersionedContentService<TextJPA, TextRepository> implements TextService {

    private final static Logger LOGGER = LogManager.getLogger(DefaultTextService.class);

    private final LanguageService languageService;
    private final VersionService versionService;
    private final TextHistoryService textHistoryService;
    private final TextContentFilter textContentFilter;
    private final LoopService loopService;
    private final UserRepository userRepository;

    DefaultTextService(TextRepository textRepository,
                       LanguageService languageService,
                       VersionService versionService,
                       TextHistoryService textHistoryService,
                       TextContentFilter textContentFilter,
                       LoopService loopService,
                       UserRepository userRepository) {

        super(textRepository);

        this.languageService = languageService;
        this.versionService = versionService;
        this.textHistoryService = textHistoryService;
        this.textContentFilter = textContentFilter;
        this.loopService = loopService;
        this.userRepository = userRepository;
    }

    @Override
    public List<TextJPA> getByDocId(Integer docId) {
        boolean isNewVersion = versionService.hasNewerVersion(docId);

        final Version version = isNewVersion
                ? versionService.getDocumentWorkingVersion(docId)
                : versionService.getLatestVersion(docId);

        return repository.findByVersion(version);
    }

    @Override
    public Text getText(Text textRequestData) {
        return getText(
                textRequestData.getDocId(),
                textRequestData.getIndex(),
                textRequestData.getLangCode(),
                textRequestData.getLoopEntryRef()
        );
    }

    @Override
    public Text getText(int docId, int index, String langCode, LoopEntryRef loopEntryRef) {
        return getText(docId, index, langCode, loopEntryRef, versionService.getDocumentWorkingVersion(docId));
    }

    @Override
    public Text getText(int docId, int index, int versionNo, String langCode, LoopEntryRef loopEntryRef){
        return getText(docId, index, langCode, loopEntryRef, versionService.findByDocIdAndNo(docId, versionNo));
    }

    @Override
    public List<TextJPA> getText(Integer index, String key) {
        return repository.findByIndexAndText(index, key);
    }

    @Override
    public Text getPublicText(int docId, int index, String langCode, LoopEntryRef loopEntryRef) {
        return getText(docId, index, langCode, loopEntryRef, versionService.getLatestVersion(docId));
    }

    @Override
    public Text getLikePublishedText(int docId, int index, String langCode, LoopEntryRef loopEntryRef) {
        final LanguageJPA language = new LanguageJPA(languageService.findByCode(langCode));
        final Version latestVersion = versionService.getLatestVersion(docId);

        return getLikePublishedText(latestVersion, index, language, loopEntryRef);
    }

    @Override
    public Text save(Text text) {
        final Integer docId = text.getDocId();
        final Version workingVersion = versionService.getDocumentWorkingVersion(docId);
        final Version latestVersion = versionService.getLatestVersion(docId);
        final LanguageJPA language = new LanguageJPA(languageService.findByCode(text.getLangCode()));

        final TextJPA textJPA = getText(text.getIndex(), workingVersion, language, text.getLoopEntryRef());

        final String textContent = text.getText();
        final Text.Type type = text.getType();
        final Text.HtmlFilteringPolicy filteringPolicy = text.getHtmlFilteringPolicy();

        if ((textJPA != null)
                && Objects.equals(textJPA.getText(), textContent)
                && Objects.equals(textJPA.getType(), type)
                && Objects.equals(textJPA.getHtmlFilteringPolicy(), filteringPolicy)
        ) return textJPA;

        if ((EDITOR.equals(type) || HTML.equals(type))
                && (RESTRICTED.equals(filteringPolicy) || RELAXED.equals(filteringPolicy)))
        {
            text.setText(textContentFilter.cleanText(textContent, filteringPolicy));
        }

        final TextJPA newTextJPA = new TextJPA(text, workingVersion, language);
        newTextJPA.setId((textJPA == null) ? null : textJPA.getId());

        final Text savedText = repository.save(newTextJPA);

        if (text.isLikePublished()) {
            final TextJPA latestVersionTextJPA = getText(text.getIndex(), latestVersion, language, text.getLoopEntryRef());
            final TextJPA newLatestTextJPA = new TextJPA(text, latestVersion, language);
            newLatestTextJPA.setId((latestVersionTextJPA == null) ? null : latestVersionTextJPA.getId());

            repository.save(newLatestTextJPA);
        }

        super.updateWorkingVersion(docId);
        if(Imcms.isVersioningAllowed()){
            super.updateVersionInIndex(docId);
        }else{
            Imcms.getServices().getDocumentMapper().invalidateDocument(docId);
        }

        textHistoryService.save(text);

        return savedText;
    }

    @Override
    public void setAsWorkingVersion(Version version) {
        final Version workingVersion = versionService.getDocumentWorkingVersion(version.getDocId());

        final List<TextJPA> textsByVersion = repository.findByVersion(version);

        final List<TextJPA> saveTexts = new ArrayList<>();
        textsByVersion.forEach(textByVersion -> {
            TextJPA textCopy = new TextJPA(textByVersion, workingVersion);
            textCopy.setId(null);
            saveTexts.add(textCopy);
        });

        repository.deleteByVersion(workingVersion);
        repository.flush();
        repository.saveAll(saveTexts);

        textsByVersion.forEach(textHistoryService::save);
    }

    @Override
    public Text updateTextInCurrentVersion(TextJPA text, SaveMode saveMode) {
        final Version version = text.getVersion();
        final LanguageJPA language = text.getLanguage();
        final Integer index = text.getIndex();
        final User currentUser = userRepository.getOne(Imcms.getUser().getId());

        if (saveMode == SaveMode.UPDATE) {

            final LoopEntryRefJPA loopEntryRef = text.getLoopEntryRef();
            final Integer id = (loopEntryRef == null)
                    ? repository.findIdByVersionAndLanguageAndIndexWhereLoopEntryRefIsNull(version, language, index)
                    : repository.findIdByVersionAndLanguageAndIndexAndLoopEntryRef(version, language, index, loopEntryRef);

            text.setId(id);
        }

        loopService.createLoopEntryIfNotExists(version, text.getLoopEntryRef());

        final TextJPA savedText = repository.save(text);

        textHistoryService.save(new TextHistoryJPA(text, language, currentUser));

        return savedText;
    }


    @Override
    public void deleteByDocId(Integer docIdToDelete) {
        repository.deleteByDocId(docIdToDelete);
    }

    @Override
    public Set<Text> getPublicTexts(int docId, Language language) {

        final Version latestVersion = versionService.getLatestVersion(docId);
        final LanguageJPA languageJPA = new LanguageJPA(language);

        final Set<TextJPA> likePublishedTexts = repository.findByDocIdAndLanguageAndLikePublishedIsTrue(docId, languageJPA);

        final Set<TextJPA> versionPublishedTexts = repository.findByVersionAndLanguage(latestVersion, languageJPA);

        return getUnionPublicTexts(likePublishedTexts, versionPublishedTexts);
    }


    private Set<Text> getUnionPublicTexts(Set<TextJPA> likePublishedTexts, Set<TextJPA> versionPublishedTexts) {
        likePublishedTexts.addAll(versionPublishedTexts);

        return likePublishedTexts.stream()
                .map(TextDTO::new)
                .collect(Collectors.toSet());
    }

    @Override
    public Set<Text> getLikePublishedTexts(int docId, Language language) {
        final LanguageJPA languageJPA = new LanguageJPA(language);

        return repository.findByDocIdAndLanguageAndLikePublishedIsTrue(docId, languageJPA)
                .stream()
                .map(TextDTO::new)
                .collect(Collectors.toSet());
    }

    private Text getText(int docId, int index, String langCode, LoopEntryRef loopEntryRef, Version version) {
        final LanguageJPA language = new LanguageJPA(languageService.findByCode(langCode));
        final TextJPA text = getText(index, version, language, loopEntryRef);

        return Optional.ofNullable(text)
                .map(TextDTO::new)
                .orElse(new TextDTO(index, docId, langCode, loopEntryRef, false));
    }

    private TextJPA getText(int index, Version version, LanguageJPA language, LoopEntryRef loopEntryRef) {

        final TextJPA likePublishedText = getLikePublishedText(version, index, language, loopEntryRef);

        if (likePublishedText != null) return likePublishedText;

        if (loopEntryRef == null) {
            return repository.findByVersionAndLanguageAndIndexWhereLoopEntryRefIsNull(version, language, index);

        } else {
            return repository.findByVersionAndLanguageAndIndexAndLoopEntryRef(
                    version, language, index, new LoopEntryRefJPA(loopEntryRef)
            );
        }
    }

    private TextJPA getLikePublishedText(Version version, int index, LanguageJPA language, LoopEntryRef loopEntryRef) {
        if (loopEntryRef == null) {
            return repository.findByIndexAndVersionAndLanguageAndLikePublishedIsTrueAndLoopEntryRefIsNull(index, version, language);

        } else {
            return repository.findByIndexAndVersionAndLanguageAndLikePublishedIsTrueAndLoopEntryRef(
                    index, version, language, new LoopEntryRefJPA(loopEntryRef)
            );
        }
    }

    @Override
    protected TextJPA removeId(TextJPA entity, Version version) {
        return new TextJPA(entity, version);
    }

    @Override
    public Text filter(Text text) {
        final String textContent = text.getText();
        final Text.HtmlFilteringPolicy filteringPolicy = text.getHtmlFilteringPolicy();

        if (RESTRICTED.equals(filteringPolicy) || RELAXED.equals(filteringPolicy)) {
            text.setText(textContentFilter.cleanText(textContent, filteringPolicy));
        }

        return text;
    }

	@Override
	public List<Text> getLoopTexts(int docId, String langCode, int loopIndex) {
		final LanguageJPA languageJPA = new LanguageJPA(languageService.findByCode(langCode));

		return repository.findByVersionAndLanguageAndLoopIndex(versionService.getDocumentWorkingVersion(docId), languageJPA, loopIndex)
				.stream()
				.map(TextDTO::new)
				.collect(Collectors.toList());
	}

	@Override
	public List<Text> getTextsContaining(String content) {
		return repository.findByTextContaining(content)
				.stream().map(TextDTO::new)
				.collect(Collectors.toList());
	}
}
