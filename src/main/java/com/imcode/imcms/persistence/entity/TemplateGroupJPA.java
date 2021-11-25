package com.imcode.imcms.persistence.entity;

import com.imcode.imcms.model.Template;
import com.imcode.imcms.model.TemplateGroup;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "template_group")
@Getter
@Setter
@NoArgsConstructor
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class TemplateGroupJPA extends TemplateGroup {

    private static final long serialVersionUID = -8767918843878238301L;

    @Id
    @Column(name = "group_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "group_name", unique = true, nullable = false)
    private String name;

    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.MERGE, CascadeType.REFRESH})
    @JoinTable(
            name = "template_template_group",
            joinColumns = { @JoinColumn(name = "group_id") },
            inverseJoinColumns = { @JoinColumn(name = "template_id") }
    )
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private Set<TemplateJPA> templates;

    public TemplateGroupJPA(TemplateGroup from) {
        super(from);
    }

    @Override
    public Set<Template> getTemplates() {
        return (templates == null) ? Collections.EMPTY_SET : new HashSet<>(templates);
    }

    @Override
    public void setTemplates(Set<Template> templates) {
        this.templates = (templates == null) ? null :
                templates.stream().map(TemplateJPA::new).collect(Collectors.toSet());
    }
}

