package imcode.server.document;

/**
 * @author kreiger
 */
public class SectionDomainObject implements Comparable {

    private int id ;
    private String name ;

    public SectionDomainObject( int id, String name ) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String toString() {
        return getName() ;
    }

    public boolean equals( Object o ) {
        if ( this == o ) return true;
        if ( !( o instanceof SectionDomainObject ) ) return false;

        final SectionDomainObject sectionDomainObject = (SectionDomainObject)o;

        if ( id != sectionDomainObject.id ) return false;

        return true;
    }

    public int hashCode() {
        return id;
    }

    public int compareTo( Object o ) {
        return name.compareToIgnoreCase( ((SectionDomainObject)o).name ) ;
    }

}
