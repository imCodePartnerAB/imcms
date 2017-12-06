package com.imcode.imcms.persistence.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.imcode.imcms.model.CategoryType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "category_types")
public class CategoryTypeJPA extends CategoryType<CategoryJPA> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_type_id")
    private Integer id;

    @NotNull
    private String name;

    @Min(value = 0)
    @Max(value = 1)
    @Column(name = "max_choices", nullable = false)
    private int maxChoices;

    @NotNull
    private boolean inherited;

    @Column(name = "is_image_archive", nullable = false)
    private boolean imageArchive;

    @OneToMany(mappedBy = "type", fetch = FetchType.LAZY)
    private List<CategoryJPA> categories;

    public CategoryTypeJPA(String name, int maxChoices, boolean inherited, boolean imageArchive) {
        this(null, name, maxChoices, inherited, imageArchive);
    }

    public CategoryTypeJPA(Integer id, String name, int maxChoices, boolean inherited, boolean imageArchive) {
        this(id, name, maxChoices, inherited, imageArchive, null);
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