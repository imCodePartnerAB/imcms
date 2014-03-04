package com.imcode.imcms.mapping.jpa.doc.content;

import com.imcode.imcms.mapping.jpa.doc.Version;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@MappedSuperclass
public abstract class VersionedDocContent {

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

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Version getVersion() {
        return version;
    }

    public void setVersion(Version contentVersion) {
        this.version = contentVersion;
    }
}
