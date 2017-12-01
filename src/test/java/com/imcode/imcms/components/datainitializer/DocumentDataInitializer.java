package com.imcode.imcms.components.datainitializer;

import com.imcode.imcms.domain.dto.CommonContentDTO;
import com.imcode.imcms.domain.dto.DocumentDTO;
import com.imcode.imcms.domain.service.core.CommonContentService;
import com.imcode.imcms.persistence.entity.Meta;
import com.imcode.imcms.persistence.entity.Version;
import com.imcode.imcms.persistence.repository.MetaRepository;
import com.imcode.imcms.persistence.repository.TemplateRepository;
import com.imcode.imcms.util.Value;
import com.imcode.imcms.util.function.TernaryFunction;
import imcode.server.Imcms;
import imcode.server.user.UserDomainObject;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashSet;
import java.util.List;

import static com.imcode.imcms.persistence.entity.Meta.DisabledLanguageShowMode.SHOW_IN_DEFAULT_LANGUAGE;

@Component
public class DocumentDataInitializer extends TestDataCleaner {

    private static final int TEST_VERSION_INDEX = 0;

    private final MetaRepository metaRepository;
    private final TernaryFunction<Meta, Version, List<CommonContentDTO>, DocumentDTO> metaToDocumentDTO;
    private final TemplateRepository templateRepository;
    private final CommonContentDataInitializer commonContentDataInitializer;
    private final VersionDataInitializer versionDataInitializer;
    private final TemplateDataInitializer templateDataInitializer;
    private final CommonContentService commonContentService;

    public DocumentDataInitializer(MetaRepository metaRepository,
                                   TernaryFunction<Meta, Version, List<CommonContentDTO>, DocumentDTO> metaToDocumentDTO,
                                   VersionDataInitializer versionDataInitializer,
                                   TemplateDataInitializer templateDataInitializer,
                                   CommonContentService commonContentService,
                                   TemplateRepository templateRepository,
                                   CommonContentDataInitializer commonContentDataInitializer) {

        this.metaRepository = metaRepository;
        this.metaToDocumentDTO = metaToDocumentDTO;
        this.versionDataInitializer = versionDataInitializer;
        this.templateDataInitializer = templateDataInitializer;
        this.commonContentService = commonContentService;
        this.templateRepository = templateRepository;
        this.commonContentDataInitializer = commonContentDataInitializer;
    }

    public DocumentDTO createData() {
        templateDataInitializer.cleanRepositories();

        final Meta metaDoc = Value.with(new Meta(), meta -> {

            meta.setArchivedDatetime(new Date());
            meta.setArchiverId(1);
            meta.setCategoryIds(new HashSet<>());
            meta.setCreatedDatetime(new Date());
            meta.setCreatorId(1);
            meta.setModifiedDatetime(new Date());
            meta.setModifierId(1);
            meta.setDefaultVersionNo(0);
            meta.setDisabledLanguageShowMode(SHOW_IN_DEFAULT_LANGUAGE);
            meta.setDocumentType(Meta.DocumentType.TEXT);
            meta.setKeywords(new HashSet<>());
            meta.setLinkableByOtherUsers(true);
            meta.setLinkedForUnauthorizedUsers(true);
            meta.setPublicationStartDatetime(new Date());
            meta.setPublicationStatus(Meta.PublicationStatus.APPROVED);
            meta.setPublisherId(1);
            meta.setSearchDisabled(false);
            meta.setTarget("test");

        });

        final UserDomainObject user = new UserDomainObject(1);
        user.setLanguageIso639_2("eng");
        Imcms.setUser(user);

        metaRepository.save(metaDoc);
        templateDataInitializer.createData(metaDoc.getId(), "demo", "demo");

        final Version version = versionDataInitializer.createData(TEST_VERSION_INDEX, metaDoc.getId());
        commonContentDataInitializer.createData(metaDoc.getId(), TEST_VERSION_INDEX);

        final List<CommonContentDTO> commonContents = commonContentService.getOrCreateCommonContents(
                metaDoc.getId(), version.getNo()
        );
        return metaToDocumentDTO.apply(metaDoc, version, commonContents);
    }

    @Override
    public void cleanRepositories() {
        commonContentDataInitializer.cleanRepositories();
    }
}
