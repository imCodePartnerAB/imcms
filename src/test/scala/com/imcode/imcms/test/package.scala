package com.imcode
package imcms

import org.mockito.invocation.InvocationOnMock
import org.mockito.stubbing.Answer
import scala.util.control.NonFatal

package object test {

  def withLogFailure[T](block: => T) =
    try {
      block
    } catch {
      case NonFatal(e) => e.printStackTrace()
    }

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