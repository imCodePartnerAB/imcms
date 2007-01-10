package imcode.util;

import java.io.Serializable;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class LazilyLoadedObject<E extends LazilyLoadedObject.Copyable<E>> implements Serializable, Cloneable {

    private Loader<E> loader;
    private E object;

    public LazilyLoadedObject(Loader<E> loader) {
        this.loader = loader ;
    }

    public Object get() {
        load() ;
        return object;
    }

    public void load() {
        if (!isLoaded()) {
            E loaded = loader.load();
            object = loaded.copy() ;
            setLoaded();
        }
    }

    private boolean isLoaded() {
        return null == loader;
    }

    public Object clone() throws CloneNotSupportedException {
        LazilyLoadedObject clone = (LazilyLoadedObject) super.clone();
        if (null != object) {
            clone.object = object.copy() ;
        }
        return clone ;
    }

    public void set(E o) {
        setLoaded();
        object = o ;
    }

    private void setLoaded() {
        loader = null ;
    }

    public interface Loader<L extends Copyable> extends Serializable {
        L load() ;
    }

    public interface Copyable<C extends Copyable> extends Serializable {
        C copy() ;
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        load();
        out.defaultWriteObject();
    }
}
