package com.imcode.imcms.controller.api;

import com.imcode.imcms.domain.dto.DocumentStoredFieldsDTO;
import com.imcode.imcms.domain.dto.SearchQueryDTO;
import com.imcode.imcms.domain.service.AccessService;
import com.imcode.imcms.domain.service.SearchDocumentService;
import imcode.server.Imcms;
import imcode.server.document.index.DocumentIndex;
import imcode.server.user.UserDomainObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by dmizem from Ubrainians for imCode on 19.10.17.
 */
@RestController
@RequestMapping("/documents/search")
public class SearchDocumentController {

    private  final AccessService accessService;
    private final SearchDocumentService searchDocumentService;

    SearchDocumentController(AccessService accessService, SearchDocumentService searchDocumentService) {
        this.accessService = accessService;
        this.searchDocumentService = searchDocumentService;
    }

    @GetMapping
    public List<DocumentStoredFieldsDTO> getDocuments(SearchQueryDTO searchQuery) {
        final UserDomainObject user = Imcms.getUser();
        boolean accessToAdminPages = accessService.getTotalRolePermissionsByUser(user).isAccessToAdminPages();

        if(!(user.isSuperAdmin() || accessToAdminPages)) searchQuery.setRoleId(null);
        boolean limitSearch = !(user.isSuperAdmin() && searchQuery.getRoleId() == null);

        return searchDocumentService.searchDocuments(searchQuery, limitSearch);
    }

    @GetMapping("/{id}")
    public DocumentStoredFieldsDTO getDocument(@PathVariable Integer id){
        return searchDocumentService.searchDocuments(DocumentIndex.FIELD__ID + ":" + id, false).get(0);
    }
}