package com

package object imcode {

  def let[B, T](binding: B)(block: B => T): T = block(binding)

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
}