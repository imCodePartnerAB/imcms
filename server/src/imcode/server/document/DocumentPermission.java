package imcode.server.document;

import org.apache.commons.lang.NullArgumentException;

import java.io.Serializable;

public class DocumentPermission implements Serializable {

    private final String name ;

    public DocumentPermission( String name ) {
        if (null == name) {
            throw new NullArgumentException( "name" ) ;
        }
        this.name = name;
    }

    public boolean equals( Object o ) {
        if ( this == o ) {
            return true;
        }
        if ( !( o instanceof DocumentPermission ) ) {
            return false;
        }

        final DocumentPermission documentPermission = (DocumentPermission)o;

        return name.equals( documentPermission.name ) ;
    }

    public int hashCode() {
        return name.hashCode() ;
    }
}
