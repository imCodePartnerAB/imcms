package com.imcode.imcms.domain.service.core;

import com.imcode.imcms.domain.dto.DocumentRoles;
import com.imcode.imcms.persistence.entity.DocumentRole;
import com.imcode.imcms.persistence.entity.Meta;
import com.imcode.imcms.persistence.entity.RoleJPA;
import com.imcode.imcms.persistence.repository.DocumentRolesRepository;
import com.imcode.imcms.persistence.repository.MetaRepository;
import imcode.server.Imcms;
import imcode.server.user.UserDomainObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DefaultDocumentRolesServiceTest {

    @Mock
    private DocumentRolesRepository documentRolesRepository;

    @Mock
    private MetaRepository metaRepository;

    @InjectMocks
    private DefaultDocumentRolesService documentRolesService;

    @Before
    public void setUp() {
        final UserDomainObject user = new UserDomainObject();
        Imcms.setUser(user);
    }

    @After
    public void tearDown() {
        Imcms.removeUser();
    }

    @Test
    public void getDocumentRoles_When_SomeRolesExist_Expect_CorrectResults() {
        final int testDocId = 1001;
        final int testUserId = 13;

        final UserDomainObject user = new UserDomainObject();
        user.setId(testUserId);

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

        when(metaRepository.findOne(testDocId)).thenReturn(testDoc);
        when(documentRolesRepository.getDocumentRolesByUserIdAndDocId(
                testUserId, testDocId
        )).thenReturn(documentRoles);

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
    public void getDocumentRoles_When_NoRolesExist_Expect_CorrectResults() {
        final int testDocId = 1001;
        final int testUserId = 13;

        final UserDomainObject user = new UserDomainObject();
        user.setId(testUserId);

        final Meta testDoc = new Meta();
        testDoc.setId(testDocId);

        final List<DocumentRole> documentRoles = Collections.emptyList();

        when(metaRepository.findOne(testDocId)).thenReturn(testDoc);
        when(documentRolesRepository.getDocumentRolesByUserIdAndDocId(
                testUserId, testDocId
        )).thenReturn(documentRoles);

        final DocumentRoles roles = documentRolesService.getDocumentRoles(testDocId, user);

        assertNotNull(roles);
        assertTrue(roles.hasNoRoles());
        assertEquals(roles.getDocument(), testDoc);
        assertTrue(roles.getPermissions().isEmpty());
        assertEquals(roles.getMostPermission(), Meta.Permission.VIEW);
    }
}
