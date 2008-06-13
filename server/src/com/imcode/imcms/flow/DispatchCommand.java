package com.imcode.imcms.flow;

import java.io.IOException;
import java.io.Serializable;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface DispatchCommand extends Serializable {
    void dispatch( HttpServletRequest request, HttpServletResponse response ) throws IOException, ServletException;
}
