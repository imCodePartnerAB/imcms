package com.imcode.imcms.components;

import jakarta.validation.ValidationException;

/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 12.04.18.
 */
public interface Validator<T> {

    void validate(T validateMe) throws ValidationException;

}
