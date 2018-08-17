package com.imcode.imcms.persistence.entity;

import com.imcode.imcms.model.Template;
import com.imcode.imcms.model.TemplateGroup;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "imcms_template_group")
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class TemplateGroupJPA extends TemplateGroup {

    private static final long serialVersionUID = -8767918843878238301L;

    @Id
    @Column(name = "group_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "group_name", unique = true, nullable = false)
    private String name;

    @ManyToMany
    @JoinTable(name = "imcms_template_group_crossref",
            joinColumns = @JoinColumn(name = "group_id"),
            inverseJoinColumns = @JoinColumn(name = "template_name"))
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private Set<TemplateJPA> templates;

    public TemplateGroupJPA(TemplateGroup from) {
        super(from);
    }

    @Override
    public Set<Template> getTemplates() {
        return (templates == null) ? null : new HashSet<>(templates);
    }

    @Override
    public void setTemplates(Set<Template> templates) {
        this.templates = templates.stream().map(TemplateJPA::new).collect(Collectors.toSet());
    }
}

