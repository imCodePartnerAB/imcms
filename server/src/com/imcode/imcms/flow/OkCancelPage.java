package com.imcode.imcms.flow;

import com.imcode.imcms.flow.DispatchCommand;
import org.apache.commons.lang.NotImplementedException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public abstract class OkCancelPage extends Page {

    public static final String REQUEST_PARAMETER__OK = "ok";
    public static final String REQUEST_PARAMETER__CANCEL = "cancel";

    protected DispatchCommand okDispatchCommand;
    protected DispatchCommand cancelDispatchCommand;

    protected OkCancelPage( DispatchCommand okDispatchCommand, DispatchCommand cancelDispatchCommand ) {
        this.okDispatchCommand = okDispatchCommand;
        this.cancelDispatchCommand = cancelDispatchCommand;
    }

    public void dispatch( HttpServletRequest request, HttpServletResponse response ) throws IOException, ServletException {
        if ( wasCanceled( request ) ) {
            dispatchCancel( request, response );
        } else {
            updateFromRequest( request );
            if ( wasOk( request ) ) {
                dispatchOk( request, response );
            } else {
                dispatchOther( request, response );
            }
        }
    }

    protected void dispatchOk( HttpServletRequest request, HttpServletResponse response ) throws IOException, ServletException {
        okDispatchCommand.dispatch( request, response );
    }

    protected void dispatchCancel( HttpServletRequest request, HttpServletResponse response ) throws IOException, ServletException {
        cancelDispatchCommand.dispatch( request, response );
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
