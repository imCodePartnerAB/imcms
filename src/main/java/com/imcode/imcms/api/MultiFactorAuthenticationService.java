package com.imcode.imcms.api;

import com.imcode.imcms.domain.component.UserLockValidator;
import com.imcode.imcms.util.l10n.LocalizedMessage;
import imcode.server.Imcms;
import imcode.server.ImcmsConstants;
import imcode.server.user.PhoneNumber;
import imcode.server.user.PhoneNumberType;
import imcode.server.user.UserDomainObject;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Arrays;
import java.util.Optional;

import static com.imcode.imcms.servlet.VerifyUser.*;

@Service
public class MultiFactorAuthenticationService {
	private final static Logger mainLog = LogManager.getLogger(ImcmsConstants.MAIN_LOG);
	private final Logger logger = LogManager.getLogger(MultiFactorAuthenticationService.class);
	@Value("${2fa.enabled}")
	private boolean twoFactoryAuthEnabled;
	@Value("${2fa.cookie.lifetime}")
	private int cookieLifetime;
	@Value("${2fa.password-length}")
	private int oneTimePasswordLength;
	@Value("${2fa.password-letters}")
	private boolean lettersEnabled;
	@Value("${2fa.password-numbers}")
	private boolean numbersEnabled;
	private final SmsService smsService;
	private final UserLockValidator lockValidator;
	private static final String COOKIE_NAME_2FA = "2fa";
	private final LocalizedMessage ERROR_NO_PHONE_NUMBER_FOUND = new LocalizedMessage("templates/login/2fa/phone-not-found");
	private final LocalizedMessage ERROR_WRONG_CODE = new LocalizedMessage("templates/login/2fa/incorrect-code");
	private final LocalizedMessage ERROR_CODE_NOT_SENT = new LocalizedMessage("templates/login/2fa/code-not-sent");
	private final LocalizedMessage SMS_AUTHORIZATION_CODE_MESSAGE = new LocalizedMessage("templates/login/2fa/authorization_code/message");
	private final String SECOND_FACTOR_IN_PROGRESS = "second_factor_in_progress";

	public MultiFactorAuthenticationService(SmsService smsService, UserLockValidator lockValidator) {
		this.smsService = smsService;
		this.lockValidator = lockValidator;
	}

	public boolean isRequired(UserDomainObject user) {
		return twoFactoryAuthEnabled && user.isTwoFactoryAuthenticationEnabled() && !isDeactivatedByCookie(user);
	}

	public void initSecondFactor(UserDomainObject user) {
		final String oneTimePassword = RandomStringUtils.random(oneTimePasswordLength, lettersEnabled, numbersEnabled);
		final Optional<PhoneNumber> phoneNumber = user.getPhoneNumbersOfType(PhoneNumberType.MOBILE).stream().findAny();

		if (phoneNumber.isEmpty()) {
			addErrorMessage(ERROR_NO_PHONE_NUMBER_FOUND);
			mainLog.info("->User '" + user.getLogin() + "' cannot finish authorization: No phone number found.");
			return;
		}

		if (!smsService.sendSms(SMS_AUTHORIZATION_CODE_MESSAGE.toLocalizedString(user) + oneTimePassword, phoneNumber.get().getNumber())) {
			addErrorMessage(ERROR_CODE_NOT_SENT);
			return;
		}

		user.setOneTimePassword(oneTimePassword);
		Imcms.getServices().getImcmsAuthenticatorAndUserAndRoleMapper().saveUser(user);
		fillSession(user);
	}

	public boolean checkSecondFactor(HttpServletRequest request, HttpServletResponse response, String oneTimePassword) {
		final HttpSession session = request.getSession();
		final UserDomainObject dbUser = Imcms.getServices().getImcmsAuthenticatorAndUserAndRoleMapper().getUserByOneTimePassword(oneTimePassword);
		final UserDomainObject sessionUser = (UserDomainObject) session.getAttribute("user");

		if (dbUser == null || !dbUser.equals(sessionUser)) {
			mainLog.info("->User '" + sessionUser.getLogin() + "' failed to log in: Wrong one time password.");
			final Integer attempts = lockValidator.increaseAttempts(sessionUser);

			if (lockValidator.isAmountAttemptsMorePropValue(attempts)) {
				mainLog.info("->User '" + sessionUser.getLogin() + "' User has exceeded the norm amount attempts to login.");
				lockValidator.lockUserForLogin(sessionUser.getId());
				session.setAttribute(REQUEST_PARAMETER__USERNAME, sessionUser.getLogin());
				session.setAttribute(REQUEST_PARAMETER__PASSWORD, sessionUser.getPassword());
				session.removeAttribute(SECOND_FACTOR_IN_PROGRESS);
				session.removeAttribute("user");
				return true;
			} else {
				sessionUser.setAttempts(attempts);
				addErrorMessage(ERROR_WRONG_CODE);
				initSecondFactor(sessionUser);
				return false;
			}
		}

		mainLog.info("->User '" + dbUser.getLogin() + "' proceed second factor!.");
		deactivateSecondFactor(request, response, dbUser);
		session.setAttribute(REQUEST_PARAMETER__USERNAME, dbUser.getLogin());
		session.setAttribute(REQUEST_PARAMETER__PASSWORD, dbUser.getPassword());
		session.removeAttribute(SECOND_FACTOR_IN_PROGRESS);
		session.removeAttribute("user");
		return true;
	}

	public boolean isInProgress(HttpServletRequest request) {
		return request.getSession().getAttribute(SECOND_FACTOR_IN_PROGRESS) != null && (boolean) request.getSession().getAttribute(SECOND_FACTOR_IN_PROGRESS);
	}

	private void deactivateSecondFactor(HttpServletRequest request, HttpServletResponse response, UserDomainObject user) {
		final Cookie cookie2FA = new Cookie(COOKIE_NAME_2FA, calculateUserHash(request, user));
		cookie2FA.setMaxAge(cookieLifetime);
		cookie2FA.setPath("/");

		response.addCookie(cookie2FA);
	}

	private String calculateUserHash(HttpServletRequest request, UserDomainObject user) {
		final String userDataToHash = user.getLogin() + '_' + user.getEmail() + '_' + request.getRemoteAddr() + '_' + user.getPassword();
		return DigestUtils.md5Hex(userDataToHash).toUpperCase();
	}

	private void addErrorMessage(LocalizedMessage message) {
		final ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
		servletRequestAttributes.getRequest().setAttribute(REQUEST_ATTRIBUTE__ERROR, message);
	}

	private boolean isDeactivatedByCookie(UserDomainObject user) {
		final ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
		final HttpServletRequest request = servletRequestAttributes.getRequest();
		final Cookie[] cookies = request.getCookies();

		if (cookies == null)
			return false;

		return Arrays.stream(cookies)
				.filter(cookie -> cookie.getName().equals(COOKIE_NAME_2FA))
				.findFirst()
				.map(Cookie::getValue)
				.filter(hash -> calculateUserHash(request, user).equals(hash))
				.isPresent();
	}

	private void fillSession(UserDomainObject user) {
		ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
		HttpSession session = servletRequestAttributes.getRequest().getSession();

		session.setAttribute(SECOND_FACTOR_IN_PROGRESS, true);
		session.setAttribute("user", user);
	}

	public void cleanSession(HttpSession session) {
		session.removeAttribute(REQUEST_PARAMETER__USERNAME);
		session.removeAttribute(REQUEST_PARAMETER__PASSWORD);
	}

}
