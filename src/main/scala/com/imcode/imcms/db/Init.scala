package com.imcode
package imcms.db

case class Init(version: Version, scripts: List[String]) {
  require(scripts.size > 0, "At least one script must be provided.")
}
