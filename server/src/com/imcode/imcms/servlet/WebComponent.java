package com.imcode.imcms.servlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class WebComponent {

    private CancelCommand cancelCommand;

    public void cancel( HttpServletRequest request, HttpServletResponse response ) throws IOException, ServletException {
        cancelCommand.cancel(request,response) ;
    }

    public boolean isCancelable() {
        return null != cancelCommand;
    }

    public void setCancelCommand( CancelCommand cancelCommand ) {
        this.cancelCommand = cancelCommand;
    }

    public interface CancelCommand {
        public void cancel( HttpServletRequest request, HttpServletResponse response ) throws IOException, ServletException;
    }

}
