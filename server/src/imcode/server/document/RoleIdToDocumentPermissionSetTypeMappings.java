package imcode.server.document;

import imcode.server.user.RoleId;
import imcode.util.LazilyLoadedObject;
import imcode.util.ShouldNotBeThrownException;
import org.apache.commons.collections4.CollectionUtils;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Optional;

public class RoleIdToDocumentPermissionSetTypeMappings implements Serializable, Cloneable, LazilyLoadedObject.Copyable<RoleIdToDocumentPermissionSetTypeMappings> {

    private HashMap<RoleId, DocumentPermissionSetTypeDomainObject> map = new HashMap<>();

    protected RoleIdToDocumentPermissionSetTypeMappings clone() {
        try {
            RoleIdToDocumentPermissionSetTypeMappings clone = (RoleIdToDocumentPermissionSetTypeMappings) super.clone();
            clone.map = (HashMap<RoleId, DocumentPermissionSetTypeDomainObject>) map.clone();
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new ShouldNotBeThrownException(e);
        }
    }

    public void setPermissionSetTypeForRole(RoleId roleId, DocumentPermissionSetTypeDomainObject documentPermissionSetType) {
        if (null == documentPermissionSetType) {
            map.remove(roleId);
        } else {
            map.put(roleId, documentPermissionSetType);
        }
    }

    public DocumentPermissionSetTypeDomainObject getPermissionSetTypeForRole(RoleId roleId) {
        return Optional.ofNullable(map.get(roleId))
                .orElse(DocumentPermissionSetTypeDomainObject.NONE);
    }

    public Mapping[] getMappings() {
        return CollectionUtils.collect(map.entrySet(), entry -> new Mapping(entry.getKey(), entry.getValue()))
                .toArray(new Mapping[0]);
    }

    public RoleIdToDocumentPermissionSetTypeMappings copy() {
        return clone();
    }

    public static class Mapping {

        private final RoleId roleId;
        private final DocumentPermissionSetTypeDomainObject documentPermissionSetType;

        public Mapping(RoleId roleId,
                       DocumentPermissionSetTypeDomainObject documentPermissionSetType) {
            this.roleId = roleId;
            this.documentPermissionSetType = documentPermissionSetType;
        }

        public DocumentPermissionSetTypeDomainObject getDocumentPermissionSetType() {
            return documentPermissionSetType;
        }

        public RoleId getRoleId() {
            return roleId;
        }

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

        public int hashCode() {
            return roleId.hashCode();
        }
    }
}
