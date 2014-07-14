package com.imcode
package imcms
package admin.docadmin.loop

import com.imcode.imcms.api.Loop
import com.imcode.imcms.mapping.TextDocumentContentLoader
import com.imcode.imcms.mapping.container.{TextDocLoopContainer, VersionRef}
import com.imcode.imcms.vaadin.Editor
import com.imcode.imcms.vaadin.component._
import com.vaadin.ui.Label

import scala.collection.JavaConverters._

class LoopEditor(versionRef: VersionRef, loopNo: Int) extends Editor with ImcmsServicesSupport {

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
      addEntryView(entryNo, view.lytEntries.getComponentCount)
    }

    editorView.miClear.setCommandHandler { mi =>
      setLoop(Loop.empty())
    }
  }

  private var loop = Loop.empty()

  override def collectValues(): ErrorsOrData = {
    Right(TextDocLoopContainer.of(versionRef, loopNo, loop))
  }

  override def resetValues() {
    val loader = imcmsServices.getManagedBean(classOf[TextDocumentContentLoader])
    loop = Option(loader.getLoop(versionRef, loopNo)).getOrElse(Loop.empty())

    setLoop(loop)
  }

  private def addEntryView(no: Int, index: Int) {
    val entryView = new EntryView |>> { v =>
      v.lblText.setValue(no.toString)
    }

    view.lytEntries.addComponent(entryView, index)
  }

  def setLoop(loop: Loop) {
    this.loop = loop

    view.lytEntries.removeAllComponents()
    view.lytEntries.addComponent(new Label("Empty"))

    for (((no, enabled), index) <- loop.getEntries.asScala.zipWithIndex) {
      addEntryView(no, index)
    }
  }
}
