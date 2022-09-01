package com.imcode.imcms.filter.xss;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.imcode.imcms.filters.xss.XSSProtectionFilter;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class XSSProtectionFilterTest {

    private final XSSProtectionFilter filter = new XSSProtectionFilter();
    private final String textWithSpanTag1 = "<span>Text1</span>";
    private final String textWithSpanTag2 = "<span>Text2</span>";
    private final String textWithScriptTag1 = "<script>alert('xss1');</script>";
    private final String textWithScriptTag2 = "<script>alert('xss2');</script>";
    private final String filteredTextWithSpanTag1 = "&lt;span&gt;Text1&lt;/span&gt;";
    private final String filteredTextWithSpanTag2 = "&lt;span&gt;Text2&lt;/span&gt;";
    private final String filteredTextWithScriptTag1 = "&lt;script&gt;alert('xss1');&lt;/script&gt;";
    private final String filteredTextWithScriptTag2 = "&lt;script&gt;alert('xss2');&lt;/script&gt;";
    private final String simpleText1 = "Simple Text1";
    private final String simpleText2 = "Simple Text2";
    private MockHttpServletRequest request;
    private MockHttpServletResponse response = new MockHttpServletResponse();
    private MockFilterChain chain = new MockFilterChain();

    public XSSProtectionFilterTest(){
        try {
            FieldUtils.writeField(filter, "include", true, true);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void doFilter_And_getParameter_WhenGetMethod_Expect_FilteredParameter() throws IOException, ServletException {
        request = new MockHttpServletRequest("GET", "/notExistUrl");
        request.addParameter("parameter1", textWithSpanTag1);
        request.addParameter("parameter2", textWithScriptTag1);
        request.addParameter("parameter3", simpleText1);

        filter.doFilter(request, response, chain);
        ServletRequest filteredRequest = chain.getRequest();

        assertEquals(filteredRequest.getParameter("parameter1"), filteredTextWithSpanTag1);
        assertEquals(filteredRequest.getParameter("parameter2"), filteredTextWithScriptTag1);
        assertEquals(filteredRequest.getParameter("parameter3"), simpleText1);
    }

    @Test
    public void doFilter_And_getParameter_WhenPostMethod_Expect_FilteredParameter() throws IOException, ServletException {
        request = new MockHttpServletRequest("POST", "/notExistUrl");
        request.addParameter("parameter1", textWithSpanTag1);
        request.addParameter("parameter2", textWithScriptTag1);
        request.addParameter("parameter3", simpleText1);

        filter.doFilter(request, response, chain);
        ServletRequest filteredRequest = chain.getRequest();

        assertEquals(filteredRequest.getParameter("parameter1"), filteredTextWithSpanTag1);
        assertEquals(filteredRequest.getParameter("parameter2"), filteredTextWithScriptTag1);
        assertEquals(filteredRequest.getParameter("parameter3"), simpleText1);
    }

    @Test
    public void doFilter_And_getParameterValues_Expect_ArrayWithFilteredParameters() throws ServletException, IOException {
        request = new MockHttpServletRequest();
        request.addParameter("parameter1", textWithSpanTag1);
        request.addParameter("parameter1", textWithSpanTag2);
        request.addParameter("parameter2", textWithScriptTag1);
        request.addParameter("parameter2", textWithScriptTag2);
        request.addParameter("parameter2", simpleText1);

        filter.doFilter(request, response, chain);
        ServletRequest filteredRequest = chain.getRequest();

        String[] values = filteredRequest.getParameterValues("parameter1");
        assertEquals(values[0], filteredTextWithSpanTag1);
        assertEquals(values[1], filteredTextWithSpanTag2);

        values = filteredRequest.getParameterValues("parameter2");
        assertEquals(values[0], filteredTextWithScriptTag1);
        assertEquals(values[1], filteredTextWithScriptTag2);
        assertEquals(values[2], simpleText1);
    }

    @Test
    public void doFilter_And_getParameterMap_Expect_MapWithFilteredParameters() throws ServletException, IOException {
        request = new MockHttpServletRequest();
        request.addParameter("parameter1", textWithSpanTag1);
        request.addParameter("parameter1", textWithSpanTag2);
        request.addParameter("parameter2", textWithScriptTag1);
        request.addParameter("parameter2", textWithScriptTag2);
        request.addParameter("parameter2", simpleText1);

        filter.doFilter(request, response, chain);
        ServletRequest filteredRequest = chain.getRequest();

        Map<String, String[]> map = filteredRequest.getParameterMap();
        String[] values = map.get("parameter1");
        assertEquals(values[0], filteredTextWithSpanTag1);
        assertEquals(values[1], filteredTextWithSpanTag2);

        values = map.get("parameter2");
        assertEquals(values[0], filteredTextWithScriptTag1);
        assertEquals(values[1], filteredTextWithScriptTag2);
        assertEquals(values[2], simpleText1);
    }

    @Test
    public void doFilter_And_getHeader_Expect_FilteredHeader() throws ServletException, IOException {
        request = new MockHttpServletRequest();

        String header1 = "40202042.1627971098.1.1.utmcsr=google|utmccn=(organic)|utmcmd=organic|utmctr=(not%20provided)";
        request.addHeader("header1", header1);
        request.addHeader("header2", textWithScriptTag1);

        filter.doFilter(request, response, chain);
        HttpServletRequest filteredRequest = (HttpServletRequest) chain.getRequest();

        assertEquals(filteredRequest.getHeader("header1"), header1);
        assertEquals(filteredRequest.getHeader("header2"), filteredTextWithScriptTag1);
    }

    @Test
    public void doFilter_And_getHeaders_Expect_EnumerationWithFilteredHeaders() throws ServletException, IOException {
        request = new MockHttpServletRequest();

        request.addHeader("header1", textWithSpanTag1);
        request.addHeader("header1", textWithSpanTag2);
        request.addHeader("header2", textWithScriptTag1);
        request.addHeader("header2", textWithScriptTag2);
        request.addHeader("header2", simpleText1);

        filter.doFilter(request, response, chain);
        HttpServletRequest filteredRequest = (HttpServletRequest) chain.getRequest();

        Enumeration<String> header1 = filteredRequest.getHeaders("header1");
        assertEquals(header1.nextElement(), filteredTextWithSpanTag1);
        assertEquals(header1.nextElement(), filteredTextWithSpanTag2);

        Enumeration<String> header2 = filteredRequest.getHeaders("header2");
        assertEquals(header2.nextElement(), filteredTextWithScriptTag1);
        assertEquals(header2.nextElement(), filteredTextWithScriptTag2);
        assertEquals(header2.nextElement(), simpleText1);
    }

    @Test
    public void doFilter_And_getInputStream_When_ContentIsNotJson_Expect_InputStreamWithNotFilteredContent() throws ServletException, IOException {
        final String expectedContent = "Content with different symbols <, >";

        request = new MockHttpServletRequest();
        request.setContent(expectedContent.getBytes(StandardCharsets.UTF_8));

        filter.doFilter(request, response, chain);
        ServletRequest filteredRequest = chain.getRequest();

        String receivedContent = IOUtils.toString(filteredRequest.getInputStream(), StandardCharsets.UTF_8.name());
        assertEquals(receivedContent, expectedContent);
    }

    @Test
    public void doFilter_And_getInputStream_When_ContentIsJson_Expect_InputStreamWithFilteredContent() throws ServletException, IOException {
        Map<String, Object> map = new HashMap<>();
        map.put("parameter1", textWithSpanTag1);
        map.put("parameter2", textWithScriptTag1);
        map.put("parameter3", new String[]{textWithSpanTag2, simpleText1});
        map.put("parameter4", simpleText2);

        Map<String, Object> expectedMap = new HashMap<>();
        expectedMap.put("parameter1", filteredTextWithSpanTag1);
        expectedMap.put("parameter2", filteredTextWithScriptTag1);
        expectedMap.put("parameter3", new String[]{filteredTextWithSpanTag2, simpleText1});
        expectedMap.put("parameter4", simpleText2);

        Gson gson = new GsonBuilder().disableHtmlEscaping().create();
        final String json = gson.toJson(map);
        final String expectedJson = gson.toJson(expectedMap);

        request = new MockHttpServletRequest();
        request.setContent(json.getBytes(StandardCharsets.UTF_8));

        filter.doFilter(request, response, chain);
        ServletRequest filteredRequest = chain.getRequest();

        final String receivedJson = IOUtils.toString(filteredRequest.getInputStream(), StandardCharsets.UTF_8.name());
        assertEquals(receivedJson, expectedJson);
    }

    @Test
    public void doFilter_When_RequestMethodIsNotGet_And_LinkIsInWhiteList_Expect_NotFilteredContent() throws Exception {
        Map<String, Object> map = new HashMap<>();
        map.put("parameter1", textWithSpanTag1);
        map.put("parameter2", textWithScriptTag1);
        map.put("parameter3", new String[]{textWithSpanTag2, simpleText1});
        map.put("parameter4", simpleText2);
        final String json = new GsonBuilder().disableHtmlEscaping().create().toJson(map);

        String whiteUrl = "whiteUrl";

        try {
            HashSet<String> whiteList = new HashSet<>();
            whiteList.add(whiteUrl);
            FieldUtils.writeField(filter, "whiteList", whiteList, true);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        request = new MockHttpServletRequest("POST", whiteUrl);
        request.addParameter("parameter1", textWithSpanTag1);
        request.addHeader("header1", textWithSpanTag1);
        request.setContent(json.getBytes(StandardCharsets.UTF_8));

        filter.doFilter(request, response, chain);
        HttpServletRequest filteredRequest = (HttpServletRequest) chain.getRequest();

        assertEquals(filteredRequest.getParameter("parameter1"), textWithSpanTag1);
        assertEquals(filteredRequest.getHeader("header1"), textWithSpanTag1);

        final String receivedJson = IOUtils.toString(filteredRequest.getInputStream(), StandardCharsets.UTF_8.name());
        assertEquals(receivedJson, json);
    }
}
