package com.imcode
package imcms.admin.filesystem

import scala.collection.JavaConversions._
import com.vaadin.ui._
import com.vaadin.data.Property
import com.vaadin.data.Property._
import com.imcode.imcms.dao.{MetaDao, SystemDao, LanguageDao, IPAccessDao}
import imcms.api.{CategoryType, SystemProperty, IPAccess, Document}
import imcms.mapping.CategoryMapper
import imcms.servlet.superadmin.AdminSearchTerms
import com.imcode.imcms.api.Document.PublicationStatus
import imcode.util.Utility
import imcode.server.user._
import imcode.server.{SystemData, Imcms}
import java.util.{Date}
import com.vaadin.ui.Layout.MarginInfo
import imcode.server.document.{CategoryDomainObject, CategoryTypeDomainObject, DocumentDomainObject}
import com.vaadin.terminal.{FileResource, Resource, UserError}
import com.imcode.imcms.vaadin.{ContainerProperty => CP, _}
import com.vaadin.data.util.FilesystemContainer
import java.io.{FilenameFilter, OutputStream, FileOutputStream, File}
import java.lang.String

class FileBrowser extends SplitPanel(SplitPanel.ORIENTATION_HORIZONTAL) {
  val tblDirContent = new DirectoryContentTable
  val accDirTrees = new Accordion {
    addListener(new TabSheet.SelectedTabChangeListener {
      def selectedTabChange(e: TabSheet#SelectedTabChangeEvent) {
        getSelectedTab match {
          case dirTree: DirectoryTree => dirTree.getValue match {
            case dir: File => tblDirContent reload Some(dir)
            case _ => dirTree select dirTree.getItemIds.head
          }

          case _ => tblDirContent reload None
        }
      }
    })

    setSizeFull
  }

  val dirTreeValueChangeListener = new ValueChangeListener {
    def valueChange(e: ValueChangeEvent) = e.getProperty.getValue match {
        case dir: File => tblDirContent reload Some(dir)
        case _ => tblDirContent reload None
    }
  }

  setFirstComponent(accDirTrees)
  setSecondComponent(tblDirContent)

  setSplitPosition(15)
  setSizeFull  

  def addDirectoryTree(caption: String, root: File, icon: Option[Resource] = None) =
    letret(new DirectoryTree(root)) { dirTree =>
      dirTree addListener block { dirTreeValueChangeListener }
      accDirTrees addTab (dirTree, caption, icon.orNull)
    }

  def reload() {
    accDirTrees.getComponentIterator foreach {
      case dirTree: DirectoryTree => dirTree.reload
      case _ =>
    }

    accDirTrees.getComponentIterator.toStream.headOption match {
      case Some(c) => accDirTrees setSelectedTab c
      case _ =>
    }
  }
}


// no-select-multielect-single-select as contructor param -> location??
class FileBrowser2 {
  private val locations = scala.collection.mutable.Map.empty[Component, (DirTree, DirContent)]

  val ui = letret(new FileBrowser2UI) { ui =>
    ui.accDirTrees.addListener(new TabSheet.SelectedTabChangeListener {
      def selectedTabChange(e: TabSheet#SelectedTabChangeEvent) {
        val (dirTree, dirContent) = locations(e.getTabSheet.getSelectedTab)
        if (dirTree.ui.value == null) dirTree.reload()
        ui.setSecondComponent(dirContent.ui)
      }
    })
  }

  def addLocation(dir: File, caption: String, icon: Option[Resource] = None) {
    val dirTree = new DirTree(dir)
    val dirContent = new DirContent

    dirTree.ui addListener block {
      whenSelected(dirTree.ui) { dirContent reload _ }
    }

    locations(dirTree.ui) = (dirTree, dirContent)
    ui.accDirTrees.addTab(dirTree.ui, caption, icon.orNull)
  }
}


class FileBrowser2UI extends SplitPanel(SplitPanel.ORIENTATION_HORIZONTAL) with FullSize {
  val accDirTrees = new Accordion with FullSize

  setFirstComponent(accDirTrees)
  setSplitPosition(15)
}


class DirTree(dir: File) {
  val ui = new Tree with SingleSelect2[File] with ItemIdType[File] with Immediate with NoNullSelection

  def reload() {
    val fc = new FilesystemContainer(dir)
    val filter = new FilenameFilter {
      def accept(file: File, name: String) = let(new File(file, name)) { fsNode =>
        fsNode.isDirectory && !fsNode.isHidden
      }
    }

    fc.setFilter(filter)

    ui.setContainerDataSource(fc)
    ui.setItemCaptionMode(AbstractSelect.ITEM_CAPTION_MODE_ITEM)
    ui.select(dir)
  }
}


class DirContent {
  val ui = letret(new Table with ItemIdType[File] with Immediate with FullSize) { ui =>
    addContainerProperties(ui,
      CP[String]("Name"),
      CP[Date]("Date modified"),
      CP[String]("Size"),
      CP[String]("Kind"))

    import Table._
    ui.setColumnAlignments(Array(ALIGN_LEFT, ALIGN_LEFT, ALIGN_RIGHT, ALIGN_RIGHT))
  }

  def reload(dir: File) {
    val base = 1024
    val baseFn = java.lang.Math.pow(1024, _:Int).toInt

    ui.removeAllItems()
    for (fsNode <- dir.listFiles() if fsNode.isFile && !fsNode.isHidden) {
      val (size, units) = fsNode.length match {
        case size if size < baseFn(1) => (size, "--")
        case size if size < baseFn(2) => (size / base, "KB")
        case size if size < baseFn(3) => (size / base, "MB")
        case size => (size / base, "GB")
      }

      ui.addItem(Array[AnyRef](fsNode.getName, new Date(fsNode.lastModified), "%d %s".format(size, units), "--"), fsNode)
    }
  }
}






class DirectoryTree(val root: File) extends Tree {
  addListener(new Tree.ExpandListener {
    def nodeExpand(e: Tree#ExpandEvent) = e.getItemId match {
      case dir: File => dir.listFiles filter (_.isDirectory) foreach (addDir(_, Some(dir)))
    }
  })

  setImmediate(true)
  reload()
  
  def reload() {
    require(root.isDirectory,
            "Tree root [%s] does not exists or not a directory." format root.getAbsoluteFile)

    //getItemIds foreach (collapseItem(_)) // workaround; without collapsing root remains expanded
    getItemIds foreach collapseItem
    removeAllItems()
    addDir(root)
    expandItem(root)
  }

  def addDir(dir: File, parent: Option[File] = None) {
    addItem(dir)
    setItemCaption(dir, "/"+dir.getName)
    setChildrenAllowed(dir, dir.listFiles.filter(_.isDirectory).length > 0)

    parent match {
      case Some(parentDir) => setParent(dir, parentDir)
      case _ =>
    }
  }
}


class DirectoryContentTable extends Table {
  addContainerProperty("Name", classOf[String], null)
  addContainerProperty("Date modified", classOf[Date], null)
  addContainerProperty("Size", classOf[JLong], null)
  addContainerProperty("Kind", classOf[String], null)

  setImmediate(true)
  setSizeFull

  def reload(dir: Option[File]) {
    removeAllItems
    dir match {
      case Some(dir) => dir.listFiles filter (_.isFile) foreach { file =>
        addItem(Array(file.getName, new Date(file.lastModified), Long box file.length, "-"), file)
      }

      case _ =>
    }
  }  
}


// image file preview - prototype
class ImagePreview(imgWidth: Int, imgHeight: Int) extends GridLayout(1, 2) {
  val lytStub = new VerticalLayout {
    val lblStub = new Label("No Image Selected") {setSizeUndefined}

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
    addComponent(new Panel {addComponent(component);  setSizeUndefined}, 0, 0)

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
  val browser = new FileBrowser
  val preview = new ImagePreview(previewImgWidth, previewImgHeight)

  // refactor to predicate fn taken as parameter
  def canPreview(file: File) = file.getName matches ".*\\.(gif|jpg|jpeg|png)$"

  addComponents(this, browser, preview)
  setComponentAlignment(preview, Alignment.MIDDLE_CENTER)
  setExpandRatio(browser, 1.0f)

  browser.tblDirContent addListener block {
    browser.tblDirContent.getValue match {
      case file: File if canPreview(file) => preview showImage file
      case _ => preview.showStub()
    }
  }
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