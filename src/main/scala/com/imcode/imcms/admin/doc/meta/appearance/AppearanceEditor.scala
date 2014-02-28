package com.imcode
package imcms
package admin.doc.meta.appearance

import com.imcode.imcms.api.DocumentLanguage
import com.imcode.imcms.mapping.jpa.doc.DocRepository
import com.imcode.imcms.mapping.{DocumentCommonContent, DocumentMeta}
import scala.language.reflectiveCalls

import scala.collection.JavaConverters._
import scala.collection.breakOut
import scala.collection.immutable.ListMap
import scala.util.control.{Exception => Ex}

import com.imcode.imcms.api._


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
 * @param commonContentMap doc's common content-s
 */
class AppearanceEditor(meta: DocumentMeta, commonContentMap: Map[DocumentLanguage, DocumentCommonContent]) extends Editor with ImcmsServicesSupport {

  case class Data(
    i18nMetas: Map[DocumentLanguage, DocumentCommonContent],
    enabledLanguages: Set[DocumentLanguage],
    disabledLanguageShowSetting: DocumentMeta.DisabledLanguageShowSetting,
    alias: Option[String],
    target: String
  )

  // i18nMetas sorted by language (default always first) and native name
  private val i18nMetaEditorViews: Seq[CommonContentEditorView] = {
    val defaultLanguage = imcmsServices.getDocumentLanguageSupport.getDefault
    val languages = imcmsServices.getDocumentLanguageSupport.getAll.asScala.sortWith {
      case (l1, _) if l1 == defaultLanguage => true
      case (_, l2) if l2 == defaultLanguage => false
      case (l1, l2) => l1.getNativeName < l2.getNativeName
    }

    for (language <- languages)
    yield {
      val caption = language.getNativeName + (if (language == defaultLanguage) " (default)" else "")
      new CommonContentEditorView(language, caption)
    }
  }

  override val view = new AppearanceEditorView |>> { v =>
    v.languages.cbShowMode.addItem(DocumentMeta.DisabledLanguageShowSetting.DO_NOT_SHOW, "Show 'Not found' page")
    v.languages.cbShowMode.addItem(DocumentMeta.DisabledLanguageShowSetting.SHOW_IN_DEFAULT_LANGUAGE, "Show document in default language")

    for (i18nMetaEditorWidget <- i18nMetaEditorViews) {
      v.languages.lytI18nMetas.addComponent(i18nMetaEditorWidget)
    }

    v.alias.txtAlias.addValidator(new Validator {
      val metaRepository = imcmsServices.getManagedBean(classOf[DocRepository])

      def findDocIdByAlias(): Option[Int] =
        for {
          alias <- v.alias.txtAlias.trimmedValueOpt
          docId <- metaRepository.getDocIdByAliasOpt(alias)
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

  override def collectValues(): ErrorsOrData = Ex.allCatch.either(view.alias.txtAlias.validate())
    .left.map(e => Seq(e.getMessage))
    .right.map { _ =>
      Data(
        i18nMetaEditorViews.collect {
          case i18nMetaEditorWidget if i18nMetaEditorWidget.chkEnabled.checked =>
            val language = i18nMetaEditorWidget.language
            val i18nMeta = DocumentCommonContent.builder()
              .headline(i18nMetaEditorWidget.txtTitle.trimmedValue)
              .menuImageURL(i18nMetaEditorWidget.embLinkImage.trimmedValue)
              .menuText(i18nMetaEditorWidget.txaMenuText.trimmedValue)
              .build()

            language -> i18nMeta
        }(breakOut),

        i18nMetaEditorViews.collect {
          case i18nMetaEditorWidget if i18nMetaEditorWidget.chkEnabled.checked => i18nMetaEditorWidget.language
        } (breakOut),

        view.languages.cbShowMode.firstSelected,
        view.alias.txtAlias.trimmedValueOpt,
        view.linkTarget.cbTarget.firstSelected
      )
    }


  // Default language checkbox is be always checked.
  override def resetValues() {
    val defaultLanguage = imcmsServices.getDocumentLanguageSupport.getDefault

    for (i18nMetaEditorWidget <- i18nMetaEditorViews) {
      val isDefaultLanguage = i18nMetaEditorWidget.language == defaultLanguage

      i18nMetaEditorWidget.chkEnabled.setReadOnly(false)
      i18nMetaEditorWidget.chkEnabled.checked = isDefaultLanguage || meta.getEnabledLanguages.contains(i18nMetaEditorWidget.language)
      i18nMetaEditorWidget.chkEnabled.setReadOnly(isDefaultLanguage)

      commonContentMap.get(i18nMetaEditorWidget.language) match {
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

    view.alias.txtAlias.setInputPrompt(meta.getId.asOption.map(_.toString).orNull)
    view.alias.txtAlias.value = meta.getAlias.trimToEmpty
    view.languages.cbShowMode.select(meta.getDisabledLanguageShowSetting)

    for ((target, targetCaption) <- ListMap("_self" -> "Same frame", "_blank" -> "New window", "_top" -> "Replace all")) {
      view.linkTarget.cbTarget.addItem(target, targetCaption)
    }

    val target = meta.getTarget match {
      case null => view.linkTarget.cbTarget.firstItemIdOpt.get
      case target =>
        view.linkTarget.cbTarget.itemIds.asScala.find(_ == target.toLowerCase) match {
          case Some(predefinedTarget) => predefinedTarget
          case _ =>
            view.linkTarget.cbTarget.addItem(target, "Other frame: %s".format(target))
            target
        }
    }

    view.linkTarget.cbTarget.select(target)
  }

  resetValues()
}


