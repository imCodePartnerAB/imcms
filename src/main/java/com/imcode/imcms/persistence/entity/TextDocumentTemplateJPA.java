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
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "text_docs")
public class TextDocumentTemplateJPA extends TextDocumentTemplate {

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

    public TextDocumentTemplateJPA(TextDocumentTemplate createFrom) {
        super(createFrom);
    }
}
