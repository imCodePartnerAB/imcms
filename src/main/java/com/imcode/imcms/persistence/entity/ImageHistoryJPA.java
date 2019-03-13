package com.imcode.imcms.persistence.entity;

import imcode.util.image.Format;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@Table(name = "imcms_text_doc_images_history")
@Data
public class ImageHistoryJPA {

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

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User modifiedBy;

    @Column(name = "modified_at")
    private LocalDateTime modifiedAt;

    public ImageHistoryJPA(Image image, User modifiedBy, LocalDateTime modifiedAt) {
        this.version = image.getVersion();
        this.language = image.getLanguage();
        this.index = image.getIndex();
        this.format = image.getFormat();
        this.loopEntryRef = image.getLoopEntryRef();
        this.width = image.getWidth();
        this.height = image.getHeight();
        this.border = image.getBorder();
        this.align = image.getAlign();
        this.alternateText = image.getAlternateText();
        this.lowResolutionUrl = image.getLowResolutionUrl();
        this.target = image.getTarget();
        this.linkUrl = image.getLinkUrl();
        this.url = image.getUrl();
        this.name = image.getName();
        this.type = image.getType();
        this.cropRegion = image.getCropRegion();
        this.rotateAngle = image.getRotateAngle();
        this.generatedFilename = image.getGeneratedFilename();
        this.resize = image.getResize();
        this.archiveImageId = image.getArchiveImageId();
        this.allLanguages = image.isAllLanguages();
        this.inText = image.isInText();
        this.setSpaceAround(new SpaceAroundJPA(image.getSpaceAround()));

        this.modifiedBy = modifiedBy;
        this.modifiedAt = modifiedAt;
    }
}

