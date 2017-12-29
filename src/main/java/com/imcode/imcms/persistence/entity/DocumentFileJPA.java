package com.imcode.imcms.persistence.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;

@Data
@Entity
@ToString(callSuper = true)
@Table(name = "fileupload_docs")
@EqualsAndHashCode(callSuper = true)
@AssociationOverride( // is this used?
        name = "docVersion",
        joinColumns = {
                @JoinColumn(name = "doc_id", referencedColumnName = "doc_id"),
                @JoinColumn(name = "doc_version_no", referencedColumnName = "no")
        })
public class DocumentFileJPA extends VersionedContent {

    @Column(name = "filename", nullable = false)
    private String filename;

    @Column(name = "created_as_image", columnDefinition = "INT")
    private boolean createdAsImage;

    @Column(name = "mime", nullable = false)
    private String mimeType;

    @Column(name = "default_variant")
    private boolean defaultFileId;

    @Column(name = "variant_name", nullable = false)
    private String fileId;

}
