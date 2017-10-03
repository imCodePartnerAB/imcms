package com.imcode.imcms.persistence.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Entity(name = "com.imcode.imcms.persistence.entity.MenuItem")
@Table(name = "imcms_menu_item")
@Data
@NoArgsConstructor
public class MenuItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "document_id", nullable = false)
    private Integer documentId;

    @ManyToOne
    @JoinColumn(name = "menu_id")
    private Menu menu;

    @OneToMany(cascade = {CascadeType.MERGE, CascadeType.REFRESH}, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "parent_item_id")
    @OrderBy("sortOrder")
    private List<MenuItem> children;

    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder;

}
