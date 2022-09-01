package com.imcode.imcms.persistence.entity;

import com.imcode.imcms.model.Category;
import com.imcode.imcms.model.CategoryType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "categories",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"name", "category_type_id"})})
@EqualsAndHashCode(callSuper = false)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class CategoryJPA extends Category {

    private static final long serialVersionUID = 8984841419157038574L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Integer id;

    @NotNull
    private String name;

    private String description;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.REFRESH)
    @JoinColumn(name = "category_type_id", nullable = false)
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private CategoryTypeJPA type;

    public CategoryJPA(Category from) {
        super(from);
    }

    @Override
    public void setType(CategoryType type) {
        this.type = new CategoryTypeJPA(type);
    }


}
