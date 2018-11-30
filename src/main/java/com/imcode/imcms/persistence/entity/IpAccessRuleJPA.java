package com.imcode.imcms.persistence.entity;

import com.imcode.imcms.model.IpAccessRule;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "ip_access_rules")
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false, exclude = {"role", "user"})
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class IpAccessRuleJPA extends IpAccessRule {
    private static final long serialVersionUID = 4144107131112953963L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rule_id")
    private Integer Id;

    @NotNull
    @Column(name = "enabled", columnDefinition = "bit")
    private boolean isEnabled;

    @NotNull
    @Column(name = "restricted", columnDefinition = "bit")
    private boolean isRestricted;

    @Column(name = "ip_range", length = 80)
    private String ipRange;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id")
    private RoleJPA role;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public IpAccessRuleJPA(IpAccessRule from) {
        super(from);
    }
}
