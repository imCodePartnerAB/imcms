package com.imcode
package imcms
package admin.doc.meta.appearance

import scala.collection.JavaConverters._
import scala.collection.breakOut
import scala.collection.immutable.ListMap
import scala.util.control.{Exception => Ex}

import com.imcode.imcms.api._
import com.imcode.imcms.vaadin._
import com.imcode.imcms.dao.MetaDao

import com.vaadin.terminal.{ExternalResource}
import com.vaadin.data.Validator
import com.vaadin.data.Validator.InvalidValueException
import com.vaadin.ui._


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

  case class Data(
    i18nMetas: Map[I18nLanguage, I18nMeta],
    enabledLanguages: Set[I18nLanguage],
    disabledLanguageShowSetting: Meta.DisabledLanguageShowSetting,
    alias: Option[String],
    target: String
  )

  // i18nMetas sorted by language (default always first) and native name
  private val i18nMetasUIs: Seq[(I18nLanguage, CheckBox, I18nMetaEditorUI)] = {
    val defaultLanguage = imcmsServices.getI18nSupport.getDefaultLanguage
    val languages = imcmsServices.getI18nSupport.getLanguages.asScala.sortWith {
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
    ui.pnlLanguages.cbShowMode.addItem(Meta.DisabledLanguageShowSetting.DO_NOT_SHOW, "Show 'Not found' page")
    ui.pnlLanguages.cbShowMode.addItem(Meta.DisabledLanguageShowSetting.SHOW_IN_DEFAULT_LANGUAGE, "Show document in default language")

    for ((_, chkLanguage, i18nMetaEditorUI) <- i18nMetasUIs) {
      addComponentsTo(ui.pnlLanguages.lytI18nMetas, chkLanguage, i18nMetaEditorUI)
    }

    ui.pnlAlias.txtAlias.addValidator(new Validator {
      val metaDao = imcmsServices.getSpringBean(classOf[MetaDao])

      def findDocIdByAlias(): Option[Int] =
        for {
          alias <- ui.pnlAlias.txtAlias.trimOpt
          docId <- metaDao.getDocIdByAliasOpt(alias)
          if meta.getId != docId
        } yield docId

      def isValid(value: AnyRef) = findDocIdByAlias().isEmpty

      def validate(value: AnyRef) {
        for (docId <- findDocIdByAlias()) {
          throw new InvalidValueException("this alias is allredy taken by doc %d."format(docId))
        }
      }
    })
  } // ui

  def collectValues(): ErrorsOrData = Ex.allCatch.either(ui.pnlAlias.txtAlias.validate())
    .left.map(e => Seq(e.getMessage))
    .right.map { _ =>
      Data(
        i18nMetasUIs.map {
          case (language, chkBox, i18nMetaEditorUI) =>
            language -> (I18nMeta.builder() |> { builder =>
              builder.id(i18nMetas.get(language).map(_.getId).orNull)
                .docId(meta.getId)
                .language(language)
                .headline(i18nMetaEditorUI.txtTitle.trim)
                .menuImageURL(i18nMetaEditorUI.embLinkImage.trim)
                .menuText(i18nMetaEditorUI.txaMenuText.trim)
                .build()
            })
        } (breakOut),
        i18nMetasUIs.collect { case (language, chkBox, _) if chkBox.isChecked => language } (breakOut),
        ui.pnlLanguages.cbShowMode.value,
        ui.pnlAlias.txtAlias.trimOpt,
        ui.pnlLinkTarget.cbTarget.value
      )
    } // data


  // Default language checkbox is be always checked.
  def resetValues() {
    val defaultLanguage = imcmsServices.getI18nSupport.getDefaultLanguage

    for ((language, chkBox, i18nMetaEditorUI) <- i18nMetasUIs) {
      val isDefaultLanguage = language == defaultLanguage

      chkBox.setReadOnly(false)
      chkBox.checked = isDefaultLanguage || meta.getEnabledLanguages.contains(language)
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
    ui.pnlAlias.txtAlias.setInputPrompt(Option(meta.getId).map(_.toString).orNull)
    ui.pnlAlias.txtAlias.value = alias.getOrElse("")
    ui.pnlLanguages.cbShowMode.select(meta.getI18nShowSetting)

    for ((target, targetCaption) <- ListMap("_self" -> "Same frame", "_blank" -> "New window", "_top" -> "Replace all")) {
      ui.pnlLinkTarget.cbTarget.addItem(target, targetCaption)
    }

    val target = meta.getTarget match {
      case null => ui.pnlLinkTarget.cbTarget.firstItemIdOpt.get
      case target =>
        ui.pnlLinkTarget.cbTarget.itemIds.asScala.find(_ == target.toLowerCase) match {
          case Some(predefinedTarget) => predefinedTarget
          case _ =>
            ui.pnlLinkTarget.cbTarget.addItem(target, "Other frame: %s".format(target))
            target
        }
    }

    ui.pnlLinkTarget.cbTarget.select(target)
  }

  resetValues()
}


class AppearanceEditorUI extends VerticalLayout with Spacing with FullWidth {
  val pnlLanguages = new Panel("Languages") with FullWidth {
    val layout = new VerticalLayout with Spacing with Margin with FullWidth

    val lytI18nMetas = new VerticalLayout with Spacing with FullWidth
    val cbShowMode = new ComboBox("When language is disabled") with SingleSelect[Meta.DisabledLanguageShowSetting] with NoNullSelection

    private val lytShowMode = new FormLayout with FullWidth
    lytShowMode.addComponent(cbShowMode)

    addComponentsTo(layout, lytI18nMetas, lytShowMode)
    setContent(layout)
  }

  val pnlLinkTarget = new Panel("Link action") with FullWidth {
    val layout = new FormLayout with Margin with FullWidth
    val cbTarget = new ComboBox("Show in") with SingleSelect[String] with NoNullSelection

    layout.addComponent(cbTarget)
    setContent(layout)
  }


  val pnlAlias = new Panel("Alias") with FullWidth {
    val layout = new FormLayout with Margin with FullWidth
    val txtAlias = new TextField("http://host/") with FullWidth |>> {
      _.setInputPrompt("alternate page name")
    }

    addComponentsTo(layout, txtAlias)
    setContent(layout)
  }

  addComponentsTo(this, pnlLanguages, pnlLinkTarget, pnlAlias)
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

  addComponentsTo(this, txtTitle, txaMenuText, embLinkImage)
}