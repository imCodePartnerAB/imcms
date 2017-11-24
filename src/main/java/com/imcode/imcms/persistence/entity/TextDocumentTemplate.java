package com.imcode.imcms.persistence.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Text document to template.
 */
@Data
@Entity
@Table(name = "text_docs")
public class TextDocumentTemplate {

    @Id
    @Column(name = "meta_id")
    private Integer docId;

    @Column(name = "template_name")
    private String templateName;

    // fixme: not used yet
    @Column(name = "group_id")
    private int templateGroupId;

    @Column(name = "children_template_name")
    private String childrenTemplateName;

}
