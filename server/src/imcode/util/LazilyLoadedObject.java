package imcode.util;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class LazilyLoadedObject<E extends LazilyLoadedObject.Copyable<E>> implements Serializable, Cloneable {

    private Loader<E> loader;
    private E object;

    public LazilyLoadedObject(Loader<E> loader) {
        this.loader = loader;
    }

    public E get() {
        load();
        return object;
    }

    public void load() {
        if (!isLoaded()) {
            E loaded = loader.load();
            object = loaded.copy();
            setLoaded();
        }
    }

    private boolean isLoaded() {
        return null == loader;
    }

    public LazilyLoadedObject<E> clone() throws CloneNotSupportedException {
        LazilyLoadedObject<E> clone = (LazilyLoadedObject<E>) super.clone();
        if (null != object) {
            clone.object = object.copy();
        }
        return clone;
    }

    public void set(E o) {
        setLoaded();
        object = o;
    }

    private void setLoaded() {
        loader = null;
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        load();
        out.defaultWriteObject();
    }

    public interface Loader<L extends Copyable> extends Serializable {
        L load();
    }

    public interface Copyable<C extends Copyable> extends Serializable {
        C copy();
    }
}
