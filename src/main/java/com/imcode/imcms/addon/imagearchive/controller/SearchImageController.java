package com.imcode.imcms.addon.imagearchive.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ValidationUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.imcode.imcms.addon.imagearchive.SessionConstants;
import com.imcode.imcms.addon.imagearchive.command.SearchImageCommand;
import com.imcode.imcms.addon.imagearchive.entity.Categories;
import com.imcode.imcms.addon.imagearchive.entity.Images;
import com.imcode.imcms.addon.imagearchive.entity.Roles;
import com.imcode.imcms.addon.imagearchive.service.Facade;
import com.imcode.imcms.addon.imagearchive.util.ArchiveSession;
import com.imcode.imcms.addon.imagearchive.util.Pagination;
import com.imcode.imcms.addon.imagearchive.util.Utils;
import com.imcode.imcms.addon.imagearchive.validator.SearchImageValidator;
import com.imcode.imcms.api.ContentManagementSystem;
import com.imcode.imcms.api.User;

@Controller
public class SearchImageController {
    private static final Pattern PAGE_PATTERN = Pattern.compile("/web/archive/page/(\\d+)/?");
    
    private static final String PAGINATION_KEY = Utils.makeKey(SearchImageController.class, "pagination");
    private static final String COMMAND_KEY = Utils.makeKey(SearchImageController.class, "command");
    
    @Autowired
    private Facade facade;
    
    
    @RequestMapping({"/archive", "/archive/"})
    public ModelAndView indexHandler(
            @ModelAttribute("search") SearchImageCommand command, 
            BindingResult result, 
            @RequestParam(required=false) String returnTo, 
            @RequestParam(required=false) String artist, 
            HttpServletRequest request, 
            HttpServletResponse response, 
            HttpSession session) {
        returnTo = StringUtils.trimToNull(returnTo);
        if (returnTo != null) {
            session.setAttribute(SessionConstants.IMCMS_RETURN_URL, returnTo);
        }
        
        ArchiveSession archiveSession = ArchiveSession.getSession(request);
        ContentManagementSystem cms = ContentManagementSystem.fromRequest(request);
        User user = cms.getCurrentUser();
        
        ModelAndView mav = new ModelAndView("image_archive/pages/search_image");
        Pagination pag = getPagination(archiveSession);
        
        if (request.getParameter("show") == null) {
            SearchImageCommand cmd = (SearchImageCommand) archiveSession.get(COMMAND_KEY);
            if (cmd != null) {
                command.copyFrom(cmd);
            }
        } else {
            archiveSession.put(COMMAND_KEY, command);
            pag.setCurrentPage(0);
        }
        
        if (command.isClear()) {
            command.copyFrom(new SearchImageCommand());
        }
        
        mav.addObject("search", command);
        
        List<Categories> categories = facade.getRoleService().findCategories(user, Roles.ALL_PERMISSIONS);
        mav.addObject("categories", categories);
        mav.addObject("keywords", facade.getImageService().findKeywords());
        mav.addObject("artists", facade.getRoleService().findArtists(user));
        
        SearchImageValidator validator = new SearchImageValidator(facade, user);
        ValidationUtils.invokeValidator(validator, command, result);
        
        if (result.hasErrors()) {
            return mav;
        }
        
        archiveSession.put(COMMAND_KEY, command);
        
        List<Integer> categoryIds = new ArrayList<Integer>(categories.size());
        for (Categories category : categories) {
            categoryIds.add(category.getId());
        }
        
        int imageCount = facade.getImageService().searchImagesCount(command, categoryIds, user);
        mav.addObject("imageCount", imageCount);
        
        pag.setPageSize(command.getResultsPerPage());
        pag.update(imageCount);
        List<Images> images = facade.getImageService().searchImages(command, pag, categoryIds, user);
        
        mav.addObject("images", images);
        mav.addObject("pag", pag);
        
        return mav;
    }
    
    @RequestMapping("/archive/page/*")
    public ModelAndView pageHandler(HttpServletRequest request, HttpServletResponse response) {
        ArchiveSession session = ArchiveSession.getSession(request);
        ContentManagementSystem cms = ContentManagementSystem.fromRequest(request);
        User user = cms.getCurrentUser();
        
        SearchImageCommand command = (SearchImageCommand) session.get(COMMAND_KEY);
        if (command == null) {
            return new ModelAndView("redirect:/web/archive/");
        }
        
        Pagination pag = getPagination(session);
        pag.setCurrentPage(getPage(request));
        
        ModelAndView mav = new ModelAndView("image_archive/pages/search_image");
        mav.addObject("search", command);
        mav.addObject("pag", pag);
        
        List<Categories> categories = facade.getRoleService().findCategories(user, Roles.ALL_PERMISSIONS);
        mav.addObject("categories", categories);
        mav.addObject("keywords", facade.getImageService().findKeywords());
        mav.addObject("artists", facade.getRoleService().findArtists(user));
        
        List<Integer> categoryIds = new ArrayList<Integer>(categories.size());
        for (Categories category : categories) {
            categoryIds.add(category.getId());
        }
        
        int imageCount = facade.getImageService().searchImagesCount(command, categoryIds, user);
        mav.addObject("imageCount", imageCount);
        
        pag.update(imageCount);
        List<Images> images = facade.getImageService().searchImages(command, pag, categoryIds, user);
        
        mav.addObject("images", images);
        
        return mav;
    }
    
    private static Pagination getPagination(ArchiveSession session) {
        Pagination pag = (Pagination) session.get(PAGINATION_KEY);
        if (pag == null) {
            pag = new Pagination(SearchImageCommand.DEFAULT_PAGE_SIZE);
            session.put(PAGINATION_KEY, pag);
        }
        
        return pag;
    }
    
    private static int getPage(HttpServletRequest request) {
        Matcher matcher = PAGE_PATTERN.matcher(request.getRequestURI());
        
        int page = 0;
        if (matcher.find()) {
            try {
                page = Integer.parseInt(matcher.group(1), 10);
                page = Math.max(page - 1, 0);
            } catch (NumberFormatException ex) {
            }
        }
        
        return page;
    }
}
