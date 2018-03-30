package com.imcode.imcms.persistence.entity;

import com.imcode.imcms.model.Category;
import com.imcode.imcms.model.CategoryType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "categories")
@EqualsAndHashCode(callSuper=false)
public class CategoryJPA extends Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Integer id;

    @NotNull
    private String name;

    private String description;

    @Column(name = "image", nullable = false)
    private String imageUrl;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.REFRESH)
    @JoinColumn(name = "category_type_id", nullable = false)
    private CategoryTypeJPA type;

    public CategoryJPA(String name, String description, String imageUrl, CategoryTypeJPA type) {
        this(null, name, description, imageUrl, type);
    }

    public CategoryJPA(Category from) {
        super(from);
    }

    @Override
    public void setImageUrl(String imageUrl) {
        this.imageUrl = (imageUrl == null) ? "" : imageUrl;
    }

    @Override
    public void setType(CategoryType type) {
        this.type = new CategoryTypeJPA(type);
    }


}
