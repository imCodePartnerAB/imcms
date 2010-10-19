package com.imcode.imcms.admin.ui

import com.imcode.imcms.servlet.superadmin.vaadin.permissions._
import com.imcode.imcms.servlet.superadmin.vaadin.filemanager._
import com.imcode.imcms.servlet.superadmin.vaadin.template._
import java.lang.{Class => JClass, Boolean => JBoolean, Integer => JInteger}
import scala.collection.JavaConversions._
import com.imcode._
import com.vaadin.event.ItemClickEvent
import com.vaadin.terminal.gwt.server.WebApplicationContext
import com.vaadin.ui._
import com.vaadin.data.Property
import com.vaadin.data.Property._
import com.imcode.imcms.dao.{MetaDao, SystemDao, LanguageDao, IPAccessDao}
import imcms.api._
import imcms.mapping.CategoryMapper
import imcms.servlet.superadmin.AdminSearchTerms
import com.imcode.imcms.api.Document.PublicationStatus
import imcms.servlet.superadmin.vaadin.ChatTopic.Message
import imcode.util.Utility
import imcode.server.user._
import imcode.server.{SystemData, Imcms}
import java.util.{Date, Collection => JCollection}
import com.vaadin.ui.Layout.MarginInfo
import com.imcode.imcms.servlet.superadmin.vaadin.ui._
import com.imcode.imcms.servlet.superadmin.vaadin.ui.AbstractFieldWrapper._
import java.util.concurrent.atomic.AtomicReference
import scala.actors.Actor._
import scala.actors._
import imcode.server.document.textdocument.TextDocumentDomainObject
import imcode.server.document.{TemplateDomainObject, CategoryDomainObject, CategoryTypeDomainObject, DocumentDomainObject}
import java.io.{ByteArrayInputStream, OutputStream, FileOutputStream, File}
import com.vaadin.terminal.{ThemeResource, UserError}

/**
 * @param doc document in default language
 */
class MetaMVC(app: VaadinApplication,
              doc: DocumentDomainObject,
              languagesMap: Map[I18nLanguage, Boolean],
              labelsMap: Map[I18nLanguage, DocumentLabels]) {

  val view = createView 
  
//  def addLanguage(la: LanguagesArea) = {}
//  def setActiveLanguages(la: LanguagesArea, languages: Seq[I18nLanguage]) = {}

  def createView = letret(new MetaLyt) { v =>
    for {
      (language, enabled) <- languagesMap
      labels = labelsMap.getOrElse(language, { new DocumentLabels {setLanguage(language)}})
    } {     
      val lytLabels = letret(new LabelsLyt) { l =>        
        l.txtTitle setValue labels.getHeadline
        l.txtMenuText  setValue labels.getMenuText
      }

      v.lytI18n.tsLabels.addTab(lytLabels, language.getName, null).setEnabled(enabled)
    }

    v.lytI18n.btnSettings addListener unit {
      app.initAndShow(new OkCancelDialog("Settings")) { w =>
        val content = new I18nSettingsDialogContent

        for ((language, enabled) <- languagesMap) {
          val chkLanguage = new CheckBox(language.getName) {
            setValue(enabled)
            setEnabled(!Imcms.getI18nSupport.isDefault(language))
            // add listner - disable tab
          }
        }

        w.setMainContent(content)
      }
    }
  }
}

//class LanguagesArea extends VerticalLayout {
//
//}

class I18nSettingsDialogContent extends FormLayout {
  val ogDisabledShowMode = new OptionGroup(
    "When disabled", 
    List("Show in default language", "Show 'Not found' page")
  )

  val pnlLanguages = new Panel {
    setCaption("Enabled languages")
    setStyleName(Panel.STYLE_LIGHT)
    setHeight("150px")
    setScrollable(true)
  }

  addComponents(this, pnlLanguages, ogDisabledShowMode)
}


class LabelsLyt extends FormLayout {
  val txtTitle = new TextField("Title")
  val txtMenuText = new TextField("Menu text")
  val embLinkImage = new TextField("Link image")

  addComponents(this, txtTitle, txtMenuText, embLinkImage)
}

class MetaLyt extends VerticalLayout {
  val lytI18n = new VerticalLayout {
    val tsLabels = new TabSheet
    val btnSettings = new Button("Settings...")

    addComponents(this, tsLabels, btnSettings)
  }

  addComponent(lytI18n)
}