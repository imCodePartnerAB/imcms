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
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
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

    @Min(value = 0)
    @Max(value = 1)
    @Column(name = "max_choices", nullable = false)
    private int maxChoices;

    @NotNull
    private boolean inherited;

    @Column(name = "is_image_archive", nullable = false)
    private boolean imageArchive;

    public CategoryTypeJPA(String name, int maxChoices, boolean inherited, boolean imageArchive) {
        this(null, name, maxChoices, inherited, imageArchive);
    }

    public CategoryTypeJPA(CategoryType from) {
        super(from);
    }

    public CategoryTypeJPA(int id, String name, boolean multiselect, boolean inherited, boolean imageArchive) {
        this(id, name, multiselect ? 0 : 1, inherited, imageArchive);
    }

    /**
     * Checks whether category type is multi select. Can be only 0 or 1.
     * {@link #maxChoices} = 0 - is multi select
     * {@link #maxChoices} = 1 - is single select
     *
     * @return is category type contains one or more categories
     */
    @Override
    @JsonIgnore
    @Transient
    public boolean isMultiSelect() {
        return maxChoices == 0;
    }

    /**
     * {@link #maxChoices} = 0 - is multi select
     * {@link #maxChoices} = 1 - is single select
     */
    @Override
    @JsonIgnore
    @Transient
    public void setMultiSelect(boolean multiSelect) {
        this.maxChoices = (multiSelect) ? 0 : 1;
    }

}