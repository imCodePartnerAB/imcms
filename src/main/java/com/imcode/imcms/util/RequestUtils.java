package com.imcode.imcms.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by Shadowgun on 13.01.2015.
 */
public class RequestUtils {
    public static Map<String, String> parse(String data) throws UnsupportedEncodingException {
        final Map<String, String> query_pairs = new LinkedHashMap<String, String>();
        final String[] pairs = data.split("&");
        for (String pair : pairs) {
            final int idx = pair.indexOf("=");
            final String key = idx > 0 ? URLDecoder.decode(pair.substring(0, idx), "UTF-8") : pair;
            final String value = idx > 0 && pair.length() > idx + 1 ? URLDecoder.decode(pair.substring(idx + 1), "UTF-8") : null;
            query_pairs.put(key, value);
        }
        return query_pairs;
    }

    public static Map<String, String> parse(InputStream data) throws IOException {
        final Map<String, String> query_pairs = new LinkedHashMap<String, String>();
        StringBuilder buffer = new StringBuilder();
        while (true) {
            final char ch = (char) data.read();
            if (ch == '&' || ch == '\uFFFF') {
                final int idx = buffer.indexOf("=");
                final String key = idx > 0 ? URLDecoder.decode(buffer.substring(0, idx), "UTF-8") : buffer.toString();
                final String value = idx > 0 && buffer.length() > idx + 1 ? URLDecoder.decode(buffer.substring(idx + 1), "UTF-8") : null;
                query_pairs.put(key, value);
                buffer = new StringBuilder();
                if (ch == '\uFFFF') break;
            } else
                buffer.append(ch);
        }
        return query_pairs;
    }
}
