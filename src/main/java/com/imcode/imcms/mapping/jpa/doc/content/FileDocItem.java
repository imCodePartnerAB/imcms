/**
 *
 */
package com.imcode.imcms.mapping.jpa.doc.content;

import javax.persistence.*;

//ORDER BY default_variant DESC, variant_name

@Entity
@Table(name = "fileupload_docs")
@AssociationOverride(
        name = "docVersion",
        joinColumns = {
                @JoinColumn(name = "meta_id", referencedColumnName = "doc_id"),
                @JoinColumn(name = "doc_version_no", referencedColumnName = "no")
        })
public class FileDocItem extends VersionedDocContent {

    @Column(name = "filename")
    private String filename;

    @Column(name = "created_as_image")
    private Boolean createdAsImage;

    @Column(name = "mime")
    private String mimeType;

    @Column(name = "default_variant")
    private Boolean defaultFileId;

    @Column(name = "variant_name")
    private String fileId;

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public Boolean getCreatedAsImage() {
        return createdAsImage;
    }

    public void setCreatedAsImage(Boolean createdAsImage) {
        this.createdAsImage = createdAsImage;
    }

    public Boolean isDefaultFileId() {
        return defaultFileId;
    }

    public void setDefaultFileId(Boolean defaultFileId) {
        this.defaultFileId = defaultFileId;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }
}