package imcode.server.document;

import java.io.Serializable;

public class TemplateGroupDomainObject implements Comparable, Serializable {

    private int id;
    private String name;

    public TemplateGroupDomainObject( int id, String name ) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean equals( Object o ) {
        if ( this == o ) {
            return true;
        }
        if ( !( o instanceof TemplateGroupDomainObject ) ) {
            return false;
        }

        final TemplateGroupDomainObject templateGroupDomainObject = (TemplateGroupDomainObject)o;

        if ( id != templateGroupDomainObject.id ) {
            return false;
        }
        if ( name != null ? !name.equals( templateGroupDomainObject.name ) : templateGroupDomainObject.name != null ) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int result;
        result = id;
        result = 29 * result + ( name != null ? name.hashCode() : 0 );
        return result;
    }

    public int compareTo( Object o ) {
        return name.compareToIgnoreCase( ( (TemplateGroupDomainObject)o ).name );
    }

}
