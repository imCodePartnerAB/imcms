package imcode.server.document;

import com.imcode.imcms.model.CategoryType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@ToString(of = "name")
@EqualsAndHashCode(of = "name", callSuper = false)
public class CategoryTypeDomainObject extends CategoryType implements Comparable, Serializable {

    private static final long serialVersionUID = 4246000976127225850L;

    private Integer id;
    private String name;
    private boolean multiSelect;
    private boolean inherited;
    private boolean imageArchive;

    public CategoryTypeDomainObject(int id, String name, boolean multiSelect, boolean inherited) {
        this.id = id;
        this.name = name;
        this.multiSelect = multiSelect;
        this.inherited = inherited;
    }

    public CategoryTypeDomainObject(int id, String name, boolean multiSelect, boolean inherited, boolean imageArchive) {
        this.id = id;
        this.name = name;
        this.multiSelect = multiSelect;
        this.inherited = inherited;
        this.imageArchive = imageArchive;
    }

    public int compareTo(Object o) {
        return name.compareToIgnoreCase(((CategoryTypeDomainObject) o).name);
    }

    /**
     * @param maxChoices max number of choices but sensitive values are 1 (single select) or any non-1 (multi select)
     * @deprecated use {@link CategoryTypeDomainObject#setMultiSelect(boolean)}
     */
    @Deprecated
    public void setMaxChoices(int maxChoices) {
        setMultiSelect(maxChoices != 1);
    }

}
