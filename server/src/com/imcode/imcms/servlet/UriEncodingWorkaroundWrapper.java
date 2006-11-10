package com.imcode.imcms.servlet;

import imcode.server.Imcms;
import imcode.util.FallbackDecoder;
import org.apache.commons.lang.UnhandledException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

class UriEncodingWorkaroundWrapper extends HttpServletRequestWrapper {

    private FallbackDecoder decoder;
    private String encoding ; 
    private boolean redecodeParameters ;
    UriEncodingWorkaroundWrapper(HttpServletRequest request, String encoding) {
        super(request);
        this.encoding = encoding;
        decoder = new FallbackDecoder(Charset.forName(Imcms.DEFAULT_ENCODING),
                                      Charset.forName(encoding));
        String method = getMethod();
        redecodeParameters = "GET".equals(method) || "HEAD".equals(method) ;
    }

    public String getPathInfo() {
        String pathInfo = super.getPathInfo();
        return redecode(pathInfo, "path-info "+pathInfo);
    }

    public String getParameter(String parameterName) {
        String parameterValue = super.getParameter(parameterName);
        return redecodeParameters ? redecode(parameterValue, "parameter "+parameterName) : parameterValue;
    }

    public String[] getParameterValues(String parameterName) {
        String[] parameterValues = super.getParameterValues(parameterName);
        return redecodeParameters ? redecode(parameterValues, "parameter "+parameterName) : parameterValues;
    }

    public Map getParameterMap() {
        Map<String, String[]> parameterMap = super.getParameterMap();
        if (redecodeParameters) {
            Map<String, String[]> result = new HashMap<String, String[]>();
            for ( Map.Entry<String, String[]> entry : parameterMap.entrySet() ) {
                result.put(entry.getKey(), redecode(entry.getValue(), entry.getKey())) ;
            }
            parameterMap = result ;
        }
        return parameterMap ;
    }

    private String[] redecode(String[] parameterValues, String parameterName) {
        if (null == parameterValues ) {
            return null ;
        }
        String[] result = new String[parameterValues.length];
        for ( int i = 0; i < parameterValues.length; i++ ) {
            result[i] = redecode(parameterValues[i], parameterName);
        }
        return result;
    }

    private String redecode(String parameterValue, String parameterName) {
        if (null == parameterValue ) {
            return null ;
        }
        try {
            return decoder.decodeBytes(parameterValue.getBytes(encoding), parameterName) ;
        } catch ( UnsupportedEncodingException e ) {
            throw new UnhandledException(e);
        }
    }
}
