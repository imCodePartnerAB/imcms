package com.imcode

trait Log4jLoggerSupport {
  protected val logger = org.apache.log4j.Logger.getLogger(getClass)
}
