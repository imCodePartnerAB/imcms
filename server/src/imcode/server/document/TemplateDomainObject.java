package imcode.server.document;

public class TemplateDomainObject {
    private int id;
    private String simple_name;
    private String template_name;

    public TemplateDomainObject( int id, String template_name, String simple_name ) {
        this.id = id;
        this.simple_name = simple_name;
        this.template_name = template_name;
    }

    public int getId() {
        return id;
    }

    public String getSimple_name() {
        return simple_name;
    }

    public String getTemplate_name() {
        return template_name;
    }

    public boolean equals( Object o ) {
        if( this == o )
            return true;
        if( !(o instanceof TemplateDomainObject) )
            return false;

        final TemplateDomainObject templateDomainObject = (TemplateDomainObject)o;

        if( id != templateDomainObject.id )
            return false;
        if( simple_name != null ? !simple_name.equals( templateDomainObject.simple_name ) : templateDomainObject.simple_name != null )
            return false;

        return true;
    }

    public int hashCode() {
        int result;
        result = id;
        result = 29 * result + (simple_name != null ? simple_name.hashCode() : 0);
        return result;
    }

}
