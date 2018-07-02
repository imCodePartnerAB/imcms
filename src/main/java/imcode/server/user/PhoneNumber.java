package imcode.server.user;

import lombok.Data;

import java.io.Serializable;
import java.util.Objects;

@Data
public class PhoneNumber implements Serializable {

    private static final long serialVersionUID = 3740907500528698968L;

    private final String number;
    private final PhoneNumberType type;

    public PhoneNumber(String number, PhoneNumberType type) {
        Objects.requireNonNull(number);
        Objects.requireNonNull(type);

        this.number = number;
        this.type = type;
    }

}
