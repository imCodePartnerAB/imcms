package com.imcode.imcms.mapping.jpa.doc.content.textdoc;

import javax.persistence.Entity;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity
@Table(name = "imcms_text_doc_images")
@NamedQuery(name = "Image.allImages",
        query = "SELECT DISTINCT i FROM Image i")

public class Image extends ImageBase {

}

