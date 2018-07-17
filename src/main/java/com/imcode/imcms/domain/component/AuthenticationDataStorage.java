package com.imcode.imcms.domain.component;

/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 17.07.18.
 */
public interface AuthenticationDataStorage {

    void storeAuthenticationData(String sessionId, String nextUrl);

}
