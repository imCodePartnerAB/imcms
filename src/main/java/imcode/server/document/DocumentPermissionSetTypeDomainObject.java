package imcode.server.document;

import java.io.Serializable;

/**
 * Permission set type.
 * <p/>
 * Permission set is assigned per role per document.
 *
 * @see imcode.server.document.RoleIdToDocumentPermissionSetTypeMappings
 * <p/>
 * Permission set with lower type id (FULL) is most privileged.
 * Any new permission defined in the system is automatically included into that set.
 * <p/>
 * Permission set with higher type id (NONE) has no privileges at all.
 * This set is always empty.
 * <p/>
 * READ permission set defines permissions only for document viewing.
 * <p/>
 * FULL, READ and NONE sets are sealed - i.e each of them contains predefined and unmodifiable permissions.
 * Those sets are shared by all documents in a system.
 * <p/>
 * RESTRICTED_1 and RESTRICTED_2 are sets customizable per document,
 * however, they also contain the fixed subset of permissions - READ.
 * Additionally any document may extend a restricted set of permissions with permissions from the FULL set.
 * <p/>
 * Please note:
 * By definition RESTRICTED_2 is more restrictive than RESTRICTED_1 but this can be changed at a document level (why?).
 */
public enum DocumentPermissionSetTypeDomainObject implements Serializable {

    FULL(0),
    RESTRICTED_1(1),
    RESTRICTED_2(2),
    READ(3),
    NONE(4);

    private final int id;

    private DocumentPermissionSetTypeDomainObject(int id) {
        this.id = id;
    }

    public static DocumentPermissionSetTypeDomainObject fromInt(int id) {
        try {
            return values()[id];
        } catch (ArrayIndexOutOfBoundsException e) {
            return NONE;
        }
    }

    public int getId() {
        return id;
    }

    public String toString() {
        return "" + id;
    }

    public String getName() {
        return super.toString().toLowerCase();
    }

    public boolean isMorePrivilegedThan(DocumentPermissionSetTypeDomainObject type) {
        return id < type.id;
    }

    public boolean isAtLeastAsPrivilegedAs(DocumentPermissionSetTypeDomainObject type) {
        return id <= type.id;
    }
}
