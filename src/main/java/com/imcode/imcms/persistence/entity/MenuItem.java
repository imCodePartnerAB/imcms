package com.imcode.imcms.persistence.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Stream;

@Entity
@Table(name = "imcms_menu_item")
@Data
@NoArgsConstructor
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class MenuItem implements Serializable {

    private static final long serialVersionUID = -8580159104614047622L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "document_id", nullable = false)
    private Integer documentId;

    @OneToMany(cascade = {CascadeType.ALL}, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "parent_item_id")
    @OrderBy("sortOrder")
    private Set<MenuItem> children;

    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder;

    @Column(name = "sort_number", nullable = false)
    private String sortNumber;

    public Set<MenuItem> getChildren() {
        if (children == null) children = new LinkedHashSet<>();
        return children;
    }

    public Stream<MenuItem> flattened() {
        return Stream.concat(
                Stream.of(this),
                children.stream().flatMap(MenuItem::flattened));
    }
}
