package com.imcode.imcms.persistence.entity;

/**
 * Unified interface for image crop region entity and DTO
 *
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 25.10.17.
 */
public interface ImageCropRegionDataHolder {
    int getCropX1();

    int getCropY1();

    int getCropX2();

    int getCropY2();
}
