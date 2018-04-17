package com.imcode.imcms.persistence.entity;

import com.imcode.imcms.model.TextDocumentTemplate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "text_docs")
@EqualsAndHashCode(callSuper=false)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class TextDocumentTemplateJPA extends TextDocumentTemplate {

    private static final long serialVersionUID = -3060069885358830246L;

    @Id
    @Column(name = "meta_id")
    private Integer docId;

    @Column(name = "template_name")
    private String templateName;

    @Column(name = "children_template_name")
    private String childrenTemplateName;

    public TextDocumentTemplateJPA(TextDocumentTemplate createFrom) {
        super(createFrom);
    }
}
