package com.imcode.imcms.domain.service.core;

import com.imcode.imcms.domain.exception.DocumentNotExistException;
import com.imcode.imcms.mapping.jpa.doc.Meta;
import com.imcode.imcms.mapping.jpa.doc.MetaRepository;
import imcode.server.user.RoleId;
import imcode.server.user.UserDomainObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.HashMap;

import static imcode.server.document.DocumentPermissionSetTypeDomainObject.FULL;
import static imcode.server.document.DocumentPermissionSetTypeDomainObject.NONE;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class MetaServiceTest {

    private static final int META_ID_EXISTED = 1;
    private static final int META_ID_NOT_EXISTED = 0;

    @Mock
    private MetaRepository metaRepository;

    @InjectMocks
    private MetaService metaService;

    private UserDomainObject user;
    private Meta meta;

    @Before
    public void setUp() throws Exception {
        meta = new Meta();
        meta.setId(META_ID_EXISTED);

        final HashMap<Integer, Integer> roleRights = new HashMap<>();

        roleRights.put(RoleId.USERS_ID, NONE.getId());
        roleRights.put(RoleId.USERADMIN_ID, FULL.getId());
        roleRights.put(RoleId.SUPERADMIN_ID, FULL.getId());

        meta.setRoleIdToPermissionSetIdMap(roleRights);

        given(metaRepository.findOne(META_ID_EXISTED)).willReturn(meta);
        given(metaRepository.findOne(META_ID_NOT_EXISTED)).willReturn(null);

        user = new UserDomainObject();
    }

    @Test
    public void hasUserAccessToDoc_When_MetaLinkedForUnauthorizedUsersAndUserNotHasRights_Expect_True() {
        meta.setLinkedForUnauthorizedUsers(true);


        assertTrue(metaService.hasUserAccessToDoc(META_ID_EXISTED, user));
    }

    @Test
    public void hasUserAccessToDoc_When_MetaNotLinkedForUnauthorizedUsersAndUserHasRights_Expect_True() {
        meta.setLinkedForUnauthorizedUsers(false);

        user.addRoleId(RoleId.USERS);
        user.addRoleId(RoleId.USERADMIN);

        assertTrue(metaService.hasUserAccessToDoc(META_ID_EXISTED, user));
    }

    @Test
    public void hasUserAccessToDoc_When_MetaNotLinkedForUnauthorizedUsersAndUserNotHasRights_Expect_False() {
        meta.setLinkedForUnauthorizedUsers(false);
        assertFalse(metaService.hasUserAccessToDoc(META_ID_EXISTED, user));
    }

    @Test(expected = DocumentNotExistException.class)
    public void hasUserAccessToDoc_When_DocNotExist_Expect_DocumentNotExistException() {
        assertTrue(metaService.hasUserAccessToDoc(META_ID_NOT_EXISTED, user));
    }

}
