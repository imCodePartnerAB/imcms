package imcode.server.document;

import com.imcode.imcms.persistence.entity.Meta.Permission;
import org.apache.commons.collections4.CollectionUtils;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;

/**
 * Represents mapping between roles and permissions sets for a document.
 *
 * For any role which is not mapped explicitly
 * permission set type defaults to Permission.NONE.
 */
public class RoleIdToDocumentPermissionSetTypeMappings implements Serializable, Cloneable {

    private static final long serialVersionUID = 7473179681005654198L;

    volatile HashMap<Integer, Permission> roleIdToPermission = new HashMap<>();

    @Override
    public RoleIdToDocumentPermissionSetTypeMappings clone() {
        try {
            RoleIdToDocumentPermissionSetTypeMappings clone = (RoleIdToDocumentPermissionSetTypeMappings) super.clone();
            clone.roleIdToPermission = new HashMap<>(roleIdToPermission);

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
    public void setPermissionSetTypeForRole(Integer roleId, Permission documentPermissionSetType) {
        if (null == documentPermissionSetType) {
            roleIdToPermission.remove(roleId);
        } else {
            roleIdToPermission.put(roleId, documentPermissionSetType);
        }
    }


    /**
     * @param roleId mapping key.
     * @return permission set type for given role.
     */
    public Permission getPermissionSetTypeForRole(Integer roleId) {
        Permission documentPermissionSetType = roleIdToPermission.get(roleId);
        if (null == documentPermissionSetType) {
            documentPermissionSetType = Permission.NONE;
        }
        return documentPermissionSetType;
    }

    /**
     * @return mapping entries as an array.
     */
    public Mapping[] getMappings() {
        Collection<Mapping> pairs = CollectionUtils.collect(
                roleIdToPermission.entrySet(),
                entry -> new Mapping(entry.getKey(), entry.getValue())
        );
        return pairs.toArray(new Mapping[0]);
    }

    /**
     * Map entry (2-tuple): roleId -> PermissionSetType
     */
    public static class Mapping {

        private final Integer roleId;
        private final Permission documentPermissionSetType;

        public Mapping(Integer roleId, Permission documentPermissionSetType) {
            this.roleId = roleId;
            this.documentPermissionSetType = documentPermissionSetType;
        }

        public Permission getDocumentPermissionSetType() {
            return documentPermissionSetType;
        }

        public Integer getRoleId() {
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
