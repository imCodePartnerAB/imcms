package com.imcode.imcms.domain.service;

import com.imcode.imcms.domain.dto.ImportEntityReferenceDTO;
import com.imcode.imcms.model.ImportEntityReferenceType;

import java.util.List;

public interface ImportEntityReferenceManagerService {
	ImportEntityReferenceDTO createReference(String name, ImportEntityReferenceType type);

	ImportEntityReferenceDTO createReference(ImportEntityReferenceDTO importReference);

	void updateReference(ImportEntityReferenceDTO reference);

	ImportEntityReferenceDTO getReference(String name, ImportEntityReferenceType type);

	List<ImportEntityReferenceDTO> getAllReferencesByType(ImportEntityReferenceType type);

}
