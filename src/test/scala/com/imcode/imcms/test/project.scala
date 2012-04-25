package com.imcode
package imcms.test

import java.io.{FileReader, File}
import imcode.server.Imcms
import org.apache.commons.dbcp.BasicDataSource
import org.hibernate.SessionFactory
import org.hibernate.cfg.{Configuration}
import java.util.concurrent.atomic.AtomicReference
import org.springframework.transaction.annotation.EnableTransactionManagement
import org.springframework.core.env.Environment
import org.springframework.beans.factory.annotation.{Autowire, Autowired}
import org.springframework.orm.hibernate4.{HibernateExceptionTranslator, LocalSessionFactoryBuilder}
import javax.sql.DataSource
import org.springframework.context.annotation._
import org.slf4j.Logger
import org.springframework.beans.factory.config.{InstantiationAwareBeanPostProcessor, BeanPostProcessor}
import org.springframework.beans.PropertyValues
import java.beans.PropertyDescriptor
import java.lang.{Class, String}

object Project extends Project

class Project extends ProjectTestDB {

  val basedir: String = ClassLoader.getSystemResource("test-log4j.xml") match {
    case null => sys.error("Not configured")
    case url => new File(url.getFile).getParentFile.getParentFile.getParentFile.getCanonicalPath
  }

  System.setProperty("log4j.configuration", "test-log4j.xml")
  System.setProperty("solr.solr.home", path("src/main/solr"))

  val env = spring.ctx.getBean(classOf[Environment])

  object hibernate {
    type Configurator = Configuration => Configuration

    object configurators {
      val Production: Configurator = _.configure()
      val Dialect: Configurator = _.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQLInnoDBDialect")
      val Cache: Configurator =  _.setProperty("hibernate.cache.provider_class", "org.hibernate.cache.HashtableCacheProvider")
      val Hbm2ddlAutoCreateDrop: Configurator = _.setProperty("hibernate.hbm2ddl.auto", "create-drop")
      val Sql: Configurator =
        _.setProperty("hibernate.use_sql_comments", "true")
         .setProperty("hibernate.show_sql", "true")
         .setProperty("hibernate.format_sql", "true")

      val Basic: Configurator = Dialect andThen Cache andThen Sql
    }
  }

  object spring {
    val ctx = createCtx(classOf[ProjectConfig])

    def createCtx(annotatedClass: Class[_]) = new AnnotationConfigApplicationContext(annotatedClass)

    def createCtx(annotatedClass: Class[_], hibernateConfigurator: hibernate.Configurator) =
      new AnnotationConfigApplicationContext() |< { c =>
        c.getBeanFactory.addBeanPostProcessor(new BeanPostProcessor {
          def postProcessAfterInitialization(bean: AnyRef, beanName: String) = {
            if (bean.isInstanceOf[LocalSessionFactoryBuilder]) {
              hibernateConfigurator(bean.asInstanceOf[LocalSessionFactoryBuilder])
            }

            bean
          }

          def postProcessBeforeInitialization(bean: AnyRef, beanName: String)  = bean
        })

        c.register(annotatedClass)
        c.refresh()
      }
  }

  def path(relativePath: String) = new File(basedir, relativePath).getCanonicalPath

  def file(relativePath: String) = new File(basedir, relativePath)

  def fileFn(relativePath: String) = () => file(relativePath)

  def subDir(relativePath: String) = new File(basedir, relativePath)

  def subDirFn(relativePath: String) = () => subDir(relativePath)

  def initImcms(start: Boolean = false, prepareDBOnStart: Boolean = false) {
//    root.subDir("src/test/resources") |> { path =>
//      Imcms.setPath(path, path)
//    }
//
//    Imcms.setSQLScriptsPath(root.path("src/main/webapp/WEB-INF/sql"))
//    Imcms.setApplicationContext(spring.context())
//    Imcms.setPrepareDatabaseOnStart(prepareDBOnStart)
//
//    if (start) Imcms.start
  }
}


object DataSourceUrlType extends Enumeration {
  val WithDBName, WithoutDBName = Value
}

object DataSourceAutocommit extends Enumeration {
  val Yes, No = Value
}


trait ProjectTestDB { project: Project =>

  object testDB {

    import com.imcode.imcms.db.{DB, Schema}

    def createDataSource(urlType: DataSourceUrlType.Value = DataSourceUrlType.WithDBName,
                         autocommit: DataSourceAutocommit.Value = DataSourceAutocommit.No) =

      project.spring.ctx.getBean(classOf[BasicDataSource]) |< { ds =>

        ds.setUrl(project.env.getRequiredProperty(
          if (urlType == DataSourceUrlType.WithDBName) "JdbcUrl" else "JdbcUrlWithoutDBName"))

        ds.setDefaultAutoCommit(autocommit == DataSourceAutocommit.Yes)
      }


    def recreate() {
      project.env.getRequiredProperty("DBName") |> { dbName =>
        new DB(createDataSource(DataSourceUrlType.WithoutDBName)) |> { db =>
          db.template.update("DROP DATABASE IF EXISTS %s" format dbName)
          db.template.update("CREATE DATABASE %s" format dbName)
        }
      }
    }


    def runScripts(script: String, scripts: String*) {
      new DB(createDataSource(autocommit=DataSourceAutocommit.Yes)) |> { db =>
        db.runScripts(script +: scripts map project.path)
      }
    }


    def prepare(recreateBeforePrepare: Boolean = false) {
      if (recreateBeforePrepare) recreate()

      val scriptsDir = project.path("src/main/web/WEB-INF/sql")
      val schema = Schema.load(project.file("src/main/resources/schema.xml")).changeScriptsDir(scriptsDir)

      new DB(createDataSource()).prepare(schema)
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

    def hibernateProperties = Map()
  }
}

object FileWatcher {

  case class State[T](lastAccessNano: Long, lastModified: Long, handlerResult: T)

  def create[T](fileFn: () => File, poolIntervalNano: Long = 1000)(handler: File => T) = new Function0[T] {
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

@org.springframework.context.annotation.Configuration
@PropertySource(Array("classpath:test-server.properties"))
class ProjectConfig {

  @Autowired
  var env: Environment = _

  @Scope("prototype")
  @Bean(destroyMethod = "close")
  def dataSource = new org.apache.commons.dbcp.BasicDataSource |< { ds =>
    ds.setDriverClassName(env.getRequiredProperty("JdbcDriver"))
    ds.setUsername(env.getRequiredProperty("Username"))
    ds.setPassword(env.getRequiredProperty("Password"))
    ds.setTestOnBorrow(true)
    ds.setValidationQuery("select 1")
    ds.setMaxActive(1)
  }
}


@org.springframework.context.annotation.Configuration
@PropertySource(Array("classpath:test-server.properties"))
class BasicConfig {

  @Autowired
  var env: Environment = _

  @Bean(destroyMethod = "close")
  def dataSource = new org.apache.commons.dbcp.BasicDataSource |< { ds =>
    ds.setDriverClassName(env.getRequiredProperty("JdbcDriver"))
    ds.setUrl(env.getRequiredProperty("JdbcUrl"))
    ds.setUsername(env.getRequiredProperty("Username"))
    ds.setPassword(env.getRequiredProperty("Password"))
    ds.setTestOnBorrow(true)
    ds.setValidationQuery("select 1")
    ds.setMaxActive(1)
  }
}


@org.springframework.context.annotation.Configuration
@EnableTransactionManagement(mode = AdviceMode.PROXY, proxyTargetClass = true)
@Import(Array(classOf[BasicConfig]))
class HibernateConfig {

  @Autowired
  private var dataSource: DataSource = _

  @Bean(autowire = Autowire.BY_TYPE)
  def txManager = new org.springframework.orm.hibernate4.HibernateTransactionManager

  @Bean(autowire = Autowire.BY_TYPE)
  def sessionFactory = sessionFactoryBuilder.buildSessionFactory()

  @Bean
  def sessionFactoryBuilder = new LocalSessionFactoryBuilder(dataSource)

  @Bean
  def exTranslator = new HibernateExceptionTranslator
}