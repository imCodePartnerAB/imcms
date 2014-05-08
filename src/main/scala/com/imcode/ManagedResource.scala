package com.imcode

import scala.language.implicitConversions
import scala.language.reflectiveCalls

trait ManagedResource[R] {
  def close(resource: R)
}

object ManagedResource {

  implicit def ioManagedResource[R <: java.io.Closeable] = new ManagedResource[R] {
    def close(resource: R) { resource.close() }
    override def toString = "ManagedResource[_ <: java.io.Closeable]"
  }

  implicit def stCloseManagedResource[R <: { def close() }](r: R) = new ManagedResource[R] {
    def close(resource: R) { resource.close() }
    override def toString = "ManagedResource[_ <: { def close() }]"
  }

  implicit def stShutdownManagedResource[R <: { def shutdown() }](r: R) = new ManagedResource[R] {
    def close(resource: R) { resource.shutdown() }
    override def toString = "ManagedResource[_ <: { def shutdown() }]"
  }
}
