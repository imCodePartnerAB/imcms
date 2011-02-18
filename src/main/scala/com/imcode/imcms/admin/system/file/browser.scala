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

/** Directory tree container with a single root. */
class DirTreeContainer(root: File) extends FilesystemContainer(root) {

  import java.util.Collections._

  setFilter(new FilenameFilter {
    def accept(file: File, name: String) = let(new File(file, name)) { fsNode =>
      fsNode.isDirectory && !fsNode.isHidden
    }
  })

  override def rootItemIds() = root |> singleton[File] |> unmodifiableCollection[File]

  override def addRoot(root: File) = error("Operation is not allowed.")
}


/** Predefined directory tree filters. */
object DirContentFilter {

  import scala.util.matching.Regex

  /** Creates a compund filter from a sequence of filters. */
  def apply(filter: File => Boolean, filters: File => Boolean*) = (file: File) => filter +: filters forall { _ apply file }

  val notHidden = apply(!_.isHidden)

  val fileOnly = apply(notHidden, _.isFile)

  def nameRE(re: Regex)(fsNode: File) = re.unapplySeq(fsNode.getName).isDefined

  def fileWithExt(ext: String, exts: String*) = nameRE("""(?i).*\.(%s)""".format(ext +: exts mkString("|")).r)_

  val imageFile = fileWithExt("png", "gif", "jpg", "jpeg")

  val templateFile = fileWithExt("jsp", "jspx", "html")
}

/** Browser predefined place (bookmark). */
case class Place(dirTreeRoot: File, dirContentFilter: File => Boolean = DirContentFilter.notHidden, recursive: Boolean = true)


case class FileBrowserSelection(dir: File, items: Seq[File]) {
  def first = items.headOption
  def nonEmpty = items.nonEmpty
}

// enum selectable:

class FileBrowser(val isSelectable: Boolean = true, val isMultiSelect: Boolean = false)
    extends Publisher[Option[FileBrowserSelection]] {

  type DirTreeTab = Component

  private val locations = MMap.empty[DirTreeTab, (DirTree, DirContent)]
  private val locationRef = new AtomicReference(Option.empty[(DirTree, DirContent)])
  private val selectionRef = new AtomicReference(Option.empty[FileBrowserSelection])

  val ui = letret(new FileBrowserUI) { ui =>
    ui.accDirTrees.addListener(new TabSheet.SelectedTabChangeListener {
      def selectedTabChange(e: TabSheet#SelectedTabChangeEvent) {
        val locationOpt = locations.get(e.getTabSheet.getSelectedTab)
        locationRef.set(locationOpt)

        for ((dirTree, dirContent) <- locationOpt) {
          ui.setSecondComponent(dirContent.ui)
        }
      }
    })
  }

  def addPlace(caption: String, place: Place, icon: Option[Resource] = None) {
    val dirTree = new DirTree(place.dirTreeRoot)
    val dirContent = new DirContent(place.dirContentFilter, isSelectable, isMultiSelect)

    dirTree.ui addValueChangeHandler {
      ?(dirTree.ui.value) match {
        case Some(dir) =>
          dirContent.reload(dir)
          let(Some(FileBrowserSelection(dir, Nil))) { selection =>
            selectionRef.set(selection)
            notifyListeners(selection)
          }

        case _ =>
          dirContent.ui.removeAllItems()

          let(None) { selection =>
            selectionRef.set(selection)
            notifyListeners(selection)
          }
      }
    }

    dirContent.ui addValueChangeHandler {
      ?(dirTree.ui.value) match {
        case Some(dir) =>
          val items = if (isMultiSelect) dirContent.ui.asInstanceOf[MultiSelect2[File]].value.toSeq
                      else ?(dirContent.ui.asInstanceOf[SingleSelect2[File]].value).toSeq

          let(Some(FileBrowserSelection(dir, items))) { selection =>
            selectionRef.set(selection)
            notifyListeners(selection)
          }

        case _ =>
          let(None) { selection =>
            selectionRef.set(selection)
            notifyListeners(selection)
          }
      }
    }

    dirTree.reload()

    locations(dirTree.ui) = (dirTree, dirContent)
    ui.accDirTrees.addTab(dirTree.ui, caption, icon.orNull)
  }

  override def notifyListeners() = notifyListeners(selection)

  def selection = selectionRef.get

  def location = locationRef.get

  def reloadLocationDir(preserveDirTreeSelection: Boolean = true) =
    for ((dirTree, _) <- location; dir = dirTree.ui.value) {
      dirTree.reload()
      if (preserveDirTreeSelection && dir.isDirectory) dirTree.ui.value = dir
    }

  def reloadLocationItems() =
    for ((dirTree, dirContent) <- location; dir = dirTree.ui.value)
      dirContent.reload(dir)
}


class FileBrowserUI extends HorizontalSplitPanel with FullSize {
  val accDirTrees = new Accordion with FullSize

  setFirstComponent(accDirTrees)
  setSplitPosition(15)
}


class DirTree(root: File) {
  val ui = new Tree with SingleSelect2[File] with ItemIdType[File] with Immediate with NoNullSelection

  def reload() {
    ui.setContainerDataSource(new DirTreeContainer(root))
    ui.setItemCaptionMode(AbstractSelect.ITEM_CAPTION_MODE_ITEM)
    ui.select(root)
    ui.expandItem(root)
  }
}


class DirContent(filter: File => Boolean, selectable: Boolean, multiSelect: Boolean) {
  val ui = letret(if (multiSelect) new Table with MultiSelect2[File]
                  else             new Table with SingleSelect2[File]) { ui =>

    ui.setSizeFull
    ui.setImmediate(true)
    ui.setSelectable(selectable)

    addContainerProperties(ui,
      CP[String]("Name"),
      CP[Date]("Date modified"),
      CP[String]("Size"),
      CP[String]("Kind"))

    import Table._
    ui.setColumnAlignments(Array(ALIGN_LEFT, ALIGN_LEFT, ALIGN_RIGHT, ALIGN_RIGHT))
    ui.setRowHeaderMode(ROW_HEADER_MODE_ICON_ONLY);
  }

  /** Populates table with dir items. */
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