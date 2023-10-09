package com.imcode.imcms.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.imcode.imcms.config.TestConfig;
import lombok.SneakyThrows;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.util.NestedServletException;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebAppConfiguration
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TestConfig.class})
public abstract class AbstractControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    protected abstract String controllerPath();

    protected ResultActions performRequestBuilderExpectedOk(MockHttpServletRequestBuilder builder) throws Exception {
        return mockMvc.perform(builder).andExpect(status().isOk());
    }

    protected ResultActions performRequestBuilderExpectedStatus(MockHttpServletRequestBuilder builder, int statusCode) throws Exception {
        return mockMvc.perform(builder).andExpect(status().is(statusCode));
    }

    protected String getJsonResponse(MockHttpServletRequestBuilder builder) throws Exception {
        return getJsonResponseWithExpectedStatus(builder, HttpStatus.OK.value());
    }

    protected String getJsonResponseWithExpectedStatus(MockHttpServletRequestBuilder builder, int statusCode) throws Exception {
        return performRequestBuilderExpectedStatusAndContentJsonUtf8(builder, statusCode)
                .andReturn()
                .getResponse()
                .getContentAsString();
    }

    protected void performRequestBuilderExpectedOkAndJsonContentEquals(MockHttpServletRequestBuilder builder,
                                                                       String expectedJson) throws Exception {
        performRequestBuilderExpectedStatusAndContentJsonUtf8(builder, HttpStatus.OK.value()).andExpect(content().json(expectedJson));
    }

    protected void performRequestBuilderExpectedOkAndContentByteEquals(MockHttpServletRequestBuilder builder,
                                                                       byte[] expectedByte) throws Exception {
        performRequestBuilderExpectedStatusAndContentTextPlainValue(builder, HttpStatus.OK.value()).andExpect(content().bytes(expectedByte));
    }

    protected void getAllExpectedOkAndJsonContentEquals(String expectedJson) throws Exception {
        performRequestBuilderExpectedOkAndJsonContentEquals(get(controllerPath()), expectedJson);
    }

    @SneakyThrows
    protected String asJson(Object object) {
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

            assertEquals(expectedExceptionClass, exceptionClass, message);
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

    protected MockHttpServletRequestBuilder getPostRequestBuilderWithContent(Object content) {
        return MockMvcRequestBuilders.post(controllerPath())
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJson(content));
    }

    protected MockHttpServletRequestBuilder getPostRequestBuilderWithoutContent(String path) {
        return MockMvcRequestBuilders.post(controllerPath() + path)
                .contentType(MediaType.APPLICATION_JSON);
    }

    protected MockHttpServletRequestBuilder getDeleteRequestBuilderWithContent(Object content) {
        return MockMvcRequestBuilders.delete(controllerPath())
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJson(content));
    }

    protected MockHttpServletRequestBuilder getPutRequestBuilderWithContent(Object content) {
        return MockMvcRequestBuilders.put(controllerPath())
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJson(content));
    }

    protected MockHttpServletRequestBuilder getPutRequestBuilderWithoutContent(){
        return MockMvcRequestBuilders.put(controllerPath())
                .contentType(MediaType.APPLICATION_JSON);
    }

    protected MockHttpServletRequestBuilder getPutRequestBuilderWithoutContent(String path){
        return MockMvcRequestBuilders.put(controllerPath() + path)
                .contentType(MediaType.APPLICATION_JSON);
    }

    protected MockHttpServletRequestBuilder getPutRequestBuilderWithContent(Object content, String path) {
        return MockMvcRequestBuilders.put(controllerPath() + path)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJson(content));
    }

    protected MockHttpServletRequestBuilder getPostRequestBuilderWithContent(Object content, String path) {
        return MockMvcRequestBuilders.post(controllerPath() + path)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJson(content));
    }

    private ResultActions performRequestBuilderExpectedStatusAndContentJsonUtf8(MockHttpServletRequestBuilder builder, int statusCode) throws Exception {
        return performRequestBuilderExpectedStatus(builder, statusCode)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    private ResultActions performRequestBuilderExpectedStatusAndContentTextPlainValue(MockHttpServletRequestBuilder builder, int statusCode) throws Exception {
        return performRequestBuilderExpectedStatus(builder, statusCode)
                .andExpect(content().contentType(MediaType.TEXT_PLAIN_VALUE));
    }
}
