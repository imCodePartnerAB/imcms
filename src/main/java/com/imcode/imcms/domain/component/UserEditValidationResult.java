package com.imcode.imcms.domain.component;

import com.imcode.imcms.domain.dto.UserDTO;
import com.imcode.imcms.domain.dto.UserFormData;
import com.imcode.imcms.domain.exception.UserNotExistsException;
import com.imcode.imcms.domain.service.UserService;
import imcode.util.Utility;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Objects;

/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 10.07.18.
 */
class UserEditValidationResult extends UserValidationResult {

    UserEditValidationResult(UserFormData userData, UserService userService) {
        super(userData, userService);
    }

    @Override
    protected void validateEmail(UserFormData userData, UserService userService) {
        final String email = userData.getEmail();

        if (email == null) {
            return;
        }

	    setEmailValid(Utility.isValidEmail(email));

	    final List<UserDTO> usersByEmail = userService.getUsersByEmail(email);

	    if (usersByEmail.isEmpty()) return;
	    if ((usersByEmail.size() == 1) && usersByEmail.get(0).getId().equals(userData.getId())) return;

	    setEmailAlreadyTaken(true);
    }

	@Override
	protected void validatePasswords(UserFormData formData) {
		String password1 = StringUtils.defaultString(formData.getPassword());
		String password2 = StringUtils.defaultString(formData.getPassword2());

		if (Objects.equals(password1, password2)) {
			if (!password1.isEmpty()) {
				validatePassword1(password1);
				validatePassword1(password2);
			}
			setPasswordsEqual(true);
		} else {
			setPasswordsEqual(false);
		}
		setPasswordTooWeak(password1.equalsIgnoreCase(formData.getLogin()));
	}

	@Override
	protected void validateLoginName(UserFormData userData, UserService userService) {
		final String login = StringUtils.defaultString(userData.getLogin());

		setEmptyLoginName(StringUtils.isBlank(login));

		try {
			final UserDTO user = userService.getUser(login);
			setLoginAlreadyTaken(!user.getId().equals(userData.getId()));

        } catch (UserNotExistsException e) {
            setLoginAlreadyTaken(false);
        }
    }
}
