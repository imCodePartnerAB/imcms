package com.imcode.imcms.domain.component;

import com.imcode.imcms.domain.dto.DocumentUrlDTO;
import com.imcode.imcms.domain.service.BasicImportDocumentInfoService;
import com.imcode.imcms.domain.service.LinkValidationService;
import com.imcode.imcms.servlet.ImcmsSetupFilter;
import imcode.server.Imcms;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Log4j2
@Component
@RequiredArgsConstructor
public class ImportToLocalDocumentURLResolver {
	private final BasicImportDocumentInfoService basicImportDocumentInfoService;
	private final LinkValidationService linkValidationService;

	public DocumentUrlDTO resolve(String url) {
		if (linkValidationService.isExternal(url)) {
			//return if external
			return DocumentUrlDTO.createDefaultWithUrl(url);
		}

		final String documentIdString = ImcmsSetupFilter.getDocumentIdString(Imcms.getServices(), url);
		try {
			final int id = Integer.parseInt(documentIdString);
			final Optional<Integer> metaId = basicImportDocumentInfoService.toMetaId(id);

			if (metaId.isEmpty()) {
				//if null -> document with such id not imported yet, so leave url as it is
				log.warn("Document with id: {} not imported yet so this document`s: url: {} stays untouched", id, url);
				return DocumentUrlDTO.createDefaultWithUrl(url);
			}

			//replace rb4 meta id in url with new rb6
			return DocumentUrlDTO.createDefaultWithUrl(url.replace(documentIdString, metaId.get().toString()));
		} catch (NumberFormatException e) {
			//if documentIdString is alias leave as it is
			return DocumentUrlDTO.createDefaultWithUrl(url);
		}
	}
}
