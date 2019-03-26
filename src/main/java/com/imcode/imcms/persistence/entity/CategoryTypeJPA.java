package com.imcode.imcms.persistence.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.imcode.imcms.model.CategoryType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

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

    @NotNull
    private boolean inherited;

    public CategoryTypeJPA(CategoryType from) {
        super(from);
    }

    @Override
    @JsonIgnore
    @Transient
    public boolean isMultiSelect() {
        return isMultiSelect;
    }

    @Override
    @JsonIgnore
    @Transient
    public void setMultiSelect(boolean multiSelect) {
        this.isMultiSelect = multiSelect;
    }

}