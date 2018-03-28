package com.imcode.imcms.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.imcode.imcms.model.ImageCropRegion;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 25.01.18.
 */
@Data
@NoArgsConstructor
public class ImageCropRegionDTO extends ImageCropRegion implements Serializable {
    private static final long serialVersionUID = -586488435877347784L;

    private volatile int cropX1;
    private volatile int cropY1;
    private volatile int cropX2;
    private volatile int cropY2;

    @JsonIgnore
    private volatile boolean valid;

    public ImageCropRegionDTO(int cropX1, int cropY1, int cropX2, int cropY2) {
        if (cropX1 > cropX2) {
            this.cropX1 = cropX2;
            this.cropX2 = cropX1;
        } else {
            this.cropX1 = cropX1;
            this.cropX2 = cropX2;
        }

        if (cropY1 > cropY2) {
            this.cropY1 = cropY2;
            this.cropY2 = cropY1;
        } else {
            this.cropY1 = cropY1;
            this.cropY2 = cropY2;
        }

        updateValid();
    }

    public ImageCropRegionDTO(ImageCropRegion cropRegionDataHolder) {
        super(cropRegionDataHolder);
    }

    @JsonIgnore
    public boolean isValid() {
        updateValid();
        return valid;
    }

    @JsonIgnore
    private void updateValid() {
        valid = (cropX1 >= 0 && cropY1 >= 0 && cropX2 >= 0 && cropY2 >= 0
                && cropX1 != cropX2 && cropY1 != cropY2);
    }

    @JsonIgnore
    public int getWidth() {
        return valid ? cropX2 - cropX1 : 0;
    }

    @JsonIgnore
    public int getHeight() {
        return valid ? cropY2 - cropY1 : 0;
    }
}
