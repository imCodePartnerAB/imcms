package com.imcode.imcms.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.util.NestedServletException;

import java.io.IOException;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public abstract class AbstractControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    protected abstract String controllerPath();

    protected ResultActions performRequestBuilderExpectedOk(MockHttpServletRequestBuilder builder) throws Exception {
        return mockMvc.perform(builder).andExpect(status().isOk());
    }

    protected ResultActions performRequestBuilderExpectedOkAndContentJsonUtf8(MockHttpServletRequestBuilder builder) throws Exception {
        return performRequestBuilderExpectedOk(builder)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8));
    }

    protected void performRequestBuilderExpectedOkAndJsonContentEquals(MockHttpServletRequestBuilder builder,
                                                                       String expectedJson) throws Exception {
        performRequestBuilderExpectedOkAndContentJsonUtf8(builder).andExpect(content().json(expectedJson));
    }

    protected void getAllExpectedOkAndJsonContentEquals(String expectedJson) throws Exception {
        performRequestBuilderExpectedOkAndJsonContentEquals(get(controllerPath()), expectedJson);
    }

    protected String asJson(Object object) throws JsonProcessingException {
        return objectMapper.writeValueAsString(object);
    }

    protected <T> T fromJson(String json, Class<T> resultClass) throws IOException {
        return objectMapper.readValue(json, resultClass);
    }

    protected <T> void performPostWithContentExpectException(Object contentObject,
                                                             Class<T> expectedExceptionClass) throws Exception {

        final MockHttpServletRequestBuilder requestBuilder = getPostRequestBuilderWithContent(contentObject);
        performRequestBuilderExpectException(expectedExceptionClass, requestBuilder);
    }

    protected <T> void performRequestBuilderExpectException(Class<T> expectedExceptionClass,
                                                            MockHttpServletRequestBuilder requestBuilder) throws Exception {
        try {
            performRequestBuilderExpectedOk(requestBuilder); // here exception should be thrown!!1

        } catch (NestedServletException e) {
            final Class<? extends Throwable> exceptionClass = e.getCause().getClass();
            final String message = "Should be " + expectedExceptionClass.getName() + "!! Received: "
                    + exceptionClass.getName();

            assertTrue(message, exceptionClass.equals(expectedExceptionClass));
            return;
        }

        fail("Expected exception wasn't thrown!");
    }

    protected void performPostWithContentExpectOk(Object content) throws Exception {
        final MockHttpServletRequestBuilder requestBuilder = getPostRequestBuilderWithContent(content);
        performRequestBuilderExpectedOk(requestBuilder);
    }

    private MockHttpServletRequestBuilder getPostRequestBuilderWithContent(Object content) throws Exception {
        return MockMvcRequestBuilders.post(controllerPath())
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(asJson(content));
    }

}
