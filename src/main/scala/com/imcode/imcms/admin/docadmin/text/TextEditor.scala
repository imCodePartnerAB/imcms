package com.imcode
package imcms
package admin.docadmin.text

import _root_.java.util
import com.imcode.imcms.api.DocumentLanguage
import com.imcode.imcms.ImcmsServicesSupport
import com.imcode.imcms.mapping.container.{LoopEntryRef, VersionRef, TextDocTextsContainer}
import com.imcode.imcms.mapping.{DocumentLanguageMapper, TextDocumentContentLoader}
import com.imcode.imcms.vaadin.component._
import com.imcode.imcms.vaadin.Editor
import com.vaadin.ui.TabSheet.{SelectedTabChangeEvent, SelectedTabChangeListener}
import com.vaadin.ui.{TextArea, Field, TextField}
import imcode.server.document.textdocument.TextDocumentDomainObject.LoopItemRef
import org.vaadin.openesignforms.ckeditor.{CKEditorTextField, CKEditorConfig}
import scala.collection.JavaConverters._
import imcode.server.document.textdocument.TextDomainObject

class TextEditor(versionRef: VersionRef, loopEntryRefOpt: Option[LoopEntryRef], textNo: Int, opts: TextEditorOpts) extends Editor with ImcmsServicesSupport {

  override type Data = TextDocTextsContainer

  override val view: TextEditorView = new TextEditorView |>> { v =>
    if (!opts.canChangeFormat) {
      v.miFormatHtml.setEnabled(false)
      v.miFormatPlain.setEnabled(false)
    }

    v.miFormatHtml.setCommandHandler { _ => setFormat(TextDomainObject.Format.HTML) }
    v.miFormatPlain.setCommandHandler { _ => setFormat(TextDomainObject.Format.PLAIN_TEXT) }
    v.miHistory.setCommandHandler { _ =>
      new TextHistoryDialog("Restore text", getSelection._2).show()
    }

    v.tsTexts.addSelectedTabChangeListener(new SelectedTabChangeListener {
      override def selectedTabChange(event: SelectedTabChangeEvent) {
        val (_, textDO) = getSelection

        updateDisabledMenuItem(v.miFormatHtml)(_.setChecked(textDO.getType == TextDomainObject.Format.HTML.ordinal()))
        updateDisabledMenuItem(v.miFormatPlain)(_.setChecked(textDO.getType == TextDomainObject.Format.PLAIN_TEXT.ordinal()))

        textDO.getType |> {
          case TextDomainObject.Format.HTML => ("Format: HTML", Theme.Icon.TextFormatHtml16)
          case _ => ("Format: Plain text", Theme.Icon.TextFormatPlain16)
        } |> {
          case (formatTypeName, formatTypeIcon) =>
            view.lblStatus.setCaption(formatTypeName)
            view.lblStatus.setIcon(formatTypeIcon)
        }
      }
    })
  }

  private val contentLoader = imcmsServices.getManagedBean(classOf[TextDocumentContentLoader])
  private val languageMapper = imcmsServices.getManagedBean(classOf[DocumentLanguageMapper])
  private val languages = languageMapper.getAll
  private var textsMap: util.Map[DocumentLanguage, TextDomainObject] = new util.HashMap

  resetValues()

  override def resetValues() {
    view.tsTexts.removeAllComponents()
    textsMap = loopEntryRefOpt match {
      case Some(loopEntryRef) => contentLoader.getLoopTexts(versionRef, LoopItemRef.of(loopEntryRef, textNo))
      case _ => contentLoader.getTexts(versionRef, textNo)
    }

    for (language <- languages.asScala) {
      val text = textsMap.get(language) match {
        case null => new TextDomainObject("", TextDomainObject.TEXT_TYPE_HTML) |>> { text => textsMap.put(language, text) }
        case text => text
      }

      val field = createFieldFor(text)

      view.tsTexts.addTab(field, language.getName, Theme.Icon.Language.flag(language))
    }
  }

  override def collectValues(): ErrorsOrData = {
    Right(TextDocTextsContainer.of(versionRef, loopEntryRefOpt.orNull, textNo, textsMap))
  }

  private def createFieldFor(textDO: TextDomainObject): Field[String] = {
    val field = textDO.getType match {
      case TextDomainObject.TEXT_TYPE_PLAIN =>
        opts.rowCountOpt match {
          case Some(1) => new TextField with FullWidth
          case _ => new TextArea with FullSize
        }

      case _ =>
        val config = new CKEditorConfig
        config.useCompactTags()
        config.disableElementsPath()
        config.setResizeDir(CKEditorConfig.RESIZE_DIR.HORIZONTAL)
        config.disableSpellChecker()
        config.setToolbarCanCollapse(false)
        //config.addOpenESignFormsCustomToolbar()
        config.setWidth("100%")

        new CKEditorTextField(config) with FullSize |>> { ckEditor =>
          //ckEditor.
        }
    }

    field.setValue(textDO.getText)
    field
  }

  private def getSelection: (DocumentLanguage, TextDomainObject) = {
    val language = languages.get(view.tsTexts.getTabIndex)
    val textDO = textsMap.get(language)

    (language, textDO)
  }

  private def setFormat(format: TextDomainObject.Format) {
    view.miFormatHtml.setChecked(format == TextDomainObject.Format.HTML)
    view.miFormatPlain.setChecked(format == TextDomainObject.Format.PLAIN_TEXT)

    val (_, textDO) = getSelection
    val field = view.tsTexts.getSelectedTab.asInstanceOf[Field[String]]

    textDO.setType(format.ordinal())
    textDO.setText(field.getValue)

    view.tsTexts.replaceComponent(field, createFieldFor(textDO))

    textDO.getType |> {
      case TextDomainObject.Format.HTML => ("Format: HTML", Theme.Icon.TextFormatHtml16)
      case _ => ("Format: Plain text", Theme.Icon.TextFormatPlain16)
    } |> {
      case (formatTypeName, formatTypeIcon) =>
        view.lblStatus.setCaption(formatTypeName)
        view.lblStatus.setIcon(formatTypeIcon)
    }
  }
}
