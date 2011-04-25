package com.imcode.imcms.addon.imagearchive.tag;

import com.imcode.imcms.addon.imagearchive.command.SearchImageCommand;
import com.imcode.imcms.addon.imagearchive.dto.LibrariesDto;
import com.imcode.imcms.addon.imagearchive.dto.LibraryEntryDto;
import com.imcode.imcms.addon.imagearchive.entity.Categories;
import com.imcode.imcms.addon.imagearchive.entity.Images;
import com.imcode.imcms.addon.imagearchive.entity.Roles;
import com.imcode.imcms.addon.imagearchive.service.Facade;
import com.imcode.imcms.addon.imagearchive.util.Pagination;
import com.imcode.imcms.addon.imagearchive.util.Utils;
import com.imcode.imcms.api.ContentManagementSystem;
import com.imcode.imcms.api.User;
import imcode.server.Imcms;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ArchiveUtils extends TagSupport {
    @Override
    public int doStartTag() throws JspException {
        JspWriter out = pageContext.getOut();
        try {
            out.print(isInArchive());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return SKIP_BODY;
    }

    private boolean isInArchive() {
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
            if (image.getImageNm().equals(getImage().getFileName()) && image.getFileSize() == getImage().getFileSize()) {
                return true;
            }
        }

        return false;
    }


    public LibraryEntryDto getImage() {
        return image;
    }

    public void setImage(LibraryEntryDto image) {
        this.image = image;
    }

    private LibraryEntryDto image;
}
