package com.imcode
package util

import java.util.concurrent.atomic.AtomicReference

object Atomics {
  def Reference[A <: AnyRef]: AtomicReference[A] = new AtomicReference[A]
  def Reference[A <: AnyRef](value: A): AtomicReference[A] = new AtomicReference(value)

  def OptionReference[A]: AtomicReference[Option[A]] = new AtomicReference(None)
  def OptionReference[A](value: A): AtomicReference[Option[A]] = new AtomicReference(Option(value))

  def swap[A](reference: AtomicReference[A])(fn: A => A): A = reference.get |> fn |>> reference.set
  def swap[A](fn: A => A)(reference: AtomicReference[A]): A = swap(reference)(fn)
}
