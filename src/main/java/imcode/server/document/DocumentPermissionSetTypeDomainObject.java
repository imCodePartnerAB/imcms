package imcode.server.document;

import java.io.Serializable;

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
