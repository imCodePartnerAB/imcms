package com.imcode
package imcms.test.config

import com.imcode._
import org.apache.tomcat.dbcp.dbcp.BasicDataSource
import org.springframework.transaction.annotation.EnableTransactionManagement
import org.springframework.context.annotation._
import org.springframework.beans.factory.annotation.Autowire
import org.springframework.orm.hibernate4.{HibernateExceptionTranslator, LocalSessionFactoryBuilder}
import javax.inject.Inject
import org.springframework.core.env.Environment


@Configuration
@EnableTransactionManagement(mode = AdviceMode.PROXY, proxyTargetClass = true)
@Import(Array(classOf[EnvironmentConfig]))
class HibernateConfig {

  @Inject
  var hibernatePropertiesConfigurator: (org.hibernate.cfg.Configuration => org.hibernate.cfg.Configuration) = _

  @Inject
  var env: Environment = _

  @Bean(destroyMethod = "close")
  def dataSource = new BasicDataSource |>> { ds =>
    ds.setDriverClassName(env.getRequiredProperty("JdbcDriver"))
    ds.setUrl(env.getRequiredProperty("JdbcUrl"))
    ds.setUsername(env.getRequiredProperty("User"))
    ds.setPassword(env.getRequiredProperty("Password"))
    ds.setTestOnBorrow(true)
    ds.setValidationQuery("select 1")
    ds.setMaxActive(1)
  }

  @Bean(autowire = Autowire.BY_TYPE)
  def txManager = new org.springframework.orm.hibernate4.HibernateTransactionManager

  @Bean
  def exTranslator = new HibernateExceptionTranslator

  @Bean
  def sessionFactory = new LocalSessionFactoryBuilder(dataSource) |> hibernatePropertiesConfigurator |> {
    _.buildSessionFactory()
  }
}