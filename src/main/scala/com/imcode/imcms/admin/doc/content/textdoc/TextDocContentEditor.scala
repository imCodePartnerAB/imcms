package com.imcode
package imcms.admin.doc.content.textdoc

import _root_.imcode.server.document.textdocument.{TextDomainObject, TextDocumentDomainObject}
import com.imcode.imcms.admin.doc.content.DocContentEditor

import com.imcode.imcms.vaadin.Editor
import com.imcode.imcms.vaadin.data._
import com.imcode.imcms.vaadin.event._
import com.imcode.imcms.vaadin.component._
import com.vaadin.ui._

import scala.collection.mutable.{Map => MMap}

class TextDocContentEditor(doc: TextDocumentDomainObject) extends DocContentEditor {
  override type Data = TextDocumentDomainObject

  override val view = new TextDocContentEditorView |>> { w =>
    w.lstItems.addValueChangeHandler { _ =>

    }
  }

  resetValues()

  override def resetValues() {
    view.lstItems.selection = view.lstItems.firstItemIdOpt.get
  }

  override def collectValues() = Right(doc)
}



class TextsEditor(texts: Map[Int, TextDomainObject]) extends Editor {

  override type Data = Map[Int, TextDomainObject]

  private var state: Map[Int, TextDomainObject] = _

  override val view = new TextsEditorView |>> { w =>

  }

  resetValues()

  override def resetValues() {
    state = texts

    view.tblTexts.removeAllItems()

    for ((no, text) <- state) {
//      view.tblTexts.addRowWithAutoId(
//        no: JInteger,
//        text.getType.toString,
//        text.getLoopItemRef.asOption.map(_.getLoopNo).get: JInteger,
//        text.getLoopItemRef.asOption.map(_.getEntryNo).get: JInteger,
//        text.getText
//      )
    }
  }

  override def collectValues(): ErrorsOrData = state.map { case (id, text) => id -> text.clone() } |> Right.apply
}


class TextsEditorView extends VerticalLayout with Spacing with Margin with FullSize {
  val mb = new MenuBar
  val miNew = mb.addItem("New")
  val miEdit = mb.addItem("Edit")
  val miDelete = mb.addItem("Delete")
  val miHelp = mb.addItem("Help")
  val tblTexts = new Table with MultiSelect[JInteger] with Immediate with FullSize

  addContainerProperties(tblTexts,
    PropertyDescriptor[JInteger]("no"),
    PropertyDescriptor[String]("type"),
    PropertyDescriptor[JInteger]("content loop no"),
    PropertyDescriptor[JInteger]("content no"),
    PropertyDescriptor[String]("text")
  )

  addComponents(mb, tblTexts)
  setExpandRatio(tblTexts, 1.0f)
}
