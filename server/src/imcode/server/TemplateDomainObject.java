package imcode.server;

public class TemplateDomainObject {
    private int id;
    private String name;

    /**
     Constructor.
     */
    public TemplateDomainObject( int id, String name ) {
        this.id = id;
        this.name = name;
    }

    /**
     Get template id.
     */
    public int getId() {
        return id;
    }

    /**
     Get template name.
     */
    public String getName() {
        return name;
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
}

