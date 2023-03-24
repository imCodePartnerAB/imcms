package com.imcode.imcms.domain.service;

import com.imcode.imcms.domain.dto.ImportEntityReferenceDTO;
import com.imcode.imcms.model.ImportEntityReferenceType;

import java.util.List;
import java.util.Optional;

public interface ImportEntityReferenceService {
	ImportEntityReferenceDTO create(String name, ImportEntityReferenceType type);

	ImportEntityReferenceDTO create(ImportEntityReferenceDTO entity);

	void update(ImportEntityReferenceDTO reference);

	List<ImportEntityReferenceDTO> getAll();

	ImportEntityReferenceDTO getByName(String name);

	Optional<ImportEntityReferenceDTO> getById(Integer id);
}
