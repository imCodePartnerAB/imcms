package imcode.server.document;

import org.apache.commons.collections.MultiHashMap;

/**
 * @author kreiger
 */
public class CategoryDomainObject {

    private String name;
    private int id;
    private String typeName;

    CategoryDomainObject(int id, String name, String typeName) {
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

    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CategoryDomainObject)) return false;

        final CategoryDomainObject categoryDomainObject = (CategoryDomainObject) o;

        if (id != categoryDomainObject.id) return false;

        return true;
    }

    public int hashCode() {
        return id;
    }

}
