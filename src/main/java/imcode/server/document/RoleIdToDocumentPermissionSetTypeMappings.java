package imcode.server.document;

import imcode.server.user.RoleId;
import imcode.util.ShouldNotBeThrownException;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;

/**
 * Represents mapping between roles and permissions sets for a document.
 * 
 * For any role which is not mapped explicitly
 * permission set type defaults to DocumentPermissionSetTypeDomainObject.NONE.
 */
public class RoleIdToDocumentPermissionSetTypeMappings implements Serializable, Cloneable {

    HashMap<RoleId, DocumentPermissionSetTypeDomainObject> map = new HashMap<RoleId, DocumentPermissionSetTypeDomainObject>() ;

    @Override
    public RoleIdToDocumentPermissionSetTypeMappings clone() {
        try {
            RoleIdToDocumentPermissionSetTypeMappings clone = (RoleIdToDocumentPermissionSetTypeMappings) super.clone();
            clone.map = (HashMap) map.clone() ;
            return clone;
        } catch ( CloneNotSupportedException e ) {
            throw new ShouldNotBeThrownException(e);
        }
    }

    /**
     * Adds or removes entry to/from this mapping.
     * 
     * @param roleId a mapping key.
     * @param documentPermissionSetType mapping value. If null then entry is removed from this mapping.
     */
    public void setPermissionSetTypeForRole(RoleId roleId, DocumentPermissionSetTypeDomainObject documentPermissionSetType) {
        if ( null == documentPermissionSetType ) {
            map.remove(roleId) ;
        } else {
            map.put(roleId, documentPermissionSetType) ;
        }
    }

    /**
     * @param roleId mapping key.
     * @return permission set type for given role.
     */
    public DocumentPermissionSetTypeDomainObject getPermissionSetTypeForRole(RoleId roleId) {
        DocumentPermissionSetTypeDomainObject documentPermissionSetType = map.get(roleId);
        if (null == documentPermissionSetType) {
            documentPermissionSetType = DocumentPermissionSetTypeDomainObject.NONE;
        }
        return documentPermissionSetType ;
    }

    /**
     * @return mapping entries as an array.
     */
    public Mapping[] getMappings() {
        Collection pairs = CollectionUtils.collect(map.entrySet(), new Transformer() {
            public Object transform(Object object) {
                Map.Entry entry = (Map.Entry) object ;
                return new Mapping((RoleId)entry.getKey(), (DocumentPermissionSetTypeDomainObject)entry.getValue()) ;
            }
        });
        return (Mapping[]) pairs.toArray(new Mapping[pairs.size()]);
    }

    /**
     * Map entry (2-tuple): RoleId -> PermissionSetType
     */
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

        @Override
        public boolean equals(Object o) {
            if ( this == o ) {
                return true;
            }
            if ( o == null || getClass() != o.getClass() ) {
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
