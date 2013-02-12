package com.imcode
package imcms.test.config

import com.imcode._
import org.springframework.context.annotation._
import javax.inject.Inject
import org.springframework.core.env.{StandardEnvironment, Environment}

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
