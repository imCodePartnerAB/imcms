package com.imcode.imcms.persistence.entity;

import com.imcode.imcms.model.UserProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user_properties")
@EqualsAndHashCode(callSuper = true)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class UserPropertyJPA extends UserProperty {

    private static final long serialVersionUID = -7934920021551439965L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(name = "key_name", nullable = false)
    private String keyName;

    @Column(name = "value", nullable = false)
    private String value;

    public UserPropertyJPA(UserProperty from) {
        super(from);
    }
}
