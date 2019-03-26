package imcode.server.document;

import com.imcode.imcms.model.Category;
import com.imcode.imcms.model.CategoryType;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper=false)
public class CategoryDomainObject extends Category implements Comparable<CategoryDomainObject>, Serializable {

    private static final long serialVersionUID = -9154498328953229889L;
    private Integer id;
    private String name;
    private String description;
    private CategoryType type;

    public CategoryDomainObject(int id, String name, String description, CategoryTypeDomainObject type) {
        this.id = id;
        this.description = description;
        this.type = type;
        this.name = name;
    }

    @Override
    public int compareTo(CategoryDomainObject category) {
        return name.compareToIgnoreCase(category.name);
    }

}
