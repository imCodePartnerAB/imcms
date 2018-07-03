package com.imcode.imcms.persistence.entity;

import com.imcode.imcms.model.Phone;
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
@Table(name = "phones")
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class PhoneJPA extends Phone {

    private static final long serialVersionUID = 8996082685561864920L;

    @EmbeddedId
    private PhoneId id;

    @Column(name = "number", length = 25, nullable = false)
    private String number;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", updatable = false, insertable = false, nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "phonetype_id", nullable = false)
    private PhoneTypeJPA phoneType;

    public PhoneJPA(String number, User user, PhoneTypeJPA phoneType) {
        this.number = number;
        this.user = user;
        this.phoneType = phoneType;

        this.id = new PhoneId(user.getId());
    }

    public PhoneJPA(String number, User user, PhoneTypeJPA phoneType, PhoneId id) {
        this.number = number;
        this.user = user;
        this.phoneType = phoneType;
        this.id = id;
    }
}
