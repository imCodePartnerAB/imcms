package com.imcode
package imcms
package admin.doc.properties

import scala.collection.JavaConversions._

import com.imcode.imcms.vaadin._
import imcode.server.Imcms
import imcode.server.document.{CategoryTypeDomainObject, DocumentDomainObject}
import admin.doc.{PublicationLyt, PermissionsEditor}
import com.vaadin.ui._
import javax.sound.sampled.ReverbType
import api.{I18nMeta, I18nLanguage, Meta, CategoryType}


//trait PropertiesDialog { this: OKDialog =>
//  mainUI =
//}

class Properties(doc: DocumentDomainObject) extends ImcmsServicesSupport {

  private var keywordsOpt = Option.empty[Keywords]
  private var categoriesOpt = Option.empty[Categories]

  val ui = letret(new PropertiesUI) { ui =>
    ui.menu.addItem("Info")
    ui.menu.addItem("Appearence")
    ui.menu.addItem("Lifecycle")
    ui.menu.addItem("Permissions")
    ui.menu.addItem("Keywords")
    ui.menu.addItem("Categories")
    // Access, Publication, Status ...

    ui.menu.addValueChangeHandler {
      ui.menu.getValue match {
        case "Permissions" =>
          ui.sp.setSecondComponent(new PermissionsEditor(ui.getApplication.asInstanceOf[ImcmsApplication], doc.getMeta, ui.getApplication.asInstanceOf[ImcmsApplication].user).ui)

        case "Lifecycle" =>
          ui.sp.setSecondComponent(new PublicationLyt)

        case "Appearence" =>
          ui.sp.setSecondComponent(new Appearance(doc.getMeta, imcmsServices.getDocumentMapper.getI18nMetas(doc.getId).map(m => m.getLanguage -> m).toMap).ui)

        case "Keywords" =>
          if (keywordsOpt.isEmpty) keywordsOpt = Some(new Keywords(doc.getKeywords.toSeq))

          ui.sp.setSecondComponent(keywordsOpt.get.ui)

        case "Categories" =>
          if (categoriesOpt.isEmpty) categoriesOpt = Some(new Categories(doc.getCategoryIds.toSet))

          ui.sp.setSecondComponent(categoriesOpt.get.ui)

        case _ =>
          ui.sp.setSecondComponent(new Label("n/a"))
          // select Info; todo: no null selection, info selected by default
      }
    }
  }
}


class PropertiesUI extends VerticalLayout with FullSize {
  val sp = new HorizontalSplitPanel with FullSize
  val menu = new Tree with Immediate

  sp.setFirstComponent(menu)
  addComponent(sp)
}


class Categories(categoriesIds: Set[CategoryId] = Set.empty) extends ImcmsServicesSupport {
  private var typeCategoriesUIs: Seq[(CheckBox with DataType[CategoryTypeDomainObject], StrictSelect[CategoryId])] = _

  val ui = new CategoriesUI

  revert()

  def revert() {
    typeCategoriesUIs =
      for {
        cType <- imcmsServices.getCategoryMapper.getAllCategoryTypes.toSeq // .sortBy(_.getName)
        categories = imcmsServices.getCategoryMapper.getAllCategoriesOfType(cType)
        if categories.nonEmpty
      } yield {
        val chkCType = new CheckBox(cType.getName) with DataType[CategoryTypeDomainObject] with Immediate with ExposeValueChange
        val sltCategories =
          if (cType.isMultiselect) new TwinColSelect with MultiSelect2[CategoryId]
          else new ComboBox with SingleSelect2[CategoryId]

        chkCType.data = cType

        categories foreach { category =>
          val id = category.getId

          sltCategories.addItem(id, category.getName)
          if (categoriesIds(id)) sltCategories.select(id)
        }

        chkCType.checked = sltCategories.isSelected
        chkCType.addValueChangeHandler {
          sltCategories.setVisible(chkCType.isChecked)
        }
        chkCType.fireValueChange(true)

        chkCType -> sltCategories
      }

    ui setContent letret(new GridLayout(2, typeCategoriesUIs.size) with Spacing) { lyt =>
      for ((chkCType, sltCategories) <- typeCategoriesUIs) {
        addComponents(lyt, chkCType, sltCategories)
      }
    }
  }

  def modified: Boolean = ???
}

class CategoriesUI extends Panel with LightStyle with FullSize


// param: editable = true/false ???
class Keywords(keywords: Seq[Keyword] = Nil) {

  val ui = new KeywordsUI

  ui.btnAdd.addClickHandler {
    ui.txtKeyword.trim.toLowerCase match {
      case value if value.length > 0 && ui.lstKeywords.getItem(value) == null =>
        setKeywords(value +: ui.lstKeywords.itemIds.toSeq)

      case _ =>
    }

    ui.txtKeyword.value = ""
  }

  ui.btnRemove.addClickHandler {
    whenSelected(ui.lstKeywords) { _ foreach (ui.lstKeywords removeItem _) }
  }

  ui.lstKeywords.addValueChangeHandler {
    ui.lstKeywords.value.toSeq match {
      case Seq(value) => ui.txtKeyword.value = value
      case Seq(_, _, _*) => ui.txtKeyword.value = ""
      case _ =>
    }
  }

  revert()


  def revert() {
    setKeywords(keywords)
  }

  def setKeywords(keywords: Seq[Keyword]) {
    ui.lstKeywords.itemIds = keywords.map(_.toLowerCase).sorted
  }

  def validate() = ???
  def sync() = ???
  def modified: Boolean = ???
}


class KeywordsUI extends GridLayout(3,2) with Spacing with UndefinedSize {

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