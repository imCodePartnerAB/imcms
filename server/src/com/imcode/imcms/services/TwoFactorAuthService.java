package com.imcode.imcms.services;

import com.imcode.imcms.api.ContentManagementSystem;
import com.imcode.imcms.util.l10n.LocalizedMessage;
import imcode.server.Imcms;
import imcode.server.ImcmsServices;
import imcode.server.user.PhoneNumber;
import imcode.server.user.PhoneNumberType;
import imcode.server.user.UserDomainObject;
import org.apache.commons.lang3.RandomStringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Arrays;
import java.util.Map;
import java.util.Properties;

import static com.imcode.imcms.servlet.VerifyUser.*;

public class TwoFactorAuthService {
    public static final String PROPERTY_NAME_2FA = "2FA";
    public static final String REQUEST_PARAMETER_2FA = "2fa";
    private static final String COOKIE_NAME_2FA = REQUEST_PARAMETER_2FA;
    private static final String USER_2FA_CODE_PROPERTY = "2faCookieCode";
    private static TwoFactorAuthService instance = null;
    private final int cookieMaxAge;
    private final ImcmsServices imcmsServices;
    private final SmsService smsService;

    private final LocalizedMessage ERROR_NO_PHONENUMBER_FOUND = new LocalizedMessage("templates/login/access_denied.html/5");
    private final LocalizedMessage SMS_AUTHORIZE_CODE_MESSAGE = new LocalizedMessage("sms/authorize_code/message");

    private TwoFactorAuthService() {
        Properties systemProperties = Imcms.getServerProperties();
        imcmsServices = Imcms.getServices();
        smsService = SmsService.getInstance();

        //Default is 10 days
        cookieMaxAge = Integer.parseInt(systemProperties.getProperty(COOKIE_NAME_2FA, "864000"));
    }


    public static TwoFactorAuthService getInstance() {
        if (null == instance) {
            instance = new TwoFactorAuthService();
        }
        return instance;
    }

    private boolean checkCode(HttpServletRequest request, HttpServletResponse response, UserDomainObject user) {
        boolean checkResult = false;
        final String twoFactorCode = request.getParameter(REQUEST_PARAMETER_2FA);
        final String login = (String) request.getSession().getAttribute(REQUEST_PARAMETER__USERNAME);

        if (null != twoFactorCode) {
            if (twoFactorCode.equals(request.getSession().getAttribute(REQUEST_PARAMETER_2FA))) {
                final Map<String, String> userProperties = user.getProperties();
                String code = userProperties.get(USER_2FA_CODE_PROPERTY);
                if (null == code) {
                    code = RandomStringUtils.randomAlphanumeric(6);
                    userProperties.put(USER_2FA_CODE_PROPERTY, code);
                    Imcms.getServices().getImcmsAuthenticatorAndUserAndRoleMapper().saveUser(user);
                }

                final String cookieName = COOKIE_NAME_2FA + login + request.getRemoteHost().replaceAll("\\D", "");
                Cookie cookie2FA = new Cookie(cookieName, code);
                cookie2FA.setMaxAge(cookieMaxAge);

                response.addCookie(cookie2FA);
                checkResult = true;
            }
        }
        return checkResult;
    }

    private void initCode(UserDomainObject user, HttpServletRequest request, String login, String password) {
        final HttpSession session = request.getSession();

        final String generatedCode = RandomStringUtils.random(6, false, true);
        final PhoneNumber foundNumber = (PhoneNumber) user.getPhoneNumbersOfType(PhoneNumberType.MOBILE).stream()
                .filter(number -> null != ((PhoneNumber) number).getNumber())
                .findFirst()
                .orElse(null);
        if (null != foundNumber) {
            boolean isSmsSend = smsService.sendSms(SMS_AUTHORIZE_CODE_MESSAGE.toLocalizedString(request) + generatedCode, foundNumber.getNumber());

            if (isSmsSend) {
                session.setAttribute(REQUEST_PARAMETER__USERNAME, login);
                session.setAttribute(REQUEST_PARAMETER__PASSWORD, password);
                session.setAttribute(REQUEST_PARAMETER_2FA, generatedCode);
            }
        } else {
            request.setAttribute(REQUEST_ATTRIBUTE__ERROR, ERROR_NO_PHONENUMBER_FOUND);
        }
    }

    public ContentManagementSystem initOrCheck(HttpServletRequest request, HttpServletResponse response, String login, String password) {
        final HttpSession session = request.getSession();
        if (null == login && null == password) {
            login = (String) session.getAttribute(REQUEST_PARAMETER__USERNAME);
            password = (String) session.getAttribute(REQUEST_PARAMETER__PASSWORD);
        }
        UserDomainObject user = imcmsServices.verifyUser(login, password);
        if (null != user && !user.isDefaultUser()) {
            boolean isDisabled = Boolean.parseBoolean(user.getProperties().getOrDefault(COOKIE_NAME_2FA, "false"));
            String finalLogin = login;
            boolean isDisabledByCookie = isDisabled || Arrays.stream(request.getCookies())
                    .filter(cookie -> cookie.getName().equals(COOKIE_NAME_2FA + finalLogin + request.getRemoteHost().replaceAll("\\D", "")))
                    .findFirst()
                    .map(Cookie::getValue)
                    .filter(code -> code.equals(user.getProperties().get(USER_2FA_CODE_PROPERTY)))
                    .isPresent();

            if (isDisabled || isDisabledByCookie || checkCode(request, response, user)) {
                return ContentManagementSystem.login(request, response, login, password);
            } else {
                initCode(user, request, login, password);
            }
        }
        return null;
    }
}
