package com.imcode.imcms.flow;

import org.apache.commons.lang.NotImplementedException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public abstract class OkCancelPage extends Page {

    public static final String REQUEST_PARAMETER__OK = "ok";
    public static final String REQUEST_PARAMETER__CANCEL = "cancel";

    protected DispatchCommand okCommand;
    protected DispatchCommand cancelCommand;

    protected OkCancelPage( DispatchCommand okDispatchCommand, DispatchCommand cancelDispatchCommand ) {
        this.okCommand = okDispatchCommand;
        this.cancelCommand = cancelDispatchCommand;
    }

    public void dispatch( HttpServletRequest request, HttpServletResponse response ) throws IOException, ServletException {
        if ( wasCanceled( request ) ) {
            dispatchCancel( request, response );
        } else {
            dispatchNotCanceled(request, response);
        }
    }

    protected void dispatchNotCanceled(HttpServletRequest request,
                                     HttpServletResponse response) throws IOException, ServletException {
        updateFromRequest( request );
        if ( wasOk( request ) ) {
            dispatchOk( request, response );
        } else {
            dispatchOther( request, response );
        }
    }

    protected void dispatchOk( HttpServletRequest request, HttpServletResponse response ) throws IOException, ServletException {
        removeFromSession(request) ;
        okCommand.dispatch( request, response );
    }

    protected void dispatchCancel( HttpServletRequest request, HttpServletResponse response ) throws IOException, ServletException {
        removeFromSession(request) ;
        cancelCommand.dispatch( request, response );
    }

    protected void dispatchOther( HttpServletRequest request, HttpServletResponse response ) throws IOException, ServletException {
        throw new NotImplementedException( this.getClass() );
    }

    protected boolean wasOk( HttpServletRequest request ) {
        return null != request.getParameter( REQUEST_PARAMETER__OK );
    }

    protected boolean wasCanceled( HttpServletRequest request ) {
        return null != request.getParameter( REQUEST_PARAMETER__CANCEL );
    }

    protected abstract void updateFromRequest( HttpServletRequest request ) ;
}
