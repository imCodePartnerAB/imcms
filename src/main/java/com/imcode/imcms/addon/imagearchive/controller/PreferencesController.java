package com.imcode.imcms.addon.imagearchive.controller;

import com.imcode.imcms.addon.imagearchive.command.*;
import com.imcode.imcms.addon.imagearchive.dto.LibraryRolesDto;
import com.imcode.imcms.addon.imagearchive.entity.Categories;
import com.imcode.imcms.addon.imagearchive.entity.CategoryRoles;
import com.imcode.imcms.addon.imagearchive.entity.Libraries;
import com.imcode.imcms.addon.imagearchive.entity.Roles;
import com.imcode.imcms.addon.imagearchive.service.Facade;
import com.imcode.imcms.addon.imagearchive.service.exception.CategoryExistsException;
import com.imcode.imcms.addon.imagearchive.util.ArchiveSession;
import com.imcode.imcms.addon.imagearchive.util.Utils;
import com.imcode.imcms.addon.imagearchive.validator.CreateCategoryValidator;
import com.imcode.imcms.addon.imagearchive.validator.EditCategoryValidator;
import com.imcode.imcms.addon.imagearchive.validator.SaveLibraryRolesValidator;
import com.imcode.imcms.api.CategoryType;
import com.imcode.imcms.api.CategoryTypeAlreadyExistsException;
import com.imcode.imcms.api.ContentManagementSystem;
import com.imcode.imcms.api.User;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ValidationUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Controller
public class PreferencesController {
    private static final Log log = LogFactory.getLog(PreferencesController.class);
    
    private static final String ROLE_KEY = Utils.makeKey(PreferencesController.class, "role");
    private static final String LIBRARY_KEY = Utils.makeKey(PreferencesController.class, "library");
    
    @Autowired
    private Facade facade;
    
    
    @RequestMapping("/archive/preferences")
    public String indexHandler(
            @ModelAttribute PreferencesActionCommand actionCommand,

            @ModelAttribute("createCategory") CreateCategoryCommand createCategoryCommand,
            BindingResult createCategoryResult,

            @ModelAttribute("editCategory") EditCategoryCommand editCategoryCommand,
            BindingResult editCategoryResult,

            @ModelAttribute("saveLibraryRoles") SaveLibraryRolesCommand librariesCommand,
            BindingResult librariesResult,

            @ModelAttribute("saveCategories") SaveRoleCategoriesCommand roleCategoriesCommand,

            HttpServletRequest request,
            Map<String, Object> model) {
        
        ArchiveSession session = ArchiveSession.getSession(request);
        ContentManagementSystem cms = ContentManagementSystem.fromRequest(request);
        User user = cms.getCurrentUser();
        
        if (!user.isSuperAdmin()) {
            return "redirect:/web/archive";
        }
        
        facade.getLibraryService().syncLibraryFolders();


        List<Roles> roles = facade.getRoleService().findRoles();
        Roles role = getRole(session, roles);
        Roles previousRole = (Roles)session.get("previousRole");
        if(previousRole != null && !role.equals(previousRole)) {
            model.put("editingRoles", true);
        }
        session.put("previousRole", role);
        model.put("currentRole", role);

        /* First gets all libraries, then get first level folders(no subfolders) of libraries and match them with libraries,
        getting first level libraries.
        Subfolders inherit permissions from their first level folders */
        List<Libraries> libraries = facade.getLibraryService().findLibraries();
        final List<File> firstLevelLibraries = facade.getFileService().listFirstLevelLibraryFolders();
        CollectionUtils.filter(libraries, new Predicate() {
            public boolean evaluate(Object o) {
                Libraries lib = (Libraries) o;
                return firstLevelLibraries.contains(new File(lib.getFilepath(), lib.getFolderNm()));
            }
        });

        Libraries library = getLibrary(session, libraries);
        Libraries previousLibrary = (Libraries) session.get("previousLibrary");
        if(previousLibrary != null && !library.equals(previousLibrary)) {
            model.put("editingLibraries", true);
        }
        session.put("previousLibrary", library);
        model.put("currentLibrary", library);

        CategoryType imagesCategoryType = cms.getDocumentService().getCategoryType("Images");
        if(imagesCategoryType == null) {
            try {
                imagesCategoryType = cms.getDocumentService().createNewCategoryType("Images", 0);
            } catch (CategoryTypeAlreadyExistsException e) {
                e.printStackTrace();
            }
        }

        if (actionCommand.isCreateCategory()) {
            createCategoryCommand.setCreateCategoryType(imagesCategoryType.getId());
            processCreateCategory(createCategoryCommand, createCategoryResult);
            model.put("editingCategories", true);
            
        } else if (actionCommand.isEditCategory()) {
            processEditCategory(editCategoryCommand);
            model.put("editingCategories", true);
            
        } else if (actionCommand.isSaveCategory()) {
            editCategoryCommand.setEditCategoryType(imagesCategoryType.getId());
            processSaveCategory(editCategoryCommand, editCategoryResult);
            model.put("editingCategories", true);
            
        } else if (actionCommand.isRemoveCategory()) {
            processRemoveCategory(editCategoryCommand);
            model.put("editingCategories", true);
            
        } else if (actionCommand.isSaveLibraryRoles() && library != null && librariesCommand.getLibraryRoles() != null) {
            processSaveLibraryRoles(librariesCommand, librariesResult, model);
            model.put("editingLibraries", true);
            
        } else if (actionCommand.isSaveRoleCategories()) {
            processSaveRoleCategories(roleCategoriesCommand, model);
            model.put("editingRoles", true);
            
        }
        
        if (!actionCommand.isSaveLibraryRoles() && library != null) {
            librariesCommand.setLibraryNm(library.getLibraryNm());
        }

        /* after operations for up-to-date properties */
        List<Roles> availableLibraryRoles = Collections.emptyList();
        List<LibraryRolesDto> libraryRoles = Collections.emptyList();

        if (library != null) {
            availableLibraryRoles = facade.getLibraryService().findRoles();

            libraryRoles = facade.getLibraryService().findLibraryRoles(library.getId());
        }

        model.put("availableLibraryRoles", availableLibraryRoles);
        model.put("libraryRoles", libraryRoles);


        List<CategoryRoles> categoryRoles = facade.getCategoryService().findCategoryRoles(role);
        model.put("categoryRoles", categoryRoles);
        
        model.put("categories", facade.getCategoryService().getCategories());
        
        model.put("roles", roles);

        model.put("libraries", libraries);
        model.put("freeCategories", facade.getRoleService().findFreeCategories(role.getId()));
        model.put("roleCategories", facade.getRoleService().findRoleCategories(role.getId()));
        model.put("allCategories", facade.getRoleService().findAllCategories());

        return "image_archive/pages/preferences";
    }
    
    private void processCreateCategory(CreateCategoryCommand command, BindingResult result) {
        CreateCategoryValidator validator = new CreateCategoryValidator();
        ValidationUtils.invokeValidator(validator, command, result);
        
        if (!result.hasErrors()) {
            String categoryName = command.getCreateCategoryName();
            int categoryTypeId = command.getCreateCategoryType();
            
            try {
                facade.getCategoryService().createCategory(categoryName, categoryTypeId);
                
                command.setCreateCategoryName("");
                command.setCreateCategoryType(0);
            } catch (CategoryExistsException ex) {
                result.rejectValue("createCategoryName", "archive.preferences.categoryExistsError");
            }
        }
    }
    
    private void processEditCategory(EditCategoryCommand command) {
        Categories category = facade.getCategoryService().getCategory(command.getEditCategoryId());
        if (category != null) {
            command.setShowEditCategory(true);
            command.setEditCategoryName(category.getName());
            command.setEditCategoryType(category.getTypeId());
        }
    }
    
    private void processSaveCategory(EditCategoryCommand command, BindingResult result) {
        EditCategoryValidator validator = new EditCategoryValidator();
        ValidationUtils.invokeValidator(validator, command, result);
        
        if (!result.hasErrors()) {
            int categoryId = command.getEditCategoryId();
            int typeId = command.getEditCategoryType();
            String newName = command.getEditCategoryName();
            
            try {
                facade.getCategoryService().updateCategory(categoryId, newName, typeId);
                
            } catch (CategoryExistsException ex) {
                result.rejectValue("editCategoryName", "archive.preferences.categoryExistsError");
            }
        }
    }
    
    private void processRemoveCategory(EditCategoryCommand command) {
        facade.getCategoryService().deleteCategory(command.getEditCategoryId());
        
        command.setEditCategoryName("");
        command.setShowEditCategory(false);
    }
    
    @SuppressWarnings("unchecked")
    private void processSaveLibraryRoles(SaveLibraryRolesCommand command, BindingResult result, Map<String, Object> model) {
        SaveLibraryRolesValidator validator = new SaveLibraryRolesValidator();
        ValidationUtils.invokeValidator(validator, command, result);
        
        Libraries library = (Libraries) model.get("currentLibrary");
        
        if (!result.hasErrors()) {
            try {
                facade.getLibraryService().updateLibraryRoles(library.getId(), command.getLibraryNm(), command.getLibraryRoles());
                
                library.setLibraryNm(command.getLibraryNm());
            } catch (Exception ex) {
                log.warn(ex.getMessage(), ex);
            }
        }
    }
    
    private void processSaveRoleCategories(SaveRoleCategoriesCommand command, Map<String, Object> model) {
        try {
            Roles role = (Roles) model.get("currentRole");
            
            facade.getRoleService().assignCategoryRoles(role, command.getAssignedCategoryIds());
        } catch (Exception ex) {
            log.fatal(ex.getMessage(), ex);
        }
    }
    
    
    @RequestMapping("/archive/preferences/role")
    public String changeCurrentRoleHandler(
            @RequestParam(required = false) Integer id,
            HttpServletRequest request) {
        
        ArchiveSession session = ArchiveSession.getSession(request);
        ContentManagementSystem cms = ContentManagementSystem.fromRequest(request);
        User user = cms.getCurrentUser();
        
        if (!user.isSuperAdmin()) {
            return "redirect:/web/archive";
        }
        
        Roles role;
        if (id != null && (role = facade.getRoleService().findRoleById(id)) != null) {
            session.put(ROLE_KEY, role);
        }
        
        return "redirect:/web/archive/preferences";
    }
    
    @RequestMapping("/archive/preferences/library")
    public String changeCurrentLibraryHander(
            @RequestParam(required = false) Integer id,
            HttpServletRequest request) {
        
        ArchiveSession session = ArchiveSession.getSession(request);
        ContentManagementSystem cms = ContentManagementSystem.fromRequest(request);
        User user = cms.getCurrentUser();
        
        if (!user.isSuperAdmin()) {
            return "redirect:/web/archive";
        }
        
        Libraries library;
        if (id != null && (library = facade.getLibraryService().findLibraryById(id)) != null) {
            session.put(LIBRARY_KEY, library);
        }
        
        return "redirect:/web/archive/preferences";
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
        
        if (library != null && libraries != null) {
            boolean exists = false;
            for (Libraries lib : libraries) {
                if (lib.getId() == library.getId()) {
                    exists = true;
                    break;
                }
            }
            
            if (!exists) {
                library = null;
                session.remove(LIBRARY_KEY);
            }
        }
        
        if (library == null && libraries != null && !libraries.isEmpty()) {
            library = libraries.get(0);
            session.put(LIBRARY_KEY, library);
        }
        
        return library;
    }
}
