package com.imcode.imcms.persistence.entity;

import com.imcode.imcms.model.PhoneType;
import com.imcode.imcms.model.PhoneTypeBase;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "phonetypes")
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class PhoneTypeJPA extends PhoneTypeBase {

    private static final long serialVersionUID = 3083078584948444016L;

    @Id
    @Column(name = "phonetype_id", nullable = false)
    private Integer id;

    @Column(name = "typename", length = 12, nullable = false)
    private String name;

    public PhoneTypeJPA(PhoneType from) {
        super(from);
    }
}
