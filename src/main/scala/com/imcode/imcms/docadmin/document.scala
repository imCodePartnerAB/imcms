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

class MetaModel(val meta: Meta,
                val defaultLanguage: I18nLanguage,
                val languages: MMap[I18nLanguage, Boolean],
                val i18nMetas: Map[I18nLanguage, I18nMeta],
                val versionInfo: Option[DocumentVersionInfo] = Option.empty) {

  val isNewDoc = versionInfo.isEmpty
}


object MetaModel {
  
  /** Creates meta model for existing document. */
  def apply(id: JInteger): MetaModel = {
    val meta = Imcms.getServices.getDocumentMapper.getDocumentLoaderCachingProxy.getMeta(id).clone
    val versionInfo = Imcms.getServices.getDocumentMapper.getDocumentLoaderCachingProxy.getDocumentVersionInfo(id)    
    val defaultLanguage = Imcms.getI18nSupport.getDefaultLanguage
    val languagesMap = MMap[I18nLanguage, Boolean]()

    Imcms.getI18nSupport.getLanguages foreach { language =>
      languagesMap.put(language, meta.getLanguages.contains(language))
    }
    languagesMap.put(defaultLanguage, true)

    val i18nMetas = Imcms.getServices.getDocumentMapper.getI18nMetas(id) map { i18nMeta =>
      i18nMeta.getLanguage -> i18nMeta
    } toMap

    new MetaModel(meta, defaultLanguage, languagesMap, i18nMetas, Some(versionInfo))    
  }

  /** Creates meta model for new document. */
  def apply(docType: Int, parentDoc: DocumentDomainObject): MetaModel = {
    val doc = Imcms.getServices.getDocumentMapper.createDocumentOfTypeFromParent(docType, parentDoc, Imcms.getServices.getImcmsAuthenticatorAndUserAndRoleMapper.getUser(UserDomainObject.DEFAULT_USER_ID))
    val defaultLanguage = Imcms.getI18nSupport.getDefaultLanguage
    val availableLanguages = Imcms.getI18nSupport.getLanguages
    val languages = availableLanguages.zip(Stream.continually(false)).toMap.updated(defaultLanguage, true)
    val i18nMetas = availableLanguages map { language =>
      let(new I18nMeta) { i18nMeta =>
        i18nMeta.setHeadline("")
        i18nMeta.setMenuText("")
        i18nMeta.setMenuImageURL("")
        i18nMeta.setLanguage(language)

        language -> i18nMeta
      }
    } toMap

    new MetaModel(
      doc.getMeta,
      Imcms.getI18nSupport.getDefaultLanguage,
      MMap(languages.toSeq : _*),
      i18nMetas
    )
  }
}


class FlowUI(page: Component, pages: Component*) extends VerticalLayout {
  addComponent(page)



//  def firstPage: Component
//  def lastPage = firstPage

//  def setOkButton(b: Button): Unit
//  def setCancelButton(b: Button): Unit
//  def setNextButton(b: Button): Unit
//  def setPrevButton(b: Button): Unit
}


class DocFlowFactory(app: VaadinApplication) {

  def editDocFlow: FlowUI = error("Not implemented")

  def newDocFlow(docType: DocumentTypeDomainObject, parentDoc: DocumentDomainObject): FlowUI = {
    val doc = Imcms.getServices.getDocumentMapper.createDocumentOfTypeFromParent(docType.getId, parentDoc, Imcms.getServices.getImcmsAuthenticatorAndUserAndRoleMapper.getUser(UserDomainObject.DEFAULT_USER_ID))
    val defaultLanguage = Imcms.getI18nSupport.getDefaultLanguage
    val availableLanguages = Imcms.getI18nSupport.getLanguages
    val languages = availableLanguages.zip(Stream.continually(false)).toMap.updated(defaultLanguage, true)
    val i18nMetas = availableLanguages map { language =>
      let(new I18nMeta) { labels =>
        labels.setHeadline("")
        labels.setMenuText("")
        labels.setMenuImageURL("")
        labels.setLanguage(language)

        language -> labels
      }
    } toMap

    val metaModel = new MetaModel(
      doc.getMeta,
      Imcms.getI18nSupport.getDefaultLanguage,
      MMap(languages.toSeq : _*),
      i18nMetas
    )

    docType match {
      case DocumentTypeDomainObject.TEXT =>
        val metaMVC = new MetaMVC(app, metaModel)

        new FlowUI(metaMVC.view)
        // setOkButton, setFlowButtons....

      case otherType => error("Not implemented. doc type: " + otherType)
    }
  }
}


/**
 * Initializes view and binds model to it.
 */
class MetaMVC(val app: VaadinApplication, val metaModel: MetaModel) {
  
  val view = createView 
  
//  def addLanguage(la: LanguagesArea) = {}
//  def setActiveLanguages(la: LanguagesArea, languages: Seq[I18nLanguage]) = {}

  def createView = letret(new MetaView) { v =>
    for {
      (language, enabled) <- metaModel.languages
      labels = metaModel.i18nMetas(language)
    } {     
      val lytLabels = letret(new I18nMetaLyt) { l =>
        l.txtTitle setValue labels.getHeadline
        l.txtMenuText  setValue labels.getMenuText
      }

      let(v.lytI18n.tsI18nMetas.addTab(lytLabels)) { tab =>
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

        for ((language, enabled) <- metaModel.languages) {
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

    v.lytSearch.lytKeywords.btnEdit addListener unit {
      app.initAndShow(new OkCancelDialog("Keywords")) { w =>
        val content = new KeywordsDialogContent(List("Alpha", "Beta", "Gamma", "Delta", "Epsilon", "Fi", "Lambda"))
        
        w setMainContent content
        w addOkButtonClickListener unit {
          content.txtKeyword setValue content.lstKeywords.value.mkString(", ") 
        }
      }
    }

    v.lytCategories.btnEdit addListener unit {
      app.initAndShow(new OkCancelDialog("Categories")) { w =>
        val mainContent = new CategoriesDialogContent
        
        let(w.setMainContent(mainContent)) { c =>
          c.setHeight("250px")
        }
      }
    }

    v.lytPublication.btnChoosePublisher addListener unit {
      app.initAndShow(new OkCancelDialog("Publisher")) { w =>
        w.setMainContent(new UserUI)
      }      
    }
  }
}


/**
 * Document I18n settings modal dialog content.
 */
class I18nSettingsDialogContent extends FormLayout {
  val ogDisabledShowMode = new OptionGroup(
    "When disabled",
    List("Show in default language", "Show 'Not found' page")
  )

  val lytLanguages = new VerticalLayout with UndefinedSize {
    setCaption("Enabled languages")
  }

  addComponents(this, lytLanguages, ogDisabledShowMode)
}


/**
 * Document I18nMeta content.
 */
class I18nMetaLyt extends FormLayout with NoSpacing with UndefinedSize {
  val txtTitle = new TextField("Title")
  val txtMenuText = new TextField("Menu text")
  val embLinkImage = new TextField("Link image")

  addComponents(this, txtTitle, txtMenuText, embLinkImage)
}


/**
 * Document publication parameters.
 */
class PublicationLyt extends GridLayout(2, 4) with Spacing {
  val lblPublisher = new Label("Publisher") with UndefinedSize
  val lblPublisherName = new Label("No publisher selected") with UndefinedSize
  val btnChoosePublisher = new Button("...") with LinkStyle

  val lytPublisher = new HorizontalLayout with Spacing {
    addComponents(this, lblPublisherName, btnChoosePublisher)    
  }

  val lblStatus = new Label("Status") with UndefinedSize
  val sltStatus = new Select with NoNullSelection {
    addItem("Approved")
    addItem("Disapproved")
    select("Disapproved")
  }

  val lblVersion = new Label("Version") with UndefinedSize
  val sltVersion = new Select with NoNullSelection {
    addItem("Working")
    select("Working")
  }

  val calStart = new DateField { setValue(new Date) }
  val calEnd = new DateField
  val chkStart = new CheckBox("Start date") with Disabled { setValue(true) } // decoration, always disabled
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


/**
 * Document keywords modal dialog content.
 */
class KeywordsDialogContent(keywords: Seq[String] = Nil) extends GridLayout(3,2) with Spacing {

  type ItemIds = JCollection[String]

  val lstKeywords = new ListSelect with ValueType[ItemIds] with MultiSelect with NullSelection with Immediate {
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

  btnAdd addListener block {
    txtKeyword.stringValue.trim.toLowerCase match {
      case value if value.length > 0 && lstKeywords.getItem(value) == null =>
        setKeywords(value :: lstKeywords.getItemIds.asInstanceOf[ItemIds].toList)
      case _ =>
    }

    txtKeyword setValue ""
  }

  btnRemove addListener block {
    whenSelected(lstKeywords) { _ foreach (lstKeywords removeItem _) }
  }

  lstKeywords addListener block {
    lstKeywords.value match {
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


/**
 * Document categories modal dialog content.
 */
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


/**
 * Document meta (doc info) view.
 */
class MetaView extends FormLayout with Margin {

  val lytIdentity = new HorizontalLayout with Spacing {
    val txtId = new TextField("Document Id") with Disabled
    val txtName = new TextField("Name")
    val txtAlias = new TextField("Alias")

    setCaption("Identity")    
    addComponents(this, txtId, txtName, txtAlias)
  }
  
  val lytI18n = new VerticalLayout with UndefinedSize {
    val tsI18nMetas = new TabSheet with FullWidth
    val btnSettings = new Button("Configure...") with LinkStyle
    val chkCopyLabelsTextToPage = new CheckBox("Copy link heading & subheading to text 1 & text 2 in page")
                                                                                              
    setCaption("Appearence")
    addComponents(this, tsI18nMetas, btnSettings, chkCopyLabelsTextToPage)
  }
  
  val lytLink = new VerticalLayout with Spacing {
    val chkOpenInNewWindow = new CheckBox("Open in new window")
    val chkShowToUnauthorizedUser = new CheckBox("Show to unauthorized user")

    setCaption("Link/menu item")
    addComponents(this, chkOpenInNewWindow, chkShowToUnauthorizedUser)
  }

  val lytSearch = new VerticalLayout with Spacing {
    val chkExclude = new CheckBox("Exclude this page from internal search")
    val lytKeywords = new HorizontalLayout with Spacing {
      val lblKeywords = new Label("Keywords")
      val txtKeywords = new TextField with Disabled { setColumns(30) }
      val btnEdit = new Button("Edit...") with LinkStyle

      addComponents(this, lblKeywords, txtKeywords, btnEdit)
    }

    setCaption("Search")
    addComponents(this, lytKeywords, chkExclude)
  }

  val lytCategories = new HorizontalLayout with Spacing {
    val lblCategories = new Label("Categories")
    val txtCategories = new TextField with Disabled { setColumns(30) }
    val btnEdit = new Button("Edit...") with LinkStyle
    
    addComponents(this, lblCategories, txtCategories, btnEdit)
  }

  val lytPublication = new PublicationLyt { setCaption("Publication") }

  forlet(lytIdentity, lytI18n, lytLink, lytSearch, lytCategories, lytPublication) { c =>
    c.setMargin(true)
    addComponent(c)
  }
}


/**
 * Single flow page.
 * To extend flexibility page ui is not referenced directly but rather returned by a function. 
 *
 * @param ui return flow page ui
 * @param validator page data validator - returns None if page is ok or Some(error message)
 */
class FlowPage(val ui: () => Component, val validator: () => Option[String])


/**
 * Contains flow pages and logic to switch between flow pages and commit flow. 
 *
 * @param fist the first page of the flow
 * @param rest rest pages of the flow 
 * @param commit commits flow data - returns either <error> or <ok> message in case of commit's op failure/sucess.  
 */
class Flow(val commit: () => Either[String, String], first: FlowPage, rest: FlowPage*) {
  val pages = first +: rest
  private var pageNoRef = new AtomicReference(0) // current page no ref
  private val lastPageNo = pages.length - 1;     // last page no

  /** Returns current flow page. */
  def page = pages(pageNoRef.get)

  /** Returns if the flow page is the first. */
  def isFirstPage = page == pages.head

  /** Returns if the flow page is the last. */
  def isLastPage = page == pages.last

  /**
   * If page validation fails returns failure message in Left.
   * Otherwise if current page is the last page in the flow returns None. If there are more pages,
   * next page become current and returned in Some. 
   */
  def maybeGoNext(): Either[String, Option[FlowPage]] = let(pageNoRef.get) { pageNo =>
    pages(pageNo).validator() match {
      case Some(ex) => Left(ex)
      case _ if pageNo == lastPageNo => Right(None)
      case _ => let(pageNo + 1) { newPageNo =>
        pageNoRef.set(newPageNo)
        Right(Some(pages(newPageNo)))
      }
    }
  }

  /**
   * If current page is the first page return None.
   * Otherwise previous page become current and returned in Some.
   */  
  def maybeGoPrev(): Option[FlowPage] = pageNoRef.get match {
    case 0 => None
    case pageNo => let(pageNo - 1) { newPageNo =>
      pageNoRef.set(newPageNo)
      Some(pages(newPageNo))
    }
  }
}


/**
 * Flow bar ui - just buttons.
 * If used in a dialog then flow bar should replace (/be used instead) dialog buttons bar.
 */
class FlowBarUI extends VerticalLayout with Spacing {
  val btnPrev = new Button("Prev") with ResourceCaption
  val btnNext = new Button("Next") with ResourceCaption
  val btnFinish = new Button("Finish") with ResourceCaption
  val btnCancel = new Button("Cancel") with ResourceCaption

  addComponents(this, btnCancel, btnPrev, btnNext, btnFinish)
}

class Flow2UI(flow: Flow) extends VerticalLayout with Spacing {
  val pnlPageUI = new Panel {setStyleName(Panel.STYLE_LIGHT)}
  val flowBar = new FlowBarUI

  private def setPageUI(page: FlowPage) {
    pnlPageUI.removeAllComponents
    pnlPageUI.addComponent(page.ui())
  }
  
  flowBar.btnPrev addListener block {
    flow.maybeGoPrev match {
      case Some(page) => setPageUI(page)
      case _ => getWindow().showNotification("This is the first page", "Press <Next> or <Finish>", Notification.TYPE_WARNING_MESSAGE)
    }
  }

  flowBar.btnNext addListener block {
    flow.maybeGoNext match {
      case Left(errorMsg) => getWindow().showNotification("Can't go to the next page", errorMsg, Notification.TYPE_ERROR_MESSAGE);
      case Right(Some(page)) => setPageUI(page)
      case _ => getWindow().showNotification("This is the last page", "Press <Finish>", Notification.TYPE_WARNING_MESSAGE)
    }
  }

  flowBar.btnFinish addListener block {
    flow.commit() match {
      case Left(errorMsg) => getWindow().showNotification("Can't commit flow", errorMsg, Notification.TYPE_ERROR_MESSAGE);
      case Right(okMsg) => 
    }
  }

  setPageUI(flow.page)
  addComponents(this, pnlPageUI, flowBar)
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
  // choose file
  // drop down mime type
  // open in
}

object URLDocFlowFactory {
  import scala.util.control.{Exception => E}
  
  def newDocFlow(app: VaadinApplication, parentDoc: DocumentDomainObject): Flow2UI = {
    val urlDocUI = new URLDocUI
    val urlDocValidator = () => E.allCatch.either(new URL(urlDocUI.txtURL.value)) fold (ex => Some(ex.getMessage), url => None)
    val page0 = new FlowPage(() => urlDocUI, urlDocValidator)

    val metaModel = MetaModel(DocumentTypeDomainObject.URL_ID, parentDoc)
    val metaMVC = new MetaMVC(app, metaModel)
    val metaValidator = () => Some("meta is invalid, please fix the following errors..")
    val page1 = new FlowPage(() => metaMVC.view, metaValidator)

    val commit = () => Left("Not implemented")

    new Flow2UI(new Flow(commit, page0, page1))
  }
}

//todo: type Component = UI ??

// http/s, ftp???
class URLDocView extends VerticalLayout {
  val txtURL = new TextField
  // open in ...
}

