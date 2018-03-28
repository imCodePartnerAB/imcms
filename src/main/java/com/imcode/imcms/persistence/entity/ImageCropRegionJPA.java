package com.imcode.imcms.persistence.entity;

import com.imcode.imcms.model.ImageCropRegion;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Data
@Embeddable
@NoArgsConstructor
public class ImageCropRegionJPA extends ImageCropRegion {

    @Column(name = "crop_x1", nullable = false)
    private int cropX1;
    @Column(name = "crop_y1", nullable = false)
    private int cropY1;
    @Column(name = "crop_x2", nullable = false)
    private int cropX2;
    @Column(name = "crop_y2", nullable = false)
    private int cropY2;

    public ImageCropRegionJPA(int cropX1, int cropY1, int cropX2, int cropY2) {
        this.cropX1 = cropX1;
        this.cropY1 = cropY1;
        this.cropX2 = cropX2;
        this.cropY2 = cropY2;
    }

    public ImageCropRegionJPA(ImageCropRegion cropRegionDataHolder) {
        super(cropRegionDataHolder);
    }

}
