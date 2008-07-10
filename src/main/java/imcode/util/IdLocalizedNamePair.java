package imcode.util;

import java.io.Serializable;

import com.imcode.imcms.util.l10n.LocalizedMessage;

public class IdLocalizedNamePair implements Serializable {

    private final IdObjectPair idObjectPair ;

    public IdLocalizedNamePair(int id, LocalizedMessage name) {
        idObjectPair = new IdObjectPair(id, name);
    }

    public int getId() {
        return idObjectPair.getId();
    }

    public LocalizedMessage getName() {
        return (LocalizedMessage) idObjectPair.getObject();
    }

    public boolean equals(Object o) {
        if ( this == o ) {
            return true;
        }
        if ( o == null || getClass() != o.getClass() ) {
            return false;
        }

        final IdLocalizedNamePair that = (IdLocalizedNamePair) o;

        return idObjectPair.equals(that.idObjectPair);

    }

    public int hashCode() {
        return idObjectPair.hashCode();
    }
}
