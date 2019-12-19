package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.domain.component.TextContentFilter;
import com.imcode.imcms.domain.dto.TextDTO;
import com.imcode.imcms.domain.service.*;
import com.imcode.imcms.model.Language;
import com.imcode.imcms.model.LoopEntryRef;
import com.imcode.imcms.model.Text;
import com.imcode.imcms.persistence.entity.LanguageJPA;
import com.imcode.imcms.persistence.entity.LoopEntryRefJPA;
import com.imcode.imcms.persistence.entity.TextJPA;
import com.imcode.imcms.persistence.entity.Version;
import com.imcode.imcms.persistence.repository.TextRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.imcode.imcms.model.Text.HtmlFilteringPolicy.RELAXED;
import static com.imcode.imcms.model.Text.HtmlFilteringPolicy.RESTRICTED;
import static com.imcode.imcms.model.Text.Type.EDITOR;
import static com.imcode.imcms.model.Text.Type.HTML;

@Service("textService")
@Transactional
class DefaultTextService extends AbstractVersionedContentService<TextJPA, TextRepository> implements TextService {

    private final LanguageService languageService;
    private final VersionService versionService;
    private final TextHistoryService textHistoryService;
    private final TextContentFilter textContentFilter;

    DefaultTextService(TextRepository textRepository,
                       LanguageService languageService,
                       VersionService versionService,
                       TextHistoryService textHistoryService,
                       TextContentFilter textContentFilter) {

        super(textRepository);

        this.languageService = languageService;
        this.versionService = versionService;
        this.textHistoryService = textHistoryService;
        this.textContentFilter = textContentFilter;
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
        return getText(docId, index, langCode, loopEntryRef, versionService::getDocumentWorkingVersion);
    }

    @Override
    public Text getPublicText(int docId, int index, String langCode, LoopEntryRef loopEntryRef) {
        return getText(docId, index, langCode, loopEntryRef, versionService::getLatestVersion);
    }

    @Override
    public Text save(Text text) {
        final Integer docId = text.getDocId();
        final Version version = versionService.getDocumentWorkingVersion(docId);
        final LanguageJPA language = new LanguageJPA(languageService.findByCode(text.getLangCode()));

        final TextJPA textJPA = getText(text.getIndex(), version, language, text.getLoopEntryRef());
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

        final TextJPA newTextJPA = new TextJPA(text, version, language);
        newTextJPA.setId((textJPA == null) ? null : textJPA.getId());

        final Text savedText = repository.save(newTextJPA);

        super.updateWorkingVersion(docId);

        textHistoryService.save(text);

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

        return repository.findByVersionAndLanguage(latestVersion, languageJPA)
                .stream()
                .map(TextDTO::new)
                .collect(Collectors.toSet());
    }

    private Text getText(int docId, int index, String langCode, LoopEntryRef loopEntryRef,
                         Function<Integer, Version> versionReceiver) {

        final Version version = versionReceiver.apply(docId);
        final LanguageJPA language = new LanguageJPA(languageService.findByCode(langCode));
        final TextJPA text = getText(index, version, language, loopEntryRef);

        return Optional.ofNullable(text)
                .map(TextDTO::new)
                .orElse(new TextDTO(index, docId, langCode, loopEntryRef));
    }

    private TextJPA getText(int index, Version version, LanguageJPA language, LoopEntryRef loopEntryRef) {
        if (loopEntryRef == null) {
            return repository.findByVersionAndLanguageAndIndexWhereLoopEntryRefIsNull(version, language, index);

        } else {
            return repository.findByVersionAndLanguageAndIndexAndLoopEntryRef(
                    version, language, index, new LoopEntryRefJPA(loopEntryRef)
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
}
