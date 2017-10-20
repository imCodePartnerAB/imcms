package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.domain.dto.DocumentDTO;
import com.imcode.imcms.domain.dto.SearchQueryDTO;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dmizem from Ubrainians for imCode on 19.10.17.
 */
@Service
public class SearchDocumentService {

    public List<DocumentDTO> searchDocuments(SearchQueryDTO searchQuery) {
        return new ArrayList<DocumentDTO>();
    }

}
