package imcode.server.document;

/**
 * @author kreiger
 */
public class CategoryTypeDomainObject {

    private int id;
    private String name ;
    private int maxChoices ;

    public CategoryTypeDomainObject(int id, String name, int maxChoices) {
        this.id = id;
        this.name = name;
        this.maxChoices = maxChoices;  // 0=singel choice, 1=multi choice
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getMaxChoices() {
        return maxChoices;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CategoryTypeDomainObject)) {
            return false;
        }

        final CategoryTypeDomainObject categoryTypeDomainObject = (CategoryTypeDomainObject) o;

        if (!name.equals(categoryTypeDomainObject.name)) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        return name.hashCode();
    }

    public String toString() {
        return getName() ;
    }

}
