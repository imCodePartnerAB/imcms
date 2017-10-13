package com.imcode.imcms.persistence.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "template")
@Data
@NoArgsConstructor
public class Template {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "template_name", unique = true, nullable = false)
    private String name;

    @Column(name = "is_hidden", nullable = false)
    private boolean hidden;

}
