package com.imcode
package imcms.test.config

import com.imcode._
import org.springframework.transaction.annotation.EnableTransactionManagement
import org.springframework.context.annotation._
import javax.sql.DataSource
import org.springframework.beans.factory.annotation.{Autowire, Autowired}
import org.springframework.orm.hibernate4.{HibernateExceptionTranslator, LocalSessionFactoryBuilder}
import javax.inject.Inject
import org.springframework.core.env.{StandardEnvironment, Environment}


@Configuration
@PropertySource(Array("classpath:test-server.properties"))
class ProjectConfig {

  var env: Environment = _

  @Inject
  def setEnv(env: StandardEnvironment) {
    env.getPropertySources.remove(StandardEnvironment.SYSTEM_ENVIRONMENT_PROPERTY_SOURCE_NAME)
    this.env = env
  }

  @Scope("prototype")
  @Bean(destroyMethod = "close")
  def dataSource = new org.apache.commons.dbcp.BasicDataSource |>> { ds =>
    ds.setDriverClassName(env.getRequiredProperty("JdbcDriver"))
    ds.setUsername(env.getRequiredProperty("User"))
    ds.setPassword(env.getRequiredProperty("Password"))
    ds.setTestOnBorrow(true)
    ds.setValidationQuery("select 1")
    ds.setMaxActive(1)
  }
}


@Configuration
@PropertySource(Array("classpath:test-server.properties"))
class BasicConfig {

  var env: Environment = _

  @Inject
  def setEnv(env: StandardEnvironment) {
    env.getPropertySources.remove(StandardEnvironment.SYSTEM_ENVIRONMENT_PROPERTY_SOURCE_NAME)
    this.env = env
  }

  @Bean(destroyMethod = "close")
  def dataSource = new org.apache.commons.dbcp.BasicDataSource |>> { ds =>
    ds.setDriverClassName(env.getRequiredProperty("JdbcDriver"))
    ds.setUrl(env.getRequiredProperty("JdbcUrl"))
    ds.setUsername(env.getRequiredProperty("User"))
    ds.setPassword(env.getRequiredProperty("Password"))
    ds.setTestOnBorrow(true)
    ds.setValidationQuery("select 1")
    ds.setMaxActive(1)
  }
}


@Configuration
@EnableTransactionManagement(mode = AdviceMode.PROXY, proxyTargetClass = true)
@Import(Array(classOf[BasicConfig]))
class AbstractHibernateConfig {

  @Inject
  var dataSource: DataSource = _

  @Inject
  var hibernatePropertiesConfigurator: org.hibernate.cfg.Configuration => org.hibernate.cfg.Configuration = _

  @Bean(autowire = Autowire.BY_TYPE)
  def txManager = new org.springframework.orm.hibernate4.HibernateTransactionManager

  @Bean
  def exTranslator = new HibernateExceptionTranslator

  @Bean
  def sessionFactory = new LocalSessionFactoryBuilder(dataSource) |> hibernatePropertiesConfigurator |> {
    _.buildSessionFactory()
  }
}