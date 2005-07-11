package imcode.server.user;

import org.apache.commons.lang.NullArgumentException;

import java.io.Serializable;

public class PhoneNumber implements Serializable {
    private final String number;
    private final PhoneNumberType type;

    public PhoneNumber(String number, PhoneNumberType type) {
        if (null == number) {
            throw new NullArgumentException("number") ;
        } else if (null == type) {
            throw new NullArgumentException("type") ;
        }
        this.number = number;
        this.type = type;
    }

    public String getNumber() {
        return number;
    }

    public PhoneNumberType getType() {
        return type;
    }

    public boolean equals(Object o) {
        if ( this == o ) {
            return true;
        }
        if ( o == null || getClass() != o.getClass() ) {
            return false;
        }

        final PhoneNumber that = (PhoneNumber) o;

        return number.equals(that.number);

    }

    public int hashCode() {
        return number.hashCode();
    }

    public String toString() {
        return number ;
    }
}
