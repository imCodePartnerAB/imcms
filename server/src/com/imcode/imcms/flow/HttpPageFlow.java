package com.imcode.imcms.flow;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serializable;

public abstract class HttpPageFlow implements Serializable {

    public static final String REQUEST_PARAMETER__PAGE = "page";
    public static final String REQUEST_PARAMETER__CANCEL_BUTTON = "cancel";
    public static final String REQUEST_PARAMETER__OK_BUTTON = "ok";

    public void dispatch(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String page = request.getParameter( REQUEST_PARAMETER__PAGE );
        if ( null != request.getParameter( REQUEST_PARAMETER__CANCEL_BUTTON ) ) {
            dispatchCancel(request,response) ;
        } else if (null == page) {
            dispatchToFirstPage( request, response ) ;
        } else if ( null != request.getParameter( REQUEST_PARAMETER__OK_BUTTON )) {
            dispatchOk( request, response, page ) ;
        } else {
            dispatchFromPage( request, response, page );
        }
    }

    protected abstract void dispatchToFirstPage( HttpServletRequest request, HttpServletResponse response ) throws IOException, ServletException;

    protected abstract void dispatchCancel( HttpServletRequest request, HttpServletResponse response ) throws IOException;

    protected abstract void dispatchOk( HttpServletRequest request, HttpServletResponse response, String page ) throws IOException, ServletException;

    protected abstract void dispatchFromPage(HttpServletRequest request, HttpServletResponse response, String page) throws IOException, ServletException;

}