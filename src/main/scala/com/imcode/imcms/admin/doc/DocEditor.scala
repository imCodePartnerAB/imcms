package com.imcode
package imcms
package admin.doc

import _root_.imcode.server.document.textdocument.TextDocumentDomainObject
import imcode.server.document.{HtmlDocumentDomainObject, UrlDocumentDomainObject, FileDocumentDomainObject, DocumentDomainObject}
import com.imcode.imcms.api.{I18nMeta, DocumentLanguage}
import com.imcode.imcms.admin.doc.meta.MetaEditor
import com.imcode.imcms.vaadin.Editor
import com.vaadin.ui.TabSheet
import com.imcode.imcms.vaadin.component.FullSize
import com.imcode.imcms.admin.doc.content.{UnsupportedDocContentEditor}
import com.imcode.imcms.admin.doc.content.textdoc.NewTextDocContentEditor
import com.imcode.imcms.admin.doc.content.urldoc.UrlDocContentEditor
import com.imcode.imcms.admin.doc.content.filedoc.FileDocContentEditor

// todo: add html editor support
class DocEditor(doc: DocumentDomainObject) extends Editor {

  override type Data = (DocumentDomainObject, Map[DocumentLanguage, I18nMeta])

  val metaEditor = new MetaEditor(doc)
  val contentEditor = doc match {
    case textDoc: TextDocumentDomainObject if textDoc.getMetaId == null => new NewTextDocContentEditor(textDoc, metaEditor)
    case fileDoc: FileDocumentDomainObject => new FileDocContentEditor(fileDoc)
    case urlDoc: UrlDocumentDomainObject => new UrlDocContentEditor(urlDoc)
    //case htmlDoc: HtmlDocumentDomainObject => new HtmlDocContentEditor(urlDoc)
    case _ => new UnsupportedDocContentEditor(doc)
  }

  override val widget = new TabSheet with FullSize |>> { w =>
    w.addTab(metaEditor.widget, "doc_editor.tab.meta".i, null)
    w.addTab(contentEditor.widget, "doc_editor.tab.content".i, null)
  }

  override def resetValues() {
    metaEditor.resetValues()
    contentEditor.resetValues()
  }

  override def collectValues(): ErrorsOrData = (metaEditor.collectValues(), contentEditor.collectValues()) match {
    case (Left(errors), _) => Left(errors)
    case (_, Left(errors)) => Left(errors)
    case (Right((metaDoc, i18nMetas)), Right(contentDoc)) =>
      val mergedDoc = contentDoc.clone()

      mergedDoc.setMeta(metaDoc.getMeta)
      mergedDoc.setVersion(metaDoc.getVersion)
      mergedDoc.setI18nMeta(metaDoc.getI18nMeta)

      Right((mergedDoc, i18nMetas))
  }
}