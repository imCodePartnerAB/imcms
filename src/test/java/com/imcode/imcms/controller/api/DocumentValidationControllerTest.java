package com.imcode.imcms.controller.api;

import com.imcode.imcms.domain.service.DocumentValidatingService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(MockitoJUnitRunner.class)
public class DocumentValidationControllerTest {

    private static final String CONTROLLER_PATH = "/documents/validate";

    @Mock
    private DocumentValidatingService validatingService;

    @InjectMocks
    private DocumentValidationController documentValidationController;

    private MockMvc mockMvc;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(documentValidationController).build();
    }

    @Test
    public void isTextDocument_When_ServiceReturnsTrue_Expect_TrueReturned() throws Exception {
        final String testIdentifier = "test_identifier";

        when(validatingService.isTextDocument(testIdentifier)).thenReturn(true);

        mockMvc.perform(get(CONTROLLER_PATH + "/isTextDocument/" + testIdentifier))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(true)));

        verify(validatingService, times(1)).isTextDocument(testIdentifier);
    }

    @Test
    public void isTextDocument_When_ServiceReturnsFalse_Expect_FalseReturned() throws Exception {
        final String testIdentifier = "test_identifier";

        when(validatingService.isTextDocument(testIdentifier)).thenReturn(false);

        mockMvc.perform(get(CONTROLLER_PATH + "/isTextDocument/" + testIdentifier))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(false)));

        verify(validatingService, times(1)).isTextDocument(testIdentifier);
    }

}
