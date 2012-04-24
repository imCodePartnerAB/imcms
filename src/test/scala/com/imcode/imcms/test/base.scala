package com.imcode
package imcms.test

import java.util.Properties
import java.io.{FileReader, File}
import org.springframework.context.support.FileSystemXmlApplicationContext
import org.springframework.context.ApplicationContext
import imcode.server.Imcms
import org.apache.commons.dbcp.BasicDataSource
import org.hibernate.SessionFactory
import org.hibernate.cfg.{Configuration}
import org.hibernate.service.ServiceRegistryBuilder
import java.util.concurrent.atomic.AtomicReference

object Project extends Project(".")

class Project(dirPath: String = ".") {

  root.cd(dirPath)

  val db = new DB(this)

  object root {
    private val dirRef = new AtomicReference[File]

    def dir() = dirRef.get

    def path() = dir().getCanonicalPath

    def cd(newDirPath: String) {
      dirRef.set(new File(newDirPath).getCanonicalFile)

      System.setProperty("log4j.configuration", "file:" + root.path("src/test/resources/log4j.xml"))
      System.setProperty("solr.solr.home", root.path("src/main/solr"))
    }

    def path(relativePath: String) = new File(dir(), relativePath).getCanonicalPath

    def file(relativePath: String) = new File(dir(), relativePath)

    def fileFn(relativePath: String) = () => file(relativePath)

    def subDir(relativePath: String) = new File(dir(), relativePath)

    def subDirFn(relativePath: String) = () => subDir(relativePath)
  }


  object properties {
    private val readBuildProperties = Util.createFileWatcher(root.fileFn("build.properties")) { file =>
      using(new FileReader(file)) { reader =>
        new Properties |< { _ load reader }
      }
    }

    private val readTestProperties = Util.createFileWatcher(root.fileFn("src/test/resources/server.properties")) { file =>
      using(new FileReader(file)) { reader =>
        new Properties |< { _ load reader }
      }
    }

    def build = readBuildProperties()

    def test = readTestProperties()

    def build(name: String, defaultValue: String = null): String = build.getProperty(name, defaultValue)

    def test(name: String, defaultValue: String = null): String = test.getProperty(name, defaultValue)
  }


  object spring {
    private val appContextRef = new AtomicReference(Option.empty[ApplicationContext])

    def context(reload: Boolean = false) = synchronized {
      System.setProperty("com.imcode.imcms.project.dir", root.path("."))

      if (appContextRef.get.isEmpty || reload) {
        appContextRef.set(Some(new FileSystemXmlApplicationContext(
          "file:" + root.file("src/test/resources/applicationContextTest.xml").getCanonicalPath)))
      }

      appContextRef.get.get
    }
  }


  def initImcms(start: Boolean = false, prepareDBOnStart: Boolean = false) {
    root.subDir("src/test/resources") |> { path =>
      Imcms.setPath(path, path)
    }

    Imcms.setSQLScriptsPath(root.path("src/main/webapp/WEB-INF/sql"))
    Imcms.setApplicationContext(spring.context())
    Imcms.setPrepareDatabaseOnStart(prepareDBOnStart)

    if (start) Imcms.start
  }

  def loc = sys.error("not implemented") // "java|jsp|htm|html|xml|properties|sql|clj|scala"
}


class DB(project: Project) {

  import com.imcode.imcms.db.{DB => DBAccess, Schema}

  def createDataSource(withDBName: Boolean = true, autocommit: Boolean = false) =
    new BasicDataSource |< { ds =>
      ds.setUsername(project.properties.test("User"))
      ds.setPassword(project.properties.test("Password"))
      ds.setDriverClassName(project.properties.test("JdbcDriver"))
      ds.setUrl(if (withDBName) project.properties.test("JdbcUrl")
                else project.properties.test("JdbcUrlWithoutDBName"))

      ds.setDefaultAutoCommit(autocommit)
    }

  def recreate() {
    project.properties.test("DBName") |> { dbName =>
      new DBAccess(createDataSource(withDBName=false)) |> { access =>
        access.template.update("DROP DATABASE IF EXISTS %s" format dbName)
        access.template.update("CREATE DATABASE %s" format dbName)
      }
    }
  }


  def createHibernateSessionFactory(annotatedClasses: Class[_]*): SessionFactory =
    createHibernateSessionFactory(annotatedClasses.toSeq)

  def createHibernateSessionFactory(annotatedClasses: Seq[Class[_]], xmlFiles: String*) =
    new Configuration |> { c =>
      for ((name, value) <- hibernateProperties) c.setProperty(name, value)
      annotatedClasses foreach { c addAnnotatedClass _}
      xmlFiles foreach { c addFile _ }

      //new ServiceRegistryBuilder().applySettings(configuration.getProperties()).buildServiceRegistry()

      c.buildSessionFactory
    }

  def hibernateProperties = Map(
    "hibernate.dialect" -> "org.hibernate.dialect.MySQLInnoDBDialect",
    "hibernate.connection.driver_class" -> project.properties.test("JdbcDriver"),
    "hibernate.connection.url" -> project.properties.test("JdbcUrl"),
    "hibernate.connection.username" -> project.properties.test("User"),
    "hibernate.connection.password" -> project.properties.test("Password"),
    "hibernate.connection.pool_size" -> "1",
    "hibernate.connection.autocommit" -> "true",
    "hibernate.cache.provider_class" -> "org.hibernate.cache.HashtableCacheProvider",
    "hibernate.hbm2ddl.auto" -> "create-drop",
    "hibernate.show_sql" -> "true",
    "hibernate.current_session_context_class" -> "thread"
  )

  def runScripts(script: String, scripts: String*) {
    new DBAccess(createDataSource(autocommit=true)) |> { access =>
      access.runScripts(script +: scripts map project.root.path)
    }
  }

  def prepare(recreateBeforePrepare: Boolean = false) {
    if (recreateBeforePrepare) recreate()

    val scriptsDir = project.root.path("src/main/web/WEB-INF/sql")
    val schema = Schema.load(project.root.file("src/main/resources/schema.xml")).changeScriptsDir(scriptsDir)

    new DBAccess(createDataSource()) |> { _ prepare schema }
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

        case _ => fileFn() |> { file =>
          State(now, file.lastModified, handler(file))
        }
      }

      stateRef.set(Some(state))

      state.handlerResult
    }
  }
}