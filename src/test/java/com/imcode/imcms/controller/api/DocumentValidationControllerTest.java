package com.imcode.imcms.controller.api;

import com.imcode.imcms.controller.MockingControllerTest;
import com.imcode.imcms.domain.service.DocumentValidatingService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@ExtendWith(MockitoExtension.class)
class DocumentValidationControllerTest extends MockingControllerTest {

    private static final String CONTROLLER_PATH = "/documents/validate";

    @Mock
    private DocumentValidatingService validatingService;

    @InjectMocks
    private DocumentValidationController documentValidationController;

    @Override
    protected Object controllerToMock() {
        return documentValidationController;
    }

    @Test
    void isTextDocument_When_ServiceReturnsTrue_Expect_TrueReturned() throws Exception {
        final String testIdentifier = "test_identifier";

        given(validatingService.isTextDocument(testIdentifier)).willReturn(true);

        final String response = perform(get(CONTROLLER_PATH + "/isTextDocument/" + testIdentifier))
                .getResponse();

        assertEquals(response, "true");

        then(validatingService).should().isTextDocument(testIdentifier);
    }

    @Test
    void isTextDocument_When_ServiceReturnsFalse_Expect_FalseReturned() throws Exception {
        final String testIdentifier = "test_identifier";

        when(validatingService.isTextDocument(testIdentifier)).thenReturn(false);

        final String response = perform(get(CONTROLLER_PATH + "/isTextDocument/" + testIdentifier))
                .getResponse();

        assertEquals(response, "false");

        verify(validatingService, times(1)).isTextDocument(testIdentifier);
    }

}
