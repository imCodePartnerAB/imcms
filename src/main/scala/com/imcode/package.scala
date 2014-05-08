package com

import scala.language.higherKinds
import scala.util.Try

package object imcode {

  type JBoolean = java.lang.Boolean
  type JByte = java.lang.Byte
  type JCharacter = java.lang.Character
  type JInteger = java.lang.Integer
  type JLong = java.lang.Long
  type JFloat = java.lang.Float
  type JDouble = java.lang.Double

  type JCollection[A <: AnyRef] = java.util.Collection[A]
  type JList[A <: AnyRef] = java.util.List[A]
  type JSet[A <: AnyRef] = java.util.Set[A]
  type JMap[A <: AnyRef, B <: AnyRef] = java.util.Map[A, B]
  type JIterator[A <: AnyRef] = java.util.Iterator[A]

  /**
   * F#-like pipe operators.
   *
   * <p>To workaround unintended semicolon inference use parentheses:
   * <code>
   * val result = (
   *   "value"
   *     |> {_.length}
   *     |> {_ * 2}
   *     |> {_.toString}
   *     |> {_.length}
   * )
   * </code>
   */
  implicit class PipeOperators[A](a: A) {
    def |>[B](fn: A => B): B = fn(a)

    def |>>(fn: A => Any): A = { fn(a); a }
  }

  implicit class NullableOps[A <: AnyRef](nullable: A) {
    def asOption: Option[A] = Option(nullable)
  }

  implicit class StringOps(string: String) {
    def trimToNull: String = string.trimToOption.orNull
    def trimToEmpty: String = string.asOption.map(_.trim).getOrElse("")
    def trimToOption: Option[String] = string.asOption.map(_.trim).filter(_.length > 0)
  }

  def opt[A](value: A) = Option(value)

  def when[A](exp: Boolean)(value: => A): Option[A] = PartialFunction.condOpt(exp) { case true => value }

  def whenSingleton[A, B](traversable: Traversable[A])(fn: A => B): Option[B] = {
    when(traversable.size == 1) {
      fn(traversable.head)
    }
  }

  def whenNotEmpty[A, T[X] <: Traversable[X], B](traversable: T[A])(fn: T[A] => B): Option[B] = {
    when(traversable.nonEmpty) {
      fn(traversable)
    }
  }

  /** extractor */
  object AnyInt {
    def unapply(string: String): Option[Int] = Try(string.toInt).toOption
  }

  /** extractor */
  object PosInt {
    def unapply(string: String): Option[Int] = AnyInt.unapply(string).filter(_ > 0)
  }

  /** extractor */
  object NonNegInt {
    def unapply(string: String): Option[Int] = AnyInt.unapply(string).filter(_ >= 0)
  }

  /** extractor */
  object NegInt {
    def unapply(string: String): Option[Int] = AnyInt.unapply(string).filter(_ < 0)
  }

  def using[R : ManagedResource, A](resource: R)(fn: R => A): A = {
    try {
      fn(resource)
    } finally {
      if (resource != null)
        Try(implicitly[ManagedResource[R]].close(resource))
    }
  }
}