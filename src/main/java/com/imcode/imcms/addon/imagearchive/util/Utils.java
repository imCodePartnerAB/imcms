package com.imcode.imcms.addon.imagearchive.util;


import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;

import com.imcode.imcms.addon.imagearchive.command.SearchImageCommand;
import com.imcode.imcms.addon.imagearchive.dto.LibrariesDto;
import com.imcode.imcms.addon.imagearchive.dto.LibraryEntryDto;
import com.imcode.imcms.addon.imagearchive.entity.Categories;
import com.imcode.imcms.addon.imagearchive.entity.Images;
import com.imcode.imcms.addon.imagearchive.entity.Libraries;
import com.imcode.imcms.addon.imagearchive.entity.Roles;
import com.imcode.imcms.api.ContentManagementSystem;
import com.imcode.imcms.api.User;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.imcode.imcms.addon.imagearchive.service.Facade;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.validation.FieldError;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

public class Utils {
    private static final Log log = LogFactory.getLog(Utils.class);
    
    
    public static void addNoCacheHeaders(HttpServletResponse response) {
        response.setHeader("Cache-Control", "no-cache, must-revalidate, max_age=0, no-store");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "0");
    }

    public static void sendErrorCode(HttpServletResponse response, int statusCode) {
        try {
            response.sendError(statusCode);
        } catch (IOException ex) {
            log.warn(ex.getMessage(), ex);
        }
    }
    
    public static String makeKey(Class<?> klass, String suffix) {
        return String.format("%s.%s", klass.getName(), suffix);
    }
    
    public static Date min(Date date1, Date date2) {
        if (date1 == null && date2 == null) {
            return null;
        }
        
        if (date1 == null) {
            return date2;
        } else if (date2 == null) {
            return date1;
        }
        
        return (date1.getTime() < date2.getTime() ? date1 : date2);
    }
    
    public static Date max(Date date1, Date date2) {
        if (date1 == null && date2 == null) {
            return null;
        }
        
        if (date1 == null) {
            return date2;
        } else if (date2 == null) {
            return date1;
        }
        
        return (date1.getTime() < date2.getTime() ? date2 : date1);
    }
    
    public static void redirectToLogin(HttpServletRequest request, HttpServletResponse response, Facade facade) {
        try {
            response.sendRedirect(request.getContextPath() + "/login/");
        } catch (IOException ex) {
            log.warn(ex.getMessage(), ex);
        }
    }
        
    public static String encodeUrl(String value) {
        try {
            return URLEncoder.encode(value, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            log.warn(ex.getMessage(), ex);
        }
        
        return null;
    }
    
    public static String decodeUrl(String value) {
        try {
            return URLDecoder.decode(value, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            log.warn(ex.getMessage(), ex);
        }
        
        return null;
    }

    public static List<Integer> getLibrarySubLibriesIds(Libraries rootLibrary, List<Libraries> allLibraries) {
        List<Integer> subLibrariesIds = new ArrayList<Integer>();

        String path = rootLibrary.getFilepath();
        if (path != null) {
            File file = new File(path, rootLibrary.getFolderNm());
            try {
                getSubdirs(file, new FileFilter() {
                    public boolean accept(File file) {
                        String name = file.getName();

                        return file.isDirectory() && name.length() <= 255;
                    }
                }, subLibrariesIds, allLibraries);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return subLibrariesIds;
    }

    private static void getSubdirs(File file, FileFilter filter, List<Integer> subLibIds, List<Libraries> allLibs) throws IOException {
        if (file == null) {
            return;
        }

        File[] subDirsTmp = file.listFiles(filter);
        if (subDirsTmp == null) {
            subDirsTmp = new File[0];
        }

        List<File> subdirs = Arrays.asList(subDirsTmp);
        subdirs = new ArrayList<File>(subdirs);

        for (File subdir : subdirs) {
            Libraries subLib = matchPathToLibrary(subdir, allLibs);
            if(subLib == null) {
                return;
            }
            subLibIds.add(subLib.getId());
            getSubdirs(subdir, filter, subLibIds, allLibs);
        }
    }

    private static Libraries matchPathToLibrary(File path, List<Libraries> allLibraries) {
        for(Libraries lib: allLibraries) {
            if(lib.getFilepath() != null) {
                File f = new File(lib.getFilepath(), lib.getFolderNm());
                if(path.equals(f)) {
                    return lib;
                }
            }
        }

        return null;
    }

    public static void writeJSON(Object object, HttpServletResponse response) {
        MappingJacksonHttpMessageConverter jsonConverter = new MappingJacksonHttpMessageConverter();
        MediaType jsonMimeType = MediaType.parseMediaType("application/json");

        if(jsonConverter.canWrite(object.getClass(), jsonMimeType)) {
            try {
                jsonConverter.write(object, jsonMimeType, new ServletServerHttpResponse(response));
            } catch (IOException e) {
                log.fatal(e.getMessage(), e);
            }
        }
    }

    public static List<Images> isInArchive(File img, Facade facade) {
        List<Images> sameImages = new ArrayList<Images>();
        List<Images> archiveImages = facade.getImageService().getAllImages();
        for (Images image : archiveImages) {
            if (img != null && image.getImageNm().equals(img.getName()) && image.getFileSize() == img.length()) {
                sameImages.add(image);
            }
        }

        return sameImages;
    }

    public static List<Categories> getCategoriesRequiredToUse(Images img, Facade facade, User user) {
        if(user.isSuperAdmin()) {
            return Collections.emptyList();
        }
        
        List<Categories> userCategories = facade.getRoleService().findCategories(user, Roles.ALL_PERMISSIONS);
        List<Categories> imageCategories = img.getCategories();
        List<Categories> categoriesesUserCantUse = Collections.emptyList();
        if(imageCategories != null) {
            categoriesesUserCantUse = (List<Categories>) CollectionUtils.subtract(imageCategories, userCategories);
        }

        /* Not an image uploaded by the user, return image's categories the user can't use or edit(so they know what to
         get to be able to activate) */
        if(img.getUsersId() != user.getId() && categoriesesUserCantUse.size() > 0){
            return categoriesesUserCantUse;
        }

        return categoriesesUserCantUse;
    }
    
    private Utils() {
    }
}
