package com.imcode.imcms.persistence.entity;

import com.imcode.imcms.model.DocumentURL;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;

/**
 * URL target and frame name are legacy fields and never used.
 */
@Data
@NoArgsConstructor
@Entity
@Table(name = "imcms_url_docs")
@EqualsAndHashCode(callSuper=false)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class DocumentUrlJPA extends DocumentURL {

    private static final long serialVersionUID = 3382770541292736409L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "frame_name", nullable = false)
    private String urlFrameName;

    @Column(name = "target", nullable = false)
    private String urlTarget;

    @Column(name = "url_ref", nullable = false)
    private String url;

    @Column(name = "url_txt", nullable = false)
    private String urlText;

    @Column(name = "lang_prefix", nullable = false)
    private String urlLanguagePrefix;

    @NotNull
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "doc_id", referencedColumnName = "doc_id"),
            @JoinColumn(name = "doc_version_no", referencedColumnName = "no")
    })
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private Version version;

    public DocumentUrlJPA(DocumentURL from, Version version) {
        super(from);
        this.version = version;
    }

    @Override
    public Integer getDocId() {
        return (version == null) ? null : version.getDocId();
    }
}