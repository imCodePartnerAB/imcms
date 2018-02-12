package com.imcode.imcms.security;

import imcode.server.document.NoPermissionToEditDocumentException;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static com.imcode.imcms.security.AccessType.ALL;

/**
 * Annotation marks method means that user is checked (before target method
 * execution) for specified access with
 * {@link NoPermissionToEditDocumentException} if access not granted.
 *
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 12.02.18.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CheckAccess {

    /**
     * Access to specified content such as all, image, text, etc.
     *
     * @return specified access type
     */
    AccessType value() default ALL;

}
