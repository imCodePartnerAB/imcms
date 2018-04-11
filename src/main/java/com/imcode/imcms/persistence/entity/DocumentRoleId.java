package com.imcode.imcms.persistence.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Data
@Embeddable
@NoArgsConstructor
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class DocumentRoleId implements Serializable {

    private static final long serialVersionUID = 5654696778234465546L;

    @Column(name = "meta_id", nullable = false)
    private int documentId;

    @Column(name = "role_id", nullable = false)
    private int roleId;

    DocumentRoleId(int documentId, int roleId) {
        this.documentId = documentId;
        this.roleId = roleId;
    }
}
