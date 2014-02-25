package com.imcode.imcms.mapping.orm;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "categories")
public class DocCategory implements Cloneable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Integer id;

    private String name;

    private String description = "";

    @Column(name = "image")
    private String imageUrl = "";

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.REFRESH)
    @JoinColumn(name = "category_type_id", nullable = false)
    private DocCategoryType type;

    public DocCategory() {
    }

    public DocCategory(String name, String description, String imageUrl, DocCategoryType type) {
        this(null, name, description, imageUrl, type);
    }

    public DocCategory(Integer id, String name, String description, String imageUrl, DocCategoryType type) {
        this.id = id;
        this.description = description;
        this.type = type;
        this.name = name;
        this.imageUrl = imageUrl;
    }

    @Override
    public DocCategory clone() {
        try {
            DocCategory clone = (DocCategory) super.clone();
            if (type != null) clone.setType(type.clone());

            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(e);
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, description, type, name, imageUrl);
    }

    @Override
    public boolean equals(Object obj) {
        return obj == this || (obj instanceof DocCategory && equals((DocCategory) obj));
    }

    private boolean equals(DocCategory that) {
        return Objects.equals(id, that.id)
                && Objects.equals(description, that.description)
                && Objects.equals(type, that.type)
                && Objects.equals(name, that.name)
                && Objects.equals(imageUrl, that.imageUrl);


    }

    @Override
    public String toString() {
        return com.google.common.base.Objects.toStringHelper(this)
                .add("id", id)
                .add("description", description)
                .add("type", type)
                .add("name", name)
                .add("imageUrl", imageUrl)
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public DocCategoryType getType() {
        return type;
    }

    public void setType(DocCategoryType type) {
        this.type = type;
    }
}
