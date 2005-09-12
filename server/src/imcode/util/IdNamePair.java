package imcode.util;

import org.apache.commons.lang.ClassUtils;

import java.io.Serializable;

public class IdNamePair implements Comparable, Serializable {

    private final IdObjectPair idObjectPair ;

    public IdNamePair(int id, String name) {
        idObjectPair = new IdObjectPair(id, name);
    }

    public int getId() {
        return idObjectPair.getId();
    }

    public String getName() {
        return (String) idObjectPair.getObject();
    }

    public boolean equals(Object o) {
        if ( this == o ) {
            return true;
        }
        if ( o == null || getClass() != o.getClass() ) {
            return false;
        }

        final IdNamePair that = (IdNamePair) o;

        return idObjectPair.equals(that.idObjectPair);

    }

    public int hashCode() {
        return idObjectPair.hashCode();
    }

    public int compareTo( Object o ) {
        return getName().compareToIgnoreCase( ( (IdNamePair)o ).getName() );
    }

    public String toString() {
        return "("+ ClassUtils.getShortClassName(getClass())+" "+idObjectPair+")";
    }
}
