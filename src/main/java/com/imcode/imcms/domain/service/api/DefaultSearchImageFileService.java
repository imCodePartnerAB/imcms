package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.domain.component.ImageFileSearchQueryConverter;
import com.imcode.imcms.domain.dto.ImageFileStoredFieldsDTO;
import com.imcode.imcms.domain.dto.SearchImageFileQueryDTO;
import com.imcode.imcms.domain.service.SearchImageFileService;
import imcode.server.document.index.ImageFileIndex;
import imcode.server.document.index.ImageFileStoredFields;
import imcode.server.document.index.IndexSearchResult;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DefaultSearchImageFileService implements SearchImageFileService {

	private final ImageFileIndex index;
	private final ImageFileSearchQueryConverter searchQueryConverter;

	public DefaultSearchImageFileService(ImageFileIndex index,
	                                     ImageFileSearchQueryConverter searchQueryConverter) {
		this.index = index;
		this.searchQueryConverter = searchQueryConverter;
	}

	@Override
	public List<ImageFileStoredFieldsDTO> search(SearchImageFileQueryDTO searchQuery) {
		return mapToImageFileStoredFieldsDTO(index.search(searchQueryConverter.convertToSolrQuery(searchQuery)));
	}

	private List<ImageFileStoredFieldsDTO> mapToImageFileStoredFieldsDTO(IndexSearchResult<ImageFileStoredFields> searchResult) {
		return searchResult.storedFieldsList()
				.stream()
				.map(ImageFileStoredFieldsDTO::new)
				.toList();
	}
}
