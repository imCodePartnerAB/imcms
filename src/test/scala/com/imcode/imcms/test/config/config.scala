package com.imcode
package imcms.test.config

import org.springframework.core.env.Environment
import org.springframework.transaction.annotation.EnableTransactionManagement
import org.springframework.context.annotation._
import javax.sql.DataSource
import org.springframework.beans.factory.annotation.{Autowire, Autowired}
import org.springframework.orm.hibernate4.{HibernateExceptionTranslator, LocalSessionFactoryBuilder}


@Configuration
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


@Configuration
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


@Configuration
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