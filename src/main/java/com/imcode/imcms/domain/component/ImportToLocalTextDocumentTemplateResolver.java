package com.imcode.imcms.domain.component;

import com.imcode.imcms.domain.dto.ImportEntityReferenceDTO;
import com.imcode.imcms.domain.dto.TextDocumentTemplateDTO;
import com.imcode.imcms.domain.service.ImportEntityReferenceManagerService;
import com.imcode.imcms.domain.service.TextDocumentTemplateService;
import com.imcode.imcms.model.ImportEntityReferenceType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ImportToLocalTextDocumentTemplateResolver {
	private final TextDocumentTemplateService textDocumentTemplateService;
	private final ImportEntityReferenceManagerService importEntityReferenceManagerService;

	public TextDocumentTemplateDTO resolve(String templateName) {
		final ImportEntityReferenceDTO templateReference = importEntityReferenceManagerService.getReference(templateName, ImportEntityReferenceType.TEMPLATE);
		final Integer templateReferenceLinkedEntityId = templateReference.getLinkedEntityId();

		TextDocumentTemplateDTO textDocumentTemplate = TextDocumentTemplateDTO.createDefault();
		if (templateReferenceLinkedEntityId != null) {
			textDocumentTemplate = textDocumentTemplateService.get(templateReferenceLinkedEntityId)
					.map(TextDocumentTemplateDTO::new)
					.orElseThrow();
		}

		return textDocumentTemplate;
	}
}
