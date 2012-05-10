package com.imcode
package imcms
package admin.doc.meta

import _root_.com.imcode.imcms.admin.doc.meta.permissions.{PermissionsEditor}
import _root_.com.imcode.imcms.admin.doc.meta.profile.ProfileEditor

import _root_.scala.collection.JavaConversions._
import _root_.scala.collection.breakOut

import _root_.imcode.server.user.UserDomainObject

import _root_.com.imcode.imcms.vaadin._
import _root_.imcode.server.document.textdocument.TextDocumentDomainObject
import _root_.imcode.server.document.{TextDocumentPermissionSetDomainObject, DocumentDomainObject}
import _root_.java.util.{Date, Calendar}
import _root_.scala.collection.immutable.ListMap
import _root_.com.vaadin.ui.ComponentContainer.{ComponentAttachEvent, ComponentAttachListener}
import _root_.com.vaadin.ui._
import _root_.com.imcode.imcms.admin.access.user.{UserSingleSelectUI, UserSingleSelect, UserSingleSelectDialog, UserSelectDialog}
import _root_.com.imcode.imcms.api._
import _root_.com.imcode.imcms.dao.MetaDao
import _root_.com.vaadin.terminal.{UserError, ErrorMessage, Sizeable, ExternalResource}
import _root_.com.vaadin.data.Validator
import _root_.com.vaadin.data.Validator.InvalidValueException

/**
 * Doc's meta editor.
 *
 * @param doc used to as editor's initial state, never modified.
 */
class MetaEditor(doc: DocumentDomainObject) extends Editor with ImcmsServicesSupport {

  type DataType = (DocumentDomainObject, Map[I18nLanguage, I18nMeta])

  private var appearanceEditorOpt = Option.empty[AppearanceEditor]
  private var lifeCycleEditorOpt = Option.empty[LifeCycleEditor]
  private var permissionsEditorOpt = Option.empty[PermissionsEditor]
  private var searchSettingsEditorOpt = Option.empty[SearchSettingsEditor]
  private var categoriesEditorOpt = Option.empty[CategoriesEditor]
  private var profileEditorOpt = Option.empty[ProfileEditor]

  val ui = new MetaEditorUI |>> { ui =>
    ui.treeMenu.addItem("Appearance")
    ui.treeMenu.addItem("Life cycle")
    ui.treeMenu.addItem("Permissions")
    ui.treeMenu.addItem("Search")
    ui.treeMenu.addItem("Categories")

    // According to v.4.x.x may be defined for text docs only
    // todo: disable profile tag =or= add lable =not supported/available =or= show empty page instead of editor
    if (doc.isInstanceOf[TextDocumentDomainObject]) ui.treeMenu.addItem("Profile")

    ui.treeMenu.addValueChangeHandler {
      ui.treeMenu.getValue match {
        case "Appearance" =>
          if (appearanceEditorOpt.isEmpty) {
            val i18nMetas: Map[I18nLanguage, I18nMeta] = Option(doc.getIdValue) match {
              case Some(id) =>
                imcmsServices.getDocumentMapper.getI18nMetas(id).map(m => m.getLanguage -> m).toMap
              case _ =>
                Map.empty
            }

            appearanceEditorOpt = Some(
              new AppearanceEditor(doc.getMeta, i18nMetas)
            )
          }

          ui.pnlMenuItem.setContent(appearanceEditorOpt.get.ui)

        case "Life cycle" =>
          if (lifeCycleEditorOpt.isEmpty) lifeCycleEditorOpt = Some(new LifeCycleEditor(doc.getMeta))

          ui.pnlMenuItem.setContent(lifeCycleEditorOpt.get.ui)

        case "Permissions" =>
          if (permissionsEditorOpt.isEmpty) permissionsEditorOpt =
            Some(
              new PermissionsEditor(ui.getApplication,
              doc,
              ui.getApplication.user)
            )

          ui.pnlMenuItem.setContent(permissionsEditorOpt.get.ui)

        case "Search" =>
          if (searchSettingsEditorOpt.isEmpty) searchSettingsEditorOpt = Some(new SearchSettingsEditor(doc.getMeta))

          ui.pnlMenuItem.setContent(searchSettingsEditorOpt.get.ui)

        case "Categories" =>
          if (categoriesEditorOpt.isEmpty) categoriesEditorOpt = Some(new CategoriesEditor(doc.getMeta))

          ui.pnlMenuItem.setContent(categoriesEditorOpt.get.ui)

        case "Profile" =>
          if (profileEditorOpt.isEmpty) profileEditorOpt = Some(new ProfileEditor(doc.asInstanceOf[TextDocumentDomainObject], ui.getApplication.user))

          ui.pnlMenuItem.setContent(profileEditorOpt.get.ui)

        case _ =>
      }
    }

    ui.sp.setSplitPosition(25, Sizeable.UNITS_PERCENTAGE)
    ui.treeMenu.select("Appearance")
  } // ui

  val data = new Data {
    case class UberData(uberData: ErrorMsgsEitherData) {
      def merge[B](childDataOpt: => Option[Either[Seq[ErrorMsg], B]])(fn: (DataType, B) => DataType): UberData =
        childDataOpt match {
          case None => this
          case Some(Right(_)) if uberData.isLeft => this
          case Some(Right(childValue)) => UberData(Right(fn(uberData.right.get, childValue)))
          case Some(Left(childErrorMsgs)) if uberData.isRight => UberData(Left(childErrorMsgs))
          case Some(Left(childErrorMsgs)) => UberData(Left(uberData.left.get ++ childErrorMsgs))
        }
    }

    def get() = {
      UberData(
        Right(doc.clone, Map.empty[I18nLanguage, I18nMeta])
      ).merge(appearanceEditorOpt.map(_.data.get())) {
          case ((dc, _), appearance) => (dc, appearance.i18nMetas) |>> { _ =>
            dc.getMeta.setLanguages(appearance.enabledLanguages)
            dc.getMeta.setI18nShowMode(appearance.disabledLanguageShowSetting)
            dc.getMeta.setAlias(appearance.alias.orNull)
            dc.getMeta.setTarget(appearance.target)
          }
      }.merge(lifeCycleEditorOpt.map(_.data.get())) {
          case (uberData @ (dc, _), lifeCycle) => uberData |>> { _ =>
            dc.getMeta.setPublicationStatus(lifeCycle.publicationStatus)
            dc.getMeta.setPublicationStartDatetime(lifeCycle.publicationStart)
            dc.getMeta.setPublicationEndDatetime(lifeCycle.publicationEnd.orNull)
            dc.getMeta.setPublicationEndDatetime(lifeCycle.publicationEnd.orNull)
            dc.getMeta.setPublisherId(lifeCycle.publisher.map(p => Int box p.getId).orNull)
            //???dc.setVersion(new DocumentVersion() state.versionNo)
            dc.getMeta.setCreatedDatetime(lifeCycle.created)
            dc.getMeta.setModifiedDatetime(lifeCycle.modified)
            dc.getMeta.setCreatorId(lifeCycle.creator.map(c => Int box c.getId).orNull)
            //???dc.getMeta.setModifierId
          }
      }.merge(permissionsEditorOpt.map(_.data.get())) {
          case (uberData @ (dc, _), permissions) => uberData |>> { _ =>
            dc.setRoleIdsMappedToDocumentPermissionSetTypes(permissions.rolesPermissions)
            dc.getPermissionSets.setRestricted1(permissions.restrictedOnePermSet)
            dc.getPermissionSets.setRestricted2(permissions.restrictedTwoPermSet)
            dc.setRestrictedOneMorePrivilegedThanRestrictedTwo(permissions.isRestrictedOneMorePrivilegedThanRestricted2)
            dc.setLinkedForUnauthorizedUsers(permissions.isLinkedForUnauthorizedUsers)
            dc.setLinkableByOtherUsers(permissions.isLinkableByOtherUsers)
          }
      }.merge(categoriesEditorOpt.map(_.data.get())) {
          case (uberData @ (dc, _), categories) => uberData |>> { _ =>
            dc.setCategoryIds(categories.categoriesIds)
          }
      }.merge(profileEditorOpt.map(_.data.get())) {
          case (uberData @ (tdc: TextDocumentDomainObject, _), profile) => uberData |>> { _ =>
            tdc.setDefaultTemplateId(profile.defaultTemplate)
            tdc.getPermissionSetsForNewDocuments.setRestricted1(profile.restrictedOnePermSet)
            tdc.getPermissionSetsForNewDocuments.setRestricted2(profile.restrictedTwoPermSet)
            tdc.setDefaultTemplateIdForRestricted1(profile.restrictedOneTemplate)
            tdc.setDefaultTemplateIdForRestricted2(profile.restrictedTwoTemplate)
          }

          case (uberData, _) => uberData
      }.uberData
      //      //// ?????????????????????????????????????
      //      ////    ui.cbDefaultTemplate.value,
      //      ////    restrictedOnePermSet, // ??? clone
      //      ////    restrictedTwoPermSet, // ??? clone
      //      ////    ui.cbRestrictedOneDefaultTemplate,
      //      ////    ui.cbRestrictedTwoDefaultTemplate
    }
  } // data
}


/**
 * Editor UI's main component is a horizontal split panel.
 * -Left component - navigation tree.
 * -Right component - scrollable panel.
 */
class MetaEditorUI extends VerticalLayout with FullSize with NoMargin {

  val sp = new HorizontalSplitPanel with FullSize
  val treeMenu = new Tree with SingleSelect[MenuItemId] with NoChildrenAllowed with Immediate
  val pnlMenuItem = new Panel with LightStyle with FullSize

  sp.setFirstComponent(treeMenu)
  sp.setSecondComponent(pnlMenuItem)

  addComponent(sp)
}




class LifeCycleEditor(meta: Meta) extends Editor with ImcmsServicesSupport {
  type DataType = Values

  case class Values(
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

  val ui = new LifeCycleEditorUI |>> { ui =>
    ui.frmPublication.lytDate.chkEnd.addValueChangeHandler {
      ui.frmPublication.lytDate.calEnd.setEnabled(ui.frmPublication.lytDate.chkEnd.checked)
    }
  }

  def revert() {
    // version
    val (versionsNos, defaultVersionNo) = meta.getId match {
      case null =>
        Seq(DocumentVersion.WORKING_VERSION_NO) -> DocumentVersion.WORKING_VERSION_NO

      case id =>
        val versionInfo = imcmsServices.getDocumentMapper.getDocumentVersionInfo(id)
        versionInfo.getVersions.map(_.getNo) -> versionInfo.getDefaultVersion.getNo
    }

    ui.ussCreator.selection = Option(meta.getCreatorId).map(imcmsServices.getImcmsAuthenticatorAndUserAndRoleMapper.getUser(_))
    ui.ussPublisher.selection = Option(meta.getPublisherId).map(imcmsServices.getImcmsAuthenticatorAndUserAndRoleMapper.getUser(_))
    ui.ussModifier.selection = None

    ui.frmPublication.sltVersion.removeAllItems
    versionsNos.foreach(no => ui.frmPublication.sltVersion.addItem(no, no.toString))
    ui.frmPublication.sltVersion.setItemCaption(DocumentVersion.WORKING_VERSION_NO, "doc.version.working".i)
    ui.frmPublication.sltVersion.select(defaultVersionNo)

    // publication status
    ui.frmPublication.sltStatus.select(meta.getPublicationStatus)

    // dates
    ui.frmPublication.lytDate.calStart.value = meta.getPublicationStartDatetime |> opt getOrElse new Date
    //ui.frmPublication.lytDate.calEnd.setReadOnly(false)
    ui.frmPublication.lytDate.calEnd.value = meta.getPublicationEndDatetime
    ui.frmPublication.lytDate.chkEnd.checked = meta.getPublicationEndDatetime != null

    // todo: ??? remember lytDate.chkEnd date when uncheked???
  }

  val data = new Data {
    def get() = Right(
      Values(
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
    )
  }

  // init
  revert()
}


class LifeCycleEditorUI extends VerticalLayout with Spacing with FullWidth {
  setMargin(true, false, false, false)

  val ussPublisher = new UserSingleSelect
  val ussCreator = new UserSingleSelect
  val ussModifier = new UserSingleSelect

  val frmPublication = new Form with FullWidth {
    setCaption("Publication")
    getLayout.setMargin(false, true, true, true)

    val sltStatus = new Select("Status") with SingleSelect[Document.PublicationStatus] with NoNullSelection {
      addItem(Document.PublicationStatus.NEW, "New")
      addItem(Document.PublicationStatus.APPROVED, "Approved")
      addItem(Document.PublicationStatus.DISAPPROVED, "Disapproved")
    }

    val sltVersion = new Select("Version") with SingleSelect[DocVersionNo] with NoNullSelection

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
 * Doc's categories editor.
 *
 * Categories are added dynamically in editor and grouped by their type.
 * Single-choice categories appear in a Select component, multi-choice in TwinSelect component.
 * Components (Select and TwinSelect) captions is set to type name.
 */
class CategoriesEditor(meta: Meta) extends Editor with ImcmsServicesSupport {
  type DataType = Values

  case class Values(categoriesIds: Set[CategoryId])

  // todo: remove???
  private val initialValues = Values(meta.getCategoryIds.toSet)

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

  val ui = new GridLayout(2, 1) with Spacing |>> { ui =>
    for ((chkCType, sltCategories) <- typeCategoriesUIs) {
      addComponents(ui, chkCType, sltCategories)
    }
  }

  def revert() {
    for ((chkCType, sltCategories) <- typeCategoriesUIs) {
      chkCType.uncheck
      sltCategories.value = Nil

      for (categoryId <- sltCategories.itemIds if initialValues.categoriesIds(categoryId)) {
        sltCategories.select(categoryId)
        chkCType.check
      }

      chkCType.fireValueChange()
    }
  }

  val data = new Data {
    def get() = Right(
      Values(
        typeCategoriesUIs.collect {
          case (chkCType, sltCategories) if chkCType.isChecked => sltCategories.value
        }.flatten.toSet
      )
    )
  }

  //def isModified = state != initialData

  // init
  revert()
}


/**
 * Doc's search settings editor.
 */
class SearchSettingsEditor(meta: Meta) extends Editor {
  type DataType = Values

  case class Values(keywords: Set[Keyword], isExcludeFromInnerSearch: Boolean)

  private val initialValues = Values(meta.getKeywords.map(_.toLowerCase).toSet, false)

  val ui = new SearchSettingsEditorUI |>> { ui =>
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
    setKeywords(initialValues.keywords)
    ui.chkExcludeFromInternalSearch.checked = initialValues.isExcludeFromInnerSearch
  }

  val data = new Data {
    def get() = Right(
      Values(ui.lytKeywords.lstKeywords.itemIds.toSet, ui.chkExcludeFromInternalSearch.isChecked)
    )
  }

  //def isModified = state != initialData
}


class SearchSettingsEditorUI extends FormLayout with UndefinedSize {

  val chkExcludeFromInternalSearch = new CheckBox("Exclude this page from internal search")
  val lytKeywords = new GridLayout(3,2) with UndefinedSize {
    setCaption("Keywords")

    val lstKeywords = new ListSelect with MultiSelect[Keyword] with Immediate {
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
 *
 * @param meta doc's Meta
 * @param i18nMetas doc's i18nMeta-s
 */
class AppearanceEditor(meta: Meta, i18nMetas: Map[I18nLanguage, I18nMeta]) extends Editor with ImcmsServicesSupport {

  type DataType = Values

  case class Values(
    i18nMetas: Map[I18nLanguage, I18nMeta],
    enabledLanguages: Set[I18nLanguage],
    disabledLanguageShowSetting: Meta.DisabledLanguageShowSetting,
    alias: Option[String],
    target: String
  )

  // i18nMetas sorted by language (default always first) and native name
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

  val ui = new AppearanceEditorUI { ui =>
    ui.frmLanguages.cbShowMode.addItem(Meta.DisabledLanguageShowSetting.DO_NOT_SHOW, "Show 'Not found' page")
    ui.frmLanguages.cbShowMode.addItem(Meta.DisabledLanguageShowSetting.SHOW_IN_DEFAULT_LANGUAGE, "Show document in default language")

    for ((_, chkLanguage, i18nMetaEditorUI) <- i18nMetasUIs) {
      addComponents(ui.frmLanguages.lytI18nMetas, chkLanguage, i18nMetaEditorUI)
    }

//    // todo: check once!!!
//    override def attach() {
//      super.attach()
//      revert()
//    }

//    ui.frmAlias.txtAlias.setImmediate(true)
//    ui.frmAlias.txtAlias.setTextChangeEventMode(TextChangeEventMode.TIMEOUT)
    ui.frmAlias.txtAlias.addValidator(new Validator {
      val errMsgOptRef = Atoms.OptRef[String]

      def isValid(value: AnyRef) = {
        val errMsgOpt = for {
          alias <- ui.frmAlias.txtAlias.trimOpt
          docId <- imcmsServices.getComponent(classOf[MetaDao]).asInstanceOf[MetaDao].getDocIdByAliasOpt(alias)
          if meta.getId != docId
        } yield "alias allready exists"

        errMsgOptRef.set(errMsgOpt)
        errMsgOpt.isEmpty
      }

      def validate(value: AnyRef) {
        if (!isValid(value)) {
          for (errMsg <- errMsgOptRef.get) {
            throw new InvalidValueException(errMsg)
          }
        }
      }
    })
  } // ui

  val data = new Data {
    def get() = EX.allCatch.either(ui.frmAlias.txtAlias.validate()).left.map(e => Seq(e.getMessage)).right.map { _ =>
      Values(
        i18nMetasUIs.map {
          case (language, chkBox, i18nMetaEditorUI) =>
            language -> (new I18nMeta |>> { i18nMeta =>
              i18nMeta.setId(i18nMetas.get(language).map(_.getId).orNull)
              i18nMeta.setDocId(meta.getId)
              i18nMeta.setLanguage(language)
              i18nMeta.setHeadline(i18nMetaEditorUI.txtTitle.trim)
              i18nMeta.setMenuImageURL(i18nMetaEditorUI.embLinkImage.trim)
              i18nMeta.setMenuText(i18nMetaEditorUI.txaMenuText.trim)
            })
        } (breakOut),
        i18nMetasUIs.collect { case (language, chkBox, _) if chkBox.isChecked => language }(breakOut),
        ui.frmLanguages.cbShowMode.value,
        ui.frmAlias.txtAlias.trimOpt,
        ui.frmLinkTarget.cbTarget.value
      )
    }
  } // data


  /**
   * Default language checkbox is always checked (hence its i18n meta form is always visible)
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
          i18nMetaEditorUI.txaMenuText.value = i18nMeta.getMenuText
          i18nMetaEditorUI.embLinkImage.value = i18nMeta.getMenuImageURL

        case _ =>
          i18nMetaEditorUI.txtTitle.clear
          i18nMetaEditorUI.txaMenuText.clear
          i18nMetaEditorUI.embLinkImage.clear
      }
    }

    val alias = Option(meta.getAlias)
    ui.frmAlias.txtAlias.setInputPrompt(Option(meta.getId).map(_.toString).orNull)
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

  revert()
}


class AppearanceEditorUI extends VerticalLayout with Spacing with FullWidth {
  val frmLanguages = new Form(new VerticalLayout with Spacing) {
    setCaption("Languages")
    setMargin(true, false, false, false)
    getLayout.setMargin(true)

    val lytI18nMetas = new VerticalLayout with Spacing with FullWidth
    val cbShowMode = new ComboBox("When language is disabled") with SingleSelect[Meta.DisabledLanguageShowSetting] with NoNullSelection

    private val lytShowMode = new FormLayout with FullWidth
    lytShowMode.addComponent(cbShowMode)

    addComponents(getLayout, lytI18nMetas, lytShowMode)
  }


  val frmLinkTarget = new Form with FullWidth {
    setCaption("Link action")
    getLayout.setMargin(true)

    val cbTarget = new ComboBox("Show in") with SingleSelect[String] with NoNullSelection

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

    getLayout.asInstanceOf[HorizontalLayout] |> { lyt =>
      addComponents(lyt, lblContextURL, txtAlias, btnCheck)
      lyt.setExpandRatio(txtAlias, 1.0f)
      doto(lblContextURL, btnCheck) {
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
  val txaMenuText = new TextArea("Menu text") with FullWidth {
    setRows(3)
  }

  val embLinkImage = new TextField("Link image") with FullWidth
  // val chkEnabled ???
  // val flag, default???

  addComponents(this, txtTitle, txaMenuText, embLinkImage)
}
