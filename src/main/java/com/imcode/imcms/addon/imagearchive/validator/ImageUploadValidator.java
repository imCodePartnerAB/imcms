package com.imcode.imcms.addon.imagearchive.validator;

import java.io.File;
import java.util.zip.ZipFile;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.imcode.imcms.addon.imagearchive.service.Facade;
import imcode.util.image.ImageInfo;
import imcode.util.image.ImageOp;

public class ImageUploadValidator implements Validator {
    private static final Log log = LogFactory.getLog(ImageUploadValidator.class);
    
    private Facade facade;
    private File tempFile;
    private ImageInfo imageInfo;
    private String imageName;
    private boolean zipFile;

    
    public ImageUploadValidator(Facade facade) {
        this.facade = facade;
    }
    
    
    @SuppressWarnings("unchecked")
    public boolean supports(Class clazz) {
        return CommonsMultipartFile.class.isAssignableFrom(clazz);
    }

    public void validate(Object target, Errors errors) {
        CommonsMultipartFile file = (CommonsMultipartFile) target;

        if (file == null || file.isEmpty()) {
            errors.rejectValue("file", "archive.addImage.invalidImageError");

            return;
        }

        try {
            imageName = file.getOriginalFilename();
            tempFile = facade.getFileService().createTemporaryFile("img_upload");
            file.transferTo(tempFile);

            ZipFile zip = null;
            try {
                zip = new ZipFile(tempFile, ZipFile.OPEN_READ);
                zipFile = true;
            } catch (Exception ex) {
            } finally {
                if (zip != null) {
                    zip.close();
                }
            }

            long maxZipUploadSize = facade.getConfig().getMaxZipUploadSize();
            long maxImageUploadSize = facade.getConfig().getMaxImageUploadSize();
            long fileLength = tempFile.length();
            double size = 0.0;
            boolean sizeError = false;

            if (zipFile && fileLength > maxZipUploadSize) {
                sizeError = true;
                size = maxZipUploadSize;
            } else if (!zipFile && fileLength > maxImageUploadSize) {
                sizeError = true;
                size = maxImageUploadSize;
            }

            if (sizeError) {
                size /= (1024.0 * 1024.0);
                errors.rejectValue("file", "archive.addImage.sizeError", new Object[] {size}, "???");

                return;
            }

            if (!zipFile) {
                imageInfo = ImageOp.getImageInfo(tempFile);
                if (imageInfo == null || imageInfo.getFormat() == null
                        || imageInfo.getWidth() < 1 || imageInfo.getHeight() < 1) {
                    errors.rejectValue("file", "archive.addImage.invalidImageError");
                    tempFile.delete();
                }
            }
        } catch (Exception ex) {
            errors.rejectValue("file", "archive.addImage.invalidImageError");
        } finally {
            file.getFileItem().delete();
        }
    }
    
    
    public File getTempFile() {
        return tempFile;
    }
    
    public ImageInfo getImageInfo() {
        return imageInfo;
    }
    
    public String getImageName() {
        return imageName;
    }

    public boolean isZipFile() {
        return zipFile;
    }
}
