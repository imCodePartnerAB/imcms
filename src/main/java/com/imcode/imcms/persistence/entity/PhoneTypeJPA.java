package com.imcode.imcms.persistence.entity;

import com.imcode.imcms.model.PhoneType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "phonetypes")
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class PhoneTypeJPA extends PhoneType {

    private static final long serialVersionUID = 3083078584948444016L;

    @EmbeddedId
    private PhoneTypeId id;

    @Column(name = "phonetype_id", nullable = false, insertable = false, updatable = false)
    private int phoneTypeId;

    @Column(name = "typename", length = 12, nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lang_id", insertable = false, updatable = false)
    private LanguageJPA language;


    public PhoneTypeJPA(int phoneTypeId, String name, LanguageJPA language) {
        this.phoneTypeId = phoneTypeId;
        this.name = name;
        this.language = language;

        this.id = new PhoneTypeId(phoneTypeId, language.getId());
    }
}
