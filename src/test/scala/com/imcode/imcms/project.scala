package com.imcode
package imcms

import java.util.concurrent.atomic.{AtomicLong, AtomicReference}
import java.util.Properties
import java.io.{FileReader, File}
import org.springframework.context.support.FileSystemXmlApplicationContext
import org.springframework.context.ApplicationContext

object Util {
  // ??? If located inside createFileWatcher then compiles but init fails ???
  case class State[T](lastAccessNano: Long, lastModified: Long, handlerResult: T)

  def createFileWatcher[T](fileFn: () => File, poolIntervalNano: Long = 1000)(handler: File => T) = new Function0[T] {

    val stateRef = new AtomicReference(Option.empty[State[T]])

    def apply() = synchronized {
      val now = System.nanoTime
      val state = stateRef.get match {
        case Some(state @ State(lastAccessNano, lastModified, handlerResult))
          if lastAccessNano + poolIntervalNano < now || fileFn().lastModified == lastModified =>
            state.copy(lastAccessNano = now)

        case _ => let(fileFn()) { file =>
          State(now, file.lastModified, handler(file))
        }
      }

      stateRef set Some(state)

      state.handlerResult
    }
  }
}


object Project {
  def apply(): Project = apply(".")
  def apply(homePath: String) = new Project(homePath)

  def initImcms(prepareDBOnStart: Boolean = false) {}
}

class Project(homePath: String) {

  private val homeRef = new AtomicReference[File]

  private val buildPropertiesFileWatcher = Util.createFileWatcher(fileFn("build.properties")) { file =>
    using(new FileReader(file)) { reader =>
      letret(new Properties) { _ load reader }
    }
  }

  private val springAppContextRef = new AtomicReference(Option.empty[ApplicationContext])

  cd(homePath)


  def cd(homePath: String) = homeRef.set(new File(homePath).getCanonicalFile)

  def home = homeRef.get

  //def path = home.getCanonicalPath

  def file(relativePath: String) = new File(home, relativePath)

  def fileFn(relativePath: String) = () => file(relativePath)

  def dir(relativePath: String) = new File(home, relativePath)

  def dirFn(relativePath: String) = () => dir(relativePath)

  def buildProperties = buildPropertiesFileWatcher()

  def buildProperty(name: String) = buildProperties getProperty name

  def testProperty() = error("not impl")

  def springAppContext(reload: Boolean = false) = synchronized {
    if (springAppContextRef.get.isEmpty || reload) {
      springAppContextRef set Some(new FileSystemXmlApplicationContext(
        "file:" + file("src/test/resources/applicationContextTest.xml").getCanonicalPath))
    }

    springAppContextRef.get.get
  }
}