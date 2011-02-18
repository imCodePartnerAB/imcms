package com.imcode
package imcms.admin.system.file

import scala.collection.JavaConversions._
import com.vaadin.ui._
import scala.collection.mutable.{Map => MMap}
import com.vaadin.terminal.{Resource}
import com.imcode.imcms.vaadin.{ContainerProperty => CP, _}
import com.vaadin.data.util.FilesystemContainer
import java.io.{FilenameFilter, File}
import java.util.concurrent.atomic.AtomicReference
import com.imcode.util.event.Publisher
import java.util.{Date}

/** Directory tree with a single root. */
class LocationTreeContainer(root: File) extends FilesystemContainer(root) {

  import java.util.Collections._

  setFilter(new FilenameFilter {
    def accept(file: File, name: String) = let(new File(file, name)) { fsNode =>
      fsNode.isDirectory && !fsNode.isHidden
    }
  })

  override def rootItemIds() = root |> singleton[File] |> unmodifiableCollection[File]

  override def addRoot(root: File) = error("Operation is not allowed.")
}


/** Predefined dir items filters. */
object LocationItemsFilter {

  import scala.util.matching.Regex

  /** Creates a compund filter from a sequence of filters. */
  def apply(filter: File => Boolean, filters: File => Boolean*) = (file: File) => filter +: filters forall { _ apply file }

  val notHidden = apply(!_.isHidden)

  //val fileOnly = apply(notHidden, _.isFile)

  def nameRE(re: Regex)(fsNode: File) = re.unapplySeq(fsNode.getName).isDefined

  def fileWithExt(ext: String, exts: String*) = nameRE("""(?i).*\.(%s)""".format(ext +: exts mkString("|")).r)_

  val imageFile = fileWithExt("png", "gif", "jpg", "jpeg")

  val templateFile = fileWithExt("jsp", "jspx", "html")
}

/** Browser location (bookmark) conf. */
case class LocationConf(root: File, itemsFilter: File => Boolean = LocationItemsFilter.notHidden, recursive: Boolean = true)


case class LocationSelection(dir: File, items: Seq[File]) {
  def first = items.headOption
  def nonEmpty = items.nonEmpty
  def isSingle = items.size == 1
}

// enum selectable??:

class FileBrowser(val isSelectable: Boolean = true, val isMultiSelect: Boolean = false)
    extends Publisher[Option[LocationSelection]] {

  type LocationTreeUI = Component

  private val locations = MMap.empty[LocationTreeUI, (LocationTree, LocationItems)]

  /** Current (visible) location. */
  private val locationRef = new AtomicReference(Option.empty[(LocationTree, LocationItems)])

  /** Selection in a current location */
  private val selectionRef = new AtomicReference(Option.empty[LocationSelection])

  val ui = letret(new FileBrowserUI) { ui =>
    ui.accLocationTrees.addListener(new TabSheet.SelectedTabChangeListener {
      def selectedTabChange(e: TabSheet#SelectedTabChangeEvent) {
        val locationOpt = locations.get(e.getTabSheet.getSelectedTab)
        locationRef.set(locationOpt)

        for ((locationTree, locationItems) <- locationOpt) {
          ui.setSecondComponent(locationItems.ui)
          updateSelection(locationTree, locationItems)
        }
      }
    })
  }

  def addLocation(caption: String, conf: LocationConf, icon: Option[Resource] = None) {
    val locationTree = new LocationTree(conf.root)
    val locationItems = new LocationItems(conf.itemsFilter, isSelectable, isMultiSelect)

    locationTree.ui addValueChangeHandler {
      ?(locationTree.ui.value) match {
        case Some(dir) =>
          locationItems.reload(dir)
          let(Some(LocationSelection(dir, Nil))) { selection =>
            selectionRef.set(selection)
            notifyListeners(selection)
          }

        case _ =>
          locationItems.ui.removeAllItems()

          let(None) { selection =>
            selectionRef.set(selection)
            notifyListeners(selection)
          }
      }
    }

    locationItems.ui addValueChangeHandler { updateSelection(locationTree, locationItems) }

    locationItems.ui.addItemClickListener {
      case e if e.isDoubleClick => e.getItemId match {
        case item: File if item.isDirectory => locationTree.cd(item)
        case _ =>
      }

      case _ =>
    }

    locationTree.reload()

    locations(locationTree.ui) = (locationTree, locationItems)
    ui.accLocationTrees.addTab(locationTree.ui, caption, icon.orNull)
  }

  private def updateSelection(locationTree: LocationTree, locationItems: LocationItems) {
    ?(locationTree.ui.value) match {
      case Some(dir) =>
        val items = if (isMultiSelect) locationItems.ui.asInstanceOf[MultiSelect2[File]].value.toSeq
                    else ?(locationItems.ui.asInstanceOf[SingleSelect2[File]].value).toSeq

        let(Some(LocationSelection(dir, items))) { selection =>
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

  override def notifyListeners() = notifyListeners(selection)

  def selection = selectionRef.get

  def location = locationRef.get

  def reloadLocationDir(preserveDirTreeSelection: Boolean = true) =
    for ((locationTree, _) <- location; dir = locationTree.ui.value) {
      locationTree.reload()
      if (preserveDirTreeSelection && dir.isDirectory) locationTree.ui.value = dir
    }

  def reloadLocationItems() =
    for ((locationTree, locationItems) <- location; dir = locationTree.ui.value)
      locationItems.reload(dir)


  def cd(dir: File) =
    for ((locationTree, _) <- location; tree = locationTree.ui) {
      tree.select(dir)
      tree.expandItem(dir)
    }
}


class FileBrowserUI extends HorizontalSplitPanel with FullSize {
  val accLocationTrees = new Accordion with FullSize

  setFirstComponent(accLocationTrees)
  setSplitPosition(15)
}


class LocationTree(root: File) {
  val ui = new Tree with SingleSelect2[File] with ItemIdType[File] with Immediate with NoNullSelection

  def reload() {
    ui.setContainerDataSource(new LocationTreeContainer(root))
    ui.setItemCaptionMode(AbstractSelect.ITEM_CAPTION_MODE_ITEM)
    cd(root)
  }

  def cd(dir: File) {
    ui.select(dir)
    ui.expandItem(if (dir == root) dir else dir.getParentFile)
  }
}


class LocationItems(filter: File => Boolean, selectable: Boolean, multiSelect: Boolean) {
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