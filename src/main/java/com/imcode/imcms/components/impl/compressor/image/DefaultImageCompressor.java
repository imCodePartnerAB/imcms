package com.imcode.imcms.components.impl.compressor.image;

import com.imcode.imcms.components.ImageCompressor;
import com.imcode.imcms.components.exception.CompressionImageException;
import imcode.util.image.Format;
import imcode.util.image.ImageOp;
import imcode.util.image.Layer;
import imcode.util.io.FileUtility;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;

/**
 * Internal image compressor ImageMagick
 */
public class DefaultImageCompressor implements ImageCompressor {

    private final Logger logger = LogManager.getLogger(DefaultImageCompressor.class);

    private final String imageMagickPath;

    public DefaultImageCompressor(String imageMagickPath){
        this.imageMagickPath = imageMagickPath;
    }

    @Override
    public byte[] compressImage(byte[] image, Format imageFormat) throws CompressionImageException {
        try{
            File tempFile = null;
            try {
                tempFile = File.createTempFile("compressImg", null);
                FileUtils.writeByteArrayToFile(tempFile, image);

                final ImageOp operation = new ImageOp(imageMagickPath).input(tempFile);

                switch (imageFormat) {
                    case JPG, JPEG -> operation.quality(80);
                    case PNG -> operation.quality(90);
                    case GIF -> operation.layers(Layer.OPTIMIZE);
                }

                byte[] result = operation.processToByteArray();
                return result.length < image.length ? result : image;
            } finally {
                if(tempFile != null) FileUtility.forceDelete(tempFile);
            }
        }catch (IOException e){
            logger.error("Error while compressing image using ImageMagick", e);
            throw new CompressionImageException(e);
        }
    }
}
