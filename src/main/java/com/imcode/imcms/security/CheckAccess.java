package com.imcode.imcms.security;

import com.imcode.imcms.api.exception.NoPermissionException;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation marks method means that user is checked (before target method execution) for specified access with
 *
 * {@link NoPermissionException} if access not granted.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CheckAccess {

    /**
     * Access to specified data or actions by {@link AccessRoleType} such as {@code ALL}, {@code DOCUMENT_EDITOR}, etc.
     *
     * @return specified access type
     */
    AccessRoleType[] role() default {AccessRoleType.ALL};

    /**
     * Access to specified content by {@link AccessContentType} such as {@code ALL}, {@code IMAGE}, {@code TEXT}, etc.
     *
     * @return specified access type
     */
    AccessContentType[] docPermission() default {};
}
