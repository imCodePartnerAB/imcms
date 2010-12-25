package com.imcode
package imcms.admin.filesystem

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

//todo: manager, file select, file select with preview.

sealed trait FileSelection
case class DirTreeSelection(selection: Option[File]) extends FileSelection
case class DirContentSelection(selection: Option[File]) extends FileSelection
// case class DirContentMultiSelection(selection: File*) extends FileSelection  ???

trait Publisher[T] {
  var listeners = List.empty[T => Unit]

  def listen(listener: T => Unit) {
    listeners ::= listener
  }

  def notifyListeners(ev: T) = for (l <- listeners) l(ev)

  def notifyListeners() {}
}

// todo: ADD DIR CONTENT MULTI SELECT?????
class FileBrowser extends Publisher[FileSelection] {
  private val locations = MMap.empty[Component, (DirTree, DirContent)]
  private val dirTreeSelectionRef = new AtomicReference[Option[File]](None)
  private val dirContentSelectionRef = new AtomicReference[Option[File]](None)

  val ui = letret(new FileBrowserUI) { ui =>
    ui.accDirTrees.addListener(new TabSheet.SelectedTabChangeListener {
      def selectedTabChange(e: TabSheet#SelectedTabChangeEvent) {
        val (dirTree, dirContent) = locations(e.getTabSheet.getSelectedTab)
        // No selection => reload
        if (dirTree.ui.value == null) dirTree.reload()
        ui.setSecondComponent(dirContent.ui)
      }
    })
  }

  def addLocation(caption: String, dir: File, icon: Option[Resource] = None) {
    val dirTree = new DirTree(dir)
    val dirContent = new DirContent

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


class DirContent {
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