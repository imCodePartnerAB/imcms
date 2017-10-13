package com.imcode.imcms.domain.exception;

import static java.lang.String.format;

public class UserNotExistsException extends RuntimeException {
    private static final long serialVersionUID = 7026118015715714785L;

    public UserNotExistsException(int id) {
        super(format("User with id = %d does not exist!", id));
    }

    public UserNotExistsException(String login) {
        super(format("User with login = %s does not exist!", login));
    }

}
