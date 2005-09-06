package imcode.server.document;

public class DocumentPermissionSetTypeDomainObject {

    public static final DocumentPermissionSetTypeDomainObject FULL = new DocumentPermissionSetTypeDomainObject(0);
    public static final DocumentPermissionSetTypeDomainObject RESTRICTED_1 = new DocumentPermissionSetTypeDomainObject(1);
    public static final DocumentPermissionSetTypeDomainObject RESTRICTED_2 = new DocumentPermissionSetTypeDomainObject(2);
    public static final DocumentPermissionSetTypeDomainObject READ = new DocumentPermissionSetTypeDomainObject(3);
    public static final DocumentPermissionSetTypeDomainObject NONE = new DocumentPermissionSetTypeDomainObject(4);

    private final static DocumentPermissionSetTypeDomainObject[] ALL = new DocumentPermissionSetTypeDomainObject[] {
            FULL,
            RESTRICTED_1,
            RESTRICTED_2,
            READ,
            NONE
    };

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

    public boolean equals(Object o) {
        if ( this == o ) {
            return true;
        }
        if ( o == null || getClass() != o.getClass() ) {
            return false;
        }

        final DocumentPermissionSetTypeDomainObject that = (DocumentPermissionSetTypeDomainObject) o;

        return id == that.id;

    }

    public int hashCode() {
        return id;
    }

    public static DocumentPermissionSetTypeDomainObject fromInt(int id) {
        try {
            return ALL[id] ;
        } catch(ArrayIndexOutOfBoundsException e) {
            return NONE;
        }
    }
}
