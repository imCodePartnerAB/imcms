package com.imcode
package imcms.test.config

import org.springframework.context.annotation._
import javax.inject.Inject
import org.springframework.core.env.{StandardEnvironment, Environment}

@Configuration
@PropertySource(value = Array("classpath:server.properties"))
class EnvironmentConfig {

  var env: Environment = _

  @Inject
  def setEnv(env: StandardEnvironment) {
    env.getPropertySources.remove(StandardEnvironment.SYSTEM_ENVIRONMENT_PROPERTY_SOURCE_NAME)
    this.env = env
  }
}
