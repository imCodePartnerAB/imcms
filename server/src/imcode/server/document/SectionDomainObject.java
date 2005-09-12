package imcode.server.document;

import imcode.util.IdNamePair;

public class SectionDomainObject extends IdNamePair {

    public SectionDomainObject( int id, String name ) {
        super(id, name);
    }

    public String toString() {
        return getName() ;
    }

}
