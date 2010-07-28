package com.imcode

import collection.mutable.ListBuffer

object Controls {
  def using[A <: {def close(): Unit}, B](param: A)(f: A => B): B = try {
    f(param)
  } finally {
    param.close()
  }

  def bmap[T](test: => Boolean)(block: => T): List[T] = {
    val ret = new ListBuffer[T]
    while(test) ret += block
    ret.toList    
  }
}