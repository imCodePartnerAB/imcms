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

    v.btnCategories addListener unit {
      app.initAndShow(new OkCancelDialog("Categories")) { w =>
        val mainContent = new CategoriesDialogContent
        
        let(w.setMainContent(mainContent)) { c =>
          c.setHeight("350px")
        }
      }
    } 
  }
}


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


class LabelsLyt extends FormLayout with NoSpacing with UndefinedSize {
  val txtTitle = new TextField("Title")
  val txtMenuText = new TextField("Menu text")
  val embLinkImage = new TextField("Link image")

  addComponents(this, txtTitle, txtMenuText, embLinkImage)
}


class PublicationLyt extends GridLayout(2, 4) with Spacing {
  val lblPublisher = new Label("Publisher") with UndefinedSize
  val lblPublisherName = new Label("No publisher selected") with UndefinedSize
  val btnChoosePublisher = new Button("...") with LinkStyle

  val lytPublisher = new HorizontalLayout with Spacing {
    addComponents(this, lblPublisherName, btnChoosePublisher)    
  }

  val lblStatus = new Label("Status") with UndefinedSize
  val sltStatus = new Select {
    setNullSelectionAllowed(false)
    addItem("Approved")
    addItem("Disapproved")
    select("Disapproved")
  }

  val lblVersion = new Label("Version") with UndefinedSize
  val sltVersion = new Select {
    setNullSelectionAllowed(false)
    addItem("Working")
    select("Working")
  }

  val calStart = new DateField { setValue(new Date) }
  val calEnd = new DateField
  val chkStart = new CheckBox("Start date") { setValue(true); setEnabled(false) } // decoration, always disabled
  val chkEnd = new CheckBox("End date") with Immediate {
    setValue(false)
  }
  
  val frmSchedule = new Form with UndefinedSize {
    setCaption("Schedule")
    let(new GridLayout(2, 2) with Spacing) { lyt =>
      addComponents(lyt, chkStart, calStart, chkEnd, calEnd)
      setLayout(lyt)
    }
  }

  addComponents(this, lblStatus, sltStatus, lblVersion, sltVersion, lblPublisher, lytPublisher)
  addComponent(frmSchedule, 0, 3, 1, 3)
}


class KeywordsDialogContent(keywords: Seq[String] = Nil) extends GridLayout(3,2) {

  type ItemIds = JCollection[String]

  val lstKeywords = new ListSelect("Keywords") with Immediate with NullSelection with MultiSelect {
    setRows(10)
    setColumns(10)
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
      case List(value) => txtKeyword setValue value
      case List(_, _, _*) => txtKeyword setValue ""
      case _ =>
    }
  }

  setKeywords(keywords)
  
  def setKeywords(keywords: Seq[String]) {
    lstKeywords.removeAllItems
    keywords.map(_.toLowerCase).sorted.foreach { lstKeywords addItem _ }
  }
}


class CategoriesDialogContent extends Panel {
  setStyleName(Panel.STYLE_LIGHT)

  val lytContent = new FormLayout

  setContent(lytContent)

  for {
    categoryType <- Imcms.getServices.getCategoryMapper.getAllCategoryTypes
    categories = Imcms.getServices.getCategoryMapper.getAllCategoriesOfType(categoryType)
    if categories.nonEmpty
  } {
    val sltCategory =
      if (categoryType.isSingleSelect) {
        letret(new Select) { slt =>
          slt.setNullSelectionAllowed(false)
          slt.setMultiSelect(false)

          categories foreach { c =>
            slt.addItem(c)
            slt.setItemCaption(c, c.getName)
          }
        }
      } else {
        letret(new TwinSelect[CategoryDomainObject]) { tws =>
          categories foreach { c =>
            tws.addAvailableItem(c, c.getName)
          }
        }
      }

    sltCategory.setCaption(categoryType.getName)

    lytContent.addComponent(sltCategory)
  }
}


class MetaLyt extends FormLayout {
  val txtId = new TextField("System Id") with Disabled
  val txtName = new TextField("Name")
  val txtAlias = new TextField("Alias")
  val lytI18n = new VerticalLayout with UndefinedSize {
    val tsLabels = new TabSheet with FullWidth
    val btnSettings = new Button("Configure...") with LinkStyle
    val chkCopyLabelsTextToPage = new CheckBox("Copy link heading & subheading to text 1 & text 2 in page")
                                                                                              
    setCaption("Appearence")
    addComponents(this, tsLabels, btnSettings, chkCopyLabelsTextToPage)
  }
  val lytLink = new VerticalLayout with Spacing with FullWidth with Margin {
    val lblLink = new Label("Show in")
    val ogLink = new OptionGroup("", List("Same frame", "New window", "Replace all", "Other frame:"))
    val txtFrameName = new TextField
    val chkShowToUnauthorizedUser = new CheckBox("Show to unauthorized user")

    val lytShowIn = new HorizontalLayout with Spacing {
      addComponents(this, lblLink, ogLink, txtFrameName)  
    }

    ogLink.addStyleName("horizontalgroup")
    setCaption("Link/menu item")
    addComponents(this, lytShowIn, chkShowToUnauthorizedUser)
  }

  val lytIdentity = new HorizontalLayout with Spacing {
    setCaption("Identity")
    addComponents(this, txtId, txtName, txtAlias)
  }

  //refactor
  val btnKeywords = new Button("Edit...") with LinkStyle

  val chkExclude = new CheckBox("Exclude this page (by default) from internal search results")

  val btnCategories = new Button("Edit...") with LinkStyle

  val lytSearch = new VerticalLayout with Spacing {
    val lblKeywords = new Label("No keywords assigned")

    setCaption("Search")
    addComponents(this, lblKeywords, btnKeywords, chkExclude)
  }

  val lytCategories = new VerticalLayout with Spacing {
    val lblCategories = new Label("No categories assigned")
    setCaption("Categories")
    addComponents(this, lblCategories, btnCategories)
  }

  val lytPublication = new PublicationLyt {
    setCaption("Publication")
  }
 
  addComponents(this, lytIdentity, new Label(""), lytI18n, new Label(""), lytLink, new Label(""), lytSearch, new Label(""), lytCategories, new Label(""), lytPublication)
}