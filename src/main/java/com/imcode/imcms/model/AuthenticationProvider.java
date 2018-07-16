package com.imcode.imcms.model;

import lombok.Getter;

public abstract class AuthenticationProvider {

    @Getter
    protected String authenticationURL;
    @Getter
    protected String providerId;
    @Getter
    protected String providerName;
    @Getter
    protected String iconPath;

}
