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
  type JClass[A >: Null] = java.lang.Class[A]
  type JCollection[A <: AnyRef] = java.util.Collection[A]
  type JList[A <: AnyRef] = java.util.List[A]
  type JSet[A <: AnyRef] = java.util.Set[A]
  type JMap[A <: AnyRef, B <: AnyRef] = java.util.Map[A, B]
  type JIterator[A <: AnyRef] = java.util.Iterator[A]


  def using[R : ManagedResource, A](resource: R)(fn: R => A): A = {
    try {
      fn(resource)
    } finally {
      if (resource != null)
        Try(implicitly[ManagedResource[R]].close(resource))
    }
  }


  implicit class NullableOps[A <: AnyRef](nullable: A) {
    def asOption: Option[A] = Option(nullable)
  }


  implicit class StringOps(string: String) {
    def trimToNull: String = string.trimToOption.orNull
    def trimToEmpty: String = string.asOption.map(_.trim).getOrElse("")
    def trimToOption: Option[String] = string.asOption.map(_.trim).filter(_.length > 0)
  }

  /**
   * Emulates Java ternary operator in the form {@code condition ? thenExpression | elseExpression}.
   */
  implicit class TernaryOperator(condition: Boolean) {
    def ?[A](thenExpression: => A) = new IfThenElse[A](thenExpression)

    class IfThenElse[+A](thenExpression: => A) {
      def |[U >: A](elseExpression: => U): U = if (condition) thenExpression else elseExpression
    }
  }


  implicit class PipeOperators[A](a: A) {
    def |>[B](fn: A => B): B = fn(a)

    def |>>(fn: A => Any): A = { fn(a); a }
  }

//  "value"
//    . |> { v =>
//      v.length
//    } |> { v =>
//      v * 2
//    } |> { v =>
//      v.toString
//    } |> { v =>
//      v.length
//    }
//
//  "value"
//    . |> {
//      case v => v.length
//    } |> {
//      case v => v * 2
//    } |> {
//      case v => v.toString
//    } |> {
//      case v => v.length
//    }

  // scala bug: 'import Option.{apply => opt}' - 'opt' can not be used as a function
  // scala> import Option.apply
  // import Option.apply
  //
  // scala> import Option.{apply => opt}
  // import Option.{apply=>opt}
  //
  // scala> "foo" |> Option.apply
  // res0: Option[java.lang.String] = Some(foo)
  //
  // scala> "foo" |> apply
  // res1: Option[java.lang.String] = Some(foo)
  //
  // scala> opt("foo")
  // res2: Option[java.lang.String] = Some(foo)
  //
  // scala> "foo" |> opt
  // <console>:12: error: value opt is not a member of object Option
  //               "foo" |> opt
  def opt[A](value: A) = Option(value)

  def whenOpt[A](exp: Boolean)(byName: => A): Option[A] = PartialFunction.condOpt(exp) { case true => byName }

  def doto[A](exp: A, exps: A*)(fn: A => Any) {
    (exp +: exps).foreach(fn)
  }


  def unfold[A, B](init: A)(fn: A => Option[(B, A)]): Stream[B] = fn(init) match {
    case None => Stream.empty
    case Some((r, next)) => r #:: unfold(next)(fn)
  }

  def whenSingle[A, T[A] <: Traversable[A], B](traversable: T[A])(fn: A => B): Option[B] = {
    whenOpt(traversable.size == 1) {
      fn(traversable.head)
    }
  }

  def whenNotEmpty[A, T[A] <: Traversable[A], B](traversable: T[A])(fn: T[A] => B): Option[B] = {
    whenOpt(traversable.nonEmpty) {
      fn(traversable)
    }
  }


  // scala bug: package methods overloading does not work
  object Atoms {
    import java.util.concurrent.atomic.AtomicReference

    def OptRef[A] = new AtomicReference(Option.empty[A])
    def OptRef[A](value: A) = new AtomicReference(Option(value))
    def Ref[A <: AnyRef] = new AtomicReference[A]
    def Ref[A <: AnyRef](value: A) = new AtomicReference(value)

    def swap[A](ref: AtomicReference[A])(fn: A => A): A = ref.get |> fn |>> ref.set
    def swap[A](fn: A => A)(ref: AtomicReference[A]): A = swap(ref)(fn)
  }


  /** extractor */
  object AnyInt {
    def unapply(string: String): Option[Int] = Try(string.toInt).toOption
  }


  /** extractor */
  object PosInt {
    def unapply(string: String): Option[Int] = AnyInt.unapply(string).filter(_ >= 0)
  }


  /** extractor */
  object NegInt {
    def unapply(string: String): Option[Int] = AnyInt.unapply(string).filter(_ < 0)
  }




  /**
   * Converts camel-case string into underscore.
   * ex: IPAccess => ip_access, SearchTerms => search_terms, mrX => mr_x, iBot => i_bot
   */
  def camelCaseToUnderscore(s: String): String = {
    def camelCaseToUnderscore(chars: List[Char]): List[Char] =
      chars.span(c => c.isLower || !c.isLetter) match {
        case (lowers, Nil) => lowers
        case (Nil, rest) => (rest.span(_.isUpper) : @unchecked) match {
          case (u :: Nil, rest2) => camelCaseToUnderscore(u.toLower :: rest2)
          case (uppers@(u1 :: u2 :: _), rest2) =>
            if (rest2.isEmpty) uppers.map(_.toLower)
            else uppers.init.map(_.toLower) ++ ('_' :: camelCaseToUnderscore(uppers.last.toLower :: rest2))
        }
        case (lowers, rest) => lowers ++ ('_' :: camelCaseToUnderscore(rest))
      }

    camelCaseToUnderscore(s.toList).mkString
  }

//  class Default[T](init: => T) { def value = init }
//
//  object Default {
//    import scala.reflect.ClassTag
//
//    implicit final class Ops[A >: Null : Default](value: A) {
//      def orDefault = if (value != null) value else implicitly[Default[A]].value
//    }
//
//    implicit object defaultString extends Default("")
//    implicit object defaultJInteger extends Default[java.lang.Integer](0)
//
//    implicit def defaultList[A] = new Default[List[A]](List.empty)
//    implicit def defaultMap[A, B] = new Default[Map[A, B]](Map.empty)
//    implicit def defaultArray[A : ClassTag] = new Default[Array[A]](Array())
//  }

}