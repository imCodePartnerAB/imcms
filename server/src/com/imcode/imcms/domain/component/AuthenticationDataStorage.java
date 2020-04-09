package com.imcode.imcms.domain.component;

public interface AuthenticationDataStorage {

    void storeAuthenticationData(String sessionId, String nextUrl);

}
