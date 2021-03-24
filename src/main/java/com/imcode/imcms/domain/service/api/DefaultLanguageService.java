package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.api.exception.DataIsNotValidException;
import com.imcode.imcms.domain.dto.DocumentDTO;
import com.imcode.imcms.domain.dto.LanguageDTO;
import com.imcode.imcms.domain.exception.ImpossibleRemoveLanguageException;
import com.imcode.imcms.domain.exception.LanguageNotAvailableException;
import com.imcode.imcms.domain.service.CommonContentService;
import com.imcode.imcms.domain.service.DocumentService;
import com.imcode.imcms.domain.service.LanguageService;
import com.imcode.imcms.model.CommonContent;
import com.imcode.imcms.model.Language;
import com.imcode.imcms.persistence.entity.LanguageJPA;
import com.imcode.imcms.persistence.repository.LanguageRepository;
import imcode.server.ImcmsConstants;
import imcode.server.LanguageMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Transactional
@Service("languageService")
@Slf4j
class DefaultLanguageService implements LanguageService {

    private final LanguageRepository languageRepository;
    private final DocumentService<DocumentDTO> documentService;
    private final CommonContentService commonContentService;

    @Value("#{'${AvailableLanguages}'.split(';')}")
    private List<String> availableLanguages;
    @Value("#{'${DefaultLanguage}'}")
    private String defaultLang;

    DefaultLanguageService(LanguageRepository languageRepository,
                           @Lazy DocumentService<DocumentDTO> documentService,
                           @Lazy CommonContentService commonContentService) {
        this.languageRepository = languageRepository;
        this.documentService = documentService;
        this.commonContentService = commonContentService;
    }

    private String convertLanguage(String code) {
        if (LanguageMapper.existsIsoCode639_2(code)) {
           code = LanguageMapper.convert639_2to639_1(code);
        }

        return code;
    }

    @Override
    public Language findByCode(String code) {
        code = convertLanguage(code);

        if (!availableLanguages.contains(code)) {
            throw new LanguageNotAvailableException(code);
        }
        return new LanguageDTO(languageRepository.findByCode(code));
    }

    @Override
    public List<Language> getAll() {
        return languageRepository.findAll()
                .stream()
                .map(LanguageDTO::new)
                .collect(Collectors.toList());
    }

    @Override
    public List<String> getAllAdminLangCode() { // maybe in future we can fetch langs admin from db..
        return Arrays.asList(ImcmsConstants.ENG_CODE, ImcmsConstants.SWE_CODE);
    }

    @Override
    public boolean isAdminLanguage(String code) {
        code = convertLanguage(code);

        return getAllAdminLangCode().contains(code);
    }

    @Override
    public List<Language> getAvailableLanguages() {
        return languageRepository.findAll()
                .stream()
                .filter(lang -> availableLanguages.contains(lang.getCode()))
                .map(LanguageDTO::new)
                .collect(Collectors.toList());
    }

    @Override
    public Language getDefaultLanguage() {
        return new LanguageDTO(findByCode(defaultLang));
    }

    //be careful with this! This remove language on the whole system!
    @Override
    public void deleteByCode(String code) {
        final LanguageJPA foundLanguage = languageRepository.findByCode(convertLanguage(code));

        final boolean foundLangIsDefaultLang = foundLanguage.getCode().equals(defaultLang);

        final Optional<CommonContent> foundCommonContent = commonContentService.getAll()
                .stream()
                .filter(commonContent -> commonContent.getLanguage().getId().equals(foundLanguage.getId()))
                .findAny();

        if (foundLangIsDefaultLang || foundCommonContent.isPresent()) throw new ImpossibleRemoveLanguageException();

        languageRepository.delete(foundLanguage);
    }

    @Override
    public void save(Language language) {
        final String nameLanguage = language.getName();
        final String nativeNameLanguage = language.getNativeName();
        final String languageCode = language.getCode();

        if (!StringUtils.hasText(nameLanguage) ||
                !StringUtils.hasText(nativeNameLanguage) ||
                !StringUtils.hasText(languageCode)) {

            throw new DataIsNotValidException(String.format(
                    "Save: nameLanguage - %s or nativeName - %s or langCode - %s are not valid!",
                    nameLanguage, nativeNameLanguage, languageCode
            ));
        }

        LanguageJPA jpaLanguage = languageRepository.findByCode(language.getCode());

        if (jpaLanguage != null) {
            jpaLanguage.setName(nameLanguage);
            jpaLanguage.setNativeName(nativeNameLanguage);
        } else {
            languageRepository.save(
                    new LanguageJPA(language.getCode(), language.getName(), language.getNativeName())
            );
        }
    }

    @Override
    public List<Language> getEnabledContentLanguagesByDocId(Integer docId) {
        return documentService.get(docId).getCommonContents().stream()
                .filter(CommonContent::isEnabled)
                .map(CommonContent::getLanguage)
                .collect(Collectors.toList());
    }
}
