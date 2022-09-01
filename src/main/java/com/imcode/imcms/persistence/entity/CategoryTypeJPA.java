package com.imcode.imcms.persistence.entity;

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
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "category_types")
@EqualsAndHashCode(callSuper=false)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class CategoryTypeJPA extends CategoryType {

    private static final long serialVersionUID = 2289471015338374731L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_type_id")
    private Integer id;

    @NotNull
    @Column(unique = true)
    private String name;

    @Column(name = "is_multi_select", nullable = false)
    private boolean isMultiSelect;

    @Column(name = "is_visible", nullable = false) // is visible in page info doc, default value true
    private boolean isVisible;

    @NotNull
    private boolean inherited;

    public CategoryTypeJPA(CategoryType from) {
        super(from);
    }

}