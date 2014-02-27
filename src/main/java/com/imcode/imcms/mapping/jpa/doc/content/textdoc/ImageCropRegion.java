package com.imcode.imcms.mapping.jpa.doc.content.textdoc;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.util.Objects;

@Embeddable
public class ImageCropRegion {

    @Column(name = "crop_x1", nullable = false)
    private int cropX1;
    @Column(name = "crop_y1", nullable = false)
    private int cropY1;
    @Column(name = "crop_x2", nullable = false)
    private int cropX2;
    @Column(name = "crop_y2", nullable = false)
    private int cropY2;

    @Override
    public int hashCode() {
        return Objects.hash(cropX1, cropX2, cropY1, cropY2);
    }

    @Override
    public boolean equals(Object object) {
        return this == object || (object instanceof ImageCropRegion && equals((ImageCropRegion) object));
    }

    private boolean equals(ImageCropRegion that) {
        return cropX1 == that.cropX1 && cropY1 == that.cropY1 &&
                cropX2 == that.cropX2 && cropY2 == that.cropY2;
    }

    @Override
    public String toString() {
        return com.google.common.base.Objects.toStringHelper(this)
                .add("cropX1", cropX1)
                .add("cropY1", cropX1)
                .add("cropX2", cropX1)
                .add("cropY1", cropX1)
                .toString();
    }

    public int getCropX1() {
        return cropX1;
    }

    public void setCropX1(int cropX1) {
        this.cropX1 = cropX1;
    }

    public int getCropY1() {
        return cropY1;
    }

    public void setCropY1(int cropY1) {
        this.cropY1 = cropY1;
    }

    public int getCropX2() {
        return cropX2;
    }

    public void setCropX2(int cropX2) {
        this.cropX2 = cropX2;
    }

    public int getCropY2() {
        return cropY2;
    }

    public void setCropY2(int cropY2) {
        this.cropY2 = cropY2;
    }
}
