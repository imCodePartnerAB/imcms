package com.imcode.imcms.controller.api;

import com.imcode.imcms.domain.dto.UberDocumentDTO;
import com.imcode.imcms.domain.service.DelegatingByTypeDocumentService;
import com.imcode.imcms.mapping.DocumentMapper;
import com.imcode.imcms.model.Document;
import com.imcode.imcms.persistence.entity.Meta;
import com.imcode.imcms.persistence.entity.Meta.DocumentType;
import com.imcode.imcms.security.AccessContentType;
import com.imcode.imcms.security.AccessRoleType;
import com.imcode.imcms.security.CheckAccess;
import imcode.server.Imcms;
import imcode.server.user.UserDomainObject;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Created by Serhii Maksymchuk from Ubrainians for imCode
 * 09.11.17.
 */
@RestController
@RequestMapping("/documents")
class DocumentController {

    private final DelegatingByTypeDocumentService documentService;
	private final DocumentMapper documentMapper;

    DocumentController(DelegatingByTypeDocumentService documentService, DocumentMapper documentMapper) {
        this.documentService = documentService;
	    this.documentMapper = documentMapper;
    }

    @GetMapping
    @CheckAccess(role = AccessRoleType.DOCUMENT_EDITOR, docPermission = AccessContentType.DOC_INFO)
    public Document get(Integer docId, DocumentType type, String parentDocIdentity) {
        if (docId == null) {
	        final Integer documentId = documentMapper.toDocumentId(parentDocIdentity);
            return documentService.createNewDocument(type, documentId);

        } else {
            return documentService.get(docId);
        }
    }

    @GetMapping("/alias/unique/{alias}")
    public String getUniqueAlias(@PathVariable String alias) {
        return documentService.getUniqueAlias(alias);
    }

    @PostMapping("/copy/{docId}")
    @CheckAccess(role = AccessRoleType.DOCUMENT_EDITOR)
    public Document copy(@PathVariable final Integer docId) {
        return documentService.copy(docId);
    }

    /**
     * Simply create document.
     *
     * @param createMe unified document, compatible with each {@link DocumentType} except HTML (yet?)
     * @return created document
     */
    @PostMapping
    @CheckAccess(role = AccessRoleType.DOCUMENT_EDITOR)
    public Document create(@RequestBody UberDocumentDTO createMe) {
        return documentService.save(createMe);
    }

    /**
     * Update document and checks the user's access to change some data
     *
     * @param updateMe unified document, compatible with each {@link DocumentType} except HTML (yet?)
     * @return updated document
     */
    @PutMapping
    @CheckAccess(docPermission = AccessContentType.DOC_INFO)
    public Document update(@RequestBody UberDocumentDTO updateMe) {
        final UserDomainObject user = Imcms.getUser();
        final Document document = documentService.get(updateMe.getId());

        if(!Imcms.getUser().isSuperAdmin() && !hasEditPermission(user, document)){
            updateMe.setRestrictedPermissions(document.getRestrictedPermissions());
            updateMe.setProperties(document.getProperties());
        }

        return documentService.save(updateMe);
    }

    @DeleteMapping("/{docId}")
    @CheckAccess(role = {AccessRoleType.ADMIN_PAGES, AccessRoleType.DOCUMENT_EDITOR})
    public void delete(@PathVariable Integer docId) {
        documentService.deleteByDocId(docId);
    }

    @DeleteMapping("/deleteAll")
    @CheckAccess(role = AccessRoleType.DOCUMENT_EDITOR)
    public void deleteAll(@RequestBody List<Integer> ids) {
        documentService.deleteByIds(ids);
    }

    private boolean hasEditPermission(UserDomainObject user, Document document){
        boolean hasEdIt = false;

        Map<Integer, Meta.Permission> roleIdToPermission = document.getRoleIdToPermission();
        for (Integer roleId : user.getRoleIds()) {
            if(Meta.Permission.EDIT.equals(roleIdToPermission.get(roleId))) {
                hasEdIt = true;
                break;
            }
        }

        return hasEdIt;
    }
}
