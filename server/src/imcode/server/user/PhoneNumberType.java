package imcode.server.user;

import imcode.util.LocalizedMessage;

import java.io.Serializable;

public class PhoneNumberType implements Serializable {
    public final static PhoneNumberType OTHER = new PhoneNumberType(0, new LocalizedMessage("phone_type/name/other"));
    public final static PhoneNumberType HOME = new PhoneNumberType(1, new LocalizedMessage("phone_type/name/home"));
    public final static PhoneNumberType WORK = new PhoneNumberType(2, new LocalizedMessage("phone_type/name/work"));
    public final static PhoneNumberType MOBILE = new PhoneNumberType(3, new LocalizedMessage("phone_type/name/mobile"));
    public final static PhoneNumberType FAX = new PhoneNumberType(4, new LocalizedMessage("phone_type/name/fax"));

    private final static PhoneNumberType[] ALL_PHONE_TYPES = {
            OTHER,
            HOME,
            WORK,
            MOBILE,
            FAX,
    };

    private final int id;
    private final LocalizedMessage name;

    public PhoneNumberType(int id, LocalizedMessage name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public LocalizedMessage getName() {
        return name;
    }

    public static PhoneNumberType[] getAllPhoneNumberTypes() {
        return ALL_PHONE_TYPES;
    }

    public static PhoneNumberType getPhoneNumberTypeById(int phoneTypeId) {
        return ALL_PHONE_TYPES[phoneTypeId] ;
    }

    public boolean equals(Object o) {
        if ( this == o ) {
            return true;
        }
        if ( o == null || getClass() != o.getClass() ) {
            return false;
        }

        final PhoneNumberType that = (PhoneNumberType) o;

        return id == that.id;

    }

    public int hashCode() {
        return id;
    }
}
