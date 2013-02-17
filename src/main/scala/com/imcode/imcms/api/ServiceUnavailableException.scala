package com.imcode.imcms.api

class ServiceUnavailableException(message: String = null, cause: Throwable = null)
  extends ServiceErrorException(message, cause)