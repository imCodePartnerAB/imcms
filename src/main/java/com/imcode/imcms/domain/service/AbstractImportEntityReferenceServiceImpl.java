package com.imcode.imcms.domain.service;

import com.imcode.imcms.domain.dto.ImportEntityReferenceDTO;
import com.imcode.imcms.model.AbstractImportEntityReference;
import com.imcode.imcms.model.ImportEntityReferenceType;
import com.imcode.imcms.persistence.repository.ImportEntityReferenceRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Log4j2
@Service
@Transactional
public abstract class AbstractImportEntityReferenceServiceImpl<T extends AbstractImportEntityReference>
		implements ImportEntityReferenceService {

	private final ImportEntityReferenceRepository<T> importEntityReferenceRepository;

	protected AbstractImportEntityReferenceServiceImpl(ImportEntityReferenceRepository<T> importEntityReferenceRepository) {
		this.importEntityReferenceRepository = importEntityReferenceRepository;
	}

	@Override
	public ImportEntityReferenceDTO create(String name, ImportEntityReferenceType type) {
		return create(new ImportEntityReferenceDTO(name, type));
	}

	@Override
	public ImportEntityReferenceDTO create(ImportEntityReferenceDTO entity) {
		return new ImportEntityReferenceDTO(importEntityReferenceRepository.save(AbstractImportEntityReference.of(entity).toTyped()));
	}

	@Override
	public void update(ImportEntityReferenceDTO reference) {
		final T entityReferenceBD = importEntityReferenceRepository.getOne(reference.getId());
		if (!entityReferenceBD.getName().equals(reference.getName())) {
			log.error("Cannot change name of entity reference, {},{}!", entityReferenceBD.toString(), reference.toString());
			throw new RuntimeException(String.format("Cannot change entity reference name,db: %s, new: %s!", entityReferenceBD, reference));
		}

		importEntityReferenceRepository.save(AbstractImportEntityReference.of(reference).toTyped());
	}

	@Override
	public List<ImportEntityReferenceDTO> getAll() {
		return importEntityReferenceRepository.findAll()
				.stream().map(ImportEntityReferenceDTO::new)
				.collect(Collectors.toList());
	}

	@Override
	public ImportEntityReferenceDTO getByName(String name) {
		return new ImportEntityReferenceDTO(importEntityReferenceRepository.findByName(name));
	}

	@Override
	public Optional<ImportEntityReferenceDTO> getById(Integer id) {
		return importEntityReferenceRepository.findById(id).map(ImportEntityReferenceDTO::new);
	}

	@Override
	public boolean existsByName(String name) {
		return importEntityReferenceRepository.existsByName(name);
	}
}
