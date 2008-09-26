package imcode.server.user;

import imcode.util.ShouldNotBeThrownException;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class RoleIds implements Cloneable, Serializable {

    private HashSet set = new HashSet();

    public RoleIds() {
    }

    public RoleIds(RoleId[] roleReferences) {
        set.addAll(Arrays.asList(roleReferences)) ;
    }

    public void add(RoleId roleId) {
        set.add(roleId);
    }

    protected Object clone() {
        try {
            RoleIds clone = (RoleIds) super.clone();
            clone.set = (HashSet) set.clone();
            return clone;
        } catch ( CloneNotSupportedException e ) {
            throw new ShouldNotBeThrownException(e);
        }
    }

    public void remove(RoleId roleId) {
        set.remove(roleId);
    }

    public boolean contains(RoleId roleId) {
        return set.contains(roleId);
    }

    public RoleId[] toArray() {
        return (RoleId[]) set.toArray(new RoleId[set.size()]);
    }

    public Set asSet() {
        return Collections.unmodifiableSet(set) ;
    }

    public boolean isEmpty() {
        return set.isEmpty() ;
    }
}
