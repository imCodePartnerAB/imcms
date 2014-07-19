package com.imcode
package imcms
package admin.docadmin.loop

import _root_.java.util.function.BiConsumer

import com.imcode.imcms.api.Loop
import com.imcode.imcms.mapping.TextDocumentContentLoader
import com.imcode.imcms.mapping.container.{DocRef, TextDocLoopContainer}
import com.imcode.imcms.vaadin.Editor
import com.imcode.imcms.vaadin.component._
import com.vaadin.ui.Label
import imcode.server.document.textdocument.TextDocumentDomainObject.LoopItemRef

import scala.collection.JavaConverters._

class LoopEditor(docRef: DocRef, loopNo: Int) extends Editor with ImcmsServicesSupport {

  private val lblEmpty = new Label("Empty")

  override type Data = TextDocLoopContainer

  override val view: LoopEditorView = new LoopEditorView |>> { editorView =>

    editorView.miAddFirst.setCommandHandler { mi =>
      loop = loop.ops().addEntryFirst()
      val entryNo = loop.getLastEntryNo.get()
      addEntryView(entryNo, 0)
    }

    editorView.miAddLast.setCommandHandler { mi =>
      loop = loop.ops().addEntryLast()
      val entryNo = loop.getLastEntryNo.get()
      val entryIndex = loop.getLastEntryIndex.get()
      addEntryView(entryNo, entryIndex)
    }

    editorView.miClear.setCommandHandler { mi =>
      setLoop(Loop.empty())
    }
  }

  private val loader = imcmsServices.getManagedBean(classOf[TextDocumentContentLoader])

  resetValues()

  private var loop = Loop.empty()

  override def collectValues(): ErrorsOrData = {
    Right(TextDocLoopContainer.of(versionRef, loopNo, loop))
  }

  override def resetValues() {
    loop = Option(loader.getLoop(docRef.getVersionRef, loopNo)).getOrElse(Loop.empty())

    setLoop(loop)
  }

  private def addEntryView(no: Int, index: Int) {
    if (view.lytEntries.getComponentCount == 1 && view.lytEntries.getComponent(0) == lblEmpty) {
      view.lytEntries.removeComponent(lblEmpty)
    }

    val text = loader.getFirstLoopEntryText(docRef, LoopItemRef.of(loopNo, no))

    val entryView = new EntryView |>> { v =>
      v.lblText.setValue(if (text != null) text.getText.take(20) else no.toString)
    }

    view.lytEntries.addComponent(entryView, index)
  }

  def setLoop(loop: Loop) {
    this.loop = loop

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
}
