package imcode.util;

import java.io.Serializable;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class LazilyLoadedObject implements Serializable, Cloneable {

    private Loader loader;
    private Copyable object;

    public LazilyLoadedObject(Loader loader) {
        this.loader = loader ;
    }

    public Object get() {
        load() ;
        return object;
    }

    public void load() {
        if (!isLoaded()) {
            object = loader.load().copy() ;
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

    public void set(Copyable o) {
        setLoaded();
        object = o ;
    }

    private void setLoaded() {
        loader = null ;
    }

    public interface Loader extends Serializable {
        Copyable load() ;
    }

    public interface Copyable extends Serializable {
        Copyable copy() ;
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        load();
        out.defaultWriteObject();
    }
}
