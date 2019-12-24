package com.imcode.imcms.controller.api;

import com.imcode.imcms.controller.AbstractControllerTest;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;


public class DefaultTemporalDataControllerTest extends AbstractControllerTest {


    @Override
    protected String controllerPath() {
        return "/temporal-data";
    }


    @Test
    public void rebuildDocumentIndex_Expected_OkResult() throws Exception {
        final String linkData = controllerPath() + "/document-index";
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.delete(linkData);
        performRequestBuilderExpectedOk(requestBuilder);
    }

    @Test
    public void addDocumentsInCache_Expected_OkResult() throws Exception {
        final String linkData = controllerPath() + "/document-recache";
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post(linkData);
        performRequestBuilderExpectedOk(requestBuilder);
    }

    @Test
    public void getAmountOfCachedDocuments_Expected_OkResult() throws Exception {
        final String linkData = controllerPath() + "/count-cached";
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(linkData);
        performRequestBuilderExpectedOk(requestBuilder);
    }

    @Test
    public void getAmountOfIndexedDocuments_Expected_OkResult() throws Exception {
        final String linkData = controllerPath() + "/indexed-documents-amount";
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(linkData);
        performRequestBuilderExpectedOk(requestBuilder);
    }

    @Test
    public void removeStaticContentCache_Expected_OkResult() throws Exception {
        final String linkData = controllerPath() + "/static-content";
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.delete(linkData);
        performRequestBuilderExpectedOk(requestBuilder);
    }

    @Test
    public void removeOtherContentCache_Expected_OkResult() throws Exception {
        final String linkData = controllerPath() + "/other-content";
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.delete(linkData);
        performRequestBuilderExpectedOk(requestBuilder);
    }

    @Test
    public void getDateDocumentReindex_Expected_OkResult() throws Exception {
        final String linkData = controllerPath() + "/date-reindex";
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(linkData);
        performRequestBuilderExpectedOk(requestBuilder);
    }
}
