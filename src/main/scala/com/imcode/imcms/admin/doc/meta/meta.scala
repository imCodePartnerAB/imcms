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
import imcode.server.document.textdocument.TextDocumentDomainObject
import imcode.server.document.{TextDocumentPermissionSetDomainObject, DocumentDomainObject}
import java.util.{Date, Calendar}
import collection.immutable.ListMap
import com.vaadin.ui.ComponentContainer.{ComponentAttachEvent, ComponentAttachListener}
import com.vaadin.ui._
import com.vaadin.terminal.{Sizeable, ExternalResource}
import admin.access.user.{UserSingleSelectUI, UserSingleSelect, UserSingleSelectDialog, UserSelectDialog}
import api._

/**
 * Doc's meta editor.
 * <p/>
 *
 * @param doc used as editor's initial state, never modified.
 */
class MetaEditor(app: ImcmsApplication, doc: DocumentDomainObject) extends ImcmsServicesSupport {

  private var appearanceSheetOpt = Option.empty[AppearanceSheet]
  private var lifeCycleEditorOpt = Option.empty[LifeCycleEditor]
  private var permissionsSheetOpt = Option.empty[PermissionsSheet]
  private var searchSheetOpt = Option.empty[SearchSheet]
  private var categoriesSheetOpt = Option.empty[CategoriesSheet]
  private var profileSheetOpt = Option.empty[ProfileSheet]


  val ui = letret(new MetaEditorUI) { ui =>
    ui.treeMenu.addItem("Appearance")
    ui.treeMenu.addItem("Life cycle")
    ui.treeMenu.addItem("Permissions")
    ui.treeMenu.addItem("Search")
    ui.treeMenu.addItem("Categories")

    // According to v.4.x.x may be defined for text docs only
    if (doc.isInstanceOf[TextDocumentDomainObject]) ui.treeMenu.addItem("Profile")

    ui.treeMenu.addValueChangeHandler {
      ui.treeMenu.getValue match {
        case "Appearance" =>
          if (appearanceSheetOpt.isEmpty)
            appearanceSheetOpt = Some(
              new AppearanceSheet(
                doc.getMeta, imcmsServices.getDocumentMapper.getI18nMetas(doc.getId).map(m => m.getLanguage -> m).toMap
              )
            )

          ui.pnlMenuItem.setContent(appearanceSheetOpt.get.ui)

        case "Life cycle" =>
          if (lifeCycleEditorOpt.isEmpty) lifeCycleEditorOpt = Some(new LifeCycleEditor(doc.getMeta))

          ui.pnlMenuItem.setContent(lifeCycleEditorOpt.get.ui)

        case "Permissions" =>
          if (permissionsSheetOpt.isEmpty) permissionsSheetOpt =
            Some(
              new PermissionsSheet(ui.getApplication,
              doc,
              ui.getApplication.user)
            )

          ui.pnlMenuItem.setContent(permissionsSheetOpt.get.ui)

        case "Search" =>
          if (searchSheetOpt.isEmpty) searchSheetOpt = Some(new SearchSheet(doc.getMeta))

          ui.pnlMenuItem.setContent(searchSheetOpt.get.ui)

        case "Categories" =>
          if (categoriesSheetOpt.isEmpty) categoriesSheetOpt = Some(new CategoriesSheet(doc.getMeta))

          ui.pnlMenuItem.setContent(categoriesSheetOpt.get.ui)

        case "Profile" =>
          if (profileSheetOpt.isEmpty) profileSheetOpt = Some(new ProfileSheet(doc.asInstanceOf[TextDocumentDomainObject], app.user))

          ui.pnlMenuItem.setContent(profileSheetOpt.get.ui)

        case _ =>
      }
    }
  }

  ui.sp.setSplitPosition(25, Sizeable.UNITS_PERCENTAGE)
  ui.treeMenu.select("Appearance")

  /**
   * Clones the original doc, copies changes into that clone and returns it.
   * todo: ??? return (DocumentDomainObject, i18nMetas: Map[I18nLanguage, I18nMeta]) ???
   */
  def state: DocumentDomainObject = letret(doc.clone()) { dc =>
    for (state <- appearanceSheetOpt.map(_.state)) {
      dc.getMeta.setLanguages(state.enabledLanguages)
      dc.getMeta.setI18nShowMode(state.disabledLanguageShowSetting)
      dc.getMeta.setAlias(state.alias.orNull)
      dc.getMeta.setTarget(state.target)
    }

    for (state <- lifeCycleEditorOpt.map(_.state)) {
      dc.getMeta.setPublicationStatus(state.publicationStatus)
      dc.getMeta.setPublicationStartDatetime(state.publicationStart)
      dc.getMeta.setPublicationEndDatetime(state.publicationEnd.orNull)
      dc.getMeta.setPublicationEndDatetime(state.publicationEnd.orNull)
      dc.getMeta.setPublisherId(state.publisher.map(p => Int box p.getId).orNull)
      //???dc.setVersion(new DocumentVersion() state.versionNo)
      dc.getMeta.setCreatedDatetime(state.created)
      dc.getMeta.setModifiedDatetime(state.modified)
      dc.getMeta.setCreatorId(state.creator.map(c => Int box c.getId).orNull)
      //???dc.getMeta.setModifierId
    }

    for (state <- permissionsSheetOpt.map(_.state)) {
      dc.setRoleIdsMappedToDocumentPermissionSetTypes(state.rolesPermissions)
      dc.getPermissionSets.setRestricted1(state.restrictedOnePermSet)
      dc.getPermissionSets.setRestricted2(state.restrictedTwoPermSet)
      dc.setRestrictedOneMorePrivilegedThanRestrictedTwo(state.isRestrictedOneMorePrivilegedThanRestricted2)
      dc.setLinkedForUnauthorizedUsers(state.isLinkedForUnauthorizedUsers)
      dc.setLinkableByOtherUsers(state.isLinkableByOtherUsers)
    }

    for (state <- searchSheetOpt.map(_.state)) {
      dc.setKeywords(state.keywords)
      dc.setSearchDisabled(state.isExcludeFromInnerSearch)
    }

    for (state <- categoriesSheetOpt.map(_.state)) {
      dc.setCategoryIds(state.categoriesIds)
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
class MetaEditorUI extends VerticalLayout with FullSize with NoMargin {

  val sp = new HorizontalSplitPanel with FullSize
  val treeMenu = new Tree with SingleSelect2[MenuItemId] with NoChildrenAllowed with Immediate
  val pnlMenuItem = new Panel with LightStyle with FullSize

  sp.setFirstComponent(treeMenu)
  sp.setSecondComponent(pnlMenuItem)

  addComponent(sp)
}




class LifeCycleEditor(meta: Meta) extends ImcmsServicesSupport {
  case class State(
    publicationStatus: Document.PublicationStatus,
    publicationStart: Date,
    publicationEnd: Option[Date],
    publisher: Option[UserDomainObject],
    versionNo: Int,
    created: Date,
    modified: Date,
    creator: Option[UserDomainObject],
    modifier: Option[UserDomainObject]
  )

  val ui = letret(new LifeCycleEditorUI) { ui =>
    ui.frmPublication.lytDate.chkEnd.addValueChangeHandler {
      ui.frmPublication.lytDate.calEnd.setEnabled(ui.frmPublication.lytDate.chkEnd.checked)
    }
  }

  revert()

  def revert() {
    // version
    val (versionsNos, defaultVersionNo) = meta.getId match {
      case null =>
        Seq(DocumentVersion.WORKING_VERSION_NO) -> DocumentVersion.WORKING_VERSION_NO

      case id =>
        val versionInfo = imcmsServices.getDocumentMapper.getDocumentVersionInfo(id)
        versionInfo.getVersions.map(_.getNo) -> versionInfo.getDefaultVersion.getNo
    }

    ui.ussCreator.selection = ?(meta.getCreatorId).map(imcmsServices.getImcmsAuthenticatorAndUserAndRoleMapper.getUser(_))
    ui.ussPublisher.selection = ?(meta.getPublisherId).map(imcmsServices.getImcmsAuthenticatorAndUserAndRoleMapper.getUser(_))
    ui.ussModifier.selection = None

    ui.frmPublication.sltVersion.removeAllItems
    versionsNos.foreach(no => ui.frmPublication.sltVersion.addItem(no, no.toString))
    ui.frmPublication.sltVersion.setItemCaption(DocumentVersion.WORKING_VERSION_NO, "doc.version.working".i)
    ui.frmPublication.sltVersion.select(defaultVersionNo)

    // publication status
    ui.frmPublication.sltStatus.select(meta.getPublicationStatus)

    // dates
    ui.frmPublication.lytDate.calStart.value = ?(meta.getPublicationStartDatetime) getOrElse new Date
    //ui.frmPublication.lytDate.calEnd.setReadOnly(false)
    ui.frmPublication.lytDate.calEnd.value = meta.getPublicationEndDatetime
    ui.frmPublication.lytDate.chkEnd.checked = meta.getPublicationEndDatetime != null

    // todo: ??? remember lytDate.chkEnd date when uncheked???
  }

  def state = State(
    ui.frmPublication.sltStatus.value,
    ui.frmPublication.lytDate.calStart.value,
    ui.frmPublication.lytDate.calEnd.valueOpt,
    ui.ussCreator.selection,
    ui.frmPublication.sltVersion.value.intValue,
    ui.frmMaintenance.dCreated.calDate.value,
    ui.frmMaintenance.dModified.calDate.value,
    ui.ussCreator.selection,
    ui.ussModifier.selection
  )
}


class LifeCycleEditorUI extends VerticalLayout with Spacing with FullWidth {
  setMargin(true, false, false, false)

  val ussPublisher = new UserSingleSelect
  val ussCreator = new UserSingleSelect
  val ussModifier = new UserSingleSelect

  val frmPublication = new Form with FullWidth {
    setCaption("Publication")
    getLayout.setMargin(false, true, true, true)

    val sltStatus = new Select("Status") with SingleSelect2[Document.PublicationStatus] with NoNullSelection {
      addItem(Document.PublicationStatus.NEW, "New")
      addItem(Document.PublicationStatus.APPROVED, "Approved")
      addItem(Document.PublicationStatus.DISAPPROVED, "Disapproved")
    }

    val sltVersion = new Select("Version") with SingleSelect2[DocVersionNo] with NoNullSelection

    val lytDate = new GridLayout(2, 2) with Spacing {
      setCaption("Date")
      val calStart = new PopupDateField with MinuteResolution with Now
      val calEnd = new PopupDateField with MinuteResolution
      val chkStart = new CheckBox("start") with Checked with ReadOnly // decoration, always read-only
      val chkEnd = new CheckBox("end") with Immediate with AlwaysFireValueChange

      addComponents(this, chkStart, calStart, chkEnd, calEnd)
    }

    ussPublisher.ui.setCaption("Publisher")

    addComponents(getLayout, sltStatus, sltVersion, lytDate, ussPublisher.ui)
  }

  val frmMaintenance = new Form with FullSize {
    setCaption("Maintenace")
    getLayout.setMargin(false, true, false, true)

    class DateUI(caption: String, ussUI: UserSingleSelectUI) extends HorizontalLayoutUI(caption, margin = false) {
      val calDate = new PopupDateField with MinuteResolution with Now
      val lblBy = new Label("by") with UndefinedSize

      addComponents(this, calDate, lblBy, ussUI)
    }

    val dCreated = new DateUI("Created", ussCreator.ui)
    val dModified = new DateUI("Modified", ussModifier.ui)

    addComponents(getLayout, dCreated, dModified)
  }

  addComponents(this, frmPublication, frmMaintenance)
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
 *
 * Used to customizes doc's L&F:
 * -enabled languages
 * -i18n metas in enabled languages
 * -disabled language show setting {@link Meta.DisabledLanguageShowSetting}
 *
 * -alias
 * -link target (_self | _top | _blank)
 */
class AppearanceSheet(meta: Meta, i18nMetas: Map[I18nLanguage, I18nMeta]) extends ImcmsServicesSupport {

  case class State(
    i18nMetas: Map[I18nLanguage, I18nMeta],
    enabledLanguages: Set[I18nLanguage],
    disabledLanguageShowSetting: Meta.DisabledLanguageShowSetting,
    alias: Option[String],
    target: String
  )

  // i18nMetas sorted by language default (always first) and native name
  private val i18nMetasUIs: Seq[(I18nLanguage, CheckBox, I18nMetaEditorUI)] = locally {
    val defaultLanguage = imcmsServices.getI18nSupport.getDefaultLanguage
    val languages = imcmsServices.getI18nSupport.getLanguages.sortWith {
      case (l1, _) if l1 == defaultLanguage => true
      case (_, l2) if l2 == defaultLanguage => false
      case (l1, l2) => l1.getNativeName < l2.getNativeName
    }

    for (language <- languages)
    yield {
      val chkLanguage = new CheckBox(language.getNativeName) with Immediate with AlwaysFireValueChange
      val i18nMetaEditorUI = new I18nMetaEditorUI

      chkLanguage.addValueChangeHandler {
        i18nMetaEditorUI.setVisible(chkLanguage.isChecked)
      }

      chkLanguage.setIcon(new ExternalResource("/imcms/images/icons/flags_iso_639_1/%s.gif" format language.getCode))

      (language, chkLanguage, i18nMetaEditorUI)
    }
  }

  val ui = new AppearanceSheetUI { ui =>
    ui.frmLanguages.cbShowMode.addItem(Meta.DisabledLanguageShowSetting.DO_NOT_SHOW, "Show 'Not found' page")
    ui.frmLanguages.cbShowMode.addItem(Meta.DisabledLanguageShowSetting.SHOW_IN_DEFAULT_LANGUAGE, "Show document in default language")

    for ((_, chkLanguage, i18nMetaEditorUI) <- i18nMetasUIs) {
      addComponents(ui.frmLanguages.lytI18nMetas, chkLanguage, i18nMetaEditorUI)
    }

    override def attach() {
      super.attach()
      revert()
    }
  } // ui


  /**
   * Default language checkbox is always checked (hence its associated i18n meta form is always visible)
   */
  def revert() {
    val defaultLanguage = imcmsServices.getI18nSupport.getDefaultLanguage

    for ((language, chkBox, i18nMetaEditorUI) <- i18nMetasUIs) {
      val isDefaultLanguage = language == defaultLanguage

      chkBox.setReadOnly(false)
      chkBox.checked = isDefaultLanguage || meta.getLanguages.contains(language)
      chkBox.setReadOnly(isDefaultLanguage)

      i18nMetas.get(language) match {
        case Some(i18nMeta) =>
          i18nMetaEditorUI.txtTitle.value = i18nMeta.getHeadline
          i18nMetaEditorUI.taMenuText.value = i18nMeta.getMenuText
          i18nMetaEditorUI.embLinkImage.value = i18nMeta.getMenuImageURL

        case _ =>
          i18nMetaEditorUI.txtTitle.clear
          i18nMetaEditorUI.taMenuText.clear
          i18nMetaEditorUI.embLinkImage.clear
      }
    }

    val alias = ?(meta.getAlias)
    ui.frmAlias.txtAlias.setInputPrompt(?(meta.getId).map(_.toString).orNull)
    ui.frmAlias.txtAlias.value = alias.getOrElse("")
    ui.frmLanguages.cbShowMode.select(meta.getI18nShowSetting)

    for ((target, targetCaption) <- ListMap("_self" -> "Same frame", "_blank" -> "New window", "_top" -> "Replace all")) {
      ui.frmLinkTarget.cbTarget.addItem(target, targetCaption)
    }

    // todo:?? add custom case class Target(id: String, boolean: Custom), so can check on override
    // legacy target support: up to v 6.x it was possible to define custom target for a doc
    // if this doc has custom target, then adds this target to the targets combo-box as a last item
    val target = meta.getTarget match {
      case null => ui.frmLinkTarget.cbTarget.firstItemIdOpt.get
      case target =>
        ui.frmLinkTarget.cbTarget.itemIds.find(_ == target.toLowerCase) match {
          case Some(predefinedTarget) => predefinedTarget
          case _ =>
            ui.frmLinkTarget.cbTarget.addItem(target, "Other frame: %s".format(target))
            target
        }
    }

    ui.frmLinkTarget.cbTarget.select(target)
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
    ui.frmLanguages.cbShowMode.value,
    ui.frmAlias.txtAlias.trimOpt,
    ui.frmLinkTarget.cbTarget.value
  )
}


class AppearanceSheetUI extends VerticalLayout with Spacing with FullWidth {
  val frmLanguages = new Form(new VerticalLayout with Spacing) {
    setCaption("Languages")
    setMargin(true, false, false, false)
    getLayout.setMargin(true)

    val lytI18nMetas = new VerticalLayout with Spacing with FullWidth
    val cbShowMode = new ComboBox("When language is disabled") with SingleSelect2[Meta.DisabledLanguageShowSetting] with NoNullSelection

    private val lytShowMode = new FormLayout with FullWidth
    lytShowMode.addComponent(cbShowMode)

    addComponents(getLayout, lytI18nMetas, lytShowMode)
  }


  val frmLinkTarget = new Form with FullWidth {
    setCaption("Link action")
    getLayout.setMargin(true)

    val cbTarget = new ComboBox("Show in") with SingleSelect2[String] with NoNullSelection

    getLayout.addComponent(cbTarget)
  }

  val frmAlias = new Form(new HorizontalLayoutUI with FullWidth) with FullWidth {
    setCaption("Alias")
    getLayout.setMargin(false, true, true, true)

    val lblContextURL = new Label("http://host/") with UndefinedSize
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

  addComponents(this, frmLanguages, frmLinkTarget, frmAlias)
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


