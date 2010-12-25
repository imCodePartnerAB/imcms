package com.imcode
package imcms.admin.filesystem

import scala.collection.JavaConversions._
import com.vaadin.ui._
import com.vaadin.data.Property
import com.vaadin.data.Property._
import imcode.server.user._
import com.imcode.imcms.vaadin._
import java.io.{FilenameFilter, OutputStream, FileOutputStream, File}
import com.vaadin.terminal.{Sizeable, FileResource, Resource, UserError}

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


// image file preview - prototype
class ImagePreview(imgWidth: Int, imgHeight: Int) extends GridLayout(1, 2) {
  val lytStub = new VerticalLayout with UndefinedSize {
    val lblStub = new Label("No Image Selected") with UndefinedSize

    addComponent(lblStub)
    setComponentAlignment(lblStub, Alignment.MIDDLE_CENTER)
  }

  val btnEnlarge = new Button("Enlarge") {setWidth("100%")}
  addComponent(btnEnlarge, 0, 1)
  setMargin(true)
  setSpacing(true)

  showStub()

  def showImage(file: File) =
    let(new Embedded("", new FileResource(file, getApplication))) { e =>
      show(e)
    }

  def showStub() = show(lytStub)

  private def show(component: Component) {
    component.setHeight (imgHeight+"px")
    component.setWidth (imgWidth+"px")

    removeComponent(0, 0)
    addComponent(new Panel with UndefinedSize {addComponent(component)}, 0, 0)

    btnEnlarge setEnabled component.isInstanceOf[Embedded]
  }

  def image: Option[Embedded] = getComponent(0, 0) match {
    case e: Embedded => Some(e)
    case _ => None
  }
}




class ImagePicker(app: VaadinApplication) {
  val browser = new FileBrowser
  val preview = new Preview(new Label("-n/a-"))
  val ui = letret(new ImagePickerUI(preview.ui)) { ui =>
    ui.btnRemove addListener block {
      preview.clear()
    }
    ui.btnChoose addListener block {
      app.initAndShow(new {val fileBrowser = browser} with OkCancelDialog("Pick an image...") with FileSelectDialog,
                      resizable = true) { dlg =>
        dlg.addOkButtonClickListener {
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
  val lytButtons = new VerticalLayout with UndefinedSize
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