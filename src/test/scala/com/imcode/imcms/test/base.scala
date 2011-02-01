package com.imcode
package imcms.test

import java.util.concurrent.atomic.{AtomicReference}
import java.util.Properties
import java.io.{FileReader, File}
import org.springframework.context.support.FileSystemXmlApplicationContext
import org.springframework.context.ApplicationContext
import imcode.server.Imcms
import org.apache.commons.dbcp.BasicDataSource
import org.hibernate.cfg.AnnotationConfiguration
import org.hibernate.SessionFactory

object Base {
  val project = new Project
  val db = new DB(project)
}

class Project(dirPath: String = ".") {

  private val dirRef = new AtomicReference[File]

  private val buildPropertiesFileWatcher = Util.createFileWatcher(fileFn("build.properties")) { file =>
    using(new FileReader(file)) { reader =>
      letret(new Properties) { _ load reader }
    }
  }

 private val testPropertiesFileWatcher = Util.createFileWatcher(fileFn("src/test/resources/server.properties")) { file =>
    using(new FileReader(file)) { reader =>
      letret(new Properties) { _ load reader }
    }
  }

  private val springAppContextRef = new AtomicReference(Option.empty[ApplicationContext])

  val buildProperties = buildPropertiesFileWatcher()

  val testProperties = testPropertiesFileWatcher()

  val buildProperty = buildProperties.getProperty(_:String)

  val testProperty = testProperties.getProperty(_:String)


  System.setProperty("log4j.configuration", "file:" + path("src/test/resources/log4j.xml"))

  cd(dirPath)


  def dir() = dirRef.get

  def cd(newDirPath: String) = dirRef.set(new File(newDirPath).getCanonicalFile)

  def path(relativePath: String) = new File(dir, relativePath).getCanonicalPath

  def file(relativePath: String) = new File(dir, relativePath)

  def fileFn(relativePath: String) = () => file(relativePath)

  def subDir(relativePath: String) = new File(dir, relativePath)

  def subDirFn(relativePath: String) = () => subDir(relativePath)

  def springAppContext(reload: Boolean = false) = synchronized {
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

  def loc = error("not implemented") // "java|jsp|htm|html|xml|properties|sql|clj|scala"
}


class DB(project: Project) {

  import com.imcode.imcms.db.{DB => DBAccess, Schema}

  def createDataSource(withDBName: Boolean = true, autocommit: Boolean = false) =
    letret(new BasicDataSource) { ds =>
      ds.setUsername(project.testProperty("User"))
      ds.setPassword(project.testProperty("Password"))
      ds.setDriverClassName(project.testProperty("JdbcDriver"))
      ds.setUrl(if (withDBName) project.testProperty("JdbcUrl")
                else project.testProperty("__JdbcUrlWithoutDBName__"))

      ds.setDefaultAutoCommit(autocommit)
    }

  def recreate() {
    let(new DBAccess(createDataSource(withDBName=false))) { access =>
      access.template.update("DROP DATABASE IF EXISTS %s" format project.testProperty("__DBName__"))
      access.template.update("CREATE DATABASE %s" format project.testProperty("__DBName__"))
    }
  }


  def createHibernateSessionFactory(annotatedClasses: Class[_]*): SessionFactory =
    createHibernateSessionFactory(annotatedClasses.toSeq)

  def createHibernateSessionFactory(annotatedClasses: Seq[Class[_]], xmlFiles: String*) =
    let(new AnnotationConfiguration) { c =>
      for ((name, value) <- hibernateProperties) c.setProperty(name, value)
      annotatedClasses foreach { c addAnnotatedClass _}
      xmlFiles foreach { c addFile _ }

      c.buildSessionFactory
    }

  def hibernateProperties = Map(
    "hibernate.dialect" -> "org.hibernate.dialect.MySQLInnoDBDialect",
    "hibernate.connection.driver_class" -> project.testProperty("JdbcDriver"),
    "hibernate.connection.url" -> project.testProperty("JdbcUrl"),
    "hibernate.connection.username" -> project.testProperty("User"),
    "hibernate.connection.password" -> project.testProperty("Password"),
    "hibernate.connection.pool_size" -> "1",
    "hibernate.connection.autocommit" -> "true",
    "hibernate.cache.provider_class" -> "org.hibernate.cache.HashtableCacheProvider",
    "hibernate.hbm2ddl.auto" -> "create-drop",
    "hibernate.show_sql" -> "true"
  )

  def runScripts(script: String, scripts: String*) {
    let(new DBAccess(createDataSource(autocommit=true))) { access =>
      access.runScripts(script +: scripts map { project path _ })
    }
  }

  def prepare(recreateBeforePrepare: Boolean = false) {
    if (recreateBeforePrepare) recreate()

    val scriptsDir = project.path("src/main/webapp/WEB-INF/sql")
    val schema = Schema.load(project.file("src/main/resources/schema.xml")).changeScriptsDir(scriptsDir)

    let(new DBAccess(createDataSource())) { _ prepare schema }
  }
}

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