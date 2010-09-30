package com

package object imcode {

  def flip[A1, A2, B](f: A1 => A2 => B): A2 => A1 => B = x1 => x2 => f(x2)(x1)

  object IntNumber {
    def unapply(s: String): Option[Int] = try {
      Some(s.toInt)
    } catch {
      case _ : java.lang.NumberFormatException => None
    }
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

  def ?[A <: AnyRef](nullable: A): Option[A] = if (nullable eq null) None else Some(nullable)

  def let[B, T](expr: B)(block: B => T): T = block(expr)

  def letret[B, T](expr: B)(block: B => T): B = {
    block(expr)
    expr
  }

  def forlet[T](exprs: T*)(block: T => Unit): Unit = exprs foreach block  

  def using[R <: {def close(): Unit}, T](resource: R)(block: R => T): T = try {
    block(resource)
  } finally {
    resource.close()
  }

  def bmap[T](test: => Boolean)(block: => T): List[T] = {
    import collection.mutable.ListBuffer
    
    val ret = new ListBuffer[T]
    while (test) ret += block
    ret.toList
  }

  //def unit(block: => Unit) = block _

  /**
   * Converts camel-case string into underscore.
   * ex: IPAccess => ip_access, SearchTerms => search_terms, mrX => mr_x, iBot => i_bot
   */
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