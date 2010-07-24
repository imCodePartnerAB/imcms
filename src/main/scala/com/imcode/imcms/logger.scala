package com.imcode.imcms.logger

import org.slf4j.{LoggerFactory}

trait Logger {

  protected val logger = LoggerFactory.getLogger(getClass);
}