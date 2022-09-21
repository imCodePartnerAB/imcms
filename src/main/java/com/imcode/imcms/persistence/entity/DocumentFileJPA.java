package com.imcode.imcms.persistence.entity;

import com.imcode.imcms.model.DocumentFile;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@Data
@Entity
@NoArgsConstructor
@ToString(callSuper = false)
@Table(name = "fileupload_docs")
@EqualsAndHashCode(callSuper = false)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class DocumentFileJPA extends DocumentFile {

    private static final long serialVersionUID = -2994023358926079667L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "doc_id", nullable = false)
    private Integer docId;

    @Column(name = "doc_version_no")
    private int versionIndex;

    @Column(name = "filename", nullable = false)
    private String filename;

    @Column(name = "created_as_image", columnDefinition = "INT")
    private boolean createdAsImage;

    @Column(name = "mime", nullable = false)
    private String mimeType;

    @Column(name = "default_variant")
    private boolean defaultFile;

    @Column(name = "variant_name", nullable = false)
    private String fileId;

    @Transient
    private MultipartFile multipartFile;

    public DocumentFileJPA(DocumentFile from) {
        super(from);
    }

}
