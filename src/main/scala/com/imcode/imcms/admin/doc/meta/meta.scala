package com.imcode
package imcms
package admin.doc.meta

import scala.collection.JavaConversions._

import permissions.PermissionsSheet
import collection.mutable.{Map => MMap}
import imcode.server.Imcms
import imcode.server.user.UserDomainObject

import imcode.server.document.{DocumentDomainObject}
import vaadin._
import admin.access.user.UserSearchDialog
import api._
import com.vaadin.terminal.ExternalResource
import java.util.Calendar
import com.vaadin.ui._

//trait PropertiesDialog { this: OKDialog =>
//  mainUI =
//}

// properties editor
// properties editor UI???

// MetaEditor

class Properties(doc: DocumentDomainObject) extends ImcmsServicesSupport {

  private var searchSheepOpt = Option.empty[SearchSheet]
  private var categoriesSheetOpt = Option.empty[CategoriesSheet]
  private var appearanceSheetOpt = Option.empty[AppearanceSheet]
  private var permissionsSheetOpt = Option.empty[PermissionsSheet]


  val ui = letret(new PropertiesUI) { ui =>
    ui.sheets.addItem("Main")
    ui.sheets.addItem("Appearance")
    ui.sheets.addItem("Permissions")
    ui.sheets.addItem("Search")
    ui.sheets.addItem("Categories")

    ui.sheets.addValueChangeHandler {
      ui.sheets.getValue match {
        case "Main" =>
          ui.sheet.setContent(new MainSheetUI)

        case "Appearance" =>
          if (appearanceSheetOpt.isEmpty)
            appearanceSheetOpt = Some(
              new AppearanceSheet(
                doc.getMeta, imcmsServices.getDocumentMapper.getI18nMetas(doc.getId).map(m => m.getLanguage -> m).toMap
              )
            )

          ui.sheet.setContent(appearanceSheetOpt.get.ui)

        case "Permissions" =>
          if (permissionsSheetOpt.isEmpty) permissionsSheetOpt =
            Some(
              new PermissionsSheet(ui.getApplication.asInstanceOf[ImcmsApplication],
              doc.getMeta,
              ui.getApplication.asInstanceOf[ImcmsApplication].user)
            )

          ui.sheet.setContent(permissionsSheetOpt.get.ui)

        case "Search" =>
          if (searchSheepOpt.isEmpty) searchSheepOpt = Some(new SearchSheet(doc.getMeta))

          ui.sheet.setContent(searchSheepOpt.get.ui)

        case "Categories" =>
          if (categoriesSheetOpt.isEmpty) categoriesSheetOpt = Some(new CategoriesSheet(doc.getMeta))

          ui.sheet.setContent(categoriesSheetOpt.get.ui)

        case _ =>
      }
    }
  }

  ui.sheets.select("Main")
}



class PropertiesUI extends VerticalLayout with FullSize with NoMargin {
  val sp = new HorizontalSplitPanel with FullSize
  val sheets = new Tree with Immediate
  val sheet = new Panel with LightStyle with FullSize

  sp.setSecondComponent(sheet)
  sp.setFirstComponent(sheets)
  addComponent(sp)
}


class CategoriesSheet(meta: Meta) extends ImcmsServicesSupport {
  case class State(categoriesIds: Set[CategoryId])

  private val initialState = State(meta.getCategoryIds.toSet)

  private val typeCategoriesUIs: Seq[(CheckBox with ExposeValueChange, MultiSelectBehavior[CategoryId])] =
    for {
      cType <- imcmsServices.getCategoryMapper.getAllCategoryTypes.toSeq
      categories = imcmsServices.getCategoryMapper.getAllCategoriesOfType(cType)
      if categories.nonEmpty
    } yield {
      val chkCType = new CheckBox(cType.getName) with ExposeValueChange with Immediate
      val sltCategories =
        if (cType.isMultiselect) new TwinColSelect with MultiSelectBehavior[CategoryId]
        else new ComboBox with MultiSelectBehavior[CategoryId] with NoNullSelection

      categories foreach { category =>
        sltCategories.addItem(category.getId, category.getName)
      }

      chkCType.addValueChangeHandler {
        sltCategories.setVisible(chkCType.isChecked)
      }

      chkCType -> sltCategories
    }

  val ui = letret(new GridLayout(2, 1) with Spacing) { ui =>
    for ((chkCType, sltCategories) <- typeCategoriesUIs) {
      addComponents(ui, chkCType, sltCategories)
    }
  }

  revert()

  def revert() {
    for ((chkCType, sltCategories) <- typeCategoriesUIs) {
      chkCType.uncheck
      sltCategories.value = Nil

      for (categoryId <- sltCategories.itemIds if initialState.categoriesIds(categoryId)) {
        sltCategories.select(categoryId)
        chkCType.check
      }

      chkCType.fireValueChange()
    }
  }

  def state = State(
    typeCategoriesUIs.collect {
      case (chkCType, sltCategories) if chkCType.isChecked => sltCategories.value
    }.flatten.toSet
  )

  def isModified = state != initialState
}


// param: editable = true/false ???
class SearchSheet(meta: Meta) {
  case class State(keywords: Set[Keyword], isExcludeFromInnerSearch: Boolean)

  private val initialState = State(meta.getKeywords.map(_.toLowerCase).toSet, false)

  val ui = letret(new SearchSheetUI) { ui =>
    import ui.lytKeywords.{btnAdd, btnRemove, txtKeyword, lstKeywords}

    btnAdd.addClickHandler {
      txtKeyword.trim.toLowerCase match {
        case value if value.length > 0 && lstKeywords.getItem(value) == null =>
          setKeywords(lstKeywords.itemIds.toSet + value)

        case _ =>
      }

      txtKeyword.value = ""
    }

    btnRemove.addClickHandler {
      whenSelected(lstKeywords) { _ foreach (lstKeywords removeItem _) }
    }

    lstKeywords.addValueChangeHandler {
      lstKeywords.value.toSeq match {
        case Seq(value) => txtKeyword.value = value
        case Seq(_, _, _*) => txtKeyword.value = ""
        case _ =>
      }
    }
  } // ui

  revert()


  private def setKeywords(keywords: Set[Keyword]) {
    ui.lytKeywords.lstKeywords.itemIds = keywords.map(_.toLowerCase).toSeq.sorted
  }


  def revert() {
    setKeywords(initialState.keywords)
    ui.chkExcludeFromInternalSearch.checked = initialState.isExcludeFromInnerSearch
  }

  def state = State(ui.lytKeywords.lstKeywords.itemIds.toSet, ui.chkExcludeFromInternalSearch.isChecked)

  def isModified = state != initialState

  //def validate() = ???
  //def sync() = ???
}


class SearchSheetUI extends FormLayout with UndefinedSize {

  val chkExcludeFromInternalSearch = new CheckBox("Exclude this page from internal search")
  val lytKeywords = new GridLayout(3,2) with UndefinedSize {
    setCaption("Keywords")

    val lstKeywords = new ListSelect with MultiSelect2[Keyword] with Immediate {
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
  }

  addComponents(this, lytKeywords, chkExcludeFromInternalSearch)
}


class AppearanceSheet(meta: Meta, i18nMetas: Map[I18nLanguage, I18nMeta]) extends ImcmsServicesSupport {
  private val i18nMetasUIs: Seq[(CheckBox, I18nMetaEditorUI)] =
    for (language <- imcmsServices.getI18nSupport.getLanguages)
    yield {
      val chkLanguage = new CheckBox(language.getName) with Immediate
      val i18nMetaEditorUI = new I18nMetaEditorUI

      chkLanguage.addValueChangeHandler {
        i18nMetaEditorUI.setVisible(chkLanguage.isChecked)
      }

      chkLanguage.setIcon(new ExternalResource("/imcms/images/icons/flags_iso_639_1/%s.gif" format language.getCode))

      chkLanguage -> i18nMetaEditorUI
    }

  val ui = letret(new AppearanceSheetUI) { ui =>
    ui.frmLanguages.cbShowMode.addItem(Meta.DisabledLanguageShowSetting.DO_NOT_SHOW, "Show 'Not found' page")
    ui.frmLanguages.cbShowMode.addItem(Meta.DisabledLanguageShowSetting.SHOW_IN_DEFAULT_LANGUAGE, "Show document in default language")

    for ((chkLanguage, i18nMetaEditorUI) <- i18nMetasUIs)
      addComponents(ui.frmLanguages.lytI18nMetas, chkLanguage, i18nMetaEditorUI)
  } // ui


  revert()

  def revert() {
    ui.frmLanguages.cbShowMode.select(meta.getI18nShowSetting)
  }
}


class AppearanceSheetUI extends VerticalLayout with Spacing with FullWidth {
  val frmLanguages = new Form(new VerticalLayout with Spacing) {
    setCaption("Languages")
    setMargin(true, false, false, false)
    getLayout.setMargin(true)

    val lytI18nMetas = new VerticalLayout with Spacing with FullWidth
    val cbShowMode = new ComboBox("When requested language is inactive") with SingleSelect2[Meta.DisabledLanguageShowSetting] with NoNullSelection

    addComponents(getLayout, lytI18nMetas, cbShowMode)
  }


  val frmLinkTarget = new Form with FullWidth {
    setCaption("Link action")
    getLayout.setMargin(true)

    val ogShowIn = new OptionGroup(null, Seq("Same frame", "New window", "Replace all", "Other frame:")) {
      addStyleName("horizontalgroup")
    }
    val txtFrameName = new TextField

    addComponents(getLayout, ogShowIn, txtFrameName)
  }

  addComponents(this, frmLanguages, frmLinkTarget)
}



////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/** Meta model */
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
    val versionInfo = Imcms.getServices.getDocumentMapper.getDocumentLoaderCachingProxy.getDocVersionInfo(id)
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

class MetaEditor(val application: ImcmsApplication, val model: MetaModel) {

  val ui = letret(new MetaUI) { ui =>
    // affects model
//    ui.lytPublication.btnChoosePublisher addClickHandler {
//      application.initAndShow(new OkCancelDialog("Choose publisher") with UserSearchDialog) { dlg =>
//        dlg.wrapOkHandler {
//          dlg.search.selection match {
//            case Seq(user) =>
//              model.meta.setPublisherId(user.getId)
//              ui.lytPublication.lblPublisherName.value = user.getLoginName
//
//            case _ =>
//              model.meta.setPublisherId(null)
//              ui.lytPublication.lblPublisherName.value = "No publisher selected"
//          }
//        }
//      }
//    }

    // does NOT alter meta - only reads its values
//    let(ui.lytPublication) { lyt =>
//      lyt.chkEnd addClickHandler {
//        lyt.chkEnd.booleanValue match {
//          case true =>
//            lyt.calEnd.setEnabled(true)
//            lyt.calEnd.value = model.meta.getPublicationEndDatetime
//          case false =>
//            lyt.calEnd.value = null
//            lyt.calEnd.setEnabled(false)
//        }
//      }
//
//      // fire event
//      lyt.chkEnd.fireClick()
//    }
  }

  /**
   * Validates data and populates model with values.
   * @returns Some(error) in case of a validation error or None.
   */
  def validate(): Option[String] = {
//    ui.lytI18n.tsI18nMetas.getComponentIterator foreach {
//      case i18nMetaUI: I18nMetaLyt with DataType[I18nLanguage] =>
//        let(model.i18nMetas(i18nMetaUI.data)) { i18nMeta =>
//          i18nMeta.setHeadline(i18nMetaUI.txtTitle.value)
//          i18nMeta.setMenuText(i18nMetaUI.txtMenuText.value)
//          i18nMeta.setMenuImageURL(i18nMetaUI.embLinkImage.value)
//        }
//    }

    ui.lytIdentity.txtAlias.value.trim match {
      case "" => model.meta.removeAlis
      case alias =>
        // todo: check alias
        model.meta.setAlias(alias)
    }

    //model.meta.setPublicationStatus(ui.lytPublication.sltStatus.value)

    let(model.meta.getLanguages) { metaLanguages =>
      metaLanguages.clear
      for ((language, enabled) <- model.languages if enabled) metaLanguages.add(language)
    }

//    model.meta.setPublicationStartDatetime(ui.lytPublication.calStart.value)
//    model.meta.setPublicationEndDatetime(
//      if (ui.lytPublication.chkEnd.booleanValue) ui.lytPublication.calEnd.value
//      else null
//    )
//
//    model.meta.setSearchDisabled(ui.lytSearch.chkExclude.value)
//    model.meta.setLinkedForUnauthorizedUsers(ui.lytLink.chkShowToUnauthorizedUser.value)
//    model.meta.setTarget(if (ui.lytLink.chkOpenInNewWindow.booleanValue) "_top" else "_self")

    None
  }
}



/**
 * I18nMeta editor.
 */
class I18nMetaEditorUI extends FormLayout with FullWidth {
  val txtTitle = new TextField("Title") with FullWidth
  val taMenuText = new TextArea("Menu text") with FullWidth {
    setRows(3)
  }

  val embLinkImage = new TextField("Link image") with FullWidth
  // val chkEnabled ???
  // val flag, default???

  addComponents(this, txtTitle, taMenuText, embLinkImage)
}


class MainSheetUI extends VerticalLayout with Spacing with FullWidth {
  setMargin(true, false, false, false)

  val frmPublication = new Form with FullWidth {
    setCaption("Publication")
    getLayout.setMargin(false, true, true, true)

    val sltStatus = new Select("Status") with XSelect[Document.PublicationStatus] with NoNullSelection {
      addItem(Document.PublicationStatus.NEW, "New")
      addItem(Document.PublicationStatus.APPROVED, "Approved")
      addItem(Document.PublicationStatus.DISAPPROVED, "Disapproved")
      select(Document.PublicationStatus.DISAPPROVED)
    }

    val sltVersion = new Select("Version") with NoNullSelection {
      addItem("Working")
      select("Working")
    }

    val lytDate = new GridLayout(2, 2) with Spacing {
      setCaption("Date")
      val calStart = new PopupDateField with MinuteResolution with Now
      val calEnd = new PopupDateField with MinuteResolution
      val chkStart = new CheckBox("start") with Checked with ReadOnly // decoration, always read-only
      val chkEnd = new CheckBox("end") with Immediate with ExposeFireClick

      addComponents(this, chkStart, calStart, chkEnd, calEnd)
    }

    val lytPublisher = new HorizontalLayoutUI("Publisher", margin = false) with UndefinedSize {
      val lblPublisherName = new Label("No publisher selected") with UndefinedSize
      val btnChoosePublisher = new Button("...") with LinkStyle
      addComponents(this, lblPublisherName, btnChoosePublisher)
    }

    addComponents(getLayout, sltStatus, sltVersion, lytDate, lytPublisher)
  }

  val frmAlias = new Form(new HorizontalLayoutUI with FullWidth) with FullWidth {
    setCaption("Alias")
    getLayout.setMargin(false, true, true, true)

    val lblContextURL = new Label("http://host:port/") with UndefinedSize
    val txtAlias = new TextField with FullWidth {
      setInputPrompt("alternate page name")
    }
    val btnCheck = new Button("check") with SmallStyle

    let(getLayout.asInstanceOf[HorizontalLayout]) { lyt =>
      addComponents(lyt, lblContextURL, txtAlias, btnCheck)
      lyt.setExpandRatio(txtAlias, 1.0f)
      forlet(lblContextURL, btnCheck) {
        lyt.setComponentAlignment(_, Alignment.MIDDLE_LEFT)
      }
    }
  }

  val frmMaintenance = new Form with FullSize {
    setCaption("Maintenace")
    getLayout.setMargin(false, true, false, true)

    class DateUI(caption: String) extends HorizontalLayoutUI(caption, margin = false) {
      val calDate = new PopupDateField with MinuteResolution with Now
      val lblBy = new Label("by") with UndefinedSize
      val btnChooseUser = new Button("...") with LinkStyle

      addComponents(this, calDate, lblBy, btnChooseUser)
    }

    val dCreated = new DateUI("Created")
    val dModified = new DateUI("Modified")

    addComponents(getLayout, dCreated, dModified)
  }

  addComponents(this, frmAlias, frmPublication, frmMaintenance)
}


/**
 * Keywords modal dialog content.
 */
class KeywordsDialogContent(keywords: Seq[String] = Nil) extends GridLayout(3,2) with Spacing {

  type ItemIds = JCollection[String]

  val lstKeywords = new ListSelect with ValueType[ItemIds] with ItemIdType[String] with MultiSelect with NullSelection with Immediate {
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

  btnAdd addClickHandler {
    txtKeyword.value.trim.toLowerCase match {
      case value if value.length > 0 && lstKeywords.getItem(value) == null =>
        setKeywords(value :: lstKeywords.getItemIds.asInstanceOf[ItemIds].toList)
      case _ =>
    }

    txtKeyword setValue ""
  }

  btnRemove addClickHandler {
    whenSelected(lstKeywords) { _ foreach (lstKeywords removeItem _) }
  }

  lstKeywords addValueChangeHandler {
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
 * Categories modal dialog content.
 *
 * Categories are added dynamically in editor and grouped by their type.
 * Single-choice categories appear in a Select component, multi-choice in TwinSelect component.
 * Components (Select and TwinSelect) captions is set to type name.
 *
 * Dialog containing this content must have defined size.
 */
class CategoriesDialogContent extends Panel(new FormLayout with UndefinedSize) with LightStyle with Scrollable with UndefinedSize


/**
 * Meta (doc info) ui.
 */
class MetaUI extends FormLayout /*with UndefinedSize*/ with Margin {

  val lytIdentity = new HorizontalLayout with UndefinedSize with Spacing {
    val txtId = new TextField("Document Id") with Disabled
    val txtName = new TextField("Name")
    val txtAlias = new TextField("Alias")

    setCaption("Identity")
    addComponents(this, txtId, txtName, txtAlias)
  }

  val lytI18n = new VerticalLayout {//with UndefinedSize {
    val tsI18nMetas = new TabSheet// with UndefinedSize
    val btnSettings = new Button("Configure...") with LinkStyle

    setCaption("Appearence")
    addComponents(this, tsI18nMetas, btnSettings)
  }

  val lytLink = new VerticalLayout with UndefinedSize with Spacing {
    val chkOpenInNewWindow = new CheckBox("Open in new window")
    val chkShowToUnauthorizedUser = new CheckBox("Show to unauthorized user")

    setCaption("Link/menu item")
    addComponents(this, chkOpenInNewWindow, chkShowToUnauthorizedUser)
  }

  val lytSearch = new VerticalLayout with UndefinedSize with Spacing {
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

  val lytCategories = new HorizontalLayout with UndefinedSize with Spacing {
    val lblCategories = new Label("Categories")
    val txtCategories = new TextField with Disabled { setColumns(30) }
    val btnEdit = new Button("Edit...") with LinkStyle

    addComponents(this, lblCategories, txtCategories, btnEdit)
  }

  forlet(lytIdentity, lytI18n, lytLink, lytSearch, lytCategories) { c =>
    c.setMargin(true)
    addComponent(c)
  }
}