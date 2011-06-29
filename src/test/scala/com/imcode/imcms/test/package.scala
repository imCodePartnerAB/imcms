package com.imcode.imcms

import com.imcode.EX

package object test {
  def withLogFailure[T](block: => T) =
    EX.allCatch.withApply[T] { t =>
      t.printStackTrace
      throw t
    } apply block
}