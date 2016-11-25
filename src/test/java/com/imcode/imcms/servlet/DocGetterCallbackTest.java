package com.imcode.imcms.servlet;

import com.imcode.imcms.api.DocumentLanguages;
import com.imcode.imcms.test.fixtures.LanguageFX;
import imcode.server.ImcmsConstants;
import imcode.server.ImcmsServices;
import imcode.server.user.UserDomainObject;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.http.HttpServletRequest;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DocGetterCallbackTest {

    DocumentLanguages dls = LanguageFX.mkI18nSupport();
    ImcmsServices services = mock(ImcmsServices.class);

    @Before
    public void beforeTest() {
        when(services.getDocumentLanguages()).thenReturn(dls);
    }

    @Test
    public void default_user_no_params() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        UserDomainObject user = new UserDomainObject(UserDomainObject.DEFAULT_USER_ID);

        when(request.getParameter(ImcmsConstants.REQUEST_PARAM__DOC_LANGUAGE)).thenReturn(null);
        when(request.getParameter(ImcmsConstants.REQUEST_PARAM__DOC_ID)).thenReturn(null);
        when(request.getParameter(ImcmsConstants.REQUEST_PARAM__DOC_VERSION)).thenReturn(null);
        when(request.getServerName()).thenReturn("127.0.0.1");

        ImcmsSetupFilter.updateUserDocGetterCallback(request, services, user);
    }

    @Test
    public void default_user_change_language() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        UserDomainObject user = new UserDomainObject(UserDomainObject.DEFAULT_USER_ID);

        when(request.getParameter(ImcmsConstants.REQUEST_PARAM__DOC_LANGUAGE)).thenReturn(LanguageFX.mkSwedish().getCode());
        when(request.getParameter(ImcmsConstants.REQUEST_PARAM__DOC_ID)).thenReturn(null);
        when(request.getParameter(ImcmsConstants.REQUEST_PARAM__DOC_VERSION)).thenReturn(null);

        ImcmsSetupFilter.updateUserDocGetterCallback(request, services, user);
    }

    @Test
    public void default_user_change_version_to_working() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        UserDomainObject user = new UserDomainObject(UserDomainObject.DEFAULT_USER_ID);

        when(request.getParameter(ImcmsConstants.REQUEST_PARAM__DOC_LANGUAGE)).thenReturn(null);
        when(request.getParameter(ImcmsConstants.REQUEST_PARAM__DOC_ID)).thenReturn("1001");
        when(request.getParameter(ImcmsConstants.REQUEST_PARAM__DOC_VERSION)).thenReturn("0");
        when(request.getServerName()).thenReturn("127.0.0.1");

        ImcmsSetupFilter.updateUserDocGetterCallback(request, services, user);
    }

    @Test
    public void default_user_change_version_to_custom() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        UserDomainObject user = new UserDomainObject(UserDomainObject.DEFAULT_USER_ID);

        when(request.getParameter(ImcmsConstants.REQUEST_PARAM__DOC_LANGUAGE)).thenReturn(null);
        when(request.getParameter(ImcmsConstants.REQUEST_PARAM__DOC_ID)).thenReturn("1001");
        when(request.getParameter(ImcmsConstants.REQUEST_PARAM__DOC_VERSION)).thenReturn("2");
        when(request.getServerName()).thenReturn("127.0.0.1");

        ImcmsSetupFilter.updateUserDocGetterCallback(request, services, user);
    }

    @Test
    public void power_user_change_version_to_working() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        UserDomainObject user = new UserDomainObject(UserDomainObject.DEFAULT_USER_ID);

        when(request.getParameter(ImcmsConstants.REQUEST_PARAM__DOC_LANGUAGE)).thenReturn(null);
        when(request.getParameter(ImcmsConstants.REQUEST_PARAM__DOC_ID)).thenReturn("1001");
        when(request.getParameter(ImcmsConstants.REQUEST_PARAM__DOC_VERSION)).thenReturn("0");
        when(request.getServerName()).thenReturn("127.0.0.1");

        ImcmsSetupFilter.updateUserDocGetterCallback(request, services, user);
    }

    @Test
    public void power_user_change_version_to_custom() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        UserDomainObject user = new UserDomainObject(UserDomainObject.DEFAULT_USER_ID);

        when(request.getParameter(ImcmsConstants.REQUEST_PARAM__DOC_LANGUAGE)).thenReturn(null);
        when(request.getParameter(ImcmsConstants.REQUEST_PARAM__DOC_ID)).thenReturn("1001");
        when(request.getParameter(ImcmsConstants.REQUEST_PARAM__DOC_VERSION)).thenReturn("2");
        when(request.getServerName()).thenReturn("127.0.0.1");

        ImcmsSetupFilter.updateUserDocGetterCallback(request, services, user);
    }
}