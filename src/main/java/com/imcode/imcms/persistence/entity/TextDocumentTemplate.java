package com.imcode.imcms.persistence.entity;

import lombok.Data;
import org.apache.commons.lang.UnhandledException;

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
public class TextDocumentTemplate implements Cloneable {

    @Id
    @Column(name = "meta_id")
    private Integer docId;

    @Column(name = "template_name")
    private String templateName;

    @Column(name = "group_id")
    private int templateGroupId;

    @Column(name = "default_template")
    private String defaultTemplateName;

    @Column(name = "default_children_template")
    private String defaultChildrenTemplate;

    @Override
    public TextDocumentTemplate clone() {
        try {
            return (TextDocumentTemplate) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new UnhandledException(e);
        }
    }

}
