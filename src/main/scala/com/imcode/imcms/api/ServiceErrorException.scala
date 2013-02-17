package com.imcode.imcms.api

class ServiceErrorException(message: String = null, cause: Throwable = null) extends RuntimeException(message, cause)