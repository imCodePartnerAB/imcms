package com.imcode.imcms.domain.service.core;

import com.imcode.imcms.WebAppSpringTestConfig;
import com.imcode.imcms.components.datainitializer.DocumentDataInitializer;
import com.imcode.imcms.components.datainitializer.LanguageDataInitializer;
import com.imcode.imcms.domain.dto.DocumentDTO;
import com.imcode.imcms.domain.dto.LanguageDTO;
import com.imcode.imcms.domain.dto.MenuItemDTO;
import com.imcode.imcms.domain.exception.DocumentNotExistException;
import com.imcode.imcms.domain.service.DocumentMenuService;
import com.imcode.imcms.domain.service.DocumentService;
import com.imcode.imcms.model.CommonContent;
import com.imcode.imcms.model.Language;
import com.imcode.imcms.model.Roles;
import com.imcode.imcms.persistence.entity.MenuItem;
import com.imcode.imcms.persistence.entity.Meta;
import com.imcode.imcms.persistence.entity.Meta.Permission;
import com.imcode.imcms.persistence.repository.MetaRepository;
import com.imcode.imcms.persistence.repository.RoleRepository;
import imcode.server.Imcms;
import imcode.server.ImcmsConstants;
import imcode.server.user.UserDomainObject;
import imcode.util.Utility;
import org.apache.commons.lang.StringUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static imcode.server.user.UserDomainObject.DEFAULT_USER_ID;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
public class DocumentMenuServiceTest extends WebAppSpringTestConfig {

    @Autowired
    private DocumentService<DocumentDTO> documentService;

    @Autowired
    private MetaRepository metaRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private DocumentDataInitializer documentDataInitializer;

    @Autowired
    private DocumentMenuService documentMenuService;

    @Autowired
    private LanguageDataInitializer languageDataInitializer;

    private Meta meta;

    final UserDomainObject defaultUser = new UserDomainObject(DEFAULT_USER_ID);
    final UserDomainObject notDefaultUser = new UserDomainObject(3);

    @BeforeEach
    public void setUp() {
        final UserDomainObject user = new UserDomainObject(1);
        user.addRoleId(Roles.SUPER_ADMIN.getId());
        user.setLanguageIso639_2(ImcmsConstants.ENG_CODE_ISO_639_2);
        Imcms.setUser(user); // means current user is admin now

        int id = documentDataInitializer.createData().getId();
        meta = metaRepository.getOne(id);

        final Language currentLanguage = languageDataInitializer.createData().get(0);
        Imcms.setLanguage(currentLanguage);
    }

    @AfterEach
    public void tearDown() {
        documentDataInitializer.cleanRepositories();

        roleRepository.findAll().forEach(role -> {
            if(!role.getId().equals(Roles.SUPER_ADMIN.getId()) && !role.getId().equals(Roles.USER.getId())){
                roleRepository.delete(role);
            }
        });
    }

    @Test
    public void hasUserAccessToDoc_When_LinkedForUnauthorizedUsersIsTrue_And_UserIsDefault_Expect_True() {
        meta.setLinkedForUnauthorizedUsers(true);
        metaRepository.save(meta);

        assertTrue(documentMenuService.hasUserAccessToDoc(meta.getId(), defaultUser));
    }

    @Test
    public void hasUserAccessToDoc_When_LinkableByOtherUsersIsTrue_And_UserIsNotDefault_Expect_True() {
        meta.setLinkableByOtherUsers(true);
        metaRepository.saveAndFlush(meta);

        assertTrue(documentMenuService.hasUserAccessToDoc(meta.getId(), notDefaultUser));
    }

    @Test
    public void hasUserAccessToDoc_When_LinkableByOtherUsersIsTrue_And_LinkedForUnauthorizedUsersIsFalse_And_UserIsDefault_Expect_False() {
        meta.setLinkableByOtherUsers(true);
        meta.setLinkedForUnauthorizedUsers(false);
        metaRepository.saveAndFlush(meta);

        assertFalse(documentMenuService.hasUserAccessToDoc(meta.getId(), defaultUser));
    }

    @Test
    public void hasUserAccessToDoc_When_LinkableByOtherUsersIsFalse_And_LinkedForUnauthorizedUsersIsTrue_And_UserIsDefault_Expect_False() {
        meta.setLinkableByOtherUsers(false);
        meta.setLinkedForUnauthorizedUsers(true);
        metaRepository.save(meta);

        assertFalse(documentMenuService.hasUserAccessToDoc(meta.getId(), notDefaultUser));
    }

    @Test
    public void hasUserAccessToDoc_When_DocIsNotExist_Expect_DocumentNotExistException() {
        assertThrows(DocumentNotExistException.class,
                () -> documentMenuService.hasUserAccessToDoc(Integer.MAX_VALUE, Imcms.getUser()));
    }

    @Test
    public void getMenuItemDTO_When_AliasIsSet_Expect_AliasInLink() {
        testMenuItemDTO("test_alias");
    }

    @Test
    public void getMenuItemDTO_When_NoAlias_Expect_DocIdInLink() {
        testMenuItemDTO(null);
    }

    @Test
    public void getMenuItemDTO_When_EmptyAlias_Expect_DocIdInLink() {
	    testMenuItemDTO("");
    }

	@Test
	public void isPublicMenuItem_When_DocIsNotExist_Expect_DocumentNotExistException() {
		assertThrows(DocumentNotExistException.class,
				() -> documentMenuService.isPublicMenuItem(Integer.MAX_VALUE));
	}

	@Test
	public void getMenuItemDTO_When_AliasIsSetAndDefaultLanguageAliasDisabled_Expect_CorrectAliasInLink() {
		final String enAlias = "alias-en";
		final String svAlias = "alias-sv";
		final Integer docId = meta.getId();

		meta.setDefaultLanguageAliasEnabled(false);

		final DocumentDTO documentDTO = documentService.get(docId);
		final CommonContent commonContentEn = documentDTO.getCommonContents().stream()
				.filter(content -> content.getLanguage().getCode().equals("en")).findFirst().get();
		final CommonContent commonContentSv = documentDTO.getCommonContents().stream()
				.filter(content -> content.getLanguage().getCode().equals("en")).findFirst().get();

		commonContentEn.setAlias(enAlias);
		commonContentSv.setAlias(svAlias);

		documentService.save(documentDTO);

		final MenuItem menuItem = new MenuItem();
		menuItem.setSortOrder("1");
		menuItem.setDocumentId(docId);

		final MenuItemDTO menuItemDTO = documentMenuService.getMenuItemDTO(menuItem);

		assertThat(menuItemDTO.getDocumentId(), is(docId));
		assertThat(menuItemDTO.getType(), is(documentDTO.getType()));
		assertThat(menuItemDTO.getLink(), is("/" + svAlias));
		assertThat(menuItemDTO.getTarget(), is(documentDTO.getTarget()));
		assertThat(menuItemDTO.getDocumentStatus(), is(documentDTO.getDocumentStatus()));
		assertThat(menuItemDTO.getPublishedDate(), is(Utility.convertDateToLocalDateTime(documentDTO.getPublished().getFormattedDate())));
		assertThat(menuItemDTO.getModifiedDate(), is(Utility.convertDateToLocalDateTime(documentDTO.getModified().getFormattedDate())));
	}

	@Test
	public void getMenuItemDTO_When_AliasIsSetAndDefaultLanguageAliasEnabled_Expect_DefaultLanguageAliasInLink() {
		final String enAlias = "alias-en";
		final String svAlias = "alias-sv";
		final Integer docId = meta.getId();

		meta.setDefaultLanguageAliasEnabled(true);

		final DocumentDTO documentDTO = documentService.get(docId);
		final CommonContent commonContentEn = documentDTO.getCommonContents().stream()
				.filter(content -> content.getLanguage().getCode().equals("en")).findFirst().get();
		final CommonContent commonContentSv = documentDTO.getCommonContents().stream()
				.filter(content -> content.getLanguage().getCode().equals("en")).findFirst().get();

		commonContentEn.setAlias(enAlias);
		commonContentSv.setAlias(svAlias);

		documentService.save(documentDTO);

		final MenuItem menuItem = new MenuItem();
		menuItem.setSortOrder("1");
		menuItem.setDocumentId(docId);

		final MenuItemDTO menuItemDTO = documentMenuService.getMenuItemDTO(menuItem);

		assertThat(menuItemDTO.getDocumentId(), is(docId));
		assertThat(menuItemDTO.getType(), is(documentDTO.getType()));
		assertThat(menuItemDTO.getLink(), is("/" + svAlias));
		assertThat(menuItemDTO.getTarget(), is(documentDTO.getTarget()));
		assertThat(menuItemDTO.getDocumentStatus(), is(documentDTO.getDocumentStatus()));
		assertThat(menuItemDTO.getPublishedDate(), is(Utility.convertDateToLocalDateTime(documentDTO.getPublished().getFormattedDate())));
		assertThat(menuItemDTO.getModifiedDate(), is(Utility.convertDateToLocalDateTime(documentDTO.getModified().getFormattedDate())));
	}

	@Test
	public void getMenuItemDTO_When_NoAliasAndDefaultLanguageAliasEnabled_Expect_DocIdInLink() {
		final Integer docId = meta.getId();

		meta.setDefaultLanguageAliasEnabled(true);

		final DocumentDTO documentDTO = documentService.get(docId);
		final CommonContent commonContentEn = documentDTO.getCommonContents().stream()
				.filter(content -> content.getLanguage().getCode().equals("en")).findFirst().get();

		commonContentEn.setAlias(null);

		documentService.save(documentDTO);

		final MenuItem menuItem = new MenuItem();
		menuItem.setSortOrder("1");
		menuItem.setDocumentId(docId);

		final MenuItemDTO menuItemDTO = documentMenuService.getMenuItemDTO(menuItem);

		assertThat(menuItemDTO.getDocumentId(), is(docId));
		assertThat(menuItemDTO.getType(), is(documentDTO.getType()));
		assertThat(menuItemDTO.getLink(), is("/" + docId));
		assertThat(menuItemDTO.getTarget(), is(documentDTO.getTarget()));
		assertThat(menuItemDTO.getDocumentStatus(), is(documentDTO.getDocumentStatus()));
		assertThat(menuItemDTO.getPublishedDate(), is(Utility.convertDateToLocalDateTime(documentDTO.getPublished().getFormattedDate())));
		assertThat(menuItemDTO.getModifiedDate(), is(Utility.convertDateToLocalDateTime(documentDTO.getModified().getFormattedDate())));
	}

	@Test
	public void getMenuItemDTO_When_AliasIsEmptyAndDefaultLanguageAliasEnabled_Expect_DefaultLanguageAliasInLink() {
		final Integer docId = meta.getId();

		meta.setDefaultLanguageAliasEnabled(true);

		final DocumentDTO documentDTO = documentService.get(docId);
		final CommonContent commonContentEn = documentDTO.getCommonContents().stream()
				.filter(content -> content.getLanguage().getCode().equals("en")).findFirst().get();

		commonContentEn.setAlias("");

		documentService.save(documentDTO);

		final MenuItem menuItem = new MenuItem();
		menuItem.setSortOrder("1");
		menuItem.setDocumentId(docId);

		final MenuItemDTO menuItemDTO = documentMenuService.getMenuItemDTO(menuItem);

		assertThat(menuItemDTO.getDocumentId(), is(docId));
		assertThat(menuItemDTO.getType(), is(documentDTO.getType()));
		assertThat(menuItemDTO.getLink(), is("/" + docId));
		assertThat(menuItemDTO.getTarget(), is(documentDTO.getTarget()));
		assertThat(menuItemDTO.getDocumentStatus(), is(documentDTO.getDocumentStatus()));
		assertThat(menuItemDTO.getPublishedDate(), is(Utility.convertDateToLocalDateTime(documentDTO.getPublished().getFormattedDate())));
		assertThat(menuItemDTO.getModifiedDate(), is(Utility.convertDateToLocalDateTime(documentDTO.getModified().getFormattedDate())));
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

    @Test
    public void isPublicMenuItem_When_VisibleIsFalse_Expect_False() {
        meta.setVisible(false);
        metaRepository.save(meta);

        assertFalse(documentMenuService.isPublicMenuItem(meta.getId()));
    }

    private void testMenuItemDTO(String testAlias) {
	    final Integer docId = meta.getId();

	    final LanguageDTO language = languageDataInitializer.createData().get(0);

	    final DocumentDTO documentDTO = documentService.get(docId);
	    final CommonContent commonContent = documentDTO.getCommonContents()
			    .stream()
			    .filter(content -> content.getLanguage().getCode().equals(language.getCode()))
			    .findFirst()
			    .get();

	    commonContent.setAlias(testAlias);
	    final String headline = commonContent.getHeadline();

	    documentService.save(documentDTO);

	    final MenuItem menuItem = new MenuItem();
	    menuItem.setSortOrder("1");
	    menuItem.setDocumentId(docId);

	    final MenuItemDTO menuItemDTO = documentMenuService.getMenuItemDTO(menuItem);

	    assertThat(menuItemDTO.getDocumentId(), is(docId));
        assertThat(menuItemDTO.getType(), is(documentDTO.getType()));
        assertThat(menuItemDTO.getTitle(), is(headline));
        assertThat(menuItemDTO.getLink(), is("/" + (StringUtils.isBlank(testAlias) ? docId : testAlias)));
        assertThat(menuItemDTO.getTarget(), is(documentDTO.getTarget()));
        assertThat(menuItemDTO.getDocumentStatus(), is(documentDTO.getDocumentStatus()));
        assertThat(menuItemDTO.getPublishedDate(), is(Utility.convertDateToLocalDateTime(documentDTO.getPublished().getFormattedDate())));
        assertThat(menuItemDTO.getModifiedDate(), is(Utility.convertDateToLocalDateTime(documentDTO.getModified().getFormattedDate())));
    }
}
