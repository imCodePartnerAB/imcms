package com.imcode.imcms.controller.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.imcode.imcms.controller.AbstractControllerTest;
import com.imcode.imcms.domain.dto.SessionInfoDTO;
import imcode.server.Imcms;
import imcode.server.ImcmsServices;
import imcode.server.user.UserDomainObject;
import imcode.util.Utility;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import javax.servlet.http.HttpSession;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

class SessionControllerTest extends AbstractControllerTest {

    @Override
    protected String controllerPath() {
        return "/sessions";
    }

    @Test
    void getActiveSessions_WhenNoOneLoggedIn_ExpectCorrectOkAndEmptyList() throws Exception {

        final MockHttpServletRequestBuilder requestBuilderGet = get(controllerPath());
        final String jsonResponse = getJsonResponse(requestBuilderGet);
        final List<SessionInfoDTO> sessions = fromJson(jsonResponse, new TypeReference<List<SessionInfoDTO>>() {
        });

        assertNotNull(sessions);
        assertTrue(sessions.isEmpty());
    }

    @Test
    void getActiveSessions_WhenTwoUsersLoggedIn_ExpectCorrectOkAndEntitiesList() throws Exception {
        final UserDomainObject user = new UserDomainObject();
        user.setId(1);
        user.setLogin("login1");
        user.setPassword("pass1");

        HttpSession mockHttpSession1 = new MockHttpSession(null, "test-mockHttpSession1-id");
        mockHttpSession1.setMaxInactiveInterval(1000);
        MockHttpServletRequest mockRequest1 = new MockHttpServletRequest();
        mockRequest1.setSession(mockHttpSession1);
        Utility.makeUserLoggedIn(mockRequest1, user);

        final UserDomainObject user2 = new UserDomainObject();
        user2.setId(2);
        user2.setLogin("login2");
        user2.setPassword("pass2");

        HttpSession mockHttpSession2 = new MockHttpSession(null, "test-mockHttpSession2-id");
        mockHttpSession2.setMaxInactiveInterval(1000);
        MockHttpServletRequest mockRequest2 = new MockHttpServletRequest();
        mockRequest2.setSession(mockHttpSession2);
        Utility.makeUserLoggedIn(mockRequest2, user2);

        final MockHttpServletRequestBuilder requestBuilderGet = get(controllerPath());

        final String jsonResponse = getJsonResponse(requestBuilderGet);
        final List<SessionInfoDTO> sessions = fromJson(jsonResponse, new TypeReference<List<SessionInfoDTO>>() {
        });

        assertNotNull(sessions);
        assertEquals(2, sessions.size());
        assertEquals("test-mockHttpSession2-id", sessions.get(0).getSessionId());
        assertEquals("test-mockHttpSession1-id", sessions.get(1).getSessionId());
    }

    @Test
    void getActiveSessions_WhenSuperAdminLoggedIn_ExpectCorrectOkAndCorrectEntity() throws Exception {
        ImcmsServices services = Imcms.getServices();
        final UserDomainObject user = services.verifyUser("admin", "admin");

        HttpSession mockHttpSession1 = new MockHttpSession(null, "test-mockHttpSession1-id");
        mockHttpSession1.setMaxInactiveInterval(1000);
        MockHttpServletRequest mockRequest1 = new MockHttpServletRequest();
        mockRequest1.setSession(mockHttpSession1);
        Utility.makeUserLoggedIn(mockRequest1, user);

        final MockHttpServletRequestBuilder requestBuilderGet = get(controllerPath());
        final String jsonResponse = getJsonResponse(requestBuilderGet);
        final List<SessionInfoDTO> sessions = fromJson(jsonResponse, new TypeReference<List<SessionInfoDTO>>() {
        });

        assertNotNull(sessions);
        assertEquals(1, sessions.size());
        assertEquals("test-mockHttpSession1-id", sessions.get(0).getSessionId());
    }
}
