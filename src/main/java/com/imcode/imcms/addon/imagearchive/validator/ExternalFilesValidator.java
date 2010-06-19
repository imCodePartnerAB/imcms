package com.imcode.imcms.addon.imagearchive.validator;

import com.imcode.imcms.addon.imagearchive.command.ExternalFilesCommand;
import com.imcode.imcms.addon.imagearchive.service.Facade;
import com.imcode.imcms.addon.imagearchive.service.file.FileService;
import imcode.util.image.ImageInfo;
import imcode.util.image.ImageOp;
import java.io.File;
import java.util.regex.Matcher;
import java.util.zip.ZipFile;
import org.apache.commons.lang.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

public class ExternalFilesValidator implements Validator {
    private Facade facade;
    
    private boolean zipFile;
    private File tempFile;
    private String fileName;

    
    public ExternalFilesValidator(Facade facade) {
        this.facade = facade;
    }

    
    public void validate(Object target, Errors errors) {
        ExternalFilesCommand command = (ExternalFilesCommand) target;
        
        if (command.getUpload() != null) {
            CommonsMultipartFile file = command.getFile();
            
            if (file == null || file.getBytes() == null || file.getBytes().length == 0) {
                errors.rejectValue("file", "archive.externalFiles.fileError");
                
                return;
            }
            
            try {
                fileName = file.getOriginalFilename();
                tempFile = facade.getFileService().createTemporaryFile("ext_file");
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
                    errors.rejectValue("file", "archive.externalFiles.fileSizeError", new Object[] {size}, "???");
                    
                    return;
                }
                
                if (!zipFile) {
                    ImageInfo info = ImageOp.getImageInfo(tempFile);
                    if (info == null) {
                        errors.rejectValue("file", "archive.externalFiles.fileError");
                        
                        return;
                    }
                    
                    Matcher matcher = FileService.FILENAME_PATTERN.matcher(fileName);
                    if (!matcher.matches() || StringUtils.isEmpty((fileName = StringUtils.trimToEmpty(matcher.group(1))))) {
                        errors.rejectValue("file", "archive.externalFiles.fileError");
                        
                        return;
                    }
                    String extension = StringUtils.substringAfterLast(fileName, ".").toLowerCase();
                    
                    if (!FileService.IMAGE_EXTENSIONS_SET.contains(extension)) {
                        errors.rejectValue("file", "archive.externalFiles.fileError");
                    }
                }
            } catch (Exception ex) {
                errors.rejectValue("file", "archive.externalFiles.fileError");
            }
        }
    }
    
    @SuppressWarnings("unchecked")
    public boolean supports(Class clazz) {
        return ExternalFilesCommand.class.isAssignableFrom(clazz);
    }

    
    public File getTempFile() {
        return tempFile;
    }

    public void setTempFile(File tempFile) {
        this.tempFile = tempFile;
    }

    public boolean isZipFile() {
        return zipFile;
    }

    public void setZipFile(boolean zipFile) {
        this.zipFile = zipFile;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
