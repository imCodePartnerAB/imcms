package com.imcode
package imcms.test

import java.io.{File}
import org.apache.commons.dbcp.BasicDataSource
import org.hibernate.cfg.{Configuration}
import org.springframework.core.env.Environment
import org.springframework.context.annotation._
import java.lang.{Class, String}
import org.springframework.context.ApplicationContext
import com.imcode.imcms.test.config.{ProjectConfig}
import org.springframework.context.support.{FileSystemXmlApplicationContext}
import org.apache.commons.io.FileUtils
import org.apache.solr.client.solrj.SolrServer
import imcode.server.{Config, Imcms}
import imcode.server.document.index.{IndexService, SolrServerShutdown}

object Test extends Test

class Test extends TestDb with TestSolr {

  val basedir: String = ClassLoader.getSystemResource("test-log4j.xml") match {
    case null => sys.error("Not configured")
    case url => new File(url.getFile).getParentFile.getParentFile.getParentFile.getCanonicalPath
  }

  System.setProperty("com.imcode.imcms.test.basedir", basedir)
  System.setProperty("log4j.configuration", "test-log4j.xml")
  //System.setProperty("solr.solr.home", path("src/main/solr"))

  val env: Environment = spring.ctx.getBean(classOf[Environment])

  object spring {
    val ctx = createCtx(classOf[ProjectConfig])

    def createCtx(annotatedClass: Class[_]) = new AnnotationConfigApplicationContext(annotatedClass)
  }

  object hibernate {
    type Configurator = Configuration => Configuration

    object configurators {
      val Dialect: Configurator = _.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQLInnoDBDialect")
      val Cache: Configurator =  _.setProperty("hibernate.cache.provider_class", "org.hibernate.cache.HashtableCacheProvider")
      val Hbm2ddlAutoCreateDrop: Configurator = _.setProperty("hibernate.hbm2ddl.auto", "create-drop")
      val Production: Configurator = _.configure()
      val Sql: Configurator =
        _.setProperty("hibernate.use_sql_comments", "true")
         .setProperty("hibernate.show_sql", "true")
         .setProperty("hibernate.format_sql", "true")

      val Basic: Configurator = Dialect andThen Cache
      val BasicWithSql: Configurator = Basic andThen Sql

      def addAnnotatedClasses(annotatedClasses: Class[_]*)(configuration: Configuration) = configuration |>> { _ =>
        annotatedClasses foreach configuration.addAnnotatedClass
      }

      def addXmlFiles(xmlFiles: String*)(configuration: Configuration) = configuration |>> { _ =>
        xmlFiles foreach { xmlFile => ClassLoader.getSystemResource(xmlFile).getFile |> configuration.addFile }
      }
    }
  }


  object imcms {
    def init(start: Boolean = false, prepareDbOnStart: Boolean = false) {
      dir("src/test/resources") |> { path =>
        Imcms.setPath(path, path)
      }

      Imcms.setSQLScriptsPath(path("src/main/web/WEB-INF/sql"))
      Imcms.setApplicationContext(new FileSystemXmlApplicationContext("file:" + path("src/test/resources/test-applicationContext.xml")))
      Imcms.setPrepareDatabaseOnStart(prepareDbOnStart)

      if (start) Imcms.start
    }
  }


  def path(relativePath: String) = new File(basedir, relativePath).getCanonicalPath

  def file(relativePath: String) = new File(basedir, relativePath)

  def dir(relativePath: String) = new File(basedir, relativePath)

  def nop() {}
}


object DataSourceUrlType extends Enumeration {
  val WithDBName, WithoutDBName = Value
}

object DataSourceAutocommit extends Enumeration {
  val Yes, No = Value
}


trait TestDb { test: Test =>

  object db {

    import com.imcode.imcms.db.{DB, Schema}

    def createDataSource(urlType: DataSourceUrlType.Value = DataSourceUrlType.WithDBName,
                         autocommit: DataSourceAutocommit.Value = DataSourceAutocommit.No) =

      test.spring.ctx.getBean(classOf[BasicDataSource]) |>> { ds =>

        ds.setUrl(test.env.getRequiredProperty(
          if (urlType == DataSourceUrlType.WithDBName) "JdbcUrl" else "JdbcUrlWithoutDBName"))

        ds.setDefaultAutoCommit(autocommit == DataSourceAutocommit.Yes)
      }


    def recreate() {
      test.env.getRequiredProperty("DBName") |> { dbName =>
        new DB(createDataSource(DataSourceUrlType.WithoutDBName)) |> { db =>
          db.template.update("DROP DATABASE IF EXISTS %s" format dbName)
          db.template.update("CREATE DATABASE %s" format dbName)
        }
      }
    }


    def runScripts(script: String, scripts: String*) {
      new DB(createDataSource(autocommit=DataSourceAutocommit.Yes)) |> { db =>
        db.runScripts(script +: scripts map test.path)
      }
    }


    def prepare(recreateBeforePrepare: Boolean = false) {
      if (recreateBeforePrepare) recreate()

      val scriptsDir = test.path("src/main/web/WEB-INF/sql")
      val schema = Schema.load(test.file("src/main/resources/schema.xml")).changeScriptsDir(scriptsDir)

      new DB(createDataSource()).prepare(schema)
    }
  }
}


trait TestSolr { test: Test =>

  object solr {
    val homeTemplate: File = test.dir("src/main/web/WEB-INF/solr").ensuring(_.isDirectory, "SOLr home template exists.")
    val home: File = test.dir("target/test/solr")

    def recreateHome() {
      if (home.exists()) {
        FileUtils.deleteDirectory(home)

        if (home.exists()) sys.error("Unable to delete SOLr home directory.")
      }

      FileUtils.copyDirectory(homeTemplate, home)
    }

    def deleteCoreDataDir() {
      new File(home, "imcms/data") |> { dir =>
        if (dir.exists() && !dir.delete()) sys.error("Unable to delete SOLr data  %s.".format(dir))
      }
    }

    def createEmbeddedServer(): SolrServer with SolrServerShutdown = {
      val config = new Config() |>> { c =>
        c.setSolrHome(home)
      }

      new IndexService(config).solrServer
    }
  }
}


object SpringUtils {
  def bean[A:ClassManifest](ctx: ApplicationContext): A = ctx.getBean(classManifest[A].erasure.asInstanceOf[Class[A]])
}