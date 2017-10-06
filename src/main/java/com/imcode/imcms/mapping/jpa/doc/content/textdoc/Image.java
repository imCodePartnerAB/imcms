package com.imcode.imcms.mapping.jpa.doc.content.textdoc;

import com.imcode.imcms.mapping.jpa.doc.content.VersionedI18nContent;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

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

    private Integer type;

    @Column(name = "format", nullable = false, columnDefinition = "smallint")
    private int format;

    private ImageCropRegion cropRegion = new ImageCropRegion();

    @Column(name = "rotate_angle", nullable = false, columnDefinition = "smallint")
    private int rotateAngle;

    @Column(name = "gen_file")
    private String generatedFilename;

    private LoopEntryRef loopEntryRef;

    private int resize;

    @Column(name = "archive_image_id")
    private Long archiveImageId;

}
