package com.imcode.imcms.domain.component;

import org.springframework.stereotype.Component;

/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 10.07.18.
 */
@Component
public class LocalUserEditPostValidationActionConsumer extends UserPostValidationActionConsumer {

    LocalUserEditPostValidationActionConsumer(LocalUserCreationValidator userValidator) {
        super(userValidator);
    }
}
