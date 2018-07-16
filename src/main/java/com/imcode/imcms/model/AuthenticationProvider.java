package com.imcode.imcms.model;

import lombok.Getter;

public abstract class AuthenticationProvider {

    @Getter
    protected String authenticationURL;
    @Getter
    protected String authenticatorName;

}
