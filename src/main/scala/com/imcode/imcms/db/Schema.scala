package com.imcode
package imcms.db

import java.net.URL

case class Schema(version: Version, init: Init, diffs: Set[Diff], scriptsDir: String = "") {
  require(diffs.size == diffs.map(_.from).size, s"'diffs' 'from' values must must be distinct: $diffs.")
  require(diffs.map(diff => diffsChain(diff.from)).forall(chain => chain.last.to == version),
    "every diffs-chain's last 'diff.to' must be equal to 'version'.")

  def diffsChain(from: Version): List[Diff] = diffs find (_.from == from) match {
    case Some(diff) => diff :: diffsChain(diff.to)
    case _ => Nil
  }

  def setScriptsDir(newScriptsDir: String): Schema = copy(scriptsDir = newScriptsDir)
}


object Schema {

  import java.io.File
  import xml.XML

  def apply(xml: scala.xml.Elem): Schema = {
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


  def load(file: File): Schema = XML.loadFile(file) |> apply

  def load(url: URL): Schema = XML.load(url) |> apply
}
