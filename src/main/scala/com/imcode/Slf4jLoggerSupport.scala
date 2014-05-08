package com.imcode

trait Slf4jLoggerSupport {
  protected val logger = org.slf4j.LoggerFactory.getLogger(getClass)
}
