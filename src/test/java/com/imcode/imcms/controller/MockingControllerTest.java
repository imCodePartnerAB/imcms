package com.imcode.imcms.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultHandler;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public abstract class MockingControllerTest {

    private ObjectMapper objectMapper = new ObjectMapper();

    private MockMvc mockMvc;

    protected abstract String controllerPath();

    protected abstract Object controllerToMock();

    @BeforeEach
    public void setUp() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(controllerToMock()).build();
    }

    protected MvcResultActions perform(RequestBuilder requestBuilder) throws Exception {
        return new MvcResultActions(mockMvc.perform(requestBuilder));
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
            return resultActions.andExpect(matcher);
        }

        @Override
        public ResultActions andDo(ResultHandler handler) throws Exception {
            return resultActions.andDo(handler);
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
    }
}
