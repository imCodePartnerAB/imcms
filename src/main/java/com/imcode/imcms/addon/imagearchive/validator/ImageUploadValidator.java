package com.imcode.imcms.addon.imagearchive.validator;

import java.io.File;
import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.imcode.imcms.addon.imagearchive.service.Facade;
import com.imcode.imcms.addon.imagearchive.util.image.ImageInfo;
import com.imcode.imcms.addon.imagearchive.util.image.ImageOp;

public class ImageUploadValidator implements Validator {
    private static final Log log = LogFactory.getLog(ImageUploadValidator.class);
    
    private Facade facade;
    private File tempFile;
    private ImageInfo imageInfo;
    private String imageName;

    
    public ImageUploadValidator(Facade facade) {
        this.facade = facade;
    }
    
    
    @SuppressWarnings("unchecked")
    public boolean supports(Class clazz) {
        return CommonsMultipartFile.class.isAssignableFrom(clazz);
    }

    public void validate(Object target, Errors errors) {
        CommonsMultipartFile file = (CommonsMultipartFile) target;
        long maxImageUploadSize = facade.getConfig().getMaxImageUploadSize();
        
        if (file == null || file.isEmpty()) {
            errors.rejectValue("file", "archive.addImage.invalidImageError");
        } else if (file.getSize() > maxImageUploadSize) {
            file.getFileItem().delete();
            double megabytes = maxImageUploadSize / (1024.0 * 1024.0);
            
            errors.rejectValue("file", "archive.addImage.sizeError", new Object[] {megabytes}, "???");
        } else {
            imageName = file.getOriginalFilename();
            tempFile = facade.getFileService().createTemporaryFile("img_upload");
            try {
                file.transferTo(tempFile);
                
                imageInfo = ImageOp.getImageInfo(tempFile);
                if (imageInfo == null || imageInfo.getFormat() == null 
                        || imageInfo.getWidth() < 1 || imageInfo.getHeight() < 1) {
                    errors.rejectValue("file", "archive.addImage.invalidImageError");
                    tempFile.delete();
                }
            } catch (IOException ex) {
                log.fatal(ex.getMessage(), ex);
            } finally {
                file.getFileItem().delete();
            }
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
}
