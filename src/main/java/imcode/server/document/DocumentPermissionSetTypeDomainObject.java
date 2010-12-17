package imcode.server.document;

import java.io.Serializable;

/**
 * Permission set type.
 * 
 * Permission set is defined per document.
 * 
 * Permission set with lower type id (FULL) is most privileged.
 *   Any permission defined in a system is automatically included into this set.
 * 
 * Permission set with higher type id (NONE) has no privileges at all.
 *   This set is always empty.
 *
 * READ permission set defines permissions only for document viewing.
 *
 * FULL, READ and NONE sets are fixed - i.e they always contain the same
 * predefined permissions for any document and can not be changed.
 *
 * RESTRICTED_1 and RESTRICTED_2 are customizable permissions sets - any permissions from FULL set
 * can be also added to those sets (separately to any document).
 * Additionally any RESTRICTED_X set automatically contains fixed subset of permissions - READ. 
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
