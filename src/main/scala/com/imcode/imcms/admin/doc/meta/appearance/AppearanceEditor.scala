package com.imcode
package imcms
package admin.doc.meta.appearance

import scala.language.reflectiveCalls

import scala.collection.JavaConverters._
import scala.collection.breakOut
import scala.collection.immutable.ListMap
import scala.util.control.{Exception => Ex}

import com.imcode.imcms.api._

import com.imcode.imcms.dao.MetaDao

import com.vaadin.data.Validator
import com.vaadin.data.Validator.InvalidValueException
import com.imcode.imcms.vaadin.component._
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
  private val i18nMetaEditorViews: Seq[I18nMetaEditorView] = {
    val defaultLanguage = imcmsServices.getDocumentI18nSupport.getDefaultLanguage
    val languages = imcmsServices.getDocumentI18nSupport.getLanguages.asScala.sortWith {
      case (l1, _) if l1 == defaultLanguage => true
      case (_, l2) if l2 == defaultLanguage => false
      case (l1, l2) => l1.getNativeName < l2.getNativeName
    }

    for (language <- languages)
    yield {
      val caption = language.getNativeName + (if (language == defaultLanguage) " (default)" else "")
      new I18nMetaEditorView(language, caption)
    }
  }

  override val view = new AppearanceEditorView |>> { w =>
    w.pnlLanguages.cbShowMode.addItem(Meta.DisabledLanguageShowSetting.DO_NOT_SHOW, "Show 'Not found' page")
    w.pnlLanguages.cbShowMode.addItem(Meta.DisabledLanguageShowSetting.SHOW_IN_DEFAULT_LANGUAGE, "Show document in default language")

    for (i18nMetaEditorWidget <- i18nMetaEditorViews) {
      w.pnlLanguages.lytI18nMetas.addComponent(i18nMetaEditorWidget)
    }

    w.pnlAlias.txtAlias.addValidator(new Validator {
      val metaDao = imcmsServices.getManagedBean(classOf[MetaDao])

      def findDocIdByAlias(): Option[Int] =
        for {
          alias <- w.pnlAlias.txtAlias.trimOpt
          docId <- metaDao.getDocIdByAliasOpt(alias)
          if meta.getId != docId
        } yield docId

      def isValid(value: AnyRef) = findDocIdByAlias().isEmpty

      override def validate(value: AnyRef) {
        for (docId <- findDocIdByAlias()) {
          throw new InvalidValueException("this alias is already taken by doc %d."format(docId))
        }
      }
    })
  } // widget

  override def collectValues(): ErrorsOrData = Ex.allCatch.either(view.pnlAlias.txtAlias.validate())
    .left.map(e => Seq(e.getMessage))
    .right.map { _ =>
      Data(
        i18nMetaEditorViews.collect {
          case i18nMetaEditorWidget if i18nMetaEditorWidget.chkEnabled.checked =>
            val language = i18nMetaEditorWidget.language
            val i18nMeta = I18nMeta.builder()
              .id(i18nMetas.get(language).map(_.getId).orNull)
              .docId(meta.getId)
              .language(language)
              .headline(i18nMetaEditorWidget.txtTitle.trim)
              .menuImageURL(i18nMetaEditorWidget.embLinkImage.trim)
              .menuText(i18nMetaEditorWidget.txaMenuText.trim)
              .build()

            language -> i18nMeta
        }(breakOut),

        i18nMetaEditorViews.collect {
          case i18nMetaEditorWidget if i18nMetaEditorWidget.chkEnabled.checked => i18nMetaEditorWidget.language
        } (breakOut),

        view.pnlLanguages.cbShowMode.value,
        view.pnlAlias.txtAlias.trimOpt,
        view.pnlLinkTarget.cbTarget.value
      )
    }


  // Default language checkbox is be always checked.
  override def resetValues() {
    val defaultLanguage = imcmsServices.getDocumentI18nSupport.getDefaultLanguage

    for (i18nMetaEditorWidget <- i18nMetaEditorViews) {
      val isDefaultLanguage = i18nMetaEditorWidget.language == defaultLanguage

      i18nMetaEditorWidget.chkEnabled.setReadOnly(false)
      i18nMetaEditorWidget.chkEnabled.checked = isDefaultLanguage || meta.getEnabledLanguages.contains(i18nMetaEditorWidget.language)
      i18nMetaEditorWidget.chkEnabled.setReadOnly(isDefaultLanguage)

      i18nMetas.get(i18nMetaEditorWidget.language) match {
        case Some(i18nMeta) =>
          i18nMetaEditorWidget.txtTitle.value = i18nMeta.getHeadline
          i18nMetaEditorWidget.txaMenuText.value = i18nMeta.getMenuText
          i18nMetaEditorWidget.embLinkImage.value = i18nMeta.getMenuImageURL

        case _ =>
          i18nMetaEditorWidget.txtTitle.clear
          i18nMetaEditorWidget.txaMenuText.clear
          i18nMetaEditorWidget.embLinkImage.clear
      }
    }

    view.pnlAlias.txtAlias.setInputPrompt(meta.getId.asOption.map(_.toString).orNull)
    view.pnlAlias.txtAlias.value = meta.getAlias.trimToEmpty
    view.pnlLanguages.cbShowMode.select(meta.getI18nShowSetting)

    for ((target, targetCaption) <- ListMap("_self" -> "Same frame", "_blank" -> "New window", "_top" -> "Replace all")) {
      view.pnlLinkTarget.cbTarget.addItem(target, targetCaption)
    }

    val target = meta.getTarget match {
      case null => view.pnlLinkTarget.cbTarget.firstItemIdOpt.get
      case target =>
        view.pnlLinkTarget.cbTarget.itemIds.asScala.find(_ == target.toLowerCase) match {
          case Some(predefinedTarget) => predefinedTarget
          case _ =>
            view.pnlLinkTarget.cbTarget.addItem(target, "Other frame: %s".format(target))
            target
        }
    }

    view.pnlLinkTarget.cbTarget.select(target)
  }

  resetValues()
}


