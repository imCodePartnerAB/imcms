package imcode.util;

import org.apache.commons.lang.ClassUtils;

import java.io.Serializable;

public class IdObjectPair implements Serializable {

    private final int id ;
    private final Object object;

    public IdObjectPair(int id, Object object) {
        this.id = id ;
        this.object = object;
    }

    public int getId() {
        return id;
    }

    public Object getObject() {
        return object;
    }

    public String toString() {
        return "("+ ClassUtils.getShortClassName(getClass()) +" "+id+" "+object.toString()+")" ;
    }

    public boolean equals(Object o) {
        if ( this == o ) {
            return true;
        }
        if ( o == null || getClass() != o.getClass() ) {
            return false;
        }

        final IdObjectPair that = (IdObjectPair) o;

        return id == that.id && object.equals(that.object);

    }

    public int hashCode() {
        return 29 * id + object.hashCode();
    }

}
