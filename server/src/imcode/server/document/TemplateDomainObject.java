package imcode.server.document;

public class TemplateDomainObject implements Comparable {
    private int id;
    private String name;
    private String fileName;

    public TemplateDomainObject( int id, String template_name, String simple_name ) {
        this.id = id;
        this.name = simple_name;
        this.fileName = template_name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getFileName() {
        return fileName;
    }

    public boolean equals( Object o ) {
        if( this == o )
            return true;
        if( !(o instanceof TemplateDomainObject) )
            return false;

        final TemplateDomainObject templateDomainObject = (TemplateDomainObject)o;

        if( id != templateDomainObject.id )
            return false;
        if( name != null ? !name.equals( templateDomainObject.name ) : templateDomainObject.name != null )
            return false;

        return true;
    }

    public int hashCode() {
        int result;
        result = id;
        result = 29 * result + (name != null ? name.hashCode() : 0);
        return result;
    }

    public int compareTo( Object o ) {
        return name.compareToIgnoreCase( ((TemplateDomainObject)o).name ) ;
    }

}
