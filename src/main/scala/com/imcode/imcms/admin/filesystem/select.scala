package com.imcode
package imcms.admin.filesystem

import scala.collection.JavaConversions._
import com.vaadin.data.Property
import com.vaadin.data.Property._
import imcode.server.user._
import com.imcode.imcms.vaadin._
import java.io.{FilenameFilter, OutputStream, FileOutputStream, File}
import com.vaadin.terminal.{Sizeable, FileResource, Resource, UserError}
import com.vaadin.ui._

// todo: file select, file select with preview
//// todo add predicate - see comments on canPreview
//  // refactor to predicate fn taken as parameter
//  def canPreview(file: File) = file.getName matches ".*\\.(gif|jpg|jpeg|png)$"

/**
 *
 */
trait FileSelectDialog extends OkCancelDialog with CustomSizeDialog with BottomMarginDialog {
  val fileBrowser: FileBrowser

  mainContent = fileBrowser.ui

  fileBrowser listen {
    case DirContentSelection(selection) => btnOk setEnabled selection.isDefined
    case _ =>
  }

  fileBrowser.notifyListeners()
}


class FilePreview {
  val preview = new Preview(new Label("n/a"))
  val ui = new FilePreviewUI(preview.ui)

  ui.btnEnlarge addListener block {

  }

  preview listen { ui.btnEnlarge setEnabled _.isDefined }
  preview.notifyListeners()
}


class FilePreviewUI(val previewUI: PreviewUI) extends GridLayout(1, 2) with Spacing {
  val btnEnlarge = new Button("Enlarge") with LinkStyle
  addComponents(this, previewUI, btnEnlarge)

  forlet(previewUI, btnEnlarge) { c => setComponentAlignment(c, Alignment.MIDDLE_CENTER) }
}


class ImagePicker(app: ImcmsApplication) {
  val browser = new FileBrowser
  val preview = new Preview(new Label("-n/a-"))
  val ui = letret(new ImagePickerUI(preview.ui)) { ui =>
    ui.btnRemove addListener block {
      preview.clear()
    }
    ui.btnChoose addListener block {
      app.initAndShow(new {val fileBrowser = browser} with OkCancelDialog("Pick an image...") with FileSelectDialog,
                      resizable = true) { dlg =>
        dlg.addOkHandler {
          for (selection <- browser.dirContentSelection)
            preview.set(new Embedded("", new FileResource(selection, app)))
        }
      }
    }
  }

  preview listen { ui.btnRemove setEnabled _.isDefined }
  preview.notifyListeners()
}


class ImagePickerUI(previewUI: PreviewUI) extends GridLayout(2, 1) with Spacing {
  val lytButtons = new VerticalLayout with Spacing with UndefinedSize
  val btnChoose = new Button("Choose") with LinkStyle
  val btnRemove = new Button("Remove") with LinkStyle

  addComponents(lytButtons, btnRemove, btnChoose)
  addComponents(this, previewUI, lytButtons)

  setComponentAlignment(lytButtons, Alignment.BOTTOM_LEFT)
}



class Preview(stub: Component) extends Publisher[Option[Component]] {
  val ui = new PreviewUI
  clear()

  def clear() {
    set(stub)
    notifyListeners(component)
  }

  def set(c: Component) {
    c.setSizeFull
    let(ui.content) { content =>
      content.removeAllComponents
      content.addComponent(c)
      content.setComponentAlignment(c, Alignment.MIDDLE_CENTER)
    }
    notifyListeners(component)
  }

  def component = if (isEmpty) None else Some(ui.content.getComponent(0))
  def isEmpty = stub == ui.content.getComponent(0)
}

class PreviewUI(width: Int = 100, height: Int = 100) extends Panel {
  val content = new VerticalLayout with FullSize

  setContent(content)
  setWidth(width, Sizeable.UNITS_PIXELS)
  setHeight(height, Sizeable.UNITS_PIXELS)
}