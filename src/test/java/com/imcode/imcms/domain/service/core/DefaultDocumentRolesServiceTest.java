package com.imcode.imcms.domain.service.core;

import com.imcode.imcms.domain.dto.DocumentRoles;
import com.imcode.imcms.domain.dto.ExternalRole;
import com.imcode.imcms.domain.dto.RoleDTO;
import com.imcode.imcms.domain.service.ExternalToLocalRoleLinkService;
import com.imcode.imcms.model.ExternalUser;
import com.imcode.imcms.model.Role;
import com.imcode.imcms.persistence.entity.DocumentRole;
import com.imcode.imcms.persistence.entity.Meta;
import com.imcode.imcms.persistence.entity.RoleJPA;
import com.imcode.imcms.persistence.repository.DocumentRolesRepository;
import com.imcode.imcms.persistence.repository.MetaRepository;
import imcode.server.Imcms;
import imcode.server.user.UserDomainObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static com.imcode.imcms.domain.component.azure.AzureAuthenticationProvider.EXTERNAL_AUTHENTICATOR_AZURE_AD;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class DefaultDocumentRolesServiceTest {

    @Mock
    private DocumentRolesRepository documentRolesRepository;

    @Mock
    private MetaRepository metaRepository;

    @Mock
    private ExternalToLocalRoleLinkService externalToLocalRoleLinkService;

    @InjectMocks
    private DefaultDocumentRolesService documentRolesService;

    @BeforeEach
    void setUp() {
        final UserDomainObject user = new UserDomainObject();
        Imcms.setUser(user);
    }

    @AfterEach
    void tearDown() {
        Imcms.removeUser();
    }

    @Test
    void getDocumentRoles_When_SomeRolesExist_Expect_CorrectResults() {
        final int testDocId = 1001;
        final int testUserId = 13;

        final UserDomainObject user = new UserDomainObject(testUserId);

        final Meta testDoc = new Meta();
        testDoc.setId(testDocId);

        final RoleJPA role1 = new RoleJPA();
        role1.setId(1);

        final RoleJPA role2 = new RoleJPA();
        role2.setId(2);

        final Meta.Permission permission1 = Meta.Permission.VIEW;
        final Meta.Permission permission2 = Meta.Permission.EDIT;

        final List<DocumentRole> documentRoles = Arrays.asList(
                new DocumentRole(testDoc, role1, permission1),
                new DocumentRole(testDoc, role2, permission2)
        );

	    given(metaRepository.getOne(testDocId)).willReturn(testDoc);
	    given(documentRolesRepository.getDocumentRolesByUserIdAndDocId(
			    testUserId, testDocId
	    )).willReturn(documentRoles);

        final DocumentRoles roles = documentRolesService.getDocumentRoles(testDocId, user);

        assertNotNull(roles);
        assertFalse(roles.hasNoRoles());
        assertEquals(roles.getDocument(), testDoc);
        assertTrue(roles.getPermissions().contains(permission1));
        assertTrue(roles.getPermissions().contains(permission2));
        assertEquals(roles.getPermissions().size(), 2);
        assertEquals(roles.getMostPermission(), permission2);
    }

    @Test
    void getDocumentRoles_When_NoRolesExist_Expect_CorrectResults() {
        final int testDocId = 1001;
        final int testUserId = 13;

        final UserDomainObject user = new UserDomainObject(testUserId);

        final Meta testDoc = new Meta();
        testDoc.setId(testDocId);

        final List<DocumentRole> documentRoles = Collections.emptyList();

	    given(metaRepository.getOne(testDocId)).willReturn(testDoc);
	    given(documentRolesRepository.getDocumentRolesByUserIdAndDocId(
			    testUserId, testDocId
	    )).willReturn(documentRoles);

        final DocumentRoles roles = documentRolesService.getDocumentRoles(testDocId, user);

        assertNotNull(roles);
        assertTrue(roles.hasNoRoles());
        assertEquals(roles.getDocument(), testDoc);
        assertTrue(roles.getPermissions().isEmpty());
        assertEquals(roles.getMostPermission(), Meta.Permission.NONE);
    }

    @Test
    void getDocumentRoles_When_CurrentUserIsAzureExternalAndSomeExternalRolesLinkedToLocalOnes_Expect_LinkedLocalRolesReturned() {
        final String providerId = EXTERNAL_AUTHENTICATOR_AZURE_AD;
        final int testDocId = 1001;

        final Meta testDoc = new Meta();
        testDoc.setId(testDocId);

        final ExternalRole externalRole1 = new ExternalRole();
        externalRole1.setId("external-role-id-1");
        externalRole1.setProviderId(providerId);

        final RoleJPA role1 = new RoleJPA(1, "role-1");
        final RoleJPA role2 = new RoleJPA(2, "role-2");

        final DocumentRole documentRole1 = new DocumentRole(testDoc, role1, Meta.Permission.EDIT);
        final DocumentRole documentRole2 = new DocumentRole(testDoc, role2, Meta.Permission.VIEW);

        final Set<DocumentRole> documentRoles = new HashSet<>();
        documentRoles.add(documentRole1);
        documentRoles.add(documentRole2);

        final Set<Role> linkedRoles = new HashSet<>();
        linkedRoles.add(new RoleDTO(role1));

        final ExternalRole externalRole2 = new ExternalRole();
        externalRole1.setId("external-role-id-2");
        externalRole1.setProviderId(providerId);

        final Set<ExternalRole> externalUserRoles = new HashSet<>();
        externalUserRoles.add(externalRole1);
        externalUserRoles.add(externalRole2);

        final ExternalUser user = new ExternalUser(providerId);
        user.setExternalId("external-user-id");
        user.setExternalRoles(externalUserRoles);

	    given(metaRepository.getOne(testDocId)).willReturn(testDoc);
	    given(externalToLocalRoleLinkService.toLinkedLocalRoles(any())).willReturn(linkedRoles);
        given(documentRolesRepository.findByDocument_Id(testDocId)).willReturn(documentRoles);

        final DocumentRoles roles = documentRolesService.getDocumentRoles(testDocId, user);

        assertFalse(roles.hasNoRoles());
        assertEquals(Meta.Permission.EDIT, roles.getMostPermission());
    }
}
