package com.imcode.util

object Streams {
  def unfold[A, B](init: A)(fn: A => Option[(B, A)]): Stream[B] = fn(init) match {
    case None => Stream.empty
    case Some((r, next)) => r #:: unfold(next)(fn)
  }
}
