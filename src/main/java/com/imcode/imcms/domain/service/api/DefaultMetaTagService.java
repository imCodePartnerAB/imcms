package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.api.exception.AlreadyExistsException;
import com.imcode.imcms.domain.dto.MetaTagDTO;
import com.imcode.imcms.domain.service.MetaTagService;
import com.imcode.imcms.persistence.entity.MetaTagJPA;
import com.imcode.imcms.persistence.repository.MetaTagRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DefaultMetaTagService implements MetaTagService {
	private final MetaTagRepository metaTagRepository;

	public DefaultMetaTagService(MetaTagRepository metaTagRepository) {
		this.metaTagRepository = metaTagRepository;
	}


	@Override
	public void saveMetaTag(String metaTagName) {
		if (metaTagRepository.existsByName(metaTagName)) {
			throw new AlreadyExistsException(String.format("Meta tag with name = %s already exists", metaTagName));
		}

		metaTagRepository.save(new MetaTagJPA(metaTagName));
	}

	@Override
	public List<MetaTagDTO> getAll() {
		return metaTagRepository.findAllOrderById().stream()
				.map(MetaTagDTO::new)
				.collect(Collectors.toList());
	}
}
