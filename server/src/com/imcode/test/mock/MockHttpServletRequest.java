package com.imcode.test.mock;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpSession;
import javax.servlet.ServletInputStream;
import javax.servlet.RequestDispatcher;
import java.util.*;
import java.security.Principal;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.UnsupportedEncodingException;

public class MockHttpServletRequest implements HttpServletRequest {

    Map parameterMap = new HashMap() ;
    private String contextPath;
    private String requestURL;

    public String getAuthType() {
        return null;  // TODO
    }

    public Cookie[] getCookies() {
        return new Cookie[0];  // TODO
    }

    public long getDateHeader( String string ) {
        return 0;  // TODO
    }

    public String getHeader( String string ) {
        return null;  // TODO
    }

    public Enumeration getHeaders( String string ) {
        return null;  // TODO
    }

    public Enumeration getHeaderNames() {
        return null;  // TODO
    }

    public int getIntHeader( String string ) {
        return 0;  // TODO
    }

    public String getMethod() {
        return null;  // TODO
    }

    public String getPathInfo() {
        return null;  // TODO
    }

    public String getPathTranslated() {
        return null;  // TODO
    }

    public String getContextPath() {
        return contextPath ;
    }

    public String getQueryString() {
        return null;  // TODO
    }

    public String getRemoteUser() {
        return null;  // TODO
    }

    public boolean isUserInRole( String string ) {
        return false;  // TODO
    }

    public Principal getUserPrincipal() {
        return null;  // TODO
    }

    public String getRequestedSessionId() {
        return null;  // TODO
    }

    public String getRequestURI() {
        return null;  // TODO
    }

    public StringBuffer getRequestURL() {
        return new StringBuffer(requestURL) ;
    }

    public String getServletPath() {
        return null;  // TODO
    }

    public HttpSession getSession( boolean b ) {
        return null;  // TODO
    }

    public HttpSession getSession() {
        return null;  // TODO
    }

    public boolean isRequestedSessionIdValid() {
        return false;  // TODO
    }

    public boolean isRequestedSessionIdFromCookie() {
        return false;  // TODO
    }

    public boolean isRequestedSessionIdFromURL() {
        return false;  // TODO
    }

    public boolean isRequestedSessionIdFromUrl() {
        return false;  // TODO
    }

    public Object getAttribute( String string ) {
        return null;  // TODO
    }

    public Enumeration getAttributeNames() {
        return null;  // TODO
    }

    public String getCharacterEncoding() {
        return null;  // TODO
    }

    public void setCharacterEncoding( String string ) throws UnsupportedEncodingException {
        // TODO
    }

    public int getContentLength() {
        return 0;  // TODO
    }

    public String getContentType() {
        return null;  // TODO
    }

    public ServletInputStream getInputStream() throws IOException {
        return null;  // TODO
    }

    public String getParameter( String parameterName ) {
        String[] parameterValues = getParameterValues( parameterName ) ;
        String parameterValue = null ;
        if (null != parameterValues && parameterValues.length > 0) {
            parameterValue = parameterValues[0] ;
        }
        return parameterValue ;
    }

    public Enumeration getParameterNames() {
        return null;  // TODO
    }

    public String[] getParameterValues( String parameterName ) {
        return (String[])parameterMap.get( parameterName ) ;
    }

    public Map getParameterMap() {
        return parameterMap ;
    }

    public String getProtocol() {
        return null;  // TODO
    }

    public String getScheme() {
        return null;  // TODO
    }

    public String getServerName() {
        return null;  // TODO
    }

    public int getServerPort() {
        return 0;  // TODO
    }

    public BufferedReader getReader() throws IOException {
        return null;  // TODO
    }

    public String getRemoteAddr() {
        return null;  // TODO
    }

    public String getRemoteHost() {
        return null;  // TODO
    }

    public void setAttribute( String string, Object object ) {
        // TODO
    }

    public void removeAttribute( String string ) {
        // TODO
    }

    public Locale getLocale() {
        return null;  // TODO
    }

    public Enumeration getLocales() {
        return null;  // TODO
    }

    public boolean isSecure() {
        return false;  // TODO
    }

    public RequestDispatcher getRequestDispatcher( String string ) {
        return null;  // TODO
    }

    public String getRealPath( String string ) {
        return null;  // TODO
    }

    public int getRemotePort() {
        return 0;
    }

    public String getLocalName() {
        return null;
    }

    public String getLocalAddr() {
        return null;
    }

    public int getLocalPort() {
        return 0;
    }

    public void setupAddParameter( String parameterName, String[] parameterValues ) {
        parameterMap.put( parameterName, parameterValues ) ;
    }

    public void setupAddParameter( String parameterName, String parameterValue ) {
        setupAddParameter( parameterName, new String[] { parameterValue } );
    }

    public void setupContextPath( String contextPath ) {
        this.contextPath = contextPath ;
    }

    public void setupRequestURL( String requestURL ) {
        this.requestURL = requestURL ;
    }
}
