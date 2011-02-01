package com.imcode
package imcms

package object test {
  def withLogFailure[T](block: => T) =
    EX.allCatch.withApply[T] { t =>
      t.printStackTrace
      throw t
    } apply block
}