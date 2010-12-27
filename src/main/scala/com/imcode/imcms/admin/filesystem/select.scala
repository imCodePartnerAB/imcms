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
import com.vaadin.Application

//// todo add predicate - see comments on canPreview
//  // refactor to predicate fn taken as parameter
//  def canPreview(file: File) = file.getName matches ".*\\.(gif|jpg|jpeg|png)$"

/**
 * Generic file dialog.
 * @param browser - preconfigured browser.
 */
class FileDialog(caption: String, browser: FileBrowser)
    extends OkCancelDialog(caption) with CustomSizeDialog with BottomMarginDialog {
  val preview = new FilePreview(browser)

  mainContent = letret(new FileDialogUI(browser.ui, preview.ui)) { ui =>
    ui.miViewPreview setCommand block {
      preview.enabled = !preview.enabled
    }
  }

  browser listen {
    case DirContentSelection(selection) => btnOk setEnabled selection.isDefined
    case _ =>
  }

  browser.notifyListeners()
  // todo: refactor out
  browser.ui.setSplitPosition(15)
  setWidth("500px"); setHeight("400px")
}


class FileDialogUI(browserUI: FileBrowserUI, previewUI: FilePreviewUI) extends GridLayout(2, 2) with FullSize {
  val mb = new MenuBar
  val miFile = mb.addItem("File", null)
  val miView = mb.addItem("View", null)
  val miHelp = mb.addItem("Help", null)
  val miViewPreview = miView.addItem("Show/Hide preview", null)
  val miFileUpload = miFile.addItem("Upload", null)
  val miFileDownload = miFile.addItem("Download", null)

  addComponent(mb, 0, 0, 1, 0)
  addComponents(this, browserUI, previewUI)

  setComponentAlignment(previewUI, Alignment.MIDDLE_CENTER)
  previewUI.setMargin(false, true, false, true)

  setColumnExpandRatio(0, 1f)
  setRowExpandRatio(1, 1f)
}


/**
 * File preview is set to listen to browser's dir selection.
 * If selected file is eligible to preview...
 */
// todo: define file mime types, size and handlers (pwf -> large PDF mark)
class FilePreview(browser: FileBrowser) {
  private val enabledRef = new AtomicBoolean(false)
  val preview = new EmbeddedPreview
  val ui = new FilePreviewUI(preview.ui)

  ui.btnEnlarge addListener block {
    browser.dirContentSelection match {
      case Some(file) =>
        // if can enlarge
        ui.getApplication.initAndShow(new OKDialog("Preview") with CustomSizeDialog with BottomMarginDialog) { dlg =>
          dlg.mainContent = letret(new Panel with FullSize with LightStyle) { panel =>
            panel.setContent(new GridLayout(1,1) { addComponent(preview.content.get) })
          }
          dlg.setWidth("500px"); dlg.setHeight("500px")
        }
      case _ =>
    }
  }

  browser listen { ev =>
    if (enabled) ev match {
      case DirContentSelection(Some(file)) =>
        preview.set(new Embedded("", new FileResource(file, ui.getApplication)))
      case DirContentSelection(None) =>
        preview.clear
      case other =>
    }
  }

  preview listen { ui.btnEnlarge setEnabled _.isDefined }
  preview.notifyListeners()
  enabled = false

  def enabled = enabledRef.get
  def enabled_=(enabled: Boolean) {
    enabledRef.set(enabled)
    ui.setVisible(enabled)
    if (enabled) browser.notifyListeners
  }
}


class FilePreviewUI(val previewUI: EmbeddedPreviewUI) extends GridLayout(1, 2) with Spacing {
  val btnEnlarge = new Button("Enlarge") with LinkStyle
  addComponents(this, previewUI, btnEnlarge)

  forlet(previewUI, btnEnlarge) { c => setComponentAlignment(c, Alignment.MIDDLE_CENTER) }
}

/**
 * Allows to choose an image using FileDialog.
 *
 * @param app reference to enclosing application
 * @param browser browser with preconfigured location(s)
 */
class ImagePicker(app: Application, browser: FileBrowser) {
  val preview = new EmbeddedPreview; preview.stubUI.value = "No Icon"
  val fileDialog = letret(new FileDialog("Pick an image", browser)) { dlg =>
    dlg.preview.enabled = true
    dlg.addOkHandler {
      for (file <- browser.dirContentSelection)
        preview.set(new Embedded("", new FileResource(file, app)))
    }
  }

  val ui = letret(new ImagePickerUI(preview.ui)) { ui =>
    ui.btnRemove addListener block {
      preview.clear()
    }

    ui.btnChoose addListener block { app.show(fileDialog, resizable = true) }
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
 * Preview is considered empty when stub is displayed.
 * By default stub is a Label but it can be any component.
 */
class EmbeddedPreview[A <: Component](val stubUI: A = new Label with UndefinedSize) extends Publisher[Option[Embedded]] {
  val ui = new EmbeddedPreviewUI
  setPreviewComponent(stubUI)

  def clear() = if (!isEmpty) setPreviewComponent(stubUI)

  def set(embedded: Embedded) {
    assert(embedded ne stubUI, "Stub can not be used as a preview component.")
    embedded.setSizeFull
    setPreviewComponent(embedded)
  }

  def content = if (isEmpty) None else Some(ui.content.getComponent(0).asInstanceOf[Embedded])
  def isEmpty = getPreviewComponent eq stubUI
  override def notifyListeners() = let(content) { notifyListeners _ }

  private def getPreviewComponent = ui.content.getComponent(0)
  private def setPreviewComponent(component: Component) {
    let(ui.content) { content =>
      content.removeAllComponents
      content.addComponent(component)
      content.setComponentAlignment(component, Alignment.MIDDLE_CENTER)
    }
    notifyListeners(content)
  }
}

class EmbeddedPreviewUI(width: Int = 100, height: Int = 100) extends Panel {
  val content = new VerticalLayout with FullSize

  setContent(content)
  setWidth(width, Sizeable.UNITS_PIXELS)
  setHeight(height, Sizeable.UNITS_PIXELS)
}