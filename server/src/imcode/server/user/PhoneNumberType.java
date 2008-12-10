package imcode.server.user;

import imcode.util.IdLocalizedNamePair;
import com.imcode.imcms.util.l10n.LocalizedMessage;

public class PhoneNumberType extends IdLocalizedNamePair {
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

    private PhoneNumberType(int id, LocalizedMessage name) {
        super(id, name);
    }

    public static PhoneNumberType[] getAllPhoneNumberTypes() {
        return ALL_PHONE_TYPES;
    }

    public static PhoneNumberType getPhoneNumberTypeById(int phoneTypeId) {
        return ALL_PHONE_TYPES[phoneTypeId] ;
    }

}
