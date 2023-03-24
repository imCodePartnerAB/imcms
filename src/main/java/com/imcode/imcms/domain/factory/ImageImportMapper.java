package com.imcode.imcms.domain.factory;

import com.imcode.imcms.domain.dto.ImageDTO;
import com.imcode.imcms.domain.dto.ImportImageDTO;
import com.imcode.imcms.domain.service.ImageService;
import com.imcode.imcms.model.Language;
import imcode.util.image.Format;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ImageImportMapper {

	private final ImageService imageService;

	public void mapAndSave(Integer docId, Language language, ImportImageDTO importImage) {
		final ImageDTO image = new ImageDTO();

		image.setDocId(docId);
		image.setIndex(importImage.getIndex());
		image.setName(StringUtils.defaultString(importImage.getName()));
		image.setGeneratedFilename(StringUtils.defaultString(importImage.getGeneratedFilename()));
		image.setAlternateText(importImage.getAltText());
		image.setType(0);

		if (importImage.getFormat() != null)
			image.setFormat(Format.findFormat(importImage.getFormat()));

		image.setAlign(importImage.getAlign());
		image.setBorder(importImage.getBorder());
		image.setWidth(importImage.getWidth());
		image.setHeight(importImage.getHeight());
		image.setRotateAngle(importImage.getAngle());

		image.setTarget(importImage.getTarget());
		image.setLinkUrl(importImage.getLinkUrl());
		image.setPath(importImage.getPath());
		image.setLowResolutionUrl(importImage.getLowResolutionUrl());

		image.setLangCode(language.getCode());
		image.setArchiveImageId(importImage.getArchiveImageId());

		imageService.saveImage(image);
	}

	public void mapAndSave(Integer docId, Language language, List<ImportImageDTO> importImages) {
		importImages.forEach(importImage -> mapAndSave(docId, language, importImage));
	}
}
