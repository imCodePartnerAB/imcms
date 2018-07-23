package com.imcode.imcms.domain.service.core;

import com.imcode.imcms.components.datainitializer.DocumentDataInitializer;
import com.imcode.imcms.components.datainitializer.LanguageDataInitializer;
import com.imcode.imcms.config.TestConfig;
import com.imcode.imcms.domain.dto.DocumentDTO;
import com.imcode.imcms.domain.dto.LanguageDTO;
import com.imcode.imcms.domain.dto.MenuItemDTO;
import com.imcode.imcms.domain.exception.DocumentNotExistException;
import com.imcode.imcms.domain.service.DocumentMenuService;
import com.imcode.imcms.domain.service.DocumentService;
import com.imcode.imcms.model.Roles;
import com.imcode.imcms.persistence.entity.Meta;
import com.imcode.imcms.persistence.entity.Meta.Permission;
import com.imcode.imcms.persistence.repository.MetaRepository;
import imcode.server.Imcms;
import imcode.server.ImcmsConstants;
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
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

@Transactional
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfig.class})
public class DocumentMenuServiceTest {

    @Autowired
    private DocumentService<DocumentDTO> documentService;

    @Autowired
    private MetaRepository metaRepository;

    @Autowired
    private DocumentDataInitializer documentDataInitializer;

    @Autowired
    private DocumentMenuService documentMenuService;

    @Autowired
    private LanguageDataInitializer languageDataInitializer;

    private Meta meta;

    @Before
    public void setUp() throws Exception {

        final UserDomainObject user = new UserDomainObject(1);
        user.addRoleId(Roles.SUPER_ADMIN.getId());
        user.setLanguageIso639_2(ImcmsConstants.ENG_CODE_ISO_639_2);
        Imcms.setUser(user); // means current user is admin now

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
        final HashMap<Integer, Permission> roleIdToPermission = new HashMap<>(Collections.singletonMap(
                Roles.USER_ADMIN.getId(), Permission.EDIT
        ));
        meta.setRoleIdToPermission(roleIdToPermission);

        metaRepository.saveAndFlush(meta);

        final UserDomainObject user = Imcms.getUser();

        user.addRoleId(Roles.USER.getId());
        user.addRoleId(Roles.USER_ADMIN.getId());

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
    public void getMenuItemDTO_When_AliasIsSet_Expect_AliasInLink() {
        testMenuItemDTO(true);
    }

    @Test
    public void getMenuItemDTO_When_NoAlias_Expect_DocIdInLink() {
        testMenuItemDTO(false);
    }

    @Test(expected = DocumentNotExistException.class)
    public void isPublicMenuItem_When_DocIsNotExist_Expect_DocumentNotExistException() {
        assertTrue(documentMenuService.isPublicMenuItem(Integer.MAX_VALUE));
    }

    @Test
    public void isPublicMenuItem_When_MetaHasStatusNew_Expect_False() {
        meta.setPublicationStatus(Meta.PublicationStatus.NEW);
        metaRepository.save(meta);

        assertFalse(documentMenuService.isPublicMenuItem(meta.getId()));
    }

    @Test
    public void isPublicMenuItem_When_MetaHasStatusDisapproved_Expect_False() {
        meta.setPublicationStatus(Meta.PublicationStatus.DISAPPROVED);
        metaRepository.save(meta);

        assertFalse(documentMenuService.isPublicMenuItem(meta.getId()));
    }

    @Test
    public void isPublicMenuItem_When_MetaHasStatusApproved_Expect_True() {
        meta.setPublicationStatus(Meta.PublicationStatus.APPROVED);
        meta.setPublicationStartDatetime(new Date(0, 1, 1));
        metaRepository.save(meta);

        assertTrue(documentMenuService.isPublicMenuItem(meta.getId()));
    }

    @Test
    public void isPublicMenuItem_When_MetaHasArchivedDateInFuture_Expect_True() {
        meta.setArchivedDatetime(new Date(Integer.MAX_VALUE, 1, 1));
        meta.setPublicationStartDatetime(new Date(0, 1, 1));
        metaRepository.save(meta);

        assertTrue(documentMenuService.isPublicMenuItem(meta.getId()));
    }

    @Test
    public void isPublicMenuItem_When_MetaHasArchivedDateInPast_Expect_False() {
        meta.setArchivedDatetime(new Date(0, 1, 1));
        metaRepository.save(meta);

        assertFalse(documentMenuService.isPublicMenuItem(meta.getId()));
    }

    @Test
    public void isPublicMenuItem_When_MetaHasPublicationEndDateInPast_Expect_False() {
        meta.setPublicationEndDatetime(new Date(0, 1, 1));
        metaRepository.save(meta);

        assertFalse(documentMenuService.isPublicMenuItem(meta.getId()));
    }

    @Test
    public void isPublicMenuItem_When_MetaHasPublicationEndDateInFuture_Expect_True() {
        meta.setPublicationEndDatetime(new Date(Integer.MAX_VALUE, 1, 1));
        meta.setPublicationStartDatetime(new Date(0, 1, 1));
        metaRepository.save(meta);

        assertTrue(documentMenuService.isPublicMenuItem(meta.getId()));
    }

    @Test
    public void isPublicMenuItem_When_MetaHasPublicationStartDateInPast_Expect_True() {
        meta.setPublicationStartDatetime(new Date(0, 1, 1));
        metaRepository.save(meta);

        assertTrue(documentMenuService.isPublicMenuItem(meta.getId()));
    }

    @Test
    public void isPublicMenuItem_When_MetaHasPublicationStartDateInFuture_Expect_False() {
        meta.setPublicationStartDatetime(new Date(Integer.MAX_VALUE, 1, 1));
        metaRepository.save(meta);

        assertFalse(documentMenuService.isPublicMenuItem(meta.getId()));
    }

    @Test
    public void isPublicMenuItem_When_MetaHasViewPermissionsForDefaultUser_Expect_True() {
        final Map<Integer, Permission> roleIdToPermission = new HashMap<>(
                Collections.singletonMap(Roles.USER.getId(), Permission.VIEW)
        );
        meta.setRoleIdToPermission(roleIdToPermission);
        meta.setPublicationStartDatetime(new Date(0, 1, 1));
        metaRepository.save(meta);

        assertTrue(documentMenuService.isPublicMenuItem(meta.getId()));
    }

    @Test
    public void isPublicMenuItem_When_MetaHasNonePermissionsForDefaultUser_Expect_False() {
        final Map<Integer, Permission> roleIdToPermission = new HashMap<>(
                Collections.singletonMap(Roles.USER.getId(), Permission.NONE)
        );
        meta.setRoleIdToPermission(roleIdToPermission);
        metaRepository.save(meta);

        assertFalse(documentMenuService.isPublicMenuItem(meta.getId()));
    }

    private void testMenuItemDTO(boolean aliasExist) {
        final Integer docId = meta.getId();
        final LanguageDTO language = languageDataInitializer.createData().get(0);

        final DocumentDTO documentDTO = documentService.get(docId);
        final String testAlias = "test_alias";

        final String headline = documentDTO.getCommonContents()
                .stream()
                .filter(commonContent -> commonContent.getLanguage().getCode().equals(language.getCode()))
                .findFirst()
                .get()
                .getHeadline();

        if (aliasExist) {
            documentDTO.setAlias(testAlias);
            documentService.save(documentDTO);
        }

        final MenuItemDTO menuItemDTO = documentMenuService.getMenuItemDTO(docId, language);

        assertThat(menuItemDTO.getDocumentId(), is(docId));
        assertThat(menuItemDTO.getType(), is(documentDTO.getType()));
        assertThat(menuItemDTO.getTitle(), is(headline));
        assertThat(menuItemDTO.getLink(), is("/" + (aliasExist ? testAlias : docId)));
        assertThat(menuItemDTO.getTarget(), is(documentDTO.getTarget()));
        assertThat(menuItemDTO.getDocumentStatus(), is(documentDTO.getDocumentStatus()));
        assertThat(menuItemDTO.getChildren(), empty());
    }
}
