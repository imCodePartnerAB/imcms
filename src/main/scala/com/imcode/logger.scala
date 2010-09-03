package com.imcode

import org.slf4j.{LoggerFactory}

trait Logger {
  protected val logger = LoggerFactory.getLogger(getClass);
}