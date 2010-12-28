package imcode.server.document;

import imcode.util.IdNamePair;

public class TemplateGroupDomainObject extends IdNamePair implements Cloneable {

    public TemplateGroupDomainObject( int id, String name ) {
        super(id, name);
    }

    @Override
    public TemplateGroupDomainObject clone() {
        return (TemplateGroupDomainObject)super.clone();
    }

}
