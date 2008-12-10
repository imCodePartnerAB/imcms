package imcode.server.document;

import imcode.server.user.RoleId;
import imcode.util.ShouldNotBeThrownException;
import imcode.util.LazilyLoadedObject;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class RoleIdToDocumentPermissionSetTypeMappings implements Serializable, Cloneable, LazilyLoadedObject.Copyable {

    HashMap map = new HashMap() ;

    protected Object clone() {
        try {
            RoleIdToDocumentPermissionSetTypeMappings clone = (RoleIdToDocumentPermissionSetTypeMappings) super.clone();
            clone.map = (HashMap) map.clone() ;
            return clone;
        } catch ( CloneNotSupportedException e ) {
            throw new ShouldNotBeThrownException(e);
        }
    }

    public void setPermissionSetTypeForRole(RoleId roleId, DocumentPermissionSetTypeDomainObject documentPermissionSetType) {
        if ( null == documentPermissionSetType ) {
            map.remove(roleId) ;
        } else {
            map.put(roleId, documentPermissionSetType) ;
        }
    }

    public DocumentPermissionSetTypeDomainObject getPermissionSetTypeForRole(RoleId roleId) {
        DocumentPermissionSetTypeDomainObject documentPermissionSetType = (DocumentPermissionSetTypeDomainObject) map.get(roleId);
        if (null == documentPermissionSetType) {
            documentPermissionSetType = DocumentPermissionSetTypeDomainObject.NONE;
        }
        return documentPermissionSetType ;
    }

    public Mapping[] getMappings() {
        Collection pairs = CollectionUtils.collect(map.entrySet(), new Transformer() {
            public Object transform(Object object) {
                Map.Entry entry = (Map.Entry) object ;
                return new Mapping((RoleId)entry.getKey(), (DocumentPermissionSetTypeDomainObject)entry.getValue()) ;
            }
        });
        return (Mapping[]) pairs.toArray(new Mapping[pairs.size()]);
    }

    public LazilyLoadedObject.Copyable copy() {
        return (LazilyLoadedObject.Copyable) clone() ;
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
            if ( this == o ) {
                return true;
            }
            if ( o == null || getClass() != o.getClass() ) {
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
