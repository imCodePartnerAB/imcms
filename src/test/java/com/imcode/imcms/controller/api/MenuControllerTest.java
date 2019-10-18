package com.imcode.imcms.controller.api;

import com.imcode.imcms.components.datainitializer.MenuDataInitializer;
import com.imcode.imcms.components.datainitializer.VersionDataInitializer;
import com.imcode.imcms.controller.AbstractControllerTest;
import com.imcode.imcms.domain.dto.DocumentDTO;
import com.imcode.imcms.domain.dto.MenuDTO;
import com.imcode.imcms.domain.dto.MenuItemDTO;
import com.imcode.imcms.domain.service.DocumentService;
import com.imcode.imcms.domain.service.MenuService;
import com.imcode.imcms.model.CommonContent;
import com.imcode.imcms.model.Language;
import com.imcode.imcms.model.Roles;
import com.imcode.imcms.persistence.entity.LanguageJPA;
import com.imcode.imcms.persistence.entity.Meta;
import com.imcode.imcms.persistence.repository.LanguageRepository;
import imcode.server.Imcms;
import imcode.server.user.UserDomainObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static imcode.server.ImcmsConstants.SWE_CODE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Transactional
public class MenuControllerTest extends AbstractControllerTest {

    @Autowired
    private MenuDataInitializer menuDataInitializer;

    @Autowired
    private VersionDataInitializer versionDataInitializer;

    @Autowired
    private DocumentService<DocumentDTO> documentService;

    @Autowired
    private MenuService menuService;

    @Autowired
    private LanguageRepository languageRepository;

    @BeforeEach
    public void setUp() {
        final UserDomainObject user = new UserDomainObject(1);
        user.setLanguageIso639_2("eng");
        Imcms.setUser(user);
    }

    @AfterEach
    public void cleanRepos() {
        menuDataInitializer.cleanRepositories();
        Imcms.removeUser();
    }

    @Override
    protected String controllerPath() {
        return "/menus";
    }

    @Test
    public void getMenuItems_When_MenuExistInModeSHOWINDEFAULTLANGAndHasNotNestedParam_Expect_CorrectEntitiesSizeAndEmptyChildren() throws Exception {
        final MenuDTO menu = menuDataInitializer.createData(true);
        final DocumentDTO document = documentService.get(menu.getDocId());
        document.setDisabledLanguageShowMode(Meta.DisabledLanguageShowMode.SHOW_IN_DEFAULT_LANGUAGE);
        documentService.save(document);

        assertEquals(Meta.DisabledLanguageShowMode.SHOW_IN_DEFAULT_LANGUAGE, document.getDisabledLanguageShowMode());

        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(controllerPath())
                .param("menuIndex", String.valueOf(menu.getMenuIndex()))
                .param("docId", String.valueOf(menu.getDocId()));

        List<MenuItemDTO> menuItems = menuService.getMenuItems(menu.getDocId(), menu.getMenuIndex(), Imcms.getUser().getLanguage(), true);

        performRequestBuilderExpectedOkAndJsonContentEquals(requestBuilder, asJson(menuItems));
    }

    @Test
    public void getMenuItems_When_MenuExistInModeSHOWINDEFAULTLANGAndNestedOn_Expect_MenuItemsDtosJson() throws Exception {
        final MenuDTO menu = menuDataInitializer.createData(true);
        final DocumentDTO document = documentService.get(menu.getDocId());
        document.setDisabledLanguageShowMode(Meta.DisabledLanguageShowMode.SHOW_IN_DEFAULT_LANGUAGE);
        documentService.save(document);

        assertEquals(Meta.DisabledLanguageShowMode.SHOW_IN_DEFAULT_LANGUAGE, document.getDisabledLanguageShowMode());

        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(controllerPath())
                .param("menuIndex", String.valueOf(menu.getMenuIndex()))
                .param("docId", String.valueOf(menu.getDocId()))
                .param("nested", String.valueOf(true));

        performRequestBuilderExpectedOkAndJsonContentEquals(requestBuilder, asJson(menu.getMenuItems()));
    }

    @Test
    public void getMenuItems_When_MenuExistAndMenuItemsHasModeDONOTSHOW_Expect_EmptyResult() throws Exception {
        final MenuDTO menu = menuDataInitializer.createData(true);
        final DocumentDTO document = documentService.get(menu.getMenuItems().get(0).getDocumentId());
        final DocumentDTO document2 = documentService.get(menu.getMenuItems().get(1).getDocumentId());
        DocumentDTO setUpDocDTO = setUpMenuDoc(document, SWE_CODE, Meta.DisabledLanguageShowMode.DO_NOT_SHOW);
        DocumentDTO setUpDocDTO2 = setUpMenuDoc(document2, SWE_CODE, Meta.DisabledLanguageShowMode.DO_NOT_SHOW);


        assertEquals(Meta.DisabledLanguageShowMode.DO_NOT_SHOW, setUpDocDTO.getDisabledLanguageShowMode());
        assertEquals(Meta.DisabledLanguageShowMode.DO_NOT_SHOW, setUpDocDTO2.getDisabledLanguageShowMode());

        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(controllerPath())
                .param("menuIndex", String.valueOf(menu.getMenuIndex()))
                .param("docId", String.valueOf(menu.getDocId()));

        performRequestBuilderExpectedOkAndJsonContentEquals(requestBuilder, "[]");
    }

    @Test
    public void getMenuItems_When_UserSuperAdminMenuExistInModeDONOTSHOWHasNotNested_Expect_CorrectEntitiesSizeAndEmptyChildren() throws Exception {
        final MenuDTO menu = menuDataInitializer.createData(true);
        final UserDomainObject user = new UserDomainObject(1);
        user.setLanguageIso639_2("eng");
        user.addRoleId(Roles.SUPER_ADMIN.getId());
        Imcms.setUser(user);

        assertTrue(Imcms.getUser().isSuperAdmin());

        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(controllerPath())
                .param("menuIndex", String.valueOf(menu.getMenuIndex()))
                .param("docId", String.valueOf(menu.getDocId()));

        List<MenuItemDTO> menuItems = menuService.getMenuItems(menu.getDocId(), menu.getMenuIndex(), user.getLanguage(), true);

        performRequestBuilderExpectedOkAndJsonContentEquals(requestBuilder, asJson(menuItems));
    }

// TODO: 18.10.19 check problems with CI!!! Maybe some cache? Or something data don't clear...
//    @Test
//    public void getMenuItems_When_UserSuperAdminMenuExistInModeDONOTSHOWNestedOn_Expect_CorrectEntitiesSize() throws Exception {
//        final MenuDTO menu = menuDataInitializer.createData(true);
//        final UserDomainObject user = new UserDomainObject(1);
//        user.setLanguageIso639_2("eng");
//        user.addRoleId(Roles.SUPER_ADMIN.getId());
//        Imcms.setUser(user);
//
//        assertTrue(Imcms.getUser().isSuperAdmin());
//
//        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(controllerPath())
//                .param("menuIndex", String.valueOf(menu.getMenuIndex()))
//                .param("docId", String.valueOf(menu.getDocId()))
//                .param("nested", String.valueOf(true));
//
//        performRequestBuilderExpectedOkAndJsonContentEquals(requestBuilder, asJson(menu.getMenuItems()));
//    }

    @Test
    public void getMenuItems_When_MenuMissing_Expect_Expect_EmptyArray() throws Exception {
        final MenuDTO menu = menuDataInitializer.createData(true);
        versionDataInitializer.createData(0, 1001);
        menuService.deleteByDocId(menu.getDocId());
        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(controllerPath())
                .param("menuIndex", "1")
                .param("docId", "1001");

        performRequestBuilderExpectedOkAndJsonContentEquals(requestBuilder, "[]");
    }

    @Test
    public void postMenu_When_MenuExistWithMenuItems_Expect_Ok() throws Exception {
        final MenuDTO menuDTO = menuDataInitializer.createData(true);

        final UserDomainObject user = new UserDomainObject(1);
        user.setLanguageIso639_2("eng");
        user.addRoleId(Roles.SUPER_ADMIN.getId());
        Imcms.setUser(user);

        performPostWithContentExpectOkAndJsonContentEquals(menuDTO, menuDTO);
    }

    @Test
    public void postMenu_When_MenuMissing_Expect_EmptyArray() throws Exception {
        final MenuDTO menuDTO = menuDataInitializer.createData(true);

        versionDataInitializer.createData(0, 1001);
        menuService.deleteByDocId(menuDTO.getDocId());

        final UserDomainObject user = new UserDomainObject(1);
        user.setLanguageIso639_2("eng");
        user.addRoleId(Roles.SUPER_ADMIN.getId());
        Imcms.setUser(user);

        performPostWithContentExpectOkAndJsonContentEquals(menuDTO, menuDTO);
    }

    private DocumentDTO setUpMenuDoc(DocumentDTO document, String enableLang, Meta.DisabledLanguageShowMode showMode) {
        for (CommonContent content : document.getCommonContents()) {
            Language language = content.getLanguage();
            language.setEnabled(content.getLanguage().getCode().equals(enableLang));
            languageRepository.save(new LanguageJPA(language));
        }
        document.setDisabledLanguageShowMode(showMode);
        return documentService.save(document);
    }

}
