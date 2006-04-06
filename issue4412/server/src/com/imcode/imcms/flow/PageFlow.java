package com.imcode.imcms.flow;

import imcode.util.HttpSessionUtils;
import imcode.util.HttpSessionAttribute;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serializable;

public abstract class PageFlow implements Serializable, HttpSessionAttribute {

    public static final String REQUEST_PARAMETER__PAGE = "flowpage";
    public static final String REQUEST_PARAMETER__CANCEL_BUTTON = "cancel";
    public static final String REQUEST_PARAMETER__OK_BUTTON = "ok";
    public static final String REQUEST_ATTRIBUTE_OR_PARAMETER__FLOW = "flow";

    protected DispatchCommand returnCommand;
    private String sessionAttributeName;

    protected PageFlow( DispatchCommand returnCommand ) {
        this.returnCommand = returnCommand;
    }

    public String getSessionAttributeName() {
        return sessionAttributeName ;
    }

    public void setSessionAttributeName( String sessionAttributeName ) {
        this.sessionAttributeName = sessionAttributeName ;
    }

    public void dispatch(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        HttpSessionUtils.setSessionAttributeAndSetNameInRequestAttribute( this, request, REQUEST_ATTRIBUTE_OR_PARAMETER__FLOW );
        String page = request.getParameter( REQUEST_PARAMETER__PAGE );
        if ( null != request.getParameter( REQUEST_PARAMETER__CANCEL_BUTTON ) ) {
            dispatchReturn( request, response );
        } else if (null == page) {
            dispatchToFirstPage( request, response ) ;
        } else if ( null != request.getParameter( REQUEST_PARAMETER__OK_BUTTON )) {
            dispatchOk( request, response, page ) ;
        } else {
            dispatchFromPage( request, response, page );
        }
    }

    protected void dispatchReturn( HttpServletRequest request, HttpServletResponse response ) throws IOException, ServletException {
        HttpSessionUtils.removeSessionAttribute(request, getSessionAttributeName()) ;
        returnCommand.dispatch( request, response );
    }

    protected abstract void dispatchToFirstPage( HttpServletRequest request, HttpServletResponse response ) throws IOException, ServletException;

    protected abstract void dispatchOk( HttpServletRequest request, HttpServletResponse response, String page ) throws IOException, ServletException;

    protected abstract void dispatchFromPage(HttpServletRequest request, HttpServletResponse response, String page) throws IOException, ServletException;

}