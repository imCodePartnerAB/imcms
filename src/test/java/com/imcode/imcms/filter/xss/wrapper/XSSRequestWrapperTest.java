package com.imcode.imcms.filter.xss.wrapper;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.imcode.imcms.filters.xss.wrapper.XSSRequestWrapper;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class XSSRequestWrapperTest {

    private final String textWithSpanTag1 = "<span>Text1</span>";
    private final String textWithSpanTag2 = "<span>Text2</span>";
    private final String textWithScriptTag1 = "<script>alert('xss1');</script>";
    private final String textWithScriptTag2 = "<script>alert('xss2');</script>";
    private final String filteredTextWithSpanTag1 = "&lt;span&gt;Text1&lt;/span&gt;";
    private final String filteredTextWithSpanTag2 = "&lt;span&gt;Text2&lt;/span&gt;";
    private final String filteredTextWithScriptTag1 = "&lt;script&gt;alert('xss1');&lt;/script&gt;";
    private final String filteredTextWithScriptTag2 = "&lt;script&gt;alert('xss2');&lt;/script&gt;";
    private final String simpleText1 = "Simple Text1";
    private final String simpleText2 = "Simplte Text2";
    private XSSRequestWrapper wrapper;
    private MockHttpServletRequest request;

    @Test
    public void getParameter_WhenGetMethod_Expect_FilteredParameter() {
        request = new MockHttpServletRequest("GET", "/url");
        request.addParameter("parameter1", textWithSpanTag1);
        request.addParameter("parameter2", textWithScriptTag1);
        request.addParameter("parameter3", simpleText1);
        wrapper = new XSSRequestWrapper(request);

        assertThat(wrapper.getParameter("parameter1"), is(filteredTextWithSpanTag1));
        assertThat(wrapper.getParameter("parameter2"), is(filteredTextWithScriptTag1));
        assertThat(wrapper.getParameter("parameter3"), is(simpleText1));
    }

    @Test
    public void getParameter_WhenPostMethod_Expect_FilteredParameter() {
        request = new MockHttpServletRequest("POST", "/url");
        request.addParameter("parameter1", textWithSpanTag1);
        request.addParameter("parameter2", textWithScriptTag1);
        request.addParameter("parameter3", simpleText1);
        wrapper = new XSSRequestWrapper(request);

        assertThat(wrapper.getParameter("parameter1"), is(filteredTextWithSpanTag1));
        assertThat(wrapper.getParameter("parameter2"), is(filteredTextWithScriptTag1));
        assertThat(wrapper.getParameter("parameter3"), is(simpleText1));
    }

    @Test
    public void getParameterValues_Expect_ArrayWithFilteredParameters() {
        request = new MockHttpServletRequest();
        request.addParameter("parameter1", textWithSpanTag1);
        request.addParameter("parameter1", textWithSpanTag2);
        request.addParameter("parameter2", textWithScriptTag1);
        request.addParameter("parameter2", textWithScriptTag2);
        request.addParameter("parameter2", simpleText1);
        wrapper = new XSSRequestWrapper(request);

        String[] values = wrapper.getParameterValues("parameter1");
        assertThat(values[0], is(filteredTextWithSpanTag1));
        assertThat(values[1], is(filteredTextWithSpanTag2));

        values = wrapper.getParameterValues("parameter2");
        assertThat(values[0], is(filteredTextWithScriptTag1));
        assertThat(values[1], is(filteredTextWithScriptTag2));
        assertThat(values[2], is(simpleText1));
    }

    @Test
    public void getParameterMap_Expect_MapWithFilteredParameters() {
        request = new MockHttpServletRequest();
        request.addParameter("parameter1", textWithSpanTag1);
        request.addParameter("parameter1", textWithSpanTag2);
        request.addParameter("parameter2", textWithScriptTag1);
        request.addParameter("parameter2", textWithScriptTag2);
        request.addParameter("parameter2", simpleText1);
        wrapper = new XSSRequestWrapper(request);

        Map<String, String[]> map = wrapper.getParameterMap();
        String[] values = map.get("parameter1");
        assertThat(values[0], is(filteredTextWithSpanTag1));
        assertThat(values[1], is(filteredTextWithSpanTag2));

        values = map.get("parameter2");
        assertThat(values[0], is(filteredTextWithScriptTag1));
        assertThat(values[1], is(filteredTextWithScriptTag2));
        assertThat(values[2], is(simpleText1));
    }

    @Test
    public void getHeader_Expect_FilteredHeader() {
        request = new MockHttpServletRequest();

        String header1 = "40202042.1627971098.1.1.utmcsr=google|utmccn=(organic)|utmcmd=organic|utmctr=(not%20provided)";
        request.addHeader("header1", header1);
        request.addHeader("header2", textWithScriptTag1);
        wrapper = new XSSRequestWrapper(request);

        assertThat(wrapper.getHeader("header1"), is(header1));
        assertThat(wrapper.getHeader("header2"), is(filteredTextWithScriptTag1));
    }

    @Test
    public void getHeaders_Expect_EnumerationWithFilteredHeaders() {
        request = new MockHttpServletRequest();

        request.addHeader("header1", textWithSpanTag1);
        request.addHeader("header1", textWithSpanTag2);
        request.addHeader("header2", textWithScriptTag1);
        request.addHeader("header2", textWithScriptTag2);
        request.addHeader("header2", simpleText1);
        wrapper = new XSSRequestWrapper(request);

        Enumeration<String> header1 = wrapper.getHeaders("header1");
        assertThat(header1.nextElement(), is(filteredTextWithSpanTag1));
        assertThat(header1.nextElement(), is(filteredTextWithSpanTag2));

        Enumeration<String> header2 = wrapper.getHeaders("header2");
        assertThat(header2.nextElement(), is(filteredTextWithScriptTag1));
        assertThat(header2.nextElement(), is(filteredTextWithScriptTag2));
        assertThat(header2.nextElement(), is(simpleText1));
    }

    @Test
    public void inputStream_When_ContentIsNotJSON_Expect_InputStreamWithNotFilteredContent() throws IOException {
        final String expectedContent = "Content with different symbols <, >";
        request = new MockHttpServletRequest();
        request.setContent(expectedContent.getBytes(StandardCharsets.UTF_8));
        wrapper = new XSSRequestWrapper(request);

        String receivedContent = IOUtils.toString(wrapper.getInputStream(), StandardCharsets.UTF_8.name());
        assertThat(receivedContent, is(expectedContent));
    }

    @Test
    public void inputStream_When_ContentIsJSON_Expect_InputStreamWithFilteredContent() throws IOException {
        Map<String, Object> map = new HashMap();
        map.put("parameter1", textWithSpanTag1);
        map.put("parameter2", textWithScriptTag1);
        map.put("parameter3", new String[]{textWithSpanTag2, simpleText1});
        map.put("parameter4", simpleText2);

        Map<String, Object> expectedMap = new HashMap();
        expectedMap.put("parameter1", filteredTextWithSpanTag1);
        expectedMap.put("parameter2", filteredTextWithScriptTag1);
        expectedMap.put("parameter3", new String[]{filteredTextWithSpanTag2, simpleText1});
        expectedMap.put("parameter4", simpleText2);

        Gson gson = new GsonBuilder().disableHtmlEscaping().create();
        final String json = gson.toJson(map);
        final String expectedJson = gson.toJson(expectedMap);

        request = new MockHttpServletRequest();
        request.setContent(json.getBytes(StandardCharsets.UTF_8));
        wrapper = new XSSRequestWrapper(request);

        final String receivedJson = IOUtils.toString(wrapper.getInputStream(), StandardCharsets.UTF_8.name());
        assertThat(receivedJson, is(expectedJson));
    }
}
