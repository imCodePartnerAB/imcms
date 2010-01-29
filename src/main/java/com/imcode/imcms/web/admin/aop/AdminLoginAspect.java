package com.imcode.imcms.web.admin.aop;

import imcode.server.Imcms;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

/**
 *
 * Prototype class
 *
 */
@Aspect
public class AdminLoginAspect {

	/**
	 * If user isn't the superadmin, an empty page will be shown.
	 */
	@Around("execution(public * com.imcode.imcms.web.admin.controller.*.*Handler(..))")
	public Object chackAdminLogin(ProceedingJoinPoint pjp) throws Throwable {
		
		return Imcms.getDocumentRequest().getUser().isSuperAdmin() ? pjp.proceed() : null;

	}
}
