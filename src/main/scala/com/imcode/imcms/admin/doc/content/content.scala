package com.imcode
package imcms
package admin.doc.content

import scala.collection.mutable.{Map => MMap}
import scala.collection.breakOut
import scala.collection.JavaConversions._
import com.imcode.imcms.dao.{MetaDao, SystemDao, LanguageDao, IPAccessDao}
import com.imcode.imcms.api._
import imcode.server.user._
import imcode.server.{Imcms}
import imcode.server.document._
import com.imcode.imcms.vaadin._

import java.net.{MalformedURLException, URL}
import imcode.server.document.FileDocumentDomainObject.FileDocumentFile
import textdocument.TextDocumentDomainObject
import java.util.{EnumSet}
import imcms.mapping.DocumentMapper.SaveDirectives
import imcms.mapping.{DocumentMapper, DocumentSaver}
import com.vaadin.data.Property.{ValueChangeEvent, ValueChangeListener}
import java.io.{FileInputStream, ByteArrayInputStream}
import com.imcode.imcms.admin.system.file.{UploadedFile, FileUploaderDialog}
import scala.collection.immutable.ListMap
import imcode.util.io.{FileInputStreamSource, InputStreamSource}
import com.vaadin.ui.Table.ColumnGenerator
import com.vaadin.terminal.{ThemeResource, ExternalResource}
import com.vaadin.ui._

trait Editor {
  type StateType

  def ui: Component
  def state: State

  trait State {
    //def clearValidationErrors(): Unit {}
    //def revert(): Unit = {}

    /** Validates editor's data. */
    def validate(): Seq[ErrorMsg] = Nil
    def validateAndGet(): Either[Seq[ErrorMsg], StateType] = validate() match {
      case errorMsgs if errorMsgs.nonEmpty => Left(errorMsgs)
      case _ => Right(get)
    }

    protected def get: StateType
  }
}


trait DocContentEditor extends Editor {
  type StateType <: DocumentDomainObject
}


class TextDocContentEditor(doc: TextDocumentDomainObject) extends DocContentEditor {
  type StateType = TextDocumentDomainObject

  val ui = letret(new TextDocContentEditorUI) { ui =>
  }

  val state = new State {
    protected def get = doc
  }
}


class URLDocContentEditor(doc: UrlDocumentDomainObject) extends DocContentEditor {
  type StateType = UrlDocumentDomainObject

  val ui = letret(new URLDocContentEditorUI) { ui =>
    ui.txtURL.value = "http://"
  }

  val state = new State {
    protected def get = doc
  }
}


class HTMLDocContentEditor(doc: HtmlDocumentDomainObject) extends DocContentEditor {
  type StateType = HtmlDocumentDomainObject

  val ui = letret(new HTMLDocContentEditorUI) { ui =>
    ui.txaHTML.value = <html/>.toString
  }

  val state = new State {
    protected def get = doc
  }
}

/**
 * Used with deprecated docs such as Browser.
 */
class UnsupportedDocContentEditor(doc: DocumentDomainObject) extends DocContentEditor {
  type StateType = DocumentDomainObject

  val ui = new Label("N/StateType".i)

  val state = new State {
    protected def get = doc
  }
}


//case class MimeType(name: String, displayName: String)


/**
 * URL document editor UI
 */
class URLDocContentEditorUI extends FormLayout {
  val txtURL = new TextField("URL/Link".i) with ValueType[String] with FullWidth

  addComponents(this, txtURL)
}


/**
 * HTML document editor UI
 */
class HTMLDocContentEditorUI extends FormLayout {
  val txaHTML = new TextArea("HTML".i) with FullSize

  addComponents(this, txaHTML)
}



class TextDocContentEditorUI extends VerticalLayout with FullSize with Spacing with Margin {
  // todo: show outline/redirect external doc editor
}


/**
 * This page is shown as a second page in the flow - next after meta.
 * User may choose whether copy link texts (filled in meta page) into the text fields no 1 and 2.
 * Every language's texts is shown in its tab.
 */
class NewTextDocContentEditorUI extends VerticalLayout with FullSize with Spacing with Margin {
  class TextsUI extends FormLayout with FullSize {
    val txtText1 = new TextField("No 1")
    val txtText2 = new TextField("No 2")

    addComponents(this, txtText1, txtText2)
  }

  val chkCopyI18nMetaTextsToTextFields = new CheckBox("Copy link heading & subheading to text 1 & text 2 in page")
                                           with Immediate
  val tsTexts = new TabSheet with UndefinedSize with FullSize

  addComponents(this, chkCopyI18nMetaTextsToTextFields, tsTexts)
  setExpandRatio(tsTexts, 1.0f)
}