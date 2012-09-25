package com.imcode
package imcms
package admin.instance.file

import scala.collection.JavaConverters._
import com.imcode.imcms.vaadin._
import java.io.{File}
import com.imcode.util.event.Publisher
import java.util.concurrent.atomic.AtomicBoolean
import com.vaadin.Application
import com.vaadin.terminal._
import org.apache.commons.io.FileUtils
import com.vaadin.ui._
import com.imcode.imcms.vaadin.ui._
import com.imcode.imcms.vaadin.ui.dialog._

/**
 * Common file operations used in file manager and preview.
 */
object FileOps {

  val extRE = """(?i).*\.(\S+)""".r

  /** Files with the following extensions can be shown directly in a browser. */
  val directlyShowableExts = Set("gif", "png", "jpg", "jpeg")

  /** Content of the files with the following extensions can be shown directly in a browser. */
  val contentShowableExts = Set("txt", "jsp", "htm", "html", "xml", "xsl", "css")

  val showableExt = directlyShowableExts | contentShowableExts

  def ext(file: File) = extRE.unapplySeq(file.getName) map { _.head.toLowerCase }
  def extString(file: File) = ext(file) getOrElse ""

  def isShowable(file: File) = showableExt contains extString(file)
  def isDirectlyShowable(file: File) = directlyShowableExts contains extString(file)
  def isContentShowable(file: File) = contentShowableExts contains extString(file)


  def download(app: Application, file: File) =
    app.getMainWindow.open(
      new FileResource(file, app) {
        override def getStream() = super.getStream |>> { ds =>
          ds.setParameter("Content-Disposition", """attachment; filename="%s"""" format file.getName)
        }
      }
    )


  def showContent(app: Application, file: File) =
    app.getMainWindow.initAndShow(new OKDialog("file.dlg.show.title".f(file.getName)) with CustomSizeDialog, resizable = true) { dlg =>
      dlg.mainUI = new TextArea("", scala.io.Source.fromFile(file).mkString) with ReadOnly with FullSize
      dlg.setSize(500, 500)
    }


  def showDirectly(app: Application, file: File) =
    app.getMainWindow.initAndShow(new OKDialog("file.dlg.show.title".f(file.getName)) with CustomSizeDialog, resizable = true) { dlg =>
      dlg.mainUI = new Embedded("", new FileResource(file, app))
      dlg.setSize(500, 500)
    }


  def default(app: Application, file: File) {
    val op = if (isDirectlyShowable(file)) showDirectly _
             else if (isContentShowable(file)) showContent _
             else download _
    op(app, file)
  }
}


class DirSelectionDialog(caption: String, browser: FileBrowser, excludedDirs: Seq[File] = Nil)
    extends OkCancelDialog(caption) with CustomSizeDialog with BottomMarginDialog {

  mainUI = browser.ui

  browser listen {
    case None => btnOk setEnabled false
    case Some(LocationSelection(dir, items)) => btnOk.setEnabled(!excludedDirs.contains(dir))
  }

  browser.notifyListeners()
  // todo: refactor out
  browser.ui.spLocation.setSplitPosition(25)
  this.setSize(550, 450)
}


/**
 * Generic file dialog.
 * @param browser - preconfigured browser.
 */
class FileDialog(caption: String, browser: FileBrowser)
extends OkCancelDialog(caption) with CustomSizeDialog with BottomMarginDialog {
  val preview = new FilePreview(browser)

  mainUI = new FileDialogUI(browser.ui, preview.ui) |>> { ui =>
    ui.miViewPreview setCommandHandler {
      preview.enabled = !preview.enabled
    }

    ui.miFileUpload setCommandHandler {
      ui.topWindow.initAndShow(new FileUploaderDialog("Upload file")) { dlg =>
        dlg.setOkHandler {
          for {
            uploadedFile <- dlg.uploader.uploadedFile
            selection <- browser.selection
            dir = selection.dir
            filename = dlg.uploader.ui.txtSaveAsName.value // todo: check not empty
            file = new File(dir, filename)
          } {
            if (file.exists && !dlg.uploader.ui.chkOverwrite.checked) sys.error("File exists")
            else {
              FileUtils.moveFile(uploadedFile.file, file)
              browser.reloadLocationItems()
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
  browser.ui.spLocation.setSplitPosition(25)
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
  addComponentsTo(this, browserUI, previewUI)

  setComponentAlignment(previewUI, Alignment.MIDDLE_CENTER)
  previewUI.setMargin(false, true, false, true)

  setColumnExpandRatio(0, 1f)
  setRowExpandRatio(1, 1f)
}


/**
 * Listens to browser selection and displays content associated with that item type.
 */
class FilePreview(browser: FileBrowser) {
  private val enabledRef = new AtomicBoolean(false)
  val preview = new EmbeddedPreview
  val ui = new FilePreviewUI(preview.ui)

  browser listen { ev =>
    if (enabled) ev match {
      case Some(LocationSelection(_, Seq(item))) if item.isFile =>
        val app = ui.getApplication
        val caption = if (FileOps.isShowable(item)) "file.preview.act.show".i
                      else "file.browser.preview.act.download".i
        val iconResource = if (FileOps.isDirectlyShowable(item)) new FileResource(item, app)
                           else new ThemeResource("images/noncommercial/%s.png".format(
                             FileOps.extString(item) match {
                               case ext @ ("txt" | "pdf") => ext
                               case "jsp" | "htm" | "html" | "css" => "firefox"
                               case _ => "file"
                             }))

        ui.btnAction.setEnabled(true)
        ui.btnAction.setCaption(caption)
        ui.btnAction.addClickHandler { FileOps.default(app, item) }
        preview.set(new Embedded("", iconResource))

      case _ =>
        if (!preview.isEmpty) {
          preview.clear
          updateDisabled(ui.btnAction) { _ setCaption "file.preview.act.na".i }
        }
    }
  }

  preview listen { ui.btnAction setEnabled _.isDefined }
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
  val btnAction = new Button with SingleClickListener with LinkStyle
  addComponentsTo(this, previewUI, btnAction)

  doto(previewUI, btnAction) { c => setComponentAlignment(c, Alignment.MIDDLE_CENTER) }
}

/**
 * Allows to choose an image using FileDialog.
 *
 * @param app reference to enclosing application
 * @param browser browser with preconfigured location(s)
 */
class ImagePicker(app: Application, browser: FileBrowser) {
  val preview = new EmbeddedPreview; preview.stubUI.value = "No Icon"
  val fileDialog = new FileDialog("Pick an image", browser) |>> { dlg =>
    dlg.preview.enabled = true
    dlg.setOkHandler {
      for (selection <- browser.selection; file <- selection.firstItem)
        preview.set(new Embedded("", new FileResource(file, app)))
    }
  }

  val ui = new ImagePickerUI(preview.ui) |>> { ui =>
    ui.btnRemove addClickHandler {
      preview.clear()
    }

    ui.btnChoose addClickHandler { app.getMainWindow.show(fileDialog, resizable = true) }
  }

  preview listen { ui.btnRemove setEnabled _.isDefined }
  preview.notifyListeners()
}


class ImagePickerUI(previewUI: EmbeddedPreviewUI) extends GridLayout(2, 1) with Spacing {
  val lytButtons = new VerticalLayout with Spacing with UndefinedSize
  val btnChoose = new Button("Choose") with LinkStyle
  val btnRemove = new Button("Remove") with LinkStyle

  addComponentsTo(lytButtons, btnRemove, btnChoose)
  addComponentsTo(this, previewUI, lytButtons)

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
  override def notifyListeners() = notifyListeners(get)

  private def getPreviewComponent = ui.content.getComponent(0)
  private def setPreviewComponent(component: Component) {
    ui.content |> { content =>
      content.removeAllComponents()
      content.addComponent(component)
      content.setComponentAlignment(component, Alignment.MIDDLE_CENTER)
    }
    notifyListeners(get)
  }
}

class EmbeddedPreviewUI(width: Int = 64, height: Int = 64) extends Panel {
  val content = new VerticalLayout with FullSize

  setContent(content)
  setWidth(width, Sizeable.UNITS_PIXELS)
  setHeight(height, Sizeable.UNITS_PIXELS)
}