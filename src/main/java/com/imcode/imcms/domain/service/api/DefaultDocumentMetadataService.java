package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.domain.dto.CommonContentDTO;
import com.imcode.imcms.domain.dto.DocumentMetadataDTO;
import com.imcode.imcms.domain.service.CommonContentService;
import com.imcode.imcms.domain.service.DocumentMetadataService;
import com.imcode.imcms.domain.service.LanguageService;
import com.imcode.imcms.domain.service.VersionService;
import com.imcode.imcms.model.DocumentMetadata;
import com.imcode.imcms.persistence.entity.LanguageJPA;
import com.imcode.imcms.persistence.entity.Version;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@Service("documentMetadataService")
@Transactional
public class DefaultDocumentMetadataService implements DocumentMetadataService {
	private final CommonContentService commonContentService;
	private final LanguageService languageService;
	private final VersionService versionService;

	public DefaultDocumentMetadataService(CommonContentService commonContentService, LanguageService languageService, VersionService versionService) {
		this.commonContentService = commonContentService;
		this.languageService = languageService;
		this.versionService = versionService;
	}

	@Override
	public List<DocumentMetadataDTO> getDocumentMetadataList(Integer docId, String languageCode) {
		return getDocumentMetadataList(docId, languageCode, versionService::getDocumentWorkingVersion);
	}

	@Override
	public List<DocumentMetadataDTO> getPublicDocumentMetadataList(Integer docId, String languageCode) {
		return getDocumentMetadataList(docId, languageCode, versionService::getLatestVersion);
	}

	private List<DocumentMetadataDTO> getDocumentMetadataList(Integer docId, String languageCode, Function<Integer, Version> versionReceiver) {
		final Version version = versionReceiver.apply(docId);
		final LanguageJPA language = new LanguageJPA(languageService.findByCode(languageCode));

		final Optional<CommonContentDTO> commonContentDTO = commonContentService.getByVersionAndLanguage(version, language);

		if (commonContentDTO.isEmpty()) {
			return Collections.emptyList();
		}

		return commonContentDTO.get().getDocumentMetadataList();
	}
}
