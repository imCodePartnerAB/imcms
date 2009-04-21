package com.imcode.imcms.addon.imagearchive.util;


import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.imcode.imcms.addon.imagearchive.service.Facade;

public class Utils {
    private static final Log log = LogFactory.getLog(Utils.class);
    
    
    public static void addNoCacheHeaders(HttpServletResponse response) {
        response.setHeader("Cache-Control", "no-cache, must-revalidate, max_age=0, no-store");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "0");
    }

    public static void sendErrorCode(HttpServletResponse response, int statusCode) {
        try {
            response.sendError(statusCode);
        } catch (IOException ex) {
            log.warn(ex.getMessage(), ex);
        }
    }
    
    public static String makeKey(Class<?> klass, String suffix) {
        return String.format("%s.%s", klass.getName(), suffix);
    }
    
    public static Date min(Date date1, Date date2) {
        if (date1 == null && date2 == null) {
            return null;
        }
        
        if (date1 == null) {
            return date2;
        } else if (date2 == null) {
            return date1;
        }
        
        return (date1.getTime() < date2.getTime() ? date1 : date2);
    }
    
    public static Date max(Date date1, Date date2) {
        if (date1 == null && date2 == null) {
            return null;
        }
        
        if (date1 == null) {
            return date2;
        } else if (date2 == null) {
            return date1;
        }
        
        return (date1.getTime() < date2.getTime() ? date2 : date1);
    }
    
    public static void redirectToLogin(HttpServletRequest request, HttpServletResponse response, Facade facade) {
        try {
            response.sendRedirect(request.getContextPath() + "/login/");
        } catch (IOException ex) {
            log.warn(ex.getMessage(), ex);
        }
    }
        
    public static String encodeUrl(String value) {
        try {
            return URLEncoder.encode(value, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            log.warn(ex.getMessage(), ex);
        }
        
        return null;
    }
    
    public static String decodeUrl(String value) {
        try {
            return URLDecoder.decode(value, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            log.warn(ex.getMessage(), ex);
        }
        
        return null;
    }
    
    private Utils() {
    }
}
