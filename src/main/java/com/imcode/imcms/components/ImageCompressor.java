package com.imcode.imcms.components;

import com.imcode.imcms.components.exception.CompressionImageException;
import imcode.util.image.Format;

public interface ImageCompressor {

    byte[] compressImage(byte[] image, Format imageFormat) throws CompressionImageException;

}
