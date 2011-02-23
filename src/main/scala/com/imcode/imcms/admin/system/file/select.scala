package com.imcode
package imcms.admin.system.file

import scala.collection.JavaConversions._
import com.imcode.imcms.vaadin._
import java.io.{File}
import com.vaadin.ui._
import com.imcode.util.event.Publisher
import java.util.concurrent.atomic.AtomicBoolean
import com.vaadin.Application
import com.vaadin.terminal._
import org.apache.commons.io.FileUtils


class DirSelectionDialog(caption: String, browser: FileBrowser, excludedDirs: Seq[File] = Nil)
    extends OkCancelDialog(caption) with CustomSizeDialog with BottomMarginDialog {

  mainContent = browser.ui

  browser listen {
    case None => btnOk setEnabled false
    case Some(LocationSelection(dir, items)) => btnOk.setEnabled(!excludedDirs.contains(dir))
  }

  browser.notifyListeners()
  // todo: refactor out
  browser.ui.setSplitPosition(25)
  setWidth("500px"); setHeight("350px")
}

//// todo add predicate - see comments on canPreview
// todo implement download
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

    ui.miFileUpload setCommand block {
      ui.getApplication.initAndShow(new FileUploadDialog("Upload file")) { dlg =>
        dlg.setOkHandler {
          for {
            data <- dlg.upload.data
            selection <- browser.selection
            dir = selection.dir
            filename = dlg.upload.ui.txtSaveAsName.value // todo: check not empty
            file = new File(dir, filename)
          } {
            if (file.exists && !dlg.upload.ui.chkOverwrite.booleanValue) error("File exists")
            else {
              FileUtils.writeByteArrayToFile(file, data.content)
              browser.reloadLocationItems
            }
          }
        }
      }
    }
  }

  browser listen {
    case Some(selection) => btnOk setEnabled selection.hasItems
    case _ => btnOk setEnabled false
  }

  browser.notifyListeners()
  // todo: refactor out
  browser.ui.setSplitPosition(25)
  setWidth("500px"); setHeight("350px")
}


class FileDialogUI(browserUI: FileBrowserUI, previewUI: FilePreviewUI) extends GridLayout(2, 2) with FullSize {
  val mb = new MenuBar
  val miFile = mb.addItem("File", null)
  val miView = mb.addItem("View", null)
  val miHelp = mb.addItem("Help", null)
  val miViewReload = miView.addItem("Reload", null)
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
 * Full size preview is allowed if content source (resource) is an image of an appropriate size.
 */
class FilePreviewContent(val content: Embedded, val allowsFullSizePreview: Boolean)

// todo: add file size check
object FilePreviewContent {
  val extRE = """(?i).*\.(\S+)""".r

  def apply(app: Application, file: File) = {
    val ext = file.getName match {
      case extRE(ext) => ext
      case _ => ""
    }

    val (resource, isImage) = ext.toLowerCase match {
      case "gif" | "png" | "jpg" | "jpeg" =>
        (new FileResource(file, app), true)
      case other =>
        val imageName = other match {
          case "txt" => "txt"
          case "jsp" | "htm" | "html" | "css" => "firefox"
          case "pdf" => "pdf"
          case _ => "file"
        }
        (new ThemeResource("images/noncommercial/%s.png" format imageName), false)
    }

    new FilePreviewContent(new Embedded("", resource), isImage)
  }
}

/**
 * File preview is set to listen to browser's dir selection.
 * If selected file is eligible to preview...
 */
class FilePreview(browser: FileBrowser) {
  private val enabledRef = new AtomicBoolean(false)
  val preview = new EmbeddedPreview
  val ui = new FilePreviewUI(preview.ui)

  ui.btnEnlarge addClickHandler {
    for {
      selection <- browser.selection
      file <- selection.firstItem
      fpc = FilePreviewContent(ui.getApplication, file) if fpc.allowsFullSizePreview
    } ui.getApplication.initAndShow(new Window("Preview"), resizable = true) { w =>
      w.getContent.addComponent(fpc.content)
      w.setWidth("500px"); w.setHeight("500px")
    }
  }

  browser listen { ev =>
    if (enabled) ev match {
      case Some(LocationSelection(_, Seq(file))) =>
        val fpc = FilePreviewContent(ui.getApplication, file)
        preview.set(fpc.content)
        ui.btnEnlarge.setEnabled(fpc.allowsFullSizePreview)
      case Some(LocationSelection(_, Nil)) =>
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
    dlg.setOkHandler {
      for (selection <- browser.selection; file <- selection.firstItem)
        preview.set(new Embedded("", new FileResource(file, app)))
    }
  }

  val ui = letret(new ImagePickerUI(preview.ui)) { ui =>
    ui.btnRemove addClickHandler {
      preview.clear()
    }

    ui.btnChoose addClickHandler { app.show(fileDialog, resizable = true) }
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

  def get = if (isEmpty) None else Some(ui.content.getComponent(0).asInstanceOf[Embedded])
  def isEmpty = getPreviewComponent eq stubUI
  override def notifyListeners() = let(get) { notifyListeners _ }

  private def getPreviewComponent = ui.content.getComponent(0)
  private def setPreviewComponent(component: Component) {
    let(ui.content) { content =>
      content.removeAllComponents
      content.addComponent(component)
      content.setComponentAlignment(component, Alignment.MIDDLE_CENTER)
    }
    notifyListeners(get)
  }
}

class EmbeddedPreviewUI(width: Int = 100, height: Int = 100) extends Panel {
  val content = new VerticalLayout with FullSize

  setContent(content)
  setWidth(width, Sizeable.UNITS_PIXELS)
  setHeight(height, Sizeable.UNITS_PIXELS)
}