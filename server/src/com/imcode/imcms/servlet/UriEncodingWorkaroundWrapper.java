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

    UriEncodingWorkaroundWrapper(HttpServletRequest request, String encoding) {
        super(request);
        this.encoding = encoding;
        decoder = new FallbackDecoder(Charset.forName(Imcms.DEFAULT_ENCODING),
                                      Charset.forName(encoding));
    }

    public String getPathInfo() {
        return redecode(super.getPathInfo());
    }

    public String getParameter(String parameterName) {
        return redecode(super.getParameter(parameterName));
    }

    public String[] getParameterValues(String string) {
        String[] parameterValues = super.getParameterValues(string);
        return redecode(parameterValues);
    }

    public Map getParameterMap() {
        Map<String, String[]> parameterMap = super.getParameterMap();
        Map<String, String[]> result = new HashMap<String, String[]>();
        for ( Map.Entry<String, String[]> entry : parameterMap.entrySet() ) {
            result.put(entry.getKey(), redecode(entry.getValue())) ;
        }
        return result ;
    }

    private String[] redecode(String[] strings) {
        if (null == strings) {
            return null ;
        }
        String[] result = new String[strings.length];
        for ( int i = 0; i < strings.length; i++ ) {
            result[i] = redecode(strings[i]);
        }
        return result;
    }

    private String redecode(String string) {
        if (null == string) {
            return null ;
        }
        try {
            return decoder.decodeBytes(string.getBytes(encoding), string) ;
        } catch ( UnsupportedEncodingException e ) {
            throw new UnhandledException(e);
        }
    }
}
