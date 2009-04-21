package com.imcode.imcms.addon.imagearchive.validator;

import com.imcode.imcms.addon.imagearchive.command.ExportImageCommand;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public class ExportImageValidator implements Validator {
    public void validate(Object target, Errors errors) {
        ExportImageCommand command = (ExportImageCommand) target;
        
        Integer quality = command.getQuality();
        if (quality == null) {
            quality = 100;
        } else {
            quality = Math.max(quality, 10);
            quality = Math.min(quality, 100);
        }
        command.setQuality(quality);
        
        Integer width = command.getWidth();
        if (width != null) {
            int w = width.intValue();
            
            if (w < 1) {
                errors.rejectValue("width", "archive.imageCard.export.tooSmallError", new Object[] {1}, "???");
            } else if (w > 10000) {
                errors.rejectValue("width", "archive.imageCard.export.tooBigError", new Object[] {10000}, "???");
            }
        }
        Integer height = command.getHeight();
        if (height != null) {
            int h = height.intValue();
            
            if (h < 1) {
                errors.rejectValue("height", "archive.imageCard.export.tooSmallError", new Object[] {1}, "???");
            } else if (h > 10000) {
                errors.rejectValue("height", "archive.imageCard.export.tooBigError", new Object[] {10000}, "???");
            }
        }
    }
    
    @SuppressWarnings("unchecked")
    public boolean supports(Class clazz) {
        return ExportImageCommand.class.isAssignableFrom(clazz);
    }
}
