package com.imcode.imcms.persistence.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.io.Serializable;

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

    @Column(name = "sort_order", nullable = false)
    private String sortOrder;
}
