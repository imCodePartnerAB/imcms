package com.imcode.imcms.domain.factory;

import com.imcode.imcms.domain.dto.AuditDTO;
import com.imcode.imcms.domain.dto.DocumentDTO;
import com.imcode.imcms.domain.dto.DocumentUrlDTO;
import com.imcode.imcms.domain.dto.FileDocumentDTO;
import com.imcode.imcms.domain.dto.RestrictedPermissionDTO;
import com.imcode.imcms.domain.dto.TextDocumentDTO;
import com.imcode.imcms.domain.dto.TextDocumentTemplateDTO;
import com.imcode.imcms.domain.dto.UrlDocumentDTO;
import com.imcode.imcms.model.RestrictedPermission;
import com.imcode.imcms.persistence.entity.Meta;
import com.imcode.imcms.persistence.entity.Version;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import static com.imcode.imcms.persistence.entity.Meta.DisabledLanguageShowMode.SHOW_IN_DEFAULT_LANGUAGE;
import static com.imcode.imcms.persistence.entity.Meta.PublicationStatus.NEW;

@Component
public class DocumentDtoFactory {

    private final CommonContentFactory commonContentFactory;

    DocumentDtoFactory(CommonContentFactory commonContentFactory) {
        this.commonContentFactory = commonContentFactory;
    }

    public DocumentDTO createEmpty() {
        final DocumentDTO documentDTO = new DocumentDTO();
        documentDTO.setTarget("");
        documentDTO.setAlias("");

        documentDTO.setCommonContents(commonContentFactory.createCommonContents());

        documentDTO.setPublicationStatus(NEW);
        documentDTO.setDisabledLanguageShowMode(SHOW_IN_DEFAULT_LANGUAGE);
        documentDTO.setKeywords(new HashSet<>());
        documentDTO.setCategories(new HashSet<>());
        documentDTO.setRoleIdToPermission(new HashMap<>());

        final Set<RestrictedPermission> restrictedPermissions = new HashSet<>();
        final RestrictedPermissionDTO restricted1 = new RestrictedPermissionDTO();
        final RestrictedPermissionDTO restricted2 = new RestrictedPermissionDTO();
        restricted1.setPermission(Meta.Permission.RESTRICTED_1);
        restricted2.setPermission(Meta.Permission.RESTRICTED_2);
        restrictedPermissions.add(restricted1);
        restrictedPermissions.add(restricted2);

        documentDTO.setRestrictedPermissions(restrictedPermissions);

        documentDTO.setPublished(new AuditDTO());
        documentDTO.setArchived(new AuditDTO());
        documentDTO.setPublicationEnd(new AuditDTO());
        documentDTO.setModified(new AuditDTO());
        documentDTO.setCreated(new AuditDTO());
        documentDTO.setCurrentVersion(new AuditDTO());
        documentDTO.getCurrentVersion().setId(Version.WORKING_VERSION_INDEX);

        return documentDTO;
    }

    public FileDocumentDTO createEmptyFileDocument() {
        final FileDocumentDTO fileDocumentDTO = new FileDocumentDTO(createEmpty());
        fileDocumentDTO.setFiles(new ArrayList<>());

        return fileDocumentDTO;
    }

    public TextDocumentDTO createEmptyTextDocument() {
        final TextDocumentDTO textDocumentDTO = new TextDocumentDTO(createEmpty());
        textDocumentDTO.setTemplate(TextDocumentTemplateDTO.createDefault());

        return textDocumentDTO;
    }

    public UrlDocumentDTO createEmptyUrlDocument() {
        final UrlDocumentDTO urlDocumentDTO = new UrlDocumentDTO(createEmpty());
        urlDocumentDTO.setDocumentURL(DocumentUrlDTO.createDefault());

        return urlDocumentDTO;
    }
}
