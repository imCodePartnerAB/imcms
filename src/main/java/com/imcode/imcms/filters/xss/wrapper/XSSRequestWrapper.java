package com.imcode.imcms.filters.xss.wrapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import imcode.util.Utility;
import org.apache.commons.io.IOUtils;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class XSSRequestWrapper extends HttpServletRequestWrapper {

    public XSSRequestWrapper(HttpServletRequest servletRequest) {
        super(servletRequest);
    }

    @Override
    public String getParameter(String parameter) {
        String value = super.getParameter(parameter);
        return stripXSS(value);
    }

    @Override
    public String[] getParameterValues(String parameter) {
        String[] values = super.getParameterValues(parameter);

        if (values == null) return null;

        int count = values.length;
        String[] encodedValues = new String[count];
        for (int i = 0; i < count; i++) {
            encodedValues[i] = stripXSS(values[i]);
        }

        return encodedValues;
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        Map<String, String[]> parameterMap = new LinkedHashMap<>(super.getParameterMap());
        parameterMap.replaceAll((k ,v) -> Arrays.stream(v).map(this::stripXSS).toArray(String[]::new));
        return parameterMap;
    }

    @Override
    public String getHeader(String name) {
        String value = super.getHeader(name);
        return stripXSS(value);
    }

    @Override
    public Enumeration<String> getHeaders(String name) {
        List<String> headersList = new ArrayList<>();

        Enumeration<String> headers = super.getHeaders(name);
        while(headers.hasMoreElements()){
            headersList.add(stripXSS(headers.nextElement()));
        }

        return Collections.enumeration(headersList);
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        byte[] contents = IOUtils.toByteArray(super.getInputStream());

        try{
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> jsonMap = mapper.readValue(contents, Map.class);

            for (String key : jsonMap.keySet()) {
                Object value = jsonMap.get(key);

                if (value instanceof String) {
                    jsonMap.put(key, stripXSS((String) value));
                }else if(value instanceof ArrayList && !((ArrayList)value).isEmpty() && ((ArrayList)value).get(0) instanceof String){
                    ArrayList<String> valueList = (ArrayList<String>) value;
                    for(int i = 0; i<valueList.size(); i++){
                        valueList.set(i, stripXSS(valueList.get(i)));
                    }
                    jsonMap.put(key, valueList);
                }
            }

            contents = mapper.writeValueAsString(jsonMap).getBytes(StandardCharsets.UTF_8);
        } catch (IOException e) {
            //Do not process data if not json
        }

        return new CustomServletInputStream(contents);
    }

    private String stripXSS(String value) {
        if(value == null) return null;

        value = Utility.unescapeValue(value);
        return Utility.escapeValue(value, "'", "\"");
    }


    private class CustomServletInputStream extends ServletInputStream {

        private final ByteArrayInputStream buffer;

        private CustomServletInputStream(byte[] contents) {
            this.buffer = new ByteArrayInputStream(contents);
        }

        @Override
        public boolean isFinished() {
            return buffer.available() == 0;
        }

        @Override
        public boolean isReady() {
            return true;
        }

        @Override
        public void setReadListener(ReadListener readListener) {
            throw new RuntimeException("Not implemented");
        }

        @Override
        public int read() throws IOException {
            return buffer.read();
        }
    }
}