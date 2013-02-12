package com.imcode
package imcms.test.config

import com.imcode._
import org.springframework.transaction.annotation.EnableTransactionManagement
import org.springframework.context.annotation._
import javax.sql.DataSource
import org.springframework.beans.factory.annotation.Autowire
import org.springframework.orm.hibernate4.{HibernateExceptionTranslator, LocalSessionFactoryBuilder}
import javax.inject.Inject


@Configuration
@EnableTransactionManagement(mode = AdviceMode.PROXY, proxyTargetClass = true)
@Import(Array(classOf[BasicConfig]))
class AbstractHibernateConfig {

  @Inject
  var dataSource: DataSource = _

  @Inject
  var hibernatePropertiesConfigurator: (org.hibernate.cfg.Configuration => org.hibernate.cfg.Configuration) = _

  @Bean(autowire = Autowire.BY_TYPE)
  def txManager = new org.springframework.orm.hibernate4.HibernateTransactionManager

  @Bean
  def exTranslator = new HibernateExceptionTranslator

  @Bean
  def sessionFactory = new LocalSessionFactoryBuilder(dataSource) |> hibernatePropertiesConfigurator |> {
    _.buildSessionFactory()
  }
}