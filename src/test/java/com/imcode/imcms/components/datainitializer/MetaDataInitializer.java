package com.imcode.imcms.components.datainitializer;

import com.imcode.imcms.domain.dto.DocumentDTO;
import com.imcode.imcms.persistence.entity.Meta;
import com.imcode.imcms.persistence.entity.Meta.DisabledLanguageShowMode;
import com.imcode.imcms.persistence.entity.Meta.DocumentType;
import com.imcode.imcms.persistence.entity.Meta.Permission;
import com.imcode.imcms.persistence.entity.Meta.PublicationStatus;
import com.imcode.imcms.persistence.repository.MetaRepository;
import imcode.server.document.DocumentDomainObject;
import imcode.server.user.RoleId;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class MetaDataInitializer extends TestDataCleaner {

    private final MetaRepository metaRepository;
    private final CommonContentDataInitializer commonContentDataInitializer;

    private Meta meta;
    private String title;

    public MetaDataInitializer(MetaRepository metaRepository, CommonContentDataInitializer commonContentDataInitializer) {
        super();
        this.metaRepository = metaRepository;
        this.commonContentDataInitializer = commonContentDataInitializer;
    }

    public Meta createData() {
        Meta meta = new Meta();

        meta.setDefaultVersionNo(3);
        meta.setDisabledLanguageShowMode(DisabledLanguageShowMode.SHOW_IN_DEFAULT_LANGUAGE);
        meta.setDocumentType(DocumentType.TEXT);
        meta.setCreatorId(1);
        meta.setLinkableByOtherUsers(true);
        meta.setLinkedForUnauthorizedUsers(true);
        meta.setCreatedDatetime(new Date());
        meta.setModifiedDatetime(new Date());
        meta.setTarget("_blank");
        meta.setPublicationStatus(PublicationStatus.APPROVED);

        meta.setProperties(Collections.singletonMap(DocumentDomainObject.DOCUMENT_PROPERTIES__IMCMS_DOCUMENT_ALIAS, "/test"));

        final Map<Integer, Meta.Permission> roleRights = new HashMap<>();
        roleRights.put(RoleId.USERADMIN_ID, Permission.EDIT);
        roleRights.put(RoleId.SUPERADMIN_ID, Permission.EDIT);
        meta.setRoleIdToPermission(roleRights);

        final Set<String> keywords = new HashSet<>();
        keywords.add("keyword 1");
        keywords.add("keyword 2");
        keywords.add("keyword 3");
        keywords.add("keyword 4");
        meta.setKeywords(keywords);

        final Meta metaSaved = metaRepository.saveAndFlush(meta);

        title = commonContentDataInitializer.createData(metaSaved.getId(), 3).get(0).getHeadline();
        this.meta = metaSaved;

        return metaSaved;
    }

    public DocumentDTO getExpectedDocument() {
        final DocumentDTO dto = new DocumentDTO();

        dto.setType(DocumentType.TEXT);
        dto.setDisabledLanguageShowMode(DisabledLanguageShowMode.SHOW_IN_DEFAULT_LANGUAGE);
        dto.setTarget("_blank");
        dto.setPublicationStatus(PublicationStatus.APPROVED);
        dto.setAlias("/test");
        dto.setTitle(title);

        final Set<String> keywords = new HashSet<>();
        keywords.add("keyword 1");
        keywords.add("keyword 2");
        keywords.add("keyword 3");
        keywords.add("keyword 4");
        dto.setKeywords(keywords);

        return dto;
    }

    @Override
    public void cleanRepositories() {
        commonContentDataInitializer.cleanRepositories();
        metaRepository.delete(meta.getId());
        metaRepository.flush();
    }
}
