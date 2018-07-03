package com.imcode.imcms.persistence.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import java.io.Serializable;

@Data
@Embeddable
@NoArgsConstructor
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class PhoneId implements Serializable {

    private static final long serialVersionUID = -2064760434043660193L;

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "phone_id", nullable = false)
    private int phoneId;

    @Column(name = "user_id", nullable = false)
    private int userId;

    public PhoneId(int userId) {
        this.userId = userId;
    }
}
