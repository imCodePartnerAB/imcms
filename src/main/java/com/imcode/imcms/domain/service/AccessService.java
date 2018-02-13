package com.imcode.imcms.domain.service;

/**
 * To know do the user have access to do something with some document or not.
 *
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 13.02.18.
 */
public interface AccessService {
    boolean hasUserEditAccess(int userId, Integer documentId);
}
