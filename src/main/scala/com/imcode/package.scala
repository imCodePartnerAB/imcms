package com

package object imcode {

  type JClass[A >: Null] = java.lang.Class[A]
  type JBoolean = java.lang.Boolean
  type JInteger = java.lang.Integer
  type JLong = java.lang.Long
  type JFloat = java.lang.Float
  type JDouble = java.lang.Double
  type JCollection[A >: Null] = java.util.Collection[A]
  type JList[A >: Null] = java.util.List[A]

  implicit val orderingJInteger = new Ordering[JInteger] { def compare(i1: JInteger, i2: JInteger) = i1 compareTo i2 }

  val EX = scala.util.control.Exception

  def ??? = let((new Exception).getStackTrace()(1)) { se =>
    error("Not implemented: %s.%s".format(se.getClassName, se.getMethodName))
  }

  //?? delete ??
  def flip[A1, A2, B](f: A1 => A2 => B): A2 => A1 => B = x1 => x2 => f(x2)(x1)

  //?? delete ??
  object IntNumber {
    import scala.util.control.Exception.catching

    def unapply(s: String): Option[Int] = catching(classOf[NumberFormatException]) opt { s.toInt }
  }

//  object PosInt {
//    def unapply(s: String): Boolean = s match {
//      case Int(n) => n >= 0
//      case _ => false
//    }
//  }
//
//  object NegInt {
//    def unapply(s: String): Boolean = s match {
//      case Int(n) => n < 0
//      case _ => false
//    }
//  }

  class Piper[A](a: A) { def |>[B](f: A => B) = f(a) }

  implicit def pipe_everything[A](a: A) = new Piper(a)

  def unfold[A, B](init: A)(f: A => Option[(B, A)]): List[B] = f(init) match {
    case None => Nil
    case Some((r, next)) => r :: unfold(next)(f)
  }

  /** Creates zero arity fn from by-name parameter. */
  def block(byName: => Unit) = byName _

  def ?[A >: Null](nullable: A) = Option(nullable)

  def let[B, T](expr: B)(fn: B => T): T = fn(expr)

  def letret[B](expr: B)(fn: B => Any): B = {
    fn(expr)
    expr
  }

  //def whenNonEmpty[A](xs:Seq[A])(f: Seq[A] => Unit) = if (xs.nonEmpty) f(xs)

  def forlet[T](exprs: T*)(fn: T => Unit): Unit = exprs foreach fn

  def using[R <: {def close(): Unit}, T](resource: R)(fn: R => T): T = try {
    fn(resource)
  } finally {
    resource.close()
  }

  def bmap[T](test: => Boolean)(fn: => T): List[T] = {
    import collection.mutable.ListBuffer
    
    val ret = new ListBuffer[T]
    while (test) ret += fn
    ret.toList
  }

  /**
   * Converts camel-case string into underscore.
   * ex: IPAccess => ip_access, SearchTerms => search_terms, mrX => mr_x, iBot => i_bot
   */
  @deprecated("prototype")
  def camelCaseToUnderscore(s: String): String = {
    def camelCaseToUnderscore(chars: List[Char]): List[Char] =
      chars span (c => c.isLower || !c.isLetter) match {
        case (lowers, Nil) => lowers
        case (Nil, rest) => (rest span (_.isUpper) : @unchecked) match {
          case (u :: Nil, rest) => camelCaseToUnderscore(u.toLower :: rest)
          case (uppers @ (u1 :: u2 :: _), rest) =>
            if (rest.isEmpty) uppers map (_.toLower)
            else uppers.init.map(_.toLower) ++ ('_' :: camelCaseToUnderscore(uppers.last.toLower :: rest))
        }
        case (lowers, rest) => lowers ++ ('_' :: camelCaseToUnderscore(rest))
      }

    camelCaseToUnderscore(s.toList) mkString
  }
}