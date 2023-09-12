package com.imcode.imcms.domain.exception;

import java.io.Serial;

public class ImageAlternateTextRequiredException extends RuntimeException{
	@Serial
	private static final long serialVersionUID = -276694678730909089L;

	public ImageAlternateTextRequiredException() {
	}

	public ImageAlternateTextRequiredException(String message) {
		super(message);
	}

	public ImageAlternateTextRequiredException(String message, Throwable cause) {
		super(message, cause);
	}

	public ImageAlternateTextRequiredException(Throwable cause) {
		super(cause);
	}

	public ImageAlternateTextRequiredException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
