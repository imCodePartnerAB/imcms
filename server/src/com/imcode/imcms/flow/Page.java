package com.imcode.imcms.flow;

import imcode.util.Html;
import imcode.util.HttpSessionAttribute;
import imcode.util.HttpSessionUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serializable;

public abstract class Page implements Serializable, HttpSessionAttribute {

    public static final String IN_REQUEST = "page";

    private String sessionAttributeName;

    public static String htmlHidden( HttpServletRequest request ) {
        Page page = Page.fromRequest(request);
        if (null == page) {
            return "";
        }
        return Html.hidden( IN_REQUEST, page.getSessionAttributeName() ) ;
    }

    public static <E extends Page> E fromRequest( HttpServletRequest request ) {
        return (E) HttpSessionUtils.getSessionAttributeWithNameInRequest( request, IN_REQUEST );
    }

    public void forward( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException {
        HttpSessionUtils.setSessionAttributeAndSetNameInRequestAttribute( this, request, IN_REQUEST );
        request.getRequestDispatcher( getPath(request) ).forward( request, response );
    }

    public String getSessionAttributeName() {
        return sessionAttributeName ;
    }

    public void setSessionAttributeName( String sessionAttributeName ) {
        this.sessionAttributeName = sessionAttributeName;
    }

    protected void removeFromSession( HttpServletRequest request ) {
        HttpSessionUtils.removeSessionAttribute( request, getSessionAttributeName() ) ;
    }

    public abstract void dispatch( HttpServletRequest request, HttpServletResponse response ) throws IOException, ServletException ;

    public abstract String getPath( HttpServletRequest request ) ;

}
