package imcode.server.document;

/**
 * @author kreiger
 */
public class CategoryTypeDomainObject {

    private String name ;
    private int maxChoices ;

    public CategoryTypeDomainObject(String name, int maxChoices) {
        this.name = name;
        this.maxChoices = maxChoices;
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
