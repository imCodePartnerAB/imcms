package com.imcode.imcms;

public class TemplateGroup {
    private int id;
    private String name;

    public TemplateGroup( int id, String name ) {
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
        if( this == o )
            return true;
        if( !(o instanceof TemplateGroup) )
            return false;

        final TemplateGroup templateGroup = (TemplateGroup)o;

        if( id != templateGroup.id )
            return false;
        if( name != null ? !name.equals( templateGroup.name ) : templateGroup.name != null )
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
