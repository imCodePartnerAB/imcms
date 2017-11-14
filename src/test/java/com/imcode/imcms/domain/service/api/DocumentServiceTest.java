package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.components.datainitializer.CommonContentDataInitializer;
import com.imcode.imcms.components.datainitializer.VersionDataInitializer;
import com.imcode.imcms.config.TestConfig;
import com.imcode.imcms.config.WebTestConfig;
import com.imcode.imcms.domain.dto.DocumentDTO;
import com.imcode.imcms.persistence.entity.Language;
import com.imcode.imcms.persistence.entity.Meta;
import com.imcode.imcms.persistence.repository.LanguageRepository;
import com.imcode.imcms.persistence.repository.MetaRepository;
import com.imcode.imcms.util.Value;
import imcode.server.Imcms;
import imcode.server.user.UserDomainObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.function.Function;

import static imcode.server.ImcmsConstants.ENG_CODE;
import static imcode.server.ImcmsConstants.SWE_CODE;
import static org.junit.Assert.assertEquals;

@Transactional
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfig.class, WebTestConfig.class})
public class DocumentServiceTest {

    private static final int TEST_VERSION_INDEX = 0;

    private DocumentDTO createdDoc;

    @Autowired
    private DocumentService documentService;

    @Autowired
    private MetaRepository metaRepository;

    @Autowired
    private LanguageRepository languageRepository;

    @Autowired
    private Function<Meta, DocumentDTO> metaToDocumentDTO;

    @Autowired
    private VersionDataInitializer versionDataInitializer;

    @Autowired
    private CommonContentDataInitializer commonContentDataInitializer;

    @Before
    public void setUp() throws Exception {
        final Meta metaDoc = Value.with(new Meta(), meta -> {

            final HashSet<Language> languages = new HashSet<>(Arrays.asList(
                    languageRepository.findByCode(ENG_CODE),
                    languageRepository.findByCode(SWE_CODE)
            ));

            meta.setArchivedDatetime(new Date());
            meta.setArchiverId(1);
            meta.setCategoryIds(Collections.emptySet());
            meta.setCreatedDatetime(new Date());
            meta.setCreatorId(1);
            meta.setDefaultVersionNo(0);
            meta.setDisabledLanguageShowMode(Meta.DisabledLanguageShowMode.SHOW_IN_DEFAULT_LANGUAGE);
            meta.setDocumentType(Meta.DocumentType.TEXT);
            meta.setEnabledLanguages(languages);
            meta.setKeywords(Collections.emptySet());
            meta.setLinkableByOtherUsers(true);
            meta.setLinkedForUnauthorizedUsers(true);
            meta.setModifiedDatetime(new Date());
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
        versionDataInitializer.createData(TEST_VERSION_INDEX, metaDoc.getId());
        commonContentDataInitializer.createData(metaDoc.getId(), TEST_VERSION_INDEX);
        createdDoc = metaToDocumentDTO.apply(metaDoc);
    }

    @Test
    public void get() throws Exception {
        final DocumentDTO documentDTO = documentService.get(createdDoc.getId());
        assertEquals(documentDTO, createdDoc);
    }

    @Test
    public void save() throws Exception {
        final DocumentDTO documentDTO = documentService.get(createdDoc.getId());
        documentDTO.setTarget("test_target");

        documentService.save(documentDTO);

        final DocumentDTO documentDTO1 = documentService.get(documentDTO.getId());

        assertEquals(documentDTO1, documentDTO);
    }

}