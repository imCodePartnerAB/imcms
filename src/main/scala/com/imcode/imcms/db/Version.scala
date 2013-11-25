package com.imcode
package imcms.db

case class Version(major: Int, minor: Int) extends Ordered[Version] {
  require(major > 0, s"'major' must be > 0 but was $major.")
  require(minor >= 0, s"'minor' must be >= 0 but was $minor.")

  def compare(that: Version) = this.major compareTo that.major match {
    case 0 => this.minor compareTo that.minor
    case i => i
  }

  override def toString: String = s"$major.$minor"
}

object Version {

  val VersionRe = """([1-9][0-9]*)\.([0-9]+)""".r

  implicit val stringToVersion: String => Version = {
    case VersionRe(major, minor) => Version(major.toInt, minor.toInt)
    case illegalArgument => throw new IllegalArgumentException(
      s"""|Provided value "$illegalArgument" can not be converted into a Version.
                                  |Expected value must be a string in a format "major.minor" where major and minor
                                  |are positive integers and major is greater than zero."""
        .stripMargin)
  }
}
