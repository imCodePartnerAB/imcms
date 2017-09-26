package com.imcode.imcms.imagearchive.service;

import com.imcode.imcms.imagearchive.Config;
import com.imcode.imcms.imagearchive.service.file.ImageArchiveFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class Facade {
    @Autowired
    private Config config;
    @Autowired
    private ImageArchiveUserService userService;
    @Autowired
    private CommonService commonService;
    @Autowired
    private ImageArchiveFileService fileService;
    @Autowired
    private ImageArchiveImageService imageService;
    @Autowired
    private ImageArchiveRoleService roleService;
    @Autowired
    private LibraryService libraryService;
//    @Autowired
//    private CategoryService categoryService;
//    @Autowired
//    private LinkService linkService;


    public Facade() {
    }


    public Config getConfig() {
        return config;
    }

    public ImageArchiveUserService getUserService() {
        return userService;
    }

    public CommonService getCommonService() {
        return commonService;
    }

    public ImageArchiveFileService getFileService() {
        return fileService;
    }

    public ImageArchiveImageService getImageService() {
        return imageService;
    }

    public ImageArchiveRoleService getRoleService() {
        return roleService;
    }

    public LibraryService getLibraryService() {
        return libraryService;
    }

//    public CategoryService getCategoryService() {
//        return categoryService;
//    }

//    public LinkService getLinkService() {
//        return linkService;
//    }
}
