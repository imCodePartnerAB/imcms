package com.imcode.imcms.docadmin

import com.imcode._
import imcms.sysadmin.permissions.{UserSelectDialog, UserSelect, UserUI, UsersView}
import scala.collection.JavaConversions._
import com.vaadin.ui._
import com.imcode.imcms.dao.{MetaDao, SystemDao, LanguageDao, IPAccessDao}
import com.imcode.imcms.api._
import imcode.server.user._
import imcode.server.{Imcms}
import java.util.{Date, Collection => JCollection}
import scala.collection.mutable.{Map => MMap}
import imcode.server.document._
import com.imcode.imcms.vaadin.{TwinSelect => TWSelect, _}

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

class MetaEditor(val application: VaadinApplication, val model: MetaModel) {

  val ui = letret(new MetaUI) { ui =>
    for {
      (language, enabled) <- model.languages
      labels = model.i18nMetas(language)
    } {
      val lytLabels = letret(new I18nMetaLyt with DataType[I18nLanguage]) { l =>
        l.txtTitle setValue labels.getHeadline
        l.txtMenuText setValue labels.getMenuText

        l.data = language
      }

      let(ui.lytI18n.tsI18nMetas.addTab(lytLabels)) { tab =>
        if (Imcms.getI18nSupport.isDefault(language)) {
          tab.setCaption(language.getName + " (default)")
        } else {
          tab.setCaption(language.getName)
          //tab.setEnabled(enabled)
        }
      }
    }

    // changes model:
    //  - languages and meta 
    ui.lytI18n.btnSettings addListener block {
      application.initAndShow(new OkCancelDialog("I18n settings")) { dlg =>
        val content = dlg.setMainContent(new I18nSettingsDialogContent)

        for ((language, enabled) <- model.languages) {
          val chkLanguage = new CheckBox(language.getName) with DataType[I18nLanguage] {
            data = language
            setValue(enabled)
            setEnabled(!Imcms.getI18nSupport.isDefault(language))
          }
          content.lytLanguages.addComponent(chkLanguage)
        }

        content.ogDisabledShowMode.select(model.meta.getDisabledLanguageShowSetting)

        dlg addOkButtonClickListener {
          content.lytLanguages.getComponentIterator foreach {
            case cb: CheckBox with DataType[I18nLanguage] => model.languages(cb.data) = cb.booleanValue
          }

          model.meta.setDisabledLanguageShowSetting(content.ogDisabledShowMode.value)
        }
      }
    }

    // affects model
    ui.lytSearch.lytKeywords.btnEdit addListener block {
      application.initAndShow(new OkCancelDialog("Keywords")) { dlg =>
        val keywords = model.meta.getKeywords
        val content = new KeywordsDialogContent(keywords.toSeq)

        dlg setMainContent content
        dlg addOkButtonClickListener {
          val newKeywords = content.lstKeywords.itemIds
          ui.lytSearch.lytKeywords.txtKeywords.value = newKeywords.mkString(", ")
          keywords.clear
          newKeywords foreach (keywords add _)
        }
      }
    }

    // affects model
    ui.lytCategories.btnEdit addListener block {
      application.initAndShow(new OkCancelDialog("Categories") with CustomSizeDialog) { dlg =>
        val content = dlg.setMainContent(new CategoriesDialogContent)
        val categoryIds = model.meta.getCategoryIds
        
        val categories = Imcms.getServices.getCategoryMapper.getAllCategories
        val categoriesMap = categories map (c => c.getId -> c) toMap
        val categoriesGroups = categories groupBy (_.getType) groupBy (_._1.isSingleSelect)
        val (ssCategoriesGroup, msCategoriesGroup) = (categoriesGroups.get(true), categoriesGroups.get(false))
        val sltCategories: Seq[Select with ValueType[CategoryDomainObject]] = ssCategoriesGroup match {
          case None => List.empty
          case Some(categoryGroups) => for ((cType, categories) <- categoryGroups.toSeq) yield
            letret(new Select with ValueType[CategoryDomainObject] with SingleSelect with NoNullSelection) { slt =>
              slt.setCaption(cType.getName)
              categories foreach { c =>
                slt.addItem(c)
                slt.setItemCaption(c, c.getName)
                if (categoryIds contains c.getId) slt.select(c)
              }
            }
        }

        val twsCategories: Seq[TWSelect[CategoryDomainObject]] = msCategoriesGroup match {
          case None => List.empty
          case Some(categoryGroups) => for ((cType, categories) <- categoryGroups.toSeq) yield
            letret(new TWSelect[CategoryDomainObject]) { tws =>
              tws.setCaption(cType.getName)
              categories foreach { c =>
                if (categoryIds contains c.getId) tws.addChosenItem(c, c.getName)
                else tws.addAvailableItem(c, c.getName)
              }
            }
        }

        sltCategories foreach (content addComponent _)
        twsCategories foreach (content addComponent _)

        dlg.addOkButtonClickListener {
          categoryIds.clear
          sltCategories foreach (categoryIds add _.value.getId)
          twsCategories foreach (_.chosenItemIds foreach (categoryIds add _.getId))

          ui.lytCategories.txtCategories.value = categoryIds map (categoriesMap.get(_).getName) mkString ", "
        }

        dlg.setWidth("400px")
        dlg.setHeight("600px")
      }
    }

    // affects model
    ui.lytPublication.btnChoosePublisher addListener block {
      application.initAndShow(new OkCancelDialog("Choose publisher") with UserSelectDialog) { dlg =>
        dlg.addOkButtonClickListener {
          dlg.userSelect.selection match {
            case Some(user) =>
              model.meta.setPublisherId(user.getId)
              ui.lytPublication.lblPublisherName.value = user.getLoginName

            case None =>
              model.meta.setPublisherId(null)
              ui.lytPublication.lblPublisherName.value = "No publisher selected"
          }
        }
      }
    }

    // does NOT alter meta - only reads its values
    let(ui.lytPublication) { lyt =>
      lyt.chkEnd addListener block {
        lyt.chkEnd.booleanValue match {
          case true =>
            lyt.calEnd.setEnabled(true)
            lyt.calEnd.value = model.meta.getPublicationEndDatetime
          case false =>
            lyt.calEnd.value = null
            lyt.calEnd.setEnabled(false)
        }
      }

      // fire event
      lyt.chkEnd.fireClick
    }
  }

  /**
   * Validates data and populates model with values.
   * @returns Some(error) in case of a validation error or None.
   */
  def validate(): Option[String] = {
    ui.lytI18n.tsI18nMetas.getComponentIterator foreach {
      case i18nMetaUI: I18nMetaLyt with DataType[I18nLanguage] =>
        let(model.i18nMetas(i18nMetaUI.data)) { i18nMeta =>
          i18nMeta.setHeadline(i18nMetaUI.txtTitle.value)
          i18nMeta.setMenuText(i18nMetaUI.txtMenuText.value)
          i18nMeta.setMenuImageURL(i18nMetaUI.embLinkImage.value)
        }
    }

    ui.lytIdentity.txtAlias.value.trim match {
      case "" => model.meta.removeAlis
      case alias =>
        // todo: check alias
        model.meta.setAlias(alias)
    }

    model.meta.setPublicationStatus(ui.lytPublication.sltStatus.value)

    let(model.meta.getLanguages) { metaLanguages =>
      metaLanguages.clear
      for ((language, enabled) <- model.languages if enabled) metaLanguages.add(language)
    }

    model.meta.setPublicationStartDatetime(ui.lytPublication.calStart.value)
    model.meta.setPublicationEndDatetime(
      if (ui.lytPublication.chkEnd.booleanValue) ui.lytPublication.calEnd.value
      else null
    )

    model.meta.setSearchDisabled(ui.lytSearch.chkExclude.value)
    model.meta.setLinkedForUnauthorizedUsers(ui.lytLink.chkShowToUnauthorizedUser.value)
    model.meta.setTarget(if (ui.lytLink.chkOpenInNewWindow.booleanValue) "_top" else "_self")

    None
  }
}


/**
 * I18n settings modal dialog content.
 */
class I18nSettingsDialogContent extends FormLayout {
  val ogDisabledShowMode = new OptionGroup("When disabled") with ValueType[Meta.DisabledLanguageShowSetting]

  ogDisabledShowMode.addItem(Meta.DisabledLanguageShowSetting.DO_NOT_SHOW)
  ogDisabledShowMode.addItem(Meta.DisabledLanguageShowSetting.SHOW_IN_DEFAULT_LANGUAGE)
  ogDisabledShowMode.setItemCaption(Meta.DisabledLanguageShowSetting.DO_NOT_SHOW, "Show 'Not found' page")
  ogDisabledShowMode.setItemCaption(Meta.DisabledLanguageShowSetting.SHOW_IN_DEFAULT_LANGUAGE, "Show in default language")

  val lytLanguages = new VerticalLayout with UndefinedSize {
    setCaption("Enabled languages")
  }

  addComponents(this, lytLanguages, ogDisabledShowMode)
}


/**
 * I18nMeta content.
 */
class I18nMetaLyt extends FormLayout with NoSpacing with UndefinedSize {
  val txtTitle = new TextField("Title")
  val txtMenuText = new TextField("Menu text")
  val embLinkImage = new TextField("Link image")

  addComponents(this, txtTitle, txtMenuText, embLinkImage)
}


/**
 * Publication parameters.
 */
class PublicationLyt extends GridLayout(2, 4) with Spacing {
  val lblPublisher = new Label("Publisher") with UndefinedSize
  val lblPublisherName = new Label("No publisher selected") with UndefinedSize
  val btnChoosePublisher = new Button("...") with LinkStyle

  val lytPublisher = new HorizontalLayout with UndefinedSize with Spacing {
    addComponents(this, lblPublisherName, btnChoosePublisher)
  }

  val lblStatus = new Label("Status") with UndefinedSize
  // todo add status new??? - may need by someone???
  val sltStatus = new Select with ValueType[Document.PublicationStatus] with NoNullSelection {
    addItem(Document.PublicationStatus.APPROVED)
    addItem(Document.PublicationStatus.DISAPPROVED)
    select(Document.PublicationStatus.DISAPPROVED)

    setItemCaption(Document.PublicationStatus.APPROVED, "Approved")
    setItemCaption(Document.PublicationStatus.DISAPPROVED, "Disapproved")
  }

  val lblVersion = new Label("Version") with UndefinedSize
  val sltVersion = new Select with NoNullSelection {
    addItem("Working")
    select("Working")
  }

  val calStart = new PopupDateField with MinuteResolution with Now
  val calEnd = new PopupDateField with MinuteResolution
  val chkStart = new CheckBox("Start date") with Checked with ReadOnly // decoration, always disabled
  val chkEnd = new CheckBox("End date") with Immediate with ExposeFireClick

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

  btnAdd addListener block {
    txtKeyword.value.trim.toLowerCase match {
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

  val lytPublication = new PublicationLyt { setCaption("Publication") }

  forlet(lytIdentity, lytI18n, lytLink, lytSearch, lytCategories, lytPublication) { c =>
    c.setMargin(true)
    addComponent(c)
  }
}