package com.imcode.imcms.persistence.entity;

import com.imcode.imcms.model.DocumentFile;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.*;

@Data
@Entity
@NoArgsConstructor
@ToString(callSuper = true)
@Table(name = "fileupload_docs")
@EqualsAndHashCode(callSuper = true)
public class DocumentFileJPA extends DocumentFile {

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
