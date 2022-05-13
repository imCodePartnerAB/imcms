package com.imcode.imcms.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.*;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public abstract class MockingControllerTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    private MockMvc mockMvc;

    protected abstract Object controllerToMock();

    @BeforeEach
    public void setUpMockMvc() {
        mockMvc = MockMvcBuilders.standaloneSetup(controllerToMock()).build();
    }

    @SneakyThrows
    protected MvcResultActions perform(RequestBuilder requestBuilder) {
        return new MvcResultActions(mockMvc.perform(requestBuilder));
    }

    @SneakyThrows
    protected MvcResultActions perform(MockHttpServletRequestBuilder requestBuilder, Object jsonContent) {
        requestBuilder = requestBuilder.contentType(MediaType.APPLICATION_JSON).content(asJson(jsonContent));
        return perform(requestBuilder);
    }

    @SneakyThrows
    protected String asJson(Object object) {
        return objectMapper.writeValueAsString(object);
    }

    @SneakyThrows
    protected <T> T fromJson(String json, TypeReference<T> resultTypeReference) {
        return objectMapper.readValue(json, resultTypeReference);
    }

    @SneakyThrows
    protected <T> T fromJson(String json, Class<T> type) {
        return objectMapper.readValue(json, type);
    }

    protected class MvcResultActions implements ResultActions {

        private final ResultActions resultActions;

        MvcResultActions(ResultActions resultActions) {
            this.resultActions = resultActions;
        }

        @Override
        @SneakyThrows
        public ResultActions andExpect(ResultMatcher matcher) {
            return new MvcResultActions(resultActions.andExpect(matcher));
        }

        @Override
        @SneakyThrows
        public ResultActions andDo(ResultHandler handler) {
            return new MvcResultActions(resultActions.andDo(handler));
        }

        @Override
        public MvcResult andReturn() {
            return resultActions.andReturn();
        }

        @SneakyThrows
        public void andExpectAsJson(Object expected) {
            resultActions.andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(content().json(asJson(expected)));
        }

        @SneakyThrows
        public String getResponse() {
            return resultActions.andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();
        }
    }
}
