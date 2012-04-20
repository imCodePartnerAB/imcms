package com.imcode

trait Slf4jLoggerSupport {
  protected val logger = org.slf4j.LoggerFactory.getLogger(getClass);
}

trait Log4jLoggerSupport {
  protected val logger =  org.apache.log4j.Logger.getLogger(getClass)
}