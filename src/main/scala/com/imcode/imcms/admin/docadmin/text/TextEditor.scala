package com.imcode
package imcms
package admin.docadmin.text

import com.imcode.imcms.vaadin.Current
import com.vaadin.ui._
import com.imcode.imcms.vaadin.ui._
import com.imcode.imcms.vaadin.data._
import imcode.server.document.textdocument.TextDomainObject
import com.imcode.imcms.vaadin.Editor
import com.imcode.imcms.ImcmsServicesSupport
import org.vaadin.openesignforms.ckeditor.{CKEditorTextField, CKEditorConfig}


class TextEditor(texts: Seq[TextDomainObject], settings: TextEditorParameters) extends Editor with ImcmsServicesSupport {

  override type Data = Seq[TextDomainObject]

  private case class TextState(text: TextDomainObject, textUI: AbstractField[String])

  private var states: Seq[TextState] = _

  override val ui = new TextEditorUI |>> { ui =>
    if (!settings.canChangeFormat) {
      ui.miFormatHtml.setEnabled(settings.format == TextDomainObject.Format.HTML)
      ui.miFormatPlain.setEnabled(settings.format == TextDomainObject.Format.PLAIN_TEXT)
    }

    ui.miFormatHtml.setCommandHandler { _ => setFormat(TextDomainObject.Format.HTML) }
    ui.miFormatPlain.setCommandHandler { _ => setFormat(TextDomainObject.Format.PLAIN_TEXT) }
    ui.miHistory.setCommandHandler { _ =>
      new TextHistoryDialog("Restore text", currentText) |> Current.ui.addWindow
    }
  }

  resetValues()

  private def getTexts(): Seq[TextDomainObject] = {
    if (states == null) {
      texts.map(_.clone())
    } else {
      states.map {
        case TextState(text, textUI) => text.clone() |>> { _.setText(textUI.getValue) }
      }
    }
  }

  private def currentText: TextDomainObject = {
    val selectedTabPositionOpt =
      for {
        component <- ui.tsTexts.getSelectedTab.asOption
        tab <- ui.tsTexts.getTab(component).asOption
      } yield ui.tsTexts.getTabPosition(tab)

    states(selectedTabPositionOpt.get) |> {
      case TextState(text, textUI) => text.clone() |>> { _.setText(textUI.value) }
    }
  }

  private def setFormat(format: TextDomainObject.Format) {
    format match {
      case TextDomainObject.Format.HTML =>
        ui.miFormatHtml.setChecked(true)
        ui.miFormatPlain.setChecked(false)

      case TextDomainObject.Format.PLAIN_TEXT =>
        ui.miFormatHtml.setChecked(false)
        ui.miFormatPlain.setChecked(true)
    }

    val selectedTabPositionOpt =
      for {
        component <- ui.tsTexts.getSelectedTab.asOption
        tab <- ui.tsTexts.getTab(component).asOption
      } yield ui.tsTexts.getTabPosition(tab)

    val tabIndex = ui.tsTexts.getTabIndex

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
        } |>> { textUI =>
          textUI.value = text.getText
        }
      )
    }

    ui.tsTexts.removeAllComponents()

    for (TextState(text, textUI) <- states) {
      ui.tsTexts.addTab(textUI) |> { tab =>
        tab.setCaption(text.getI18nDocRef.language().getName)
        tab.setIcon(Theme.Icon.Language.flag(text.getI18nDocRef.language()))
      }
    }

    selectedTabPositionOpt.foreach(ui.tsTexts.setSelectedTab)

    format |> {
      case TextDomainObject.Format.HTML => ("Format: HTML", Theme.Icon.TextFormatHtml)
      case _ => ("Format: Plain text", Theme.Icon.TextFormatPlain)
    } |> {
      case (formatTypeName, formatTypeIcon) =>
        ui.lblStatus.setCaption(formatTypeName)
        ui.lblStatus.setIcon(formatTypeIcon)
    }
  }

  def resetValues() {
    setFormat(settings.format)
  }

  def collectValues(): ErrorsOrData = {
    Right(getTexts())
  }
}
