package imcode.server.document;

import imcode.util.LocalizedMessage;

public class DocumentTypeDomainObject {
    private final int id ;
    private final LocalizedMessage name ;

    public DocumentTypeDomainObject( int id, LocalizedMessage name ) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public LocalizedMessage getName() {
        return name;
    }

    public String toString() {
        return ""+id ;
    }
}
