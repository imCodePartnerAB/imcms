package com.imcode
package imcms
package admin.docadmin

import com.vaadin.ui._
import com.imcode.imcms.vaadin.ui._
import com.imcode.imcms.vaadin.data._
import imcode.server.document.textdocument.TextDomainObject
import com.imcode.imcms.vaadin.Editor


class TextEditor(texts: Seq[TextDomainObject], settings: TextEditorParameters) extends Editor with ImcmsServicesSupport {

  type Data = Seq[TextDomainObject]

  private case class TextState(text: TextDomainObject, textUI: AbstractField[String])

  private var states: Seq[TextState] = _

  val ui = new TextEditorUI |>> { ui =>
    if (!settings.canChangeFormat) {
      ui.miFormatHtml.setEnabled(settings.format == TextDomainObject.Format.HTML)
      ui.miFormatPlain.setEnabled(settings.format == TextDomainObject.Format.PLAIN_TEXT)
    }

    ui.miFormatHtml.setCommandHandler { setFormat(TextDomainObject.Format.HTML) }
    ui.miFormatPlain.setCommandHandler { setFormat(TextDomainObject.Format.PLAIN_TEXT) }
    ui.miHistory.setCommandHandler {
      new TextHistoryDialog("Restore text", currentText) |> UI.getCurrent.addWindow
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
          case TextDomainObject.Format.HTML => new RichTextArea with FullSize
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
        tab.setCaption(text.getLanguage.getName)
        tab.setIcon(Theme.Icon.Language.flag(text.getLanguage))
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
