package imcode.server.user;

import com.imcode.imcms.domain.exception.DocumentNotExistException;
import com.imcode.imcms.persistence.entity.Meta;
import org.apache.commons.lang3.ArrayUtils;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


public class UserDomainObjectTest {

    private UserDomainObject user;
    private Meta meta;

    @Before
    public void setUp() throws Exception {
        meta = new Meta();
        meta.setId(0);

        final HashMap<Integer, Meta.Permission> roleRights = new HashMap<>();

        roleRights.put(RoleId.USERS_ID, Meta.Permission.NONE);
        roleRights.put(RoleId.USERADMIN_ID, Meta.Permission.EDIT);
        roleRights.put(RoleId.SUPERADMIN_ID, Meta.Permission.EDIT);

        meta.setRoleIdToPermissionSetIdMap(roleRights);

        user = new UserDomainObject();
    }

    @Test
    public void testUserAlwaysHasUsersRole() {
        assertTrue(user.hasRoleId(RoleId.USERS));
        assertTrue(ArrayUtils.contains(user.getRoleIds(), RoleId.USERS));
        user.removeRoleId(RoleId.USERS);
        assertTrue(user.hasRoleId(RoleId.USERS));
        assertTrue(ArrayUtils.contains(user.getRoleIds(), RoleId.USERS));
        user.setRoleIds(new RoleId[0]);
        assertTrue(user.hasRoleId(RoleId.USERS));
        assertTrue(ArrayUtils.contains(user.getRoleIds(), RoleId.USERS));
    }

    @Test
    public void hasUserAccessToDoc_When_MetaLinkedForUnauthorizedUsersAndUserNotHasRights_Expect_True() {
        meta.setLinkedForUnauthorizedUsers(true);

        assertTrue(user.hasUserAccessToDoc(meta));
    }

    @Test
    public void hasUserAccessToDoc_When_MetaNotLinkedForUnauthorizedUsersAndUserHasRights_Expect_True() {
        meta.setLinkedForUnauthorizedUsers(false);

        user.addRoleId(RoleId.USERS);
        user.addRoleId(RoleId.USERADMIN);

        assertTrue(user.hasUserAccessToDoc(meta));
    }

    @Test
    public void hasUserAccessToDoc_When_MetaNotLinkedForUnauthorizedUsersAndUserNotHasRights_Expect_False() {
        meta.setLinkedForUnauthorizedUsers(false);
        assertFalse(user.hasUserAccessToDoc(meta));
    }

    @Test(expected = DocumentNotExistException.class)
    public void hasUserAccessToDoc_When_DocIsNull_Expect_DocumentNotExistException() {
        assertTrue(user.hasUserAccessToDoc(null));
    }

}
