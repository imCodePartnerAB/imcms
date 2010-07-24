package com.imcode.imcms.db

import java.io.File
import xml.XML

case class Version(major: Int, minor: Int) extends Ordered[Version] {
  require(major > 0, "'major' must be > 0 but was %d." format major)
  require(minor >= 0, "'minor' must be >= 0 but was %d." format minor)

  def compare(that: Version) = this.major - that.major match {
    case 0 => this.minor - that.minor
    case i => i
  }
}

object Version {

  val VersionRe = """([1-9][0-9]*)\.([0-9]+)""".r

  implicit val stringToVersion: String => Version = {
    case VersionRe(major, minor) => Version(major.toInt, minor.toInt)
    case illegalArgument => throw new IllegalArgumentException(
                              """|Provided value "%s" can not be converted into a Version.
                                 |Expected value must be a string in a format 'major.minor' where major and minor
                                 |are positive integers and major is greater than zero."""
                                 .stripMargin format illegalArgument)
  }
}


case class Init(version: Version, scripts: List[String])


case class Diff(from: Version, to: Version, scripts: List[String]) {
  require(from < to, "'from' %s must be < 'to' %s." format (from, to))
}


case class Schema(version: Version, init: Init, diffs: Set[Diff], scriptsDir: String = "") {
  require(diffs.size == diffs.map(_.from).size, "'diffs' 'from' values must must be distinct: %s." format diffs)
  require(diffs map (diff => diffsChain(diff.from)) forall (chain => chain.last.to == version),
          "every diffs-chain's last 'diff.to' must be equal to 'version'.")

  def diffsChain(from: Version) = {
    def diffsChainIter(from: Version = from, diffsAcc: List[Diff] = List()): List[Diff] =
      diffs find (_.from == from) match {
        case Some(diff) => diffsChainIter(diff.to, diff::diffsAcc)
        case _ => diffsAcc.reverse
      }

    diffsChainIter()
  }

  def changeScriptsDir(newScriptsDir: String): Schema = copy(scriptsDir = newScriptsDir)
}

object Schema {

  implicit def xmlToSchema(xml: scala.xml.Elem) = {
    val version = (xml \ "@version").text
    val scriptsDir = (xml \ "@scripts-dir").text

    val initElem = xml \ "init"
    val initVersion = (initElem \ "@version").text
    val initScripts = initElem \ "script" map (_.text)

    val diffs = for {
      diffElem <- xml \ "diffs" \ "diff"

      diffFrom = (diffElem \ "@from").text
      diffTo = (diffElem \ "@to").text
      diffScripts = diffElem \ "script" map (_.text)

    } yield Diff(diffFrom, diffTo, diffScripts.toList)

    Schema(version, Init(initVersion, initScripts.toList), diffs.toSet, scriptsDir)
  }


  def load(file: File) = XML.loadFile(file) : Schema
}