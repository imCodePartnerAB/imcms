package com.imcode.imcms

import scala.util.control.{Exception => Ex}
import org.mockito.invocation.InvocationOnMock
import org.mockito.stubbing.Answer

package object test {
  def withLogFailure[T](block: => T) =
    Ex.allCatch.withApply[T] { t =>
      t.printStackTrace
      throw t
    } apply block

  implicit def fnToAnswer[A](f: InvocationOnMock => A): Answer[A] =
    new Answer[A] {
      def answer(invocation: InvocationOnMock): A = f(invocation)
    }
}