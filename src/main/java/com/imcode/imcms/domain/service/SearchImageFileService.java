package com.imcode.imcms.domain.service;

import com.imcode.imcms.domain.dto.ImageFileStoredFieldsDTO;
import com.imcode.imcms.domain.dto.SearchImageFileQueryDTO;

import java.util.List;

public interface SearchImageFileService {

	List<ImageFileStoredFieldsDTO> search(SearchImageFileQueryDTO searchQuery);
}
