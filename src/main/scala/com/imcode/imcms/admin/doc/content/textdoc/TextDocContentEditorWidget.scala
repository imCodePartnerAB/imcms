package com.imcode.imcms.admin.doc.content.textdoc

import com.vaadin.ui._
import com.imcode.imcms.vaadin.component._

import scala.collection.JavaConverters._


class TextDocContentEditorWidget extends CustomComponent with FullSize {

  val vspContent = new VerticalSplitPanel

  val lstItems = new ListSelect(null, Seq("Texts", "Menus", "Content loops").asJavaCollection)
    with SingleSelect[String] with Immediate with NoNullSelection

  setCompositionRoot(vspContent)
}
