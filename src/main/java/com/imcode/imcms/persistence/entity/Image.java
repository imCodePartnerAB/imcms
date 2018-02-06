package com.imcode.imcms.persistence.entity;

import imcode.util.image.Format;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "imcms_text_doc_images")
@Getter
@Setter
public class Image extends VersionedI18nContent {

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

    @Column(name = "v_space")
    private int verticalSpace;

    @Column(name = "h_space")
    private int horizontalSpace;

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

    public Image(Image from) {
        setId(from.getId());
        setVersion(from.getVersion());
        setLanguage(from.getLanguage());
        setIndex(from.getIndex());
        setFormat(from.getFormat());
        setLoopEntryRef(from.getLoopEntryRef());
    }

    public Image() {
    }
}
