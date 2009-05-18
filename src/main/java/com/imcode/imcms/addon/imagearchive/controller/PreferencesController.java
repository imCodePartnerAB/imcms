package com.imcode.imcms.addon.imagearchive.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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

import com.imcode.imcms.addon.imagearchive.command.SaveLibraryRolesCommand;
import com.imcode.imcms.addon.imagearchive.command.SaveRoleCategoriesCommand;
import com.imcode.imcms.addon.imagearchive.dto.LibraryRolesDto;
import com.imcode.imcms.addon.imagearchive.entity.Categories;
import com.imcode.imcms.addon.imagearchive.entity.Libraries;
import com.imcode.imcms.addon.imagearchive.entity.Roles;
import com.imcode.imcms.addon.imagearchive.service.Facade;
import com.imcode.imcms.addon.imagearchive.util.ArchiveSession;
import com.imcode.imcms.addon.imagearchive.util.Utils;
import com.imcode.imcms.addon.imagearchive.validator.SaveLibraryRolesValidator;
import com.imcode.imcms.api.ContentManagementSystem;
import com.imcode.imcms.api.User;

@Controller
public class PreferencesController {
    private static final Log log = LogFactory.getLog(PreferencesController.class);
    
    private static final String ROLE_KEY = Utils.makeKey(PreferencesController.class, "role");
    private static final String LIBRARY_KEY = Utils.makeKey(PreferencesController.class, "library");
    
    @Autowired
    private Facade facade;
    
    
    @RequestMapping("/archive/preferences")
    public ModelAndView indexHandler(HttpServletRequest request, HttpServletResponse response) {
        ArchiveSession session = ArchiveSession.getSession(request);
        ContentManagementSystem cms = ContentManagementSystem.fromRequest(request);
        User user = cms.getCurrentUser();
        
        if (user.isDefaultUser()) {
            Utils.redirectToLogin(request, response, facade);
            
            return null;
        } else if (!user.isSuperAdmin()) {
            return new ModelAndView("redirect:/web/archive");
        }
        
        ModelAndView mav = new ModelAndView("image_archive/pages/preferences");
        
        List<Roles> roles = facade.getRoleService().findRoles();
        Roles role = getRole(session, roles);
        
        facade.getLibraryService().syncLibraryFolders();
        
        List<Libraries> libraries = facade.getLibraryService().findLibraries();
        Libraries library = getLibrary(session, libraries);
        
        SaveLibraryRolesCommand saveLibraryRoles = new SaveLibraryRolesCommand();
        if (library != null) {
            saveLibraryRoles.setLibraryNm(library.getLibraryNm());
            
            List<Roles> availableLibraryRoles = facade.getLibraryService().findAvailableRoles(library.getId());
            List<LibraryRolesDto> libraryRoles = facade.getLibraryService().findLibraryRoles(library.getId());
            
            mav.addObject("currentLibrary", library);
            mav.addObject("libraries", facade.getLibraryService().findLibraries());
            mav.addObject("availableLibraryRoles", availableLibraryRoles);
            mav.addObject("libraryRoles", libraryRoles);
        }
        
        List<Categories> freeCategories = facade.getRoleService().findFreeCategories(role.getId());
        List<Categories> roleCategories = facade.getRoleService().findRoleCategories(role.getId());
        
        mav.addObject("roles", roles);
        mav.addObject("currentRole", role);
        mav.addObject("freeCategories", freeCategories);
        mav.addObject("roleCategories", roleCategories);
        mav.addObject("saveCategories", new SaveRoleCategoriesCommand());
        mav.addObject("saveLibraryRoles", saveLibraryRoles);
        
        return mav;
    }
    
    @RequestMapping("/archive/preferences/role")
    public String changeCurrentRoleHandler(
            @RequestParam(required=false) Integer id, 
            HttpServletRequest request, 
            HttpServletResponse response) {
        
        ArchiveSession session = ArchiveSession.getSession(request);
        ContentManagementSystem cms = ContentManagementSystem.fromRequest(request);
        User user = cms.getCurrentUser();
        
        if (user.isDefaultUser()) {
            Utils.redirectToLogin(request, response, facade);
            
            return null;
        } else if (!user.isSuperAdmin()) {
            return "redirect:/web/archive";
        }
        
        Roles role = null;
        if (id != null && (role = facade.getRoleService().findRoleById(id)) != null) {
            session.put(ROLE_KEY, role);
        }
        
        return "redirect:/web/archive/preferences";
    }
    
    @RequestMapping("/archive/preferences/library")
    public String changeCurrentLibraryHander(
            @RequestParam(required=false) Integer id, 
            HttpServletRequest request, 
            HttpServletResponse response) {
        
        ArchiveSession session = ArchiveSession.getSession(request);
        ContentManagementSystem cms = ContentManagementSystem.fromRequest(request);
        User user = cms.getCurrentUser();
        
        if (user.isDefaultUser()) {
            Utils.redirectToLogin(request, response, facade);
            
            return null;
        } else if (!user.isSuperAdmin()) {
            return "redirect:/web/archive";
        }
        
        Libraries library = null;
        if (id != null && (library = facade.getLibraryService().findLibraryById(id)) != null) {
            session.put(LIBRARY_KEY, library);
        }
        
        return "redirect:/web/archive/preferences";
    }
    
    @RequestMapping("/archive/preferences/library/save")
    public ModelAndView saveLibraryRolesHandler(
            @ModelAttribute("saveLibraryRoles") SaveLibraryRolesCommand command, 
            BindingResult result, 
            HttpServletRequest request, 
            HttpServletResponse response) {
        
        ArchiveSession session = ArchiveSession.getSession(request);
        ContentManagementSystem cms = ContentManagementSystem.fromRequest(request);
        User user = cms.getCurrentUser();
        
        if (user.isDefaultUser()) {
            Utils.redirectToLogin(request, response, facade);
            
            return null;
        } else if (!user.isSuperAdmin()) {
            return new ModelAndView("redirect:/web/archive");
        }
        
        Libraries library = getLibrary(session, null);
        if (library == null) {
            return new ModelAndView("redirect:/web/archive/preferences");
        }
        
        if (command.getLibraryRoles() == null) {
            return new ModelAndView("redirect:/web/archive/preferences");
        }
        
        SaveLibraryRolesValidator validator = new SaveLibraryRolesValidator();
        ValidationUtils.invokeValidator(validator, command, result);
        
        if (result.hasErrors()) {
            ModelAndView mav = new ModelAndView("image_archive/pages/preferences");
             
            List<Roles> roles = facade.getRoleService().findRoles();
            Roles role = getRole(session, roles);
             
            List<Categories> freeCategories = facade.getRoleService().findFreeCategories(role.getId());
            List<Categories> roleCategories = facade.getRoleService().findRoleCategories(role.getId());

            mav.addObject("roles", roles);
            mav.addObject("currentRole", role);
            mav.addObject("freeCategories", freeCategories);
            mav.addObject("roleCategories", roleCategories);
            mav.addObject("saveCategories", new SaveRoleCategoriesCommand());
            
            List<Roles> availableLibraryRoles = facade.getLibraryService().findAvailableRoles(library.getId());
            List<LibraryRolesDto> libraryRoles = command.getLibraryRoles();
            
            for (LibraryRolesDto libraryRole : libraryRoles) {
                availableLibraryRoles.remove(new Roles(libraryRole.getRoleId()));
            }
            
            mav.addObject("currentLibrary", library);
            mav.addObject("libraries", facade.getLibraryService().findLibraries());
            mav.addObject("availableLibraryRoles", availableLibraryRoles);
            mav.addObject("libraryRoles", libraryRoles);
            
            return mav;
        }
        
        try {
            facade.getLibraryService().updateLibraryRoles(library.getId(), command.getLibraryNm(), command.getLibraryRoles());
            
            library.setLibraryNm(command.getLibraryNm());
        } catch (Exception ex) {
            log.warn(ex.getMessage(), ex);
        }
        
        return new ModelAndView("redirect:/web/archive/preferences");
    }
    
    @RequestMapping("/archive/preferences/role/save")
    public ModelAndView saveRoleCategoriesHandler(
            @ModelAttribute("saveCategories") SaveRoleCategoriesCommand command,
            HttpServletRequest request, 
            HttpServletResponse response) {
        
        ArchiveSession session = ArchiveSession.getSession(request);
        ContentManagementSystem cms = ContentManagementSystem.fromRequest(request);
        User user = cms.getCurrentUser();
        
        if (user.isDefaultUser()) {
            Utils.redirectToLogin(request, response, facade);
            
            return null;
        } else if (!user.isSuperAdmin()) {
            return new ModelAndView("redirect:/web/archive");
        }
        
        Roles role = getRole(session, null);
        if (role == null) {
            return new ModelAndView("redirect:/web/archive/preferences");
        }
        
        try {
            facade.getRoleService().assignCategoryRoles(role, command.getAssignedCategoryIds(), 
                    command.isCanUse(), command.isCanChange());
        } catch (Exception ex) {
            log.fatal(ex.getMessage(), ex);
        }
        
        return new ModelAndView("redirect:/web/archive/preferences");
    }
    
    
    private static Roles getRole(ArchiveSession session, List<Roles> roles) {
        Roles role = (Roles) session.get(ROLE_KEY);
        if (role == null && roles != null) {
            role = roles.get(0);
            session.put(ROLE_KEY, role);
        }
        
        return role;
    }
    
    private static Libraries getLibrary(ArchiveSession session, List<Libraries> libraries) {
        Libraries library = (Libraries) session.get(LIBRARY_KEY);
        if (library == null && libraries != null && !libraries.isEmpty()) {
            library = libraries.get(0);
            session.put(LIBRARY_KEY, library);
        }
        
        return library;
    }
}
