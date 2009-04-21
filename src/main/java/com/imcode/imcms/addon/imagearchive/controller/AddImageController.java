package com.imcode.imcms.addon.imagearchive.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ValidationUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.imcode.imcms.addon.imagearchive.command.AddImageActionCommand;
import com.imcode.imcms.addon.imagearchive.command.AddImageUploadCommand;
import com.imcode.imcms.addon.imagearchive.command.ChangeImageDataCommand;
import com.imcode.imcms.addon.imagearchive.entity.Images;
import com.imcode.imcms.addon.imagearchive.service.Facade;
import com.imcode.imcms.addon.imagearchive.util.Utils;
import com.imcode.imcms.addon.imagearchive.validator.ChangeImageDataValidator;
import com.imcode.imcms.addon.imagearchive.validator.ImageUploadValidator;
import com.imcode.imcms.api.ContentManagementSystem;
import com.imcode.imcms.api.User;

@Controller
public class AddImageController {
    private static final Log log = LogFactory.getLog(AddImageController.class);
    
    private static final String IMAGE_KEY = Utils.makeKey(AddImageController.class, "image");
    private static final String KEYWORDS_KEY = Utils.makeKey(AddImageController.class, "keywords");
    private static final String IMAGE_KEYWORDS_KEY = Utils.makeKey(AddImageController.class, "imageKeywords");
    
    @Autowired
    private Facade facade;
    
    
    @SuppressWarnings("unchecked")
    @RequestMapping("/archive/add-image")
    public ModelAndView indexHandler(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        ContentManagementSystem cms = ContentManagementSystem.fromRequest(request);
        User user = cms.getCurrentUser();
        
        if (user.isDefaultUser()) {
            Utils.redirectToLogin(request, response, facade);
            
            return null;
        }
        
        ModelAndView mav = new ModelAndView("image_archive/pages/add_image");
        
        mav.addObject("upload", new AddImageUploadCommand());
        
        Images image = (Images) session.getAttribute(IMAGE_KEY);
        List<String> keywords = (List<String>) session.getAttribute(KEYWORDS_KEY);
        List<String> imageKeywords = (List<String>) session.getAttribute(IMAGE_KEYWORDS_KEY);
        if (image != null) {
            mav.addObject("image", image);
            
            ChangeImageDataCommand changeData = new ChangeImageDataCommand();
            changeData.fromImage(image);
            mav.addObject("changeData", changeData);
            mav.addObject("categories", facade.getImageService().findAvailableImageCategories(image.getId(), user));
            mav.addObject("imageCategories", facade.getImageService().findImageCategories(image.getId()));
            
            if (keywords == null) {
                keywords = facade.getImageService().findAvailableKeywords(image.getId());
            }
            if (imageKeywords == null) {
                imageKeywords = facade.getImageService().findImageKeywords(image.getId());
            }
            mav.addObject("keywords", keywords);
            mav.addObject("imageKeywords", imageKeywords);
        }
        
        return mav;
    }
    
    @RequestMapping("/archive/add-image/upload")
    public ModelAndView uploadHandler(
            @ModelAttribute("upload") AddImageUploadCommand command, 
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
        
        ModelAndView mav = new ModelAndView("image_archive/pages/add_image");
        mav.addObject("changeData", new ChangeImageDataCommand());
        
        ImageUploadValidator validator = new ImageUploadValidator(facade);
        ValidationUtils.invokeValidator(validator, command.getFile(), result);
        
        if (!result.hasErrors()) {
            try {
                Images image = facade.getImageService().createImage(validator.getTempFile(), 
                        validator.getImageInfo(), validator.getImageName(), user);

                if (image == null) {
                    result.rejectValue("file", "archive.addImage.invalidImageError");
                } else {
                    session.setAttribute(IMAGE_KEY, image);
                    mav.addObject("image", image);
                    
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
                }
            } catch (Exception ex) {
                log.fatal(ex.getMessage(), ex);
                result.rejectValue("file", "archive.addImage.invalidImageError");
            } finally {
                validator.getTempFile().delete();
            }
        }
        
        return mav;
    }
    
    @RequestMapping("/archive/add-image/change")
    public ModelAndView changeDataHandler(
            @ModelAttribute("changeData") ChangeImageDataCommand changeData, 
            BindingResult result,
            @ModelAttribute AddImageActionCommand action,
            HttpServletRequest request, 
            HttpServletResponse response, 
            HttpSession session) {
        ContentManagementSystem cms = ContentManagementSystem.fromRequest(request);
        User user = cms.getCurrentUser();
        
        if (user.isDefaultUser()) {
            Utils.redirectToLogin(request, response, facade);
            
            return null;
        }
        
        Images image = (Images) session.getAttribute(IMAGE_KEY);
        if (image == null) {
            return new ModelAndView("redirect:/web/archive/add-image");
        }
        
        if (action.isDiscontinue()) {
            facade.getImageService().deleteImage(image.getId());
            session.removeAttribute(IMAGE_KEY);
            session.removeAttribute(KEYWORDS_KEY);
            session.removeAttribute(IMAGE_KEYWORDS_KEY);
            
            return new ModelAndView("redirect:/web/archive/add-image");
        }
        
        ChangeImageDataValidator validator = new ChangeImageDataValidator(facade, user);
        ValidationUtils.invokeValidator(validator, changeData, result);
        
        ModelAndView mav = new ModelAndView("image_archive/pages/add_image");
        mav.addObject("image", image);
        
        List<String> keywords = changeData.getKeywordNames();
        List<String> imageKeywords = changeData.getImageKeywordNames();
        session.setAttribute(KEYWORDS_KEY, keywords);
        session.setAttribute(IMAGE_KEYWORDS_KEY, imageKeywords);
        mav.addObject("keywords", keywords);
        mav.addObject("imageKeywords", imageKeywords);
        
        if (result.hasErrors()) {
            mav.addObject("upload", new AddImageUploadCommand());
            mav.addObject("categories", facade.getImageService().findAvailableImageCategories(image.getId(), user));
            mav.addObject("imageCategories", facade.getImageService().findImageCategories(image.getId()));
            
            return mav;
        }
        
        changeData.toImage(image);
        
        try {
            facade.getImageService().updateData(image, changeData.getCategoryIds(), imageKeywords);
            
            if (action.isAdd()) {
                session.removeAttribute(IMAGE_KEY);
                session.removeAttribute(KEYWORDS_KEY);
                session.removeAttribute(IMAGE_KEYWORDS_KEY);
                
                return new ModelAndView("redirect:/web/archive/add-image");
            } else if (action.isUse()) {
                session.removeAttribute(IMAGE_KEY);
                session.removeAttribute(KEYWORDS_KEY);
                session.removeAttribute(IMAGE_KEYWORDS_KEY);
                
                return new ModelAndView("redirect:/web/archive/use?id=" + image.getId());
            } else if (action.isImageCard()) {
                session.removeAttribute(IMAGE_KEY);
                session.removeAttribute(KEYWORDS_KEY);
                session.removeAttribute(IMAGE_KEYWORDS_KEY);
                
                return new ModelAndView("redirect:/web/archive/image/" + image.getId());
            }
            
            mav.addObject("categories", facade.getImageService().findAvailableImageCategories(image.getId(), user));
            mav.addObject("imageCategories", facade.getImageService().findImageCategories(image.getId()));
        } catch (Exception ex) {
            log.fatal(ex.getMessage(), ex);
            
            return new ModelAndView("redirect:/web/archive/add-image");
        }
        
        return mav;
    }
}
