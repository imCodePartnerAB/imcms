package com.imcode.imcms.addon.imagearchive.tag.func;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import com.imcode.imcms.addon.imagearchive.command.SearchImageCommand;
import com.imcode.imcms.addon.imagearchive.dto.LibraryEntryDto;
import com.imcode.imcms.addon.imagearchive.entity.Categories;
import com.imcode.imcms.addon.imagearchive.entity.Images;
import com.imcode.imcms.addon.imagearchive.entity.Roles;
import com.imcode.imcms.addon.imagearchive.service.Facade;
import com.imcode.imcms.addon.imagearchive.util.Pagination;
import com.imcode.imcms.api.ContentManagementSystem;
import com.imcode.imcms.api.User;
import org.apache.commons.lang.StringUtils;
import org.apache.sanselan.common.RationalNumber;
import org.apache.sanselan.common.RationalNumberUtilities;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.jsp.PageContext;

public class Functions {
    private static final Pattern NEWLINE_PATTERN = Pattern.compile("\r\n?");
    
    public static String newlineToBr(String value) {
        if (value == null) {
            return null;
        } else {
            return NEWLINE_PATTERN.matcher(value).replaceAll("<br/>");
        }
    }
    
    public static String abbreviate(String value, int maxLength) {
        return StringUtils.abbreviate(value, maxLength);
    }
    
    public static String join(Collection<Object> elements, String separator) {
        if (elements == null || elements.isEmpty()) {
            return "";
        }
        
        StringBuilder builder = new StringBuilder();
        
        Iterator<Object> it = elements.iterator();
        while (it.hasNext()) {
            builder.append(it.next());
            
            if (it.hasNext()) {
                builder.append(separator);
            }
        }
        
        return builder.toString();
    }

    public static boolean isInArchive(LibraryEntryDto img, PageContext pageContext) {
        WebApplicationContext context = WebApplicationContextUtils.getWebApplicationContext(pageContext.getServletContext());
        Facade facade = context.getBean(com.imcode.imcms.addon.imagearchive.service.Facade.class);


        SearchImageCommand searchImageCommand = new SearchImageCommand();
        searchImageCommand.setCategoryId(SearchImageCommand.CATEGORY_ALL);
        ContentManagementSystem cms = ContentManagementSystem.fromRequest(pageContext.getRequest());
        User user = cms.getCurrentUser();
        List<Categories> categories = facade.getRoleService().findCategories(user, Roles.ALL_PERMISSIONS);
        List<Integer> categoryIds = new ArrayList<Integer>(categories.size());
        for (Categories category : categories) {
            categoryIds.add(category.getId());
        }

        int imageCount = facade.getImageService().searchImagesCount(searchImageCommand, categoryIds, user);
        Pagination pag = new Pagination();
        pag.setPageSize(imageCount);
        List<Images> archiveImages = facade.getImageService().searchImages(searchImageCommand, pag, categoryIds, user);
        for (Images image : archiveImages) {
            if (img != null && image.getImageNm().equals(img.getFileName()) && image.getFileSize() == img.getFileSize()) {
                return true;
            }
        }

        return false;
    }

    public static String doubleToFractionsString(Double number) {
        if(number == null) {
            return null;
        }

        RationalNumber rationalNumber = RationalNumberUtilities.getRationalNumber(number);
        return rationalNumber.numerator + "/" + rationalNumber.divisor;
    }
}
