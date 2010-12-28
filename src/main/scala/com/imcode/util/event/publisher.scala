package com.imcode
package util.event

trait Publisher[T] {
  var listeners = List.empty[T => Unit]

  def listen(listener: T => Unit) {
    listeners ::= listener
  }

  def notifyListeners(ev: T) = for (l <- listeners) l(ev)

  def notifyListeners(): Unit = error("Not implemented")
}