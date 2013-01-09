package com.imcode
package imcms.admin.doc

import _root_.imcode.server.document.textdocument.TextDocumentDomainObject
import _root_.imcode.server.document.{UrlDocumentDomainObject, FileDocumentDomainObject, DocumentDomainObject}
import com.imcode.imcms.admin.doc.content.filedoc.FileDocContentEditor
import com.imcode.imcms.admin.doc.content.{UnsupportedDocContentEditor, UrlDocContentEditor, TextDocContentEditor, DocContentEditor}
import com.imcode.imcms.api.{I18nMeta, I18nLanguage}
import com.imcode.imcms.admin.doc.meta.MetaEditor
import com.imcode.imcms.vaadin.Editor
import com.vaadin.ui.TabSheet
import com.imcode.imcms.vaadin.ui.FullSize


object DocEditor {

  def mkContentEditor(doc: DocumentDomainObject): DocContentEditor = doc match {
    case textDoc: TextDocumentDomainObject => new TextDocContentEditor(textDoc)
    case fileDoc: FileDocumentDomainObject => new FileDocContentEditor(fileDoc)
    case urlDoc: UrlDocumentDomainObject => new UrlDocContentEditor(urlDoc)
    case _ => new UnsupportedDocContentEditor(doc)
  }
}


class DocEditor(doc: DocumentDomainObject) extends Editor {

  type Data = (DocumentDomainObject, Map[I18nLanguage, I18nMeta])

  val metaEditor = new MetaEditor(doc)
  val contentEditor = DocEditor.mkContentEditor(doc)

  val ui = new TabSheet with FullSize |>> { ts =>
    ts.addTab(metaEditor.ui, "Properties", null)
    ts.addTab(contentEditor.ui, "Content", null)
  }

  def resetValues() {
    metaEditor.resetValues()
    contentEditor.resetValues()
  }

  def collectValues(): ErrorsOrData = (metaEditor.collectValues(), contentEditor.collectValues()) match {
    case (Left(errors), _) => Left(errors)
    case (_, Left(errors)) => Left(errors)
    case (Right((metaDoc, i18nMetas)), Right(contentDoc)) =>
      // todo: merge meta doc and content doc
      Right((metaDoc, i18nMetas))
  }
}

//class DocEditorUI extends TabSheet with FullSize


