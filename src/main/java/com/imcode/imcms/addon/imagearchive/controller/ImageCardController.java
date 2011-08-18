package com.imcode.imcms.addon.imagearchive.controller;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.imcode.imcms.addon.imagearchive.util.SessionUtils;
import com.imcode.imcms.addon.imagearchive.util.exif.Flash;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ValidationUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.imcode.imcms.addon.imagearchive.Config;
import com.imcode.imcms.addon.imagearchive.command.ChangeImageDataCommand;
import com.imcode.imcms.addon.imagearchive.command.ExportImageCommand;
import com.imcode.imcms.addon.imagearchive.command.ImageCardChangeActionCommand;
import com.imcode.imcms.addon.imagearchive.entity.Categories;
import com.imcode.imcms.addon.imagearchive.entity.Exif;
import com.imcode.imcms.addon.imagearchive.entity.Images;
import com.imcode.imcms.addon.imagearchive.entity.Keywords;
import com.imcode.imcms.addon.imagearchive.service.Facade;
import com.imcode.imcms.addon.imagearchive.util.ArchiveSession;
import com.imcode.imcms.addon.imagearchive.util.Utils;
import com.imcode.imcms.addon.imagearchive.util.exif.ExifData;
import com.imcode.imcms.addon.imagearchive.util.exif.ExifUtils;
import com.imcode.imcms.addon.imagearchive.validator.ChangeImageDataValidator;
import com.imcode.imcms.addon.imagearchive.validator.ExportImageValidator;
import com.imcode.imcms.addon.imagearchive.validator.ImageUploadValidator;
import com.imcode.imcms.api.ContentManagementSystem;
import com.imcode.imcms.api.User;
import imcode.util.image.Filter;
import imcode.util.image.Format;
import imcode.util.image.ImageInfo;
import imcode.util.image.ImageOp;
import imcode.util.image.Resize;

@Controller
public class ImageCardController {
    private static final Log log = LogFactory.getLog(ImageCardController.class);
    
    private static final Pattern IMAGE_ID_PATTERN = Pattern.compile("/web/archive/image/([^/]+)/?");
    private static final String IMAGE_KEY = Utils.makeKey(ImageCardController.class, "image");
    
    @Autowired
    private Facade facade;
    
    @Autowired
    private Config config;
    
    
    @RequestMapping({"/archive/image/*", "/archive/image/*/"})
    public ModelAndView indexHandler(
            @ModelAttribute("exportImage") ExportImageCommand command, 
            BindingResult result, 
            HttpServletRequest request, 
            HttpServletResponse response) {
        ContentManagementSystem cms = ContentManagementSystem.fromRequest(request);
        User user = cms.getCurrentUser();
        
        Long imageId = getImageId(request);
        Images image;
        
        if (imageId == null || (image = facade.getImageService().findById(imageId, user)) == null) {
            return new ModelAndView("redirect:/web/archive/");
        }
        
        if (command.getExport() != null && !image.isArchived()) {
            
            ExportImageValidator validator = new ExportImageValidator();
            ValidationUtils.invokeValidator(validator, command, result);
            
            Format imageFormat = Format.findFormat(command.getFileFormat());
            
            if (imageFormat != null && imageFormat.isWritable() && !result.hasErrors()) {
                if (processExport(imageId, image, imageFormat, command, response)) {
                    return null;
                }
            }
            
        } else {
            
            Format format = Format.findFormat(image.getFormat());
            if (format.isWritable()) {
                command.setFileFormat(format.getOrdinal());
            }
        }
        
        facade.getImageService().setImageMetaIds(image);
        
        ModelAndView mav = new ModelAndView("image_archive/pages/image_card/image_card");
        mav.addObject("image", image);
        mav.addObject("categories", getCategories(image));
        mav.addObject("keywords", getKeywords(image));
        mav.addObject("canUseInImcms", SessionUtils.getImcmsReturnToUrl(request.getSession()) != null
                && (facade.getImageService().canUseImage(user, imageId) || image.isCanChange()));
        mav.addObject("canExport", (facade.getImageService().canUseImage(user, imageId) || image.isCanChange()));
        
        return mav;
    }
    
    private boolean processExport(Long imageId, Images image, Format imageFormat, 
            ExportImageCommand command, HttpServletResponse response) {
        
        File tempFile = null;
        try {
            tempFile = facade.getFileService().createTemporaryFile("export");
            File originalFile = facade.getFileService().getImageOriginalFile(imageId, false);

            ImageOp op = new ImageOp().input(originalFile);

            Integer width = command.getWidth();
            Integer height = command.getHeight();
            if (width != null || height != null) {
                Resize resize = (width != null && height != null && !command.isKeepAspectRatio() ? Resize.FORCE : Resize.DEFAULT);
                op.filter(Filter.LANCZOS);
                op.resize(width, height, resize);
            }

            op.quality(command.getQuality());
            op.outputFormat(imageFormat);
            
            if (!op.processToFile(tempFile)) {
                return false;
            }
            
            if (imageFormat == Format.JPEG) {
                File exifTempFile = facade.getFileService().createTemporaryFile("export_exif");
                
                ExifData data = new ExifData();
                Exif changedExif = image.getChangedExif();
                data.setArtist(changedExif.getArtist());
                data.setCopyright(changedExif.getCopyright());
                data.setDescription(changedExif.getDescription());
                
                boolean res = ExifUtils.writeExifData(tempFile, data, exifTempFile);
                if (res) {
                    tempFile.delete();
                    tempFile = exifTempFile;
                } else {
                    exifTempFile.delete();
                }
            }
            
            response.setContentType(imageFormat.getMimeType());
            response.setContentLength((int) tempFile.length());
            
            String contentDisposition = String.format("attachment; filename=export_img_%d.%s", imageId, imageFormat.getExtension());
            response.setHeader("Content-Disposition", contentDisposition);
            
            OutputStream output = null;
            InputStream input = null;
            try {
                input = new FileInputStream(tempFile);
                output = new BufferedOutputStream(response.getOutputStream());
                
                IOUtils.copy(input, output);
                output.flush();
            } catch (Exception ex) {
                log.warn(ex.getMessage(), ex);
            } finally {
                IOUtils.closeQuietly(output);
                IOUtils.closeQuietly(input);
            }
        } finally {
            if (tempFile != null) {
                tempFile.delete();
            }
        }
        
        return true;
    }
    
    private static String getCategories(Images image) {
        List<Categories> categories = image.getCategories();
        StringBuilder categoryBuilder = new StringBuilder();
        for (int i = 0, len = categories.size(); i < len; i++) {
            Categories category = categories.get(i);
            categoryBuilder.append(category.getName());
            
            if (i < (len - 1)) {
                categoryBuilder.append(", ");
            }
        }
        
        return categoryBuilder.toString();
    }
    
    private static String getKeywords(Images image) {
        List<Keywords> keywords = image.getKeywords();
        StringBuilder keywordBuilder = new StringBuilder();
        for (int i = 0, len = keywords.size(); i < len; i++) {
            keywordBuilder.append(keywords.get(i).getKeywordNm());
            
            if (i < (len - 1)) {
                keywordBuilder.append(", ");
            }
        }
        
        return keywordBuilder.toString();
    }
    
    @RequestMapping("/archive/image/*/exif")
    public ModelAndView exifHandler(HttpServletRequest request, HttpServletResponse response) {
        ContentManagementSystem cms = ContentManagementSystem.fromRequest(request);
        User user = cms.getCurrentUser();
        
        Long imageId = getImageId(request);
        Images image;
        if (imageId == null || (image = facade.getImageService().findById(imageId, user)) == null) {
            return new ModelAndView("redirect:/web/archive");
        }
        
        Exif originalExif = facade.getImageService().findExifByPK(image.getId(), Exif.TYPE_ORIGINAL);
        image.setOriginalExif(originalExif);
        
        ModelAndView mav = new ModelAndView("image_archive/pages/image_card/image_card");
        mav.addObject("action", "exif");
        mav.addObject("image", image);
        
        
        return mav;
    }

    @RequestMapping("/archive/image/*/unarchive")
    public ModelAndView unarchiveHandler(HttpServletRequest request, HttpServletResponse response) {
        ContentManagementSystem cms = ContentManagementSystem.fromRequest(request);
        User user = cms.getCurrentUser();

        Long imageId = getImageId(request);
        Images image = null;
        if (imageId == null || (image = facade.getImageService().findById(imageId, user)) == null) {
            return new ModelAndView("redirect:/web/archive");
        }

        if(user.isSuperAdmin()) {
            facade.getImageService().unarchiveImage(imageId);
        }

        return new ModelAndView("redirect:/web/archive/image/" + imageId);
    }
    
    @RequestMapping("/archive/image/*/erase")
    public ModelAndView eraseHandler(
            @RequestParam(required=false) Boolean delete, 
            HttpServletRequest request, 
            HttpServletResponse response) {
        ContentManagementSystem cms = ContentManagementSystem.fromRequest(request);
        User user = cms.getCurrentUser();
        
        if (user.isDefaultUser()) {
            Utils.redirectToLogin(request, response, facade);
            
            return null;
        }
        
        Long imageId = getImageId(request);
        Images image;
        if (imageId == null || (image = facade.getImageService().findById(imageId, user)) == null) {
            return new ModelAndView("redirect:/web/archive/");
        } else if (image.isArchived() || !image.isCanChange()) {
            return new ModelAndView("redirect:/web/archive/image/" + imageId);
        }
        
        if (delete == null) {
            ModelAndView mav = new ModelAndView("image_archive/pages/image_card/image_card");
            mav.addObject("action", "erase");
            mav.addObject("image", image);
            
            facade.getImageService().setImageMetaIds(image);
            
            return mav;
        } else if (delete.booleanValue()) {
            facade.getImageService().archiveImage(imageId);
            
            return new ModelAndView("redirect:/web/archive/");
        } else {
            return new ModelAndView("redirect:/web/archive/image/" + imageId);
        }
    }
    
    @RequestMapping("/archive/image/*/change")
    public ModelAndView changeHandler(
            @ModelAttribute("changeData") ChangeImageDataCommand changeData,
            BindingResult result, 
            @ModelAttribute ImageCardChangeActionCommand action,  
            HttpServletRequest request, 
            HttpServletResponse response) {
        ArchiveSession session = ArchiveSession.getSession(request);
        ContentManagementSystem cms = ContentManagementSystem.fromRequest(request);
        User user = cms.getCurrentUser();
        
        if (user.isDefaultUser()) {
            Utils.redirectToLogin(request, response, facade);
            
            return null;
        }
        
        Long imageId = getImageId(request);
        
        if (imageId == null) {
            return new ModelAndView("redirect:/web/archive/");
        }
        
        ModelAndView mav = new ModelAndView("image_archive/pages/image_card/image_card");
        mav.addObject("action", "change");
        
        if (!action.isSet()) {
            Images image = facade.getImageService().findById(imageId, user);
            if (image == null) {
                return new ModelAndView("redirect:/web/archive/");
            } else if (image.isArchived() || !image.isCanChange()) {
                return new ModelAndView("redirect:/web/archive/image/" + imageId);
            }
            
            session.put(IMAGE_KEY, image);
            changeData.fromImage(image);
            mav.addObject("image", image);
            
            facade.getFileService().createTemporaryCopyOfCurrentImage(image.getId());
            
            mav.addObject("categories", facade.getImageService().findAvailableImageCategories(image.getId(), user));
            mav.addObject("imageCategories", facade.getImageService().findImageCategories(image.getId()));
            
            List<String> keywords = facade.getImageService().findAvailableKeywords(image.getId());
            List<String> imageKeywords = facade.getImageService().findImageKeywords(image.getId());
            mav.addObject("keywords", keywords);
            mav.addObject("imageKeywords", imageKeywords);
        } else {
            Images image = (Images) session.get(IMAGE_KEY);
            if (image == null) {
                return new ModelAndView("redirect:/web/archive/");
            } else if (!image.isCanChange()) {
                return new ModelAndView("redirect:/web/archive/image/" + imageId);
            }
            mav.addObject("image", image);
            
            if (action.isCancel()) {
                session.remove(IMAGE_KEY);
                
                facade.getFileService().deleteTemporaryImage(imageId);
                
                return new ModelAndView("redirect:/web/archive/image/" + image.getId());
            }
            
            List<String> keywords = changeData.getKeywordNames();
            List<String> imageKeywords = changeData.getImageKeywordNames();
            
            mav.addObject("keywords", keywords);
            mav.addObject("imageKeywords", imageKeywords);
            mav.addObject("categories", facade.getImageService().findAvailableImageCategories(image.getId(), user));
            mav.addObject("imageCategories", facade.getImageService().findImageCategories(image.getId()));
            
            if (action.isUpload()) {
                ImageUploadValidator validator = new ImageUploadValidator(facade);
                ValidationUtils.invokeValidator(validator, changeData.getFile(), result);

                if(validator.isZipFile()) {
                    result.reject("file", "archive.addImage.invalidImageError");
                }
                
                File tempFile = validator.getTempFile();
                if (result.hasErrors()) {
                    return mav;
                }
                
                String copyright = "";
                String description = "";
                String artist = "";
                String manufacturer = null;
                String model = null;
                String compression = null;
                Double exposure = null;
                String exposureProgram = null;
                Float fStop = null;
                Date dateOriginal = null;
                Date dateDigitized = null;
                Flash flash = null;
                Float focalLength = null;
                String colorSpace = null;
                Integer xResolution = null;
                Integer yResolution = null;
                Integer resolutionUnit = null;
                Integer pixelXDimension = null;
                Integer pixelYDimension = null;
                Integer ISO = null;

                ExifData data = ExifUtils.getExifData(tempFile);
                if (data != null) {
                    copyright = StringUtils.substring(data.getCopyright(), 0, 255);
                    description = StringUtils.substring(data.getDescription(), 0, 255);
                    artist = StringUtils.substring(data.getArtist(), 0, 255);
                    xResolution = data.getxResolution();
                    yResolution = data.getyResolution();
                    manufacturer = data.getManufacturer();
                    model = data.getModel();
                    compression = data.getCompression();
                    exposure = data.getExposure();
                    exposureProgram = data.getExposureProgram();
                    fStop = data.getfStop();
                    flash = data.getFlash();
                    focalLength = data.getFocalLength();
                    colorSpace = data.getColorSpace();
                    resolutionUnit = data.getResolutionUnit();
                    pixelXDimension = data.getPixelXDimension();
                    pixelYDimension = data.getPixelYDimension();
                    ISO = data.getISO();
                    dateOriginal = data.getDateOriginal();
                    dateDigitized = data.getDateDigitized();
                }
                int fileSize = (int) tempFile.length();
                
                if (!facade.getFileService().storeImage(tempFile, imageId, true)) {
                    return mav;
                }
                
                changeData.setChangedFile(true);
                
                image.setFileSize(fileSize);
                
                Exif changedExif = new Exif(xResolution, yResolution, description, artist, copyright, Exif.TYPE_CHANGED,
                        manufacturer, model, compression, exposure, exposureProgram, fStop, flash, focalLength, colorSpace,
                        resolutionUnit, pixelXDimension, pixelYDimension, dateOriginal, dateDigitized, ISO);
                Exif originalExif = new Exif(xResolution, yResolution, description, artist, copyright, Exif.TYPE_ORIGINAL,
                        manufacturer, model, compression, exposure, exposureProgram, fStop, flash, focalLength, colorSpace,
                        resolutionUnit, pixelXDimension, pixelYDimension, dateOriginal, dateDigitized, ISO);
                image.setChangedExif(changedExif);
                image.setOriginalExif(originalExif);
                image.setImageNm(StringUtils.substring(validator.getImageName(), 0, 255));
                
                String uploadedBy = String.format("%s %s", user.getFirstName(), user.getLastName()).trim();
                image.setUploadedBy(uploadedBy);
                
                ImageInfo imageInfo = validator.getImageInfo();
                
                image.setFormat(imageInfo.getFormat().getOrdinal());
                
                image.setWidth(imageInfo.getWidth());
                image.setHeight(imageInfo.getHeight());
                
                changeData.fromImage(image);
                
                return mav;
            }
            
            ChangeImageDataValidator validator = new ChangeImageDataValidator(facade, user);
            ValidationUtils.invokeValidator(validator, changeData, result);
            
            if (action.getRotateLeft() != null) {
                facade.getFileService().rotateImage(image.getId(), -90, true);
                
            } else if (action.getRotateRight() != null) {
                facade.getFileService().rotateImage(image.getId(), 90, true);
                
            } else if (!result.hasErrors()) {
                changeData.toImage(image);
                
                if (changeData.isChangedFile()) {
                    facade.getImageService().updateFullData(image, changeData.getCategoryIds(), imageKeywords);
                    changeData.setChangedFile(false);
                    
                } else {
                    facade.getImageService().updateData(image, changeData.getCategoryIds(), imageKeywords);
                }

                /* refreshing categories since those are also set earlier, before update(in case of file upload) */
                mav.getModel().remove("categories");
                mav.getModel().remove("imageCategories");
                mav.addObject("categories", facade.getImageService().findAvailableImageCategories(image.getId(), user));
                mav.addObject("imageCategories", facade.getImageService().findImageCategories(image.getId()));
                
                facade.getFileService().copyTemporaryImageToCurrent(imageId);
                
                if (action.isUse()) {
                    facade.getFileService().deleteTemporaryImage(image.getId());
                    session.remove(IMAGE_KEY);
                    
                    return new ModelAndView("redirect:/web/archive/use?id=" + image.getId());
                } else if (action.isImageCard()) {
                    facade.getFileService().deleteTemporaryImage(image.getId());
                    session.remove(IMAGE_KEY);
                    
                    return new ModelAndView("redirect:/web/archive/image/" + image.getId());
                }
            }
        }
        
        return mav;
    }
    
    private static Long getImageId(HttpServletRequest request) {
        String uri = request.getRequestURI();
        
        Matcher matcher = IMAGE_ID_PATTERN.matcher(uri);
        if (matcher.find()) {
            try {
                return Long.parseLong(matcher.group(1), 10);
            } catch (Exception ex) {
            }
        }
        
        return null;
    }
}
