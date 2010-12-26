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
import com.imcode.util.event.Publisher
import java.util.concurrent.atomic.AtomicBoolean

//// todo add predicate - see comments on canPreview
//  // refactor to predicate fn taken as parameter
//  def canPreview(file: File) = file.getName matches ".*\\.(gif|jpg|jpeg|png)$"


class FileDialog(caption: String, browser: FileBrowser = new FileBrowser)
    extends OkCancelDialog(caption) with CustomSizeDialog with BottomMarginDialog {

  mainContent = {
    val preview = new FilePreview(getApplication.asInstanceOf[ImcmsApplication], browser) // todo: appliaction casta - temp hack!
    letret(new FileDialogUI(browser.ui, preview.ui)) { ui =>

    }
  }

  browser listen {
    case DirContentSelection(selection) => btnOk setEnabled selection.isDefined
    case _ =>
  }

  browser.notifyListeners()
  setWidth("500px"); setHeight("400px")
}



class FileDialogUI(browserUI: FileBrowserUI, previewUI: FilePreviewUI) extends GridLayout(2, 2) with Spacing with FullSize {
  val mb = new MenuBar
  val miFile = mb.addItem("File", null)
  val miOptions = mb.addItem("Options", null)
  val miHelp = mb.addItem("Help", null)

  addComponent(mb, 0, 0, 1, 0)
  addComponents(this, browserUI, previewUI)
}


/**
 * File preview is set to listen browser directory file selection.
 * If selected file is eligible to preview
 */
// todo: define file mime types, size and handlers (pwf -> large PDF mark)
class FilePreview(app: ImcmsApplication, browser: FileBrowser) {
  private val enabledRef = new AtomicBoolean(false)
  val preview = new EmbeddedPreview
  val ui = new FilePreviewUI(preview.ui)

  ui.btnEnlarge addListener block {
    browser.dirContentSelection match {
      case Some(file) =>
      case _ =>
    }
  }

  browser listen {
    case DirContentSelection(Some(file)) => preview.set(new Embedded("", new FileResource(file, app)))
    case _ =>
  }

  preview listen { ui.btnEnlarge setEnabled _.isDefined }
  preview.notifyListeners()

  def enabled = enabledRef.get
  def enabled_=(enabled: Boolean) = enabledRef.set(enabled)
}


class FilePreviewUI(val previewUI: EmbeddedPreviewUI) extends GridLayout(1, 2) with Spacing {
  val btnEnlarge = new Button("Enlarge") with LinkStyle
  addComponents(this, previewUI, btnEnlarge)

  forlet(previewUI, btnEnlarge) { c => setComponentAlignment(c, Alignment.MIDDLE_CENTER) }
}


// val browser = new FileBrowser  || FileDialog???
class ImagePicker(app: ImcmsApplication) {
  val browser = new FileBrowser
  val preview = new EmbeddedPreview; preview.stubUI.value = "No image"

  val ui = letret(new ImagePickerUI(preview.ui)) { ui =>
    ui.btnRemove addListener block {
      preview.clear()
    }
    ui.btnChoose addListener block {
      app.initAndShow(new FileDialog("Pick an image", browser), resizable = true) { dlg =>
        dlg.addOkHandler {
          for (file <- browser.dirContentSelection)
            preview.set(new Embedded("", new FileResource(file, app)))
        }
      }
    }
  }

  preview listen { ui.btnRemove setEnabled _.isDefined }
  preview.notifyListeners()
}


class ImagePickerUI(previewUI: EmbeddedPreviewUI) extends GridLayout(2, 1) with Spacing {
  val lytButtons = new VerticalLayout with Spacing with UndefinedSize
  val btnChoose = new Button("Choose") with LinkStyle
  val btnRemove = new Button("Remove") with LinkStyle

  addComponents(lytButtons, btnRemove, btnChoose)
  addComponents(this, previewUI, lytButtons)

  setComponentAlignment(lytButtons, Alignment.BOTTOM_LEFT)
}

/**
 * Displays embedded component in a fixed size container.
 *
 * When embedded is not set displays a `stub` component instead.
 * A stub can be any component (also an embedded).
 * Preview is considered empty when stub is displayed.
 */
class EmbeddedPreview[A <: Component](val stubUI: A = new Label with UndefinedSize) extends Publisher[Option[Embedded]] {
  val ui = new EmbeddedPreviewUI

  clear()

  def clear() {
    let(ui.content) { content =>
      content.removeAllComponents
      content.addComponent(stubUI)
      content.setComponentAlignment(stubUI, Alignment.MIDDLE_CENTER)
    }
    notifyListeners(content)
  }

  def set(embedded: Embedded) {
    assert(embedded ne stubUI, "Stub can not be used as a preview component.")

    embedded.setSizeFull
    let(ui.content) { content =>
      content.removeAllComponents
      content.addComponent(embedded)
      content.setComponentAlignment(embedded, Alignment.MIDDLE_CENTER)
    }
    notifyListeners(content)
  }

  def content = if (isEmpty) None else Some(ui.content.getComponent(0).asInstanceOf[Embedded])
  def isEmpty = ui.content.getComponent(0) eq stubUI

  override def notifyListeners() = notifyListeners(content)
}

class EmbeddedPreviewUI(width: Int = 100, height: Int = 100) extends Panel {
  val content = new VerticalLayout with FullSize

  setContent(content)
  setWidth(width, Sizeable.UNITS_PIXELS)
  setHeight(height, Sizeable.UNITS_PIXELS)
}