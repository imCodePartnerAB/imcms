package com.imcode
package imcms
package admin.instance.file

import com.imcode.imcms.vaadin.Current
import scala.collection.JavaConverters._
import java.io.{File}
import com.imcode.util.event.Publisher
import java.util.concurrent.atomic.AtomicBoolean
import org.apache.commons.io.FileUtils
import com.vaadin.ui._
import com.imcode.imcms.vaadin.component._
import com.imcode.imcms.vaadin.component.dialog._
import com.imcode.imcms.vaadin.data._
import com.vaadin.server._
import com.vaadin.shared.ui.{MarginInfo, BorderStyle}

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

  def ext(file: File): Option[String] = extRE.unapplySeq(file.getName).map(_.head.toLowerCase)
  def extString(file: File) = ext(file).getOrElse("")

  def isShowable(file: File) = showableExt contains extString(file)
  def isDirectlyShowable(file: File) = directlyShowableExts contains extString(file)
  def isContentShowable(file: File) = contentShowableExts contains extString(file)

  // todo: fix
  def download(file: File) = {
    new FileResource(file) {
      override def getStream() = super.getStream |>> { ds =>
        ds.setParameter("Content-Disposition", s"""attachment; filename="${file.getName}" """)
      }
    } |> { resource =>
      Current.page.open(resource, "", 500, 500, BorderStyle.DEFAULT)
    }
  }

  // todo: fix
  def showContent(file: File) =
    new OKDialog("file.dlg.show.title".f(file.getName)) with CustomSizeDialog with Resizable |>> { dlg =>
      dlg.mainWidget = new TextArea("", scala.io.Source.fromFile(file).mkString) with ReadOnly with FullSize
      dlg.setSize(500, 500)
    } |> Current.ui.addWindow

  // todo: fix
  def showDirectly(file: File) =
    new OKDialog("file.dlg.show.title".f(file.getName)) with CustomSizeDialog with Resizable |>> { dlg =>
      dlg.mainWidget = new Embedded("", new FileResource(file))
      dlg.setSize(500, 500)
    } |> Current.ui.addWindow


  // todo: fix
  def default(file: File) {
    val op = if (isDirectlyShowable(file)) showDirectly _
             else if (isContentShowable(file)) showContent _
             else download _
    op(file)
  }
}


class DirSelectionDialog(caption: String, browser: FileBrowser, excludedDirs: Seq[File] = Nil)
    extends OkCancelDialog(caption) with CustomSizeDialog with BottomContentMarginDialog {

  mainWidget = browser.widget

  browser listen {
    case None => btnOk setEnabled false
    case Some(LocationSelection(dir, items)) => btnOk.setEnabled(!excludedDirs.contains(dir))
  }

  browser.notifyListeners()
  // todo: refactor out
  browser.widget.spLocation.setSplitPosition(25)
  this.setSize(550, 450)
}


/**
 * Generic file dialog.
 * @param browser - preconfigured browser.
 */
class FileDialog(caption: String, browser: FileBrowser)
extends OkCancelDialog(caption) with CustomSizeDialog with BottomContentMarginDialog {
  val preview = new FilePreview(browser)

  mainWidget = new FileDialogWidget(browser.widget, preview.widget) |>> { w =>
    w.miViewPreview.setCommandHandler { _ =>
      preview.enabled = !preview.enabled
    }

    w.miFileUpload.setCommandHandler { _ =>
      new FileUploaderDialog("Upload file") |>> { dlg =>
        dlg.setOkButtonHandler {
          for {
            uploadedFile <- dlg.uploader.uploadedFile
            selection <- browser.selection
            dir = selection.dir
            filename = dlg.uploader.widget.txtSaveAsName.value // todo: check not empty
            file = new File(dir, filename)
          } {
            if (file.exists && !dlg.uploader.widget.chkOverwrite.checked) sys.error("File exists")
            else {
              FileUtils.moveFile(uploadedFile.file, file)
              browser.reloadLocationItems()
            }
          }
        }
      } |> Current.ui.addWindow
    }
  }

  browser listen {
    case Some(selection) => btnOk setEnabled selection.hasItems
    case _ => btnOk setEnabled false
  }

  browser.notifyListeners()
  // todo: refactor out
  browser.widget.spLocation.setSplitPosition(25)
  setWidth("500px"); setHeight("350px")
}


class FileDialogWidget(browserWidget: FileBrowserWidget, previewWidget: FilePreviewWidget) extends GridLayout(2, 2) with FullSize {
  val mb = new MenuBar
  val miFile = mb.addItem("File", null)
  val miView = mb.addItem("View", null)
  val miHelp = mb.addItem("Help", null)
  val miViewReload = miView.addItem("Reload", null)
  val miViewPreview = miView.addItem("Show/Hide preview", null)
  val miFileUpload = miFile.addItem("Upload", null)
  val miFileDownload = miFile.addItem("Download", null)

  addComponent(mb, 0, 0, 1, 0)
  this.addComponents(browserWidget, previewWidget)

  setComponentAlignment(previewWidget, Alignment.MIDDLE_CENTER)
  previewWidget.setMargin(new MarginInfo(false, true, false, true))

  setColumnExpandRatio(0, 1f)
  setRowExpandRatio(1, 1f)
}


/**
 * Listens to browser selection and displays content associated with that item type.
 */
class FilePreview(browser: FileBrowser) {
  private val enabledRef = new AtomicBoolean(false)
  val preview = new EmbeddedPreview
  val widget = new FilePreviewWidget(preview.widget)

  browser.listen { ev =>
    if (enabled) ev match {
      case Some(LocationSelection(_, Seq(item))) if item.isFile =>
        val caption = if (FileOps.isShowable(item)) "file.preview.act.show".i
                      else "file.browser.preview.act.download".i
        val iconResource = if (FileOps.isDirectlyShowable(item)) new FileResource(item)
                           else new ThemeResource("images/noncommercial/%s.png".format(
                             FileOps.extString(item) match {
                               case ext @ ("txt" | "pdf") => ext
                               case "jsp" | "htm" | "html" | "css" => "firefox"
                               case _ => "file"
                             }))

        widget.btnAction.setEnabled(true)
        widget.btnAction.setCaption(caption)
        widget.btnAction.addClickHandler { _ => FileOps.default(item) }
        preview.set(new Embedded("", iconResource))

      case _ =>
        if (!preview.isEmpty) {
          preview.clear
          updateDisabled(widget.btnAction) { _ setCaption "file.preview.act.na".i }
        }
    }
  }

  preview.listen { widget.btnAction setEnabled _.isDefined }
  preview.notifyListeners()
  enabled = false

  def enabled = enabledRef.get
  def enabled_=(enabled: Boolean) {
    enabledRef.set(enabled)
    widget.setVisible(enabled)
    if (enabled) browser.notifyListeners
  }
}


class FilePreviewWidget(val previewWidget: EmbeddedPreviewWidget) extends GridLayout(1, 2) with Spacing {
  val btnAction = new Button with SingleClickListener with LinkStyle
  this.addComponents(previewWidget, btnAction)

  Seq(previewWidget, btnAction).foreach(c => setComponentAlignment(c, Alignment.MIDDLE_CENTER))
}

/**
 * Allows to choose an image using FileDialog.
 *
 * @param browser browser with preconfigured location(s)
 */
class ImagePicker(browser: FileBrowser) {
  val preview = new EmbeddedPreview; preview.stubWidget.value = "No Icon"
  val fileDialog = new FileDialog("Pick an image", browser) with Resizable |>> { dlg =>
    dlg.preview.enabled = true
    dlg.setOkButtonHandler {
      for (selection <- browser.selection; file <- selection.firstItem)
        preview.set(new Embedded("", new FileResource(file)))
    }
  }

  val widget = new ImagePickerWidget(preview.widget) |>> { w =>
    w.btnRemove.addClickHandler { _ =>
      preview.clear()
    }

    w.btnChoose.addClickHandler { _ => Current.ui.addWindow(fileDialog) }
  }

  preview.listen { widget.btnRemove setEnabled _.isDefined }
  preview.notifyListeners()
}


class ImagePickerWidget(previewWidget: EmbeddedPreviewWidget) extends GridLayout(2, 1) with Spacing {
  val lytButtons = new VerticalLayout with Spacing with UndefinedSize
  val btnChoose = new Button("Choose") with LinkStyle
  val btnRemove = new Button("Remove") with LinkStyle

  lytButtons.addComponents(btnRemove, btnChoose)
  this.addComponents(previewWidget, lytButtons)

  setComponentAlignment(lytButtons, Alignment.BOTTOM_LEFT)
}

/**
 * Displays embedded component in a fixed size container.
 *
 * When embedded is not set displays a `stub` component instead.
 * Preview is considered empty when stub is displayed.
 * By default stub is a Label but it can be any component.
 */
class EmbeddedPreview[A <: Component](val stubWidget: A = new Label with UndefinedSize) extends Publisher[Option[Embedded]] {
  val widget = new EmbeddedPreviewWidget
  setPreviewWidget(stubWidget)

  def clear() = if (!isEmpty) setPreviewWidget(stubWidget)

  def set(embedded: Embedded) {
    assert(embedded ne stubWidget, "Stub can not be used as a preview component.")
    embedded.setSizeFull
    setPreviewWidget(embedded)
  }

  def get = if (isEmpty) None else Some(widget.content.getComponent(0).asInstanceOf[Embedded])
  def isEmpty = getPreviewWidget eq stubWidget
  override def notifyListeners() = notifyListeners(get)

  private def getPreviewWidget = widget.content.getComponent(0)
  private def setPreviewWidget(component: Component) {
    widget.content |> { content =>
      content.removeAllComponents()
      content.addComponent(component)
      content.setComponentAlignment(component, Alignment.MIDDLE_CENTER)
    }
    notifyListeners(get)
  }
}

class EmbeddedPreviewWidget(width: Int = 64, height: Int = 64) extends Panel {
  val content = new VerticalLayout with FullSize

  setContent(content)
  setWidth(width, Sizeable.UNITS_PIXELS)
  setHeight(height, Sizeable.UNITS_PIXELS)
}