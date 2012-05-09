package com

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
  type JMap[A <: AnyRef, B <: AnyRef] = java.util.Map[A, B]

  //implicit val orderingJInteger = new Ordering[JInteger] { def compare(i1: JInteger, i2: JInteger) = i1 compareTo i2 }

  // scala bug: package methods overloading does not work
  object Atoms {
    import java.util.concurrent.atomic.AtomicReference

    def OptRef[A] = new AtomicReference(Option.empty[A])
    def OptRef[A](value: A) = new AtomicReference(Option(value))
    def Ref[A <: AnyRef] = new AtomicReference[A]
    def Ref[A <: AnyRef](value: A) = new AtomicReference(value)
  }


  val EX = scala.util.control.Exception


  def ??? = new Exception().getStackTrace()(1) |> { se =>
    sys.error("Not implemented: %s.%s".format(se.getClassName, se.getMethodName))
  }

  //?? delete ??
  //def flip[A1, A2, B](f: A1 => A2 => B): A2 => A1 => B = x1 => x2 => f(x2)(x1)

  /** extractor */
  object IntNumber {
    def unapply(s: String): Option[Int] = EX.catching(classOf[NumberFormatException]) opt { s.toInt }
  }


  /** extractor */
  object PosInt {
    def unapply(s: String): Option[Int] = IntNumber.unapply(s).filter(_ >= 0)
  }


  /** extractor */
  object NegInt {
    def unapply(s: String): Option[Int] = IntNumber.unapply(s).filter(_ < 0)
  }


  class Piper[A](a: A) {
    def |>[B](f: A => B): B = f(a)

    def |>>(f: A => Any): A = { f(a); a }
  }

  implicit def any2Piper[A](a: A) = new Piper(a)


  def unfold[A, B](init: A)(f: A => Option[(B, A)]): List[B] = f(init) match {
    case None => Nil
    case Some((r, next)) => r :: unfold(next)(f)
  }

  /** Creates zero arity fn from by-name parameter. */
  //def toF[A](byName: => A): () => A = byName _

  // scala bug:
  // import Option.{apply => ?}
  //  scala> {
  //       | import Option.apply
  //       | import Option.{apply => ?}
  //       |
  //       | val r1 = apply('ok) // Option[Symbol]
  //       | val f1 = apply _    // Nothing => Option[Nothing]
  //       |
  //       | val r2 = ?('ok)     // Option[Symbol]
  //       | val f2 = ? _        // error: value ? is not a member of object Option
  //       | }
  //def ?[A <: AnyRef](nullable: A) = Option(nullable)

  def option[A](value: A) = Option(value)

  def when[A](exp: Boolean)(byName: => A): Option[A] = PartialFunction.condOpt(exp) { case true => byName }

  def doall[A](exp: A, exps: A*)(f: A => Any) {
    exp +: exps foreach f
  }


  trait CloseableResource[R] {
    def close(resource: R)
  }


  object CloseableResource {
    implicit def stCloseableResource[R <: { def close() }](r: R) = new CloseableResource[R] {
      def close(resource: R) { resource.close() }
      override def toString = "Resource[_ <: def close()]"
    }

    implicit def ioCloseableResource[R <: java.io.Closeable] = new CloseableResource[R] {
      def close(resource: R) { resource.close() }
      override def toString = "Resource[_ <: java.io.Closeable]"
    }
  }


  def using[R: CloseableResource, A](resource: R)(f: R => A): A =
    try {
      f(resource)
    } finally {
      if (resource != null) {
        EX.allCatch(implicitly[CloseableResource[R]].close(resource))
      }
    }

//  def bmap[A](test: => Boolean)(byName: => A): List[A] = {
//    import collection.mutable.ListBuffer
//
//    val ret = new ListBuffer[A]
//    while (test) ret += byName
//    ret.toList
//  }

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