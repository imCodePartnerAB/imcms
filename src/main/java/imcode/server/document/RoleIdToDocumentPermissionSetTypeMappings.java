package imcode.server.document;

import com.imcode.imcms.persistence.entity.Meta.Permission;
import imcode.server.user.RoleId;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents mapping between roles and permissions sets for a document.
 * <p/>
 * For any role which is not mapped explicitly
 * permission set type defaults to Permission.NONE.
 */
public class RoleIdToDocumentPermissionSetTypeMappings implements Serializable, Cloneable {

    volatile HashMap<RoleId, Permission> map = new HashMap<>();

    @Override
    public RoleIdToDocumentPermissionSetTypeMappings clone() {
        try {
            RoleIdToDocumentPermissionSetTypeMappings clone = (RoleIdToDocumentPermissionSetTypeMappings) super.clone();
            clone.map = new HashMap<>(map);

            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(e);
        }
    }

    /**
     * Adds or removes entry to/from this mapping.
     *
     * @param roleId                    a mapping key.
     * @param documentPermissionSetType mapping value. If null then entry is removed from this mapping.
     */
    public void setPermissionSetTypeForRole(RoleId roleId, Permission documentPermissionSetType) {
        if (null == documentPermissionSetType) {
            map.remove(roleId);
        } else {
            map.put(roleId, documentPermissionSetType);
        }
    }


    /**
     * @param roleId mapping key.
     * @return permission set type for given role.
     */
    public Permission getPermissionSetTypeForRole(RoleId roleId) {
        Permission documentPermissionSetType = map.get(roleId);
        if (null == documentPermissionSetType) {
            documentPermissionSetType = Permission.NONE;
        }
        return documentPermissionSetType;
    }

    /**
     * @return mapping entries as an array.
     */
    public Mapping[] getMappings() {
        Collection pairs = CollectionUtils.collect(map.entrySet(), new Transformer() {
            public Object transform(Object object) {
                Map.Entry entry = (Map.Entry) object;
                return new Mapping((RoleId) entry.getKey(), (Permission) entry.getValue());
            }
        });
        return (Mapping[]) pairs.toArray(new Mapping[pairs.size()]);
    }

    /**
     * Map entry (2-tuple): RoleId -> PermissionSetType
     */
    public static class Mapping {

        private final RoleId roleId;
        private final Permission documentPermissionSetType;

        public Mapping(RoleId roleId, Permission documentPermissionSetType) {
            this.roleId = roleId;
            this.documentPermissionSetType = documentPermissionSetType;
        }

        public Permission getDocumentPermissionSetType() {
            return documentPermissionSetType;
        }

        public RoleId getRoleId() {
            return roleId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            final Mapping mapping = (Mapping) o;

            return roleId.equals(mapping.roleId);

        }

        @Override
        public int hashCode() {
            return roleId.hashCode();
        }
    }
}
