package com.imcode.imcms.persistence.entity;

import com.imcode.imcms.model.Template;
import com.imcode.imcms.model.TemplateGroup;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
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

    @NotFound(action = NotFoundAction.IGNORE)
    @OneToMany(mappedBy = "templateGroup", fetch = FetchType.EAGER, cascade = {CascadeType.MERGE, CascadeType.REFRESH})
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private Set<TemplateJPA> templates;

    public TemplateGroupJPA(TemplateGroup from) {
        super(from);
    }

    @Override
    public Set<Template> getTemplates() {
        Set<Template> temp = new HashSet<>();
        if (templates == null) {
            return null;
        } else {
            templates.stream().forEach(templateJPA -> {
                if (templateJPA.getTemplateGroup() == null) {
                    temp.add(templateJPA);
                } else {
                    if (this.getId().equals(templateJPA.getTemplateGroup().getId())) {
                        temp.add(templateJPA);
                    }
                }
            });
        }
        return temp;
    }

    @Override
    public void setTemplates(Set<Template> templates) {
        this.templates = (templates == null) ? null :
                templates.stream().map(TemplateJPA::new).collect(Collectors.toSet());
    }
}

