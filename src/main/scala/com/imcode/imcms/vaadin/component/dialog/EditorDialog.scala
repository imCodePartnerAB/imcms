package com.imcode
package imcms
package vaadin.component.dialog

import com.imcode.imcms.vaadin.server._

import com.imcode.imcms.vaadin.{Current, Editor}

trait EditorDialog extends Dialog with OKButton {
  val editor: Editor
}

object EditorDialog {

  def bind(dialog: Dialog with OKButton, editor: Editor)(validDataHandler: editor.Data => Unit) {
    dialog.mainComponent = editor.view
    dialog.setOkButtonHandler {
      editor.collectValues() match {
        case Left(errors) =>
          Current.page.showConstraintViolationNotification(errors)

        case Right(data) =>
          validDataHandler(data)
          dialog.close()
      }
    }
  }

  def bind(dialog: EditorDialog)(validDataHandler: dialog.editor.Data => Unit) {
    dialog.setOkButtonHandler {
      dialog.editor.collectValues() match {
        case Left(errors) =>
          Current.page.showConstraintViolationNotification(errors)

        case Right(data) =>
          validDataHandler(data)
          dialog.close()
      }
    }
  }
}
