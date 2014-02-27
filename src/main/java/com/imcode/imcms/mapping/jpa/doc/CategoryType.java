package com.imcode.imcms.mapping.jpa.doc;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "category_types")
public class CategoryType implements Cloneable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_type_id")
    private Integer id;

    @Column(name = "name")
    private String name;

    @Column(name = "max_choices")
    private int maxChoices;

    @Column(name = "inherited")
    private boolean inherited;

    @Column(name = "is_image_archive", nullable = false)
    private boolean imageArchive;

    public CategoryType() {
    }

    public CategoryType(String name, int maxChoices, boolean inherited, boolean imageArchive) {
        this(null, name, maxChoices, inherited, imageArchive);
    }

    public CategoryType(Integer id, String name, int maxChoices, boolean inherited, boolean imageArchive) {
        this.id = id;
        this.name = name;
        this.maxChoices = maxChoices;  // 0=single choice, 1=multi choice
        this.inherited = inherited;
        this.imageArchive = imageArchive;
    }

    @Override
    public CategoryType clone() {
        try {
            return (CategoryType) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(e);
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, maxChoices, inherited, imageArchive);
    }

    @Override
    public boolean equals(Object obj) {
        return obj == this || (obj instanceof CategoryType && equals((CategoryType) obj));
    }

    private boolean equals(CategoryType that) {
        return Objects.equals(id, that.id)
                && Objects.equals(name, that.name)
                && maxChoices == that.maxChoices
                && inherited == that.inherited
                && imageArchive == that.imageArchive;
    }


    @Override
    public String toString() {
        return com.google.common.base.Objects.toStringHelper(this)
                .add("id", id)
                .add("name", name)
                .add("maxChoices", maxChoices)
                .add("inherited", inherited)
                .add("imageArchive", imageArchive)
                .toString();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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
}