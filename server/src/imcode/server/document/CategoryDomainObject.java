package imcode.server.document;

/**
 * @author kreiger
 */
public class CategoryDomainObject {

    private String name;
    private int id;
    private CategoryTypeDomainObject type;
    private String description;

    CategoryDomainObject(int id, String name, String description, CategoryTypeDomainObject type) {
        this.description = description;
        this.type = type;
        this.name = name;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public CategoryTypeDomainObject getType() {
        return type;
    }

    public String toString() {
        return type+": "+name ;
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

    public String getDescription() {
        return description ;
    }

}
