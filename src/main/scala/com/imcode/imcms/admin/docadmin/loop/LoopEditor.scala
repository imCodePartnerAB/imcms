package com.imcode
package imcms
package admin.docadmin.loop

import _root_.java.util.function.BiConsumer

import com.imcode.imcms.api.Loop
import com.imcode.imcms.mapping.TextDocumentContentLoader
import com.imcode.imcms.mapping.container.{LoopEntryRef, DocRef, TextDocLoopContainer}
import com.imcode.imcms.vaadin.Editor
import com.imcode.imcms.vaadin.component._
import com.vaadin.ui.Label
import imcode.server.document.textdocument.TextDocumentDomainObject.LoopItemRef

import scala.collection.JavaConverters._

// next version
// -- drag & drop
// -- allow edit text
// -- allow edit image
class LoopEditor(docRef: DocRef, loopNo: Int) extends Editor with ImcmsServicesSupport {

  private val lblEmpty = new Label("Empty")

  override type Data = TextDocLoopContainer

  private val loader = imcmsServices.getManagedBean(classOf[TextDocumentContentLoader])

  private var loop = Loop.empty()

  private var initialLoop: Loop = null

  override val view: LoopEditorView = new LoopEditorView |>> { editorView =>

    editorView.miAddFirst.setCommandHandler { mi =>
      loop = loop.ops().addEntryFirst()
      val entryNo = loop.getLastEntryNo.get()
      addEntryView(entryNo, 0)
    }

    editorView.miAddLast.setCommandHandler { mi =>
      loop = loop.ops().addEntryLast()
      val entryNo = loop.getLastEntryNo.get()
      addEntryView(entryNo, view.lytEntries.getComponentCount - 1)
    }

    editorView.miClear.setCommandHandler { mi =>
      setLoop(Loop.empty())
    }
  }

  resetValues()

  override def collectValues(): ErrorsOrData = {
    Right(TextDocLoopContainer.of(docRef.getVersionRef, loopNo, loop))
  }

  override def resetValues() {
    loop = Option(loader.getLoop(docRef.getVersionRef, loopNo)).getOrElse(Loop.empty())

    setLoop(loop)
  }

  private def addEntryView(no: Int, index: Int) {
    if (view.lytEntries.getComponentCount == 1 && view.lytEntries.getComponent(0) == lblEmpty) {
      view.lytEntries.removeComponent(lblEmpty)
    }

    val text = loader.getFirstLoopEntryText(docRef, LoopEntryRef.of(loopNo, no))

    val entryView = new EntryView |>> { v =>
      v.lblText.setValue(no + ": " + (if (text != null) text.getText.take(30) else "<content is not defined>"))
    }

    view.lytEntries.addComponent(entryView, index)

    entryView.btnDelete.addClickHandler { _ =>
      view.lytEntries.removeComponent(entryView)
    }

    entryView.btnMoveUp.addClickHandler { _ =>
      view.lytEntries.getComponentIndex(entryView) match {
        case 0 =>
        case componentIndex =>
          view.lytEntries.removeComponent(entryView)
          addEntryView(no, componentIndex - 1)
      }
    }

    entryView.btnMoveDown.addClickHandler { _ =>
      view.lytEntries.getComponentIndex(entryView) match {
        case n if n == view.lytEntries.getComponentCount - 1 =>
        case componentIndex =>
          view.lytEntries.removeComponent(entryView)
          addEntryView(no, componentIndex + 1)
      }
    }
  }

  def setLoop(loop: Loop) {
    this.loop = loop
    if (initialLoop != null) {
      initialLoop = loop
    }

    view.lytEntries.removeAllComponents()
    view.lytEntries.addComponent(lblEmpty)

    loop.getEntries.forEach(new BiConsumer[Integer, java.lang.Boolean] {
      var index = 0
      override def accept(no: Integer, enabled: java.lang.Boolean) {
        addEntryView(no, index)
        index += 1
      }
    })
  }

  def isModified(): Boolean = initialLoop != loop
}
