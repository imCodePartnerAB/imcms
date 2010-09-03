package com

package object imcode {

  def let[T, R](binding: T)(block: T => R) = block(binding)

  def using[A <: {def close(): Unit}, B](resource: A)(block: A => B): B = try {
    block(resource)
  } finally {
    resource.close()
  }

  def bmap[T](test: => Boolean)(block: => T): List[T] = {
    import collection.mutable.ListBuffer
    
    val ret = new ListBuffer[T]
    while(test) ret += block
    ret.toList
  }
}