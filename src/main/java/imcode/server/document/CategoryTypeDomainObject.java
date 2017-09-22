package imcode.server.document;

import imcode.server.Imcms;

import java.io.Serializable;

public class CategoryTypeDomainObject implements Comparable, Serializable, Cloneable {

    private int id;
    private String name;
    private int maxChoices;
    private boolean inherited;
    private boolean imageArchive;

    public CategoryTypeDomainObject(int id, String name, int maxChoices, boolean inherited) {
        this.id = id;
        this.name = name;
        this.maxChoices = maxChoices;  // 1=single choice, 0=multi choice
        this.inherited = inherited;
    }

    public CategoryTypeDomainObject(int id, String name, int maxChoices, boolean inherited, boolean imageArchive) {
        this.id = id;
        this.name = name;
        this.maxChoices = maxChoices;  // 1=single choice, 0=multi choice
        this.inherited = inherited;
        this.imageArchive = imageArchive;
    }


    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getMaxChoices() {
        return maxChoices;
    }

    public void setMaxChoices(int maxChoices) {
        this.maxChoices = maxChoices;
    }

    public boolean isInherited() {
        return inherited;
    }

    public void setInherited(boolean inherited) {
        this.inherited = inherited;
    }

    public boolean isImageArchive() {
        return imageArchive;
    }

    public void setImageArchive(boolean imageArchive) {
        this.imageArchive = imageArchive;
    }


    public boolean equals(Object o) {
        return this == o
                || (o instanceof CategoryTypeDomainObject &&
                ((CategoryTypeDomainObject) o).getName().toLowerCase().equals(getName().toLowerCase()));
    }

    public int hashCode() {
        return getName().hashCode();
    }

    public String toString() {
        return getName();
    }

    public int compareTo(Object o) {
        return name.compareToIgnoreCase(((CategoryTypeDomainObject) o).name);
    }

    public boolean hasImages() {
        CategoryDomainObject[] categories = Imcms.getServices().getCategoryMapper().getAllCategoriesOfType(this);
        boolean hasImages = false;
        for (int i = 0; i < categories.length; i++) {
            CategoryDomainObject category = categories[i];
            if (!"".equals(category.getImageUrl())) {
                hasImages = true;
                break;
            }
        }
        return hasImages;
    }

    public boolean isMultiselect() {
        return maxChoices == 0;
    }

    public void setMultiselect(boolean multiselect) {
        setMaxChoices(0);
    }

    public boolean isSingleSelect() {
        return !isMultiselect();
    }

    @Override
    public CategoryTypeDomainObject clone() {
        try {
            return (CategoryTypeDomainObject) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(e);
        }
    }
}