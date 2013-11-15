package com.imcode
package imcms.test.config

import com.imcode._
import org.springframework.context.annotation._
import javax.inject.Inject
import org.springframework.core.env.Environment

@Configuration
@Import(Array(classOf[EnvironmentConfig]))
class TestConfig {

  @Inject
  var env: Environment = _

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
