package com.imcode

import org.apache.log4j.Logger

trait Log4jLogger {
  protected val logger = Logger.getLogger(getClass)
}
