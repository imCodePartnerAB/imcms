package com.imcode
package imcms.admin.system.file

import scala.collection.JavaConversions._
import com.vaadin.ui._
import com.vaadin.data.Property
import com.vaadin.data.Property._

import imcode.util.Utility
import imcode.server.user._
import scala.collection.mutable.{Map => MMap}
import imcode.server.{SystemData, Imcms}
import com.vaadin.ui.Layout.MarginInfo
import imcode.server.document.{CategoryDomainObject, CategoryTypeDomainObject, DocumentDomainObject}
import com.vaadin.terminal.{FileResource, Resource, UserError}
import com.imcode.imcms.vaadin.{ContainerProperty => CP, _}
import com.vaadin.data.util.FilesystemContainer
import java.io.{FilenameFilter, OutputStream, FileOutputStream, File}
import java.util.concurrent.atomic.AtomicReference
import com.imcode.util.event.Publisher
import java.util.{Date}

class DirFilesystemContainer(root: File) extends FilesystemContainer(root) {

  import java.util.Collections._

  setFilter(new FilenameFilter {
    def accept(file: File, name: String) = let(new File(file, name)) { fsNode =>
      fsNode.isDirectory && !fsNode.isHidden
    }
  })

  override def rootItemIds() = root |> singleton[File] |> unmodifiableCollection[File]
}

sealed trait FileSelection
case class DirTreeSelection(selection: Option[File]) extends FileSelection
case class DirContentSelection(selection: Option[File]) extends FileSelection
// case class DirContentMultiSelection(selection: File*) extends FileSelection  ???

// todo: ADD DIR CONTENT MULTI SELECT?????

object DirContentFilter {

  import scala.util.matching.Regex

  def apply(filter: File => Boolean, filters: File => Boolean*) = (file: File) => filter +: filters forall { _ apply file }

  val notHidden = apply(!_.isHidden)

  val fileOnly = apply(notHidden, _.isFile)

  def nameRE(re: Regex)(fsNode: File) = re.unapplySeq(fsNode.getName).isDefined

  def fileWithExt(ext: String, exts: String*) = nameRE("""(?i).*\.(%s)""".format(ext +: exts mkString("|")).r)_

  val imageFile = fileWithExt("png", "gif", "jpg", "jpeg")

  val templateFile = fileWithExt("jsp", "jspx", "html")
}


case class Place(dirTreeRoot: File, dirContentFilter: File => Boolean = DirContentFilter.notHidden, recursive: Boolean = true)


class FileBrowser extends Publisher[FileSelection] {
  type DirTreeTab = Component

  private val locations = MMap.empty[DirTreeTab, (DirTree, DirContent)]
  private val dirTreeSelectionRef = new AtomicReference[Option[File]](None)
  private val dirContentSelectionRef = new AtomicReference[Option[File]](None)
  private val locationRef = new AtomicReference[Option[(DirTree, DirContent)]](None)

  val ui = letret(new FileBrowserUI) { ui =>
    ui.accDirTrees.addListener(new TabSheet.SelectedTabChangeListener {
      def selectedTabChange(e: TabSheet#SelectedTabChangeEvent) {
        val location @ (dirTree, dirContent) = locations(e.getTabSheet.getSelectedTab)
        locationRef.set(?(location))
        // No selection? => reload
        if (dirTree.ui.value == null) dirTree.reload()
        ui.setSecondComponent(dirContent.ui)
      }
    })
  }

  def addPlace(caption: String, place: Place, icon: Option[Resource] = None) {
    val dirTree = new DirTree(place.dirTreeRoot)
    val dirContent = new DirContent(place.dirContentFilter)

    dirTree.ui addListener block {
      dirTreeSelectionRef set ?(dirTree.ui.value)

      whenSelected(dirTree.ui) { dirContent reload _ }
      notifyListeners(DirTreeSelection(dirTreeSelection))
    }

    dirContent.ui addListener block {
      dirContentSelectionRef set ?(dirContent.ui.value)
      notifyListeners(DirContentSelection(dirContentSelection))
    }

    locations(dirTree.ui) = (dirTree, dirContent)
    ui.accDirTrees.addTab(dirTree.ui, caption, icon.orNull)
  }

  def dirTreeSelection = dirTreeSelectionRef.get
  def dirContentSelection = dirContentSelectionRef.get

  override def notifyListeners() {
    notifyListeners(DirTreeSelection(dirTreeSelection))
    notifyListeners(DirContentSelection(dirContentSelection))
  }

  // reloads dir content in a current accordion's tab.
  def reloadDirContent() =
    for {
      dirContent <- (?(ui.accDirTrees.getSelectedTab) map locations map (_._2))
      selectedDir <- dirTreeSelection
    } dirContent.reload(selectedDir)

  def location() = locationRef.get
}


class FileBrowserUI extends SplitPanel(SplitPanel.ORIENTATION_HORIZONTAL) with FullSize {
  val accDirTrees = new Accordion with FullSize

  setFirstComponent(accDirTrees)
  setSplitPosition(15)
}


class DirTree(root: File) {
  val ui = new Tree with SingleSelect2[File] with ItemIdType[File] with Immediate with NoNullSelection

  def reload() {
    ui.setContainerDataSource(new DirFilesystemContainer(root))
    ui.setItemCaptionMode(AbstractSelect.ITEM_CAPTION_MODE_ITEM)
    ui.select(root)
  }
}


class DirContent(filter: File => Boolean) {
  val ui = letret(new Table with Selectable with SingleSelect2[File] with ItemIdType[File] with Immediate with FullSize) { ui =>
    addContainerProperties(ui,
      CP[String]("Name"),
      CP[Date]("Date modified"),
      CP[String]("Size"),
      CP[String]("Kind"))

    import Table._
    ui.setColumnAlignments(Array(ALIGN_LEFT, ALIGN_LEFT, ALIGN_RIGHT, ALIGN_RIGHT))
    ui.setRowHeaderMode(ROW_HEADER_MODE_ICON_ONLY);
    //ui.setItemIconPropertyId();
  }

  def reload(dir: File) {
    val base = 1024
    val baseFn = java.lang.Math.pow(1024, _:Int).toInt
    val (dirs, files) = dir.listFiles.partition(_.isDirectory)

    ui.removeAllItems()

    dirs.sortWith((d1, d2) => d1.getName.compareToIgnoreCase(d2.getName) < 0) foreach { dir =>
      ui.addItem(Array[AnyRef](dir.getName, new Date(dir.lastModified), "--", "Folder"), dir)
      ui.setItemIcon(dir, Theme.Icons.Folder16)
    }

    for (file <- files.sortWith((f1, f2) => f1.getName.compareToIgnoreCase(f2.getName) < 0) if filter(file)) {
      val (size, units) = file.length match {
        case size if size < baseFn(1) => (size, "--")
        case size if size < baseFn(2) => (size / base, "KB")
        case size if size < baseFn(3) => (size / base, "MB")
        case size => (size / base, "GB")
      }

      ui.addItem(Array[AnyRef](file.getName, new Date(file.lastModified), "%d %s".format(size, units), "File"), file)
      ui.setItemIcon(file, Theme.Icons.File16)
    }
  }
}