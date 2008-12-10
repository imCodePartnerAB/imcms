package com.imcode.imcms.flow;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serializable;

public interface DispatchCommand extends Serializable {
    void dispatch( HttpServletRequest request, HttpServletResponse response ) throws IOException, ServletException;
}
