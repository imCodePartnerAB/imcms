package com.imcode.imcms.admin.docadmin.loop

import com.imcode.imcms.api.Loop
import com.imcode.imcms.ImcmsServicesSupport
import com.imcode.imcms.mapping.container.{VersionRef, TextDocLoopContainer}
import com.imcode.imcms.mapping.TextDocumentContentLoader
import com.imcode.imcms.vaadin.Editor

class LoopEditor(versionRef: VersionRef, loopNo: Int) extends Editor with ImcmsServicesSupport {

  override type Data = TextDocLoopContainer

  override val view: LoopEditorView = new LoopEditorView

  private var loop = Loop.empty()

  override def collectValues(): ErrorsOrData = {
    Right(TextDocLoopContainer.of(versionRef, loopNo, loop))
  }

  override def resetValues() {
    val loader = imcmsServices.getManagedBean(classOf[TextDocumentContentLoader])
    loop = Option(doc.getLoop).getOrElse(Loop.empty())
  }
}
