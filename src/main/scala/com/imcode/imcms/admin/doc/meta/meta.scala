package com.imcode
package imcms
package admin.doc.meta

import permissions.{PermissionsSheet}
import profile.ProfileSheet
import scala.collection.JavaConversions._
import scala.collection.breakOut

import collection.mutable.{Map => MMap}
import imcode.server.Imcms
import imcode.server.user.UserDomainObject

import vaadin._
import admin.access.user.UserSearchDialog
import api._
import com.vaadin.terminal.ExternalResource
import java.util.Calendar
import com.vaadin.ui._
import imcode.server.document.textdocument.TextDocumentDomainObject
import imcode.server.document.{TextDocumentPermissionSetDomainObject, DocumentDomainObject}

/**
 * Doc's meta editor.
 * <p/>
 *
 * @param doc used as editor's initial state, never modified.
 */
class MetaEditor(app: ImcmsApplication, doc: DocumentDomainObject) extends ImcmsServicesSupport {

  private var searchSheetOpt = Option.empty[SearchSheet]
  private var categoriesSheetOpt = Option.empty[CategoriesSheet]
  private var appearanceSheetOpt = Option.empty[AppearanceSheet]
  private var permissionsSheetOpt = Option.empty[PermissionsSheet]
  private var profileSheetOpt = Option.empty[ProfileSheet]


  val ui = letret(new MetaEditorUI) { ui =>
    ui.sheets.addItem("Main")
    ui.sheets.addItem("Appearance")
    ui.sheets.addItem("Permissions")
    ui.sheets.addItem("Search")
    ui.sheets.addItem("Categories")

    // According to v.4.x.x may be defined for text docs only
    if (doc.isInstanceOf[TextDocumentDomainObject]) ui.sheets.addItem("Profile")

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
              new PermissionsSheet(ui.getApplication,
              doc,
              ui.getApplication.user)
            )

          ui.sheet.setContent(permissionsSheetOpt.get.ui)

        case "Search" =>
          if (searchSheetOpt.isEmpty) searchSheetOpt = Some(new SearchSheet(doc.getMeta))

          ui.sheet.setContent(searchSheetOpt.get.ui)

        case "Categories" =>
          if (categoriesSheetOpt.isEmpty) categoriesSheetOpt = Some(new CategoriesSheet(doc.getMeta))

          ui.sheet.setContent(categoriesSheetOpt.get.ui)

        case "Profile" =>
          if (profileSheetOpt.isEmpty) profileSheetOpt = Some(new ProfileSheet(doc.asInstanceOf[TextDocumentDomainObject], app.user))

          ui.sheet.setContent(profileSheetOpt.get.ui)

        case _ =>
      }
    }
  }

  ui.sheets.select("Main")

  /**
   * Clones the original doc, copies changes into that clone and returns it.
   * todo: ??? return (DocumentDomainObject, i18nMetas: Map[I18nLanguage, I18nMeta]) ???
   */
  def state: DocumentDomainObject = letret(doc.clone()) { dc =>
    for (state <- searchSheetOpt.map(_.state)) {
      dc.setKeywords(state.keywords)
      dc.setSearchDisabled(state.isExcludeFromInnerSearch)
    }

    for (state <- categoriesSheetOpt.map(_.state)) {
      dc.setCategoryIds(state.categoriesIds)
    }

    for (state <- appearanceSheetOpt.map(_.state)) {
      dc.getMeta.setLanguages(state.enabledLanguages)
      dc.getMeta.setI18nShowMode(state.disabledLanguageShowSetting)
      // todo: return i18nMetas
    }

    for (state <- permissionsSheetOpt.map(_.state)) {
      dc.setRoleIdsMappedToDocumentPermissionSetTypes(state.rolesPermissions)
      dc.getPermissionSets.setRestricted1(state.restrictedOnePermSet)
      dc.getPermissionSets.setRestricted2(state.restrictedTwoPermSet)
      dc.setRestrictedOneMorePrivilegedThanRestrictedTwo(state.isRestrictedOneMorePrivilegedThanRestricted2)
      dc.setLinkedForUnauthorizedUsers(state.isLinkedForUnauthorizedUsers)
      dc.setLinkableByOtherUsers(state.isLinkableByOtherUsers)
    }

//    ui.cbDefaultTemplate.value,
//    restrictedOnePermSet, // ??? clone
//    restrictedTwoPermSet, // ??? clone
//    ui.cbRestrictedOneDefaultTemplate,
//    ui.cbRestrictedTwoDefaultTemplate
    dc match {
      case tdc: TextDocumentDomainObject =>
        for (state <- profileSheetOpt.map(_.state)) {
          tdc.setDefaultTemplateId(state.defaultTemplate)
          tdc.getPermissionSetsForNewDocuments.setRestricted1(state.restrictedOnePermSet)
          tdc.getPermissionSetsForNewDocuments.setRestricted2(state.restrictedTwoPermSet)
          tdc.setDefaultTemplateIdForRestricted1(state.restrictedOneTemplate)
          tdc.setDefaultTemplateIdForRestricted2(state.restrictedTwoTemplate)
        }
      case _ =>
    }
  }
}


/**
 * Editor UI's main component is a horizontal split panel.
 * -Left component - navigation tree.
 * -Right component - scrollable panel.
 */
private class MetaEditorUI extends VerticalLayout with FullSize with NoMargin {

  val sp = new HorizontalSplitPanel with FullSize
  val sheets = new Tree with Immediate
  val sheet = new Panel with LightStyle with FullSize

  sp.setSecondComponent(sheet)
  sp.setFirstComponent(sheets)

  addComponent(sp)
}


/**
 * Doc's categories (editor).
 */
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


/**
 * Doc's search settings (editor).
 */
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


/**
 * Doc's appearance settings.
 * Used to customizes doc's L&F in system predefined languages.
 */
class AppearanceSheet(meta: Meta, i18nMetas: Map[I18nLanguage, I18nMeta]) extends ImcmsServicesSupport {

  case class State(
    i18nMetas: Map[I18nLanguage, I18nMeta],
    enabledLanguages: Set[I18nLanguage],
    disabledLanguageShowSetting: Meta.DisabledLanguageShowSetting
    //, linkOpenSettings???
  )

  private val i18nMetasUIs: Seq[(I18nLanguage, CheckBox, I18nMetaEditorUI)] =
    for (language <- imcmsServices.getI18nSupport.getLanguages)
    yield {
      val chkLanguage = new CheckBox(language.getNativeName) with Immediate
      val i18nMetaEditorUI = new I18nMetaEditorUI

      chkLanguage.addValueChangeHandler {
        i18nMetaEditorUI.setVisible(chkLanguage.isChecked)
      }

      chkLanguage.setIcon(new ExternalResource("/imcms/images/icons/flags_iso_639_1/%s.gif" format language.getCode))

      (language, chkLanguage, i18nMetaEditorUI)
    }

  val ui = letret(new AppearanceSheetUI) { ui =>
    ui.frmLanguages.cbShowMode.addItem(Meta.DisabledLanguageShowSetting.DO_NOT_SHOW, "Show 'Not found' page")
    ui.frmLanguages.cbShowMode.addItem(Meta.DisabledLanguageShowSetting.SHOW_IN_DEFAULT_LANGUAGE, "Show document in default language")

    for ((_, chkLanguage, i18nMetaEditorUI) <- i18nMetasUIs)
      addComponents(ui.frmLanguages.lytI18nMetas, chkLanguage, i18nMetaEditorUI)
  } // ui


  revert()

  def revert() {
    ui.frmLanguages.cbShowMode.select(meta.getI18nShowSetting)
    for ((language, chkBox, i18nMetaEditorUI) <- i18nMetasUIs) {
      chkBox.checked = meta.getLanguages.contains(language)

      i18nMetas.get(language) match {
        case Some(i18nMeta) =>
          i18nMetaEditorUI.txtTitle.value = i18nMeta.getHeadline
          i18nMetaEditorUI.taMenuText.value = i18nMeta.getMenuText
          i18nMetaEditorUI.embLinkImage.value = i18nMeta.getMenuImageURL

        case _ =>
          i18nMetaEditorUI.txtTitle.value = ""
          i18nMetaEditorUI.taMenuText.value = ""
          i18nMetaEditorUI.embLinkImage.value = ""
      }
    }

    //set target
    //ui.frmLinkTarget.ogShowIn.select(meta.getTarget)
  }

  def state = State(
    i18nMetasUIs.map {
      case (language, chkBox, i18nMetaEditorUI) =>
        language -> letret(new I18nMeta) { i18nMeta =>
          i18nMeta.setId(i18nMetas.get(language).map(_.getId).orNull)
          i18nMeta.setDocId(meta.getId)
          i18nMeta.setLanguage(language)
          i18nMeta.setHeadline(i18nMetaEditorUI.txtTitle.trim)
          i18nMeta.setMenuImageURL(i18nMetaEditorUI.embLinkImage.trim)
        }
    } (breakOut),
    i18nMetasUIs.collect { case (language, chkBox, _) if chkBox.isChecked => language }(breakOut),
    ui.frmLanguages.cbShowMode.value
  )
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


