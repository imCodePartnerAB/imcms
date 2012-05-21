package com.imcode.imcms

import scala.util.control.{Exception => Ex}

package object test {
  def withLogFailure[T](block: => T) =
    Ex.allCatch.withApply[T] { t =>
      t.printStackTrace
      throw t
    } apply block
}