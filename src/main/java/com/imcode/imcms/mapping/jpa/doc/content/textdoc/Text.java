package com.imcode.imcms.mapping.jpa.doc.content.textdoc;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "imcms_text_doc_texts")
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Text extends TextBase {
}
