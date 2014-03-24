package com.imcode.imcms.api;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;

import com.imcode.imcms.test.fixtures.LanguageFX;
import com.imcode.imcms.test.fixtures.UserFX;
import imcode.server.ImcmsServices;
import imcode.server.ImcmsConstants;
import imcode.server.user.UserDomainObject;
import org.junit.Before;
import org.junit.Test;


import javax.servlet.http.HttpServletRequest;

public class DocGetterCallbackTest {

    DocumentLanguageSupport dls = LanguageFX.mkI18nSupport();
    ImcmsServices services = mock(ImcmsServices.class);

    @Before
    public void beforeTest() {
        when(services.getDocumentLanguageSupport()).thenReturn(dls);
    }

    @Test
    public void default_user_no_params() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        UserDomainObject user = new UserDomainObject(UserDomainObject.DEFAULT_USER_ID);

        when(request.getParameter(ImcmsConstants.REQUEST_PARAM__DOC_LANGUAGE)).thenReturn(null);
        when(request.getParameter(ImcmsConstants.REQUEST_PARAM__DOC_ID)).thenReturn(null);
        when(request.getParameter(ImcmsConstants.REQUEST_PARAM__DOC_VERSION)).thenReturn(null);
        when(request.getServerName()).thenReturn("127.0.0.1");

        DocGetterCallbacks.updateUserDocGetterCallback(request, services, user);
        assertTrue(user.getDocGetterCallback() instanceof DefaultDocGetterCallback);

        //assertThat(user.getDocGetterCallback(), isA(DefaultDocGetterCallback.class));
    }

    @Test
    public void default_user_change_language() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        UserDomainObject user = new UserDomainObject(UserDomainObject.DEFAULT_USER_ID);

        when(request.getParameter(ImcmsConstants.REQUEST_PARAM__DOC_LANGUAGE)).thenReturn(LanguageFX.mkSwedish().getCode());
        when(request.getParameter(ImcmsConstants.REQUEST_PARAM__DOC_ID)).thenReturn(null);
        when(request.getParameter(ImcmsConstants.REQUEST_PARAM__DOC_VERSION)).thenReturn(null);

        DocGetterCallbacks.updateUserDocGetterCallback(request, services, user);
        assertTrue(user.getDocGetterCallback() instanceof DefaultDocGetterCallback);

        assertEquals(LanguageFX.mkSwedish(), user.getDocGetterCallback().documentLanguages().getPreferred());
    }

    @Test
    public void default_user_change_version_to_working() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        UserDomainObject user = new UserDomainObject(UserDomainObject.DEFAULT_USER_ID);

        when(request.getParameter(ImcmsConstants.REQUEST_PARAM__DOC_LANGUAGE)).thenReturn(null);
        when(request.getParameter(ImcmsConstants.REQUEST_PARAM__DOC_ID)).thenReturn("1001");
        when(request.getParameter(ImcmsConstants.REQUEST_PARAM__DOC_VERSION)).thenReturn("0");
        when(request.getServerName()).thenReturn("127.0.0.1");

        DocGetterCallbacks.updateUserDocGetterCallback(request, services, user);
        assertTrue(user.getDocGetterCallback() instanceof DefaultDocGetterCallback);
    }

    @Test
    public void default_user_change_version_to_custom() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        UserDomainObject user = new UserDomainObject(UserDomainObject.DEFAULT_USER_ID);

        when(request.getParameter(ImcmsConstants.REQUEST_PARAM__DOC_LANGUAGE)).thenReturn(null);
        when(request.getParameter(ImcmsConstants.REQUEST_PARAM__DOC_ID)).thenReturn("1001");
        when(request.getParameter(ImcmsConstants.REQUEST_PARAM__DOC_VERSION)).thenReturn("2");
        when(request.getServerName()).thenReturn("127.0.0.1");

        DocGetterCallbacks.updateUserDocGetterCallback(request, services, user);
        assertTrue(user.getDocGetterCallback() instanceof DefaultDocGetterCallback);
    }

    @Test
    public void power_user_change_version_to_working() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        UserDomainObject user = new UserDomainObject(UserDomainObject.DEFAULT_USER_ID);

        when(request.getParameter(ImcmsConstants.REQUEST_PARAM__DOC_LANGUAGE)).thenReturn(null);
        when(request.getParameter(ImcmsConstants.REQUEST_PARAM__DOC_ID)).thenReturn("1001");
        when(request.getParameter(ImcmsConstants.REQUEST_PARAM__DOC_VERSION)).thenReturn("0");
        when(request.getServerName()).thenReturn("127.0.0.1");

        DocGetterCallbacks.updateUserDocGetterCallback(request, services, user);

        assertTrue(user.getDocGetterCallback() instanceof WorkingDocGetterCallback);
        WorkingDocGetterCallback gdc = (WorkingDocGetterCallback) user.getDocGetterCallback();

        assertEquals(1001, gdc.getSelectedDocId());
    }

    @Test
    public void power_user_change_version_to_custom() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        UserDomainObject user = new UserDomainObject(UserDomainObject.DEFAULT_USER_ID);

        when(request.getParameter(ImcmsConstants.REQUEST_PARAM__DOC_LANGUAGE)).thenReturn(null);
        when(request.getParameter(ImcmsConstants.REQUEST_PARAM__DOC_ID)).thenReturn("1001");
        when(request.getParameter(ImcmsConstants.REQUEST_PARAM__DOC_VERSION)).thenReturn("2");
        when(request.getServerName()).thenReturn("127.0.0.1");

        DocGetterCallbacks.updateUserDocGetterCallback(request, services, user);

        assertTrue(user.getDocGetterCallback() instanceof CustomDocGetterCallback);
        CustomDocGetterCallback gdc = (CustomDocGetterCallback) user.getDocGetterCallback();

        assertEquals(1001, gdc.getSelectedDocId());
        assertEquals(2, gdc.getSelectedDocVersionNo());
    }
}