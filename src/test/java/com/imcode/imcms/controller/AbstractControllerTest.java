package com.imcode.imcms.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.imcode.imcms.config.TestConfig;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.util.NestedServletException;

import javax.transaction.Transactional;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfig.class})
@Transactional
public abstract class AbstractControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    protected abstract String controllerPath();

    protected ResultActions performRequestBuilderExpectedOk(MockHttpServletRequestBuilder builder) throws Exception {
        return mockMvc.perform(builder).andExpect(status().isOk());
    }

    protected String getJsonResponse(MockHttpServletRequestBuilder builder) throws Exception {
        return performRequestBuilderExpectedOkAndContentJsonUtf8(builder)
                .andReturn()
                .getResponse()
                .getContentAsString();
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

    protected <T> T fromJson(String json, TypeReference<T> resultTypeReference) throws IOException {
        return objectMapper.readValue(json, resultTypeReference);
    }

    protected <T> void performPostWithContentExpectException(Object contentObject,
                                                             Class<T> expectedExceptionClass) throws Exception {

        final MockHttpServletRequestBuilder requestBuilder = getPostRequestBuilderWithContent(contentObject);
        performRequestBuilderExpectException(expectedExceptionClass, requestBuilder);
    }

    protected <T> void performDeleteWithContentExpectException(Object contentObject,
                                                               Class<T> expectedExceptionClass) throws Exception {

        final MockHttpServletRequestBuilder requestBuilder = getDeleteRequestBuilderWithContent(contentObject);
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

            assertEquals(message, exceptionClass, expectedExceptionClass);
            return;
        }

        fail("Expected exception wasn't thrown!");
    }

    protected void performPostWithContentExpectOk(Object content) throws Exception {
        final MockHttpServletRequestBuilder requestBuilder = getPostRequestBuilderWithContent(content);
        performRequestBuilderExpectedOk(requestBuilder);
    }

    protected void performPostWithContentExpectOkAndJsonContentEquals(Object content, Object response) throws Exception {
        final MockHttpServletRequestBuilder requestBuilder = getPostRequestBuilderWithContent(content);
        performRequestBuilderExpectedOkAndJsonContentEquals(requestBuilder, asJson(response));
    }

    protected MockHttpServletRequestBuilder getPostRequestBuilderWithContent(Object content) throws Exception {
        return MockMvcRequestBuilders.post(controllerPath())
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(asJson(content));
    }

    protected MockHttpServletRequestBuilder getDeleteRequestBuilderWithContent(Object content) throws Exception {
        return MockMvcRequestBuilders.delete(controllerPath())
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(asJson(content));
    }

    protected MockHttpServletRequestBuilder getPutRequestBuilderWithContent(Object content) throws Exception {
        return MockMvcRequestBuilders.put(controllerPath())
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(asJson(content));
    }

    private ResultActions performRequestBuilderExpectedOkAndContentJsonUtf8(MockHttpServletRequestBuilder builder) throws Exception {
        return performRequestBuilderExpectedOk(builder)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8));
    }

}
