package com.imcode.imcms.persistence.entity;

import com.imcode.imcms.model.Template;
import lombok.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "template")
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class TemplateJPA extends Template {

    private static final long serialVersionUID = 86902163555073323L;

    @Id
    @Column(name = "template_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "template_name", unique = true, nullable = false)
    private String name;

    @Column(name = "is_hidden", nullable = false)
    private boolean hidden;

    public TemplateJPA(Template templateFrom) {
        super(templateFrom);
    }

}
