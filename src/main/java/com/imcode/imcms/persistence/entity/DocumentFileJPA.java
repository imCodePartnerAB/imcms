package com.imcode.imcms.persistence.entity;

import com.imcode.imcms.model.DocumentFile;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

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
public class DocumentFileJPA extends DocumentFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "doc_id", referencedColumnName = "doc_id"),
            @JoinColumn(name = "doc_version_no", referencedColumnName = "no")
    })
    private Version version;

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
