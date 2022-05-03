package imcode.server.user;

import com.imcode.imcms.model.Roles;
import com.imcode.imcms.persistence.entity.Meta;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.AfterEach;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


public class UserDomainObjectTest {

    private UserDomainObject user;
    private Meta meta;

    private final int roleEditId = 10;

    @Before
    @AfterEach
    public void setUp() {
        meta = new Meta();
        meta.setId(0);

        final HashMap<Integer, Meta.Permission> roleRights = new HashMap<>();
        roleRights.put(Roles.USER.getId(), Meta.Permission.NONE);
        roleRights.put(Roles.SUPER_ADMIN.getId(), Meta.Permission.EDIT);
        roleRights.put(roleEditId, Meta.Permission.EDIT);
        meta.setRoleIdToPermission(roleRights);

        user = new UserDomainObject();
        user.setRoleIds(Collections.singleton(Roles.USER.getId()));
    }

    @Test
    public void testUserAlwaysHasUsersRole() {
        assertTrue(user.hasRoleId(Roles.USER.getId()));
        assertTrue(user.getRoleIds().contains(Roles.USER.getId()));

        user.removeRoleId(Roles.USER.getId());
        assertTrue(user.hasRoleId(Roles.USER.getId()));
        assertTrue(user.getRoleIds().contains(Roles.USER.getId()));

        final Set<Integer> roleIds = new HashSet<>(1);
        roleIds.add(0);

        user.setRoleIds(roleIds);
        assertTrue(user.hasRoleId(Roles.USER.getId()));
        assertTrue(user.getRoleIds().contains(Roles.USER.getId()));
    }

    @Test
    public void hasUserAccessToDoc_When_MetaIsVisible_And_UserIsDefault_Expect_True() {
        meta.setVisible(true);
        assertTrue(user.hasUserAccessToDoc(meta));
    }

    @Test
    public void hasUserAccessToDoc_When_MetaIsNotVisible_And_UserIsDefault_Expect_False() {
        meta.setVisible(false);
        assertFalse(user.hasUserAccessToDoc(meta));
    }

    @Test
    public void hasUserAccessToDoc_When_MetaIsNotVisible_And_UserHasNotPermission_Expect_False() {
        meta.setVisible(false);

        int documentHasNotThisRole = 100;
        user.setRoleIds(Collections.singleton(documentHasNotThisRole));

        assertFalse(user.hasUserAccessToDoc(meta));
    }

    @Test
    public void hasUserAccessToDoc_When_MetaIsNotVisible_And_UserHasPermission_Expect_True() {
        meta.setVisible(false);

        user.setRoleIds(Collections.singleton(roleEditId));

        assertTrue(user.hasUserAccessToDoc(meta));
    }
}
