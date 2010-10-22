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

      let(v.lytI18n.tsLabels.addTab(lytLabels)) { tab =>
        if (Imcms.getI18nSupport.isDefault(language)) {
          tab.setCaption(language.getName + " (default)")  
        } else {
          tab.setCaption(language.getName)
          tab.setEnabled(enabled)
        }
      }
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
          content.lytLanguages.addComponent(chkLanguage)
        }

        w.setMainContent(content)
      }
    }

    v.btnKeywords addListener unit {
      app.initAndShow(new OkCancelDialog("Keywords")) { w =>
        w setMainContent new KeywordsDialogContent(List("Alpha", "Beta", "Gamma", "Delta", "Epsilon", "Fi", "Lambda"))
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

  val lytLanguages = new VerticalLayout {
    setCaption("Enabled languages")
    setSizeUndefined
  }

  addComponents(this, lytLanguages, ogDisabledShowMode)
}


class LabelsLyt extends FormLayout {
  val txtTitle = new TextField("Title")
  val txtMenuText = new TextField("Menu text")
  val embLinkImage = new TextField("Link image")

  addComponents(this, txtTitle, txtMenuText, embLinkImage)
  setSpacing(false)
  setSizeUndefined
}

class KeywordsDialogContent(keywords: Seq[String] = Nil) extends GridLayout(3,2) {

  type ItemIds = JCollection[String]

  val lstKeywords = new ListSelect("Keywords") {
    setNullSelectionAllowed(true)
    setMultiSelect(true)
    setRows(10)
    setColumns(10)
    setImmediate(true)
  }

  val btnAdd = new Button("+")
  val btnRemove = new Button("-")
  val txtKeyword = new TextField {
    setInputPrompt("New keyword")
  }

  addComponent(txtKeyword, 0, 0)
  addComponent(btnAdd, 1, 0)
  addComponent(btnRemove, 2, 0)
  addComponent(lstKeywords, 0, 1, 2, 1)

  btnAdd addListener unit {
    txtKeyword.stringValue.trim.toLowerCase match {
      case value if value.length > 0 && lstKeywords.getItem(value) == null =>
        setKeywords(value :: lstKeywords.getItemIds.asInstanceOf[ItemIds].toList)
      case _ =>
    }

    txtKeyword setValue ""
  }

  btnRemove addListener unit {
    whenSelected[ItemIds](lstKeywords) { _ foreach (lstKeywords removeItem _) }
  }

  lstKeywords addListener unit {
    lstKeywords.getValue.asInstanceOf[ItemIds].toList match {
      case value :: Nil => txtKeyword setValue value
      case _ :: _ :: _ => txtKeyword setValue ""
      case _ =>
    }
  }

  setKeywords(keywords)
  
  def setKeywords(keywords: Seq[String]) {
    lstKeywords.removeAllItems
    keywords.map(_.toLowerCase).sorted.foreach { lstKeywords addItem _ }
  }
}

class MetaLyt extends FormLayout {
  val txtName = new TextField("Name")
  val txtAlias = new TextField("Alias")
  val lytI18n = new VerticalLayout {
    val tsLabels = new TabSheet {setWidth("100%")}
    val btnSettings = new Button("Settings...") {
      setStyleName(Button.STYLE_LINK)
      
    }
    val chkCopyLabelsTextToPage = new CheckBox("Copy link heading & subheading to text 1 & text 2 in page")

    setCaption("Appearence")
    addComponents(this, tsLabels, btnSettings, chkCopyLabelsTextToPage)
    setSizeUndefined
  }

  //refactor
  val btnKeywords = new Button("Keywords"){
    setStyleName(Button.STYLE_LINK)
  }
  val btnCategories = new Button("Categories"){
    setStyleName(Button.STYLE_LINK)
  }

  addComponents(this, txtName, txtAlias, lytI18n, btnKeywords, btnCategories)
  setMargin(true)
  setWidth("100%")
}