package com.imcode.imcms.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "categories")
public class Category implements Cloneable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Integer id;

    @NotNull
    private String name;

    private String description = "";

    @Column(name = "image")
    private String imageUrl = "";

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.REFRESH)
    @JoinColumn(name = "category_type_id", nullable = false)
    private CategoryType type;

    public Category(String name, String description, String imageUrl, CategoryType type) {
        this(null, name, description, imageUrl, type);
    }

    @Override
    public Category clone() {
        try {
            Category clone = (Category) super.clone();
            if (type != null) clone.setType(type.clone());

            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(e);
        }
    }

}
