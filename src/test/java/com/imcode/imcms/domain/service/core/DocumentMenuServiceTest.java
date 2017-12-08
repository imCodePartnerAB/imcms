package com.imcode.imcms.domain.service.core;

import com.imcode.imcms.components.datainitializer.DocumentDataInitializer;
import com.imcode.imcms.config.TestConfig;
import com.imcode.imcms.config.WebTestConfig;
import com.imcode.imcms.domain.dto.CommonContentDTO;
import com.imcode.imcms.domain.dto.DocumentDTO;
import com.imcode.imcms.domain.exception.DocumentNotExistException;
import com.imcode.imcms.domain.service.DocumentMenuService;
import com.imcode.imcms.domain.service.DocumentService;
import com.imcode.imcms.persistence.entity.Meta;
import com.imcode.imcms.persistence.entity.Meta.Permission;
import com.imcode.imcms.persistence.repository.MetaRepository;
import imcode.server.Imcms;
import imcode.server.user.RoleId;
import imcode.server.user.UserDomainObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@Transactional
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfig.class, WebTestConfig.class})
public class DocumentMenuServiceTest {

    @Autowired
    private DocumentService documentService;

    @Autowired
    private MetaRepository metaRepository;

    @Autowired
    private DocumentDataInitializer documentDataInitializer;

    @Autowired
    private DocumentMenuService documentMenuService;

    private Meta meta;

    @Before
    public void setUp() throws Exception {
        int id = documentDataInitializer.createData().getId();
        meta = metaRepository.getOne(id);
    }

    @After
    public void tearDown() {
        documentDataInitializer.cleanRepositories();
    }

    @Test
    public void hasUserAccessToDoc_When_MetaLinkedForUnauthorizedUsersAndUserNotHasRights_Expect_True() {
        meta.setLinkedForUnauthorizedUsers(true);
        metaRepository.save(meta);

        assertTrue(documentMenuService.hasUserAccessToDoc(meta.getId(), Imcms.getUser()));
    }

    @Test
    public void hasUserAccessToDoc_When_MetaNotLinkedForUnauthorizedUsersAndUserHasRights_Expect_True() {
        meta.setLinkedForUnauthorizedUsers(false);
        final HashMap<Integer, Permission> roleIdToPermission = new HashMap<>(Collections.singletonMap(RoleId.USERADMIN_ID, Permission.EDIT));
        meta.setRoleIdToPermission(roleIdToPermission);

        metaRepository.saveAndFlush(meta);

        final UserDomainObject user = Imcms.getUser();

        user.addRoleId(RoleId.USERS);
        user.addRoleId(RoleId.USERADMIN);

        assertTrue(documentMenuService.hasUserAccessToDoc(meta.getId(), user));
    }

    @Test
    public void hasUserAccessToDoc_When_MetaNotLinkedForUnauthorizedUsersAndUserNotHasRights_Expect_False() {
        meta.setLinkedForUnauthorizedUsers(false);
        metaRepository.save(meta);

        assertFalse(documentMenuService.hasUserAccessToDoc(meta.getId(), Imcms.getUser()));
    }

    @Test(expected = DocumentNotExistException.class)
    public void hasUserAccessToDoc_When_DocIsNotExist_Expect_DocumentNotExistException() {
        assertTrue(documentMenuService.hasUserAccessToDoc(Integer.MAX_VALUE, Imcms.getUser()));
    }

    @Test
    public void getDocumentTitle() {
        final DocumentDTO documentDTO = documentService.get(meta.getId());
        final String testHeadline = "test_headline";

        for (CommonContentDTO commonContentDTO : documentDTO.getCommonContents()) {
            commonContentDTO.setHeadline(testHeadline);
        }

        documentService.save(documentDTO);

        assertEquals(documentMenuService.getDocumentTitle(meta.getId()), testHeadline);
    }

    @Test
    public void getDocumentTarget() {
        final DocumentDTO documentDTO = documentService.get(meta.getId());
        final String testTarget = "_target";
        documentDTO.setTarget(testTarget);
        documentService.save(documentDTO);

        assertEquals(documentMenuService.getDocumentTarget(meta.getId()), testTarget);
    }

    @Test
    public void getDocumentLink_When_NoAlias_Expect_DocIdInLink() {
        final int docId = meta.getId();

        assertEquals(documentMenuService.getDocumentLink(docId), "/" + meta.getId());
    }

    @Test
    public void getDocumentLink_When_AliasIsSet_Expect_AliasInLink() {
        final DocumentDTO documentDTO = documentService.get(meta.getId());
        final String testAlias = "test_alias";
        documentDTO.setAlias(testAlias);
        documentService.save(documentDTO);

        assertEquals(documentMenuService.getDocumentLink(meta.getId()), "/" + testAlias);
    }
}
