package com.imcode
package imcms.admin.filesystem

import scala.collection.JavaConversions._
import com.vaadin.ui._
import com.vaadin.data.Property
import com.vaadin.data.Property._
import imcode.server.user._
import imcode.server.{SystemData, Imcms}
import java.util.{Date}
import com.vaadin.terminal.{FileResource, Resource, UserError}
import com.imcode.imcms.vaadin.{ContainerProperty => CP, _}
import java.io.{FilenameFilter, OutputStream, FileOutputStream, File}

// todo: file select, file select with preview

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

// prototype
// todo add predicate - see comments on canPreview
class FileBrowserWithImagePreview(previewImgWidth: Int, previewImgHeight: Int) extends HorizontalLayout with FullSize {
  val browser = new FileBrowser2
  val preview = new ImagePreview(previewImgWidth, previewImgHeight)

  // refactor to predicate fn taken as parameter
  def canPreview(file: File) = file.getName matches ".*\\.(gif|jpg|jpeg|png)$"

  addComponents(this, browser.ui, preview)
  setComponentAlignment(preview, Alignment.MIDDLE_CENTER)
  setExpandRatio(browser.ui, 1.0f)

  // ????????????????????????????????????????
//  browser.tblDirContent addListener block {
//    browser.tblDirContent.getValue match {
//      case file: File if canPreview(file) => preview showImage file
//      case _ => preview.showStub()
//    }
//  }
}


class IconImagePicker(imgWidth: Int, imgHeight: Int) extends GridLayout(2, 1) {
  val lytStub = new VerticalLayout {
    val lblStub = new Label("No Icon")

    addComponent(lblStub)
    setComponentAlignment(lblStub, Alignment.MIDDLE_CENTER)
  }

  val btnChoose = new Button("Choose")
  val btnRemove = new Button("Remove")
  val lytControls = new VerticalLayout {
    addComponents(this, btnRemove, btnChoose)
    forlet(btnChoose, btnRemove) { _ setWidth "100%" }
    setSpacing(true)
    setWidth("100px")
  }

  addComponent(lytControls, 1, 0)
  setComponentAlignment(lytControls, Alignment.BOTTOM_LEFT)
  setSpacing(true)

  showStub()

  def showImage(embedded: Embedded) = show(embedded)

  def showStub() = show(lytStub)

  private def show(component: Component) {
    component.setHeight (imgHeight+"px")
    component.setWidth (imgWidth+"px")

    removeComponent(0, 0)
    addComponent(new Panel {addComponent(component);  setSizeUndefined}, 0, 0)
    btnRemove setEnabled component.isInstanceOf[Embedded]
  }
}