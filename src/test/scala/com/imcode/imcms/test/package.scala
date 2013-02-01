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

  implicit def smiToAnswer[A](fn: InvocationOnMock => A): Answer[A] =
    new Answer[A] {
      def answer(invocation: InvocationOnMock): A = fn(invocation)
    }

  implicit def argsToAnswer[A](fn: Array[AnyRef] => A): Answer[A] =
    new Answer[A] {
      def answer(invocation: InvocationOnMock): A = fn(invocation.getArguments)
    }

  implicit def fn0ToAnswer[A](fn: () => A): Answer[A] =
    new Answer[A] {
      def answer(invocation: InvocationOnMock): A = fn()
    }
}