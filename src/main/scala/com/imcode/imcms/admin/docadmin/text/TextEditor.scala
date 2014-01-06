package com.imcode
package imcms
package admin.docadmin.text

import com.imcode.imcms.vaadin.Current
import com.vaadin.ui._
import com.imcode.imcms.vaadin.component._
import com.imcode.imcms.vaadin.data._
import imcode.server.document.textdocument.TextDomainObject
import com.imcode.imcms.vaadin.Editor
import com.imcode.imcms.ImcmsServicesSupport
import org.vaadin.openesignforms.ckeditor.{CKEditorTextField, CKEditorConfig}


class TextEditor(texts: Seq[TextDomainObject], settings: TextEditorParameters) extends Editor with ImcmsServicesSupport {

  override type Data = Seq[TextDomainObject]

  private case class TextState(text: TextDomainObject, textComponent: AbstractField[String])

  private var states: Seq[TextState] = _

  override val view = new TextEditorView |>> { w =>
    if (!settings.canChangeFormat) {
      w.miFormatHtml.setEnabled(settings.format == TextDomainObject.Format.HTML)
      w.miFormatPlain.setEnabled(settings.format == TextDomainObject.Format.PLAIN_TEXT)
    }

    w.miFormatHtml.setCommandHandler { _ => setFormat(TextDomainObject.Format.HTML) }
    w.miFormatPlain.setCommandHandler { _ => setFormat(TextDomainObject.Format.PLAIN_TEXT) }
    w.miHistory.setCommandHandler { _ =>
      new TextHistoryDialog("Restore text", currentText) |> Current.ui.addWindow
    }
  }

  resetValues()

  private def getTexts(): Seq[TextDomainObject] = {
    if (states == null) {
      texts.map(_.clone())
    } else {
      states.map {
        case TextState(text, testWidget) => text.clone() |>> { _.setText(testWidget.getValue) }
      }
    }
  }

  private def currentText: TextDomainObject = {
    val selectedTabPositionOpt =
      for {
        component <- view.tsTexts.getSelectedTab.asOption
        tab <- view.tsTexts.getTab(component).asOption
      } yield view.tsTexts.getTabPosition(tab)

    states(selectedTabPositionOpt.get) |> {
      case TextState(text, textWidget) => text.clone() |>> { _.setText(textWidget.value) }
    }
  }

  private def setFormat(format: TextDomainObject.Format) {
    format match {
      case TextDomainObject.Format.HTML =>
        view.miFormatHtml.setChecked(true)
        view.miFormatPlain.setChecked(false)

      case TextDomainObject.Format.PLAIN_TEXT =>
        view.miFormatHtml.setChecked(false)
        view.miFormatPlain.setChecked(true)
    }

    val selectedTabPositionOpt =
      for {
        component <- view.tsTexts.getSelectedTab.asOption
        tab <- view.tsTexts.getTab(component).asOption
      } yield view.tsTexts.getTabPosition(tab)

    val tabIndex = view.tsTexts.getTabIndex

    states = texts.map { text =>
      TextState(
        text,
        format |> {
          case TextDomainObject.Format.HTML =>
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
          case TextDomainObject.Format.PLAIN_TEXT => settings.rowCountOpt match {
            case Some(1) => new TextField with FullWidth
            case _ => new TextArea with FullSize
          }
        } |>> { textWidget =>
          textWidget.value = text.getText
        }
      )
    }

    view.tsTexts.removeAllComponents()

    for (TextState(text, textWidget) <- states) {
      view.tsTexts.addTab(textWidget) |> { tab =>
        tab.setCaption(text.getI18nDocRef.language().getName)
        tab.setIcon(Theme.Icon.Language.flag(text.getI18nDocRef.language()))
      }
    }

    selectedTabPositionOpt.foreach(view.tsTexts.setSelectedTab)

    format |> {
      case TextDomainObject.Format.HTML => ("Format: HTML", Theme.Icon.TextFormatHtml)
      case _ => ("Format: Plain text", Theme.Icon.TextFormatPlain)
    } |> {
      case (formatTypeName, formatTypeIcon) =>
        view.lblStatus.setCaption(formatTypeName)
        view.lblStatus.setIcon(formatTypeIcon)
    }
  }

  def resetValues() {
    setFormat(settings.format)
  }

  def collectValues(): ErrorsOrData = {
    Right(getTexts())
  }
}
