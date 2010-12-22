package com

package object imcode {

  type JClass[T >: Null] = java.lang.Class[T]
  type JBoolean = java.lang.Boolean
  type JInteger = java.lang.Integer
  type JLong = java.lang.Long
  type JFloat = java.lang.Float
  type JDouble = java.lang.Double
  type JCollection[A] = java.util.Collection[A]

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

  /** Creates zero arity fn from by-name parameter. */
  def block(byName: => Unit) = byName _

  def ?[A >: Null](nullable: A) = Option(nullable)

  def let[B, T](expr: B)(fn: B => T): T = fn(expr)

  def letret[B](expr: B)(fn: B => Any): B = {
    fn(expr)
    expr
  }

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