package com.imcode.imcms.persistence.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "imcms_template_group")
@Data
public class TemplateGroupJpa extends TemplateGroup<TemplateJPA> {

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
    private List<TemplateJPA> templates;

}
