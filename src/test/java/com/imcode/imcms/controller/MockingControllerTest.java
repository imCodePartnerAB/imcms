package com.imcode.imcms.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultHandler;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public abstract class MockingControllerTest {

    private ObjectMapper objectMapper = new ObjectMapper();

    private MockMvc mockMvc;

    protected abstract Object controllerToMock();

    @BeforeEach
    public void setUp() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(controllerToMock()).build();
    }

    protected MvcResultActions perform(RequestBuilder requestBuilder) throws Exception {
        return new MvcResultActions(mockMvc.perform(requestBuilder));
    }

    @SneakyThrows
    protected MvcResultActions perform(MockHttpServletRequestBuilder requestBuilder, Object jsonContent) {
        requestBuilder = requestBuilder.contentType(MediaType.APPLICATION_JSON_UTF8).content(asJson(jsonContent));
        return perform(requestBuilder);
    }

    protected String asJson(Object object) throws JsonProcessingException {
        return objectMapper.writeValueAsString(object);
    }

    protected class MvcResultActions implements ResultActions {

        private final ResultActions resultActions;

        MvcResultActions(ResultActions resultActions) {
            this.resultActions = resultActions;
        }

        @Override
        public ResultActions andExpect(ResultMatcher matcher) throws Exception {
            return new MvcResultActions(resultActions.andExpect(matcher));
        }

        @Override
        public ResultActions andDo(ResultHandler handler) throws Exception {
            return new MvcResultActions(resultActions.andDo(handler));
        }

        @Override
        public MvcResult andReturn() {
            return resultActions.andReturn();
        }

        public void andExpectAsJson(Object expected) throws Exception {
            resultActions.andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                    .andExpect(content().json(asJson(expected)));
        }

        public String getResponse() throws Exception {
            return resultActions.andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();
        }
    }
}
