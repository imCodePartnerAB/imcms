package com.imcode.imcms.controller.api;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.servlet.http.HttpServletRequest;
import java.net.MalformedURLException;

import static com.imcode.imcms.controller.api.RequestExternalIdentifierController.*;
import static imcode.server.ImcmsConstants.API_PREFIX;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class RequestExternalIdentifierControllerTest {

    @InjectMocks
    private RequestExternalIdentifierController controller;

    @Test
    void getRedirectURL_When_HttpLocalHostWithPortAndContextPath_Expect_CorrectUrlBuilt() {
        final String id = "test-id";
        final String protocol = "http";
        final String host = "localhost";
        final String port = "8080";
        final String contextPath = "/cp";
        final HttpServletRequest request = mock(HttpServletRequest.class);

        given(request.getRequestURL()).willReturn(new StringBuffer(
                protocol + "://" + host + ":" + port + contextPath + "/test-stuff?test=true&a=2")
        );
        given(request.getContextPath()).willReturn("/cp");

        final String redirectURL = controller.getRedirectURL(id, request);

        final String expected = protocol + "://" + host + ":" + port + contextPath + API_PREFIX
                + EXTERNAL_IDENTIFIERS_PATH + EXTERNAL_IDENTIFIER_REDIRECT_URI + "/" + id;

        assertEquals(expected, redirectURL);
    }

    @Test
    void getRedirectURL_When_HttpsNotLocalHost_Expect_CorrectUrlBuilt() {
        final String id = "test-id";
        final String protocol = "https";
        final String host = "imcode.com";
        final HttpServletRequest request = mock(HttpServletRequest.class);

        given(request.getRequestURL()).willReturn(new StringBuffer(
                protocol + "://" + host + "/test-stuff?test=true&a=2")
        );
        given(request.getContextPath()).willReturn("");

        final String redirectURL = controller.getRedirectURL(id, request);

        final String expected = protocol + "://" + host + API_PREFIX + EXTERNAL_IDENTIFIERS_PATH
                + EXTERNAL_IDENTIFIER_REDIRECT_URI + "/" + id;

        assertEquals(expected, redirectURL);
    }
}
