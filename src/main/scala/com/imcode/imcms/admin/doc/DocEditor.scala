package com.imcode
package imcms
package admin.doc

import _root_.imcode.server.document.textdocument.TextDocumentDomainObject
import com.imcode.imcms.admin.doc.content.htmldoc.HtmlDocContentEditor
import com.imcode.imcms.mapping.orm.{DocumentLanguage, I18nMeta}
import com.vaadin.ui.themes.Reindeer
import imcode.server.document.{HtmlDocumentDomainObject, UrlDocumentDomainObject, FileDocumentDomainObject, DocumentDomainObject}
import com.imcode.imcms.admin.doc.meta.MetaEditor
import com.imcode.imcms.vaadin.Editor
import com.vaadin.ui.TabSheet
import com.imcode.imcms.vaadin.component.FullSize
import com.imcode.imcms.admin.doc.content.UnsupportedDocContentEditor
import com.imcode.imcms.admin.doc.content.textdoc.NewTextDocContentEditor
import com.imcode.imcms.admin.doc.content.urldoc.UrlDocContentEditor
import com.imcode.imcms.admin.doc.content.filedoc.FileDocContentEditor

class DocEditor(doc: DocumentDomainObject) extends Editor {

  override type Data = (DocumentDomainObject, Map[DocumentLanguage, I18nMeta])

  val metaEditor = new MetaEditor(doc)
  val contentEditor = doc match {
    case textDoc: TextDocumentDomainObject if textDoc.getMetaId == null => new NewTextDocContentEditor(textDoc, metaEditor)
    case fileDoc: FileDocumentDomainObject => new FileDocContentEditor(fileDoc)
    case urlDoc: UrlDocumentDomainObject => new UrlDocContentEditor(urlDoc)
    case htmlDoc: HtmlDocumentDomainObject => new HtmlDocContentEditor(htmlDoc)
    case _ => new UnsupportedDocContentEditor(doc)
  }

  override val view = new TabSheet with FullSize |>> { w =>
    w.addTab(metaEditor.view, "doc_editor_tab.meta".i, null)
    w.addTab(contentEditor.view, "doc_editor_tab.content".i, null)
    w.addStyleName(Reindeer.TABSHEET_MINIMAL)
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