package com.imcode.imcms.model;

import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
public abstract class ImageCropRegion implements Serializable {

    private static final long serialVersionUID = -947412260935252455L;

    protected ImageCropRegion(ImageCropRegion from) {
        setCropX1(from.getCropX1());
        setCropY1(from.getCropY1());
        setCropX2(from.getCropX2());
        setCropY2(from.getCropY2());
    }

    public abstract int getCropX1();

    public abstract void setCropX1(int cropX1);

    public abstract int getCropY1();

    public abstract void setCropY1(int cropY1);

    public abstract int getCropX2();

    public abstract void setCropX2(int cropX2);

    public abstract int getCropY2();

    public abstract void setCropY2(int cropY2);
}
