package com.imcode
package imcms.db

case class Diff(from: Version, to: Version, scripts: List[String]) {
  require(from < to, s"'from' $from must be < 'to' $to.")
  require(scripts.size > 0, "At least one script must be provided.")
}