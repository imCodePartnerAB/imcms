package com.imcode
package imcms
package admin.doc.meta.appearance

import scala.collection.JavaConverters._
import scala.collection.breakOut
import scala.collection.immutable.ListMap
import scala.util.control.{Exception => Ex}

import com.imcode.imcms.api._

import com.imcode.imcms.dao.MetaDao

import com.vaadin.data.Validator
import com.vaadin.data.Validator.InvalidValueException
import com.imcode.imcms.vaadin.ui._
import com.imcode.imcms.vaadin.data._
import com.imcode.imcms.vaadin.Editor


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
class AppearanceEditor(meta: Meta, i18nMetas: Map[DocumentLanguage, I18nMeta]) extends Editor with ImcmsServicesSupport {

  case class Data(
    i18nMetas: Map[DocumentLanguage, I18nMeta],
    enabledLanguages: Set[DocumentLanguage],
    disabledLanguageShowSetting: Meta.DisabledLanguageShowSetting,
    alias: Option[String],
    target: String
  )

  // i18nMetas sorted by language (default always first) and native name
  private val i18nMetaEditorUIs: Seq[I18nMetaEditorUI] = {
    val defaultLanguage = imcmsServices.getDocumentI18nSupport.getDefaultLanguage
    val languages = imcmsServices.getDocumentI18nSupport.getLanguages.asScala.sortWith {
      case (l1, _) if l1 == defaultLanguage => true
      case (_, l2) if l2 == defaultLanguage => false
      case (l1, l2) => l1.getNativeName < l2.getNativeName
    }

    for (language <- languages)
    yield {
      val caption = language.getNativeName + (if (language == defaultLanguage) " (default)" else "")
      new I18nMetaEditorUI(language, caption)
    }
  }

  val ui = new AppearanceEditorUI { ui =>
    ui.pnlLanguages.cbShowMode.addItem(Meta.DisabledLanguageShowSetting.DO_NOT_SHOW, "Show 'Not found' page")
    ui.pnlLanguages.cbShowMode.addItem(Meta.DisabledLanguageShowSetting.SHOW_IN_DEFAULT_LANGUAGE, "Show document in default language")

    for (i18nMetaEditorUI <- i18nMetaEditorUIs) {
      ui.pnlLanguages.lytI18nMetas.addComponent(i18nMetaEditorUI)
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
          throw new InvalidValueException("this alias is already taken by doc %d."format(docId))
        }
      }
    })
  } // ui

  def collectValues(): ErrorsOrData = Ex.allCatch.either(ui.pnlAlias.txtAlias.validate())
    .left.map(e => Seq(e.getMessage))
    .right.map { _ =>
      Data(
        i18nMetaEditorUIs.collect {
          case i18nMetaEditorUI if i18nMetaEditorUI.chkEnabled.checked =>
            val language = i18nMetaEditorUI.language
            val i18nMeta = I18nMeta.builder()
              .id(i18nMetas.get(language).map(_.getId).orNull)
              .docId(meta.getId)
              .language(language)
              .headline(i18nMetaEditorUI.txtTitle.trim)
              .menuImageURL(i18nMetaEditorUI.embLinkImage.trim)
              .menuText(i18nMetaEditorUI.txaMenuText.trim)
              .build()

            language -> i18nMeta
        }(breakOut),

        i18nMetaEditorUIs.collect {
          case i18nMetaEditorUI if i18nMetaEditorUI.chkEnabled.checked => i18nMetaEditorUI.language
        } (breakOut),

        ui.pnlLanguages.cbShowMode.value,
        ui.pnlAlias.txtAlias.trimOpt,
        ui.pnlLinkTarget.cbTarget.value
      )
    }


  // Default language checkbox is be always checked.
  def resetValues() {
    val defaultLanguage = imcmsServices.getDocumentI18nSupport.getDefaultLanguage

    for (i18nMetaEditorUI <- i18nMetaEditorUIs) {
      val isDefaultLanguage = i18nMetaEditorUI.language == defaultLanguage

      i18nMetaEditorUI.chkEnabled.setReadOnly(false)
      i18nMetaEditorUI.chkEnabled.checked = isDefaultLanguage || meta.getEnabledLanguages.contains(i18nMetaEditorUI.language)
      i18nMetaEditorUI.chkEnabled.setReadOnly(isDefaultLanguage)

      i18nMetas.get(i18nMetaEditorUI.language) match {
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

    ui.pnlAlias.txtAlias.setInputPrompt(meta.getId.asOption.map(_.toString).orNull)
    ui.pnlAlias.txtAlias.value = meta.getAlias.trimToEmpty
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


