package com.imcode.imcms.components.datainitializer;

import com.imcode.imcms.domain.dto.DocumentMetadataDTO;
import com.imcode.imcms.model.Language;
import com.imcode.imcms.persistence.entity.MetaTagJPA;
import com.imcode.imcms.persistence.repository.MetaTagRepository;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class DocumentMetadataInitializer extends TestDataCleaner {
	private final MetaTagRepository metaTagRepository;

	public DocumentMetadataInitializer(MetaTagRepository metaTagRepository) {
		super();
		this.metaTagRepository = metaTagRepository;
	}

	public List<DocumentMetadataDTO> createDTO(Language language) {
		final List<DocumentMetadataDTO> documentMetadata = new ArrayList<>();

		for (MetaTagJPA metaTagJPA : metaTagRepository.findAll()) {
			final DocumentMetadataDTO metadataDTO = new DocumentMetadataDTO();
			metadataDTO.setMetaTag(metaTagJPA);
			metadataDTO.setContent(metaTagJPA.getName() + "_" + language.getCode());

			documentMetadata.add(metadataDTO);
		}

		return documentMetadata;
	}
}
