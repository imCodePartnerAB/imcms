package com.imcode
package imcms

import java.util.concurrent.atomic.{AtomicLong, AtomicReference}
import java.util.Properties
import java.io.{FileReader, File}
import org.springframework.context.support.FileSystemXmlApplicationContext
import org.springframework.context.ApplicationContext
import imcode.server.Imcms

object Util {
  // ??? If located inside createFileWatcher then compiles but init fails ???
  case class State[T](lastAccessNano: Long, lastModified: Long, handlerResult: T)

  //def createResourceWatcher[R, H](resourceFn: () => R, poolIntervalNano: Long = 1000)(handler: R => H)

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


class Project(dirPath: String) {

  private val buildPropertiesFileWatcher = Util.createFileWatcher(fileFn("build.properties")) { file =>
    using(new FileReader(file)) { reader =>
      letret(new Properties) { _ load reader }
    }
  }

  private val springAppContextRef = new AtomicReference(Option.empty[ApplicationContext])

  val dir = new File(dirPath).getCanonicalFile

  val buildProperties = buildPropertiesFileWatcher()

  def buildProperty(name: String) = buildProperties getProperty name

  def path(relativePath: String) = new File(dir, relativePath).getCanonicalPath

  def file(relativePath: String) = new File(dir, relativePath)

  def fileFn(relativePath: String) = () => file(relativePath)

  def subDir(relativePath: String) = new File(dir, relativePath)

  def subDirFn(relativePath: String) = () => subDir(relativePath)

  def testProperty() = error("not impl")

  def springAppContext(reload: Boolean = false) = synchronized {
    System.setProperty("log4j.configuration", "file:" + path("src/test/resources/log4j.xml"))
    System.setProperty("com.imcode.imcms.project.dir", path("."))

    if (springAppContextRef.get.isEmpty || reload) {
      springAppContextRef set Some(new FileSystemXmlApplicationContext(
        "file:" + file("src/test/resources/applicationContextTest.xml").getCanonicalPath))
    }

    springAppContextRef.get.get
  }

  def initImcms(start: Boolean = false, prepareDBOnStart: Boolean = false) {
    Imcms.setRelativePrefsConfigPath("")
    Imcms.setPath(subDir("src/test/resources"))
    Imcms.setSQLScriptsPath(path("src/main/webapp/WEB-INF/sql"))
    Imcms.setApplicationContext(springAppContext())
    Imcms.setPrepareDatabaseOnStart(prepareDBOnStart)

    if (start) Imcms.start
  }
}