package com.imcode.imcms.imagearchive.service;

import com.imcode.imcms.api.linker.LinkService;
import com.imcode.imcms.imagearchive.Config;
import com.imcode.imcms.imagearchive.service.file.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class Facade {
    @Autowired
    private Config config;
    @Autowired
    private UserService userService;
    @Autowired
    private CommonService commonService;
    @Autowired
    private FileService fileService;
    @Autowired
    private ImageService imageService;
    @Autowired
    private RoleService roleService;
    @Autowired
    private LibraryService libraryService;
//    @Autowired
//    private CategoryService categoryService;
    @Autowired
    private LinkService linkService;


    public Facade() {
    }


    public Config getConfig() {
        return config;
    }

    public UserService getUserService() {
        return userService;
    }

    public CommonService getCommonService() {
        return commonService;
    }

    public FileService getFileService() {
        return fileService;
    }

    public ImageService getImageService() {
        return imageService;
    }

    public RoleService getRoleService() {
        return roleService;
    }

    public LibraryService getLibraryService() {
        return libraryService;
    }

//    public CategoryService getCategoryService() {
//        return categoryService;
//    }

    public LinkService getLinkService() {
        return linkService;
    }
}
