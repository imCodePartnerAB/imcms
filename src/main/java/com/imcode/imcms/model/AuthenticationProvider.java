package com.imcode.imcms.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.imcode.imcms.domain.dto.ExternalRole;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@EqualsAndHashCode
public abstract class AuthenticationProvider {

    @Getter
    protected final String authenticationURL;
    @Getter
    protected final String providerId;
    @Getter
    protected final String providerName;
    @Getter
    protected final String iconPath;

    public AuthenticationProvider(String authenticationURL, String providerId, String providerName, String iconPath) {
        this.authenticationURL = authenticationURL;
        this.providerId = providerId;
        this.providerName = providerName;
        this.iconPath = iconPath;
    }

    public abstract String buildAuthenticationURL(String redirectURL, String sessionId, String nextUrl);

    /**
     * Returns URI user should be redirected to
     */
    public abstract String processAuthentication(HttpServletRequest request);

    @JsonIgnore
    public abstract ExternalUser getUser(HttpServletRequest request);

    public abstract void updateAuthData(HttpServletRequest request);

    @JsonIgnore
    public abstract List<ExternalRole> getRoles();
}
