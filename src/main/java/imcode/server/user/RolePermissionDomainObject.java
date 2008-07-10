package imcode.server.user;

import java.io.Serializable;

import com.imcode.imcms.util.l10n.LocalizedMessage;

public class RolePermissionDomainObject implements Serializable {
    private int id ;
    private LocalizedMessage description ;

    RolePermissionDomainObject( int id, LocalizedMessage description ) {
        this.id = id;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public int hashCode() {
        return id ;
    }

    public boolean equals( Object obj ) {
        return obj instanceof RolePermissionDomainObject && ((RolePermissionDomainObject)obj).id == id ;
    }

    public LocalizedMessage getDescription() {
        return description;
    }
}
