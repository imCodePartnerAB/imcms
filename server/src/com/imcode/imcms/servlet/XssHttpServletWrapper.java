package com.imcode.imcms.servlet;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

public class XssHttpServletWrapper extends HttpServletRequestWrapper {

    private String newUrl;
    @Getter
    @Setter
    private boolean isVisit;

    public XssHttpServletWrapper(HttpServletRequest request, String newUrl) {
        super(request);
        this.newUrl = newUrl;
    }

    @Override
    public ServletRequest getRequest() {
        return super.getRequest();
    }

    @Override
    public String[] getParameterValues(String name) {
        return super.getParameterValues(name);
    }

    public String getNewUrl() {
        return newUrl;
    }

    @Override
    public StringBuffer getRequestURL() {
        return StringUtils.isNotBlank(newUrl) ? new StringBuffer(newUrl) : super.getRequestURL();
    }
}
