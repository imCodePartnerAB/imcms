package com.imcode.imcms.domain.dto;

import imcode.server.document.index.ImageFileStoredFields;
import imcode.util.DateConstants;
import imcode.util.image.Format;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.apache.commons.io.FilenameUtils;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class ImageFileStoredFieldsDTO extends ImageFileDTO {

	private String id;

	public ImageFileStoredFieldsDTO(ImageFileStoredFields from) {
		this.id = from.id();
		setName(from.name());
		setPath(from.path());
		setFormat(Format.findFormat(FilenameUtils.getExtension(from.name())));
		setUploaded(DateConstants.DATETIME_DOC_FORMAT.get().format(from.uploaded()));
		setResolution(from.width() + "x" + from.height());
		setSize(from.size());
		setWidth(from.width());
		setHeight(from.height());
		setExifInfo(from.exifInfo());

	}
}


