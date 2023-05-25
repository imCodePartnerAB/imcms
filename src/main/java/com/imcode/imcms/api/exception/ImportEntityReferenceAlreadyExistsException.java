package com.imcode.imcms.api.exception;

import com.imcode.imcms.model.ImportEntityReferenceType;

import java.io.Serial;

public class ImportEntityReferenceAlreadyExistsException extends RuntimeException {
	@Serial
	private static final long serialVersionUID = 4151038391929948843L;

	public ImportEntityReferenceAlreadyExistsException(String name, ImportEntityReferenceType type) {
		super(String.format("Name: %s, type: %s", name, type));
	}
}
