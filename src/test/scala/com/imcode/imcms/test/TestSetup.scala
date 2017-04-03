//package com.imcode
//package imcms.test
//
//import java.io.File
//
//import _root_.imcode.server.Imcms
//import imcode.server.document.index.service.SolrServerFactory
//import org.apache.commons.dbcp2.BasicDataSource
//import org.apache.commons.io.FileUtils
//import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer
//import org.springframework.context.ApplicationContext
//import org.springframework.context.annotation._
//import org.springframework.context.support.FileSystemXmlApplicationContext
//import org.springframework.core.env.Environment
//
//import scala.collection.JavaConverters._
//import scala.reflect.ClassTag
//
//object TestSetup extends TestSetup
//
//class TestSetup extends TestDb with TestSolr {
//
//  val classLoader = Thread.currentThread.getContextClassLoader
//
//  val basedir: String = classLoader.getResource("log4j.xml") match {
//    case null => sys.error("Not configured")
//    case testClassesLog4jConfUrl => new File(testClassesLog4jConfUrl.getFile).getParentFile |> {
//      testClassesDir =>
//        testClassesDir.getParentFile.getParentFile.getCanonicalPath
//    }
//  }
//
//  // ???
//  // System.setProperty("solr.solr.home", path("src/main/solr"))
//
//  val env: Environment = spring.ctx.getBean(classOf[Environment])
//
//  def path(relativePath: String) = new File(basedir, relativePath).getCanonicalPath
//
//  def file(relativePath: String) = new File(basedir, relativePath)
//
//  def dir(relativePath: String) = new File(basedir, relativePath)
//
//  object spring {
//    val ctx = createCtx(classOf[TestConfig])
//
//    def createCtx(annotatedClass: Class[_]) = new AnnotationConfigApplicationContext(annotatedClass)
//  }
//
//  object imcms {
//    def init(start: Boolean = false, prepareDbOnStart: Boolean = false) {
//
//      Imcms.setSQLScriptsPath(path("src/main/webapp/WEB-INF/sql"))
//      System.setProperty("com.imcode.imcms.test.basedir", basedir)
//      // Can not be replaced with @Configuration since XML configuration always takes precedence over annotation configuration.
//      Imcms.setApplicationContext(new FileSystemXmlApplicationContext("file:" + path("src/test/resources/applicationContext.xml")))
//      Imcms.setPrepareDatabaseOnStart(prepareDbOnStart)
//
//      if (start) Imcms.start()
//    }
//  }
//}
//
//
//object DataSourceUrlType extends Enumeration {
//  val WithDBName, WithoutDBName = Value
//}
//
//object DataSourceAutocommit extends Enumeration {
//  val Yes, No = Value
//}
//
//
//trait TestDb {
//  test: TestSetup =>
//
//  object db {
//
//    import com.imcode.imcms.db.{DB, Schema}
//
//    def runScripts(script: String, scripts: String*) {
//      new DB(createDataSource(autocommit = DataSourceAutocommit.Yes)) |> {
//        db =>
//          db.runScripts((script +: scripts).map(test.path).asJava)
//      }
//    }
//
//    def prepare(recreateBeforePrepare: Boolean = false) {
//      if (recreateBeforePrepare) recreate()
//
//      val scriptsDir = test.path("src/main/webapp/WEB-INF/sql")
//      val schema = Schema.fromFile(test.file("src/main/resources/schema.xml")).setScriptsDir(scriptsDir)
//
//      new DB(createDataSource()).prepare(schema)
//    }
//
//    def createDataSource(urlType: DataSourceUrlType.Value = DataSourceUrlType.WithDBName,
//                         autocommit: DataSourceAutocommit.Value = DataSourceAutocommit.No): BasicDataSource = {
//
//      test.spring.ctx.getBean(classOf[BasicDataSource]) |>> {
//        ds =>
//          ds.setUrl(test.env.getRequiredProperty(
//            if (urlType == DataSourceUrlType.WithDBName) "JdbcUrl" else "JdbcUrlWithoutDBName"))
//
//          ds.setDefaultAutoCommit(autocommit == DataSourceAutocommit.Yes)
//      }
//    }
//
//    def recreate() {
//      test.env.getRequiredProperty("DBName") |> {
//        dbName =>
//          new DB(createDataSource(DataSourceUrlType.WithoutDBName)) |> {
//            db =>
//              db.getJdbcTemplate.update(s"DROP DATABASE IF EXISTS $dbName")
//              db.getJdbcTemplate.update(s"CREATE DATABASE $dbName")
//          }
//      }
//    }
//  }
//
//}
//
//
//trait TestSolr {
//  test: TestSetup =>
//
//  object solr {
//    val home: String = test.path("target/test-classes/WEB-INF/solr")
//    val homeDir: File = new File(home)
//    val homeTemplateDir: File = test.dir("src/main/webapp/WEB-INF/solr").ensuring(_.isDirectory, "SOLr home template exists.")
//
//    def deleteCoreDataDir() {
//      new File(home, "core/data") |> {
//        dir =>
//          if (dir.exists() && !dir.delete()) sys.error("Unable to delete SOLr data dir %s.".format(dir))
//      }
//    }
//
//    def createEmbeddedServer(recreateHome: Boolean = false): EmbeddedSolrServer = {
//      if (recreateHome) {
//        this.recreateHome()
//      }
//
//      SolrServerFactory.createEmbeddedSolrServer(home)
//    }
//
//    def recreateHome() {
//      if (homeDir.exists()) {
//        FileUtils.deleteDirectory(homeDir)
//
//        if (homeDir.exists()) sys.error("Unable to delete SOLr home directory.")
//      }
//
//      FileUtils.copyDirectory(homeTemplateDir, homeDir)
//    }
//  }
//
//}
//
//
//object SpringUtils {
//  def bean[A: ClassTag](ctx: ApplicationContext): A = ctx.getBean(scala.reflect.classTag[A].runtimeClass.asInstanceOf[Class[A]])
//}