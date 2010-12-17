package imcode.server.document;

import java.io.Serializable;

/**
 * Permission set type.
 * 
 * Permission set is assigned per role per document.
 * @see imcode.server.document.RoleIdToDocumentPermissionSetTypeMappings
 * 
 * Permission set with lower type id (FULL) is most privileged.
 *   Any permission defined in a system is automatically included into this set.
 * 
 * Permission set with higher type id (NONE) has no privileges at all.
 *   This set is always empty.
 *
 * READ permission set defines permissions only for document viewing.
 *
 * FULL, READ and NONE sets are fixed - i.e each of them contains
 * predefined unmodifiable set of permissions.
 * Those sets are shared by all documents in a system.
 *
 * RESTRICTED_1 and RESTRICTED_2 are sets customizable per document,
 * however, they also contain the fixed subset of permissions - READ.
 * Additionally any document may extend a restricted set of permissions with permissions from the FULL set.
 *
 * Please note:
 * By definition RESTRICTED_2 is more restrictive than RESTRICTED_1 but this can be changed at a document level (why?).
 */
public enum DocumentPermissionSetTypeDomainObject implements Serializable {

    FULL(0),
    RESTRICTED_1(1),
    RESTRICTED_2(2),
    READ(3),
    NONE(4);
    
    private final int id ;

    private DocumentPermissionSetTypeDomainObject(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String toString() {
        return ""+id ;
    }

    public boolean isMorePrivilegedThan(DocumentPermissionSetTypeDomainObject type) {
        return id < type.id ;
    }

    public boolean isAtLeastAsPrivilegedAs(DocumentPermissionSetTypeDomainObject type) {
        return id <= type.id ;
    }

    public static DocumentPermissionSetTypeDomainObject fromInt(int id) {
        try {
            return values()[id];
        } catch(ArrayIndexOutOfBoundsException e) {
            return NONE;
        }
    }
}
