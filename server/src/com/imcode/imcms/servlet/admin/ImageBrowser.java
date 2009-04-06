package com.imcode.imcms.servlet.admin;

import com.imcode.imcms.servlet.WebComponent;
import imcode.util.HttpSessionUtils;
import imcode.util.image.ImageInfo;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serializable;

public class ImageBrowser extends WebComponent {

    public static final String REQUEST_ATTRIBUTE_OR_PARAMETER__IMAGE_BROWSER = "imageBrowser";

    private SelectImageUrlCommand selectImageUrlCommand;

    public void setSelectImageUrlCommand( SelectImageUrlCommand selectImageUrlCommand ) {
        this.selectImageUrlCommand = selectImageUrlCommand;
    }

    public void forward( HttpServletRequest request, HttpServletResponse response ) throws IOException, ServletException {
        HttpSessionUtils.setSessionAttributeAndSetNameInRequestAttribute( this, request, REQUEST_ATTRIBUTE_OR_PARAMETER__IMAGE_BROWSER);
        ImageBrowse.browse( null, false, request, response );
    }

    public void selectImageUrl( String imageUrl, HttpServletRequest request, HttpServletResponse response ) throws IOException, ServletException {
        selectImageUrlCommand.selectImageUrl( imageUrl, request, response );
    }

    public static interface SelectImageUrlCommand extends Serializable {
        void selectImageUrl(String imageUrl, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException;
    }
}
