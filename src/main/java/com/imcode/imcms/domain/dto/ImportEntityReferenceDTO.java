package com.imcode.imcms.domain.dto;

import com.imcode.imcms.model.AbstractImportEntityReference;
import com.imcode.imcms.model.ImportEntityReferenceType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serial;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class ImportEntityReferenceDTO extends AbstractImportEntityReference {
	@Serial
	private static final long serialVersionUID = 5864425039430104017L;
	private Integer id;
	private String name;
	private Integer linkedEntityId;
	private ImportEntityReferenceType type;

	public ImportEntityReferenceDTO(AbstractImportEntityReference entity) {
		super(entity);
	}

	public ImportEntityReferenceDTO(String name, ImportEntityReferenceType type) {
		super(name, type);
	}


}
