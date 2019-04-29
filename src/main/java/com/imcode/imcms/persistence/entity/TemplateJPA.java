package com.imcode.imcms.persistence.entity;

import com.imcode.imcms.model.Template;
import com.imcode.imcms.model.TemplateGroup;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "template")
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class TemplateJPA extends Template {

    private static final long serialVersionUID = 86902163555073323L;

    @Id
    @Column(name = "template_name", unique = true, nullable = false)
    private String name;

    @Column(name = "is_hidden", nullable = false)
    private boolean hidden;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "template_group_id")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private TemplateGroupJPA templateGroup;

    public TemplateJPA(Template templateFrom) {
        super(templateFrom);
    }

    @Override
    public void setTemplateGroup(TemplateGroup templateGroup) {
        if (templateGroup == null) {
            this.templateGroup = null;
            return;
        }
        this.templateGroup = new TemplateGroupJPA(templateGroup);
    }

}
