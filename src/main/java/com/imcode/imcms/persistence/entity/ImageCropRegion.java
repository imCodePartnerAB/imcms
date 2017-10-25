package com.imcode.imcms.persistence.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Data
@Embeddable
@NoArgsConstructor
public class ImageCropRegion implements ImageCropRegionDataHolder {

    @Column(name = "crop_x1", nullable = false)
    private int cropX1;
    @Column(name = "crop_y1", nullable = false)
    private int cropY1;
    @Column(name = "crop_x2", nullable = false)
    private int cropX2;
    @Column(name = "crop_y2", nullable = false)
    private int cropY2;

    public ImageCropRegion(int cropX1, int cropY1, int cropX2, int cropY2) {
        this.cropX1 = cropX1;
        this.cropY1 = cropY1;
        this.cropX2 = cropX2;
        this.cropY2 = cropY2;
    }

    public static ImageCropRegion of(ImageCropRegionDataHolder cropRegionDataHolder) {
        return new ImageCropRegion(
                cropRegionDataHolder.getCropX1(),
                cropRegionDataHolder.getCropY1(),
                cropRegionDataHolder.getCropX2(),
                cropRegionDataHolder.getCropY2()
        );
    }

}
