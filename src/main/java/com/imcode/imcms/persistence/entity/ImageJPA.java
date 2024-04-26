package com.imcode.imcms.persistence.entity;

import com.imcode.imcms.model.SpaceAround;
import imcode.util.image.Format;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@NoArgsConstructor
@Table(name = "imcms_text_doc_images")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class ImageJPA implements Serializable {

    private static final long serialVersionUID = -4028479126820231301L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "doc_id", referencedColumnName = "doc_id"),
            @JoinColumn(name = "doc_version_no", referencedColumnName = "no")
    })
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private Version version;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "language_id")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private LanguageJPA language;

    @Column(name = "`index`")
    private Integer index;

    private int width;

    private int height;

    private int border;

    private String align = "";

    @Column(name = "alt_text")
    private String alternateText = "";

    @Column(name = "description_text")
    private String descriptionText = "";

    @Column(name = "low_scr")
    private String lowResolutionUrl = "";

    private SpaceAroundJPA spaceAround = new SpaceAroundJPA();

    private String target = "";

    @Column(name = "linkurl")
    private String linkUrl = "";

    @Column(name = "imgurl")
    private String url = "";

    @Column(name = "image_name")
    private String name = "";

    private int type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Format format;

    private ImageCropRegionJPA cropRegion = new ImageCropRegionJPA();

    @Column(name = "rotate_angle", nullable = false, columnDefinition = "smallint")
    private int rotateAngle;

    @Column(name = "gen_file")
    private String generatedFilename;

    private LoopEntryRefJPA loopEntryRef;

    private int resize;

    @Column(name = "archive_image_id")
    private Long archiveImageId;

    @Column(name = "all_languages", columnDefinition = "tinyint")
    private boolean allLanguages;

    @Column(name = "in_text", columnDefinition = "tinyint")
    private boolean inText;

    @Transient
    private boolean compress;

    public ImageJPA(ImageJPA from, Version version) {
        this.version = version;
        this.language = from.language;
        this.index = from.index;
        this.format = from.format;
        this.loopEntryRef = from.loopEntryRef;
        this.width = from.width;
        this.height = from.height;
        this.border = from.border;
        this.align = from.align;
        this.alternateText = from.alternateText;
        this.descriptionText = from.descriptionText;
        this.lowResolutionUrl = from.lowResolutionUrl;
        this.target = from.target;
        this.linkUrl = from.linkUrl;
        this.url = from.url;
        this.name = from.name;
        this.type = from.type;
        this.cropRegion = from.cropRegion;
        this.rotateAngle = from.rotateAngle;
        this.generatedFilename = from.generatedFilename;
        this.resize = from.resize;
        this.archiveImageId = from.archiveImageId;
        this.allLanguages = from.allLanguages;
        this.inText = from.inText;
        this.compress = from.compress;

        setSpaceAround(from.getSpaceAround());
    }

    public SpaceAround getSpaceAround() {
        return spaceAround;
    }

    public void setSpaceAround(SpaceAround spaceAround) {
        this.spaceAround = new SpaceAroundJPA(spaceAround);
    }
}
