package com.imcode.imcms.domain.service;

import com.imcode.imcms.domain.dto.MetaTagDTO;

import java.util.List;

public interface MetaTagService {

	void saveMetaTag(String metaTagName);

	List<MetaTagDTO> getAll();
}
