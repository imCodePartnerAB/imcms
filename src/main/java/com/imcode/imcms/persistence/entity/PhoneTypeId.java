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
public class PhoneTypeId implements Serializable {

    private static final long serialVersionUID = 4593790231880735313L;

    @Column(name = "phonetype_id", nullable = false)
    private int phoneTypeId;

    @Column(name = "lang_id", nullable = false)
    private int langId;

    public PhoneTypeId(int phoneTypeId, int langId) {
        this.phoneTypeId = phoneTypeId;
        this.langId = langId;
    }
}
