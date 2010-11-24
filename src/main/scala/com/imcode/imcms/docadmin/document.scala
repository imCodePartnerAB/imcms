package com.imcode.imcms.docadmin

import com.imcode._
import scala.collection.JavaConversions._
import com.vaadin.ui._
import com.imcode.imcms.dao.{MetaDao, SystemDao, LanguageDao, IPAccessDao}
import com.imcode.imcms.api._
import com.imcode.imcms.sysadmin.permissions.{UserUI, UsersView}
import imcode.server.user._
import imcode.server.{Imcms}
import java.util.{Date, Collection => JCollection}
import scala.collection.mutable.{Map => MMap}
import imcode.server.document._
import com.imcode.imcms.vaadin._
import com.imcode.imcms.vaadin.AbstractFieldWrapper._
import java.util.concurrent.atomic.AtomicReference
import java.net.{MalformedURLException, URL}
import com.vaadin.ui.Window.Notification
import com.imcode.imcms.vaadin.flow.{Flow, FlowPage, FlowUI}

//todo: type Component = UI ??


/**
 *
 */
class DocAdmin(application: VaadinApplication) {
  
  import scala.util.control.{Exception => E}
  
  def newURLDocFlow(parentDoc: DocumentDomainObject): FlowUI = {
    val docUI = new URLDocUI
    val docValidator = () => E.allCatch.either(new URL(docUI.txtURL.value)) fold (ex => Some(ex.getMessage), url => None)
    val page0 = new FlowPage(() => docUI, docValidator)

    val metaModel = MetaModel(DocumentTypeDomainObject.URL_ID, parentDoc)
    val metaMVC = new MetaMVC(application, metaModel)
    val metaValidator = () => Some("meta is invalid, please fix the following errors..")
    val page1 = new FlowPage(() => metaMVC.view, metaValidator)

    val commit = () => Left("Not implemented")

    new FlowUI(new Flow(commit, page0, page1))
  }

  
  def newFileDocFlow(parentDoc: DocumentDomainObject): FlowUI = {
    val docUI = new FileDocUI
    val docValidator = () => E.allCatch.either(new URL(docUI.txtURL.value)) fold (ex => Some(ex.getMessage), url => None)
    val page0 = new FlowPage(() =>docUI, docValidator)

    val metaModel = MetaModel(DocumentTypeDomainObject.URL_ID, parentDoc)
    val metaMVC = new MetaMVC(application, metaModel)
    val metaValidator = () => Some("meta is invalid, please fix the following errors..")
    val page1 = new FlowPage(() => metaMVC.view, metaValidator)

    val commit = () => Left("Not implemented")

    new FlowUI(new Flow(commit, page0, page1))
  }

  def newTextDocFlow(parentDoc: DocumentDomainObject): FlowUI = {
    val metaModel = MetaModel(DocumentTypeDomainObject.URL_ID, parentDoc)
    val metaMVC = new MetaMVC(application, metaModel)
    val metaValidator = () => Some("meta is invalid, please fix the following errors..")
    val page1 = new FlowPage(() => metaMVC.view, metaValidator)

    val commit = () => Left("Not implemented")

    new FlowUI(new Flow(commit, page1))
  }

  def editURLDocument: URLDocUI = new URLDocUI
  def editFileDocument: FileDocUI = new FileDocUI
  def editTextDocument {}
}




// dlg-flow-mode
// in-place-mode 

//URLDocFlowFactory?

// http/s, ftp???
class URLDocUI extends VerticalLayout with Spacing {
  val txtURL = new TextField("Link URL") with ValueType[String]

  addComponents(this, txtURL)
}

class FileDocUI extends VerticalLayout {
  val txtURL = new TextField("File") with ValueType[String]

  addComponents(this, txtURL)
}



