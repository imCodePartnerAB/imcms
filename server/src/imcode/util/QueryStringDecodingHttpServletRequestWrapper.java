package imcode.util;

import org.apache.commons.collections.MultiHashMap;
import org.apache.commons.collections.MultiMap;
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
    private String previousForwardQueryString;
    private String previousIncludeQueryString;
    private Map<String, String[]> parameterMap;

    public QueryStringDecodingHttpServletRequestWrapper(HttpServletRequest request) {
        super(request);
        decoder = new FallbackDecoder(Charset.forName(Imcms.DEFAULT_ENCODING), Charset.forName(Imcms.ISO_8859_1_ENCODING));
    }

    public String getParameter(String parameterName) {
        String[] parameterValues = getParameterValues(parameterName);
        if ( null == parameterValues ) {
            return null;
        }
        return parameterValues[0];
    }

    public Enumeration getParameterNames() {
        return new IteratorEnumeration(getParameterMap().keySet().iterator());
    }

    public String[] getParameterValues(String parameterName) {
        return getParameterMap().get(parameterName);
    }

    public Map<String, String[]> getParameterMap() {
        String forwardQueryString = (String) getAttribute("javax.servlet.forward.query_string");
        String includeQueryString = (String) getAttribute("javax.servlet.include.query_string");
        boolean shouldBuildMap = null == parameterMap 
                                 || null == forwardQueryString ^ null == previousForwardQueryString 
                                 || null != forwardQueryString && !forwardQueryString.equals(previousForwardQueryString)
                                 || null == includeQueryString ^ null == previousIncludeQueryString
                                 || null != includeQueryString && !includeQueryString.equals(previousIncludeQueryString);
        if (shouldBuildMap) {
            previousForwardQueryString = forwardQueryString ;
            previousIncludeQueryString = includeQueryString ;
            parameterMap = buildParameterMap();
        }
        return Collections.unmodifiableMap(parameterMap);
    }

    private Map<String, String[]> buildParameterMap() {
        MultiHashMap result = new MultiHashMap();
        addQueryStringParameters(previousForwardQueryString, result);
        addQueryStringParameters(previousIncludeQueryString, result);
        addQueryStringParameters(getQueryString(), result);
        return convertMultiMapToArrayMap(result);
    }

    private void addQueryStringParameters(String queryString, MultiHashMap result) {
        MultiMap queryStringParameterMap = decodeQueryString(queryString);
        for ( Map.Entry<String, Collection<String>> entry : (Set<Map.Entry<String, Collection<String>>>)queryStringParameterMap.entrySet() ) {
            String parameterName = entry.getKey();
            Collection<String> parameterValues = entry.getValue();
            for ( String parameterValue : parameterValues ) {
                result.put(parameterName, parameterValue);
            }
        }
    }

    private MultiMap decodeQueryString(String queryString) {
        if ( null == queryString ) {
            return new MultiHashMap();
        }
        String[] parameterPairs = queryString.split("[&;]");
        MultiHashMap localParameterMap = new MultiHashMap();
        for ( String parameterPair : parameterPairs ) {
            if ( 0 == parameterPair.length() ) {
                continue;
            }
            String[] nameAndValue = parameterPair.split("=", 2);
            String parameterName = decode(nameAndValue[0]);
            String parameterValue = "";
            if (nameAndValue.length > 1) {
                parameterValue = decode(nameAndValue[1]);
            }
            localParameterMap.put(parameterName, parameterValue);
        }
        return localParameterMap ;
    }

    private String decode(String encodedString) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        Matcher matcher = parameterBytePattern.matcher(encodedString);
        while ( matcher.find() ) {
            int c;
            String match = matcher.group();
            if ( 3 == match.length() ) {
                c = Integer.parseInt(match.substring(1), 16);
            } else if ( 1 == match.length() ) {
                c = match.charAt(0);
                if ( c == '+' ) {
                    c = ' ';
                }
            } else {
                throw new RuntimeException("Illegal match length.");
            }
            bytes.write(c);
        }
        encodedString = decoder.decodeBytes(bytes.toByteArray(), "\"" + encodedString + "\"");
        return encodedString;
    }

    private static Map<String,String[]> convertMultiMapToArrayMap(MultiMap multiMap) {
        Map<String, String[]> parameterMap = new HashMap<String, String[]>();
        for ( Map.Entry<String, Collection<String>> entry : (Set<Map.Entry<String, Collection<String>>>) multiMap.entrySet() )
        {
            String parameterName = entry.getKey();
            Collection<String> parameterValues = entry.getValue();
            parameterMap.put(parameterName, parameterValues.toArray(new String[parameterValues.size()]));
        }
        return parameterMap ;        
    }

}
