package com.imcode
package imcms.admin.file

import scala.collection.JavaConversions._
import com.vaadin.ui._
import com.vaadin.data.Property
import com.vaadin.data.Property._

import imcode.util.Utility
import imcode.server.user._
import scala.collection.mutable.{Map => MMap}
import imcode.server.{SystemData, Imcms}
import java.util.{Date}
import com.vaadin.ui.Layout.MarginInfo
import imcode.server.document.{CategoryDomainObject, CategoryTypeDomainObject, DocumentDomainObject}
import com.vaadin.terminal.{FileResource, Resource, UserError}
import com.imcode.imcms.vaadin.{ContainerProperty => CP, _}
import com.vaadin.data.util.FilesystemContainer
import java.io.{FilenameFilter, OutputStream, FileOutputStream, File}
import java.util.concurrent.atomic.AtomicReference
import com.imcode.util.event.Publisher

//todo: manager actions

sealed trait FileSelection
case class DirTreeSelection(selection: Option[File]) extends FileSelection
case class DirContentSelection(selection: Option[File]) extends FileSelection
// case class DirContentMultiSelection(selection: File*) extends FileSelection  ???

// todo: ADD DIR CONTENT MULTI SELECT?????

class Location(val dir: File, val dirContentFilter: File => Boolean, recursive: Boolean=true)

object Location {
  import scala.util.matching.Regex

  def defaultFileFilter(fsNode: File) = fsNode.isFile && !fsNode.isHidden

  def fileNameREFilter(re: Regex)(fsNode: File) = defaultFileFilter(fsNode) && re.unapplySeq(fsNode.getName).isDefined

  def fileExtFilter(ext: String, exts: String*) = fileNameREFilter("""(?i).*\.(%s)""".format(ext +: exts mkString("|")).r)_

  val imageFileFilter = fileExtFilter("png", "gif", "jpg", "jpeg")

  val templateFileFilter = fileExtFilter("jsp", "jspx", "html")

  def apply(dir: File): Location = apply(dir, defaultFileFilter)

  def apply(dir: File, dirContentFilter: File => Boolean) = new Location(dir, dirContentFilter)
}

class FileBrowser extends Publisher[FileSelection] {
  private val locations = MMap.empty[Component, (DirTree, DirContent)]
  private val dirTreeSelectionRef = new AtomicReference[Option[File]](None)
  private val dirContentSelectionRef = new AtomicReference[Option[File]](None)

  val ui = letret(new FileBrowserUI) { ui =>
    ui.accDirTrees.addListener(new TabSheet.SelectedTabChangeListener {
      def selectedTabChange(e: TabSheet#SelectedTabChangeEvent) {
        val (dirTree, dirContent) = locations(e.getTabSheet.getSelectedTab)
        // No selection? => reload
        if (dirTree.ui.value == null) dirTree.reload()
        ui.setSecondComponent(dirContent.ui)
      }
    })
  }

  def addLocation(caption: String, location: Location, icon: Option[Resource] = None) {
    val dirTree = new DirTree(location.dir)
    val dirContent = new DirContent(location.dirContentFilter)

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
  def reloadDirContent() = for {
    dirContent <- (?(ui.accDirTrees.getSelectedTab) map locations map (_._2))
    selectedDir <- dirTreeSelection
  } dirContent.reload(selectedDir)
}


class FileBrowserUI extends SplitPanel(SplitPanel.ORIENTATION_HORIZONTAL) with FullSize {
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


class DirContent(filter: File => Boolean) {
  val ui = letret(new Table with Selectable with SingleSelect2[File] with ItemIdType[File] with Immediate with FullSize) { ui =>
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
    for (fsNode <- dir.listFiles() if filter(fsNode)) {
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