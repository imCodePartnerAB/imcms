package com.imcode.imcms.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "template")
@AllArgsConstructor
@NoArgsConstructor
public class TemplateJPA extends Template {

    @Id
    @Column(name = "template_name", unique = true, nullable = false)
    private String name;

    @Column(name = "is_hidden", nullable = false)
    private boolean hidden;

    public TemplateJPA(Template templateFrom) {
        super(templateFrom);
    }

}
