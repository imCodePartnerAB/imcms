package imcode.server.document;

import org.apache.commons.collections.MultiHashMap;

/**
 * @author kreiger
 */
public class CategoryDomainObject {

    private String name;
    private int id;
    private String typeName;

    CategoryDomainObject(int id, String typeName, String name) {
        this.typeName = typeName;
        this.name = name;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public String getTypeName() {
        return typeName;
    }

    public String toString() {
        return typeName+": "+name ;
    }

}
