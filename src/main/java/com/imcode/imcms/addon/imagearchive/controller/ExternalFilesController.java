package com.imcode.imcms.addon.imagearchive.controller;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

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
import com.imcode.imcms.addon.imagearchive.command.ExternalFilesCommand;
import com.imcode.imcms.addon.imagearchive.command.ExternalFilesSaveImageCommand;
import com.imcode.imcms.addon.imagearchive.dto.LibrariesDto;
import com.imcode.imcms.addon.imagearchive.dto.LibraryEntryDto;
import com.imcode.imcms.addon.imagearchive.entity.Images;
import com.imcode.imcms.addon.imagearchive.service.Facade;
import com.imcode.imcms.addon.imagearchive.service.file.LibrarySort;
import com.imcode.imcms.addon.imagearchive.util.Utils;
import com.imcode.imcms.addon.imagearchive.util.image.Format;
import com.imcode.imcms.addon.imagearchive.util.image.ImageInfo;
import com.imcode.imcms.addon.imagearchive.util.image.ImageOp;
import com.imcode.imcms.addon.imagearchive.validator.ChangeImageDataValidator;
import com.imcode.imcms.addon.imagearchive.validator.ExternalFilesValidator;
import com.imcode.imcms.api.ContentManagementSystem;
import com.imcode.imcms.api.User;

@Controller
public class ExternalFilesController {
    private static final Log log = LogFactory.getLog(ExternalFilesController.class);
    
    private static final String LIBRARY_KEY = Utils.makeKey(ExternalFilesController.class, "library");
    private static final String SORT_KEY = Utils.makeKey(ExternalFilesController.class, "sortBy");
    
    private static final String IMAGE_KEY = Utils.makeKey(ExternalFilesController.class, "image");
    private static final String KEYWORDS_KEY = Utils.makeKey(ExternalFilesController.class, "keywords");
    private static final String IMAGE_KEYWORDS_KEY = Utils.makeKey(ExternalFilesController.class, "imageKeywords");
    
    @Autowired
    private Facade facade;
    
    @Autowired
    private Config config;
    
    
    @RequestMapping("/archive/external-files")
    public ModelAndView indexHandler(
            HttpServletRequest request, 
            HttpServletResponse response, 
            HttpSession session) {
        ContentManagementSystem cms = ContentManagementSystem.fromRequest(request);
        User user = cms.getCurrentUser();
        
        if (user.isDefaultUser()) {
            Utils.redirectToLogin(request, response, facade);
            
            return null;
        }
        
        ModelAndView mav = new ModelAndView("image_archive/pages/external_files/external_files");
        
        List<LibrariesDto> libraries = facade.getLibraryService().findLibraries(user);
        if (libraries.isEmpty()) {
            return mav;
        }
        
        LibrariesDto library = getLibrary(session, user, libraries);
        LibrarySort sortBy = getSortBy(session);
        List<LibraryEntryDto> libraryEntries = facade.getFileService().listLibraryEntries(library, sortBy);
        
        mav.addObject("currentLibrary", library);
        mav.addObject("libraries", libraries);
        mav.addObject("libraryEntries", libraryEntries);
        mav.addObject("sortBy", sortBy);
        mav.addObject("externalFiles", new ExternalFilesCommand());
        
        return mav;
    }
    
    @RequestMapping("/archive/external-files/library")
    public String changeLibraryHandler(
            @RequestParam(required=false) Integer id,
            HttpServletRequest request, 
            HttpServletResponse response, 
            HttpSession session) {
        ContentManagementSystem cms = ContentManagementSystem.fromRequest(request);
        User user = cms.getCurrentUser();
        
        if (user.isDefaultUser()) {
            Utils.redirectToLogin(request, response, facade);
            
            return null;
        }
        
        if (id != null) {
            LibrariesDto library = null;
            if (id == LibrariesDto.USER_LIBRARY_ID) {
                library = LibrariesDto.userLibrary(user);
            } else {
                library = facade.getLibraryService().findLibraryById(user, id);
            }
            session.setAttribute(LIBRARY_KEY, library);
        }
        
        return "redirect:/web/archive/external-files";
    }
    
    @RequestMapping("/archive/external-files/sort")
    public String changeSortByHandler(
            @RequestParam(required=false) String sortBy, 
            HttpServletResponse response, 
            HttpSession session) {
        LibrarySort sort = null;
        if (sortBy != null && (sort = LibrarySort.findByName(sortBy)) != null) {
            session.setAttribute(SORT_KEY, sort);
        }
        
        return "redirect:/web/archive/external-files";
    }
    
    @RequestMapping("/archive/external-files/process")
    public ModelAndView processHandler(
            @ModelAttribute("externalFiles") ExternalFilesCommand command, 
            BindingResult result, 
            HttpServletRequest request, 
            HttpServletResponse response, 
            HttpSession session) {
        ContentManagementSystem cms = ContentManagementSystem.fromRequest(request);
        User user = cms.getCurrentUser();
        
        if (user.isDefaultUser()) {
            Utils.redirectToLogin(request, response, facade);
            
            return null;
        }
        
        LibrariesDto library = getLibrary(session, user, null);
        if (library == null) {
            return new ModelAndView("redirect:/web/archive/external-files");
        }
        
        if (command.getActivate() != null) {
            String[] fileNames = command.getFileNames();
            if (!library.isCanUse() || fileNames == null || fileNames.length == 0) {
                return new ModelAndView("redirect:/web/archive/external-files");
            }
            
            ModelAndView mav = new ModelAndView("image_archive/pages/external_files/external_files");
            mav.addObject("activate", true);
            
            if (fileNames.length == 1) {
                Images image = activateImage(library, fileNames[0], user);
                if (image == null) {
                    mav.addObject("activateError", true);
                    
                    return mav;
                }
                session.setAttribute(IMAGE_KEY, image);
                
                ChangeImageDataCommand changeData = new ChangeImageDataCommand();
                changeData.fromImage(image);
                mav.addObject("changeData", changeData);
                mav.addObject("categories", facade.getImageService().findAvailableImageCategories(image.getId(), user));

                List<String> keywords = facade.getImageService().findAvailableKeywords(image.getId());
                List<String> imageKeywords = facade.getImageService().findImageKeywords(image.getId());
                session.setAttribute(KEYWORDS_KEY, keywords);
                session.setAttribute(IMAGE_KEYWORDS_KEY, imageKeywords);

                mav.addObject("keywords", keywords);
                mav.addObject("imageKeywords", imageKeywords);
                mav.addObject("image", image);
            } else {
                List<Object[]> tuples = new ArrayList<Object[]>(fileNames.length);
                for (String fileName : fileNames) {
                    try {
                        File imageFile = facade.getFileService().getImageFileFromLibrary(library, fileName);
                        ImageInfo imageInfo = null;
                        if (imageFile == null || (imageInfo = ImageOp.getImageInfo(config, imageFile)) == null) {
                            continue;
                        }
                        
                        tuples.add(new Object[] {imageFile, imageInfo, fileName});
                    } catch (Exception ex) {
                        log.warn(ex.getMessage(), ex);
                    }
                }
                
                if (!tuples.isEmpty()) {
                    facade.getImageService().createImages(tuples, user);
                }
                
                return new ModelAndView("redirect:/web/archive/external-files");
            }
            
            return mav;
        } else if (command.getErase() != null) {
            String[] fileNames = command.getFileNames();
            if (library.isCanChange() && fileNames != null) {
                for (String fileName : fileNames) {
                    facade.getFileService().deleteFileFromLibrary(library, fileName);
                }
            }
        } else if (command.getUpload() != null) {
            if (!library.isCanChange()) {
                return new ModelAndView("redirect:/web/archive/external-files");
            }
            
            ExternalFilesValidator validator = new ExternalFilesValidator(facade);
            ValidationUtils.invokeValidator(validator, command, result);
            
            ModelAndView mav = new ModelAndView("image_archive/pages/external_files/external_files");
            
            List<LibrariesDto> libraries = facade.getLibraryService().findLibraries(user);
            LibrarySort sortBy = getSortBy(session);
            List<LibraryEntryDto> libraryEntries = facade.getFileService().listLibraryEntries(library, sortBy);

            mav.addObject("currentLibrary", library);
            mav.addObject("libraries", libraries);
            mav.addObject("libraryEntries", libraryEntries);
            mav.addObject("sortBy", sortBy);
            
            if (result.hasErrors()) {
                if (validator.getTempFile() != null) {
                    validator.getTempFile().delete();
                }
                
                return mav;
            }
            
            
            boolean success = false;
            if (!validator.isZipFile()) {
                success = facade.getFileService().storeImageToLibrary(library, validator.getTempFile(), validator.getFileName());
            } else {
                success = facade.getFileService().storeZipToLibrary(library, validator.getTempFile());
            }
            
            validator.getTempFile().delete();
            
            if (!success) {
                return mav;
            }
        }
        
        return new ModelAndView("redirect:/web/archive/external-files");
    }
    
    private Images activateImage(LibrariesDto library, String fileName, User user) {
        File imageFile = facade.getFileService().getImageFileFromLibrary(library, fileName);
        ImageInfo imageInfo = null;
        if (imageFile == null || (imageInfo = ImageOp.getImageInfo(config, imageFile)) == null) {
            return null;
        }
        
        try {
            return facade.getImageService().createImage(imageFile, imageInfo, fileName, user);
        } catch (Exception ex) {
            log.warn(ex.getMessage(), ex);
        }
        
        return null;
    }
    
    @RequestMapping("/archive/external-files/change")
    public ModelAndView changeHandler(
            @ModelAttribute("changeData") ChangeImageDataCommand changeData, 
            BindingResult result, 
            ExternalFilesSaveImageCommand saveCommand, 
            HttpServletRequest request, 
            HttpServletResponse response, 
            HttpSession session) {
        ContentManagementSystem cms = ContentManagementSystem.fromRequest(request);
        User user = cms.getCurrentUser();
        
        Images image = (Images) session.getAttribute(IMAGE_KEY);
        if (user.isDefaultUser()) {
            Utils.redirectToLogin(request, response, facade);
            
            return null;
        } else if (image == null) {
            return new ModelAndView("redirect:/web/archive/external-files");
        }
        
        if (saveCommand.getCancel() != null) {
            facade.getImageService().deleteImage(image.getId());
            session.removeAttribute(IMAGE_KEY);
            session.removeAttribute(KEYWORDS_KEY);
            session.removeAttribute(IMAGE_KEYWORDS_KEY);
            
            return new ModelAndView("redirect:/web/archive/external-files");
        }
        
        ChangeImageDataValidator validator = new ChangeImageDataValidator(facade, user);
        ValidationUtils.invokeValidator(validator, changeData, result);
        
        ModelAndView mav = new ModelAndView("image_archive/pages/external_files/external_files");
        mav.addObject("activate", true);
        mav.addObject("image", image);
        
        List<String> keywords = changeData.getKeywordNames();
        List<String> imageKeywords = changeData.getImageKeywordNames();
        session.setAttribute(KEYWORDS_KEY, keywords);
        session.setAttribute(IMAGE_KEYWORDS_KEY, imageKeywords);
        mav.addObject("keywords", keywords);
        mav.addObject("imageKeywords", imageKeywords);
        
        if (result.hasErrors()) {
            mav.addObject("categories", facade.getImageService().findAvailableImageCategories(image.getId(), user));
            mav.addObject("imageCategories", facade.getImageService().findImageCategories(image.getId()));
            
            return mav;
        }
        
        changeData.toImage(image);
        
        try {
            facade.getImageService().updateData(image, changeData.getCategoryIds(), imageKeywords);
            
            if (saveCommand.getSaveActivate() != null) {
                session.removeAttribute(IMAGE_KEY);
                session.removeAttribute(KEYWORDS_KEY);
                session.removeAttribute(IMAGE_KEYWORDS_KEY);
                
                return new ModelAndView("redirect:/web/archive/external-files");
            } else if (saveCommand.getSaveUse() != null) {
                session.removeAttribute(IMAGE_KEY);
                session.removeAttribute(KEYWORDS_KEY);
                session.removeAttribute(IMAGE_KEYWORDS_KEY);
                
                return new ModelAndView("redirect:/web/archive/use?id=" + image.getId());
            } else if (saveCommand.getSaveImageCard() != null) {
                session.removeAttribute(IMAGE_KEY);
                session.removeAttribute(KEYWORDS_KEY);
                session.removeAttribute(IMAGE_KEYWORDS_KEY);
                
                return new ModelAndView("redirect:/web/archive/image/" + image.getId());
            }
            
            mav.addObject("categories", facade.getImageService().findAvailableImageCategories(image.getId(), user));
            mav.addObject("imageCategories", facade.getImageService().findImageCategories(image.getId()));
        } catch (Exception ex) {
            log.fatal(ex.getMessage(), ex);
            
            return new ModelAndView("redirect:/web/archive/external-files");
        }
        
        return mav;
    }
    
    @RequestMapping("/archive/external-files/preview")
    public ModelAndView previewHandler(
            @RequestParam(required=false) Integer id, 
            @RequestParam(required=false) String name, 
            HttpServletRequest request, 
            HttpServletResponse response, 
            HttpSession session) {
        ContentManagementSystem cms = ContentManagementSystem.fromRequest(request);
        User user = cms.getCurrentUser();
        
        LibrariesDto library = null;
        ImageInfo imageInfo = null;
        if (!user.isDefaultUser() && id != null) {
            if (id == LibrariesDto.USER_LIBRARY_ID) {
                library = LibrariesDto.userLibrary(user);
            } else {
                library = facade.getLibraryService().findLibraryById(user, id);
            }
            
            name = StringUtils.trimToNull(name);
            
            if (name != null) {
                File imageFile = facade.getFileService().getImageFileFromLibrary(library, name);
                if (imageFile != null) {
                    imageInfo = ImageOp.getImageInfo(config, imageFile);
                }
            }
        }
        
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("user", user);
        model.put("library", library);
        model.put("imageInfo", imageInfo);
        model.put("name", name);
        
        return new ModelAndView("image_archive/pages/external_files/preview", model);
    }
    
    @RequestMapping("/archive/external-files/image")
    public String imageHandler(
            @RequestParam(required=false) Integer id, 
            @RequestParam(required=false) String name, 
            HttpServletRequest request, 
            HttpServletResponse response, 
            HttpSession session) {
        ContentManagementSystem cms = ContentManagementSystem.fromRequest(request);
        User user = cms.getCurrentUser();
        
        if (user.isDefaultUser() || id == null) {
            Utils.sendErrorCode(response, HttpServletResponse.SC_NOT_FOUND);
            return null;
        }
        
        LibrariesDto library = null;
        if (id == LibrariesDto.USER_LIBRARY_ID) {
            library = LibrariesDto.userLibrary(user);
        } else {
            library = facade.getLibraryService().findLibraryById(user, id);
        }
        
        if (library == null || !library.isCanUse()) {
            Utils.sendErrorCode(response, HttpServletResponse.SC_NOT_FOUND);
            return null;
        }
        
        File imageFile = facade.getFileService().getImageFileFromLibrary(library, name);
        if (imageFile == null) {
            Utils.sendErrorCode(response, HttpServletResponse.SC_NOT_FOUND);
            return null;
        }
        
        try {
            byte[] data = new ImageOp(config).input(imageFile)
                    .outputFormat(Format.JPEG)
                    .processToByteArray();
            
            if (data == null) {
                Utils.sendErrorCode(response, HttpServletResponse.SC_NOT_FOUND);
            
                return null;
            }
            
            Utils.addNoCacheHeaders(response);
            response.setContentLength(data.length);
            response.setContentType("image/jpeg");
            
            OutputStream output = null;
            try {
                output = new BufferedOutputStream(response.getOutputStream());
                IOUtils.copy(new ByteArrayInputStream(data), output);
                output.flush();
            } catch (Exception ex) {
            } finally {
                IOUtils.closeQuietly(output);
            }
        } catch (Exception ex) {
            Utils.sendErrorCode(response, HttpServletResponse.SC_NOT_FOUND);
            
            return null;
        }
        
        return null;
    }
    
    private LibrariesDto getLibrary(HttpSession session, User user, List<LibrariesDto> libraries) {
        LibrariesDto library = (LibrariesDto) session.getAttribute(LIBRARY_KEY);
        if (library != null && !library.isUserLibrary()) {
            library = facade.getLibraryService().findLibraryById(user, library.getId());
        }
        if (library == null && libraries != null) {
            library = facade.getLibraryService().findLibraryById(user, libraries.get(0).getId());
            session.setAttribute(LIBRARY_KEY, library);
        }
        
        return library;
    }
    
    private static LibrarySort getSortBy(HttpSession session) {
        LibrarySort sortBy = (LibrarySort) session.getAttribute(SORT_KEY);
        if (sortBy == null) {
            sortBy = LibrarySort.DATE;
            session.setAttribute(SORT_KEY, sortBy);
        }
        
        return sortBy;
    }
}
