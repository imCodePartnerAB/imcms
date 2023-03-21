package com.imcode.imcms.domain.exception;

import lombok.extern.log4j.Log4j2;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;

import java.lang.reflect.Method;

@Log4j2
public class ImcmsAsyncExceptionHandler implements AsyncUncaughtExceptionHandler {
	@Override
	public void handleUncaughtException(Throwable ex, Method method, Object... params) {
		log.error("Exception message - " + ex.getMessage());
		log.error("Method name - " + method.getName());

		for (Object param : params) {
			log.error("Parameter value - " + param);
		}
	}
}
