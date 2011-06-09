package com.imcode
package imcms
package admin.doc.properties

import scala.collection.JavaConversions._

import com.imcode.imcms.vaadin._
import imcode.server.document.{CategoryTypeDomainObject, DocumentDomainObject}
import api.{I18nMeta, I18nLanguage, Meta, CategoryType}
import com.vaadin.ui._
import admin.doc.{PublicationLyt, PermissionsEditor}

//trait PropertiesDialog { this: OKDialog =>
//  mainUI =
//}

class Properties(doc: DocumentDomainObject) extends ImcmsServicesSupport {

  private var searchPropertiesOpt = Option.empty[SearchProperties]
  private var categoriesOpt = Option.empty[Categories]

  val ui = letret(new PropertiesUI) { ui =>
    ui.menu.addItem("Info")
    ui.menu.addItem("Appearence")
    ui.menu.addItem("Lifecycle")
    ui.menu.addItem("Permissions")
    ui.menu.addItem("Search")
    ui.menu.addItem("Categories")
    // Access, Publication, Status ...

    ui.menu.addValueChangeHandler {
      ui.menu.getValue match {
        case "Permissions" =>
          ui.property.setContent(new PermissionsEditor(ui.getApplication.asInstanceOf[ImcmsApplication], doc.getMeta, ui.getApplication.asInstanceOf[ImcmsApplication].user).ui)

        case "Lifecycle" =>
          ui.property.setContent(new PublicationLyt)

        case "Appearence" =>
          ui.property.setContent(new Appearance(doc.getMeta, imcmsServices.getDocumentMapper.getI18nMetas(doc.getId).map(m => m.getLanguage -> m).toMap).ui)

        case "Search" =>
          if (searchPropertiesOpt.isEmpty) searchPropertiesOpt = Some(new SearchProperties(doc.getMeta))

          ui.property.setContent(searchPropertiesOpt.get.ui)

        case "Categories" =>
          if (categoriesOpt.isEmpty) categoriesOpt = Some(new Categories(doc.getMeta))

          ui.property.setContent(categoriesOpt.get.ui)

        case _ =>
          ui.property.setContent(new VerticalLayout with UndefinedSize { addComponent(new Label("n/a") with UndefinedSize) })
          // select Info; todo: no null selection, info selected by default
      }
    }
  }
}


class PropertiesUI extends VerticalLayout with FullSize with NoMargin {
  val sp = new HorizontalSplitPanel with FullSize
  val menu = new Tree with Immediate
  val property = new Panel with LightStyle with FullSize

  sp.setSecondComponent(property)
  sp.setFirstComponent(menu)
  addComponent(sp)
}


class Categories(meta: Meta) extends ImcmsServicesSupport {
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
class SearchProperties(meta: Meta) {
  case class State(keywords: Set[Keyword], isExcludeFromInnerSearch: Boolean)

  private val initialState = State(meta.getKeywords.map(_.toLowerCase).toSet, false)

  val ui = letret(new SearchPropertiesUI) { ui =>
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


class SearchPropertiesUI extends FormLayout with UndefinedSize {

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


class Appearance(meta: Meta, i18nMetas: Map[I18nLanguage, I18nMeta]) extends ImcmsServicesSupport {
  val ui = new AppearanceUI

  ui.cbShowMode.addItem(Meta.DisabledLanguageShowSetting.DO_NOT_SHOW, "Show 'Not found' page")
  ui.cbShowMode.addItem(Meta.DisabledLanguageShowSetting.SHOW_IN_DEFAULT_LANGUAGE, "Show in default language")

  revert()

  def revert() {
    ui.cbShowMode.select(meta.getI18nShowSetting)

    ui.tblI18nMetas.removeAllItems()
    imcmsServices.getI18nSupport.getLanguages foreach { language =>
      val i18nMeta = i18nMetas.get(language) getOrElse letret(new I18nMeta) { m =>
        m.setHeadline("")
        m.setMenuText("")
        m.setMenuImageURL("")
      }

      ui.tblI18nMetas.addItem(
        Array[AnyRef](language.getName, i18nMeta.getHeadline, i18nMeta.getMenuText, i18nMeta.getMenuImageURL, Boolean box meta.getLanguages.contains(language)),
        language.getId
      )
    }
  }
}

class AppearanceUI extends VerticalLayout with Spacing with UndefinedSize {
  val tblI18nMetas = new Table with MultiSelect2[LanguageId] {
    addContainerProperties(this,
      ContainerProperty[String]("Language"),
      ContainerProperty[String]("Headline"),
      ContainerProperty[String]("Menu text"),
      ContainerProperty[String]("Menu image"),
      ContainerProperty[JBoolean]("Enabled"))
  }

  val cbShowMode = new ComboBox("When disabled") with SingleSelect2[Meta.DisabledLanguageShowSetting] with NoNullSelection

//  val txtTitle = new TextField("Title")
//  val txtMenuText = new TextField("Menu text")
//  val embLinkImage = new TextField("Link image")

  addComponents(this, tblI18nMetas, cbShowMode)
}