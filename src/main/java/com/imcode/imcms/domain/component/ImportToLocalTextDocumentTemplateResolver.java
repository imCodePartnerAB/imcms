package com.imcode.imcms.domain.component;

import com.imcode.imcms.domain.dto.ImportEntityReferenceDTO;
import com.imcode.imcms.domain.dto.TextDocumentTemplateDTO;
import com.imcode.imcms.domain.service.ImportEntityReferenceManagerService;
import com.imcode.imcms.domain.service.TemplateService;
import com.imcode.imcms.model.ImportEntityReferenceType;
import com.imcode.imcms.model.Template;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ImportToLocalTextDocumentTemplateResolver {
	private final TemplateService templateService;
	private final ImportEntityReferenceManagerService importEntityReferenceManagerService;

	public TextDocumentTemplateDTO resolve(String templateName) {
		final ImportEntityReferenceDTO templateReference = importEntityReferenceManagerService.getReference(templateName, ImportEntityReferenceType.TEMPLATE);
		final Integer templateReferenceLinkedEntityId = templateReference.getLinkedEntityId();

		final TextDocumentTemplateDTO textDocumentTemplate = TextDocumentTemplateDTO.createDefault();
		if (templateReferenceLinkedEntityId != null) {
			final Template template = templateService.getById(templateReferenceLinkedEntityId);
			textDocumentTemplate.setTemplateName(template.getName());
			textDocumentTemplate.setChildrenTemplateName(template.getName());
		}

		return textDocumentTemplate;
	}
}
