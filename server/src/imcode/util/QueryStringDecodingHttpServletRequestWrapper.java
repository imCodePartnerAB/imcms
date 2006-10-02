package imcode.util;

import org.apache.commons.collections.MultiHashMap;
import org.apache.commons.collections.iterators.IteratorEnumeration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.ByteArrayOutputStream;
import java.nio.charset.Charset;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import imcode.server.Imcms;

public class QueryStringDecodingHttpServletRequestWrapper extends HttpServletRequestWrapper {

    private FallbackDecoder decoder;
    private Pattern parameterBytePattern = Pattern.compile("%[0-9a-fA-F][0-9a-fA-F]|.", Pattern.DOTALL);
    private Map<String, String[]> parameterMap = new HashMap<String, String[]>();

    public QueryStringDecodingHttpServletRequestWrapper(HttpServletRequest request) {
        super(request);
        decoder = new FallbackDecoder(Charset.forName(Imcms.DEFAULT_ENCODING), Charset.forName(Imcms.ISO_8859_1_ENCODING));
        decode(request);
    }

    private void decode(HttpServletRequest request) {
        String queryString = request.getQueryString();
        if (null == queryString) {
            return ;
        }
        String[] parameterPairs = queryString.split("[&;]");
        MultiHashMap localParameterMap = new MultiHashMap();
        for ( String parameterPair: parameterPairs ) {
            if (0 == parameterPair.length()) {
                continue;
            }
            String[] nameAndValue = parameterPair.split("=", 2) ;
            String parameterName = decode(nameAndValue[0]) ;
            String parameterValue = decode(nameAndValue[1]);
            localParameterMap.put(parameterName, parameterValue) ;
        }
        for ( Map.Entry<String, Collection<String>> entry : (Set<Map.Entry<String, Collection<String>>>)localParameterMap.entrySet() ) {
            String parameterName = entry.getKey();
            Collection<String> parameterValues = entry.getValue();
            parameterMap.put(parameterName, parameterValues.toArray(new String[parameterValues.size()])) ;
        }
    }

    private String decode(String encodedString) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        Matcher matcher = parameterBytePattern.matcher(encodedString);
        while (matcher.find()) {
            int c ;
            String match = matcher.group();
            if (3 == match.length()) {
                c = Integer.parseInt(match.substring(1), 16) ;
            } else if (1 == match.length()) {
                c = match.charAt(0);
                if (c == '+') {
                    c = ' ';
                }
            } else {
                throw new RuntimeException("Illegal match length.") ;
            }
            bytes.write(c);
        }
        encodedString = decoder.decodeBytes(bytes.toByteArray(), "\""+encodedString +"\"");
        return encodedString;
    }

    public String getParameter(String parameterName) {
        String[] parameterValues = getParameterValues(parameterName);
        if (null == parameterValues) {
            return null ;
        }
        return parameterValues[0] ;
    }

    public Map<String, String[]> getParameterMap() {
        return Collections.unmodifiableMap(parameterMap) ;
    }

    public Enumeration getParameterNames() {
        return new IteratorEnumeration(parameterMap.keySet().iterator());
    }

    public String[] getParameterValues(String parameterName) {
        return parameterMap.get(parameterName) ;
    }

}
