package com.imcode
package util

import java.util.concurrent.Callable

object Threads {

  def mkThread(runBody: => Unit): Thread =
    new Thread {
      override def run() {
        runBody
      }
    }

  def mkRunnable(runBody: => Unit): Runnable =
    new Runnable {
      def run() { runBody }
    }

  def mkCallable[A](callBody: => A): Callable[A]  =
    new Callable[A] {
      def call(): A = callBody
    }

  def spawn(runBody: => Unit): Thread = mkThread(runBody) |>> { t => t.start() }
  def spawnDaemon(runBody: => Unit): Thread = mkThread(runBody) |>> { t => t.setDaemon(true); t.start() }

  def terminated(thread: Thread): Boolean = thread == null || thread.getState == Thread.State.TERMINATED
  def notTerminated(thread: Thread): Boolean = !terminated(thread)

  def interruptAndAwaitTermination(thread: Thread) {
    if (thread != null) {
      thread.interrupt()
      thread.join()
    }
  }
}
