package com.imcode.imcms.domain.factory;

import com.imcode.imcms.domain.dto.ImportTextDTO;
import com.imcode.imcms.domain.dto.TextDTO;
import com.imcode.imcms.domain.service.TextService;
import com.imcode.imcms.model.Language;
import com.imcode.imcms.model.Text;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.List;

@Log4j2
@Component
@RequiredArgsConstructor
public class TextImportMapper {

	private final TextService textService;

	public void mapAndSave(int importDocId, int docId, Language language, ImportTextDTO importText) {
		final TextDTO textDTO = new TextDTO();

		textDTO.setDocId(docId);
		textDTO.setType(Text.Type.valueOf(importText.getType()));
		textDTO.setHtmlFilteringPolicy(Text.HtmlFilteringPolicy.ALLOW_ALL);
		textDTO.setLangCode(language.getCode());
		textDTO.setText(importText.getText());
		textDTO.setIndex(importText.getIndex());

		textService.save(textDTO);
	}

	public void mapAndSave(int importDocId, int docId, Language language, List<ImportTextDTO> importTexts) {
		importTexts.forEach(importText -> mapAndSave(importDocId, docId, language, importText));
	}
}
